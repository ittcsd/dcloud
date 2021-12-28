package com.dcloud.dependencies.aspect;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dcloud.common.entity.annotation.SysLog;
import com.dcloud.common.entity.constants.CommonCode;
import com.dcloud.common.entity.log.SysLogBean;
import com.dcloud.common.entity.log.SysLogTypeEnum;
import com.dcloud.dependencies.mapper.SysLogMapper;
import com.dcloud.dependencies.utlils.DateUtil;
import com.dcloud.dependencies.utlils.IpUtil;
import com.dcloud.dependencies.utlils.LogAopUtil;
import com.dcloud.dependencies.utlils.PKUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author: dcloud
 * @Email: ittcsd@163.com
 * @Date: 2021/12/28 11:29
 * @Description: 操作日志切面类
 **/
@Component
@Slf4j
@Aspect
public class LogAspect {

    @Autowired
    private SysLogMapper sysLogMapper;
//    @Autowired
//    private UserService userService;

    public LogAspect() {
    }

    /*
     * 切点
     * */
    @Pointcut("@annotation(com.dcloud.common.entity.annotation.SysLog)")
    public void logPointCut() {
    }

    @Around("logPointCut()")
    public void around(ProceedingJoinPoint point) throws Throwable {
        // 若方法上未加注解，则不执行切面日志
        SysLog sysLog = getAnnotation(point);
        if (Objects.isNull(sysLog)) {
            return;
        }
        try {
            point.proceed();
        } catch (Exception e) {
            // 异常处理记录日志..log.error(e);
            log.error(String.format("%s 请求异常", getMethodName(point)), e.getMessage());
            throw e;
        }
        saveLog(point, sysLog);
    }

    private void saveLog(ProceedingJoinPoint point, SysLog sysLog) throws Exception {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder
                .getRequestAttributes())).getRequest();
        String methodName = point.getSignature().getName();
        Object[] args = point.getArgs();
        String classType = point.getTarget().getClass().getName();
        Class<?> clazz = Class.forName(classType);
        String clazzName = clazz.getName();
        //获取操作人名称
//        UserInfo userInfo = userService.getUserInfo(PKUtil.createId());
        SysLogBean sysLogBean = new SysLogBean.LogBuilder()
                .logId(PKUtil.createId())
                .model(getModel(sysLog))
                .requestUrl(request.getRequestURI())
                .logType(getLogType(sysLog))
                .methodName(getMethodName(point))
                .params(getSaveDataFlag(sysLog) ? LogAopUtil.getNameAndArgs(this.getClass(), clazzName, methodName, args).toString() : StringUtils.EMPTY)
                .msg(StringUtils.EMPTY)
                .logNote(getLogNote(sysLog))
                .ip(IpUtil.getIpAddress(request))
                .code(CommonCode.SUCCESS.getCode())
                .saveDataFlag(String.valueOf(sysLog.saveRequestData()))
                .creator(PKUtil.createId())
                .createTime(DateUtil.date())
                .build();
        //保存日志
        sysLogMapper.saveSysLogDetail(sysLogBean);
    }

    /**
     * 获取所有请求参数，封装为map对象
     *
     * @return
     */
    public Map<String, Object> getParameterMap(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        Enumeration<String> enumeration = request.getParameterNames();
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        StringBuilder stringBuilder = new StringBuilder();
        while (enumeration.hasMoreElements()) {
            String key = enumeration.nextElement();
            String value = request.getParameter(key);
            String keyValue = key + " : " + value + " ; ";
            stringBuilder.append(keyValue);
            parameterMap.put(key, value);
        }
        return parameterMap;
    }

    public String getReqParameter(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        Enumeration<String> enumeration = request.getParameterNames();
        //StringBuilder stringBuilder = new StringBuilder();
        JSONArray jsonArray = new JSONArray();
        while (enumeration.hasMoreElements()) {
            String key = enumeration.nextElement();
            String value = request.getParameter(key);
            JSONObject json = new JSONObject();
            //String keyValue = key+" : " +value+" ; ";
            json.put(key, value);
            //stringBuilder.append(keyValue);
            jsonArray.add(json);
        }
        //JSONObject jsonObject = new JSONObject();
        //jsonObject.put("请求参数为：",jsonArray.toString());
        return jsonArray.toString();
    }


    /*
     * 获取方法名称
     * */
    private String getMethodName(ProceedingJoinPoint point) {
        Signature signature = point.getSignature();
        if (signature instanceof MethodSignature) {
            MethodSignature methodSignature = (MethodSignature) signature;
            return methodSignature.getMethod().getName();
        }
        return StringUtils.EMPTY;
    }

    public String getLogNote(SysLog sysLog) {
        log.info("获取方法接口描述...");
        return sysLog.note();
    }

    public String getModel(SysLog sysLog) {
        log.info("获取接口所属业务模块...");
        return sysLog.model();
    }

    /*
     * @see com.dcloud.common.entity.log.SysLogTypeEnum
     * */
    public String getLogType(SysLog sysLog) {
        log.info("获取接口请求类型...");
        SysLogTypeEnum type = sysLog.type();
        return type.getOperateType();
    }

    public Boolean getSaveDataFlag(SysLog sysLog) {
        log.info("获取接口请求类型...");
        return sysLog.saveRequestData();
    }

    /*
     * 获取注解
     * */
    public SysLog getAnnotation(ProceedingJoinPoint point) {
        Signature signature = point.getSignature();
        if (signature instanceof MethodSignature) {
            MethodSignature methodSignature = (MethodSignature) signature;
            Method method = methodSignature.getMethod();

            if (Objects.nonNull(method)) {
                return method.getAnnotation(SysLog.class);
            }
        }
        return null;
    }
}

