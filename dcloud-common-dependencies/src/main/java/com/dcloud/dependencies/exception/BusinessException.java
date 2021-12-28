package com.dcloud.dependencies.exception;

import com.dcloud.common.entity.response.Response;

/**
 * @author dcloud
 * @date 2021-08-30 7:27 am
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = -9178187320416490226L;

    public BusinessException(Response respCode) {
        super(respCode.getMsg());
        this.code = respCode.getCode();
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public BusinessException(Integer code, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
    }

    private Integer code;

    public Integer getCode() {
        return code;
    }


}
