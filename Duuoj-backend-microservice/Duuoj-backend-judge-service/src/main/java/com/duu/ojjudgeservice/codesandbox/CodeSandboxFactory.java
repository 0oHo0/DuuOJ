package com.duu.ojjudgeservice.codesandbox;


import com.duu.ojjudgeservice.codesandbox.impl.ExampleCodeSandbox;
import com.duu.ojjudgeservice.codesandbox.impl.RemoteCodeSandbox;
import com.duu.ojjudgeservice.codesandbox.impl.ThirdPartyCodeSandbox;

/**
 * @author : duu
 * @data : 2023/12/13
 * @from ：https://github.com/0oHo0
 **/
public class CodeSandboxFactory {
    public static CodeSandbox newInstance(String type){
        switch (type){
            case "remote":
                return new RemoteCodeSandbox();
            case "thirdParty":
                return new ThirdPartyCodeSandbox();
            case "example":
            default:
                return new ExampleCodeSandbox();
        }
    }
}
