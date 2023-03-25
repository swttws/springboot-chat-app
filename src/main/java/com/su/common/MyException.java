package com.su.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MyException {

    @ExceptionHandler(Errors.class)
    public ResultBean exception(Errors errors){
        errors.printStackTrace();
        return ResultBean.error().message(errors.getMessage());
    }

    // 捕捉UnauthorizedException
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public ResultBean handle401() {
        return new ResultBean("401", "请登录后再操作", null);
    }
}
