package com.su.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 
 * </p>
 *
 * @author swt 2023-3-18
 * @since 2023-03-21
 */
@TableName("dialog")
@ApiModel(value = "Dialog对象", description = "")
@Data
public class Dialog implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("自己的id1")
    @TableField("my_id")
    private Integer myId;

    @ApiModelProperty("好友消息")
    @TableField("friend_id")
    private Integer friendId;

    @ApiModelProperty("最后在线时间")
    @TableField("last_time")
    private Date lastTime;

    @ApiModelProperty("会话类型，1.单聊，2.群聊")
    @TableField("type")
    private Integer type;

    @ApiModelProperty("未读消息")
    @TableField(exist = false)
    private Integer notReadMsgCount;

    @ApiModelProperty("好友名字")
    @TableField(exist = false)
    private String friendName;

    @ApiModelProperty("评分，会话列表展示顺序")
    @TableField(exist = false)
    private long timeMillis;


}
