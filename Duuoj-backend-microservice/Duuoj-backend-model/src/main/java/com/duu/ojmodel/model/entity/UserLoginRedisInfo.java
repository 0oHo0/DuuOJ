package com.duu.ojmodel.model.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLoginRedisInfo {

    private User user;

    private String ip;

}