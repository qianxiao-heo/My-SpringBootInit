package com.yanyu.init.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 管理员修改用户信息请求体
 */
@Data
public class UpdateRequest implements Serializable {

    private Long id;

    private String userName;//用户名

//    private String userAccount;//账户

    private String userPassword;//用户密码

    private String avatarUrl;//用户头像

    private String userProfile;//用户简介

    private String gender;//性别

    private Integer userStatus;//用户状态

    private Integer isDelete;//是否删除

    private String userRole;//角色

    private String planetCode;//编号

    private static final long serialVersionUID = -7257534231975712173L;
}
