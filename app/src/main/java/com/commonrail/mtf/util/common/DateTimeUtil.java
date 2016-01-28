/*
 * Copyright (c) 2013 - 2014 Dajiabao
 *
 * This software is the confidential and proprietary information
 * of Dajiabao.
 * You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Dajiabao.
 *
 */
package com.commonrail.mtf.util.common;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtil {

    public final static String longFormat = "yyyy-MM-dd HH:mm:ss";
    public final static String noYearLongFormat = "MM-dd HH:mm:ss";
    public final static String shortFormat = "yyyy-MM-dd";
    public final static String birthdayFormat = "yyyy年MM月dd日 ";
    public final static String withWordLongFormat = "MM月dd日 HH:mm:ss";
    public final static String withWordNoSFormat = "MM月dd日 HH:mm";

    public final static String withFormat = "yyyy-MM-dd HH:mm";
    public final static String withHMFormat = "HH:mm";
    public final static String withYearFormat = "yyyy年MM月dd日 HH:mm";

    public final static String shortFormatHm = "HH:mm:ss";

    /**
     * 取得格式化后的日期时间
     *
     * @param format
     *            格式化类型 如 ：yyyy-MM-dd HH:mm:ss
     * @param date
     *            日期时间 new Date(long time)
     * @return 格式化后的日期数据 如：2011-11-22 11:22:33
     */

    public static String format(String format, Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.CHINA);
        Log.d("TTT", "format = " + format + "result" + dateFormat.format(date));
        return dateFormat.format(date);
    }

    public static Date getDate(String format, String dateStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.CHINA);
        try {
            return dateFormat.parse(dateStr);
        } catch (ParseException ignored) {
        }
        return null;
    }

    public static long getQuot(Date startTime, Date endTime) {
        long quot;
        quot = endTime.getTime() - startTime.getTime();
        quot = quot / 1000 / 60 / 60 / 24;
        return quot;
    }

}
