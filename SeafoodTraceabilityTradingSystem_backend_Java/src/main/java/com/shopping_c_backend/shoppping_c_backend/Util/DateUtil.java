package com.shopping_c_backend.shoppping_c_backend.Util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    public static String date2String(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static Date string2Date(String dateString, String format) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.parse(dateString);
    }
}

