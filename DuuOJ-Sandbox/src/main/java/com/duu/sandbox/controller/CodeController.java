package com.duu.sandbox.controller;

import com.duu.sandbox.CodeSandbox;
import com.duu.sandbox.CppCodeSandboxTemplate;
import com.duu.sandbox.JavaDockerCodeSandbox;
import com.duu.sandbox.enums.CodeLanguage;
import com.duu.sandbox.model.ExecuteCodeRequest;
import com.duu.sandbox.model.ExecuteCodeResponse;
import com.duu.sandbox.model.ExecuteMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author : duu
 * @data : 2023/12/25
 * @from ：https://github.com/0oHo0
 **/
@RestController
public class CodeController {

    @Resource
    private CodeSandbox javaCodeSandboxTemplate;

    @Resource
    private CodeSandbox cppCodeSandboxTemplate;
    @PostMapping("/execute")
    public ExecuteCodeResponse executeCode(@RequestBody ExecuteCodeRequest executeCodeRequest){
        //todo 还没加鉴权机制
        String language = executeCodeRequest.getLanguage();
        if(language.equals(CodeLanguage.JAVA.getName())){
            return javaCodeSandboxTemplate.executeCode(executeCodeRequest);
        }else{
            return cppCodeSandboxTemplate.executeCode(executeCodeRequest);
        }
    }
}
