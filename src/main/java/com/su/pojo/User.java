package com.su.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * <p>
 * 
 * </p>
 *
 * @author swt 2023-3-18
 * @since 2023-03-18
 */
@TableName("user")
@ApiModel(value = "User对象", description = "")
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable{

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("用户名称")
    @TableField("user_name")
    private String userName;

    @ApiModelProperty("密码")
    @TableField("password")
    private String password;

    @ApiModelProperty("手机号")
    @TableField("phone")
    private String phone;

    @ApiModelProperty("性别，1男，2女")
    @TableField("sex")
    private Integer sex;

    @ApiModelProperty("微聊号")
    @TableField("account")
    private String account;

    @ApiModelProperty("地址")
    @TableField("address")
    private String address;

    @ApiModelProperty("头像")
    @TableField("img_url")
    private String imgUrl;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;


}
