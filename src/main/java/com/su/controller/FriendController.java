package com.su.controller;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.su.common.ResultBean;
import com.su.pojo.Friend;
import com.su.pojo.view.MyFriend;
import com.su.pojo.view.Select;
import com.su.service.FriendService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author swt 2023-3-18
 * @since 2023-03-19
 */
@RestController
@RequestMapping("/friend")
@CrossOrigin
public class FriendController {

    @Autowired
    private FriendService friendService;

    @ApiOperation("获取好友申请列表8条数据")
    @GetMapping("getFriend/{userId}")
    public ResultBean getFriendApplication(@PathVariable Integer userId){
        List <Friend> list=friendService.getFrienList(userId);
        return ResultBean.success().data(list);
    }

    @ApiOperation("获取好友列表")
    @GetMapping("getFriendList/{userId}")
    public ResultBean getFriends(@PathVariable Integer userId){
        List<MyFriend> list=friendService.getFriends(userId);
        return ResultBean.success().data(list);
    }

    @ApiOperation("获取好友姓名和id")
    @GetMapping("getFriendIdAndName/{userId}")
    public ResultBean getFriendIdAndName(@PathVariable Integer userId){
        List<Select> selects=friendService.getFriendIdAndName(userId);
        return ResultBean.success().data(selects);
    }



}

