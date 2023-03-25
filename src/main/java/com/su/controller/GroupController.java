package com.su.controller;


import com.su.common.ResultBean;
import com.su.pojo.Group;
import com.su.service.GroupService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.RelationSupport;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author swt 2023-3-18
 * @since 2023-03-22
 */
@RestController
@RequestMapping("/group")
@CrossOrigin
public class GroupController {

    @Autowired
    private GroupService groupService;

    @ApiOperation("根据群号id获取群信息")
    @GetMapping("getGroupById/{groupId}")
    public ResultBean getGroupById(@PathVariable Integer groupId){
        Group byId = groupService.getById(groupId);
        return ResultBean.success().data(byId);
    }


}

