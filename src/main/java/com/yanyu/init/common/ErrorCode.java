package com.yanyu.init.common;

/**
 * 全局状态码
 *
 * @author 33032
 */
public enum ErrorCode {
    SUCCESS(0, "ok", ""),
    PARAMS_ERROR(40000, "请求参数错误", ""),
    NULL_ERROR(40001, "请求数据为空", ""),
    NOT_LOGIN(40100, "未登录", ""),
    NO_AUTH(40101, "无权限", ""),
    SYSTEM_ERROR(50000, "系统内部异常", ""),
    USER_REPEAT(40102, "用户名重复", "用户名重复"),
    PLATE_CODE_REPEAT(40103, "编号重复", "用户编号重复"),
    SAVE_USER_ERROR(40104, "注册失败", "注册失败，数据库错误"),
    USER_STATE(40105, "登录失败", "账号或密码不匹配"),
    VERIFY_EMPTY(40106, "传入参数为空", ""),
    VERIFY_ERROR(40107, "验证错误", "验证错误"),
    EMAIL_FORMAT_ERROR(40108, "邮箱格式错误", "邮箱格式错误"),
    OPERATION_ERROR(50001,"操作失败","操作失败"),
    NOT_FOUND_ERROR(40400, "请求数据不存在","请求数据不存在");

    private final int code;

    /**
     * 状态码信息
     */
    private final String message;

    /**
     * 状态码描述（详情）
     */
    private final String description;

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}
