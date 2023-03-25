package com.su.controller;


import com.su.common.ResultBean;
import com.su.pojo.Onemessage;
import com.su.service.OnemessageService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.websocket.OnMessage;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author swt 2023-3-18
 * @since 2023-03-21
 */
@RestController
@RequestMapping("/onemessage")
@CrossOrigin
public class OnemessageController {

    @Autowired
    private OnemessageService onemessageService;

    @ApiOperation("根据自己id和好友id获取消息")
    @GetMapping("getMessageList/{myId}/{friendId}")
    public ResultBean getMessageList(@PathVariable Integer myId,@PathVariable Integer friendId){
        List<Onemessage> onemessageList=onemessageService.getMessageList(myId,friendId);
        return ResultBean.success().data(onemessageList);
    }
}

