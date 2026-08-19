package com.clj.domain.dto;

import com.clj.common.validation.PhoneOrEmail;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@PhoneOrEmail(message = "手机号和邮箱不能同时为空，请至少填写一项")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SystemUserDto {
    /*
    * String 手机号
    * */
    private String phone;
    /*
    * String 邮箱
    * */
    private String email;
    /*
    * String 验证码
    * */
    @NotBlank(message = "验证码不能为空")
    private String code;
    /*
    * String 密码
    * */
    @NotBlank(message = "密码不能为空")
    private String password;

    // ⚠️重要提醒：注册接口不要让前端传 roleCode！！
    /*
    * String 角色(USER、ENTERPRISE_ADMIN)
    * */
    private String roleCode;
}
