package com.su.controller;


import com.su.common.ResultBean;
import com.su.pojo.Dialog;
import com.su.service.DialogService;
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
 * @since 2023-03-21
 */
@RestController
@RequestMapping("/dialog")
@CrossOrigin
public class DialogController {

    @Autowired
    private DialogService dialogService;

    @ApiOperation("获取会话列表")
    @GetMapping("getDialogByMyId/{userId}")
    public ResultBean getDialogByMyId(@PathVariable Integer userId){
        List<Dialog> dialogList=dialogService.getDialogByMyId(userId);
        return ResultBean.success().data(dialogList);
    }







}

