package com.example.xyzreader.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by alfianlosari on 30/01/18.
 */

public class DateUtils {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
    // Use default locale format
    private static SimpleDateFormat outputFormat = new SimpleDateFormat();
    // Most time functions can only handle 1902 - 2037
    private static GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2,1,1);

    public static Date parsePublishedDate(String publishedDate) {
        try {
            return dateFormat.parse(publishedDate);
        } catch (ParseException ex) {
            return new Date();
        }
    }

    public static String formatPublishedDate(String publishedDateString) {
        Date publishedDate = parsePublishedDate(publishedDateString);
        String formattedDate = android.text.format.DateUtils.getRelativeTimeSpanString(
                publishedDate.getTime(),
                System.currentTimeMillis(), android.text.format.DateUtils.HOUR_IN_MILLIS,
                android.text.format.DateUtils.FORMAT_ABBREV_ALL
        ).toString();
        return formattedDate;
    }

}
