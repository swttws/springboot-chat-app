package com.su;

import com.su.netty.protocol.MyMessage;
import com.su.netty.protocol.Type;
import com.su.netty.strategy.HandlerMessage;
import com.su.netty.strategy.MessageStrategy;
import com.su.pojo.Dialog;
import io.netty.channel.sctp.nio.NioSctpChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootTest(classes = ChatApplication.class)
@RunWith(SpringRunner.class)
public class Test {

    @Autowired
    private HandlerMessage handlerMessage;

    @org.junit.Test
    public void test(){

        MyMessage message = new MyMessage();
        message.setType(Type.CONNECT_EVENT);
        handlerMessage.handlerMessage(new NioServerSocketChannel(),message );
    }

    @Autowired
    private RedisTemplate redisTemplate;

    @org.junit.Test
    public void test23(){
        redisTemplate.opsForZSet().add("333",new A("john",1),23);
        redisTemplate.opsForZSet().add("333",new A("tom",1),23);
        redisTemplate.opsForZSet().remove("333",new A("tom",1));
        Set set = redisTemplate.opsForZSet().range("333", 0, -1);
        for (Object o : set) {
            System.out.println(o);
        }
    }

    @org.junit.Test
    public void test15(){
        redisTemplate.opsForValue().setIfPresent("23",27);
        System.out.println(redisTemplate.opsForValue().get("23"));
    }

    @Data
    @AllArgsConstructor
    @ToString
    public static class A implements Serializable {
        private String name;
        private Integer age;
    }

    @org.junit.Test
    public void teest(){
        List<A> list=new ArrayList<>();
        list.add(new A("2",1));
        list.add(new A("3",1));
        list.add(new A("4",1));
        for (A a : list) {
            redisTemplate.opsForZSet().add("222",a,1);
        }
        redisTemplate.opsForZSet().add("333",new A("5",1),2);

        redisTemplate.opsForZSet().unionAndStore("222","333","444");

        Set range = redisTemplate.opsForZSet().range("444", 0, -1);
        range.forEach(System.out::println);

    }

}
