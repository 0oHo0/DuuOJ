package com.duu.ojmodel.model.dto.question;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : duu
 * @date : 2024/3/16
 * @from ：https://github.com/0oHo0
 **/
@Data
public class CommentReplyAddRequest implements Serializable {

    /**
     * 回复的评论Id
     */
    private Long commentId;


    /**
     * 回复内容
     */
    private String content;

    private static final long serialVersionUID = 1L;
}
