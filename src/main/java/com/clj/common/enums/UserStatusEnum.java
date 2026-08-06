package com.clj.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户状态枚举
 */
@Getter
@AllArgsConstructor
public enum UserStatusEnum {

    DISABLED(0, "禁用"),
    NORMAL(1, "正常");

    private final int status;
    private final String description;

    /**
     * 根据 status 获取枚举
     */
    public static UserStatusEnum getByStatus(int status) {
        for (UserStatusEnum userStatusEnum : values()) {
            if (userStatusEnum.getStatus() == status) {
                return userStatusEnum;
            }
        }
        return null;
    }
}
