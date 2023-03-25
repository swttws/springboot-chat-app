package com.su.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMqConfig {

    //处理信息保存数据库队列交换机
    public static final String DATABASE_EXCHANGE="database_exchange";
    public static final String DATABASE_QUEUE="database_queue";
    public static final String DATABASE_KEY="database_key";

    @Bean
    public Queue queue(){
        return QueueBuilder.durable(DATABASE_QUEUE).build();
    }

    @Bean
    public DirectExchange exchange(){
        return ExchangeBuilder.directExchange(DATABASE_EXCHANGE).build();
    }

    @Bean
    public Binding binding(){
        return BindingBuilder.bind(queue()).to(exchange())
                .with(DATABASE_KEY);
    }

    public static final String DELAY_EXCHANGE="delay_exchange";
    public static final String DELAY_QUEUE="delay_queue";
    public static final String DELAY_KEY="delay";

    @Bean
    public Queue delayQueue(){
        return QueueBuilder.durable(DELAY_QUEUE).build();
    }

    //延迟交换机
    @Bean
    public CustomExchange customExchange(){
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange(DELAY_EXCHANGE,"x-delayed-message", true, false,args);
    }

    @Bean
    public Binding bindings(){
        return BindingBuilder.bind(delayQueue())
                .to(customExchange())
                .with(DELAY_KEY)
                .noargs();
    }


}
