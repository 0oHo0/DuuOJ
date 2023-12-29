package com.duu.ojjudgeservice.codesandbox;

import com.duu.ojmodel.model.entity.ExecuteCodeRequest;
import com.duu.ojmodel.model.entity.ExecuteCodeResponse;

/**
 * @author : duu
 * @data : 2023/12/13
 * @from ：https://github.com/0oHo0
 **/
public interface CodeSandbox {

    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
