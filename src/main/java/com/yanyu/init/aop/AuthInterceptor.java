package com.yanyu.init.aop;

import com.yanyu.init.annotation.AuthCheck;
import com.yanyu.init.common.ErrorCode;
import com.yanyu.init.exception.BusinessException;
import com.yanyu.init.model.entity.User;
import com.yanyu.init.model.enums.UserRoleEnum;
import com.yanyu.init.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 权限校验 AOP
 *
 * @author 33032
 */
@Aspect
@Component
public class AuthInterceptor {

    @Resource
    private UserService userService;

    /**
     * 执行拦截
     *
     * @param joinPoint 连接点
     * @param authCheck 需要的权限
     * @return 鉴权结果
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        String mustRole = authCheck.mustRole();
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 当前登录用户
        User loginUser = userService.getLoginUser(request);
        // 必须有该权限才通过
        if (StringUtils.isNotBlank(mustRole)) {
            UserRoleEnum mustUserRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
            if (mustUserRoleEnum == null) {
                throw new BusinessException(ErrorCode.NO_AUTH);
            }
            String userRole = loginUser.getUserRole();
            Integer userStatus = loginUser.getUserStatus();
            // 如果被封号，直接拒绝
            if (userStatus == 1) {
                throw new BusinessException(ErrorCode.NO_AUTH);
            }
            // 管理员权限判断
            if (UserRoleEnum.ADMIN.equals(mustUserRoleEnum)) {
                if (!mustRole.equals(userRole)) {
                    throw new BusinessException(ErrorCode.NO_AUTH);
                }
            }
        }
        // 通过权限校验，放行
        return joinPoint.proceed();
    }
}

