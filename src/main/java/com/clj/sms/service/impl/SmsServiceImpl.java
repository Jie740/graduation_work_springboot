package com.clj.sms.service.impl;

import com.clj.common.constant.RedisConstant;
import com.clj.common.exception.BusinessException;
import com.clj.common.result.ResultCode;
import com.clj.domain.dto.SendCodeDto;
import com.clj.sms.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 短信/邮件验证码服务实现
 * <p>
 * 当前阶段：验证码存入 Redis 并打印到日志
 * TODO: 后续接入阿里云短信服务 / 腾讯云短信服务 / JavaMailSender
 *
 * @author ajie
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void sendCode(SendCodeDto sendCodeDto) {
        String phone = sendCodeDto.getPhone();
        String email = sendCodeDto.getEmail();
        String purpose = sendCodeDto.getPurpose();

        // 1. 手机号和邮箱至少填写一项
        boolean phoneBlank = phone == null || phone.isBlank();
        boolean emailBlank = email == null || email.isBlank();
        if (phoneBlank && emailBlank) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "手机号和邮箱至少填写一项");
        }

        // 2. 优先使用手机号，否则使用邮箱
        if (!phoneBlank) {
            sendCodeByPhone(phone, purpose);
        } else {
            sendCodeByEmail(email, purpose);
        }
    }

    @Override
    public void sendCodeByPhone(String phone, String purpose) {
        String code = generateCode();
        String redisKey = buildRedisKey(purpose, phone);
        redisTemplate.opsForValue().set(redisKey, code, RedisConstant.CAPTCHA_EXPIRE_SECONDS, TimeUnit.SECONDS);
        log.info("【短信验证码】用途={}, 手机号={}, 验证码={}, 有效期={}秒", purpose, phone, code, RedisConstant.CAPTCHA_EXPIRE_SECONDS);
        // TODO: 接入阿里云/腾讯云短信服务，调用 SMS API 发送验证码
    }

    @Override
    public void sendCodeByEmail(String email, String purpose) {
        String code = generateCode();
        String redisKey = buildRedisKey(purpose, email);
        redisTemplate.opsForValue().set(redisKey, code, RedisConstant.CAPTCHA_EXPIRE_SECONDS, TimeUnit.SECONDS);
        log.info("【邮件验证码】用途={}, 邮箱={}, 验证码={}, 有效期={}秒", purpose, email, code, RedisConstant.CAPTCHA_EXPIRE_SECONDS);
        // TODO: 接入 JavaMailSender，发送验证码邮件
    }

    @Override
    public void verifyCode(String target, String code, String purpose) {
        String redisKey = buildRedisKey(purpose, target);
        String cachedCode = (String) redisTemplate.opsForValue().get(redisKey);
        if (cachedCode == null) {
            throw new BusinessException("验证码已过期，请重新获取");
        }
        if (!cachedCode.equals(code)) {
            throw new BusinessException("验证码错误");
        }
        // 验证码使用后立即删除，防止重复使用
        redisTemplate.delete(redisKey);
    }

    /**
     * 构建 Redis key：captcha:{purpose}:{target}
     */
    private String buildRedisKey(String purpose, String target) {
        return RedisConstant.CAPTCHA_PREFIX + purpose + ":" + target;
    }

    /**
     * 随机生成6位数字验证码
     */
    private String generateCode() {
        return String.format("%06d", new Random().nextInt(1000000));
    }
}
