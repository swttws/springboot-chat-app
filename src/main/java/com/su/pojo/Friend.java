package com.su.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 
 * </p>
 *
 * @author swt 2023-3-18
 * @since 2023-03-19
 */
@TableName("friend")
@ApiModel(value = "Friend对象", description = "")
@Data
public class Friend implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("自己的id")
    @TableField("user_id")
    private Integer userId;

    @ApiModelProperty("好友id")
    @TableField("friend_id")
    private Integer friendId;

    @ApiModelProperty("好友姓名")
    @TableField("friend_name")
    private String friendName;

    @ApiModelProperty("好友姓名首字母")
    @TableField("first_name")
    private String firstName;

    @ApiModelProperty("0未通过好友，1已通过好友，2已过期好友请求")
    @TableField("status")
    private Integer status;

      @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

      @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


      @TableField(exist = false)
    private String userName;//申请人姓名

}
