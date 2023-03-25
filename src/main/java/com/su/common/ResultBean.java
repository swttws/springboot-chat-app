package com.su.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

//返回结果封装
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ResultBean implements Serializable {
    private String code;//状态码
    private String message;//消息
    private Object data;//数据

    //成功返回
    public static ResultBean success(){
        ResultBean resultBean=new ResultBean();
        resultBean.setCode("200");
        return resultBean;
    }

    //失败
    public static ResultBean error(){
        ResultBean resultBean = new ResultBean();
        resultBean.setCode("500");
        return resultBean;
    }

    //消息
    public ResultBean message(String message){
        this.setMessage(message);
        return this;
    }

    //数据
    public ResultBean data(Object data){
        this.setData(data);
        return this;
    }
}
