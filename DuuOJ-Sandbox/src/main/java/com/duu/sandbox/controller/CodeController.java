package com.duu.sandbox.controller;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.duu.sandbox.template.CodeSandbox;
import com.duu.sandbox.enums.CodeLanguage;
import com.duu.sandbox.model.ExecuteCodeRequest;
import com.duu.sandbox.model.ExecuteCodeResponse;
import com.duu.sandbox.utils.SignUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

/**
 * @author : duu
 * @data : 2023/12/25
 * @from ：https://github.com/0oHo0
 **/
@Slf4j
@RestController
public class CodeController {

    @Resource
    private CodeSandbox javaNativeAcmSandbox;

    @Resource
    private CodeSandbox cppNativeAcmSandbox;
    @PostMapping("/execute/sign")
    public ExecuteCodeResponse executeCodeBySign(@RequestBody ExecuteCodeRequest executeCodeRequest, HttpServletRequest request){
        String accessKey = request.getHeader("accessKey");
        String body = request.getHeader("body");
        // 防止中文乱码
//        try {
//            body = URLDecoder.decode(Objects.requireNonNull(request.getHeader("body")), StandardCharsets.UTF_8.name());
//        } catch (UnsupportedEncodingException e) {
//            throw new RuntimeException(e);
//        }
        String sign = request.getHeader("sign");
        String nonce = request.getHeader("nonce");
        String timestamp = request.getHeader("timestamp");
        boolean hasBlank = StringUtils.isAnyBlank(accessKey, sign, nonce, timestamp);
         //判断是否有空
        if (hasBlank) {
            return null;
        }
        if (Long.parseLong(nonce) > 10000L) {
            return null;
        }
        // 时间戳是否为数字
        if (!NumberUtil.isNumber(timestamp)) {
            return null;
        }
        Long currentTime = System.currentTimeMillis() / 1000;
        Long requestTime = Long.parseLong(timestamp);
        if (currentTime - requestTime > 60 * 1L) {
            log.info("时间错误");
            return null;
        }
        HashMap<String, Object> paramMap = new HashMap<String, Object>() {{
            put("accessKey", accessKey);
        }};
        String secretKey = HttpUtil.get("http://localhost:8108/api/user/inner/get/secretKey", paramMap);
        Optional.ofNullable(secretKey).orElseThrow(() -> new RuntimeException("secretKey 错误"));

        String sign1 = SignUtils.getSign(body, secretKey);
        if (!StrUtil.equals(sign, sign1)) {
            log.info("签名错误");
            return null;
        }

        String language = executeCodeRequest.getLanguage();
        if(language.equals(CodeLanguage.JAVA.getName())){
            return javaNativeAcmSandbox.executeCode(executeCodeRequest);
        }else if(language.equals(CodeLanguage.CPP.getName())){
            return cppNativeAcmSandbox.executeCode(executeCodeRequest);
        }else {
            return null;
        }
    }

    @PostMapping("/execute")
    public ExecuteCodeResponse executeCode(@RequestBody ExecuteCodeRequest executeCodeRequest){
        String language = executeCodeRequest.getLanguage();
        if(language.equals(CodeLanguage.JAVA.getName())){
            return javaNativeAcmSandbox.executeCode(executeCodeRequest);
        }else if(language.equals(CodeLanguage.CPP.getName())){
            return cppNativeAcmSandbox.executeCode(executeCodeRequest);
        }else {
            return null;
        }
    }
}
