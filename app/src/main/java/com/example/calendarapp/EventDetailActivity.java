package com.example.calendarapp;

import android.app.AlertDialog;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.Calendar;

public class EventDetailActivity extends AppCompatActivity {

    static final String EXTRA_EVENT_ID = "event_id";
    static final String EXTRA_DAY_TIME = "day_time";

    private TextView tvHeaderTitle;
    private EditText titleEditText;
    private EditText descriptionEditText;
    private EditText locationEditText;
    private TextView startDateTimeTextView;
    private TextView endDateTimeTextView;
    private MaterialButton startDateButton;
    private MaterialButton startTimeButton;
    private MaterialButton endDateButton;
    private MaterialButton endTimeButton;
    private MaterialButton saveButton;
    private MaterialButton cancelButton;
    private MaterialButton deleteButton;

    // 반복 행
    private LinearLayout btnRepeat;
    private TextView tvRepeat;

    // 알림 행
    private LinearLayout btnAlarm;
    private TextView tvAlarm;

    // 카테고리 칩 (커스텀 LinearLayout)
    private LinearLayout chipWork;
    private LinearLayout chipPersonal;
    private LinearLayout chipHealth;
    private LinearLayout chipSocial;

    private EventRepository eventRepository;
    private Event existingEvent;
    private long startTime;
    private long endTime;
    private String selectedCategory = "none";
    private String selectedRepeat = "none";
    private int selectedAlarm = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        initializeViews();
        eventRepository = new EventRepository(this);
        requestNotificationPermission();

        long dayTime = getIntent().getLongExtra(EXTRA_DAY_TIME, -1L);
        if (dayTime != -1L) {
            // 선택된 날짜 + 현재 시각(시/분)으로 초기화
            Calendar now = Calendar.getInstance();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(dayTime);
            cal.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY));
            cal.set(Calendar.MINUTE, now.get(Calendar.MINUTE));
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            startTime = cal.getTimeInMillis();
        } else {
            startTime = System.currentTimeMillis();
        }
        endTime = startTime + 3600000;

        int eventId = getIntent().getIntExtra(EXTRA_EVENT_ID, -1);
        if (eventId != -1) {
            tvHeaderTitle.setText("일정 수정");
            deleteButton.setVisibility(View.VISIBLE);
            loadEvent(eventId);
        } else {
            setupListeners();
            updateDateTimeDisplay();
            updateRepeatDisplay();
            updateAlarmDisplay();
        }
    }

    private void initializeViews() {
        tvHeaderTitle = findViewById(R.id.tv_header_title);
        titleEditText = findViewById(R.id.et_title);
        descriptionEditText = findViewById(R.id.et_memo);
        locationEditText = findViewById(R.id.et_location);
        startDateTimeTextView = findViewById(R.id.tv_start_datetime);
        endDateTimeTextView = findViewById(R.id.tv_end_datetime);
        startDateButton = findViewById(R.id.btn_start_date);
        startTimeButton = findViewById(R.id.btn_start_time);
        endDateButton = findViewById(R.id.btn_end_date);
        endTimeButton = findViewById(R.id.btn_end_time);
        saveButton = findViewById(R.id.btn_save);
        cancelButton = findViewById(R.id.btn_cancel);
        deleteButton = findViewById(R.id.btn_delete);
        btnRepeat = findViewById(R.id.btn_repeat);
        tvRepeat = findViewById(R.id.tv_repeat);
        btnAlarm = findViewById(R.id.btn_alarm);
        tvAlarm = findViewById(R.id.tv_alarm);
        chipWork = findViewById(R.id.chip_work);
        chipPersonal = findViewById(R.id.chip_personal);
        chipHealth = findViewById(R.id.chip_health);
        chipSocial = findViewById(R.id.chip_social);
    }

    private void loadEvent(int eventId) {
        eventRepository.getEventById(eventId, new EventRepository.OnSingleEventLoadListener() {
            @Override
            public void onEventLoaded(Event event) {
                existingEvent = event;
                runOnUiThread(() -> {
                    if (event != null) {
                        startTime = event.startTime;
                        endTime = event.endTime;
                        titleEditText.setText(event.title);
                        descriptionEditText.setText(event.description);
                        locationEditText.setText(event.location);
                        selectedCategory = event.category != null ? event.category : "none";
                        selectedRepeat = event.repeatType != null ? event.repeatType : "none";
                        selectedAlarm = event.alarmOffset;
                        applyCategoryChips();
                    }
                    setupListeners();
                    updateDateTimeDisplay();
                    updateRepeatDisplay();
                    updateAlarmDisplay();
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> setupListeners());
            }
        });
    }

    private void setupListeners() {
        startDateButton.setOnClickListener(v -> showStartDatePicker());
        startTimeButton.setOnClickListener(v -> showStartTimePicker());
        endDateButton.setOnClickListener(v -> showEndDatePicker());
        endTimeButton.setOnClickListener(v -> showEndTimePicker());
        saveButton.setOnClickListener(v -> saveEvent());
        cancelButton.setOnClickListener(v -> finish());
        deleteButton.setOnClickListener(v -> deleteEvent());

        btnRepeat.setOnClickListener(v -> showRepeatDialog());
        btnAlarm.setOnClickListener(v -> showAlarmDialog());

        chipWork.setOnClickListener(v -> selectCategory("work"));
        chipPersonal.setOnClickListener(v -> selectCategory("personal"));
        chipHealth.setOnClickListener(v -> selectCategory("health"));
        chipSocial.setOnClickListener(v -> selectCategory("social"));
    }

    // ── 카테고리 ────────────────────────────────────────────────────────────────

    private void selectCategory(String category) {
        if (category.equals(selectedCategory)) {
            selectedCategory = "none";
        } else {
            selectedCategory = category;
        }
        applyCategoryChips();
    }

    private void applyCategoryChips() {
        applyChipState(chipWork,     "work",     getColor(R.color.cat_work));
        applyChipState(chipPersonal, "personal", getColor(R.color.cat_personal));
        applyChipState(chipHealth,   "health",   getColor(R.color.cat_health));
        applyChipState(chipSocial,   "social",   getColor(R.color.cat_social));
    }

    private void applyChipState(LinearLayout chip, String category, int catColor) {
        View dot = chip.getChildAt(0);
        TextView label = (TextView) chip.getChildAt(1);
        if (category.equals(selectedCategory)) {
            chip.setBackgroundResource(R.drawable.bg_pill);
            chip.setBackgroundTintList(ColorStateList.valueOf(catColor));
            dot.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            label.setTextColor(Color.WHITE);
        } else {
            chip.setBackgroundResource(R.drawable.bg_pill_outline);
            chip.setBackgroundTintList(null);
            dot.setBackgroundTintList(ColorStateList.valueOf(catColor));
            label.setTextColor(getColor(R.color.ink_soft));
        }
    }

    // ── 반복 ────────────────────────────────────────────────────────────────────

    private void showRepeatDialog() {
        String[] labels = {"안 함", "매일", "매주", "매월", "매년"};
        String[] values = {"none", "daily", "weekly", "monthly", "yearly"};

        int current = 0;
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(selectedRepeat)) { current = i; break; }
        }

        new AlertDialog.Builder(this)
                .setTitle("반복")
                .setSingleChoiceItems(labels, current, (d, which) -> {
                    selectedRepeat = values[which];
                    updateRepeatDisplay();
                    d.dismiss();
                })
                .setNegativeButton("취소", null)
                .show();
    }

    private void updateRepeatDisplay() {
        switch (selectedRepeat) {
            case "daily":   tvRepeat.setText("매일");  break;
            case "weekly":  tvRepeat.setText("매주");  break;
            case "monthly": tvRepeat.setText("매월");  break;
            case "yearly":  tvRepeat.setText("매년");  break;
            default:        tvRepeat.setText("안 함"); break;
        }
        int color = "none".equals(selectedRepeat)
                ? getColor(R.color.ink_faint)
                : getColor(R.color.ink);
        tvRepeat.setTextColor(color);
    }

    // ── 알림 ────────────────────────────────────────────────────────────────────

    private static final String[] ALARM_LABELS = {"없음", "이벤트 시간", "5분 전", "10분 전", "15분 전", "30분 전", "1시간 전", "1일 전"};
    private static final int[]    ALARM_VALUES = {-1,    0,            5,       10,       15,       30,       60,        1440};

    private void showAlarmDialog() {
        int current = 0;
        for (int i = 0; i < ALARM_VALUES.length; i++) {
            if (ALARM_VALUES[i] == selectedAlarm) { current = i; break; }
        }

        new AlertDialog.Builder(this)
                .setTitle("알림")
                .setSingleChoiceItems(ALARM_LABELS, current, (d, which) -> {
                    selectedAlarm = ALARM_VALUES[which];
                    updateAlarmDisplay();
                    d.dismiss();
                })
                .setNegativeButton("취소", null)
                .show();
    }

    private void updateAlarmDisplay() {
        for (int i = 0; i < ALARM_VALUES.length; i++) {
            if (ALARM_VALUES[i] == selectedAlarm) {
                tvAlarm.setText(ALARM_LABELS[i]);
                break;
            }
        }
        int color = selectedAlarm == -1
                ? getColor(R.color.ink_faint)
                : getColor(R.color.ink);
        tvAlarm.setTextColor(color);
    }

    // ── 날짜/시간 피커 ───────────────────────────────────────────────────────────

    private void showStartDatePicker() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(startTime);
        new DatePickerDialog(this,
                (v, year, month, day) -> {
                    Calendar nc = Calendar.getInstance();
                    nc.setTimeInMillis(startTime);
                    nc.set(year, month, day);
                    startTime = nc.getTimeInMillis();
                    updateDateTimeDisplay();
                },
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void showStartTimePicker() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(startTime);
        new TimePickerDialog(this,
                (v, hour, minute) -> {
                    Calendar nc = Calendar.getInstance();
                    nc.setTimeInMillis(startTime);
                    nc.set(Calendar.HOUR_OF_DAY, hour);
                    nc.set(Calendar.MINUTE, minute);
                    startTime = nc.getTimeInMillis();
                    updateDateTimeDisplay();
                },
                cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true
        ).show();
    }

    private void showEndDatePicker() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(endTime);
        new DatePickerDialog(this,
                (v, year, month, day) -> {
                    Calendar nc = Calendar.getInstance();
                    nc.setTimeInMillis(endTime);
                    nc.set(year, month, day);
                    endTime = nc.getTimeInMillis();
                    updateDateTimeDisplay();
                },
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void showEndTimePicker() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(endTime);
        new TimePickerDialog(this,
                (v, hour, minute) -> {
                    Calendar nc = Calendar.getInstance();
                    nc.setTimeInMillis(endTime);
                    nc.set(Calendar.HOUR_OF_DAY, hour);
                    nc.set(Calendar.MINUTE, minute);
                    endTime = nc.getTimeInMillis();
                    updateDateTimeDisplay();
                },
                cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true
        ).show();
    }

    private void updateDateTimeDisplay() {
        startDateTimeTextView.setText(DateUtils.formatDateTime(startTime));
        endDateTimeTextView.setText(DateUtils.formatDateTime(endTime));
    }

    // ── 저장/삭제 ────────────────────────────────────────────────────────────────

    private void saveEvent() {
        // 알림 설정 시 권한 확인
        if (selectedAlarm >= 0 && !checkAlarmPermissions()) return;

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

        if (existingEvent != null) {
            existingEvent.title = title;
            existingEvent.description = description;
            existingEvent.location = location;
            existingEvent.startTime = startTime;
            existingEvent.endTime = endTime;
            existingEvent.category = selectedCategory;
            existingEvent.repeatType = selectedRepeat;
            existingEvent.alarmOffset = selectedAlarm;
            eventRepository.updateEvent(existingEvent, new EventRepository.OnEventOperationListener() {
                @Override
                public void onSuccess(String message) {
                    runOnUiThread(() -> {
                        Toast.makeText(EventDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    });
                }
                @Override
                public void onError(String errorMessage) {
                    runOnUiThread(() -> Toast.makeText(EventDetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show());
                }
            });
        } else {
            Event event = new Event(title, description, location, startTime, endTime);
            event.category = selectedCategory;
            event.repeatType = selectedRepeat;
            event.alarmOffset = selectedAlarm;
            eventRepository.insertEventWithRepeat(event, new EventRepository.OnEventOperationListener() {
                @Override
                public void onSuccess(String message) {
                    runOnUiThread(() -> {
                        Toast.makeText(EventDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
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

    private void deleteEvent() {
        if (existingEvent == null) return;
        boolean hasGroup = existingEvent.repeatGroupId != null
                && !existingEvent.repeatGroupId.isEmpty();

        if (hasGroup) {
            new AlertDialog.Builder(this)
                    .setTitle("반복 일정 삭제")
                    .setMessage("이 일정만 삭제하거나, 모든 반복 일정을 삭제할 수 있습니다.")
                    .setPositiveButton("모두 삭제", (d, w) ->
                            eventRepository.deleteEventGroup(existingEvent.repeatGroupId, makeDeleteListener()))
                    .setNeutralButton("이 일정만", (d, w) ->
                            eventRepository.deleteEvent(existingEvent, makeDeleteListener()))
                    .setNegativeButton("취소", null)
                    .show();
        } else {
            eventRepository.deleteEvent(existingEvent, makeDeleteListener());
        }
    }

    private EventRepository.OnEventOperationListener makeDeleteListener() {
        return new EventRepository.OnEventOperationListener() {
            @Override
            public void onSuccess(String message) {
                runOnUiThread(() -> {
                    Toast.makeText(EventDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                });
            }
            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> Toast.makeText(EventDetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show());
            }
        };
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 100);
            }
        }
    }

    /**
     * 알람 관련 권한 상태를 확인하고, 부족하면 안내 다이얼로그를 표시한다.
     * @return 모든 권한이 충족되면 true
     */
    private boolean checkAlarmPermissions() {
        // 1) 정확한 알람 권한 (Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (!am.canScheduleExactAlarms()) {
                new AlertDialog.Builder(this)
                        .setTitle("알림 권한 필요")
                        .setMessage("정확한 알림을 받으려면 '알람 및 리마인더' 권한이 필요합니다.\n설정으로 이동하시겠어요?")
                        .setPositiveButton("설정으로", (d, w) -> {
                            Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                            startActivity(intent);
                        })
                        .setNegativeButton("취소", null)
                        .show();
                return false;
            }
        }

        // 2) 알림 표시 권한 (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                new AlertDialog.Builder(this)
                        .setTitle("알림 권한 필요")
                        .setMessage("알림을 표시하려면 알림 권한이 필요합니다.")
                        .setPositiveButton("권한 요청", (d, w) ->
                                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 100))
                        .setNegativeButton("취소", null)
                        .show();
                return false;
            }
        }

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventRepository.shutdown();
    }
}
