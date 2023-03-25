package com.su.service;

import com.su.pojo.Friend;
import com.baomidou.mybatisplus.extension.service.IService;
import com.su.pojo.view.MyFriend;
import com.su.pojo.view.Select;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author swt 2023-3-18
 * @since 2023-03-19
 */
public interface FriendService extends IService<Friend> {

    List<Friend> getFrienList(Integer userId);

    List<MyFriend> getFriends(Integer userId);

    List<Select> getFriendIdAndName(Integer userId);
}
