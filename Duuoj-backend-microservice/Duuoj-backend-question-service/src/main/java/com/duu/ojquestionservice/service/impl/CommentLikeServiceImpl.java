package com.duu.ojquestionservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duu.ojcommon.common.ErrorCode;
import com.duu.ojcommon.constant.RedisKeyConstant;
import com.duu.ojcommon.exception.BusinessException;
import com.duu.ojmodel.model.entity.CommentLike;
import com.duu.ojmodel.model.entity.User;
import com.duu.ojquestionservice.service.CommentLikeService;
import com.duu.ojquestionservice.mapper.CommentLikeMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
* @author Administrator
* @description 针对表【comment_like(评论点赞表)】的数据库操作Service实现
* @createDate 2024-03-19 21:12:29
*/
@Service
public class CommentLikeServiceImpl extends ServiceImpl<CommentLikeMapper, CommentLike>
    implements CommentLikeService{
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Override
    public Boolean commentLike(Long id, User loginUser) {
        String key = RedisKeyConstant.COMMENT_LIKE+id;
        Long add = stringRedisTemplate.opsForSet().add(key, String.valueOf(loginUser.getId()));
        stringRedisTemplate.expire(key,3, TimeUnit.MINUTES);
        if (add == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"点赞失败");
        } else if (add > 0) {
            return true;
        }else {
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"不能重复点赞");
        }
    }

    @Override
    public Boolean commentDislike(Long id, User loginUser) {
        String key = RedisKeyConstant.COMMENT_LIKE+id;
        Long remove = stringRedisTemplate.opsForSet().remove(key, String.valueOf(loginUser.getId()));
        if (remove == null || remove<=0) {
            CommentLike commentLike = this.lambdaQuery().eq(CommentLike::getCommentId, id)
                    .eq(CommentLike::getUserLikeId, loginUser.getId())
                    .one();
            if (commentLike == null) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"取消点赞失败");
            }
            this.removeById(commentLike);
        }
        return true;
    }

    @Override
    public Long countCommentLike(Long id) {
        String key = RedisKeyConstant.COMMENT_LIKE+id;
        Long redisNum = Optional.ofNullable(stringRedisTemplate.opsForSet().size(key)).orElse(0L);
        Long sqlNum = this.lambdaQuery().eq(CommentLike::getCommentId, id).count();
        return redisNum+sqlNum;
    }

    @Override
    public Boolean checkLike(Long id, User loginUser) {
        String key = RedisKeyConstant.COMMENT_LIKE+id;
        Boolean member = stringRedisTemplate.opsForSet().isMember(key, String.valueOf(loginUser.getId()));
        if (Boolean.FALSE.equals(member)) {
            CommentLike commentLike = this.lambdaQuery().eq(CommentLike::getCommentId, id)
                    .eq(CommentLike::getUserLikeId, loginUser.getId())
                    .one();
            return commentLike != null;
        }
        return true;
    }
}




