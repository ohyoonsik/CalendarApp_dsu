package com.example.calendarapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    
    private List<Event> events = new ArrayList<>();

    // 1. 리스너 인터페이스 정의
    public interface OnEventClickListener {
        void onEventClick(Event event);
    }

    private OnEventClickListener listener;

    // 2. 리스너 설정 메서드
    public void setOnEventClickListener(OnEventClickListener listener) {
        this.listener = listener;
    }
    

    public void updateEvents(List<Event> newEvents) {
        this.events = new ArrayList<>(newEvents);
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.bind(event, listener);
    }
    
    @Override
    public int getItemCount() {
        return events.size();
    }
    
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView dateTextView;
        private final TextView timeTextView;
        
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.event_title);
            dateTextView = itemView.findViewById(R.id.event_date);
            timeTextView = itemView.findViewById(R.id.event_time);
        }

        public void bind(final Event event, final OnEventClickListener listener) {
            titleTextView.setText(event.title);
            dateTextView.setText(DateUtils.formatDate(event.startTime));
            timeTextView.setText(DateUtils.formatTime(event.startTime) + " - " + DateUtils.formatTime(event.endTime));

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEventClick(event);
                }
            });
        }
    }
}
