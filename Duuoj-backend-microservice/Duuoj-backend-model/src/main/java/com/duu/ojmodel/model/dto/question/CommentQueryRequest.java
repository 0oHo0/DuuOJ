package com.duu.ojmodel.model.dto.question;

import com.duu.ojcommon.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author : duu
 * @date : 2024/3/16
 * @from ：https://github.com/0oHo0
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class CommentQueryRequest extends PageRequest implements Serializable{

    private Long questionId;

    private static final long serialVersionUID = 1L;
}
