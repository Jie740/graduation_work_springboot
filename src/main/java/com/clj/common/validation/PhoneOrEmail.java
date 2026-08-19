package com.clj.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 手机号、邮箱不能同时为空，二选一
 */
@Documented
@Constraint(validatedBy = PhoneOrEmailValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PhoneOrEmail {

    String message() default "手机号和邮箱不能同时为空，请至少填写一项";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
