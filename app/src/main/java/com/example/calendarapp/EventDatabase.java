package com.example.calendarapp;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Event.class}, version = 1, exportSchema = false)
public abstract class EventDatabase extends RoomDatabase {
    
    public abstract EventDao eventDao();
    
    private static volatile EventDatabase instance;
    
    public static EventDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (EventDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            EventDatabase.class,
                            "calendar_database"
                    ).build();
                }
            }
        }
        return instance;
    }
}
