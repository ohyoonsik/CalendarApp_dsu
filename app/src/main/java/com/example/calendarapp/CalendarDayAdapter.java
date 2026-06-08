package com.example.calendarapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CalendarDayAdapter extends BaseAdapter {
    
    private Context context;
    private List<CalendarUtils.CalendarDay> calendarDays = new ArrayList<>();
    
    public CalendarDayAdapter(Context context) {
        this.context = context;
    }
    
    public void updateCalendarDays(List<CalendarUtils.CalendarDay> newDays) {
        this.calendarDays = new ArrayList<>(newDays);
        notifyDataSetChanged();
    }
    
    @Override
    public int getCount() {
        return calendarDays.size();
    }
    
    @Override
    public Object getItem(int position) {
        return calendarDays.get(position);
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_calendar_day, parent, false);
        }
        
        CalendarUtils.CalendarDay day = calendarDays.get(position);
        TextView dayTextView = convertView.findViewById(R.id.day_text);
        
        dayTextView.setText(String.valueOf(day.date));
        
        if (day.isCurrentMonth) {
            dayTextView.setTextColor(context.getResources().getColor(R.color.black, null));
        } else {
            dayTextView.setTextColor(context.getResources().getColor(R.color.light_gray, null));
        }
        
        return convertView;
    }
}
