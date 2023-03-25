package com.su.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

/**
 * <p>
 * 
 * </p>
 *
 * @author swt 2023-3-18
 * @since 2023-03-21
 */
@TableName("onemessage")
@ApiModel(value = "Onemessage对象", description = "")
@Data
public class Onemessage implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("发送者id")
    @TableField("send_userid")
    private Integer sendUserid;

    @ApiModelProperty("接收人id1")
    @TableField("receiver_userid")
    private Integer receiverUserid;

    @ApiModelProperty("消息")
    @TableField("context")
    private String context;

    @ApiModelProperty("1未读，2.已读")
    @TableField("status")
    private Integer status;

    @ApiModelProperty("消息发送时间")
      @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty("消息类型,1.普通文本，2.文件，3表情")
    @TableField("type")
    private Integer type;

    @ApiModelProperty("是否是自己")
    @TableField(exist = false)
    private boolean isself;



}
