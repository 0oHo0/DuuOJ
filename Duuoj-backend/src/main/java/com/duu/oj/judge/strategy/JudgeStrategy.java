package com.duu.oj.judge.strategy;

import com.duu.oj.judge.codesandbox.model.JudgeInfo;

/**
 * @author : duu
 * @data : 2023/12/13
 * @from ：https://github.com/0oHo0
 **/
public interface JudgeStrategy {
    JudgeInfo doJudge(JudgeContext judgeContext);
}
