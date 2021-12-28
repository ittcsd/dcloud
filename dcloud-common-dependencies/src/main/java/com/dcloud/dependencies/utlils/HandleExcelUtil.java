package com.dcloud.dependencies.utlils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author dcloud
 * @Version v1.0
 * @Date 2021/12/28 20:19
 */
@Slf4j
public class HandleExcelUtil {

    public HandleExcelUtil() {
    }

    public static void downExcel(HttpServletRequest request, HttpServletResponse response, String name, ByteArrayOutputStream os) {
        InputStream is = null;
        BufferedInputStream bis = null;
        ServletOutputStream outputStream = null;
        BufferedOutputStream bos = null;
        try {
            byte[] content = os.toByteArray();
            is = new ByteArrayInputStream(content);
            response.reset();
            String fileName = "";
            Boolean flag = request.getHeader("User-Agent").indexOf("like Gecko") > 0;
            if (request.getHeader("User-Agent").toLowerCase().indexOf("msie") <= 0 && !flag) {
                fileName = new String((name + ".xlsx").getBytes("UTF-8"), "iso-8859-1");
            } else {
                fileName = URLEncoder.encode(name.replace(" ", "") + ".xlsx", "UTF-8");
            }

            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            outputStream = response.getOutputStream();
            bis = new BufferedInputStream(is);
            bos = new BufferedOutputStream(outputStream);
            byte[] buff = new byte[2048];

            int bytesRead;
            while(-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
            outputStream.flush();
        } catch (Exception var13) {
            log.error("文件下载异常！", var13);
        } finally {
            IOUtils.closeQuietly(bis);
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(bos);
            IOUtils.closeQuietly(outputStream);
        }

    }

    public static void downExcelInfo(HttpServletRequest request, HttpServletResponse response, String name, ByteArrayOutputStream os) {
        InputStream is = null;
        BufferedInputStream bis = null;
        ServletOutputStream outputStream = null;
        BufferedOutputStream bos = null;
        try {
            byte[] content = os.toByteArray();
            is = new ByteArrayInputStream(content);
            response.reset();
            String fileName = "";
            Boolean flag = request.getHeader("User-Agent").indexOf("like Gecko") > 0;
            if (request.getHeader("User-Agent").toLowerCase().indexOf("msie") <= 0 && !flag) {
                fileName = new String((name + ".xls").getBytes("UTF-8"), "iso-8859-1");
            } else {
                fileName = URLEncoder.encode(name.replace(" ", "") + ".xls", "UTF-8");
            }

            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
            outputStream = response.getOutputStream();
            bis = new BufferedInputStream(is);
            bos = new BufferedOutputStream(outputStream);
            byte[] buff = new byte[2048];

            int bytesRead;
            while(-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } catch (Exception var13) {
            log.error("文件下载异常！", var13);
        } finally {
            IOUtils.closeQuietly(bis);
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(bos);
            IOUtils.closeQuietly(outputStream);
        }

    }

    public static List<String> handle(InputStream inputStream, String realName) {
        List<String> list = new ArrayList();
        Object wb = null;

        try {
            if ("xls".equals(FilenameUtils.getExtension(realName))) {
                wb = new HSSFWorkbook(inputStream);
            } else {
                wb = new XSSFWorkbook(inputStream);
            }

            for(int i = 0; i < ((Workbook)wb).getNumberOfSheets(); ++i) {
                Sheet sheet = ((Workbook)wb).getSheetAt(i);
                list.addAll(read(sheet));
            }
        } catch (FileNotFoundException var21) {
            log.error(var21.getMessage(), var21);
        } catch (IOException var22) {
            log.error(var22.getMessage(), var22);
        } finally {
            if (wb != null) {
                try {
                    ((Workbook)wb).close();
                } catch (IOException var20) {
                    log.error(var20.getMessage(), var20);
                    var20.printStackTrace();
                }
            }

            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException var19) {
                    log.error(var19.getMessage(), var19);
                }
            }

        }

        return list.isEmpty() ? null : list;
    }

    public static List<String> read(Sheet sheet) {
        List<String> relist = new ArrayList();
        int rows = sheet.getLastRowNum();

        for(int t = 0; t <= rows; ++t) {
            StringBuffer sb = new StringBuffer();
            Row row = sheet.getRow(t);
            if (row != null) {
                int cells = row.getLastCellNum();
                int flag = 1;
                if (cells >= 5) {
                    for(int c = 0; c < cells; ++c) {
                        Cell cell = row.getCell(c);
                        if (cell == null) {
                            ++flag;
                            sb.append("|");
                        } else {
                            String str = readCell(cell);
                            if (StringUtils.isBlank(str)) {
                                ++flag;
                            }

                            sb.append(str + "|");
                        }
                    }

                    if (flag <= 3) {
                        relist.add(sb.toString());
                    }
                }
            }
        }

        return relist;
    }

    public static String StringRep(String str) {
        String regEx = "[`~!@#$%^&()+=|{}':;',\\[\\]<>?~！@#￥%……&（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    public static String readCell(Cell cell) {
        String value = null;
        switch(cell.getCellType()) {
            case NUMERIC:
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    value = HSSFDateUtil.getJavaDate(cell.getNumericCellValue()).toString();
                } else {
                    value = (new DecimalFormat("#.##")).format(cell.getNumericCellValue());
                }
                break;
            case STRING:
                value = cell.getStringCellValue();
                break;
            case FORMULA:
                value = null;
                break;
            case BLANK:
                value = "";
                break;
            case BOOLEAN:
                boolean boo = cell.getBooleanCellValue();
                if (boo) {
                    value = "yes";
                } else {
                    value = "no";
                }
                break;
            case ERROR:
                value = null;
                break;
            default:
                value = "";
        }

        return value;
    }

    public static String getValue(Object obj) {
        return obj == null ? null : obj.toString();
    }

}
