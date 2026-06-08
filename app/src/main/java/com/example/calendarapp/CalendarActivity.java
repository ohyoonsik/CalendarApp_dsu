package com.example.calendarapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarActivity extends AppCompatActivity {
    
    private TextView monthYearTextView;
    private GridView calendarGridView;
    private RecyclerView eventsRecyclerView;
    private Button prevMonthButton;
    private Button nextMonthButton;
    private Button addEventButton;
    
    private CalendarDayAdapter calendarDayAdapter;
    private EventAdapter eventAdapter;
    private EventRepository eventRepository;
    
    private long currentMonthTime;
    private List<Event> eventList = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        
        initializeViews();
        initializeAdapters();
        initializeRepository();
        setupListeners();
        
        currentMonthTime = System.currentTimeMillis();
        displayCalendar();
        displayEvents();
    }
    
    private void initializeViews() {
        monthYearTextView = findViewById(R.id.month_year);
        calendarGridView = findViewById(R.id.calendar_grid);
        eventsRecyclerView = findViewById(R.id.events_recycler);
        prevMonthButton = findViewById(R.id.prev_month_button);
        nextMonthButton = findViewById(R.id.next_month_button);
        addEventButton = findViewById(R.id.add_event_button);
    }
    
    private void initializeAdapters() {
        calendarDayAdapter = new CalendarDayAdapter(this);
        calendarGridView.setAdapter(calendarDayAdapter);
        
        eventAdapter = new EventAdapter();
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventsRecyclerView.setAdapter(eventAdapter);
    }
    
    private void initializeRepository() {
        eventRepository = new EventRepository(this);
    }
    
    private void setupListeners() {
        prevMonthButton.setOnClickListener(v -> {
            currentMonthTime = DateUtils.addMonths(currentMonthTime, -1);
            displayCalendar();
            displayEvents();
        });
        
        nextMonthButton.setOnClickListener(v -> {
            currentMonthTime = DateUtils.addMonths(currentMonthTime, 1);
            displayCalendar();
            displayEvents();
        });
        
        addEventButton.setOnClickListener(v -> {
            Intent intent = new Intent(CalendarActivity.this, EventDetailActivity.class);
            startActivityForResult(intent, 1);
        });
    }
    
    private void displayCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentMonthTime);
        
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        
        String monthName = CalendarUtils.getMonthName(month);
        monthYearTextView.setText(monthName + " " + year);
        
        List<CalendarUtils.CalendarDay> calendarDays = CalendarUtils.getCalendarDays(currentMonthTime);
        calendarDayAdapter.updateCalendarDays(calendarDays);
    }
    
    private void displayEvents() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentMonthTime);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        long monthStart = DateUtils.getStartOfDay(calendar.getTimeInMillis());
        
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        long monthEnd = DateUtils.getEndOfDay(calendar.getTimeInMillis());
        
        eventRepository.getEventsByDateRange(monthStart, monthEnd, new EventRepository.OnEventsLoadListener() {
            @Override
            public void onEventsLoaded(List<Event> events) {
                eventList.clear();
                eventList.addAll(events);
                eventAdapter.updateEvents(eventList);
            }
            
            @Override
            public void onError(String errorMessage) {
                Toast.makeText(CalendarActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            displayEvents();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventRepository.shutdown();
    }
}
