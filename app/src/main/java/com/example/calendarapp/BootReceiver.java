package com.example.calendarapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

/** 기기 재부팅 후 모든 미래 알람을 재등록한다. */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) return;

        PendingResult result = goAsync();
        new Thread(() -> {
            try {
                long now    = System.currentTimeMillis();
                long future = now + 365L * 24 * 60 * 60 * 1000;
                List<Event> events = EventDatabase.getInstance(context)
                        .eventDao().getEventsByDateRange(now, future);
                for (Event event : events) {
                    AlarmScheduler.schedule(context, event);
                }
            } finally {
                result.finish();
            }
        }).start();
    }
}
