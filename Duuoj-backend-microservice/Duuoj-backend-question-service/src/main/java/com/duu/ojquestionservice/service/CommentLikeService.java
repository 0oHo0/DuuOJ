package com.duu.ojquestionservice.service;

import com.duu.ojmodel.model.entity.CommentLike;
import com.baomidou.mybatisplus.extension.service.IService;
import com.duu.ojmodel.model.entity.User;

import java.util.List;

/**
* @author Administrator
* @description 针对表【comment_like(评论点赞表)】的数据库操作Service
* @createDate 2024-03-19 21:12:29
*/
public interface CommentLikeService extends IService<CommentLike> {

    Boolean commentLike(Long id, User loginUser);

    Boolean commentDislike(Long id, User loginUser);

    Long countCommentLike(Long id);

    Boolean checkLike(Long id,User loginUser);
}
