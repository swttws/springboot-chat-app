package com.su.pojo.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyFriend implements Serializable {

    private String letter;//收字母

    private List<String> contacts;//好友


}
