package com.example.calendarapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CalendarDayAdapter extends RecyclerView.Adapter<CalendarDayAdapter.DayViewHolder> {

    public interface OnDayClickListener {
        void onDayClick(CalendarUtils.CalendarDay day);
    }

    private List<CalendarUtils.CalendarDay> calendarDays = new ArrayList<>();
    private Set<String> daysWithEvents = new HashSet<>();
    private long selectedDayTime = System.currentTimeMillis();
    private final OnDayClickListener listener;

    public CalendarDayAdapter(OnDayClickListener listener) {
        this.listener = listener;
    }

    public void updateCalendarDays(List<CalendarUtils.CalendarDay> newDays) {
        this.calendarDays = new ArrayList<>(newDays);
        notifyDataSetChanged();
    }

    public void setDaysWithEvents(Set<String> days) {
        this.daysWithEvents = days;
        notifyDataSetChanged();
    }

    public void setSelectedDay(long timeInMillis) {
        this.selectedDayTime = timeInMillis;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar_day, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        holder.bind(calendarDays.get(position), position);
    }

    @Override
    public int getItemCount() {
        return calendarDays.size();
    }

    class DayViewHolder extends RecyclerView.ViewHolder {
        final TextView tvDay;
        final View dot1, dot2, dot3;

        DayViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tv_day);
            dot1 = itemView.findViewById(R.id.dot1);
            dot2 = itemView.findViewById(R.id.dot2);
            dot3 = itemView.findViewById(R.id.dot3);
        }

        void bind(CalendarUtils.CalendarDay day, int position) {
            Context ctx = itemView.getContext();
            tvDay.setText(String.valueOf(day.date));

            boolean isToday = DateUtils.isToday(day.getTimeInMillis());
            boolean isSelected = DateUtils.isSameDay(day.getTimeInMillis(), selectedDayTime);
            boolean hasSundayCol = (position % 7 == 0);
            boolean hasSatCol = (position % 7 == 6);

            itemView.setBackground(null);
            tvDay.setBackground(null);

            if (isToday) {
                tvDay.setBackgroundResource(R.drawable.circle_today);
                tvDay.setTextColor(ctx.getColor(R.color.white));
            } else if (isSelected) {
                itemView.setBackgroundResource(R.drawable.bg_day_selected);
                tvDay.setTextColor(getTextColor(ctx, day.isCurrentMonth, hasSundayCol, hasSatCol));
            } else {
                tvDay.setTextColor(getTextColor(ctx, day.isCurrentMonth, hasSundayCol, hasSatCol));
            }

            String dayKey = DateUtils.formatDate(day.getTimeInMillis());
            dot1.setVisibility(daysWithEvents.contains(dayKey) ? View.VISIBLE : View.GONE);
            dot2.setVisibility(View.GONE);
            dot3.setVisibility(View.GONE);

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onDayClick(day);
            });
        }

        private int getTextColor(Context ctx, boolean isCurrentMonth, boolean isSunday, boolean isSaturday) {
            if (!isCurrentMonth) return ctx.getColor(R.color.ink_faint);
            if (isSunday) return ctx.getColor(R.color.weekend_sun);
            if (isSaturday) return ctx.getColor(R.color.weekend_sat);
            return ctx.getColor(R.color.ink);
        }
    }
}
