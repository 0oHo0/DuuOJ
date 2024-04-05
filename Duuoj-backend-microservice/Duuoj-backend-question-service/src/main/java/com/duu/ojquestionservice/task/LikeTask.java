package com.duu.ojquestionservice.task;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.duu.ojmodel.model.entity.CommentLike;
import com.duu.ojmodel.model.entity.Question;
import com.duu.ojquestionservice.service.CommentLikeService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author : duu
 * @data : 2024/3/20
 * @from ：https://github.com/0oHo0
 **/
@Component
public class LikeTask {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private CommentLikeService commentLikeService;
    @Async
    @Scheduled(fixedDelay = 120000)
    public void executeTask(){
        Set<String> keys = stringRedisTemplate.keys("CommentLike:*");
        keys.forEach(key->{
            //缓存持久化
            Set<String> members = stringRedisTemplate.opsForSet().members(key);
            String[] split = key.split(":");
            long commentId = Long.parseLong(split[1]);
            List<CommentLike> commentLikes = new ArrayList<>();
            members.forEach(member->{
                CommentLike commentLike = new CommentLike();
                commentLike.setUserLikeId(Long.parseLong(member));
                commentLike.setCommentId(commentId);
                commentLikes.add(commentLike);
            });
            commentLikeService.saveBatch(commentLikes);
        });
        //清理缓存
        stringRedisTemplate.delete(keys);
    }
}
