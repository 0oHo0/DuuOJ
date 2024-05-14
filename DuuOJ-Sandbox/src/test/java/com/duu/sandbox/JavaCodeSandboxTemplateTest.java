package com.duu.sandbox;

import com.duu.sandbox.model.ExecuteCodeRequest;
import com.duu.sandbox.model.ExecuteCodeResponse;
import com.duu.sandbox.template.CodeSandbox;
import com.duu.sandbox.template.java.JavaCodeSandboxTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Scanner;

/**
 * @author : duu
 * @data : 2023/12/16
 * @from ：https://github.com/0oHo0
 **/
@SpringBootTest
class JavaCodeSandboxTemplateTest {
    @Test
    public void main() {
        String code = "import java.util.*;\n" +
                "\n" +
                "public class Main{\n" +
                "    public static void main(String args[]){\n" +
                "       Scanner scanner = new Scanner(System.in);\n" +
                "       Integer a = scanner.nextInt();\n" +
                "       Integer b = scanner.nextInt();\n" +
                "       int sum = a+b;\n" +
                "       System.out.println(sum);\n" +
                "             \n" +
                "    }\n" +
                "}";
        CodeSandbox codeSandbox = new JavaCodeSandboxTemplate();
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        executeCodeRequest.setCode(code);
        executeCodeRequest.setInputList(Arrays.asList("1 2","3 4"));
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        System.out.println(executeCodeResponse);
    }
}