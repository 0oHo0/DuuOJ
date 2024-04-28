package com.duu.sandbox.utils;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;

/**
 * @author : duu
 * @data : 2023/11/22
 * @from ：https://github.com/0oHo0
 **/
public class SignUtils {
    public static String getSign(String body,String secretKey){
        Digester md5 = new Digester(DigestAlgorithm.SHA256);
        String content = body + '.'+secretKey;
        return  md5.digestHex(content);
    }
}
