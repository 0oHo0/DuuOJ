package com.duu.sandbox.sdk.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
/**
 * @description: 代码执行请求
 * @author: duu
 * @date: 2024/4/27 19:10
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeRequest {

    private List<String> inputList;

    private String code;

    private String language;
}
