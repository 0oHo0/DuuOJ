package com.duu.ojcommon.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author : duu
 * @date : 2024/3/18
 * @from ：https://github.com/0oHo0
 **/
public class TimeUtils {
    public static void main(String[] args) {
        String originalDateTime = "2024-03-17T08:59:59.000+00:00";
        Date formattedDateTime = formatDateTime(originalDateTime);
        System.out.println("Formatted DateTime: " + formattedDateTime);
    }

    public static Date formatDateTime(String originalDateTime) {
        try {
            // 创建原始时间格式化对象
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            // 解析原始时间字符串
            Date date = originalFormat.parse(originalDateTime);
            // 创建目标时间格式化对象
          //  SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 格式化日期时间并返回
            return date;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
