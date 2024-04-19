package com.duu.ojquestionservice.job;

import cn.hutool.core.util.RandomUtil;
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
import java.util.concurrent.TimeUnit;

import static com.duu.ojcommon.constant.RedisKeyConstant.QUESTION_ID;

/**
 * @author : duu
 * @date : 2024/3/11
 * @from ：https://github.com/0oHo0
 **/
@Component
public class FullSyncQuestionToRedis {
    @Resource
    private QuestionService questionService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Scheduled(cron = "0 0 6 * * *")
    public void executeTask(){
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        List<Question> questionList = questionService.list(queryWrapper);
        //批量插入进redis
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        questionList.forEach(iter->{
            String key=QUESTION_ID+iter.getId();
            String jsonStr = JSONUtil.toJsonStr(iter);
            operations.set(key,jsonStr);
            stringRedisTemplate.expire(key,60*60*24+ RandomUtil.randomInt(-60*3, 60*3), TimeUnit.SECONDS);

        });
    }
}
