package com.dcloud.dependencies.utlils;

import java.util.Random;
import java.util.UUID;

/**
 * @author dcloud
 * @date 2021/12/28 20:19
 */
public class PKUtil {
    /**
     * 生成32位主键
     *
     * @return 32位字符串
     */
    public static String createId() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 生成设定长度随机数字
     * @param length 随机数长度
     * @return 规定长度的随机数
     */
    public static String getRandomNum(int length){
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            stringBuilder.append(random.nextInt(10));
        }
        return stringBuilder.toString();
    }

    /**
     * 主键生成方法(纯英文字符串)
     *
     * @return 32位字符串主键
     */
    public static String getTablePk2() {
        return UUID.randomUUID().toString().replaceAll("-", "")
                .replaceAll("0", "o").replaceAll("1", "p").replaceAll("2", "q")
                .replaceAll("3", "r").replaceAll("4", "s").replaceAll("5", "t")
                .replaceAll("6", "u").replaceAll("7", "v").replaceAll("8", "w")
                .replaceAll("9", "x");
    }

    public static void main(String[] args) {
        String id = createId();
        System.out.println(id);
    }
}
