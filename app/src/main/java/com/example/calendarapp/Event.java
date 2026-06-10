package com.example.calendarapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "events")
public class Event {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public String description;
    public String location;
    public long startTime;
    public long endTime;
    public long createdAt;
    public long updatedAt;

    // "none" | "work" | "personal" | "health" | "social"
    public String category;

    // "none" | "daily" | "weekly" | "monthly" | "yearly"
    public String repeatType;

    // UUID that groups repeating instances; null for non-repeating events
    public String repeatGroupId;

    // 알림: -1=없음, 0=이벤트시간, 5/10/15/30/60/1440=N분전
    public int alarmOffset = -1;

    public Event() {
    }

    public Event(String title, String description, String location, long startTime, long endTime) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.category = "none";
        this.repeatType = "none";
        this.alarmOffset = -1;
    }
}
