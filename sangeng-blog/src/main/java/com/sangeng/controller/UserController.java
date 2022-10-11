package com.sangeng.controller;

import com.sangeng.annotation.SystemLog;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.User;
import com.sangeng.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;
    /**
     * 3.13 个人信息查询接口
     */
    @GetMapping("/userInfo")
    public ResponseResult userInfo(){
        return userService.userInfo();
    }

    /**
     * 3.15更新个人信息接口
     * @return
     */
    @PutMapping("/userInfo")
    @SystemLog(businessName = "更新用户信息")
    public ResponseResult updateUserInfo(@RequestBody User user){
        return userService.updateUserInfo(user);
    }

    /**
     * 3.16 用户注册
     */
    @PostMapping("register")
    public ResponseResult register(@RequestBody User user){
        return userService.register(user);
    }
}
