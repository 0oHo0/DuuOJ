package com.duu.ojcommon.utils;

import cn.hutool.crypto.KeyUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.signers.AlgorithmUtil;
import cn.hutool.jwt.signers.JWTSignerUtil;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : duu
 * @data : 2024/3/8
 * @from ：https://github.com/0oHo0
 **/
public class JwtUtils {
    //签名密钥
    private static final String SECRET = "!DAR$";

    /**
     *
     ⽣成token
     * @param  uuId 作为payload
     * @return token字符串
     */
    public static String getToken(String uuId) throws UnsupportedEncodingException {
        Map<String, Object> payload =  new HashMap<>();
        payload.put("uuId",uuId);
        byte[] key = SECRET.getBytes();
        String token = JWT.create().setPayload("uuId", uuId).setKey(key).sign();
        return token;
    }


    /**
     *
     解析token
     * @param token token字符串
     * @return 解析后的token
     */
    public static JWT decode(String token) throws UnsupportedEncodingException {
        return JWT.of(token);
    }
}
