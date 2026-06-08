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
    }
}
