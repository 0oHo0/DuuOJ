package com.duu.sandbox.template.java;

import cn.hutool.core.util.StrUtil;
import com.duu.sandbox.model.ExecuteMessage;
import com.duu.sandbox.utils.ProcessUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.duu.sandbox.constant.SandBoxConstants.TIME_OUT;


/**
 * @description: Java本地ACM模式
 * @author: duu
 * @date: 2024/4/22 17:45
 **/
@Service
@Slf4j
public class JavaNativeMultiAcmSandbox extends JavaCodeSandboxTemplate {
    @Override
    protected List<ExecuteMessage> runFile(File userCodeFile, List<String> inputList) {
        String userCodeParentFilePath = userCodeFile.getParentFile().getAbsolutePath();
        // 3. 执行代码，得到输出结果
        ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 6, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(150));
        List<CompletableFuture<ExecuteMessage>> futures = inputList.stream()
                .map(input -> CompletableFuture.supplyAsync(() -> run(input, userCodeParentFilePath), executor)
                        .exceptionally(ex -> {
                            ExecuteMessage message = new ExecuteMessage();
                            message.setErrorMessage("超出时间限制或其他错误: " + ex.getMessage());
                            return message;
                        }))
                .collect(Collectors.toList());

        List<ExecuteMessage> messageList;
        messageList = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenApply(
                v ->
                        futures.stream().map(CompletableFuture::join).collect(Collectors.toList())

        ).join();

//        for (CompletableFuture<ExecuteMessage> future : futures) {
//            ExecuteMessage executeMessage = future.join();
//            executeMessageList.add(executeMessage);
//            if(StrUtil.isNotBlank(executeMessage.getErrorMessage())) {
//                break;
//            }
//            executor.shutdown();
//        }
        return messageList;
    }

    private static ExecuteMessage run(String input, String userCodeParentFilePath) {
        //Linux下的命令
        //String runCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s:%s -Djava.security.manager=%s
        // Main", userCodeParentFilePath, SECURITY_MANAGER_PATH, SECURITY_MANAGER_CLASS_NAME);
        //Windows下的命令
        String runCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s;%s -Djava.security.manager=%s Main"
                , userCodeParentFilePath, SECURITY_MANAGER_PATH, SECURITY_MANAGER_CLASS_NAME);
        //String runCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s Main", userCodeParentFilePath);
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
        } catch (IOException e) {
            log.error("执行出错: {}", e.toString());
        }
        stopWatch.stop();
        if (!thread.isAlive()) {
            executeMessage = new ExecuteMessage();
            executeMessage.setTime(stopWatch.getLastTaskTimeMillis());
            executeMessage.setErrorMessage("超出时间限制");
        }
        return executeMessage;
    }
}