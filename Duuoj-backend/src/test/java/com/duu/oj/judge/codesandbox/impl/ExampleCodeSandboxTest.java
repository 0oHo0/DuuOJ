package com.duu.oj.judge.codesandbox.impl;

import com.duu.oj.judge.codesandbox.CodeSandbox;
import com.duu.oj.judge.codesandbox.model.ExecuteCodeRequest;
import com.duu.oj.judge.codesandbox.model.ExecuteCodeResponse;
import com.duu.oj.model.enums.QuestionSubmitLanguageEnum;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

/**
 * @author : duu
 * @data : 2023/12/13
 * @from ：https://github.com/0oHo0
 **/
@SpringBootTest
class ExampleCodeSandboxTest {

    @Test
    void executeCode() {
        String code = "int main(){}";
        String language = QuestionSubmitLanguageEnum.JAVA.getValue();
        List<String> inputList = Arrays.asList("1 2","3 4");
        ExecuteCodeRequest executeCodeRequest =
                ExecuteCodeRequest.builder().code(code).inputList(inputList).language(language).build();
        CodeSandbox codeSandBox = new ExampleCodeSandbox();
        ExecuteCodeResponse executeCodeResponse = codeSandBox.executeCode(executeCodeRequest);
        System.out.println(executeCodeResponse.toString());
    }
}