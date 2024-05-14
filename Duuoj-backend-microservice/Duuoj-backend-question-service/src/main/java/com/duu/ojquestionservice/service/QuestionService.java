package com.duu.ojquestionservice.service;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.duu.ojmodel.model.dto.question.QuestionQueryRequest;
import com.duu.ojmodel.model.dto.question.QuestionStatisticsResponse;
import com.duu.ojmodel.model.dto.question.SearchQueryRequest;
import com.duu.ojmodel.model.entity.Question;
import com.duu.ojmodel.model.entity.User;
import com.duu.ojmodel.model.vo.QuestionVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

/**
* @author duu
* @description 针对表【question(题目)】的数据库操作Service
* @createDate 2023-08-07 20:58:00
*/
public interface QuestionService extends IService<Question> {


    /**
     * 校验
     *
     * @param question
     * @param add
     */
    void validQuestion(Question question, boolean add);

    /**
     * 获取查询条件
     *
     * @param questionQueryRequest
     * @return
     */
    QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest);
    
    /**
     * 获取题目封装
     *
     * @param question
     * @param request
     * @return
     */
    QuestionVO getQuestionVO(Question question, HttpServletRequest request);

    /**
     * 分页获取题目封装
     *
     * @param questionPage
     * @param request
     * @return
     */
    Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request);

    Optional<Question> getQuestionByRedis(Long questionId);

    Page<QuestionVO> searchQuestionByEs(SearchQueryRequest searchQueryRequest);

    QuestionStatisticsResponse questionStatistics(Long id);
}
