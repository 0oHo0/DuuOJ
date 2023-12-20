package com.duu.oj.judge.codesandbox;

import com.duu.oj.judge.codesandbox.impl.ExampleCodeSandbox;
import com.duu.oj.judge.codesandbox.impl.RemoteCodeSandbox;
import com.duu.oj.judge.codesandbox.impl.ThirdPartyCodeSandbox;

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
