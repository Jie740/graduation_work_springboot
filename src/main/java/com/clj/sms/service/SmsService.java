package com.clj.sms.service;

import com.clj.domain.dto.SendCodeDto;

/**
 * 短信/邮件验证码服务
 */
public interface SmsService {

    /**
     * 发送验证码（根据 DTO 中的手机号或邮箱自动选择发送方式）
     *
     * @param sendCodeDto 发送验证码请求
     */
    void sendCode(SendCodeDto sendCodeDto);

    /**
     * 发送验证码到手机号
     *
     * @param phone   手机号
     * @param purpose 验证码用途（LOGIN/REGISTER）
     */
    void sendCodeByPhone(String phone, String purpose);

    /**
     * 发送验证码到邮箱
     *
     * @param email   邮箱
     * @param purpose 验证码用途（LOGIN/REGISTER）
     */
    void sendCodeByEmail(String email, String purpose);

    /**
     * 校验验证码
     *
     * @param target  手机号或邮箱
     * @param code    用户输入的验证码
     * @param purpose 验证码用途（LOGIN/REGISTER）
     */
    void verifyCode(String target, String code, String purpose);
}
