package com.clj.domain.dto;

import com.clj.common.validation.PhoneOrEmail;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 发送验证码请求 DTO
 * <p>
 * phone 和 email 至少填写一项，优先使用 phone
 *
 * @author ajie
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@PhoneOrEmail(message = "手机号和邮箱不能同时为空，请至少填写一项")
public class SendCodeDto {
    /**
     * 手机号
     */
    String phone;

    /**
     * 邮箱
     */
    String email;

    /**
     * 验证码用途：LOGIN（登录）/ REGISTER（注册）
     */
    @NotBlank(message = "验证码用途不能为空")
    String purpose;
}
