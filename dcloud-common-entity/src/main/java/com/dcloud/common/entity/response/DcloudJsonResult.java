package com.dcloud.common.entity.response;

import com.dcloud.common.entity.constants.CommonEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DcloudJsonResult<T> implements Serializable {
    private static final long serialVersionUID = -1007977656652355029L;
    private Integer code;
    private String msg;
    private Boolean success;
    private T data;

    public DcloudJsonResult(Response response) {
        this.code = response.getCode();
        this.msg = response.getMsg();
        this.success = response.getSuccess();
    }

    public DcloudJsonResult(Response response, T data) {
        this.code = response.getCode();
        this.msg = response.getMsg();
        this.success = response.getSuccess();
        this.data = data;
    }

    public DcloudJsonResult(Integer code, String msg, boolean success) {
        this.code = code;
        this.msg = msg;
        this.success = success;
    }

    public DcloudJsonResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static <T>  DcloudJsonResult<T> success(T data) {
        return new DcloudJsonResult<T>(CommonEnum.SUCCESS, data);
    }

    public static <T>  DcloudJsonResult<T> success() {
        return new DcloudJsonResult<T>(CommonEnum.SUCCESS);
    }

    public static <T> DcloudJsonResult<T> fail() {
        return new DcloudJsonResult<T>(CommonEnum.FAIL);
    }

    public static <T> DcloudJsonResult<T> fail(String message) {
        DcloudJsonResult<T> dto = new DcloudJsonResult<T>(CommonEnum.FAIL);
        dto.setMsg(message);
        return dto;
    }
}
