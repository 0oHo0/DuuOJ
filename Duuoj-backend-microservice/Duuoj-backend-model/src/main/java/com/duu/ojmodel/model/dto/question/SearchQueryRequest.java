package com.duu.ojmodel.model.dto.question;

import com.duu.ojcommon.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class SearchQueryRequest extends PageRequest implements Serializable {

    /**
     * 搜索词
     */
    private String searchText;
    /**
     * 标签列表
     */
    private List<String> tags;
    /**
     * 搜索类型
     */
    private String type;

    private static final long serialVersionUID = 1L;
}