package com.sangeng.controller;

import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.User;
import com.sangeng.enums.AppHttpCodeEnum;
import com.sangeng.exception.SystemException;
import com.sangeng.service.BlogLoginService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 3.6.5 登录接口代码实现
 */
@RestController
public class BlogLoginController {
    @Resource
    private BlogLoginService blogLoginService;;

    //登录接口
    @PostMapping("/login")
    public ResponseResult login(@RequestBody User user){
        if (!StringUtils.hasText(user.getUserName())){
            //提示： 必须传用户名
            throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);

        }
        return blogLoginService.login(user);
    }

    //注销接口
    @PostMapping("/logout")
    public ResponseResult logout(){
        return blogLoginService.logout();
    }
}
