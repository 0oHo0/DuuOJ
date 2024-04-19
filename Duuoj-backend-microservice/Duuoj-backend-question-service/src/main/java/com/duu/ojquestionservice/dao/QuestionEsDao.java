package com.duu.ojquestionservice.dao;

import com.duu.ojmodel.model.dto.question.QuestionEsDTO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * @author : duu
 * @date : 2024/4/5
 * @from ：https://github.com/0oHo0
 **/
public interface QuestionEsDao extends ElasticsearchRepository<QuestionEsDTO,String> {

}
