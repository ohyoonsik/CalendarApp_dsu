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
        holder.bind(event);
    }
    
    @Override
    public int getItemCount() {
        return events.size();
    }
    
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView timeTextView;
        
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.event_title);
            timeTextView = itemView.findViewById(R.id.event_time);
        }
        
        public void bind(Event event) {
            titleTextView.setText(event.title);
            timeTextView.setText(DateUtils.formatTime(event.startTime) + " - " + DateUtils.formatTime(event.endTime));
        }
    }
}
