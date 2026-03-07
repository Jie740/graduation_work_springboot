package com.clj.service;

import com.clj.domain.dto.LoginDto;
import com.clj.utils.Result;
import org.springframework.stereotype.Service;

public interface LoginService {
    public Result login(LoginDto loginDto);
}
