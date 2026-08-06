package com.clj.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 统一响应状态码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),

    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未登录或token已过期"),
    FORBIDDEN(403, "没有操作权限"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_ERROR(500, "服务器内部错误"),

    USER_NOT_FOUND(1001, "用户不存在"),
    USER_PASSWORD_ERROR(1002, "密码错误"),
    USER_DISABLED(1003, "用户已被禁用"),
    USER_ALREADY_EXISTS(1004, "用户名已存在"),

    ROLE_NOT_FOUND(2001, "角色不存在"),
    ROLE_ALREADY_EXISTS(2002, "角色编码已存在"),

    PERMISSION_NOT_FOUND(3001, "权限不存在"),
    PERMISSION_ALREADY_EXISTS(3002, "权限编码已存在");

    private final int code;
    private final String message;
}
