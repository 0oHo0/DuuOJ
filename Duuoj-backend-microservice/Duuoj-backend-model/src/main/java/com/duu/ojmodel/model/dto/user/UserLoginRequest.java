package com.duu.ojmodel.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求
 *
 * @author duu
 * @from https://github.com/0oHo0
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    private String userAccount;

    private String userPassword;
}
