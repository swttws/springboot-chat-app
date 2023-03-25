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

/**
 * <p>
 * 
 * </p>
 *
 * @author swt 2023-3-18
 * @since 2023-03-22
 */
@TableName("`group`")
@ApiModel(value = "Group对象", description = "")
public class Group implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("群主id")
    @TableField("user_id")
    private Integer userId;

    @ApiModelProperty("群员id，以逗号隔开")
    @TableField("group_id")
    private String groupId;

    @ApiModelProperty("群名称")
    @TableField("group_name")
    private String groupName;

      @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

      @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "Group{" +
        "id=" + id +
        ", userId=" + userId +
        ", groupId=" + groupId +
        ", groupName=" + groupName +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        "}";
    }
}
