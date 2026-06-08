package com.example.calendarapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class EventDetailActivity extends AppCompatActivity {
    
    private EditText titleEditText;
    private EditText descriptionEditText;
    private EditText locationEditText;
    private TextView startDateTimeTextView;
    private TextView endDateTimeTextView;
    private Button startDateButton;
    private Button startTimeButton;
    private Button endDateButton;
    private Button endTimeButton;
    private Button saveButton;
    private Button cancelButton;
    
    private EventRepository eventRepository;
    private Event event;
    private long startTime;
    private long endTime;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        
        initializeViews();
        initializeRepository();
        
        int eventId = getIntent().getIntExtra("EVENT_ID", -1);
        if (eventId != -1) {
            loadEventData(eventId);
        } else {
            startTime = System.currentTimeMillis();
            endTime = startTime + 3600000;
            updateDateTimeDisplay();
        }
        
        setupListeners();
    }
    
    private void loadEventData(int eventId) {
        eventRepository.getEventById(eventId, new EventRepository.OnSingleEventLoadListener() {
            @Override
            public void onEventLoaded(Event loadedEvent) {
                event = loadedEvent;
                startTime = event.startTime;
                endTime = event.endTime;
                
                runOnUiThread(() -> {
                    titleEditText.setText(event.title);
                    descriptionEditText.setText(event.description);
                    locationEditText.setText(event.location);
                    updateDateTimeDisplay();
                    invalidateOptionsMenu(); // 메뉴 다시 그리기 (삭제 버튼 표시용)
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> Toast.makeText(EventDetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show());
            }
        });
    }
    
    private void initializeViews() {
        titleEditText = findViewById(R.id.event_title_edit);
        descriptionEditText = findViewById(R.id.event_description_edit);
        locationEditText = findViewById(R.id.event_location_edit);
        startDateTimeTextView = findViewById(R.id.start_datetime_text);
        endDateTimeTextView = findViewById(R.id.end_datetime_text);
        startDateButton = findViewById(R.id.start_date_button);
        startTimeButton = findViewById(R.id.start_time_button);
        endDateButton = findViewById(R.id.end_date_button);
        endTimeButton = findViewById(R.id.end_time_button);
        saveButton = findViewById(R.id.save_button);
        cancelButton = findViewById(R.id.cancel_button);
    }
    
    private void initializeRepository() {
        eventRepository = new EventRepository(this);
    }
    
    private void setupListeners() {
        startDateButton.setOnClickListener(v -> showStartDatePicker());
        startTimeButton.setOnClickListener(v -> showStartTimePicker());
        endDateButton.setOnClickListener(v -> showEndDatePicker());
        endTimeButton.setOnClickListener(v -> showEndTimePicker());
        
        saveButton.setOnClickListener(v -> saveEvent());
        cancelButton.setOnClickListener(v -> finish());
    }
    
    private void showStartDatePicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTime);
        
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar newCalendar = Calendar.getInstance();
                    newCalendar.setTimeInMillis(startTime);
                    newCalendar.set(year, month, dayOfMonth);
                    startTime = newCalendar.getTimeInMillis();
                    
                    if (startTime >= endTime) {
                        endTime = startTime + 3600000;
                    }
                    updateDateTimeDisplay();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }
    
    private void showStartTimePicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTime);
        
        TimePickerDialog dialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    Calendar newCalendar = Calendar.getInstance();
                    newCalendar.setTimeInMillis(startTime);
                    newCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    newCalendar.set(Calendar.MINUTE, minute);
                    startTime = newCalendar.getTimeInMillis();
                    
                    if (startTime >= endTime) {
                        endTime = startTime + 3600000;
                    }
                    updateDateTimeDisplay();
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );
        dialog.show();
    }
    
    private void showEndDatePicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(endTime);
        
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar newCalendar = Calendar.getInstance();
                    newCalendar.setTimeInMillis(endTime);
                    newCalendar.set(year, month, dayOfMonth);
                    endTime = newCalendar.getTimeInMillis();
                    updateDateTimeDisplay();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }
    
    private void showEndTimePicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(endTime);
        
        TimePickerDialog dialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    Calendar newCalendar = Calendar.getInstance();
                    newCalendar.setTimeInMillis(endTime);
                    newCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    newCalendar.set(Calendar.MINUTE, minute);
                    endTime = newCalendar.getTimeInMillis();
                    updateDateTimeDisplay();
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );
        dialog.show();
    }
    
    private void updateDateTimeDisplay() {
        startDateTimeTextView.setText(DateUtils.formatDateTime(startTime));
        endDateTimeTextView.setText(DateUtils.formatDateTime(endTime));
    }
    
    private void saveEvent() {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String location = locationEditText.getText().toString().trim();
        
        if (title.isEmpty()) {
            Toast.makeText(this, "제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (startTime >= endTime) {
            Toast.makeText(this, "시작 시간이 종료 시간보다 빨라야 합니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (event == null) {
            event = new Event(title, description, location, startTime, endTime);
            eventRepository.insertEvent(event, new EventRepository.OnEventOperationListener() {
                @Override
                public void onSuccess(String message) {
                    runOnUiThread(() -> {
                        Toast.makeText(EventDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        
                        Intent intent = new Intent(EventDetailActivity.this, CalendarActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                    });
                }
                
                @Override
                public void onError(String errorMessage) {
                    runOnUiThread(() -> Toast.makeText(EventDetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show());
                }
            });
        } else {
            event.title = title;
            event.description = description;
            event.location = location;
            event.startTime = startTime;
            event.endTime = endTime;
            event.updatedAt = System.currentTimeMillis();
            
            eventRepository.updateEvent(event, new EventRepository.OnEventOperationListener() {
                @Override
                public void onSuccess(String message) {
                    runOnUiThread(() -> {
                        Toast.makeText(EventDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        
                        Intent intent = new Intent(EventDetailActivity.this, CalendarActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                    });
                }
                
                @Override
                public void onError(String errorMessage) {
                    runOnUiThread(() -> Toast.makeText(EventDetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show());
                }
            });
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventRepository.shutdown();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (event != null) {
            getMenuInflater().inflate(R.menu.event_detail_menu, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            showDeleteConfirmationDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        new android.app.AlertDialog.Builder(this)
                .setTitle("일정 삭제")
                .setMessage("정말 이 일정을 삭제하시겠습니까?")
                .setPositiveButton("삭제", (dialog, which) -> deleteEvent())
                .setNegativeButton("취소", null)
                .show();
    }

    private void deleteEvent() {
        if (event != null) {
            eventRepository.deleteEvent(event, new EventRepository.OnEventOperationListener() {
                @Override
                public void onSuccess(String message) {
                    runOnUiThread(() -> {
                        Toast.makeText(EventDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        
                        Intent intent = new Intent(EventDetailActivity.this, CalendarActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                    });
                }

                @Override
                public void onError(String errorMessage) {
                    runOnUiThread(() -> Toast.makeText(EventDetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show());
                }
            });
        }
    }
}
