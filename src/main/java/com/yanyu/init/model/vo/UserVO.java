package com.yanyu.init.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户视图
 */
@Data
public class UserVO implements Serializable {

    private Long id;
    /**
     * 用户昵称
     */
    private String userName;
    /**
     * 用户头像
     */
    private String userAvatar;
    /**
     * 用户简介
     */
    private String userProfile;
    /**
     * 用户角色 user - 普通用户  admin - 管理员
     */
    private String userRole;
    /**
     * 性别
     */
    private String gender;
    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;
    /**
     * 状态 0 - 正常 1 - 封禁
     */
    private Integer userStatus;
    /**
     * 创建时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;
}
