package com.example.calendarapp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    
    public static String formatDate(long timeInMillis) {
        return dateFormat.format(new Date(timeInMillis));
    }
    
    public static String formatTime(long timeInMillis) {
        return timeFormat.format(new Date(timeInMillis));
    }
    
    public static String formatDateTime(long timeInMillis) {
        return dateTimeFormat.format(new Date(timeInMillis));
    }
    
    public static long getStartOfDay(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
    
    public static long getEndOfDay(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }
    
    public static long addDays(long timeInMillis, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar.getTimeInMillis();
    }
    
    public static long addMonths(long timeInMillis, int months) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        calendar.add(Calendar.MONTH, months);
        return calendar.getTimeInMillis();
    }
    
    public static boolean isSameDay(long time1, long time2) {
        return formatDate(time1).equals(formatDate(time2));
    }
    
    public static boolean isToday(long timeInMillis) {
        return isSameDay(timeInMillis, System.currentTimeMillis());
    }
    
    public static int getDayOfMonth(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }
    
    public static int getMonth(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        return calendar.get(Calendar.MONTH);
    }
    
    public static int getYear(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        return calendar.get(Calendar.YEAR);
    }
}
