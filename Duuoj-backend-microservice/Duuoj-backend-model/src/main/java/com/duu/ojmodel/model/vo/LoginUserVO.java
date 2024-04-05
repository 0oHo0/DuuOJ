package com.duu.ojmodel.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 已登录用户视图（脱敏）
 *
 * @author duu
 * @from https://github.com/0oHo0
 **/
@Data
public class LoginUserVO implements Serializable {

    /**
     * 用户 id
     */
    private Long id;


    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
    private String userAccount;
    /**
     * 性别 男 女
     */
    private String gender;
    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;
    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    private String age;
    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * token
     */
    private String token;

    private static final long serialVersionUID = 1L;
}