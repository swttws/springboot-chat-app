package com.su.netty.strategy;


import com.su.netty.protocol.MyMessage;
import io.netty.channel.Channel;

//处理消息事件接口
public interface MessageStrategy  {

    //处理消息
    public void handleMessage(Channel channel, MyMessage msg);

    //处理mq消息
    public void dealMqMessage(MyMessage message);

}
