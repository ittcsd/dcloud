package com.dcloud.dependencies.exception;

import com.dcloud.common.entity.constants.CommonCode;
import com.dcloud.common.entity.response.DcloudJsonResult;
import com.dcloud.common.entity.response.Response;
import com.google.common.collect.ImmutableMap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 全局统一异常捕获
 *
 * @author dcloud
 * @Version v1.0
 * @date 2021/8/30 12:04
 */
@Slf4j
@ControllerAdvice
@SuppressWarnings("all")
public class GlobExceptionHandler {

    /**
     * 定义map，配置异常类型对应的错误代码
     */
    private static ImmutableMap<Class<? extends Throwable>, Response> EXCEPTIONS;
    /**
     * 定义map的builder对象，去构建ImmutableMap
     */
    protected static ImmutableMap.Builder<Class<? extends Throwable>, Response> builder = ImmutableMap.builder();

    static {
        // 定义异常类型所对应的错误代码
        builder.put(HttpMessageNotReadableException.class, CommonCode.INVALID_PARAMS);
        builder.put(ClassCastException.class, CommonCode.CLASS_CAST_ERROR);
    }

    @ExceptionHandler({BusinessException.class})
    @ResponseBody
    public DcloudJsonResult exceptionHandler(BusinessException ex) {
        log.error("catch exception:{}", ex.getMessage(), ex);
        DcloudJsonResult dcloudJsonResult = new DcloudJsonResult(ex.getCode(), ex.getMessage(), Boolean.FALSE);
        return dcloudJsonResult;

    }

    /**
     * 捕获系统Exception异常
     *
     * @param ex 系统异常
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public DcloudJsonResult systemExceptionHandler(Exception ex) {
        // 记录日志
        log.error("catch exception:{}", ex.getMessage(), ex);
        if (EXCEPTIONS == null) {
            // EXCEPTIONS构建成功
            EXCEPTIONS = builder.build();
        }
        // 从EXCEPTIONS中找异常类型所对应的错误代码，如果找到了将错误代码响应给用户，如果找不到给用户响应5001异常
        Response respCode = EXCEPTIONS.get(ex.getClass());
        if (respCode != null) {
            DcloudJsonResult dcloudJsonResult = new DcloudJsonResult<>(respCode);
            dcloudJsonResult.setMsg(respCode.getMsg() + ":" + ex.getMessage());
            return dcloudJsonResult;
        }
        if (ex instanceof NoHandlerFoundException) {
            NoHandlerFoundException exception = (NoHandlerFoundException) ex;
            DcloudJsonResult dcloudJsonResult = new DcloudJsonResult(CommonCode.NOT_FOUND);
            return dcloudJsonResult;
        }

        DcloudJsonResult dcloudJsonResult = new DcloudJsonResult<>(CommonCode.SERVER_ERROR);
        return dcloudJsonResult;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public DcloudJsonResult handleBindException(MethodArgumentNotValidException ex) {
        log.error(ex.getMessage(), ex);
        List<FieldError> fieldErrorList = ex.getBindingResult().getFieldErrors();
        String message = "";
        if (!CollectionUtils.isEmpty(fieldErrorList)) {
            message = fieldErrorList.stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(","));
        } else {
            message = ex.getMessage();
        }
        DcloudJsonResult dcloudJsonResult = new DcloudJsonResult();
        dcloudJsonResult.setCode(CommonCode.FIEID_ERROR.getCode());
        dcloudJsonResult.setMsg(message);
        dcloudJsonResult.setSuccess(Boolean.FALSE);
        return dcloudJsonResult;
    }

    @ExceptionHandler(BindException.class)
    @ResponseBody
    public DcloudJsonResult handleBindException(BindException ex) {
        log.error(ex.getMessage(), ex);
        List<FieldError> fieldErrorList = ex.getBindingResult().getFieldErrors();
        String message = "";
        if (!CollectionUtils.isEmpty(fieldErrorList)) {
            message = fieldErrorList.stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(","));
        } else {
            message = ex.getMessage();
        }
        DcloudJsonResult dcloudJsonResult = new DcloudJsonResult();
        dcloudJsonResult.setCode(CommonCode.FIEID_ERROR.getCode());
        dcloudJsonResult.setMsg(message);
        dcloudJsonResult.setSuccess(Boolean.FALSE);
        return dcloudJsonResult;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public DcloudJsonResult resolveConstraintViolationException(ConstraintViolationException ex) {
        log.error(ex.getMessage(), ex);
        String message = "";
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        if (!CollectionUtils.isEmpty(constraintViolations)) {
            message = constraintViolations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(","));
        }
        DcloudJsonResult dcloudJsonResult = new DcloudJsonResult();
        dcloudJsonResult.setCode(CommonCode.FIEID_ERROR.getCode());
        dcloudJsonResult.setMsg(message);
        dcloudJsonResult.setSuccess(Boolean.FALSE);
        return dcloudJsonResult;
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseBody
    public DcloudJsonResult handleException(HttpMediaTypeNotSupportedException ex) {
        log.error(ex.getMessage(), ex);
        DcloudJsonResult dcloudJsonResult = new DcloudJsonResult();
        dcloudJsonResult.setCode(CommonCode.CONTENT_TYPE_NOT_SUPPORT.getCode());
        dcloudJsonResult.setMsg(ex.getMessage());
        dcloudJsonResult.setSuccess(Boolean.FALSE);
        return dcloudJsonResult;
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public DcloudJsonResult handleException(HttpRequestMethodNotSupportedException ex) {
        log.error(ex.getMessage(), ex);
        DcloudJsonResult dcloudJsonResult = new DcloudJsonResult();
        dcloudJsonResult.setCode(CommonCode.REQUEST_METHOD_NOT_SUPPORT.getCode());
        dcloudJsonResult.setMsg(ex.getMessage());
        dcloudJsonResult.setSuccess(Boolean.FALSE);
        return dcloudJsonResult;
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseBody
    public DcloudJsonResult handleException(MaxUploadSizeExceededException ex) {
        log.error(ex.getMessage(), ex);
        DcloudJsonResult dcloudJsonResult = new DcloudJsonResult();
        dcloudJsonResult.setCode(CommonCode.MAX_UPLOAD_SIZE.getCode());
        dcloudJsonResult.setMsg(ex.getRootCause().getLocalizedMessage());
        dcloudJsonResult.setSuccess(Boolean.FALSE);
        return dcloudJsonResult;
    }
}
