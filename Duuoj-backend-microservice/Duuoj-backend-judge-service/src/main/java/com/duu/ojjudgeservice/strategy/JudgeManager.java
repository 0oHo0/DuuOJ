package com.duu.ojjudgeservice.strategy;


import com.duu.ojmodel.model.entity.JudgeInfo;
import com.duu.ojmodel.model.enums.QuestionSubmitLanguageEnum;
import org.springframework.stereotype.Service;

/**
 * @author : duu
 * @date : 2023/12/13
 * @from ：https://github.com/0oHo0
 *    判题管理
 **/
@Service
public class JudgeManager {
    /**
     * @description: 执行判题
     * @author: duu
     * @date: 2023/12/13 21:23
     * @param: judgeContext
     * @return: JudgeInfo
     **/
    public JudgeInfo doJudge(JudgeContext judgeContext){
        String language = judgeContext.getQuestionSubmit().getLanguage();
        JudgeStrategy judgeStrategy = new DefaultStrategy();
        if (language.equals(QuestionSubmitLanguageEnum.JAVA.getValue())){
            judgeStrategy = new JavaStrategy();
        }else if (language.equals(QuestionSubmitLanguageEnum.CPLUSPLUS.getValue()))
        {
            judgeStrategy = new JavaStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }
}
