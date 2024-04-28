package com.duu.ojcommon.Aspect;

import cn.hutool.core.net.NetUtil;
import com.duu.ojcommon.annotation.RequestLimit;
import com.duu.ojcommon.common.ErrorCode;
import com.duu.ojcommon.constant.RedisKeyConstant;
import com.duu.ojcommon.exception.BusinessException;
import com.duu.ojcommon.utils.NetUtils;
import com.duu.ojcommon.utils.TimeUtils;
import lombok.extern.log4j.Log4j;
import org.apache.catalina.util.RequestUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author : duu
 * @date : 2024/3/7
 * @from ：https://github.com/0oHo0
 **/
@Aspect
@Component
public class RequestLimitAspect {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RedissonClient redissonClient;

    @Pointcut("@annotation(requestLimit)")
    public void controllerAspect(RequestLimit requestLimit) {
    }

    @Around(value = "controllerAspect(requestLimit)", argNames = "joinPoint,requestLimit")
    public Object doAround(ProceedingJoinPoint joinPoint, RequestLimit requestLimit) throws Throwable {
        long countLimit = requestLimit.count();
        long period = requestLimit.period();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getMethod().getName();
        HttpServletRequest request =
                ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String ipAddress = NetUtils.getIpAddress(request);
        String key = RedisKeyConstant.REQUEST_LIMIT + methodName + ":" + ipAddress;
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        rateLimiter.expire(Instant.now().plus(1, ChronoUnit.HOURS));
        rateLimiter.trySetRate(
                RateType.OVERALL,
                countLimit,
                period,
                RateIntervalUnit.SECONDS
        );
        if (!rateLimiter.tryAcquire(1)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "操作过于频繁");
        }
        //滑动窗口实现
//        long currentTimeMillis = System.currentTimeMillis();
//        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
//        zSetOperations.add(key, currentTimeMillis, currentTimeMillis);
//        redisTemplate.expire(key,period, TimeUnit.SECONDS);
//
//        zSetOperations.removeRangeByScore(key,0,currentTimeMillis-period*1000);
//
//        Long zSetCount = zSetOperations.zCard(key);
//        if (zSetCount>countLimit){
//            throw new BusinessException(ErrorCode.OPERATION_ERROR,"操作过于频繁");
//        }
        return joinPoint.proceed();
    }
}
