package com.example.calendarapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.List;

public class DayActivity extends AppCompatActivity {

    static final String EXTRA_DAY_TIME = "day_time";

    private TextView tvDate, tvWeekday, tvTodayBadge, labelEvents;
    private ImageButton btnBack, btnAdd;
    private RecyclerView rvTimeline;

    private TimelineAdapter timelineAdapter;
    private EventRepository eventRepository;
    private long dayTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);

        dayTime = getIntent().getLongExtra(EXTRA_DAY_TIME, System.currentTimeMillis());

        tvDate = findViewById(R.id.tv_date);
        tvWeekday = findViewById(R.id.tv_weekday);
        tvTodayBadge = findViewById(R.id.tv_today_badge);
        labelEvents = findViewById(R.id.label_events);
        btnBack = findViewById(R.id.btn_back);
        btnAdd = findViewById(R.id.btn_add);
        rvTimeline = findViewById(R.id.rv_timeline);

        timelineAdapter = new TimelineAdapter(event -> {
            Intent intent = new Intent(DayActivity.this, EventDetailActivity.class);
            intent.putExtra(EventDetailActivity.EXTRA_EVENT_ID, event.id);
            startActivityForResult(intent, 1);
        });
        rvTimeline.setLayoutManager(new LinearLayoutManager(this));
        rvTimeline.setNestedScrollingEnabled(false);
        rvTimeline.setAdapter(timelineAdapter);

        eventRepository = new EventRepository(this);

        btnBack.setOnClickListener(v -> finish());
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(DayActivity.this, EventDetailActivity.class);
            intent.putExtra(EventDetailActivity.EXTRA_DAY_TIME, dayTime);
            startActivityForResult(intent, 1);
        });

        displayHeader();
        loadEvents();
    }

    private void displayHeader() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dayTime);
        tvDate.setText((cal.get(Calendar.MONTH) + 1) + "월 " + cal.get(Calendar.DAY_OF_MONTH) + "일");
        String[] weekdays = {"일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"};
        tvWeekday.setText(weekdays[cal.get(Calendar.DAY_OF_WEEK) - 1]);
        tvTodayBadge.setVisibility(DateUtils.isToday(dayTime) ? View.VISIBLE : View.GONE);
    }

    private void loadEvents() {
        long dayStart = DateUtils.getStartOfDay(dayTime);
        long dayEnd = DateUtils.getEndOfDay(dayTime);

        eventRepository.getEventsByDateRange(dayStart, dayEnd, new EventRepository.OnEventsLoadListener() {
            @Override
            public void onEventsLoaded(List<Event> events) {
                runOnUiThread(() -> {
                    timelineAdapter.updateEvents(events);
                    labelEvents.setText("일정 · " + events.size());
                });
            }
            @Override
            public void onError(String errorMessage) {}
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            loadEvents();
            setResult(RESULT_OK); // 캘린더 화면도 갱신되도록 전파
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventRepository.shutdown();
    }
}
