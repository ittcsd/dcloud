package com.dcloud.dependencies.utlils;

import java.io.Serializable;
import java.util.List;

/**
 * ElasticSearch分页查询结果集
 * @author dcloud
 * @date 2021-12-17 16:00
 * @version es: 6.3.1
 */
public class EsPage<T> implements Serializable {

    /**
     * 当前页
     */
    private int pageNum;
    /**
     * 每页显示多少条
     */
    private int pageSize;

    /**
     * 总记录数
     */
    private int total;

    /**
     * 本页的数据列表
     */
    private List<T> list;

    /**
     * 只接受前4个必要的属性，会自动的计算出其他3个属性的值
     *
     * @param pageNum
     * @param pageSize
     * @param total
     * @param list
     */
    public EsPage(int pageNum, int pageSize, int total, List<T> list) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.list = list;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
