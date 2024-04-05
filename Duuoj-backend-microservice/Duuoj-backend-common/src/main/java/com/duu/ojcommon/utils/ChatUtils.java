package com.duu.ojcommon.utils;

import com.yupi.yucongming.dev.YuCongMingClientConfig;
import com.yupi.yucongming.dev.client.YuCongMingClient;
import com.yupi.yucongming.dev.common.BaseResponse;
import com.yupi.yucongming.dev.model.DevChatRequest;
import com.yupi.yucongming.dev.model.DevChatResponse;

/**
 * @author : duu
 * @data : 2024/3/9
 * @from ：https://github.com/0oHo0
 **/
@Deprecated
public class ChatUtils {
    public static void main(String[] args) {
        String accessKey = "";
        String secretKey = "";
        YuCongMingClient client = new YuCongMingClient(accessKey, secretKey);
        DevChatRequest devChatRequest = new DevChatRequest();
        devChatRequest.setModelId(1651468516836098050L);
        devChatRequest.setMessage("你好");
        BaseResponse<DevChatResponse> response = client.doChat(devChatRequest);
        System.out.println(response.getData());
    }


}
