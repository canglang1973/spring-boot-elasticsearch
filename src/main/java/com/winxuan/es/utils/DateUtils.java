package com.winxuan.es.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author leitao.
 * @category
 * @time: 2018/9/26 0026-13:13
 * @version: 1.0
 * @description:
 **/
public class DateUtils {

    private static String UTC_FORMAT_PATTERN = "yyyy-MM-dd\'T\'HH:mm:ss.SSSZ";
    private static String UTC_TIME_ZONE = "UTC";

    /**
     * 将日期格式化成与Kibana日期格式一致
     * 时区 es默认utc，中国默认cst，差8小时，要转换
     * @param date
     * @return 将CST日期转换成UTC日期字符串
     */
    public static String dateCSTToUTCString(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(UTC_FORMAT_PATTERN);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(UTC_TIME_ZONE));
        String dateStr = simpleDateFormat.format(date);
        return dateStr;
    }

    /**
     * 将UTC日期字符串转换成CST日期
     * @param dateUTCStr
     * @return
     */
    public static Date dateUTCStringToCST(String dateUTCStr){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(UTC_FORMAT_PATTERN);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(UTC_TIME_ZONE));
        Date date = null;
        try {
            date = simpleDateFormat.parse(dateUTCStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

}
