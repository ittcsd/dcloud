package com.dcloud.common.entity.log;

import com.dcloud.common.entity.constants.CommonCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统日志
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysLogBean implements Serializable {
    private static final long serialVersionUID = -8057498216097487494L;
    private Long id ;
    /** 日志id */
    private String logId ;
    /** 业务模块 */
    private String model ;
    /** 请求url */
    private String requestUrl ;
    /** 请求方式 */
    private String logType ;
    /** 请求方法名称 */
    private String methodName ;
    /** 请求参数 */
    private String params ;
    /** 异常信息 */
    private String msg ;
    /** 接口备注 */
    private String logNote ;
    /** 请求ip */
    private String ip ;
    /** 请求状态码 */
    private Integer code = CommonCode.SUCCESS.getCode();
    /** 是否存储请求参数 */
    private String saveDataFlag = Boolean.FALSE.toString();
    /** 创建人 */
    private String creator ;
    /** 创建时间 */
    private Date createTime ;
    /** 更新人 */
    private String updater ;
    /** 更新时间 */
    private Date updateTime ;

    public SysLogBean(String logId, String model, String requestUrl,
                      String logType, String methodName, String params,
                      String msg, String logNote, String ip, Integer code,
                      String saveDataFlag, String creator, Date createTime, String updater, Date updateTime) {
        this.logId = logId;
        this.model = model;
        this.requestUrl = requestUrl;
        this.logType = logType;
        this.methodName = methodName;
        this.params = params;
        this.msg = msg;
        this.logNote = logNote;
        this.ip = ip;
        this.code = code;
        this.saveDataFlag = saveDataFlag;
        this.creator = creator;
        this.createTime = createTime;
        this.updater = updater;
        this.updateTime = updateTime;
    }

    @Data
    public static class LogBuilder {
        private String logId ;
        private String model ;
        private String requestUrl ;
        private String logType ;
        private String methodName ;
        private String params ;
        private String msg ;
        private String logNote ;
        private String ip ;
        private Integer code;
        private String saveDataFlag;
        private String creator ;
        private Date createTime ;
        private String updater ;
        private Date updateTime ;

        public LogBuilder logId(String logId) {
            this.logId = logId;
            return this;
        }

        public LogBuilder model(String model) {
            this.model = model;
            return this;
        }

        public LogBuilder requestUrl(String requestUrl) {
            this.requestUrl = requestUrl;
            return this;
        }

        public LogBuilder logType(String logType) {
            this.logType = logType;
            return this;
        }

        public LogBuilder methodName(String methodName) {
            this.methodName = methodName;
            return this;
        }

        public LogBuilder params(String params) {
            this.params = params;
            return this;
        }

        public LogBuilder msg(String msg) {
            this.msg = msg;
            return this;
        }

        public LogBuilder logNote(String logNote) {
            this.logNote = logNote;
            return this;
        }

        public LogBuilder ip(String ip) {
            this.ip = ip;
            return this;
        }

        public LogBuilder code(Integer code) {
            this.code = code;
            return this;
        }

        public LogBuilder saveDataFlag(String saveDataFlag) {
            this.saveDataFlag = saveDataFlag;
            return this;
        }

        public LogBuilder creator(String creator) {
            this.creator = creator;
            return this;
        }

        public LogBuilder updater(String updater) {
            this.updater = updater;
            return this;
        }

        public LogBuilder createTime(Date createTime) {
            this.createTime = createTime;
            return this;
        }

        public LogBuilder updateTime(Date updateTime) {
            this.updateTime = updateTime;
            return this;
        }

        public SysLogBean build() {
            return new SysLogBean(this.logId,
                    this.model,
                    this.requestUrl,
                    this.logType,
                    this.methodName,
                    this.params,
                    this.msg,
                    this.logNote,
                    this.ip,
                    this.code,
                    this.saveDataFlag,
                    this.creator,
                    this.createTime,
                    this.updater,
                    this.updateTime);
        }
    }
}
