package com.clj.common.validation;

import com.clj.domain.dto.SystemUserDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 校验手机号和邮箱至少有一个不为空
 */
public class PhoneOrEmailValidator implements ConstraintValidator<PhoneOrEmail, SystemUserDto> {

    @Override
    public boolean isValid(SystemUserDto dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return false;
        }
        // 手机号、邮箱不能同时为空
        boolean phoneBlank = dto.getPhone() == null || dto.getPhone().isBlank();
        boolean emailBlank = dto.getEmail() == null || dto.getEmail().isBlank();
        return !(phoneBlank && emailBlank);
    }
}
