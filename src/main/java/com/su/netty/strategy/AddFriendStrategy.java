package com.su.netty.strategy;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.su.config.RabbitMqConfig;
import com.su.netty.protocol.MyMessage;
import com.su.netty.protocol.Type;
import com.su.pojo.Dialog;
import com.su.pojo.Friend;
import com.su.pojo.User;
import com.su.service.DialogService;
import com.su.service.FriendService;
import com.su.service.UserService;
import com.su.utils.PinyinUtils;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

//添加好友 消息事件处理器
@Component
@TypeAnnotation(Type.ADD_FRIEND_EVENT)
@Slf4j
public class AddFriendStrategy implements MessageStrategy{

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private FriendService friendService;

    @Autowired
    private UserService userService;

    @Override
    public void handleMessage(Channel channel, MyMessage message) {
        log.info("添加朋友事件处理器执行-----");
        //判断好友是否是自己
        if(message.getSendUserId().equals(message.getReceiverId())){
            message.setType(Type.MESSAGE_ERROR);
            message.setData("不能添加自己");
            channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(message)));
            return;
        }
        //判断好友是否已经添加过
        List<Integer> friendIdList = friendService.list(Wrappers.<Friend>
                lambdaQuery().eq(Friend::getUserId,message.getSendUserId()).
                eq(Friend::getStatus,1))
                .stream().map(Friend::getFriendId)
                .collect(Collectors.toList());
        if (friendIdList.contains(message.getReceiverId())){
            message.setType(Type.MESSAGE_ERROR);
            message.setData("好友已经添加过，不能重复添加");
            channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(message)));
            return;
        }
        //发送消息给rabbitmq,执行好友申请保存
        rabbitTemplate.convertAndSend(RabbitMqConfig.DATABASE_EXCHANGE,
                RabbitMqConfig.DATABASE_KEY,message);
        //获取接收人信息
        Integer receiverId = message.getReceiverId();
        //查询接收人管道
        HandlerMessage.onlineGroup.forEach(channel1 -> {
            //获取channel1管道信息
            MyMessage channelMessage = channel1.attr(HandlerMessage.MY_MESSAGE_ATTRIBUTE_KEY).get();
            //查询到被添加人在线
            if (channelMessage.getSendUserId().equals(receiverId)){
                //发送消息，让其执行拉去好友申请列表事件
                MyMessage outMsg = new MyMessage();
                outMsg.setType(Type.NEW_FRIEND);
                String m = JSON.toJSONString(outMsg);
                channel1.writeAndFlush(new TextWebSocketFrame(m));
            }
        });
    }

    //mq消息处理
    @Override
    public void dealMqMessage(MyMessage message) {
        //查询添加人信息
        User user = userService.getById(message.getReceiverId());
        //保存数据到redis中
        Friend friend = new Friend();
        friend.setFriendId(message.getReceiverId());
        friend.setUserId(message.getSendUserId());
        friend.setStatus(0);
        friend.setFriendName(user.getUserName());
        //获取用户名首字母
        char first = friend.getFriendName().toCharArray()[0];
        friend.setFirstName(PinyinUtils.getHead(first));
        //保存数据库
        friendService.save(friend);
        //给延迟交换机添加一个添加请求过期任务
        rabbitTemplate.convertAndSend(RabbitMqConfig.DELAY_EXCHANGE,RabbitMqConfig.DELAY_KEY,
                friend.getId(),msg -> {
                    //设置延迟发送时间
                    msg.getMessageProperties().setHeader("x-delay",3*24*60*60);
                    return msg;
                });
    }

}
