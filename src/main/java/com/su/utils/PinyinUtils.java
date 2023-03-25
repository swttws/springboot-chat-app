package com.su.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;

//汉字转换为拼音工具类
public class PinyinUtils {

    public static  String getHead(char first){
        HanyuPinyinOutputFormat format=new HanyuPinyinOutputFormat();
        if (String.valueOf(first).matches("[\\u4E00-\\u9FA5]+")){
            //转换为拼音后换取首字母
            first=getFirst(first);
        }
        return String.valueOf(first);
    }

    private static char getFirst(char first) {
        String[] strings = PinyinHelper.toHanyuPinyinStringArray(first);
        return strings[0].charAt(0);
    }

}
