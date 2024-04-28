package com.duu.sandbox.template.java;

import cn.hutool.core.util.StrUtil;

import com.duu.sandbox.model.ExecuteMessage;
import com.duu.sandbox.utils.ProcessUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.duu.sandbox.constant.SandBoxConstants.TIME_OUT;


/**
 * @description: Java本地ACM模式
 * @author: duu
 * @date: 2024/4/22 17:45
 **/
@Service
@Slf4j
public class JavaNativeAcmSandbox extends JavaCodeSandboxTemplate {
    @Value("${antares.sandbox.security-manager-path:/www/wwwroot/oj.zqk.asia-backend/security}")
    private String SECURITY_MANAGER_PATH;
    @Value("${antares.sandbox.security-manager-class-name:MySecurityManager}")
    private String SECURITY_MANAGER_CLASS_NAME;

    @Override
    protected List<ExecuteMessage> runFile(File userCodeFile, List<String> inputList){
    String userCodeParentFilePath = userCodeFile.getParentFile().getAbsolutePath();
        // 3. 执行代码，得到输出结果
        List<ExecuteMessage> executeMessageList = new ArrayList<>();
        for (String input : inputList) {
            //Linux下的命令
            //String runCmd = String.format("/software/jdk1.8.0_361/bin/java -Xmx256m -Dfile.encoding=UTF-8 -cp %s:%s -Djava.security.manager=%s Main", userCodeParentFilePath, SECURITY_MANAGER_PATH, SECURITY_MANAGER_CLASS_NAME);
            //Windows下的命令
//             String runCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s;%s -Djava.security.manager=%s Main", dir, SECURITY_MANAGER_PATH, SECURITY_MANAGER_CLASS_NAME);
            String runCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s Main", userCodeParentFilePath);
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            Process runProcess = null;
            try {
                runProcess = Runtime.getRuntime().exec(runCmd);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // 超时控制
            Process finalRunProcess = runProcess;
            Thread thread = new Thread(() -> {
                try {
                    Thread.sleep(TIME_OUT);
                    //超时了
                    finalRunProcess.destroy();
                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
                }
            });
            thread.start();

            ExecuteMessage executeMessage = null;
            try {
                executeMessage = ProcessUtils.getAcmProcessMessage(runProcess, input);
            } catch (IOException e){
                log.error("执行出错: {}", e.toString());
            }
            stopWatch.stop();
            if(!thread.isAlive()){
                executeMessage = new ExecuteMessage();
                executeMessage.setTime(stopWatch.getLastTaskTimeMillis());
                executeMessage.setErrorMessage("超出时间限制");
            }
            executeMessageList.add(executeMessage);

            //已经有用例失败了
            if(StrUtil.isNotBlank(executeMessage.getErrorMessage())){
                break;
            }
        }
        return executeMessageList;
    }
}