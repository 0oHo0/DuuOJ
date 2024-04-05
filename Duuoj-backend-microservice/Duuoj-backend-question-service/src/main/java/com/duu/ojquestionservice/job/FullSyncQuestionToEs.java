package com.duu.ojquestionservice.job;


import com.duu.ojmodel.model.dto.question.QuestionEsDTO;
import com.duu.ojmodel.model.entity.Question;
import com.duu.ojquestionservice.dao.QuestionEsDao;
import com.duu.ojquestionservice.service.QuestionService;
import javafx.collections.FXCollections;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.apache.commons.collections4.CollectionUtils;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : duu
 * @data : 2024/3/11
 * @from ：https://github.com/0oHo0
 **/
@Component
public class FullSyncQuestionToEs implements CommandLineRunner {
    @Resource
    private QuestionService questionService;
    @Resource
    private QuestionEsDao questionEsDao;

    @Override
    public void run(String... args){
        List<Question> questionList = questionService.list();
        if (CollectionUtils.isEmpty(questionList)) {
            return;
        }
        List<QuestionEsDTO> questionEsDTOList =
                questionList.stream().map(QuestionEsDTO::objToDto).collect(Collectors.toList());
        try {
            questionEsDao.saveAll(questionEsDTOList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
