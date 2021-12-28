package com.dcloud.common.entity.constants;


import com.dcloud.common.entity.response.Response;
import lombok.ToString;

/**
 * 通用返回给前端信息(系统级)
 *
 * @author csd
 * @Version v1.0
 * @date 2020/1/8 10:47
 */
@ToString
public enum CommonCode implements Response {
    /**
     * 操作成功： 200
     */
    SUCCESS(true, 200, "操作成功！"),
    /**
     * 操作失败： 404
     */
    NOT_FOUND(false, 404, "Not Found"),
    /**
     * 参数错误：1001-1999
     */
    UN_AUTHENTICATED(false, 1001, "此操作需要登陆系统！"),
    UN_AUTHORISE(false, 1002, "权限不足，无权操作！"),
    INVALID_PARAMS(false, 1003, "非法参数！"),
    CLASS_CAST_ERROR(false, 1004, "参数类型错误！"),
    PARAM_NOT_COMPLETE(false, 1005, "参数缺失"),
    REPETITIVE_OPERATION(false, 1006, "请勿重复操作"),
    ACCESS_LIMIT(false, 1007, "请求太频繁, 请稍后再试"),
    MAIL_SEND_SUCCESS(true, 1008, "邮件发送成功"),


    /**
     * 用户错误： 2001-2999
     */
    USER_NOT_LOGGED_IN(false, 2001, "用户未登陆，访问的路劲需要验证，请登录"),
    USER_LOGIN_ERROR(false, 2002, "账户不存在或密码错误"),
    USER_ACCOUNT_FORBIDDEN(false, 2003, "账户已被禁用"),
    USER_NOT_EXIST(false, 2004, "用户不存在"),
    USER_HAS_EXIST(false, 2005, "用户已存在"),
    WRONG_PASSWORD(false, 2006, "密码错误"),
    NEED_LOGIN(false, 2007, "登录失效"),
    USER_MORE(false, 2008, "匹配多个用户"),

    /**
     * 参数错误
     */
    FIEID_ERROR(false, 400, "参数错误"),

    /**
     * 服务错误： 5001-5999
     */
    SERVER_ERROR(false, 5001, "抱歉，系统繁忙，请稍后重试！"),
    CONTENT_TYPE_NOT_SUPPORT(false, 5002, "ContentType不支持"),
    REQUEST_METHOD_NOT_SUPPORT(false, 5003, "method不支持"),
    MAX_UPLOAD_SIZE(false, 5004, "超出上传最大限制"),
    ;

    /**
     * 操作是否成功
     */
    Boolean success;

    /**
     * 操作返回状态码
     */
    Integer code;

    /**
     * 提示信息
     */
    String message;

    CommonCode(Boolean success, Integer code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }


    public Integer getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.message;
    }

    public boolean getSuccess() {
        return this.success;
    }
}
