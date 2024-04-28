package com.duu.sandbox.sdk;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.duu.sandbox.sdk.model.CodeRequest;
import com.duu.sandbox.sdk.model.CodeResponse;
import com.duu.sandbox.sdk.utils.SignUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Duu
 * @description
 * @date 2024/04/27 15:52
 * @from https://github.com/0oHo0
 **/
@Slf4j
@Data
public class SandboxClient {

    private String accessKey;

    private String secretKey;

    private final String HOST = "http://localhost:8103/api/execute/sign";

    public SandboxClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public CodeResponse executeCode(CodeRequest codeRequest){
        if(codeRequest == null){
            throw new RuntimeException("请求参数为空");
        } else if (codeRequest.getInputList().isEmpty()) {
            throw new RuntimeException("请输入测试用例");
        }
        String jsonStr = JSONUtil.toJsonStr(codeRequest);
        try(HttpResponse httpResponse = HttpRequest.post(HOST).addHeaders(getHeaderMap(jsonStr)).body(jsonStr).execute();
        ) {
            if(httpResponse.getStatus() != 200){
                log.info("调用沙箱服务失败");
                return null;
            }
            return JSONUtil.toBean(httpResponse.body(), CodeResponse.class);
        } catch (Exception e) {
            log.info("请求沙箱失败：", e);
            return null;
        }

    }

    private Map<String,String> getHeaderMap(String body){
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("accessKey",accessKey);
        paramMap.put("body",body);
        paramMap.put("nonce", RandomUtil.randomNumbers(4));
        paramMap.put("timestamp",String.valueOf(System.currentTimeMillis()/1000));
        paramMap.put("sign", SignUtils.getSign(body,secretKey));
        return paramMap;
    }
}