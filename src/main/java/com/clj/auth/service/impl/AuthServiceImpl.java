package com.clj.auth.service.impl;

import com.clj.auth.service.AuthService;
import com.clj.auth.service.TokenBlackListService;
import com.clj.common.constant.RedisConstant;
import com.clj.common.constant.SystemConstant;
import com.clj.common.enums.UserStatusEnum;
import com.clj.common.exception.BusinessException;
import com.clj.common.result.Result;
import com.clj.common.result.ResultCode;
import com.clj.domain.SysRole;
import com.clj.domain.SysUser;
import com.clj.domain.dto.LoginDto;
import com.clj.security.LoginUser;
import com.clj.security.service.UserDetailsServiceImpl;
import com.clj.security.util.JwtUtil;
import com.clj.service.SysUserRoleService;
import com.clj.service.SysUserService;
import com.clj.sms.service.SmsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 登录服务
 * 基于 Spring Security：加载用户 -> 校验密码 -> 构建 Authentication
 * 放入 SecurityContext -> 生成 JWT（含 jti，用于 Redis 黑名单）
 *
 * @author ajie
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserDetailsServiceImpl userDetailsService;
    private final SysUserService sysUserService;
    private final SysUserRoleService sysUserRoleService;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final TokenBlackListService tokenBlackListService;
    private final SmsService smsService;

    @Override
    public Result<Map<String, String>> login(LoginDto loginDto) {
        String account = loginDto.getAccount();
        String password = loginDto.getPassword();
        String code = loginDto.getCode();

        LoginUser loginUser;

        if (code != null && !code.isBlank()) {
            // ========== 验证码登录（仅限 user / ENTERPRISE_ADMIN）==========
            loginUser = loginByCode(account, code);
        } else {
            // ========== 密码登录 ==========
            loginUser = loginByPassword(account, password);
        }

        // 3. 构建 Authentication 并放入 SecurityContext
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        loginUser, null, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 4. 生成 JWT（携带 jti，登出时加入黑名单）
        String token = jwtUtil.createToken(loginUser);

        // 5. 缓存用户权限到 Redis，供 JwtAuthenticationFilter 恢复认证信息
        redisTemplate.opsForValue().set(
                RedisConstant.USER_PERMISSION_PREFIX + loginUser.getUserId(),
                loginUser.getPermissions(),
                RedisConstant.TOKEN_EXPIRE_SECONDS,
                TimeUnit.SECONDS);

        // 6. 返回登录结果
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        map.put("role", loginUser.getRole());
        map.put("username", loginUser.getUsername());
        map.put("name", loginUser.getNickname());
        return Result.success(map);
    }

    /**
     * 验证码登录：仅限 USER / ENTERPRISE_ADMIN 角色
     */
    private LoginUser loginByCode(String account, String code) {
        // 1. 校验验证码
        smsService.verifyCode(account, code, "LOGIN");

        // 2. 按邮箱/手机号查找用户（管理员不允许验证码登录）
        SysUser user = userDetailsService.findByEmailOrPhoneForNonAdmin(account);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        if (UserStatusEnum.DISABLED.getStatus() == user.getStatus()) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        log.info("验证码登录成功: account={}, role={}", account, getPrimaryRoleCode(user.getId()));
        return userDetailsService.buildLoginUser(user);
    }

    /**
     * 密码登录：支持所有角色
     * <p>
     * - 管理员（SYSTEM_ADMIN）：必须使用用户名登录
     * - 普通用户/企业管理员：可使用用户名、邮箱或手机号登录
     */
    private LoginUser loginByPassword(String account, String password) {
        // 1. 查找用户（先按用户名，再按邮箱/手机号）
        SysUser user = userDetailsService.findByAccount(account);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        if (UserStatusEnum.DISABLED.getStatus() == user.getStatus()) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        // 2. 管理员不允许通过邮箱/手机号登录（基于 RBAC 角色判断）
        if (hasRole(user.getId(), "SYSTEM_ADMIN") && !account.equals(user.getUsername())) {
            throw new BusinessException(ResultCode.FAIL.getCode(), "管理员请使用用户名登录");
        }

        // 3. 校验密码：BCrypt 匹配；兼容存量明文密码（登录成功后自动升级为 BCrypt）
        String dbPassword = user.getPassword();
        boolean passwordMatched = dbPassword.startsWith("$2")
                ? passwordEncoder.matches(password, dbPassword)
                : dbPassword.equals(password);
        if (!passwordMatched) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }
        if (!dbPassword.startsWith("$2")) {
            sysUserService.lambdaUpdate()
                    .eq(SysUser::getId, user.getId())
                    .set(SysUser::getPassword, passwordEncoder.encode(password))
                    .update();
            log.info("存量明文密码已升级为 BCrypt: username={}", user.getUsername());
        }

        log.info("密码登录成功: account={}, role={}", account, getPrimaryRoleCode(user.getId()));
        return userDetailsService.buildLoginUser(user);
    }

    /**
     * 获取用户的主角色编码（RBAC）
     */
    private String getPrimaryRoleCode(Long userId) {
        List<SysRole> roles = sysUserRoleService.getRolesByUserId(userId);
        return roles.isEmpty() ? null : roles.get(0).getRoleCode();
    }

    /**
     * 判断用户是否拥有指定角色编码（RBAC）
     */
    private boolean hasRole(Long userId, String roleCode) {
        return sysUserRoleService.getRolesByUserId(userId).stream()
                .anyMatch(role -> roleCode.equals(role.getRoleCode()));
    }

    @Override
    public Result<Void> logout(HttpServletRequest request) {
        String header = request.getHeader(SystemConstant.TOKEN_HEADER);
        if (header != null && header.startsWith(SystemConstant.TOKEN_PREFIX)) {
            String token = header.substring(SystemConstant.TOKEN_PREFIX.length());
            tokenBlackListService.addTokenToBlackList(token);
        }
        SecurityContextHolder.clearContext();
        return Result.success();
    }
}
