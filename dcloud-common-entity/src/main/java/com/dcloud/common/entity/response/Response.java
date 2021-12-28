package com.dcloud.common.entity.response;

public interface Response {

    /**
     * @return 状态码：200-成功，0-失败
     */
    Integer getCode();

    /**
     * @return 返回消息
     */
    String getMsg();

    /**
     * @return 返回状态：true-成功，false-失败
     */
    boolean getSuccess();

}
