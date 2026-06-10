package com.example.calendarapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class AlarmScheduler {

    /** 알람 예약. alarmOffset == -1 이면 아무것도 하지 않음. */
    static void schedule(Context context, Event event) {
        if (event.alarmOffset < 0) return;

        long triggerAt = event.startTime - (long) event.alarmOffset * 60_000;
        if (triggerAt <= System.currentTimeMillis()) return; // 이미 지난 시각

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !am.canScheduleExactAlarms()) return;

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(AlarmReceiver.EXTRA_TITLE, event.title);
        intent.putExtra(AlarmReceiver.EXTRA_EVENT_ID, event.id);

        PendingIntent pi = PendingIntent.getBroadcast(context, event.id, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi);
    }

    /** 예약된 알람 취소. */
    static void cancel(Context context, int eventId) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, eventId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        am.cancel(pi);
    }
}
