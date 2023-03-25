package com.su.security.filter;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.mysql.cj.util.TimeUtil;
import com.su.common.ResultBean;
import com.su.mapper.UserMapper;
import com.su.pojo.User;
import com.su.service.UserService;
import com.su.utils.JWTUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import sun.rmi.runtime.Log;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

//认证过滤器
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    private RedisTemplate<String,Object> redisTemplate;

    private UserMapper userMapper;

    public LoginFilter(AuthenticationManager authenticationManager,RedisTemplate<String,Object> redisTemplate,UserMapper userMapper){
        this.redisTemplate=redisTemplate;
        this.authenticationManager=authenticationManager;
        this.userMapper=userMapper;
    }

    //认证方法
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username,password,new ArrayList<>()));
    }

    //认证成功
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        //生成token
        String username = request.getParameter("username");
        String token = JWTUtils.createJWT(username);
        //将token存入redis，,用户名为value
        redisTemplate.opsForValue().set(username,token);
        //封装数据
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUserName, username));
        Map<String,Object> map=new HashMap<>();
        map.put("user",user);
        map.put("token",token);
        ResultBean resultBean = ResultBean.success().data(map);
        //放行到登录成功请求
        response.setHeader("Authorization",token);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().write( JSON.toJSONString(resultBean));
    }

    //认证失败
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        ResultBean error = ResultBean.error().message("账号或密码错误");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().write(JSON.toJSONString(error));
    }
}
