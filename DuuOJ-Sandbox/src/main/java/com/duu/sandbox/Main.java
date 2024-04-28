package com.duu.sandbox;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.duu.sandbox.model.ExecuteCodeRequest;
import com.duu.sandbox.utils.SignUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class Main {

    public static void main(String[] args) {
        String secretKey = "662cea6e0654d2be521d6a2fe80818ec";

        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        executeCodeRequest.setInputList(Arrays.asList("1 1", "1 2", "1 4"));
        executeCodeRequest.setCode("#include <iostream>\n" +
                "#include<string.h>\n" +
                "\n" +
                "using namespace std;\n" +
                "\n" +
                "int main(int argc, char* argv[])\n" +
                "{\n" +
                "    int a;\n" +
                "    int b;\n" +
                "    cin>>a;\n" +
                "    cin>>b;\n" +
                "    cout<<a+b<<endl;\n" +
                "    return 0;\n" +
                "}");
        executeCodeRequest.setLanguage("cpp");
        String jsonStr = JSONUtil.toJsonStr(executeCodeRequest);
        String sign1 = SignUtils.getSign(jsonStr, secretKey);
        System.out.println(sign1);

        String body = "{\"inputList\":[\"1 1\",\"1 2\",\"1 4\"],\"code\":\"#include <iostream>\\n#include<string.h>\\n\\nusing namespace std;\\n\\nint main(int argc, char* argv[])\\n{\\n    int a;\\n    int b;\\n    cin>>a;\\n    cin>>b;\\n    cout<<a b<<endl;\\n    return 0;\\n}\",\"language\":\"cpp\"}";
        System.out.println(StrUtil.equals(body, jsonStr));
        ExecuteCodeRequest bean = JSONUtil.toBean(body, ExecuteCodeRequest.class);
        String jsonStr1 = JSONUtil.toJsonStr(bean);
        System.out.println(StrUtil.equals(jsonStr1, jsonStr));
        String sign = SignUtils.getSign(jsonStr1, secretKey);
        System.out.println(sign);
    }
}