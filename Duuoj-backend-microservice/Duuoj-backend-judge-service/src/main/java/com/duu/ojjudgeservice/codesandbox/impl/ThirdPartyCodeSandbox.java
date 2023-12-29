package com.duu.ojjudgeservice.codesandbox.impl;


import com.duu.ojjudgeservice.codesandbox.CodeSandbox;
import com.duu.ojmodel.model.entity.ExecuteCodeRequest;
import com.duu.ojmodel.model.entity.ExecuteCodeResponse;

/**
 * 第三方代码沙箱（调用网上现成的代码沙箱）
 */
public class ThirdPartyCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("第三方代码沙箱");
        return null;
    }
}
