package com.example.calendarapp;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    public interface OnEventClickListener {
        void onEventClick(Event event);
    }

    private List<Event> events = new ArrayList<>();
    private final OnEventClickListener clickListener;

    public EventAdapter(OnEventClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void updateEvents(List<Event> newEvents) {
        this.events = new ArrayList<>(newEvents);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_agenda, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        holder.bind(events.get(position), clickListener);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        final View catBar;
        final TextView tvTitle, tvTime, tvPlace, tvCat;

        EventViewHolder(@NonNull View itemView) {
            super(itemView);
            catBar = itemView.findViewById(R.id.cat_bar);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvPlace = itemView.findViewById(R.id.tv_place);
            tvCat = itemView.findViewById(R.id.tv_cat);
        }

        void bind(Event event, OnEventClickListener listener) {
            Context ctx = itemView.getContext();

            tvTitle.setText(event.title);
            tvTime.setText(DateUtils.formatTime(event.startTime) + "–" + DateUtils.formatTime(event.endTime));
            String place = (event.location != null && !event.location.isEmpty()) ? event.location : "";
            tvPlace.setText(place);
            tvPlace.setVisibility(place.isEmpty() ? View.GONE : View.VISIBLE);

            applyCategory(ctx, event);

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onEventClick(event);
            });
        }

        private void applyCategory(Context ctx, Event event) {
            String category = event.category;
            if (category == null) category = "none";
            switch (category) {
                case "work":
                    catBar.setBackgroundTintList(ctx.getColorStateList(R.color.cat_work));
                    tvCat.setText("업무");
                    tvCat.setTextColor(ctx.getColor(R.color.cat_work));
                    tvCat.setBackgroundTintList(ctx.getColorStateList(R.color.cat_work_soft));
                    tvCat.setVisibility(View.VISIBLE);
                    break;
                case "personal":
                    catBar.setBackgroundTintList(ctx.getColorStateList(R.color.cat_personal));
                    tvCat.setText("개인");
                    tvCat.setTextColor(ctx.getColor(R.color.cat_personal));
                    tvCat.setBackgroundTintList(ctx.getColorStateList(R.color.cat_personal_soft));
                    tvCat.setVisibility(View.VISIBLE);
                    break;
                case "health":
                    catBar.setBackgroundTintList(ctx.getColorStateList(R.color.cat_health));
                    tvCat.setText("건강");
                    tvCat.setTextColor(ctx.getColor(R.color.cat_health));
                    tvCat.setBackgroundTintList(ctx.getColorStateList(R.color.cat_health_soft));
                    tvCat.setVisibility(View.VISIBLE);
                    break;
                case "social":
                    catBar.setBackgroundTintList(ctx.getColorStateList(R.color.cat_social));
                    tvCat.setText("약속");
                    tvCat.setTextColor(ctx.getColor(R.color.cat_social));
                    tvCat.setBackgroundTintList(ctx.getColorStateList(R.color.cat_social_soft));
                    tvCat.setVisibility(View.VISIBLE);
                    break;
                case "custom":
                    int color = event.customColor != null ? event.customColor : ctx.getColor(R.color.accent);
                    catBar.setBackgroundTintList(ColorStateList.valueOf(color));
                    
                    String customName = (event.customCategoryName != null && !event.customCategoryName.isEmpty()) 
                            ? event.customCategoryName : "기타";
                    tvCat.setText(customName);

                    tvCat.setTextColor(color);
                    // 연한 배경색은 색상의 투명도를 조절하여 생성 (약 15% 투명도)
                    int softColor = Color.argb(38, Color.red(color), Color.green(color), Color.blue(color));
                    tvCat.setBackgroundTintList(ColorStateList.valueOf(softColor));
                    tvCat.setVisibility(View.VISIBLE);
                    break;
                default:
                    catBar.setBackgroundTintList(ctx.getColorStateList(R.color.line));
                    tvCat.setVisibility(View.GONE);
                    break;
            }
        }
    }
}
