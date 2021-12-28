package com.dcloud.dependencies.utlils;

import com.dcloud.common.entity.constants.CommonCode;
import com.dcloud.dependencies.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;

import static java.lang.String.format;

public class DcloudValidUtil {


    public DcloudValidUtil() {
    }

    public static void checkParams(String param) {
        if (StringUtils.isBlank(param)) {
            throw new BusinessException(CommonCode.PARAM_NOT_COMPLETE);
        }

    }

    public static <T> T checkNotNull(T reference,
                                     String errorMessageTemplate,
                                     Object... errorMessageArgs) {
        if (reference == null) {
            throw new NullPointerException(
                    format(errorMessageTemplate, errorMessageArgs));
        }
        return reference;
    }

    public static void checkArgument(boolean expression,
                                     String errorMessageTemplate,
                                     Object... errorMessageArgs) {
        if (!expression) {
            throw new IllegalArgumentException(
                    format(errorMessageTemplate, errorMessageArgs));
        }
    }

    public static String emptyToNull(String string) {
        return string == null || string.isEmpty() ? null : string;
    }
}
