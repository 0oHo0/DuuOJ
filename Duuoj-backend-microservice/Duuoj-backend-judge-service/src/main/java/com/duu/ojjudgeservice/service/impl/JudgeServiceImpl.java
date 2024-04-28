package com.duu.ojjudgeservice.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;

import com.duu.ojcommon.common.ErrorCode;
import com.duu.ojcommon.exception.BusinessException;
import com.duu.ojjudgeservice.codesandbox.CodeSandbox;
import com.duu.ojjudgeservice.codesandbox.CodeSandboxFactory;
import com.duu.ojjudgeservice.codesandbox.CodeSandboxProxy;
import com.duu.ojjudgeservice.service.JudgeService;

import com.duu.ojjudgeservice.strategy.JudgeContext;
import com.duu.ojjudgeservice.strategy.JudgeManager;
import com.duu.ojmodel.model.dto.question.JudgeCase;
import com.duu.ojmodel.model.entity.*;
import com.duu.ojmodel.model.enums.JudgeInfoMessageEnum;
import com.duu.ojmodel.model.enums.QuestionSubmitStatusEnum;
import com.duu.ojserviceclient.service.QuestionFeignClient;

import com.duu.sandbox.sdk.SandboxClient;
import com.duu.sandbox.sdk.model.CodeRequest;
import com.duu.sandbox.sdk.model.CodeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : duu
 * @date : 2023/12/13
 * @from ：https://github.com/0oHo0
 **/
@Service
public class JudgeServiceImpl implements JudgeService {
    @Resource
    private QuestionFeignClient questionFeignClient;

    @Resource
    private JudgeManager judgeManager;
    @Value("${duuoj.sandbox.access-key}")
    public String accessKey;
    @Value("${duuoj.sandbox.secret-key}")
    public String secretKey;

    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        if (questionSubmitId<=0)
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"提交ID错误");
//        1）传入题目的提交 id，获取到对应的题目、提交信息（包含代码、编程语言等）
        QuestionSubmit questionSubmit = questionFeignClient.getQuestionSubmitById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交信息不存在");
        }
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionFeignClient.getQuestionById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }
//        2）如果题目提交状态不为等待中，就不用重复执行了
        Integer status = questionSubmit.getStatus();
        if (!status.equals(QuestionSubmitStatusEnum.WAITING.getValue())){
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目正在判题中");
        }
//        3）更改判题（题目提交）的状态为 “判题中”，防止重复执行，也能让用户即时看到状态
        questionSubmit.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean update = questionFeignClient.updateQuestionSubmitById(questionSubmit);
        if (!update)
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
//        4）调用沙箱，获取到执行结果
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance("example");
        codeSandbox = new CodeSandboxProxy(codeSandbox);
        String code = questionSubmit.getCode();
        String language = questionSubmit.getLanguage();
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCases = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        List<String> inputList = judgeCases.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder().code(code).language(language).inputList(inputList).build();
        String executeCodeRequestJson = JSONUtil.toJsonStr(executeCodeRequest);
        String post = HttpUtil.post("localhost:8103/api/execute", executeCodeRequestJson);
        ExecuteCodeResponse executeCodeResponse = JSONUtil.toBean(post, ExecuteCodeResponse.class);
        JudgeInfo judgeInfo = executeCodeResponse.getJudgeInfo();
        List<String> outputList = executeCodeResponse.getOutputList();
     //测试 代码沙箱sdk
        //   CodeRequest codeRequest = CodeRequest.builder().code(code).language(language).inputList(inputList).build();
        //String executeCodeRequestJson = JSONUtil.toJsonStr(executeCodeRequest);
//        SandboxClient sandboxClient = new SandboxClient(accessKey, secretKey);
//        CodeResponse codeResponse = sandboxClient.executeCode(codeRequest);
//        com.duu.sandbox.sdk.model.JudgeInfo judgeInfo = codeResponse.getJudgeInfo();
        //测试SDK时 包类型转换
//        String message = judgeInfo.getMessage();
//        Long memory = judgeInfo.getMemory();
//        Long time = judgeInfo.getTime();
//        JudgeInfo judgeInfo1 = new JudgeInfo();
//        judgeInfo1.setMessage(message);
//        judgeInfo1.setMemory(memory);
//        judgeInfo1.setTime(time);
//        5）根据沙箱的执行结果，设置题目的判题状态和信息
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeInfo(judgeInfo);
        judgeContext.setInputList(inputList);
        judgeContext.setOutputList(outputList);
        judgeContext.setJudgeCaseList(judgeCases);
        judgeContext.setQuestion(question);
        judgeContext.setQuestionSubmit(questionSubmit);
        JudgeInfo judgeInfoResponse = judgeManager.doJudge(judgeContext);
        String responseJsonStr = JSONUtil.toJsonStr(judgeInfoResponse);
        // 6）修改数据库中的判题结果
        questionSubmit.setStatus(QuestionSubmitStatusEnum.PASS.getValue());
        questionSubmit.setJudgeInfo(responseJsonStr);
        update = questionFeignClient.updateQuestionSubmitById(questionSubmit);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
        Integer acceptedNum = question.getAcceptedNum();
        if (judgeInfoResponse.getMessage().equals(JudgeInfoMessageEnum.ACCEPTED.getText())){
            acceptedNum = acceptedNum+1;
        }
        Integer submitNum = question.getSubmitNum()+1;
        question.setAcceptedNum(acceptedNum);
        question.setSubmitNum(submitNum);
        update = questionFeignClient.updateQuestionById(question);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目通过率更新失败");
        }
//        List<String> outputList1 = judgeCases.stream().map(JudgeCase::getOutput).collect(Collectors.toList());
        return questionSubmit;
    }
}
