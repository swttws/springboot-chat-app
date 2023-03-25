package com.su.service;

import com.su.netty.protocol.MyMessage;
import com.su.pojo.Dialog;
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
public interface DialogService extends IService<Dialog> {

    List<Dialog> getDialogByMyId(Integer userId);

    void getDialogByTypeToRedis(Integer userId,Integer type);

    void updateDialog(MyMessage message,Integer type);

    void saveOffLineInfo(MyMessage message,Integer type);
}
