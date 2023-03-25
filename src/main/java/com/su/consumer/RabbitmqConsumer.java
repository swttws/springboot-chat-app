package com.su.consumer;

import com.su.config.RabbitMqConfig;
import com.su.netty.protocol.MyMessage;
import com.su.netty.protocol.Type;
import com.su.netty.strategy.HandlerMessage;
import com.su.pojo.Friend;
import com.su.pojo.User;
import com.su.service.FriendService;
import com.su.service.UserService;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class RabbitmqConsumer {

    @Autowired
    private FriendService friendService;


    @Autowired
    private HandlerMessage handlerMessage;

    @RabbitListener(queues = RabbitMqConfig.DATABASE_QUEUE)
    public void dealMessage(MyMessage message){
        log.info("数据库保存队列队列消息消费");
        //根据不同的处理策略处理mq消息
        handlerMessage.dealMqMessage(message);
    }

    //修改好友申请状态
    @RabbitListener(queues = RabbitMqConfig.DELAY_QUEUE)
    public void setFriendStatus(Integer id){
        log.info("申请过期队列队列消费");
        //修改好友状态信息
        Friend friend = friendService.getById(id);
        if (friend!=null&&friend.getStatus()!=1){
            friend.setStatus(2);
            friendService.updateById(friend);
        }
    }


}
