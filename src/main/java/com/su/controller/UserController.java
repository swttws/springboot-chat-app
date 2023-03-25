package com.su.controller;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.su.common.ResultBean;
import com.su.pojo.User;
import com.su.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author swt 2023-3-18
 * @since 2023-03-18
 */
@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @ApiOperation("注册")
    @PostMapping("register")
    public ResultBean register(@RequestBody User user){
        userService.register(user);
        return ResultBean.success();
    }

    @ApiOperation("根据用户名或手机号查找好友")
    @GetMapping("/getUser/{condition}")
    public ResultBean getUser(@PathVariable String condition){
        User user = userService.getOne(Wrappers.<User>lambdaQuery()
                .eq(User::getUserName, condition).or().eq(User::getPhone, condition));
        return ResultBean.success().data(user);
    }

    @ApiOperation("根据用户id查询好友")
    @GetMapping("getUserById/{userId}")
    public ResultBean getUserById(@PathVariable Integer userId){
        User user = userService.getById(userId);
        return ResultBean.success().data(user);
    }

    @ApiOperation("根据用户名获取用户id")
    @GetMapping("getUserByUserName/{userName}")
    public ResultBean getUserByUserName(@PathVariable String userName){
        User one = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getUserName, userName));
        return ResultBean.success().data(one);
    }




}

