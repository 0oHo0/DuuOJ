package com.duu.ojmodel.model.dto.question;

import lombok.Data;

/**
 * @author Duu
 * @description
 * @date 2024/04/19 12:04
 * @from https://github.com/0oHo0
 **/
@Data
public class QuestionStatisticsResponse {

    private Integer simpleTotalNum;

    private Integer simpleAcceptedNum;

    private Integer mediumTotalNum;

    private Integer mediumAcceptedNum;

    private Integer hardTotalNum;

    private Integer hardAcceptedNum;

    private Integer totalNum;

    private Integer acceptedNum;
}