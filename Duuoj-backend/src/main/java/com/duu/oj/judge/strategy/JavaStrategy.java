package com.duu.oj.judge.strategy;

import cn.hutool.json.JSONUtil;
import com.duu.oj.model.dto.question.JudgeCase;
import com.duu.oj.model.dto.question.JudgeConfig;
import com.duu.oj.judge.codesandbox.model.JudgeInfo;
import com.duu.oj.model.entity.Question;
import com.duu.oj.model.enums.JudgeInfoMessageEnum;

import java.util.List;

/**
 * @author : duu
 * @data : 2023/12/13
 * @from ：https://github.com/0oHo0
 **/
public class JavaStrategy implements JudgeStrategy{
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        JudgeInfo judgeInfo = judgeContext.getJudgeInfo();
        Long memory = judgeInfo.getMemory();
        Long time = judgeInfo.getTime();
        JudgeInfo judgeInfoResponse = new JudgeInfo();
        judgeInfoResponse.setTime(time);
        judgeInfoResponse.setMemory(memory);
        JudgeInfoMessageEnum answer = JudgeInfoMessageEnum.ACCEPTED;
        List<String> inputList = judgeContext.getInputList();
        List<String> outputList = judgeContext.getOutputList();
        if (inputList.size()!=outputList.size()){
            answer = JudgeInfoMessageEnum.WRONG_ANSWER;
            judgeInfoResponse.setMessage(answer.getValue());
            return judgeInfoResponse;
        }
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();
        for (int i=0;i<judgeCaseList.size();i++){
            if (!judgeCaseList.get(i).getOutput().equals(outputList.get(i))) {
                answer = JudgeInfoMessageEnum.WRONG_ANSWER;
                judgeInfoResponse.setMessage(answer.getValue());
                return judgeInfoResponse;
            }
        }
        Question question = judgeContext.getQuestion();
        String judgeConfigStr = question.getJudgeConfig();
        JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class);
        Long timeLimit = judgeConfig.getTimeLimit();
        Long memoryLimit = judgeConfig.getMemoryLimit();
        long JAVA_PROGRAM_TIME_COST = 10000L;
        if (memoryLimit<memory){
            answer = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(answer.getValue());
            return judgeInfoResponse;
        } else if (timeLimit<time) {
            answer = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(answer.getValue());
            return judgeInfoResponse;
        }
        judgeInfoResponse.setMessage(answer.getValue());
        return judgeInfoResponse;
    }
}
