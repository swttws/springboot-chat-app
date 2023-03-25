package com.su.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.su.pojo.Friend;
import com.su.mapper.FriendMapper;
import com.su.pojo.view.MyFriend;
import com.su.pojo.view.Select;
import com.su.service.FriendService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.su.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author swt 2023-3-18
 * @since 2023-03-19
 */
@Service
public class FriendServiceImp extends ServiceImpl<FriendMapper, Friend> implements FriendService {

    @Autowired
    private UserService userService;

    //获取好友申请列表
    @Override
    public List<Friend> getFrienList(Integer userId) {
        List<Friend> list = baseMapper.selectList(Wrappers.<Friend>lambdaQuery().
                eq(Friend::getFriendId, userId).
                orderByDesc(Friend::getCreateTime).
                last("limit 8"));
        list.forEach(friend -> {
            String userName = userService.getById(friend.getUserId()).getUserName();
            friend.setUserName(userName);
        });
        return list;
    }

    //获取好友列表
    @Override
    public List<MyFriend> getFriends(Integer userId) {
        List<MyFriend> myFriendList=new ArrayList<>();
        //查询好友列表
        List<Friend> friends = baseMapper.selectList(Wrappers.<Friend>lambdaQuery()
                .eq(Friend::getUserId, userId).eq(Friend::getStatus, 1));
        //处理数据
        Map<String, List<Friend>> collect = friends.
                stream().collect(Collectors.groupingBy(Friend::getFirstName));
        collect.forEach((key,values)->{
            MyFriend myFriend = new MyFriend();
            myFriend.setLetter(key);
            //获取好友姓名
            List<String> nameList
                    = values.stream().map(Friend::getFriendName).collect(Collectors.toList());
            myFriend.setContacts(nameList);
            myFriendList.add(myFriend);
        });
        return myFriendList;
    }

    //获取好友姓名和好友id
    @Override
    public List<Select> getFriendIdAndName(Integer userId) {
        List<Friend> friendList = baseMapper.selectList(Wrappers.<Friend>lambdaQuery()
                .eq(Friend::getUserId, userId).eq(Friend::getStatus, 1));
        List<Select> selects=new ArrayList<>();
        //结果即封装
        friendList.forEach(friend -> {
            Select select = new Select();
            select.setText(friend.getFriendName());
            select.setValue(friend.getFriendId());
            selects.add(select);
        });
        return selects;
    }
}
