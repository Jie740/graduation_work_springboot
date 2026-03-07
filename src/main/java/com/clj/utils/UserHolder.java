package com.clj.utils;

public class UserHolder {
    private static final ThreadLocal<Long> userId = new ThreadLocal<>();
    public static void setUserId(Long id) {
        userId.set(id);
    }
    public static Long getUserId() {
        return userId.get();
    }
    public static void remove() {
        userId.remove();
    }
}
