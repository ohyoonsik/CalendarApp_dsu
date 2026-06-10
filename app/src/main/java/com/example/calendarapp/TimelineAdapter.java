package com.example.calendarapp;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder> {

    public interface OnEventClickListener {
        void onEventClick(Event event);
    }

    private List<Event> events = new ArrayList<>();
    private final OnEventClickListener clickListener;

    public TimelineAdapter(OnEventClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void updateEvents(List<Event> newEvents) {
        this.events = new ArrayList<>(newEvents);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TimelineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_timeline, parent, false);
        return new TimelineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimelineViewHolder holder, int position) {
        holder.bind(events.get(position), position == events.size() - 1, clickListener);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    static class TimelineViewHolder extends RecyclerView.ViewHolder {
        final TextView tvStart, tvEnd, tvTitle, tvPlace, tvCat;
        final View node, line;
        final LinearLayout placeRow;

        TimelineViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStart = itemView.findViewById(R.id.tv_start);
            tvEnd = itemView.findViewById(R.id.tv_end);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvPlace = itemView.findViewById(R.id.tv_place);
            tvCat = itemView.findViewById(R.id.tv_cat);
            node = itemView.findViewById(R.id.node);
            line = itemView.findViewById(R.id.line);
            placeRow = itemView.findViewById(R.id.place_row);
        }

        void bind(Event event, boolean isLast, OnEventClickListener listener) {
            Context ctx = itemView.getContext();

            tvStart.setText(DateUtils.formatTime(event.startTime));
            tvEnd.setText(DateUtils.formatTime(event.endTime));
            tvTitle.setText(event.title);

            boolean hasPlace = event.location != null && !event.location.isEmpty();
            placeRow.setVisibility(hasPlace ? View.VISIBLE : View.GONE);
            if (hasPlace) tvPlace.setText(event.location);

            line.setVisibility(isLast ? View.GONE : View.VISIBLE);

            int catColor = catColor(ctx, event.category);
            int catSoft = catSoftColor(ctx, event.category);
            String catLabel = catLabel(event.category);

            node.setBackgroundTintList(ColorStateList.valueOf(catColor));

            if (catLabel != null) {
                tvCat.setVisibility(View.VISIBLE);
                tvCat.setText(catLabel);
                tvCat.setTextColor(catColor);
                tvCat.setBackgroundTintList(ColorStateList.valueOf(catSoft));
            } else {
                tvCat.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onEventClick(event);
            });
        }

        private int catColor(Context ctx, String cat) {
            if (cat == null) return ctx.getColor(R.color.accent);
            switch (cat) {
                case "work":     return ctx.getColor(R.color.cat_work);
                case "personal": return ctx.getColor(R.color.cat_personal);
                case "health":   return ctx.getColor(R.color.cat_health);
                case "social":   return ctx.getColor(R.color.cat_social);
                default:         return ctx.getColor(R.color.accent);
            }
        }

        private int catSoftColor(Context ctx, String cat) {
            if (cat == null) return ctx.getColor(R.color.accent_soft);
            switch (cat) {
                case "work":     return ctx.getColor(R.color.cat_work_soft);
                case "personal": return ctx.getColor(R.color.cat_personal_soft);
                case "health":   return ctx.getColor(R.color.cat_health_soft);
                case "social":   return ctx.getColor(R.color.cat_social_soft);
                default:         return ctx.getColor(R.color.accent_soft);
            }
        }

        private String catLabel(String cat) {
            if (cat == null) return null;
            switch (cat) {
                case "work":     return "업무";
                case "personal": return "개인";
                case "health":   return "건강";
                case "social":   return "약속";
                default:         return null;
            }
        }
    }
}
