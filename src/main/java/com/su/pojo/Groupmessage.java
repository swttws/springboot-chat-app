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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 
 * </p>
 *
 * @author swt 2023-3-18
 * @since 2023-03-22
 */
@TableName("groupmessage")
@ApiModel(value = "Groupmessage对象", description = "")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Groupmessage implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("发送消息id")
    @TableField("user_id")
    private Integer userId;

    @ApiModelProperty("发送者姓名")
    @TableField("user_name")
    private String userName;

    @ApiModelProperty("消息内容")
    @TableField("context")
    private String context;

    @ApiModelProperty("消息类型,1.文本，2表情，3图片")
    @TableField("type")
    private Integer type;

    @ApiModelProperty("群号id")
    @TableField("groups_id")
    private Integer groupsId;

      @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

      @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


    @TableField(exist = false)
    @ApiModelProperty("redis中是否更新到数据库，1")
    private Integer status;

    @TableField(exist = false)
    @ApiModelProperty("是否是自己发的消息")
    private boolean isself;
}
