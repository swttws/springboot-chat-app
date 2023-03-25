package com.su.security.filter;

import com.alibaba.fastjson.JSON;
import com.su.common.Errors;
import com.su.common.ResultBean;
import com.su.service.UserService;
import com.su.service.impl.UserServiceImp;
import com.su.utils.JWTUtils;
import io.jsonwebtoken.Claims;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtTokenFilter extends BasicAuthenticationFilter {

    private UserServiceImp userService;

    private RedisTemplate<String,Object> redisTemplate;

    public JwtTokenFilter(AuthenticationManager authenticationManager,UserServiceImp  userService,
                          RedisTemplate<String,Object> redisTemplate) {
        super(authenticationManager);
        this.userService=userService;
        this.redisTemplate=redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            //获取头部token信息
            String token = request.getHeader("Authorization");
            //token为空
            if (token==null||token.equals("")){
                chain.doFilter(request,response);
                return;
            }
            //校验token是否合法
            Claims claims = JWTUtils.parseJWT(token);
            if (claims==null){
                throw new Errors("500","不合法的请求,请重新登录");
            }
            String userName = (String) claims.get("username");
            String redisToken = (String) redisTemplate.opsForValue().get(userName);
            //token与缓存中不一样
            if (!redisToken.equals(token)){
                throw new Errors("500","不合法的请求,请重新登录");
            }
            //验证用户名密码是否正确
            UserDetails userDetails = userService.loadUserByUsername(userName);
            if (userDetails==null){
                throw new Errors("500","账号或用户名错误");
            }
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails.getUsername(),null, userDetails.getAuthorities());
            response.setHeader("Authorization",token);
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        } catch (Exception e) {
            //抛出异常，并返回给前端
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/json; charset=utf-8");
            ResultBean message = ResultBean.error().message(e.getLocalizedMessage());
            response.getWriter().write(JSON.toJSONString(message));
            response.getWriter().flush();
            chain.doFilter(request, response);
            return;
        }
        //放行请求
        super.doFilterInternal(request,response,chain);
    }
}
