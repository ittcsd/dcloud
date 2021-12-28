package com.dcloud.common.entity.constants;

import com.dcloud.common.entity.response.Response;

public enum CommonEnum implements Response {

    SUCCESS(CommonConstant.SUCCESS, "成功", true),
    FAIL(CommonConstant.FAIL, "失败", false);

    private Integer code;
    private String msg;
    private boolean success;

    CommonEnum(Integer code, String msg, boolean success) {
        this.code = code;
        this.msg = msg;
        this.success = success;
    }


    public Integer getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

    public boolean getSuccess() {
        return this.success;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
