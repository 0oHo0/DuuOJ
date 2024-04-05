package com.duu.ojquestionservice.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duu.ojcommon.constant.CommonConstant;
import com.duu.ojcommon.utils.SqlUtils;
import com.duu.ojmodel.model.dto.question.CommentQueryRequest;
import com.duu.ojmodel.model.entity.Comment;
import com.duu.ojmodel.model.entity.CommentReply;
import com.duu.ojmodel.model.entity.User;
import com.duu.ojmodel.model.vo.CommentReplyVO;
import com.duu.ojmodel.model.vo.CommentVO;
import com.duu.ojmodel.model.vo.UserVO;
import com.duu.ojquestionservice.service.CommentLikeService;
import com.duu.ojquestionservice.service.CommentReplyService;
import com.duu.ojquestionservice.service.CommentService;
import com.duu.ojquestionservice.mapper.CommentMapper;
import com.duu.ojserviceclient.service.UserFeignClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Administrator
 * @description 针对表【comment】的数据库操作Service实现
 * @createDate 2024-03-16 15:56:57
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
        implements CommentService {

    @Resource
    CommentReplyService commentReplyService;
    @Resource
    UserFeignClient userFeignClient;

    @Resource
    CommentLikeService commentLikeService;
    @Override
    public QueryWrapper<Comment> getQueryWrapper(CommentQueryRequest commentQueryRequest) {
        Long questionId = commentQueryRequest.getQuestionId();
        String sortField = commentQueryRequest.getSortField();
        String sortOrder = commentQueryRequest.getSortOrder();
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjectUtil.isNotEmpty(questionId),"questionId", questionId);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),sortField);
        return queryWrapper;
    }

    @Override
    public Page<CommentVO> getCommentVOPage(Page<Comment> commentPage,User loginUser) {
        Page<CommentVO> commentVOPage = new Page<>();
        BeanUtil.copyProperties(commentPage, commentVOPage, "records");
        ArrayList<CommentVO> commentVOS = new ArrayList<>();
        List<Comment> records = commentPage.getRecords();
        records.forEach(item -> {
            CommentVO commentVO = new CommentVO();
            BeanUtil.copyProperties(item, commentVO, "userId","likeNum");
            User user = userFeignClient.getUserById(item.getUserId());
            UserVO userVO = userFeignClient.getUserVO(user);
            commentVO.setLikeNum(commentLikeService.countCommentLike(item.getId()).intValue());
            commentVO.setLikeFlag(commentLikeService.checkLike(item.getId(),loginUser));
            commentVO.setUserVO(userVO);
            QueryWrapper<CommentReply> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("commentId", item.getId());
            List<CommentReply> commentReplies = commentReplyService.list(queryWrapper);
            ArrayList<CommentReplyVO> commentReplyVOS = new ArrayList<>();
            commentReplies.forEach(commentReply ->{
                Long id = commentReply.getId();
                Long commentId = commentReply.getCommentId();
                Long userId = commentReply.getUserId();
                String content = commentReply.getContent();
                Date createTime = commentReply.getCreateTime();
                CommentReplyVO commentReplyVO = new CommentReplyVO();
                commentReplyVO.setId(id);
                commentReplyVO.setCommentId(commentId);
                User user1 = userFeignClient.getUserById(userId);
                UserVO userVO1 = userFeignClient.getUserVO(user1);
                commentReplyVO.setUserVO(userVO1);
                commentReplyVO.setContent(content);
                commentReplyVO.setCreateTime(createTime);
                commentReplyVOS.add(commentReplyVO);
            });
            commentVO.setCommentReplies(commentReplyVOS);
            commentVOS.add(commentVO);
        });
        commentVOPage.setRecords(commentVOS);
        return commentVOPage;
    }
}




