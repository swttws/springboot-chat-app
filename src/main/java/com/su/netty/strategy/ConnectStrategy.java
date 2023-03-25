package com.su.netty.strategy;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.su.netty.protocol.MyMessage;
import com.su.netty.protocol.Type;
import com.su.pojo.Friend;
import com.su.service.DialogService;
import com.su.service.FriendService;
import com.su.service.UserService;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

//客户端请求连接处理事件
@Component
@TypeAnnotation(Type.CONNECT_EVENT)
@Slf4j
public class ConnectStrategy implements MessageStrategy{
    @Autowired
    private FriendService friendService;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private DialogService dialogService;

    //连接处理逻辑
    @Override
    public void handleMessage(Channel channel, MyMessage msg) {
        log.info("connect连接事件触发----");
        //解析消息体
        channel.attr(HandlerMessage.MY_MESSAGE_ATTRIBUTE_KEY).getAndSet(msg);
        //添加到在线用户列表
        HandlerMessage.onlineGroup.remove(channel);
        HandlerMessage.onlineGroup.add(channel);
        //根据自己的用户id获取好友列表
        List<Friend> friendList = friendService.list(Wrappers.<Friend>lambdaQuery().
                eq(Friend::getUserId, msg.getSendUserId()).
                eq(Friend::getStatus, 1).
                select(Friend::getFriendId));
        List<Integer> friendIdList = friendList.stream()
                .map(Friend::getFriendId)
                .collect(Collectors.toList());

        //同步数据库会话到redis中
        //查询单聊,群聊会话，保存到redis中
        dialogService.getDialogByTypeToRedis(msg.getSendUserId(),1);
        dialogService.getDialogByTypeToRedis(msg.getSendUserId(),2);

        //通知客户端好友上线通知
        for (Channel client : HandlerMessage.onlineGroup) {
            //判断是否为自己
            boolean itself=(client==channel);
            //获取attr属性
            MyMessage channelMessage = channel.attr(HandlerMessage.MY_MESSAGE_ATTRIBUTE_KEY).get();
            //不是自己，且为自己的好友
            if (!itself&&friendIdList.contains(channelMessage.getSendUserId())){
                String content="你的好友["+channelMessage.getSendUserName()+"]已经上线";
                //封装数据包发送给客户端
                MyMessage message = new MyMessage();
                BeanUtils.copyProperties(channelMessage,message);
                message.setData(content);
                message.setType(Type.FRIEND_ONLINE);
                //写回客户端
                String m = JSON.toJSONString(message);
                client.writeAndFlush(new TextWebSocketFrame(m));
            }
        }
        log.info("连接数"+HandlerMessage.onlineGroup.size());

    }

    //处理mq消息
    @Override
    public void dealMqMessage(MyMessage message) {

    }


}
