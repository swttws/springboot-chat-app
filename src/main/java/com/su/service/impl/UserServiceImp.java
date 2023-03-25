package com.su.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.su.common.Errors;
import com.su.pojo.User;
import com.su.mapper.UserMapper;
import com.su.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author swt 2023-3-18
 * @since 2023-03-18
 */
@Service
public class UserServiceImp extends ServiceImpl<UserMapper, User> implements UserService , UserDetailsService {

    //注册
    @Override
    public void register(User user) {
        String password = user.getPassword();
        String phone = user.getPhone();
        String userName = user.getUserName();
        //验证参数
        if (StringUtils.isEmpty(phone)||StringUtils.isEmpty(password)||StringUtils.isEmpty(userName)){
            throw new Errors("500","用户名，密码或手机号不能为空");
        }
        //查询是否注册过
        Long count = baseMapper.selectCount(Wrappers.<User>lambdaQuery().eq(User::getPhone, phone)
                .or().eq(User::getUserName, userName));
        if (count>0){
            throw new Errors("500","手机号或用户名已经注册过");
        }
        //密码加密
        BCryptPasswordEncoder bCryptPasswordEncoder=new BCryptPasswordEncoder();
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        baseMapper.insert(user);
    }


    //获取用户信息
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = baseMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUserName, s));
        if (user==null){
            throw new Errors("500","账号或密码错误");
        }
        return new org.springframework.security.core.userdetails.User(user.getUserName(),
                user.getPassword(),new ArrayList<>());
    }
}
