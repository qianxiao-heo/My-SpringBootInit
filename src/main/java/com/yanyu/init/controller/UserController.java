package com.yanyu.init.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yanyu.init.annotation.AuthCheck;
import com.yanyu.init.common.BaseResponse;
import com.yanyu.init.model.dto.user.DeleteRequest;
import com.yanyu.init.common.ErrorCode;
import com.yanyu.init.common.ResultUtils;
import com.yanyu.init.constant.UserConstant;
import com.yanyu.init.exception.BusinessException;
import com.yanyu.init.model.dto.user.UpdateRequest;
import com.yanyu.init.model.entity.User;
import com.yanyu.init.model.dto.user.UserLoginRequest;
import com.yanyu.init.model.dto.user.UserRegisterRequest;
import com.yanyu.init.model.vo.LoginUserVO;
import com.yanyu.init.model.vo.UserVO;
import com.yanyu.init.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static com.yanyu.init.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 *
 * @author 33032
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 注册
     *
     * @param userRegisterRequest 注册请求体
     * @return 用户ID
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String email = userRegisterRequest.getEmail();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, email)) {
            return ResultUtils.error(ErrorCode.VERIFY_EMPTY);
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword, email);
        return ResultUtils.success(result);
    }

    /**
     * 登录
     *
     * @param userLoginRequest 登录请求体
     * @param request          request
     * @return 用户脱敏信息
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "请求参数为空", "请求参数为空");
        }
        LoginUserVO user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    /**
     * 注销
     *
     * @param request request
     * @return 注销结果(true为成功，false为失败)
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLoginOut(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.userLoginOut(request);
        return ResultUtils.success(result);
    }

    /**
     * 获取登录用户信息
     *
     * @param request 客户端请求
     * @return 脱敏后的用户信息
     */
    @GetMapping("/current")
    public BaseResponse<UserVO> getCurrentUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Long userId = currentUser.getId();
        //todo 校验用户是否合法
        User user = userService.getById(userId);
        //用户信息脱敏
        UserVO safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }

    /**
     * 模糊查询
     *
     * @param username 用户名
     * @return 脱敏后的用户信息
     */
    @GetMapping("/search")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<List<UserVO>> searchUsers(String username, HttpServletRequest request) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<UserVO> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(list);
    }

    /**
     * 列表查询
     *
     * @return 脱敏后的用户列表
     */
    @GetMapping("/list")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<List<UserVO>> userList() {
        List<User> userList = userService.list();
        List<UserVO> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(list);
    }

    /**
     * 删除
     *
     * @param deleteRequest 删除请求体
     * @return boolean
     * @description 用户删除
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUsers(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "删除参数错误");
        }
        boolean result = userService.removeById(deleteRequest.getId());
        return ResultUtils.success(result);
    }

    /**
     * 用户更新
     *
     * @param updateRequest 更新请求体
     * @return 执行结果
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UpdateRequest updateRequest) {
        if (updateRequest == null || updateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "更新参数错误");
        }
        User user = new User();
        BeanUtils.copyProperties(updateRequest, user);
        boolean result = userService.updateById(user);
        return ResultUtils.success(result);
    }
}
