package com.duu.ojmodel.model.dto.question;

import com.duu.ojcommon.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class SearchQueryRequest extends PageRequest implements Serializable {

    /**
     * 搜索词
     */
    private String searchText;
    /**
     * 搜索类型
     */
    private String type;

    private static final long serialVersionUID = 1L;
}