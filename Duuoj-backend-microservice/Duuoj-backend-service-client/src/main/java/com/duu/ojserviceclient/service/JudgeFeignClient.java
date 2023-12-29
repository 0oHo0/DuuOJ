package com.duu.ojserviceclient.service;

import com.duu.ojmodel.model.entity.QuestionSubmit;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "Duuoj-backend-judge-service", path = "/api/judge/inner",contextId = "DuuApi1")
public interface JudgeFeignClient {
    @PostMapping("/do")
    QuestionSubmit doJudge(@RequestParam(name = "questionSubmitId") long questionSubmitId);
}
