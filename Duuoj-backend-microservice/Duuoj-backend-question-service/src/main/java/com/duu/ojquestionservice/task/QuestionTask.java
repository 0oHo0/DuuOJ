package com.duu.ojquestionservice.task;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.duu.ojmodel.model.entity.Question;
import com.duu.ojquestionservice.service.QuestionService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author : duu
 * @data : 2024/3/11
 * @from ：https://github.com/0oHo0
 **/
@Component
public class QuestionTask {
    @Resource
    private QuestionService questionService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Scheduled(cron = "0 0 6 * * *")
    public void executeTask(){
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        List<Question> questionList = questionService.list(queryWrapper);
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        questionList.forEach(iter->{
            String key="questionId:"+iter.getId();
            String jsonStr = JSONUtil.toJsonStr(iter);
            operations.set(key,jsonStr);
        });
    }
}
