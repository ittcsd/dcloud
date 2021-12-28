package com.dcloud.common.entity.log;

public enum SysLogTypeEnum {

     /**
     * 未定义
     */
    UNDEFINED("undefined","未定义"),

    /**
     * 查询
     */
    GET("get","查询"),

    /**
     * 新增
     */
    ADD("add","新增"),

    /**
     * 更新
     */
    UPDATE("update","更新"),

    /**
     * 删除
     */
    DELETE("delete","删除");

    private String operateType;

    private String operateName;

    SysLogTypeEnum(String operateType, String operateName) {
        this.operateType = operateType;
        this.operateName = operateName;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getOperateName() {
        return operateName;
    }

    public void setOperateName(String operateName) {
        this.operateName = operateName;
    }

}
