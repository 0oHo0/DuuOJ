package com.duu.ojjudgeservice.codesandbox.impl;


import com.duu.ojjudgeservice.codesandbox.CodeSandbox;
import com.duu.ojmodel.model.entity.ExecuteCodeRequest;
import com.duu.ojmodel.model.entity.ExecuteCodeResponse;

/**
 * 远程代码沙箱（实际调用接口的沙箱）
 */
public class RemoteCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("远程代码沙箱");
        return null;
    }
}
