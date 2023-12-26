package com.duu.sandbox.controller;

import com.duu.sandbox.CodeSandbox;
import com.duu.sandbox.JavaDockerCodeSandbox;
import com.duu.sandbox.model.ExecuteCodeRequest;
import com.duu.sandbox.model.ExecuteCodeResponse;
import com.duu.sandbox.model.ExecuteMessage;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author : duu
 * @data : 2023/12/25
 * @from ：https://github.com/0oHo0
 **/
@RestController
@RequestMapping("/")
public class CodeController {

    @Resource
    private CodeSandbox JavaCodeSandboxTemplate;

    @PostMapping("/execute")
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest){

        //todo 还没加鉴权机制
        return JavaCodeSandboxTemplate.executeCode(executeCodeRequest);
    }
}
