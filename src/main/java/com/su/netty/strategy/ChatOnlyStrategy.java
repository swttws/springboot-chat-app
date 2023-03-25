package com.su.netty.strategy;

import com.alibaba.fastjson.JSON;
import com.su.config.RabbitMqConfig;
import com.su.netty.protocol.MyMessage;
import com.su.netty.protocol.Type;
import com.su.pojo.Dialog;
import com.su.pojo.Onemessage;
import com.su.pojo.view.OffLIneCache;
import com.su.service.DialogService;
import com.su.service.OnemessageService;
import com.su.utils.CommonUtils;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@TypeAnnotation(Type.CHAT_ONLY)
@Component
public class ChatOnlyStrategy implements MessageStrategy{

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private OnemessageService onemessageService;

    @Autowired
    private DialogService dialogService;

    //处理单聊消息
    @Override
    public void handleMessage(Channel channel, MyMessage msg) {
        //判断客户端2是否在线
        long count = HandlerMessage.onlineGroup.stream().filter(onlineChannel ->
                onlineChannel.attr(HandlerMessage.MY_MESSAGE_ATTRIBUTE_KEY).get().
                        getSendUserId().equals(msg.getReceiverId())
        ).count();
        //封装消息
        Onemessage onemessage=new Onemessage();
        onemessage.setContext((String) msg.getData());
        onemessage.setCreateTime(new Date());
        onemessage.setReceiverUserid(msg.getReceiverId());
        onemessage.setSendUserid(msg.getSendUserId());
        //TODO 不同消息类型，暂时只发送文本消息
        onemessage.setType(1);
        onemessage.setStatus(count>0?2:1);
        //redis保存聊天记录
        String messageKey=CommonUtils.message_prefix;
        List<Onemessage> onemessageList = (List<Onemessage>) redisTemplate.
                opsForValue().get(messageKey);
        if (onemessageList==null||onemessageList.size()==0){
            onemessageList=new ArrayList<>();
        }
        onemessageList.add(onemessage);
        redisTemplate.opsForValue().set(messageKey,onemessageList);
        //在线
        if (count>0){
            //更新会话信息
            dialogService.updateDialog(msg,1);
            //获取会话列表
            List<Dialog> collect = dialogService.getDialogByMyId(msg.getReceiverId());

            //返回用户会话列表给接收方
            HandlerMessage.onlineGroup.forEach(onlineChannel->{
                MyMessage message = onlineChannel.attr(HandlerMessage.MY_MESSAGE_ATTRIBUTE_KEY).get();
                if (message.getSendUserId().equals(msg.getReceiverId())){
                    //返回新消息和新对话列表给接收方
                    Map<String,Object> map=new HashMap<>();
                    map.put("dialog",collect);
                    map.put("message",onemessage);
                    msg.setData(map);
                    msg.setType(Type.NEW_MESSAGE);
                    onlineChannel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(msg)));
                }
            });
        }else{//不在线
            //发送消息给mq，存储到数据库中
            msg.setData(onemessage);
            rabbitTemplate.convertAndSend(RabbitMqConfig.DATABASE_EXCHANGE,
                    RabbitMqConfig.DATABASE_KEY,msg);
        }
        //数据写回客户端
        msg.setOnlineNums(1);
        channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(msg)));
    }

    //处理mq消息消费逻辑
    @Override
    public void dealMqMessage(MyMessage message) {
        //获取消息，保存数据库
        Onemessage onemessage= (Onemessage) message.getData();
        onemessageService.save(onemessage);
        //保存离线日志到缓存中
        dialogService.saveOffLineInfo(message,1);
    }
}
