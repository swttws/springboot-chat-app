package com.su.service;

import com.su.pojo.Groupmessage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author swt 2023-3-18
 * @since 2023-03-22
 */
public interface GroupmessageService extends IService<Groupmessage> {

    List<Groupmessage> getGroupMessage(Integer groupId);
}
