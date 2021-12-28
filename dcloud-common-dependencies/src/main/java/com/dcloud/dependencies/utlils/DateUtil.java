package com.dcloud.dependencies.utlils;


import cn.hutool.core.date.DateTime;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * date time util
 *
 * @author: dcloud
 * @date: 2021/8/30 10:50
 */
public class DateUtil {

    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String PATTERN_DAY = "yyyy-MM-dd";

    public static final String p1 = "yyyyMMddHHmmss";

    public static DateTime date() {
        return new DateTime();
    }

    /**
     * 把日期字符串格式化成日期类型
     *
     * @param dateStr 日期字符串
     * @param format  格式
     * @return 日期
     */
    public static Date convert2Date(String dateStr, String format) {
        SimpleDateFormat simple = new SimpleDateFormat(format);
        try {
            simple.setLenient(false);
            return simple.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 把日期字符串格式化成日期类型
     *
     * @param dateStr 日期字符串
     * @return 日期
     */
    public static Date convert2Date(String dateStr) {
        SimpleDateFormat simple = new SimpleDateFormat(PATTERN);
        try {
            simple.setLenient(false);
            return simple.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }
    
    
    /**
          * 把日期字符串格式化成日期类型
     *
     * @param dateStr 日期字符串
     * @return 日期
     */
    public static Date convert2Day(String dateStr) {
        SimpleDateFormat simple = new SimpleDateFormat(PATTERN_DAY);
        try {
            simple.setLenient(false);
            return simple.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 把日期类型格式化成字符串
     *
     * @param date   日期
     * @param format 格式
     * @return 日期字符串
     */
    public static String convert2String(Date date, String format) {
        SimpleDateFormat formater = new SimpleDateFormat(format);
        try {
            return formater.format(date);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 使用默认字符串把日期类型格式化成字符串
     *
     * @param date   日期
     * @return 日期字符串
     */
    public static String convert2String(Date date) {
        SimpleDateFormat formater = new SimpleDateFormat(PATTERN);
        try {
            return formater.format(date);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 使用默认字符串把日期类型格式化成字符串
     *
     * @param date   日期
     * @return 日期字符串
     */
    public static String convert2StringDay(Date date) {
        SimpleDateFormat formater = new SimpleDateFormat(PATTERN_DAY);
        try {
            return formater.format(date);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 转sql的time格式
     *
     * @param date 日期
     * @return sql timestamp
     */
    public static java.sql.Timestamp convertSqlTime(Date date) {
        java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());
        return timestamp;
    }

    /**
     * 转sql的日期格式
     *
     * @param date 日期
     * @return sql date
     */
    public static java.sql.Date convertSqlDate(Date date) {
        java.sql.Date Datetamp = new java.sql.Date(date.getTime());
        return Datetamp;
    }


    /**
     * 获取当前日期
     *
     * @param format 格式
     * @return 获取日期
     */
    public static String getCurrentDate(String format) {
        return new SimpleDateFormat(format).format(new Date());
    }

    /**
     * 获取时间戳
     *
     * @return 系统当前毫秒值
     */
    public static long getTimestamp() {
        return System.currentTimeMillis();
    }

    /**
     * 获取月份的天数
     *
     * @param year  日期
     * @param month 月份
     * @return 天数
     */
    public static int getDaysOfMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取日期的年
     *
     * @param date 日期
     * @return 年数
     */
    public static int getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 获取日期的月
     *
     * @param date 日期
     * @return 月数
     */
    public static int getMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取日期的日
     *
     * @param date 日期
     * @return 日数
     */
    public static int getDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DATE);
    }

    /**
     * 获取日期的时(24小时制)
     *
     * @param date 日期
     * @return 小时数
     */
    public static int getHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取日期的分种
     *
     * @param date 日期
     * @return 分钟数
     */
    public static int getMinute(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }

    /**
     * 获取日期的秒
     *
     * @param date 日期
     * @return 秒数
     */
    public static int getSecond(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.SECOND);
    }

    /**
     * 获取星期几
     * @param date 日期
     * @return 星期数 星期天是0
     */
    public static int getWeekDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek - 1 == 0 ? 7 : dayOfWeek - 1;
    }

    /**
     * 获取哪一年共有多少周
     *
     * @param year 年数
     * @return 周数
     */
    public static int getMaxWeekNumOfYear(int year) {
        Calendar c = new GregorianCalendar();
        c.set(year, Calendar.DECEMBER, 31, 23, 59, 59);
        return getWeekNumOfYear(c.getTime());
    }

    /**
     * 取得某天是一年中的多少周
     *
     * @param date 日期
     * @return 周数
     */
    public static int getWeekNumOfYear(Date date) {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setMinimalDaysInFirstWeek(7);
        c.setTime(date);
        return c.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 取得某天所在周的第一天
     *
     * @param date 日期
     * @return 最后一天
     */
    public static Date getFirstDayOfWeek(Date date) {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
        return c.getTime();
    }

    /**
     * 取得某天所在周的最后一天
     *
     * @param date 日期
     * @return 最后一天
     */
    public static Date getLastDayOfWeek(Date date) {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 6);
        return c.getTime();
    }

    /**
     * 取得某年某周的第一天 对于交叉:2008-12-29到2009-01-04属于2008年的最后一周,2009-01-05为2009年第一周的第一天
     *
     * @param year 年
     * @param week 周
     * @return 最后一天
     */
    public static Date getFirstDayOfWeek(int year, int week) {
        Calendar calFirst = Calendar.getInstance();
        calFirst.set(year, 0, 7);
        Date firstDate = getFirstDayOfWeek(calFirst.getTime());

        Calendar firstDateCal = Calendar.getInstance();
        firstDateCal.setTime(firstDate);

        Calendar c = new GregorianCalendar();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, Calendar.JANUARY);
        c.set(Calendar.DATE, firstDateCal.get(Calendar.DATE));

        Calendar cal = (GregorianCalendar) c.clone();
        cal.add(Calendar.DATE, (week - 1) * 7);
        firstDate = getFirstDayOfWeek(cal.getTime());

        return firstDate;
    }

    /**
     * 取得某年某周的最后一天 对于交叉:2008-12-29到2009-01-04属于2008年的最后一周, 2009-01-04为 2008年最后一周的最后一天
     *
     * @param year 年
     * @param week 周
     * @return 最后一天
     */
    public static Date getLastDayOfWeek(int year, int week) {
        Calendar calLast = Calendar.getInstance();
        calLast.set(year, 0, 7);
        Date firstDate = getLastDayOfWeek(calLast.getTime());

        Calendar firstDateCal = Calendar.getInstance();
        firstDateCal.setTime(firstDate);

        Calendar c = new GregorianCalendar();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, Calendar.JANUARY);
        c.set(Calendar.DATE, firstDateCal.get(Calendar.DATE));

        Calendar cal = (GregorianCalendar) c.clone();
        cal.add(Calendar.DATE, (week - 1) * 7);
        Date lastDate = getLastDayOfWeek(cal.getTime());

        return lastDate;
    }

    private static Date add(Date date, int calendarField, int amount) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        } else {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(calendarField, amount);
            return c.getTime();
        }
    }

    /*
     * 以下方法中用到的操作说明：
     * 1是对年份操作，
     * 2是对月份操作，
     * 3是对星期操作，
     * 5是对日期操作，
     * 11是对小时操作，
     * 12是对分钟操作，
     * 13是对秒操作，
     * 14是对毫秒操作
     */

    /**
     * 增加年
     *
     * @param date   日期
     * @param amount 年数
     * @return 新日期
     */
    public static Date addYears(Date date, int amount) {
        return add(date, 1, amount);
    }

    /**
     * 增加月
     *
     * @param date   日期
     * @param amount 月数
     * @return 新日期
     */
    public static Date addMonths(Date date, int amount) {
        return add(date, 2, amount);
    }

    /**
     * 增加周
     *
     * @param date   日期
     * @param amount 周数
     * @return 新日期
     */
    public static Date addWeeks(Date date, int amount) {
        return add(date, 3, amount);
    }

    /**
     * 增加天
     *
     * @param date   日期
     * @param amount 天数
     * @return 新日期
     */
    public static Date addDays(Date date, int amount) {
        return add(date, 5, amount);
    }

    /**
     * 增加时
     *
     * @param date   日期
     * @param amount 小时数
     * @return 新日期
     */
    public static Date addHours(Date date, int amount) {
        return add(date, 11, amount);
    }

    /**
     * 增加分
     *
     * @param date   日期
     * @param amount 分钟数
     * @return 新日期
     */
    public static Date addMinutes(Date date, int amount) {
        return add(date, 12, amount);
    }

    /**
     * 增加秒
     *
     * @param date   日期
     * @param amount 秒数
     * @return 新日期
     */
    public static Date addSeconds(Date date, int amount) {
        return add(date, 13, amount);
    }

    /**
     * 增加毫秒
     *
     * @param date   日期1
     * @param amount 日期2
     * @return 差值
     */
    public static Date addMilliseconds(Date date, int amount) {
        return add(date, 14, amount);
    }


    /**
     * 毫秒差
     *
     * @param before 日期1
     * @param after  日期2
     * @return 差值
     */
    public static long diffTimes(Date before, Date after) {
        return after.getTime() - before.getTime();
    }

    /**
     * 秒差
     *
     * @param before 日期1
     * @param after  日期2
     * @return 差值
     */
    public static long diffSecond(Date before, Date after) {
        return (after.getTime() - before.getTime()) / 1000;
    }

    /**
     * 分种差
     *
     * @param before 日期1
     * @param after  日期2
     * @return 差值
     */
    public static int diffMinute(Date before, Date after) {
        return (int) (after.getTime() - before.getTime()) / 1000 / 60;
    }

    /**
     * 时差
     *
     * @param before 日期1
     * @param after  日期2
     * @return 差值
     */
    public static int diffHour(Date before, Date after) {
        return (int) (after.getTime() - before.getTime()) / 1000 / 60 / 60;
    }

    /**
     * 天数差
     *
     * @param before 日期1
     * @param after  日期2
     * @return 差值
     */
    public static int diffDay(Date before, Date after) {
        return Integer.parseInt(String.valueOf(((after.getTime() - before.getTime()) / 86400000)));
    }

    /**
     * 计算时间
     *
     * @param startTime ： 开始时间[未来时间]
     * @param endTime   ： 结束时间[现在及过去时间]
     * @return
     */
    public static int caculateTotalTime(String startTime, String endTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date endDate = null;
        Date startDate = null;
        Long l = 0L;
        try {
            startDate = formatter.parse(startTime);
            long ts = startDate.getTime();
            endDate = formatter.parse(endTime);
            long te = endDate.getTime();

            l = (ts - te) / (1000 * 60 * 60 * 24);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return l.intValue();
    }

    /**
     * 月差
     *
     * @param before 日期1
     * @param after  日期2
     * @return 差值
     */
    public static int diffMonth(Date before, Date after) {
        int monthAll = 0;
        int yearsX = diffYear(before, after);
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(before);
        c2.setTime(after);
        int monthsX = c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);
        monthAll = yearsX * 12 + monthsX;
        int daysX = c2.get(Calendar.DATE) - c1.get(Calendar.DATE);
        if (daysX > 0) {
            monthAll = monthAll + 1;
        }
        return monthAll;
    }

    /**
     * 年差
     *
     * @param before 日期1
     * @param after  日期2
     * @return 差值
     */
    public static int diffYear(Date before, Date after) {
        return getYear(after) - getYear(before);
    }

    /**
     * 设置23:59:59
     *
     * @param date 日期
     * @return 某天最后一秒
     */
    public static Date setEndDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    /**
     * 设置00:00:00
     *
     * @param date 日期
     * @return 某天第一秒
     */
    public static Date setStartDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 00);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        return calendar.getTime();
    }
    
    
    public static Date getYesterday(){  
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE);  
        calendar.set(Calendar.DATE, day-1);  
        return calendar.getTime();
    }  
    
    public static Date getBeforeTwoday(){  
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE);  
        calendar.set(Calendar.DATE, day-2);  
        return calendar.getTime();
    }  
    
    
    public static Date getNowDay(){  
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }
    
    
    
    
    
    public static Date getFirstDayOfMonth(int year,int month) {
        Calendar calendar = Calendar.getInstance();
    	//int month = calendar.get(Calendar.MONTH )+1;
        // 设置月份
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        // 获取某月最小天数
        int firstDay = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
        // 设置日历中月份的最小天数
        calendar.set(Calendar.DAY_OF_MONTH, firstDay);
        calendar.set(Calendar.HOUR_OF_DAY, 00);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        return calendar.getTime();
    }
    
    public static Date   getLastDayOfMonth(int year,int month) {
        Calendar calendar = Calendar.getInstance();
    	//int month = calendar.get(Calendar.MONTH )+1;
        // 设置月份
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        // 获取某月最大天数
        int lastDay=0;
        //2月的平年瑞年天数
        if(month==2) {
            lastDay = calendar.getLeastMaximum(Calendar.DAY_OF_MONTH);
        }else {
            lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        }
        // 设置日历中月份的最大天数
        calendar.set(Calendar.DAY_OF_MONTH, lastDay);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }
    
    
    public static Date getAfterOneDay( ){  
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE);  
        calendar.set(Calendar.DATE, day+1);  
        return calendar.getTime();
    }  
    
    
    public static Date getThreeOneDay( ){  
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE);  
        calendar.set(Calendar.DATE, day+3);  
        return calendar.getTime();
    }  
    
    public static Date getListYear( ){  
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR, -1);
        return calendar.getTime();
    }  
    
    

    /**
     * 到当天00:00:00相差秒
     *
     * @param currentDate
     * @return
     */
    public static Long getRemainSecondsOneDay(Date currentDate) {
        LocalDateTime midnight = LocalDateTime.ofInstant(currentDate.toInstant(),
                ZoneId.systemDefault()).plusDays(1).withHour(0).withMinute(0)
                .withSecond(0).withNano(0);
        LocalDateTime currentDateTime = LocalDateTime.ofInstant(currentDate.toInstant(),
                ZoneId.systemDefault());
        long seconds = ChronoUnit.SECONDS.between(currentDateTime, midnight);
        return seconds;
    }

    /**
     * 功能描述：相差小时数
     *
     * @param start
     * @param end
     * @return
     */
    public static Long betweenDays(Date start, Date end) {
        return ChronoUnit.DAYS.between(dateToLocalDateTime(start), dateToLocalDateTime(end));
    }

    /**
     * 功能描述：相差小时数
     *
     * @param start
     * @param end
     * @return
     */
    public static Long betweenHours(Date start, Date end) {
        return ChronoUnit.HOURS.between(dateToLocalDateTime(start), dateToLocalDateTime(end));
    }

    /**
     * 功能描述：相差分钟数
     *
     * @param start
     * @param end
     * @return
     */
    public static Long betweenMinutes(Date start, Date end) {
        return ChronoUnit.MINUTES.between(dateToLocalDateTime(start), dateToLocalDateTime(end));
    }

    /**
     * 功能描述：相差秒数
     *
     * @param start
     * @param end
     * @return
     */
    public static Long betweenSeconds(Date start, Date end) {
        return ChronoUnit.SECONDS.between(dateToLocalDateTime(start), dateToLocalDateTime(end));
    }

    public static String getPeriod(Date startTime, Date endTime) {
        Period period = Period.between(dateToLocalDate(startTime), dateToLocalDate(endTime));

        StringBuffer sb = new StringBuffer();
        if (period.getYears() > 0) {
            sb.append(period.getYears()).append("岁");
        }
        if (period.getMonths() > 0) {
            sb.append(period.getMonths()).append("个月");
        }
        if (period.getYears() == 0 && period.getMonths() == 0) {
            sb.append("1").append("个月");
        }

        return sb.toString();
    }

    public static LocalDate dateToLocalDate(Date date) {
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        LocalDate localDate = localDateTime.toLocalDate();
        return localDate;
    }

    public static LocalDateTime dateToLocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        return localDateTime;
    }

    /**
     * 获取定时统计的统计起止时间
     * <p>
     *     1.如果传参，代表需要修复指定日期的数据，日期格式：yyyy-MM-dd，e.g.: 2020-10-01
     *     2.未传参，正常执行统计定时任务，则统计昨日全天的数据
     * </p>
     *
     * @param statisticParams   统计时间参数
     * @return  统计起止时间
     * @throws ParseException   日期格式解析异常
     */
    public static Map<String, String> getStatisticTime(String statisticParams) throws ParseException {
        String beginTime;
        String endTime;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (StringUtils.isNotBlank(statisticParams)) {
            // 传参，统计修复指定日期数据
            sdf.setLenient(false);
            sdf.parse(statisticParams);

            beginTime = statisticParams + " 00:00:00";
            endTime = statisticParams + " 23:59:59";
        } else {
            // 统计昨日的数据
            // 获取统计时间
            String yesterday = sdf.format(addDays(new Date(), -1));
            beginTime = yesterday + " 00:00:00";
            endTime = yesterday + " 23:59:59";
        }

        Map<String, String> map = new HashMap<>();
        map.put("beginTime", beginTime);
        map.put("endTime", endTime);
        return map;
    }

    public static void main(String[] args) throws  Exception {

    }

    public static boolean isSameDay(Date d1, Date d2){
        return DateUtils.isSameDay(d1, d2);
    }

    /**
     * 传入两个时间,返回一个从开始日期开始,每次+1天的迭代器
     * 例如:
     * Date date1 = convert2Date("2020-11-01", "yyyy-MM-dd");
     *         Date date2 = convert2Date("2020-11-05", "yyyy-MM-dd");
     *         DateIterator i = dayIterator(date1, date2);
     *         while(i.hasNext()){
     *             Calendar next = i.next();
     *             System.out.println(next.get(Calendar.DATE));
     *         }
     * 输入结果:
     *  1
     *  2
     *  3
     *  4
     *  5
     * @param start 开始时间
     * @param end 结束时间
     * @return 步长为"天"的迭代器
     */
    public static Iterator<Calendar> dayIterator(Date start, Date end){
        Calendar c1 = Calendar.getInstance();
        c1.setTime(start);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(end);
        return new DateIterator(c1, c2);
    }


    static class DateIterator implements Iterator<Calendar> {
        private final Calendar endFinal;
        private final Calendar spot;

        DateIterator(Calendar startFinal, Calendar endFinal) {
            super();
            this.endFinal = endFinal;
            spot = startFinal;
            spot.add(Calendar.DATE, -1);
        }

        public boolean hasNext() {
            return spot.before(endFinal);
        }

        public Calendar next() {
            if (spot.equals(endFinal)) {
                throw new NoSuchElementException();
            }
            spot.add(Calendar.DATE, 1);
            return (Calendar) spot.clone();
        }
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * 返回 mm:ss
     * @param time 秒
     * @return
     */
    public static String formatDuration(int time) {
        int min = time / 60;
        int sec = time  % 60;
        String m = min < 10 ? ("0" + min) : String.valueOf(min);
        return String.format("%s:%02d", m ,sec);
    }

}