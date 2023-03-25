package com.su.netty.strategy;

import com.alibaba.fastjson.JSON;
import com.su.config.RabbitMqConfig;
import com.su.netty.protocol.MyMessage;
import com.su.netty.protocol.Type;
import com.su.pojo.Dialog;
import com.su.pojo.Friend;
import com.su.service.DialogService;
import com.su.service.FriendService;
import com.su.service.UserService;
import com.su.utils.PinyinUtils;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@TypeAnnotation(Type.AGREE_APPLICATION)
public class AcceptFriendStrategy implements MessageStrategy{

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private FriendService friendService;

    @Autowired
    private UserService userService;

    @Autowired
    private DialogService dialogService;

    //好友同意申请处理
    @Override
    public void handleMessage(Channel channel, MyMessage msg) {
        //发送消息给mq，修改好友申请
        rabbitTemplate.convertAndSend(RabbitMqConfig.DATABASE_EXCHANGE,
                RabbitMqConfig.DATABASE_KEY,msg);
        //写消息通知客户端，通知申请人客户端拉取新的好友列表
        HandlerMessage.onlineGroup.forEach(channel1 -> {
            //获取对应管道绑定信息
            MyMessage message = channel1.attr(HandlerMessage.MY_MESSAGE_ATTRIBUTE_KEY).get();
            //客户端为申请人id，发送消息
            if (message.getSendUserId().equals(msg.getReceiverId())){
                MyMessage message1 = new MyMessage();
                message1.setType(Type.PULL_FRIEND);
                String tip=msg.getSendUserName()+"同意了你的好友申请";
                message1.setData(tip);
                String m= JSON.toJSONString(message1);
                //写消息给客户端
                channel1.writeAndFlush(new TextWebSocketFrame(m));
            }
            //写消息给客户端，重新拉取申请列表
            MyMessage message1 = new MyMessage();
            message1.setType(Type.PULL_MSG);
            channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(message1)));
        });
    }

    //mq消息处理
    @Override
    public void dealMqMessage(MyMessage message) {
        //获取消息中的好友申请id
        Integer friendId = (Integer) message.getData();
        //查询申请信息
        Friend friend = friendService.getById(friendId);
        //修改状态为同意申请
        friend.setStatus(1);
        friendService.updateById(friend);
        //获取申请人姓名
        String userName = userService.getById(friend.getUserId()).getUserName();
        //保存同意者与申请者关系
        Friend friend1 = new Friend();
        friend1.setUserId(message.getSendUserId());
        friend1.setStatus(1);
        friend1.setFriendId(friend.getUserId());
        friend1.setFriendName(userName);
        //获取首字符
        char first= userName.toCharArray()[0];
        friend1.setFirstName(PinyinUtils.getHead(first));
        friendService.save(friend1);
        //保存双方会话
        //保存发送者--接收者会话
        Dialog dialog = new Dialog();
        dialog.setMyId(message.getSendUserId());
        dialog.setFriendId(message.getReceiverId());
        dialog.setLastTime(new Date());
        dialog.setType(1);
        dialogService.save(dialog);
        Dialog dialog1 = new Dialog();
        dialog1.setFriendId(message.getSendUserId());
        dialog1.setMyId(message.getReceiverId());
        dialog1.setLastTime(new Date());
        dialog1.setType(1);
        dialogService.save(dialog1);
    }


}
