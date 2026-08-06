package com.clj.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 角色枚举
 */
@Getter
@AllArgsConstructor
public enum RoleEnum {

    ADMIN(1L, "ROLE_ADMIN", "超级管理员"),
    TEACHER(2L, "ROLE_TEACHER", "教师"),
    STUDENT(3L, "ROLE_STUDENT", "学生");

    private final Long id;
    private final String code;
    private final String description;

    /**
     * 根据 code 获取枚举
     */
    public static RoleEnum getByCode(String code) {
        for (RoleEnum role : values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        return null;
    }

    /**
     * 根据 id 获取枚举
     */
    public static RoleEnum getById(Long id) {
        for (RoleEnum role : values()) {
            if (role.getId().equals(id)) {
                return role;
            }
        }
        return null;
    }
}
