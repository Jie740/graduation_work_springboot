package com.clj.security.util;

import com.clj.common.exception.BusinessException;
import com.clj.security.LoginUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 当前登录用户工具类
 * 替代原 utils/UserHolder（ThreadLocal 自管理），
 * 统一从 Spring Security 的 SecurityContext 获取登录用户
 *
 * @author ajie
 */
public class SecurityUtil {

    private SecurityUtil() {
    }

    /**
     * 获取当前登录用户
     *
     * @return LoginUser
     */
    public static LoginUser getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof LoginUser loginUser)) {
            throw new BusinessException("未登录");
        }
        return loginUser;
    }

    /**
     * 获取当前登录用户 ID
     *
     * @return 用户ID
     */
    public static Long getUserId() {
        return getLoginUser().getUserId();
    }
}
