package com.su.service;

import com.su.pojo.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author swt 2023-3-18
 * @since 2023-03-18
 */
public interface UserService extends IService<User> {

    void register(User user);
}
