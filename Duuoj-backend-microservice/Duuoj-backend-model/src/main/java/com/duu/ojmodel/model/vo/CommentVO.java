package com.duu.ojmodel.model.vo;

import com.duu.ojmodel.model.entity.Comment;
import com.duu.ojmodel.model.entity.CommentReply;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author : duu
 * @data : 2024/3/16
 * @from ：https://github.com/0oHo0
 **/
@Data
public class CommentVO implements Serializable {

    private Long id;

    /**
     * 评论的题目ID
     */
    private Long questionId;

    /**
     * 用户ID
     */
    private UserVO userVO;

    /**
     * 评论内容
     */
    private String content;

    private Integer likeNum;

    /**
     * 判断已登陆用户是否点赞了该评论
     */
    private Boolean likeFlag;
    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 回复
     */
    private List<CommentReplyVO> commentReplies;

    private static final long serialVersionUID = 1L;
}
