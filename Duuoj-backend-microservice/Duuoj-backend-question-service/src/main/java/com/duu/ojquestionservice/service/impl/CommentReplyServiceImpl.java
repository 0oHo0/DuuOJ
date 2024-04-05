package com.duu.ojquestionservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duu.ojmodel.model.entity.CommentReply;
import com.duu.ojquestionservice.service.CommentReplyService;
import com.duu.ojquestionservice.mapper.CommentReplyMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【comment_reply(评论回复表)】的数据库操作Service实现
* @createDate 2024-03-16 15:56:57
*/
@Service
public class CommentReplyServiceImpl extends ServiceImpl<CommentReplyMapper, CommentReply>
    implements CommentReplyService{

}




