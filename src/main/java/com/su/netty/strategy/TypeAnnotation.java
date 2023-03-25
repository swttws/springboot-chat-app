package com.su.netty.strategy;

import com.su.netty.protocol.Type;
import org.mapstruct.TargetType;

import java.lang.annotation.*;

//枚举类型注解
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface TypeAnnotation {
    Type value();
}
