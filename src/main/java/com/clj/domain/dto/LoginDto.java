package com.clj.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录请求 DTO
 * <p>
 * account 字段语义：
 * - SYSTEM_ADMIN 管理员登录：account = 用户名
 * - user / ENTERPRISE_ADMIN 登录：account = 邮箱 或 手机号
 * <p>
 * 验证码登录：填写 account + code，password 可空
 * 密码登录：  填写 account + password，code 可空
 *
 * @author ajie
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {
    /**
     * 登录账号（管理员=用户名，普通用户/企业管理员=邮箱或手机号）
     */
    String account;

    /**
     * 密码（验证码登录时可为空）
     */
    String password;

    /**
     * 验证码（密码登录时可为空）
     */
    String code;

}
