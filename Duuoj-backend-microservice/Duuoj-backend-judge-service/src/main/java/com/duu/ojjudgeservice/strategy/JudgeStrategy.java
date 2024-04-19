package com.duu.ojjudgeservice.strategy;


import com.duu.ojmodel.model.entity.JudgeInfo;

/**
 * @author : duu
 * @date : 2023/12/13
 * @from ：https://github.com/0oHo0
 **/
public interface JudgeStrategy {
    JudgeInfo doJudge(JudgeContext judgeContext);
}
