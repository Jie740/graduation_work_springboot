package com.clj.sms.controller;

import com.clj.common.result.Result;
import com.clj.domain.dto.SendCodeDto;
import com.clj.sms.service.SmsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短信验证码接口
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/sms")
public class SmsController {

    private final SmsService smsService;

    @PostMapping("/send-code")
    public Result<Void> sendCode(@RequestBody @Valid SendCodeDto sendCodeDto) {
        smsService.sendCode(sendCodeDto);
        return Result.success();
    }
}
