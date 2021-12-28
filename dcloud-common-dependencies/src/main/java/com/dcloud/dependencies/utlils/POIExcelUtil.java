package com.dcloud.dependencies.utlils;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description Excel表格导入导出工具类
 * @Author dcloud
 * @Version v1.0
 * @Date 2020/2/24 1:41
 */
public class POIExcelUtil {

    public POIExcelUtil() {
    }

    public static Workbook exportExcelByList(String[] keys, String[] values, List<Map<String, Object>> dataList) {
        return ExcelExportUtil.exportExcel(new ExportParams(), handleHeadTile(keys, values), dataList);
    }

    public static Workbook exportExcelByList(String tilte, String sheetName, String[] keys, String[] values, List<Map<String, Object>> dataList) {
        return ExcelExportUtil.exportExcel(new ExportParams(tilte, sheetName), handleHeadTile(keys, values), dataList);
    }

    private static List<ExcelExportEntity> handleHeadTile(String[] keys, String[] values) {
        List<ExcelExportEntity> list = new ArrayList();

        for (int i = 0; i < keys.length; ++i) {
            ExcelExportEntity excelExportEntity = new ExcelExportEntity();
            excelExportEntity.setWidth(30.0D);
            excelExportEntity.setKey(keys[i]);
            excelExportEntity.setName(values[i]);
            list.add(excelExportEntity);
        }

        return list;
    }

}
