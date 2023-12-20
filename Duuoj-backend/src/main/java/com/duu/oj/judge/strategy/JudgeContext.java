package com.duu.oj.judge.strategy;

import com.duu.oj.model.dto.question.JudgeCase;
import com.duu.oj.judge.codesandbox.model.JudgeInfo;
import com.duu.oj.model.entity.Question;
import com.duu.oj.model.entity.QuestionSubmit;
import lombok.Data;

import java.util.List;

/**
 * @author : duu
 * @data : 2023/12/13
 * @from ：https://github.com/0oHo0
 **/
@Data
public class JudgeContext {

    private JudgeInfo judgeInfo;

    private List<String> inputList;

    private List<String> outputList;

    private List<JudgeCase> judgeCaseList;

    private Question question;

    private QuestionSubmit questionSubmit;

}
