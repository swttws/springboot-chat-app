package com.su.security.handler;

import com.alibaba.fastjson.JSON;
import com.su.common.ResultBean;
import com.su.utils.JWTUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//注销
public class LogoutHandler implements LogoutSuccessHandler {

    private RedisTemplate<String,Object> redisTemplate;

    public LogoutHandler(RedisTemplate<String,Object> redisTemplate){
        this.redisTemplate=redisTemplate;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json; charset=utf-8");
        ResultBean resultBean=new ResultBean();
        try {
            //获取token
            String token = request.getHeader("Authorization");
            if (token==null||token.equals("")){
                throw new Exception("注销失败");
            }
            //redis删除用户信息
            String userName = (String) JWTUtils.parseJWT(token).get("username");
            if (userName==null){
                throw new Exception("注销失败");
            }
            redisTemplate.delete(userName);
            resultBean.setCode("200");
        } catch (Exception e) {
            resultBean.setCode("500");
            resultBean.setMessage(e.getLocalizedMessage());
            e.printStackTrace();
        }
        response.getWriter().write(JSON.toJSONString(resultBean));
    }
}
