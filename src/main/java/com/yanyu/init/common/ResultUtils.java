package com.yanyu.init.common;

/**
 * 返回工具类
 *
 * @author 33032
 */
public class ResultUtils {

    /**
     * 成功
     *
     * @param data data
     * @param <T> 范型
     * @return 成功返回
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "ok","success");
    }

    /**
     * 失败
     *
     * @param errorCode 错误码
     * @return 错误码
     */
    public static <T> BaseResponse<T> error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    /**
     * 失败
     *
     * @param code 状态码
     * @param message 信息
     * @param description 描述
     * @return result
     */
    public static <T> BaseResponse<T> error(int code, String message, String description) {
        return new BaseResponse<>(code, null, message, description);
    }

    /**
     * 失败
     *
     * @param errorCode 错误码
     * @return result
     */
    public static <T> BaseResponse<T> error(ErrorCode errorCode, String message, String description) {
        return new BaseResponse<>(errorCode.getCode(), null, message, description);
    }

}
