package com.duu.ojjudgeservice.strategy;


import com.duu.ojmodel.model.dto.question.JudgeCase;
import com.duu.ojmodel.model.entity.JudgeInfo;
import com.duu.ojmodel.model.entity.Question;
import com.duu.ojmodel.model.entity.QuestionSubmit;
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
