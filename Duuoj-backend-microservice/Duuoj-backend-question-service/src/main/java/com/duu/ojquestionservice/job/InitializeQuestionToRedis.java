package com.duu.ojquestionservice.job;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.duu.ojcommon.common.ErrorCode;
import com.duu.ojcommon.exception.BusinessException;
import com.duu.ojcommon.exception.ThrowUtils;
import com.duu.ojmodel.model.entity.Question;
import com.duu.ojquestionservice.service.QuestionService;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.duu.ojcommon.constant.RedisKeyConstant.GET_QUESTION_FILTER;
import static com.duu.ojcommon.constant.RedisKeyConstant.QUESTION_ID;

/**
 * @author Duu
 * @description 初始化问题到redis 预热题目+Bloom过滤器
 * @date 2024/04/22 20:52
 * @from https://github.com/0oHo0
 **/
@Component
public class InitializeQuestionToRedis implements CommandLineRunner {
    @Resource
    private QuestionService questionService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RedissonClient redissonClient;

    @Override
    public void run(String... args) {
        //初始化布隆过滤器
        RBloomFilter<Object> filter = redissonClient.getBloomFilter(GET_QUESTION_FILTER);
        if (!filter.isExists()){
            boolean init = filter.tryInit(1000L, 0.03);
            ThrowUtils.throwIf(!init, new BusinessException(ErrorCode.SYSTEM_ERROR, "布隆过滤器初始化失败"));
        }
        List<Question> questionList = questionService.list();
        Optional.ofNullable(questionList).orElseThrow(() -> new BusinessException(ErrorCode.SYSTEM_ERROR, "初始化查询问题为空"));
        //批量插入进redis
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        questionList.forEach(iter -> {
            String key = QUESTION_ID + iter.getId();
            String jsonStr = JSONUtil.toJsonStr(iter);
            operations.set(key, jsonStr, 60 * 60 * 24 + RandomUtil.randomInt(-60 * 3, 60 * 3), TimeUnit.SECONDS);
            filter.add(iter.getId());
        });
    }
}