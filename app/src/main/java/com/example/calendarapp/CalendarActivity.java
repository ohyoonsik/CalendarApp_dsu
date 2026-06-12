package com.example.calendarapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CalendarActivity extends AppCompatActivity {

    private TextView tvMonth, tvYear, tvAgendaDate, tvTodayBadge, tvAgendaCount;
    private RecyclerView rvCalendar, rvAgenda;
    private ImageButton btnPrev, btnNext, btnTheme;

    private CalendarDayAdapter calendarDayAdapter;
    private EventAdapter eventAdapter;
    private EventRepository eventRepository;

    private long currentMonthTime;
    private long selectedDayTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        currentMonthTime = System.currentTimeMillis();
        selectedDayTime = System.currentTimeMillis();

        initViews();
        initAdapters();
        eventRepository = new EventRepository(this);
        setupListeners();

        displayCalendar();
        displayAgenda(selectedDayTime);
    }

    private void initViews() {
        tvMonth = findViewById(R.id.tv_month);
        tvYear = findViewById(R.id.tv_year);
        tvAgendaDate = findViewById(R.id.tv_agenda_date);
        tvTodayBadge = findViewById(R.id.tv_today_badge);
        tvAgendaCount = findViewById(R.id.tv_agenda_count);
        rvCalendar = findViewById(R.id.rv_calendar);
        rvAgenda = findViewById(R.id.rv_agenda);
        btnPrev = findViewById(R.id.btn_prev);
        btnNext = findViewById(R.id.btn_next);
        btnTheme = findViewById(R.id.btn_theme);
    }

    private void initAdapters() {
        calendarDayAdapter = new CalendarDayAdapter(day -> {
            selectedDayTime = day.getTimeInMillis();
            calendarDayAdapter.setSelectedDay(selectedDayTime);
            displayAgenda(selectedDayTime);
        });
        rvCalendar.setLayoutManager(new GridLayoutManager(this, 7));
        rvCalendar.setAdapter(calendarDayAdapter);

        eventAdapter = new EventAdapter(event -> {
            Intent intent = new Intent(CalendarActivity.this, EventDetailActivity.class);
            intent.putExtra(EventDetailActivity.EXTRA_EVENT_ID, event.id);
            startActivityForResult(intent, 1);
        });
        rvAgenda.setLayoutManager(new LinearLayoutManager(this));
        rvAgenda.setAdapter(eventAdapter);
    }

    private void setupListeners() {
        updateThemeIcon();
        btnTheme.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("theme_prefs", MODE_PRIVATE);
            boolean isDarkMode = prefs.getBoolean("is_dark_mode", false);
            isDarkMode = !isDarkMode;
            prefs.edit().putBoolean("is_dark_mode", isDarkMode).apply();

            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            updateThemeIcon();
        });

        btnPrev.setOnClickListener(v -> {
            currentMonthTime = DateUtils.addMonths(currentMonthTime, -1);
            displayCalendar();
        });

        btnNext.setOnClickListener(v -> {
            currentMonthTime = DateUtils.addMonths(currentMonthTime, 1);
            displayCalendar();
        });

        View btnToday = findViewById(R.id.btn_today);
        btnToday.setOnClickListener(v -> {
            currentMonthTime = System.currentTimeMillis();
            selectedDayTime = System.currentTimeMillis();
            calendarDayAdapter.setSelectedDay(selectedDayTime);
            displayCalendar();
            displayAgenda(selectedDayTime);
        });

        View btnAdd = findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(CalendarActivity.this, EventDetailActivity.class);
            intent.putExtra(EventDetailActivity.EXTRA_DAY_TIME, selectedDayTime);
            startActivityForResult(intent, 1);
        });

        View agendaHeader = findViewById(R.id.agenda_header);
        agendaHeader.setOnClickListener(v -> {
            Intent intent = new Intent(CalendarActivity.this, DayActivity.class);
            intent.putExtra(DayActivity.EXTRA_DAY_TIME, selectedDayTime);
            startActivityForResult(intent, 2);
        });
    }

    private void displayCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(currentMonthTime);
        tvMonth.setText((cal.get(Calendar.MONTH) + 1) + "월");
        tvYear.setText(String.valueOf(cal.get(Calendar.YEAR)));

        List<CalendarUtils.CalendarDay> days = CalendarUtils.getCalendarDays(currentMonthTime);
        calendarDayAdapter.updateCalendarDays(days);
        calendarDayAdapter.setSelectedDay(selectedDayTime);

        cal.set(Calendar.DAY_OF_MONTH, 1);
        long monthStart = DateUtils.getStartOfDay(cal.getTimeInMillis());
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        long monthEnd = DateUtils.getEndOfDay(cal.getTimeInMillis());

        eventRepository.getEventsByDateRange(monthStart, monthEnd, new EventRepository.OnEventsLoadListener() {
            @Override
            public void onEventsLoaded(List<Event> events) {
                Set<String> daysWithEvents = new HashSet<>();
                for (Event e : events) {
                    daysWithEvents.add(DateUtils.formatDate(e.startTime));
                }
                runOnUiThread(() -> calendarDayAdapter.setDaysWithEvents(daysWithEvents));
            }

            @Override
            public void onError(String errorMessage) {}
        });
    }

    private void displayAgenda(long dayTime) {
        tvAgendaDate.setText(formatAgendaDate(dayTime));
        tvTodayBadge.setVisibility(DateUtils.isToday(dayTime) ? View.VISIBLE : View.GONE);

        long dayStart = DateUtils.getStartOfDay(dayTime);
        long dayEnd = DateUtils.getEndOfDay(dayTime);

        eventRepository.getEventsByDateRange(dayStart, dayEnd, new EventRepository.OnEventsLoadListener() {
            @Override
            public void onEventsLoaded(List<Event> events) {
                runOnUiThread(() -> {
                    eventAdapter.updateEvents(events);
                    tvAgendaCount.setText(events.size() + "건");
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() ->
                    Toast.makeText(CalendarActivity.this, errorMessage, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private String formatAgendaDate(long timeInMillis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeInMillis);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        String[] weekdays = {"일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"};
        String weekday = weekdays[cal.get(Calendar.DAY_OF_WEEK) - 1];
        return month + "월 " + day + "일 " + weekday;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && (requestCode == 1 || requestCode == 2)) {
            displayCalendar();
            displayAgenda(selectedDayTime);
        }
    }

    private void updateThemeIcon() {
        SharedPreferences prefs = getSharedPreferences("theme_prefs", MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("is_dark_mode", false);
        if (isDarkMode) {
            btnTheme.setImageResource(R.drawable.ic_sun);
        } else {
            btnTheme.setImageResource(R.drawable.ic_moon);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventRepository.shutdown();
    }
}
