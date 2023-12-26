package com.duu.sandbox;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import com.duu.sandbox.model.ExecuteCodeRequest;
import com.duu.sandbox.model.ExecuteCodeResponse;
import com.duu.sandbox.model.ExecuteMessage;
import com.duu.sandbox.model.JudgeInfo;
import com.duu.sandbox.utils.ProcessUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : duu
 * @data : 2023/12/15
 * @from ：https://github.com/0oHo0
 **/
@Slf4j
@Component
public class JavaCodeSandboxTemplate implements CodeSandbox {

    private static final String SECURITY_MANAGER_PATH = "D:\\Data\\Code\\OJ\\DuuOJ-Sandbox\\src\\main\\resources" +
            "\\security";

    private static final String SECURITY_MANAGER_CLASS_NAME = "MySecurityManager";

    public File saveCodeToFile(String code) {
        String userDir = System.getProperty("user.dir");
        String globalCodePathName = userDir + File.separator + "src/main/resources/userCode";
        if (!FileUtil.exist(globalCodePathName)){
            FileUtil.mkdir(globalCodePathName);
        }
        String userCodeParentPath = globalCodePathName + File.separator + UUID.randomUUID();
        String userCodePath = userCodeParentPath + File.separator + "Main.java";
        File file = FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);
        return file;
    }


    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
//        1. 把用户的代码保存为文件
        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        File file = saveCodeToFile(code);
//        2. 编译代码，得到 class 文件
        ExecuteMessage executeMessage = compileFile(file);
//        3. 执行代码，得到输出结果
        List<ExecuteMessage> executeMessageList = runFile(file, inputList);
//        4. 收集整理输出结果
        ExecuteCodeResponse outputResponse = getOutputResponse(executeMessageList);
//        5. 文件清理，释放空间
        boolean del = FileUtil.del(file.getParentFile());
        if (!del) {
            log.error("deleteFile error, userCodeFilePath = {}", file.getParentFile().getAbsolutePath());
        }
//        6. 错误处理，提升程序健壮性
        return outputResponse;
    }

    public ExecuteMessage compileFile(File userCodeFile) {
        String compileCmd = String.format("javac -encoding utf-8 %s", userCodeFile.getAbsolutePath());
        try {
            Process process = Runtime.getRuntime().exec(compileCmd);
            ExecuteMessage executeMessage = ProcessUtils.runProcessAndGetMessage(process, "编译");
            return executeMessage;
        } catch (IOException e) {
            throw new RuntimeException("编译出错");
        }
    }
    public List<ExecuteMessage> runFile(File userCodeFile, List<String> inputList){
        String userCodeParentFilePath = userCodeFile.getParentFile().getAbsolutePath();
        ArrayList<ExecuteMessage> executeMessageArrayList = new ArrayList<>();
        for (String input : inputList) {
            //String runCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s;%s -Djava.security.manager=%s Main %s", userCodeParentFilePath, SECURITY_MANAGER_PATH, SECURITY_MANAGER_CLASS_NAME, input);
            String runCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s Main %s", userCodeParentFilePath, input);
            try {
                Process process = Runtime.getRuntime().exec(runCmd);
                ExecuteMessage executeMessage = ProcessUtils.runProcessAndGetMessage(process, "执行");
                executeMessageArrayList.add(executeMessage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return executeMessageArrayList;
    }

    public ExecuteCodeResponse getOutputResponse(List<ExecuteMessage> executeMessageList) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        List<String> outputList = new ArrayList<>();
        long maxTime = 0;
        for (ExecuteMessage executeMessage : executeMessageList) {
            Integer exitValue = executeMessage.getExitValue();
            String errorMessage = executeMessage.getErrorMessage();
            if(StringUtils.isNotBlank(errorMessage)){
                executeCodeResponse.setMessage(errorMessage);
                // 出错
                executeCodeResponse.setStatus(3);
                break;
            }
            Long time = executeMessage.getTime();
            if (time != null) {
                maxTime = Math.max(time, maxTime);
            }
            String message = executeMessage.getMessage();
            outputList.add(message);
        }
        // 正常提交
        if (outputList.size() == executeMessageList.size()) {
            executeCodeResponse.setStatus(1);
        }
        executeCodeResponse.setOutputList(outputList);
        executeCodeResponse.setMessage("执行通过");
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage("执行通过");
        judgeInfo.setMemory(50L);
        judgeInfo.setTime(maxTime);
        executeCodeResponse.setJudgeInfo(judgeInfo);
        return executeCodeResponse;
    }
}
