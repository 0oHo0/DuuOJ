package com.duu.ojgateway.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.duu.ojcommon.utils.JwtUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class GlobalAuthFilter implements GlobalFilter, Ordered {

    private AntPathMatcher antPathMatcher = new AntPathMatcher();
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        String path = serverHttpRequest.getURI().getPath();
        ServerHttpResponse response = exchange.getResponse();
        // 判断路径中是否包含 inner，只允许内部调用
        if (antPathMatcher.match("/**/inner/**", path)) {
            response.setStatusCode(HttpStatus.FORBIDDEN);
            DataBufferFactory dataBufferFactory = response.bufferFactory();
            DataBuffer dataBuffer = dataBufferFactory.wrap("无权限".getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(dataBuffer));
        }
        //统一权限校验，通过 JWT 获取登录用户信息
        if (antPathMatcher.match("/**/login", path)) {
            return chain.filter(exchange);
        }
//        String token = serverHttpRequest.getHeaders().get("Authorization").get(0);
//        String authorization;
//        try {
//            authorization = JwtUtils.decode(token).getPayload("uuId").toString();
//        } catch (UnsupportedEncodingException e) {
//            throw new RuntimeException(e);
//        }
//        Boolean hasKey = false;
//        if (authorization != null) {
//            hasKey = stringRedisTemplate.hasKey(authorization);
//        }
//        if (Boolean.FALSE.equals(hasKey)) {
//            response.setStatusCode(HttpStatus.FORBIDDEN);
//            DataBufferFactory dataBufferFactory = response.bufferFactory();
//            DataBuffer dataBuffer = dataBufferFactory.wrap("无权限".getBytes(StandardCharsets.UTF_8));
//            return response.writeWith(Mono.just(dataBuffer));
//        }
        return chain.filter(exchange);
    }
    /**
     * 优先级提到最高
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
