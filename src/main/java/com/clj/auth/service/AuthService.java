package com.clj.auth.service;

import com.clj.common.result.Result;
import com.clj.domain.dto.LoginDto;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface AuthService {
    Result<Map<String, String>> login(LoginDto loginDto);

    Result<Void> logout(HttpServletRequest request);
}
