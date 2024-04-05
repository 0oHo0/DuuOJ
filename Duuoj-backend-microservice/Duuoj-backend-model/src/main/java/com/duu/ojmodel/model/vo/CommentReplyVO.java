package com.duu.ojmodel.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author : duu
 * @data : 2024/3/19
 * @from ：https://github.com/0oHo0
 **/
@Data
public class CommentReplyVO implements Serializable {

    private Long id;

    /**
     * 回复的评论Id
     */
    private Long commentId;

    /**
     * 用户Id
     */
    private UserVO userVO;

    /**
     * 回复内容
     */
    private String content;

    /**
     * 回复时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;
}
