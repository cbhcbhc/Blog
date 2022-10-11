package com.sangeng.service;

import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.User;

/**
 * 3.6.5 登录接口代码实现
 */
public interface LoginService {
    ResponseResult login(User user);

    ResponseResult logout();

}
