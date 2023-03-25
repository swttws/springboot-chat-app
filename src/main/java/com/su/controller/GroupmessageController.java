package com.su.controller;


import com.su.common.ResultBean;
import com.su.pojo.Groupmessage;
import com.su.pojo.User;
import com.su.service.GroupmessageService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author swt 2023-3-18
 * @since 2023-03-22
 */
@RestController
@RequestMapping("/groupmessage")
@CrossOrigin
public class GroupmessageController {

    @Autowired
    private GroupmessageService groupmessageService;

    @ApiOperation("根据自己的id和群号id，获取群聊最晚5条消息")
    @GetMapping("/getGroupMessage/{groupId}")
    public ResultBean getGroupMessage(@PathVariable Integer groupId){
        List<Groupmessage> groupmessageList=groupmessageService.getGroupMessage(groupId);
        return ResultBean.success().data(groupmessageList);
    }

}

