package com.clj.service.impl;

import com.clj.domain.User;
import com.clj.domain.dto.LoginDto;
import com.clj.service.LoginService;
import com.clj.service.UserService;
import com.clj.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {
    private final UserService userService;
    @Override
    public Result login(LoginDto loginDto) {
        User one = this.userService.lambdaQuery().eq(User::getUsername, loginDto.getUsername())
                .eq(User::getPassword, loginDto.getPassword())
                .one();
        return one == null ? Result.error("用户名或密码错误") : Result.ok(one);
    }
}
