package com.clj.controller;

import com.clj.domain.dto.LoginDto;
import com.clj.service.LoginService;
import com.clj.utils.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;

    @PostMapping("/login")
    public Result login(@RequestBody LoginDto loginDto) {
        return loginService.login(loginDto);
    }

    @PostMapping("/logout")
    public Result logout(HttpServletRequest request) {
        return loginService.logout(request);
    }
}
