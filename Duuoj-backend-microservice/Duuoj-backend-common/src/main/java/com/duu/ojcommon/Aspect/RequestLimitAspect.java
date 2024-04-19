package com.duu.ojcommon.Aspect;

import cn.hutool.core.net.NetUtil;
import com.duu.ojcommon.annotation.RequestLimit;
import com.duu.ojcommon.common.ErrorCode;
import com.duu.ojcommon.exception.BusinessException;
import com.duu.ojcommon.utils.NetUtils;
import lombok.extern.log4j.Log4j;
import org.apache.catalina.util.RequestUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
    @Pointcut("@annotation(requestLimit)")
    public void controllerAspect(RequestLimit requestLimit){}
    @Around(value = "controllerAspect(requestLimit)", argNames = "joinPoint,requestLimit")
    public Object doAround(ProceedingJoinPoint joinPoint, RequestLimit requestLimit) throws Throwable {
        long countLimit = requestLimit.count();
        long period = requestLimit.period();

        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String ipAddress = NetUtils.getIpAddress(request);
        String requestURL = RequestUtil.getRequestURL(request).toString();
        String key = "req_lim".concat(requestURL).concat(ipAddress);
        long currentTimeMillis = System.currentTimeMillis();
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        zSetOperations.add(key, currentTimeMillis, currentTimeMillis);
        redisTemplate.expire(key,period, TimeUnit.SECONDS);

        zSetOperations.removeRangeByScore(key,0,currentTimeMillis-period*1000);

        Long zSetCount = zSetOperations.zCard(key);
        if (zSetCount>countLimit){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"操作过于频繁");
        }
        return joinPoint.proceed();
    }
}
