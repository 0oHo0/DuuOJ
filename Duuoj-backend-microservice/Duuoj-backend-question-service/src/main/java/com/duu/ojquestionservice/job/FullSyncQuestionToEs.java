package com.duu.ojquestionservice.job;


import com.duu.ojmodel.model.dto.question.QuestionEsDTO;
import com.duu.ojmodel.model.entity.Question;
import com.duu.ojquestionservice.dao.QuestionEsDao;
import com.duu.ojquestionservice.service.QuestionService;
import javafx.collections.FXCollections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.apache.commons.collections4.CollectionUtils;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : duu
 * @date : 2024/3/11
 * @from ：https://github.com/0oHo0
 **/
@Component
@Slf4j
public class FullSyncQuestionToEs implements CommandLineRunner {
    @Resource
    private QuestionService questionService;
    @Resource
    private QuestionEsDao questionEsDao;

    @Override
    public void run(String... args){
        List<Question> questionList = questionService.list();
        if (questionList == null || CollectionUtils.isEmpty(questionList)) {
            return;
        }
        List<QuestionEsDTO> questionEsDTOList =
                questionList.stream().map(QuestionEsDTO::objToDto).collect(Collectors.toList());
        try {
            questionEsDao.saveAll(questionEsDTOList);
        } catch (Exception e) {
            log.error("Es同步失败");
            e.printStackTrace();
        }
    }
}
