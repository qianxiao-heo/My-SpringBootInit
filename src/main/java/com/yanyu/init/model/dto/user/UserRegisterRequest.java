package com.yanyu.init.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户请求体
 *
 * @author 33032
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = -8611708662141275496L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;

    private String email;
}
