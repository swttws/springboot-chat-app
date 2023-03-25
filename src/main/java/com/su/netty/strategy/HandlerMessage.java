package com.su.netty.strategy;

import com.su.netty.protocol.MyMessage;
import com.su.netty.protocol.Type;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class HandlerMessage implements ApplicationContextAware {

    private static Map<Type,Class<MessageStrategy>> strategyMap=new ConcurrentHashMap<>();//存储策略模式容器

    //记录在线用户
    public static ChannelGroup onlineGroup=new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    //扩展属性,保存连接相关西信息
    public static final AttributeKey<MyMessage> MY_MESSAGE_ATTRIBUTE_KEY=
            AttributeKey.valueOf("myMessage");

    @Autowired
    private ApplicationContext applicationContext;

    //通过消息类型获取执行对应的策略
    public void handlerMessage(Channel channel, MyMessage msg){
        //获取策略类
        Class<MessageStrategy> messageStrategyClass =
                strategyMap.get(msg.getType());
        MessageStrategy strategy = applicationContext.getBean(messageStrategyClass);
        strategy.handleMessage(channel,msg);
    }

    //mq消息处理
    public void dealMqMessage(MyMessage message){
        //获取策略类
        Class<MessageStrategy> messageStrategyClass =
                strategyMap.get(message.getType());
        MessageStrategy strategy = applicationContext.getBean(messageStrategyClass);
        strategy.dealMqMessage(message);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        applicationContext.getBeansWithAnnotation(TypeAnnotation.class)
                .entrySet()
                .iterator()
                .forEachRemaining(clazz->{
                    //获取策略bean
                    Class<MessageStrategy> aClass = (Class<MessageStrategy>) clazz.getValue().getClass();
                    //获取注解上的值
                    Type value = aClass.getAnnotation(TypeAnnotation.class).value();
                    //保存在map中
                    strategyMap.put(value,aClass);
                });
    }
}
