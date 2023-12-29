package com.duu.ojjudgeservice.codesandbox.impl;


import com.duu.ojjudgeservice.codesandbox.CodeSandbox;
import com.duu.ojmodel.model.entity.ExecuteCodeRequest;
import com.duu.ojmodel.model.entity.ExecuteCodeResponse;
import com.duu.ojmodel.model.entity.JudgeInfo;
import com.duu.ojmodel.model.enums.JudgeInfoMessageEnum;
import com.duu.ojmodel.model.enums.QuestionSubmitStatusEnum;

import java.util.List;

/**
 * @author : duu
 * @data : 2023/12/13
 * @from ：https://github.com/0oHo0
 **/
public class ExampleCodeSandbox implements CodeSandbox {

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setOutputList(inputList);
        executeCodeResponse.setMessage("成功");
        executeCodeResponse.setStatus(QuestionSubmitStatusEnum.SUCCEED.getText());
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage(JudgeInfoMessageEnum.ACCEPTED.getValue());
        judgeInfo.setMemory(100L);
        judgeInfo.setTime(100L);
        executeCodeResponse.setJudgeInfo(judgeInfo);
        return executeCodeResponse;
    }
}
