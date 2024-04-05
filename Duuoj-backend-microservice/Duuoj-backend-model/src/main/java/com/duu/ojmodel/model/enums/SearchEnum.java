package com.duu.ojmodel.model.enums;
import org.apache.commons.lang3.ObjectUtils;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description: 查询类型枚举
 * @author: duu
 * @date: 2024/4/5 16:45
 **/
public enum SearchEnum {

    USER("标题", "title"),
    POST("标签", "tag"),
    PICTURE("内容", "content");

    private final String text;

    private final String value;

    SearchEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static SearchEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (SearchEnum anEnum : SearchEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}