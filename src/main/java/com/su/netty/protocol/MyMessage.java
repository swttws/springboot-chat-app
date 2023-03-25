package com.su.netty.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

//消息信息
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyMessage implements Serializable {

    private Type type;//消息事件类型

    private Object data;//消息内容

    private Integer sendUserId;//发送人id

    private String sendUserName;//发送人姓名

    private Integer receiverId;//接收人id

    private Date sendTime;//发送消息时间

    private Integer onlineNums;//在线人数

    private String ipaddr;//ip地址以及端口号

}
