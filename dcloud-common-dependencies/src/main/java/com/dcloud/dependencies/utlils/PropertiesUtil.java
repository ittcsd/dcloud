package com.dcloud.dependencies.utlils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 获取Properties文件属性值
 *
 * @Author dcloud
 * @Version v1.0
 * @Date 2021/12/23 21:13
 */
public class PropertiesUtil {

    public PropertiesUtil() {
    }

    /**
     * @param propertiesName properties 文件名
     * @return Properties
     */
    public static Properties getProperties(String propertiesName) {
        Properties prop = new Properties();
        InputStream in = PropertiesUtil.class.getClassLoader().getResourceAsStream(propertiesName);

        try {
            prop.load(in);
            return prop;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * @param propertiesName properties 文件名
     * @param key 属性
     * @return 属性值
     */
    public static String getValue(String propertiesName, String key) {
        Properties prop = new Properties();
        InputStream in = PropertiesUtil.class.getClassLoader().getResourceAsStream(propertiesName);

        try {
            prop.load(in);
            return prop.getProperty(key);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        String test = PropertiesUtil.getValue("application.properties", "test");
        System.out.println("test = " + test);
    }
}
