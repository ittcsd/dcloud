package com.dcloud.dependencies.utlils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dcloud
 * @Version v1.0
 * @date 2021/12/15 16:38
 * @Description: list中随机抽取元素
 */
public class RandomListUtil {

    /**
     * 从list中随机抽取元素
     *
     * @param list 集合
     * @param n    获取元素个数
     * @Description:
     */
    public static <T> List<T> buildRandomList(List<T> list, int n) {
        Map<Integer,Object> map = new HashMap<>(n);
        List<T> listNew = new ArrayList<T>();
        if (list.size() <= n) {
            return list;
        } else {
            while (map.size() < n) {
                int random = (int) (Math.random() * list.size());
                if (!map.containsKey(random)) {
                    map.put(random, "");
                    listNew.add(list.get(random));
                }
            }
            return listNew;
        }
    }

    public static void main(String[] args) {
        List<Integer> numberList = new ArrayList<>();
        numberList.add(1);
        numberList.add(2);
        numberList.add(3);
        numberList.add(4);
        numberList.add(5);
        numberList.add(6);
        numberList.add(7);
        numberList.add(8);
        numberList.add(9);
        numberList.add(10);
        numberList.add(11);
        numberList.add(12);
        List<Integer> randomList = buildRandomList(numberList, 5);
        System.out.println("randomList = " + randomList);
    }


}
