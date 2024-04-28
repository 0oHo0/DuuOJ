package com.duu.sandbox.sdk.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
/**
 * @description: 代码执行响应
 * @author: duu
 * @date: 2024/4/27 19:10
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeResponse {

    private List<String> outputList;

    /**
     * 接口信息
     */
    private String message;

    /**
     * 执行状态
     */
    private Integer status;

    /**
     * 判题信息
     */
    private JudgeInfo judgeInfo;
}
