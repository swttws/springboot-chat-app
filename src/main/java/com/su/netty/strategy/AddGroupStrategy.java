package com.su.netty.strategy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.su.config.RabbitMqConfig;
import com.su.netty.protocol.MyMessage;
import com.su.netty.protocol.Type;
import com.su.pojo.Dialog;
import com.su.pojo.Group;
import com.su.service.DialogService;
import com.su.service.GroupService;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@TypeAnnotation(Type.ADD_GROUP)
public class AddGroupStrategy implements MessageStrategy{

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private GroupService groupService;

    @Autowired
    private DialogService dialogService;

    @Override
    public void handleMessage(Channel channel, MyMessage msg) {
        //通知mq，群聊数据保存数据库
        rabbitTemplate.convertAndSend(RabbitMqConfig.DATABASE_EXCHANGE,
                RabbitMqConfig.DATABASE_KEY,msg);
        Map<String,Object> map= (Map<String, Object>) msg.getData();
        List<Integer> groupIds = (List<Integer>) map.get("groupIds");
        //通知在线用户群聊创建成功
        HandlerMessage.onlineGroup.forEach(onlineChannel->{
            //获取管道绑定值
            MyMessage message = onlineChannel.attr(HandlerMessage.MY_MESSAGE_ATTRIBUTE_KEY).get();
            if (msg.getSendUserId().equals(message.getSendUserId())||
            groupIds.contains(message.getSendUserId())){
                String str="你的好友建立了群，恭喜成为群成员";
                msg.setData(str);
                onlineChannel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(msg)));
            }
        });
    }

    @Override
    public void dealMqMessage(MyMessage message) {
        //保存群信息
        Map<String,Object> map= (Map<String, Object>) message.getData();
        List<Integer> groupIds = (List<Integer>) map.get("groupIds");
        String groupName= (String) map.get("groupName");
        Group group = new Group();
        group.setUserId(message.getSendUserId());
        group.setGroupId(JSON.toJSONString(groupIds));
        group.setGroupName(groupName);
        //保存数据库
        groupService.save(group);
        //建立会话信息
        groupIds.add(message.getSendUserId());
        List<Dialog> dialogList=new ArrayList<>();
        groupIds.forEach(id->{
            Dialog dialog = new Dialog();
            dialog.setType(2);
            dialog.setMyId(id);
            dialog.setLastTime(new Date());
            dialog.setFriendId(group.getId());
            dialogList.add(dialog);
        });
        //批量添加
        dialogService.saveBatch(dialogList);
    }
}
