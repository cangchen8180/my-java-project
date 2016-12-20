package com.jimi.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 日期工具类
 * Created by jimi on 15-7-29.
 */
public class DateUtil {

    public static class DatePattern{
        private static String DEFULT_WHOLE_DATE = "yyyy-MM-dd HH:mm:ss";
        private static String DEFULT_YEAR_DATE = "yyyy-MM-dd";
        private static String ISO_UTC = "yyyy-MM-dd'T'HH:mm:ssZ";
    }

    /**
     * 获取当前时间
     * @return
     */
    public static Date getNowDate(){
        return new Date();
    }

    /**
     * 获取ISO标准格式时间
     * @return
     */
    public static String getNowDateASISO(){
        SimpleDateFormat dateFormat = new SimpleDateFormat(DatePattern.ISO_UTC);
        String format = dateFormat.format(new Date());
        return format;
    }

    // 获得当天0点时间
    public static Date getCurrentDayStartTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    // 获得当天24点时间
    public static Date getCurrentDayEndTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    // 获得昨天0点时间
    public static Date getYesterdaymorning() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(getCurrentDayStartTime().getTime() - 3600 * 24 * 1000);
        return cal.getTime();
    }

    // 获得当天近7天时间
    public static Date getWeekFromNow() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(getCurrentDayStartTime().getTime() - 3600 * 24 * 1000 * 7);
        return cal.getTime();
    }

    // 获得本周一0点时间
    public static Date getCurrentWeekStartTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return cal.getTime();
    }

    // 获得本周日24点时间
    public static Date getCurrentWeekEndTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getCurrentWeekStartTime());
        cal.add(Calendar.DAY_OF_WEEK, 7);
        return cal.getTime();
    }

    // 获得本月第一天0点时间
    public static Date getCurrentMonthStartTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    // 获得本月最后一天24点时间
    public static Date getCurrentMonthEndTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 24);
        return cal.getTime();
    }

    /**
     * 上个月开始时间
     * @return
     */
    public static Date getLastMonthStartMorning() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getCurrentMonthStartTime());
        cal.add(Calendar.MONTH, -1);
        return cal.getTime();
    }

    /**
     * 本季度开始时间
     * @return
     */
    public static Date getCurrentQuarterStartTime() {
        Calendar c = Calendar.getInstance();
        int currentMonth = c.get(Calendar.MONTH) + 1;
        SimpleDateFormat longSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat shortSdf = new SimpleDateFormat("yyyy-MM-dd");
        Date now = null;
        try {
            if (currentMonth >= 1 && currentMonth <= 3)
                c.set(Calendar.MONTH, 0);
            else if (currentMonth >= 4 && currentMonth <= 6)
                c.set(Calendar.MONTH, 3);
            else if (currentMonth >= 7 && currentMonth <= 9)
                c.set(Calendar.MONTH, 4);
            else if (currentMonth >= 10 && currentMonth <= 12)
                c.set(Calendar.MONTH, 9);
            c.set(Calendar.DATE, 1);
            now = longSdf.parse(shortSdf.format(c.getTime()) + " 00:00:00");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }

    /**
     * 当前季度的结束时间，即2012-03-31 23:59:59
     *
     * @return
     */
    public static Date getCurrentQuarterEndTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getCurrentQuarterStartTime());
        cal.add(Calendar.MONTH, 3);
        return cal.getTime();
    }

    /**
     * 本年开始时间
     * @return
     */
    public static Date getCurrentYearStartTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), 0, 1, 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.YEAR));
        return cal.getTime();
    }

    /**
     * 本年结束时间
     * @return
     */
    public static Date getCurrentYearEndTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getCurrentYearStartTime());
        cal.add(Calendar.YEAR, 1);
        return cal.getTime();
    }

    /**
     * 去年开始时间
     * @return
     */
    public static Date getLastYearStartTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getCurrentYearStartTime());
        cal.add(Calendar.YEAR, -1);
        return cal.getTime();
    }

    /**
     * 增加n天，返回制定格式字符串
     * @param s
     * @param n
     * @return
     */
    public static String addDay(String s, int n, String parseFormat) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(parseFormat);

            Calendar cd = Calendar.getInstance();
            cd.setTime(sdf.parse(s));
            cd.add(Calendar.DATE, n);//增加一天
            //cd.add(Calendar.MONTH, n);//增加一个月
            return sdf.format(cd.getTime());

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 增加n天，返回制定格式字符串
     * @param s
     * @param n
     * @return
     */
    public static String subDay(String s, int n, String parseFormat) {
        try {
            SimpleDateFormat dft = new SimpleDateFormat(parseFormat);
            Calendar cd = Calendar.getInstance();
            cd.setTime(dft.parse(s));
            cd.set(Calendar.DATE, cd.get(Calendar.DATE) - n);
            return dft.format(cd.getTime());
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * 字符串转换为日期类型时间
     * @param dateTimeString
     * @param pattern
     * @return
     */
    public static Date string2dateFormate(String dateTimeString, String pattern) throws ParseException {
        if(dateTimeString == null){return null;}
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.parse(dateTimeString);
    }
    /**
     * 日期类型转换字符串为时间
     * @param dateTime
     * @param pattern
     * @return
     */
    public static String date2stringFormate(Date dateTime, String pattern){
        if(dateTime == null){return null;}
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(dateTime);
    }

    /**
     * 当天0:0:0点时刻的日期
     * @return
     */
    public static Date dateOfStartDay(Date inputDate){
        Calendar cal = Calendar.getInstance();
        cal.setTime(inputDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return  cal.getTime();
    }

    /**
     * 当天23:59:59点时刻的日期
     * @return
     */
    public static Date dateOfEndDay(Date inputDate){
        Calendar cal = Calendar.getInstance();
        cal.setTime(inputDate);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return  cal.getTime();
    }

    /**
     * 两个日期是否为同一天
     * @return
     */
    public static boolean isSameDay(Date d1,Date d2){
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(d1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(d2);

        boolean year = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) ;
        boolean month = cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) ;
        boolean day = cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH) ;
        //同年同月同日
        return year && month && day;
    }


//    public static void main(String[] args) {
//        isSameDay(new Date(), new Date());
//    }
}
