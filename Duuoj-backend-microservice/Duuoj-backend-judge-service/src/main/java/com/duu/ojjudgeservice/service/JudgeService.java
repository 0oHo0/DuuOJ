package com.duu.ojjudgeservice.service;


import com.duu.ojmodel.model.entity.QuestionSubmit;

/**
 * @author : duu
 * @date : 2023/12/13
 * @from ：https://github.com/0oHo0
 **/
public interface JudgeService {
    QuestionSubmit doJudge(long questionSubmitId);
}
