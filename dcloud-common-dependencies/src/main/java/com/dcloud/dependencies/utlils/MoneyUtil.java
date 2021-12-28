package com.dcloud.dependencies.utlils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Objects;

/**
 * @author dcloud
 * @date 2021/12/28 20:19
 */
public class MoneyUtil {

    public static final String p1 = "￥,##0.00";
    public static final String p2 = ",##0.00";

    public static String format(String pattern, BigDecimal bigDecimal) {
        DecimalFormat df = new DecimalFormat(pattern);
        return df.format(bigDecimal) ;
    }

    public static String feeToYuan(Integer fee) {
        if (Objects.isNull(fee)) {
            return "";
        }
        BigDecimal feeBig = new BigDecimal(fee);
        BigDecimal yuanBig = feeBig.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return format(p1, yuanBig);
    }

    public static String feeToYuan(Integer fee, String pattern) {
        if (Objects.isNull(fee)) {
            return "";
        }
        BigDecimal feeBig = new BigDecimal(fee);
        BigDecimal yuanBig = feeBig.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return format(pattern, yuanBig);
    }

    public static void main(String[] args) {
        System.out.println(feeToYuan(2111111));
        System.out.println(feeToYuan(10020, p2));
    }
}
