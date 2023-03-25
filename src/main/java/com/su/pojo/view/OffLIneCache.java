package com.su.pojo.view;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel("存储离线消息")
public class OffLIneCache implements Serializable {

    private Integer sendUserId;//发送者id/群id

    private Integer type;//1单聊离线，2群聊离线

    private Date lastTime;//离线最后一次消息

}
