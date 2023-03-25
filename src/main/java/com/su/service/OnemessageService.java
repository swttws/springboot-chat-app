package com.su.service;

import com.su.pojo.Onemessage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author swt 2023-3-18
 * @since 2023-03-21
 */
public interface OnemessageService extends IService<Onemessage> {

    List<Onemessage> getMessageList(Integer myId, Integer friendId);
}
