package com.example.calendarapp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface EventDao {
    
    @Insert
    long insertEvent(Event event);
    
    @Update
    int updateEvent(Event event);
    
    @Delete
    int deleteEvent(Event event);
    
    @Query("SELECT * FROM events WHERE id = :id")
    Event getEventById(int id);
    
    @Query("SELECT * FROM events ORDER BY startTime ASC")
    List<Event> getAllEvents();
    
    @Query("SELECT * FROM events WHERE startTime >= :startTime AND startTime <= :endTime ORDER BY startTime ASC")
    List<Event> getEventsByDateRange(long startTime, long endTime);
    
    @Query("SELECT * FROM events WHERE title LIKE '%' || :keyword || '%' ORDER BY startTime ASC")
    List<Event> searchEventsByTitle(String keyword);

    @Query("DELETE FROM events WHERE repeatGroupId = :groupId")
    void deleteByGroupId(String groupId);
}
