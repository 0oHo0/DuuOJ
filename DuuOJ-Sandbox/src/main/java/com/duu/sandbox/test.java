package com.duu.sandbox;

import com.duu.sandbox.model.ExecuteCodeRequest;
import com.duu.sandbox.model.ExecuteCodeResponse;

import java.util.Arrays;

/**
 * @author : duu
 * @data : 2023/12/16
 * @from ：https://github.com/0oHo0
 **/
public class test {
    public static void main(String[] args) {
        String code = "public class Main {\n" +
                "\n" +
                "    public static void main(String[] args) {\n" +
                "        int a = Integer.parseInt(args[0]);\n" +
                "        int b = Integer.parseInt(args[1]);\n" +
                "        System.out.println(\"结果:\" + (a + b));\n" +
                "    }\n" +
                "}\n";
        CodeSandbox codeSandbox = new JavaCodeSandboxTemplate();
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        executeCodeRequest.setCode(code);
        executeCodeRequest.setInputList(Arrays.asList("1 2","3 4"));
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        System.out.println(executeCodeResponse);
    }
}
