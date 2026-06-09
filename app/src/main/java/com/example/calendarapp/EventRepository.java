package com.example.calendarapp;

import android.content.Context;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventRepository {

    private final EventDao eventDao;
    private final ExecutorService executorService;

    public interface OnEventOperationListener {
        void onSuccess(String message);
        void onError(String errorMessage);
    }

    public interface OnEventsLoadListener {
        void onEventsLoaded(List<Event> events);
        void onError(String errorMessage);
    }

    public interface OnSingleEventLoadListener {
        void onEventLoaded(Event event);
        void onError(String errorMessage);
    }

    public EventRepository(Context context) {
        EventDatabase database = EventDatabase.getInstance(context);
        this.eventDao = database.eventDao();
        this.executorService = Executors.newFixedThreadPool(2);
    }

    public void insertEvent(Event event, OnEventOperationListener listener) {
        executorService.execute(() -> {
            try {
                long id = eventDao.insertEvent(event);
                if (id > 0 && listener != null) {
                    listener.onSuccess("일정이 추가되었습니다.");
                }
            } catch (Exception e) {
                if (listener != null) listener.onError("오류: " + e.getMessage());
            }
        });
    }

    /**
     * 반복 타입에 따라 여러 인스턴스를 생성하여 저장합니다.
     * repeatType == "none" 이면 단건 저장과 동일합니다.
     */
    public void insertEventWithRepeat(Event event, OnEventOperationListener listener) {
        executorService.execute(() -> {
            try {
                if ("none".equals(event.repeatType) || event.repeatType == null) {
                    long id = eventDao.insertEvent(event);
                    if (id > 0 && listener != null) listener.onSuccess("일정이 추가되었습니다.");
                    return;
                }

                String groupId = UUID.randomUUID().toString();
                event.repeatGroupId = groupId;

                int count = repeatCount(event.repeatType);
                long duration = event.endTime - event.startTime;

                for (int i = 0; i < count; i++) {
                    Event inst = new Event(
                            event.title, event.description, event.location,
                            addInterval(event.startTime, event.repeatType, i),
                            addInterval(event.startTime, event.repeatType, i) + duration
                    );
                    inst.category = event.category;
                    inst.repeatType = event.repeatType;
                    inst.repeatGroupId = groupId;
                    eventDao.insertEvent(inst);
                }

                if (listener != null) listener.onSuccess("반복 일정이 추가되었습니다.");
            } catch (Exception e) {
                if (listener != null) listener.onError("오류: " + e.getMessage());
            }
        });
    }

    private long addInterval(long base, String repeatType, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(base);
        switch (repeatType) {
            case "daily":   cal.add(Calendar.DAY_OF_MONTH, n); break;
            case "weekly":  cal.add(Calendar.WEEK_OF_YEAR, n); break;
            case "monthly": cal.add(Calendar.MONTH, n);        break;
            case "yearly":  cal.add(Calendar.YEAR, n);         break;
        }
        return cal.getTimeInMillis();
    }

    private int repeatCount(String repeatType) {
        switch (repeatType) {
            case "daily":   return 30;
            case "weekly":  return 12;
            case "monthly": return 12;
            case "yearly":  return 3;
            default:        return 1;
        }
    }

    public void updateEvent(Event event, OnEventOperationListener listener) {
        executorService.execute(() -> {
            try {
                event.updatedAt = System.currentTimeMillis();
                int result = eventDao.updateEvent(event);
                if (result > 0 && listener != null) listener.onSuccess("일정이 수정되었습니다.");
            } catch (Exception e) {
                if (listener != null) listener.onError("오류: " + e.getMessage());
            }
        });
    }

    public void deleteEvent(Event event, OnEventOperationListener listener) {
        executorService.execute(() -> {
            try {
                int result = eventDao.deleteEvent(event);
                if (result > 0 && listener != null) listener.onSuccess("일정이 삭제되었습니다.");
            } catch (Exception e) {
                if (listener != null) listener.onError("오류: " + e.getMessage());
            }
        });
    }

    public void deleteEventGroup(String groupId, OnEventOperationListener listener) {
        executorService.execute(() -> {
            try {
                eventDao.deleteByGroupId(groupId);
                if (listener != null) listener.onSuccess("반복 일정이 모두 삭제되었습니다.");
            } catch (Exception e) {
                if (listener != null) listener.onError("오류: " + e.getMessage());
            }
        });
    }

    public void getEventById(int id, OnSingleEventLoadListener listener) {
        executorService.execute(() -> {
            try {
                Event event = eventDao.getEventById(id);
                if (listener != null) listener.onEventLoaded(event);
            } catch (Exception e) {
                if (listener != null) listener.onError("오류: " + e.getMessage());
            }
        });
    }

    public void getEventsByDateRange(long startTime, long endTime, OnEventsLoadListener listener) {
        executorService.execute(() -> {
            try {
                List<Event> events = eventDao.getEventsByDateRange(startTime, endTime);
                if (listener != null) listener.onEventsLoaded(events);
            } catch (Exception e) {
                if (listener != null) listener.onError("오류: " + e.getMessage());
            }
        });
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
