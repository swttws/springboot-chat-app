package com.su.netty.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.su.netty.protocol.MyMessage;
import com.su.netty.strategy.HandlerMessage;
import com.su.pojo.Dialog;
import com.su.pojo.Groupmessage;
import com.su.pojo.Onemessage;
import com.su.service.DialogService;
import com.su.service.GroupmessageService;
import com.su.service.OnemessageService;
import com.su.utils.CommonUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

//消息处理器
@Slf4j
public class WebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private HandlerMessage handlerMessage;//消息处理器

    private RedisTemplate<String,Object> redisTemplate;

    private DialogService dialogService;

    private OnemessageService onemessageService;

    private GroupmessageService groupmessageService;

    public WebSocketServerHandler(HandlerMessage handlerMessage,RedisTemplate<String,Object> redisTemplate,
                                  DialogService dialogService,OnemessageService onemessageService,
                                  GroupmessageService groupmessageService){
        this.handlerMessage=handlerMessage;
        this.redisTemplate=redisTemplate;
        this.dialogService=dialogService;
        this.onemessageService=onemessageService;
        this.groupmessageService=groupmessageService;
    }

    //处理客户端发送过来的消息
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        log.info("客户端传来的消息：{}",textWebSocketFrame.text()+":");
        //获取消息，管道
        String text = textWebSocketFrame.text();
        Channel channel = channelHandlerContext.channel();
        //获取事件类型
        JSONObject jsonObject = JSONObject.parseObject(text);
        MyMessage message = jsonObject.toJavaObject(MyMessage.class);

        //采用不同策略处理不同的消息事件
        handlerMessage.handlerMessage(channel,message);
    }

    //发生异常时关闭连接
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        Channel client = ctx.channel();
        System.out.println("出现异常");
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
    }


}
