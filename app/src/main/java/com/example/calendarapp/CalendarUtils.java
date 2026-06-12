package com.example.calendarapp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarUtils {
    
    public static class CalendarDay {
        public int date;
        public int month;
        public int year;
        public boolean isCurrentMonth;
        
        public CalendarDay(int date, int month, int year, boolean isCurrentMonth) {
            this.date = date;
            this.month = month;
            this.year = year;
            this.isCurrentMonth = isCurrentMonth;
        }
        
        public long getTimeInMillis() {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, date);
            return calendar.getTimeInMillis();
        }
    }
    
   public static List<CalendarDay> getCalendarDays(long timeInMillis) {
    List<CalendarDay> calendarDays = new ArrayList<>();
    
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(timeInMillis);
    
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    
    // 월의 1일 설정
    Calendar firstDayCalendar = Calendar.getInstance();
    firstDayCalendar.set(year, month, 1);
    
    // 월의 1일이 몇 요일인지 확인 (0=일요일, 1=월요일, ..., 6=토요일)
    int firstDayOfWeek = firstDayCalendar.get(Calendar.DAY_OF_WEEK) - 1;
    
    // 이전 달의 마지막 날짜
    Calendar prevCalendar = Calendar.getInstance();
    prevCalendar.set(year, month, 1);
    prevCalendar.add(Calendar.DAY_OF_MONTH, -1);
    
    int lastDayOfPrevMonth = prevCalendar.get(Calendar.DAY_OF_MONTH);
    
    // 이전 달의 날짜 추가
    for (int i = lastDayOfPrevMonth - firstDayOfWeek + 1; i <= lastDayOfPrevMonth; i++) {
        calendarDays.add(new CalendarDay(i, month - 1, year, false));
    }
    
    // 현재 달의 날짜 추가
    int lastDayOfMonth = firstDayCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    for (int i = 1; i <= lastDayOfMonth; i++) {
        calendarDays.add(new CalendarDay(i, month, year, true));
    }
    
    // 다음 달의 날짜 추가 (42칸 채우기)
    int remainingDays = 42 - calendarDays.size();
    for (int i = 1; i <= remainingDays; i++) {
        calendarDays.add(new CalendarDay(i, month + 1, year, false));
    }
    
    return calendarDays;
}

    
    public static String getMonthName(int month) {
        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        return months[month];
    }
}
