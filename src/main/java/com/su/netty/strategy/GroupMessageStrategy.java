package com.su.netty.strategy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.su.config.RabbitMqConfig;
import com.su.netty.protocol.MyMessage;
import com.su.netty.protocol.Type;
import com.su.pojo.Dialog;
import com.su.pojo.Group;
import com.su.pojo.Groupmessage;
import com.su.service.DialogService;
import com.su.service.GroupService;
import com.su.service.GroupmessageService;
import com.su.utils.CommonUtils;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@TypeAnnotation(Type.GROUP_MESSAGE)
@Component
public class GroupMessageStrategy implements MessageStrategy{

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private GroupmessageService groupmessageService;

    @Autowired
    private DialogService dialogService;

    @Autowired
    private GroupService groupService;

    @Override
    public void handleMessage(Channel channel, MyMessage msg) {
        //查找改群聊用户id
        Group group = groupService.getById(msg.getReceiverId());
        String groupId = group.getGroupId();
        List<Integer> groupIds = JSONArray.parseArray(groupId, Integer.class);

        //获取离线群用户id
        List<Integer> offLineIds= groupIds.stream().filter(id -> !HandlerMessage.onlineGroup.contains(id))
                .collect(Collectors.toList());
        Map<String,Object> map1=new HashMap<>();
        map1.put("message",msg.getData());
        map1.put("offLine",offLineIds);
        msg.setData(map1);
        //通知mq，数据保存到redis中
        rabbitTemplate.convertAndSend(RabbitMqConfig.DATABASE_EXCHANGE,
                RabbitMqConfig.DATABASE_KEY,msg);

        //通知在线用户有消息到达
        HandlerMessage.onlineGroup.forEach(onlineChannel->{
            //获取与管道绑定的信息
            MyMessage message = onlineChannel.attr(HandlerMessage.MY_MESSAGE_ATTRIBUTE_KEY).get();
            //若是群成员信息,通知
            if (groupIds.contains(message.getSendUserId())){
                //更改在线成员会话信息，并通知客户端
                MyMessage myMessage = new MyMessage();
                myMessage.setSendUserId(msg.getReceiverId());//修改群会话id
                myMessage.setReceiverId(message.getSendUserId());//会话拥有者id
                dialogService.updateDialog(myMessage,2);
                List<Dialog> newDialogList = dialogService.getDialogByMyId(message.getSendUserId());

                String tip=group.getGroupName()+"群聊的成员："+msg.getSendUserName()+"发送了一条消息，不要错过了";
                Map<String,Object> map=new HashMap<>();
                map.put("tip",tip);
                map.put("dialogList",newDialogList);
                map.put("message",msg.getData());
                msg.setData(map);
                msg.setSendUserId(1);
                onlineChannel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(msg)));
            }
        });
        //消息发送回去给自己
        msg.setSendUserId(2);
        channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(msg)));
    }

    @Override
    public void dealMqMessage(MyMessage message) {
        Map<String,Object> map = (Map) message.getData();
        //redis保存消息
        List<Groupmessage> groupmessageList = (List<Groupmessage>)
                redisTemplate.opsForValue().get(CommonUtils.group_message);
        if (groupmessageList==null){
            groupmessageList=new ArrayList<>();
        }
        //群聊消息封装
        Groupmessage groupmessage = new Groupmessage();
        groupmessage.setGroupsId(message.getReceiverId());
        groupmessage.setCreateTime(new Date());
        groupmessage.setContext((String) map.get("message"));
        groupmessage.setStatus(0);
        groupmessage.setUserId(message.getSendUserId());
        groupmessage.setUserName(message.getSendUserName());
        groupmessage.setType(1);
        //redis中增加消息达到15，取最晚添加15条数据更新进数据库
        if (groupmessageList.size()%CommonUtils.group_count==0){
            //取添加数据前15条数据
            Collections.reverse(groupmessageList);
            List<Groupmessage> collect = groupmessageList.stream().limit(CommonUtils.group_count)
                    .filter(msg -> msg.getStatus() == 0)
                    .collect(Collectors.toList());
            groupmessageService.saveBatch(collect);
            groupmessageList.forEach(item->{
                if (collect.contains(item)){
                    item.setStatus(1);
                }
            });
        }
        groupmessageList.add(groupmessage);
        //删除原来key，保存新的值
        redisTemplate.delete(CommonUtils.group_message);
        redisTemplate.opsForValue().set(CommonUtils.group_message,groupmessageList);

        //保存缓存日志到redis中
        List<Integer> offLineIds = (List<Integer>) map.get("offLine");
        offLineIds.forEach(offLineId->{
            message.setSendUserId(message.getReceiverId());//群id
            message.setReceiverId(offLineId);//离线用户id
            dialogService.saveOffLineInfo(message,2);
        });
    }
}
