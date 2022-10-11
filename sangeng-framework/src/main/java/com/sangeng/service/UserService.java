package com.sangeng.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.User;


/**
 * 用户表(User)表服务接口
 *
 * @author makejava
 * @since 2022-09-28 09:21:22
 */
public interface UserService extends IService<User> {
    /**
     * 3.13 个人信息查询接口
     * @return
     */
    ResponseResult userInfo();

    /**
     * 3.15更新个人信息接口
     * @return
     */
    ResponseResult updateUserInfo(User user);

    /**
     * 3.16 用户注册
     */
    ResponseResult register(User user);

    /**
     * 5.23 用户列表
     */
    ResponseResult selectUserPage(User user, Integer pageNum, Integer pageSize);

    boolean checkUserNameUnique(String userName);

    boolean checkPhoneUnique(User user);

    boolean checkEmailUnique(User user);

    ResponseResult addUser(User user);

    void updateUser(User user);
}

