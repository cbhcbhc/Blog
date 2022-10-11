package com.sangeng.service.impl;

import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.LoginUser;
import com.sangeng.domain.entity.User;
import com.sangeng.domain.vo.BlogUserLoginVo;
import com.sangeng.domain.vo.UserInfoVo;
import com.sangeng.service.BlogLoginService;
import com.sangeng.utils.BeanCopyUtils;
import com.sangeng.utils.JwtUtil;
import com.sangeng.utils.RedisCache;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 3.6.5 登录接口代码实现
 */
@Service
public class BlogLoginServiceImpl implements BlogLoginService {
    //注入身份验证管理器，我们用的是他的实现类
    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private RedisCache redisCache;

    @Override
    public ResponseResult login(User user) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user.getUserName(),user.getPassword());
        /**
         * 这里内部默认会跳到-->UserDetailsServiceImpl.loadUserByUsername()根据用户名查询用户信息
         * 然后把User用户信息分装在 UserDetails中
         * 然后密码校验，成功后将权限信息设置到Authentication中
         * 最后封装返回Authentication
         */
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);

        //判断是否通过认证
        if (Objects.isNull(authenticate)){
            throw new RuntimeException("用户名或密码错误");
        }

        //获取userid 生成token
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String userId = loginUser.getUser().getId().toString();
        String jwt = JwtUtil.createJWT(userId);//jwt就是token
        //把用户信息存入redis
        redisCache.setCacheObject("bloglogin:"+userId,loginUser);

        /**
         * 把token 和userInfo 封装返回
         */
        //把User转换成UserInfoVo
        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(loginUser.getUser(), UserInfoVo.class);
        BlogUserLoginVo vo = new BlogUserLoginVo(jwt, userInfoVo);
        return ResponseResult.okResult(vo);
    }

    @Override
    public ResponseResult logout() {
        //获取token 解析获取userid
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //获取userid  getPrincipal():获取属性
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Long userId = loginUser.getUser().getId();
        //删除redis中的用户信息
        redisCache.deleteObject("bloglogin:" + userId);
        return ResponseResult.okResult();
    }
}
