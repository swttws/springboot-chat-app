package com.su.pojo.view;

import lombok.Data;

import java.io.Serializable;

@Data
public class Select implements Serializable {
    private String text;

    private Integer value;
}
