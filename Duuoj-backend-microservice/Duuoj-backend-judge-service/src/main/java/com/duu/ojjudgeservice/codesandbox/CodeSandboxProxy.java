package com.duu.ojjudgeservice.codesandbox;

import com.duu.ojmodel.model.entity.ExecuteCodeRequest;
import com.duu.ojmodel.model.entity.ExecuteCodeResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : duu
 * @data : 2023/12/13
 * @from : https://github.com/0oHo0
 **/
@Data
@Slf4j
public class CodeSandboxProxy implements CodeSandbox {
    private final CodeSandbox codeSandbox;


    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        log.info("代码沙箱请求信息：" + executeCodeRequest.toString());
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        log.info("代码沙箱响应信息：" + executeCodeResponse.toString());
        return executeCodeResponse;
    }
}
