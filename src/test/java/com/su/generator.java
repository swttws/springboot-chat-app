package com.su;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.fill.Column;
import com.baomidou.mybatisplus.generator.fill.Property;

import java.util.Collections;

public class generator {
    public static void main(String[] args) {

        FastAutoGenerator.create("jdbc:mysql://localhost:3306/chat?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=UTF-8",
                "root","217812.com")
                .globalConfig(builder -> {
                    builder.author("swt 2023-3-18") // 设置作者
                            .fileOverride()
                            .outputDir("D:\\java-mianshi\\Chat\\springboot-chat-app\\src\\main\\java")
                            .enableSwagger(); // 开启 swagger 模式
                })
                .packageConfig(builder -> {
                    builder.parent("com.su") // 设置父包名
                            .entity("pojo")
                            .service("service")
                            .controller("controller")
                            .mapper("mapper")
                            .serviceImpl("service.impl")
                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml, "D:\\java-mianshi\\Chat\\springboot-chat-app\\src\\main\\resources\\mapper")); // 设置mapperXml生成路;
                })
                .strategyConfig(builder -> {
                    builder.addInclude("groupmessage") // 设置需要生成的表名
                            .entityBuilder()
                            .enableTableFieldAnnotation()
                            .addTableFills(new Column("create_time", FieldFill.INSERT))
                            .addTableFills(new Property("updateTime", FieldFill.INSERT_UPDATE))
                            .idType(IdType.AUTO)
                            .controllerBuilder()
                            .enableRestStyle()
                            .formatFileName("%sController")
                            .serviceBuilder()
                            .formatServiceFileName("%sService")
                            .formatServiceImplFileName("%sServiceImp")
                            .mapperBuilder()
                            .formatMapperFileName("%sMapper")
                            .formatXmlFileName("%sMapper")
                            .enableMapperAnnotation();
                })
                .execute();

    }
}
