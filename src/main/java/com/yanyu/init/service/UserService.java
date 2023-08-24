package com.yanyu.init.service;

import com.yanyu.init.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yanyu.init.model.vo.LoginUserVO;
import com.yanyu.init.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 33032
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2023-04-03 16:31:49
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @param email         校验邮箱
     * @return 返回用户ID
     */
    long userRegister(String userAccount, String userPassword, String checkPassword, String email);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request      客户端请求
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户信息脱敏
     *
     * @param originUser 原始用户信息
     * @return 脱敏后的用户信息
     */
    UserVO getSafetyUser(User originUser);

    /**
     * 获取脱敏的已登录用户信息
     *
     * @param user 原始用户信息
     * @return 登录脱敏后的用户信息
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 用户注销
     *
     * @param request request
     */
    boolean userLoginOut(HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request 客户端请求
     * @return 当前登录用户
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param request 客户端请求
     * @return 是否为管理员
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param user 用户
     * @return 是否为管理员
     */
    boolean isAdmin(User user);

    /**
     * 获取当前登录用户（允许未登录）
     *
     * @param request 客户端请求
     * @return 根据Id查询后的User
     */
    User getLoginUserPermitNull(HttpServletRequest request);

}
