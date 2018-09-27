package com.winxuan.es.utils;

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

    /**
     * 将日期格式化成与Kibana日期格式一致
     * @param date
     * @return
     */
    public static String dateToString(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ss.SSSZ");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String dateStr = simpleDateFormat.format(date);
        return dateStr;
    }

}
