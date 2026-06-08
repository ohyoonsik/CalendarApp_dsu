package com.example.calendarapp;

import android.content.Context;

import java.util.List;
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
                if (id > 0) {
                    if (listener != null) {
                        listener.onSuccess("일정이 추가되었습니다.");
                    }
                }
            } catch (Exception e) {
                if (listener != null) {
                    listener.onError("오류: " + e.getMessage());
                }
            }
        });
    }
    
    public void updateEvent(Event event, OnEventOperationListener listener) {
        executorService.execute(() -> {
            try {
                int result = eventDao.updateEvent(event);
                if (result > 0) {
                    if (listener != null) {
                        listener.onSuccess("일정이 수정되었습니다.");
                    }
                }
            } catch (Exception e) {
                if (listener != null) {
                    listener.onError("오류: " + e.getMessage());
                }
            }
        });
    }
    
    public void deleteEvent(Event event, OnEventOperationListener listener) {
        executorService.execute(() -> {
            try {
                int result = eventDao.deleteEvent(event);
                if (result > 0) {
                    if (listener != null) {
                        listener.onSuccess("일정이 삭제되었습니다.");
                    }
                }
            } catch (Exception e) {
                if (listener != null) {
                    listener.onError("오류: " + e.getMessage());
                }
            }
        });
    }
    
    public void getEventsByDateRange(long startTime, long endTime, OnEventsLoadListener listener) {
        executorService.execute(() -> {
            try {
                List<Event> events = eventDao.getEventsByDateRange(startTime, endTime);
                if (listener != null) {
                    listener.onEventsLoaded(events);
                }
            } catch (Exception e) {
                if (listener != null) {
                    listener.onError("오류: " + e.getMessage());
                }
            }
        });
    }
    
    public void shutdown() {
        executorService.shutdown();
    }
}
