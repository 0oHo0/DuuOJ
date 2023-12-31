package com.duu.oj.judge;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.duu.oj.common.ErrorCode;
import com.duu.oj.exception.BusinessException;
import com.duu.oj.judge.codesandbox.CodeSandbox;
import com.duu.oj.judge.codesandbox.CodeSandboxFactory;
import com.duu.oj.judge.codesandbox.CodeSandboxProxy;
import com.duu.oj.judge.codesandbox.model.ExecuteCodeRequest;
import com.duu.oj.judge.codesandbox.model.ExecuteCodeResponse;
import com.duu.oj.judge.codesandbox.model.JudgeInfo;
import com.duu.oj.judge.strategy.JudgeContext;
import com.duu.oj.judge.strategy.JudgeManager;
import com.duu.oj.model.dto.question.JudgeCase;
import com.duu.oj.model.entity.Question;
import com.duu.oj.model.entity.QuestionSubmit;
import com.duu.oj.model.enums.QuestionSubmitStatusEnum;
import com.duu.oj.service.QuestionService;
import com.duu.oj.service.QuestionSubmitService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : duu
 * @data : 2023/12/13
 * @from ：https://github.com/0oHo0
 **/
@Service
public class JudgeServiceImpl implements JudgeService{
    @Resource
    private QuestionService questionService;
    @Resource
    private QuestionSubmitService questionSubmitService;
    @Resource
    private JudgeManager judgeManager;
    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        if (questionSubmitId<=0)
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"提交ID错误");
//        1）传入题目的提交 id，获取到对应的题目、提交信息（包含代码、编程语言等）
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交信息不存在");
        }
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionService.getById(questionId);
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
        boolean update = questionSubmitService.updateById(questionSubmit);
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
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder().code(code)
                .language(language).inputList(inputList).build();

        Map<String, Object> executeCodeRequestMap = BeanUtil.beanToMap(executeCodeRequest);
        String post = HttpUtil.post("localhost:8103/api/execute", executeCodeRequestMap);
        ExecuteCodeResponse executeCodeResponse = JSONUtil.toBean(post, ExecuteCodeResponse.class);
        //ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        List<String> outputList = executeCodeResponse.getOutputList();
        JudgeInfo judgeInfo = executeCodeResponse.getJudgeInfo();
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
        questionSubmit.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        questionSubmit.setJudgeInfo(responseJsonStr);
        update = questionSubmitService.updateById(questionSubmit);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
//        List<String> outputList1 = judgeCases.stream().map(JudgeCase::getOutput).collect(Collectors.toList());

        return questionSubmit;
    }
}
