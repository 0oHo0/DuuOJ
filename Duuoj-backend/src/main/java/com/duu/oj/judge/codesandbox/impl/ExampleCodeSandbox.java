package com.duu.oj.judge.codesandbox.impl;

import com.duu.oj.judge.codesandbox.CodeSandbox;
import com.duu.oj.judge.codesandbox.model.ExecuteCodeRequest;
import com.duu.oj.judge.codesandbox.model.ExecuteCodeResponse;
import com.duu.oj.judge.codesandbox.model.JudgeInfo;
import com.duu.oj.model.enums.JudgeInfoMessageEnum;
import com.duu.oj.model.enums.QuestionSubmitStatusEnum;

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
