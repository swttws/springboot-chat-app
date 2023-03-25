package com.su.netty.strategy;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.su.config.RabbitMqConfig;
import com.su.netty.protocol.MyMessage;
import com.su.netty.protocol.Type;
import com.su.pojo.Dialog;
import com.su.pojo.Groupmessage;
import com.su.pojo.Onemessage;
import com.su.service.DialogService;
import com.su.service.GroupmessageService;
import com.su.service.OnemessageService;
import com.su.utils.CommonUtils;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@TypeAnnotation(Type.DISCONNECT_EVENT)
@Slf4j
public class DisconnectStrategy implements MessageStrategy{

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private OnemessageService onemessageService;

    @Autowired
    private DialogService dialogService;

    @Autowired
    private GroupmessageService groupmessageService;

    //消息处理
    @Override
    public void handleMessage(Channel channel, MyMessage msg) {
        //获取绑定信息
        log.info("客户端断开连接了，redis数据保存数据库");
        //获取绑定信息
        MyMessage myMessage = channel.attr(HandlerMessage.MY_MESSAGE_ATTRIBUTE_KEY).get();
        //单聊，群聊会话
        List<Integer> dialogIdList=new ArrayList<>();
        List<Dialog> dialogList=new ArrayList<>();
        //将单聊会话信息同步数据库
        Set<Object> oneDialogs = redisTemplate.opsForZSet().
                range(CommonUtils.dialog_prefix + myMessage.getSendUserId(), 0, -1);
        if (oneDialogs!=null){
            addDialog(dialogIdList,dialogList,oneDialogs);
        }
        //将群聊会话信息同步数据库
        Set<Object> groupDialogs = redisTemplate.opsForZSet().
                range(CommonUtils.groupDialog_prefix + myMessage.getSendUserId(), 0, -1);
        if (groupDialogs!=null){
            addDialog(dialogIdList,dialogList,groupDialogs);
        }
        //添加到数据库
        dialogService.removeBatchByIds(dialogIdList);
        dialogService.saveBatch(dialogList);
        //删除redis中数据
        redisTemplate.delete(CommonUtils.dialog_prefix+myMessage.getSendUserId());
        redisTemplate.delete(CommonUtils.groupDialog_prefix+myMessage.getSendUserId());

        //将单聊信息同步数据库（自己发送的消息）
        List<Onemessage> onemessageList = (List<Onemessage>) redisTemplate.
                opsForValue().get(CommonUtils.message_prefix);
        if (onemessageList!=null){
            List<Onemessage> collect = onemessageList.stream()
                    .filter(item -> item.getSendUserid().equals(myMessage.getSendUserId()))
                    .collect(Collectors.toList());
            onemessageService.saveBatch(collect);
            onemessageList.removeAll(collect);
            //更新redis数据
            redisTemplate.delete(CommonUtils.message_prefix);
            redisTemplate.opsForValue().set(CommonUtils.message_prefix,onemessageList);
        }
        //将群聊消息同步数据库（自己发送的消息）
        List<Groupmessage> groupmessageList = (List<Groupmessage>) redisTemplate.
                opsForValue().get(CommonUtils.group_message);
        //去除已经添加到数据库的数据
        if(groupmessageList!=null){
            List<Groupmessage> myMsgs = groupmessageList.stream()
                    .filter(item -> item.getUserId().equals(myMessage.getSendUserId()))
                    .collect(Collectors.toList());
            List<Groupmessage> collect = myMsgs.stream().
                    filter(groupmessage -> groupmessage.getStatus() == 0)
                    .collect(Collectors.toList());
            groupmessageService.saveBatch(collect);
            //redis数据更新
            groupmessageList.removeAll(myMsgs);
            redisTemplate.delete(CommonUtils.group_message);
            redisTemplate.opsForValue().set(CommonUtils.group_message,groupmessageList);
        }

    }

    //单聊，群聊id合集
    private void addDialog(List<Integer> idList,List<Dialog> dialogList,Set<Object> dialogSets){
        List<Dialog> collect = dialogSets.stream().
                map(item -> (Dialog) item).collect(Collectors.toList());
        idList.addAll(
                collect.stream().map(Dialog::getId).collect(Collectors.toList())
        );
        dialogList.addAll(collect);
    }

    @Override
    public void dealMqMessage(MyMessage message) {

    }
}
