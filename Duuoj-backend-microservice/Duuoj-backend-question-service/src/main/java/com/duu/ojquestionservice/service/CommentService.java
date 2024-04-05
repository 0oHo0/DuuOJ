package com.duu.ojquestionservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.duu.ojmodel.model.dto.question.CommentQueryRequest;
import com.duu.ojmodel.model.dto.question.QuestionQueryRequest;
import com.duu.ojmodel.model.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.duu.ojmodel.model.entity.User;
import com.duu.ojmodel.model.vo.CommentVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import javax.servlet.http.HttpServletRequest;

/**
* @author Administrator
* @description 针对表【comment】的数据库操作Service
* @createDate 2024-03-16 15:56:57
*/
public interface CommentService extends IService<Comment> {

    QueryWrapper<Comment> getQueryWrapper(CommentQueryRequest commentQueryRequest);

    Page<CommentVO> getCommentVOPage(Page<Comment> commentPage, User loginUser);
}
