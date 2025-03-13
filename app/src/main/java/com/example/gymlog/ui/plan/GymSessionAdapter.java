package com.example.gymlog.ui.plan;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymlog.R;
import com.example.gymlog.data.plan.GymSession;

import java.util.List;

// Адаптер для списку тренувальних днів
public class GymSessionAdapter extends RecyclerView.Adapter<GymSessionAdapter.GymDayViewHolder> {

    private final List<GymSession> gymSessions;
    private final OnGymDayClickListener listener;

    // Інтерфейс для обробки натискань
    public interface OnGymDayClickListener {
        void onDayClick(GymSession gymSession);
        void onDeleteDayClick(GymSession gymSession);
        void onAddTrainingBlockClick(GymSession gymSession);
    }

    // Конструктор
    public GymSessionAdapter(List<GymSession> gymSessions, OnGymDayClickListener listener) {
        this.gymSessions = gymSessions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GymDayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_gym_day, parent, false);
        return new GymDayViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull GymDayViewHolder holder, int position) {
        GymSession gymSession = gymSessions.get(position);
        holder.dayName.setText(gymSession.getName() + "\\n" + gymSession.getDescription());

        // Натискання на елемент для редагування дня
        holder.itemView.setOnClickListener(v -> listener.onDayClick(gymSession));

        // Натискання на кнопку видалення дня
        holder.buttonDeleteDay.setOnClickListener(v -> listener.onDeleteDayClick(gymSession));

        // Додаємо обробку кнопки «Додати тренувальний блок»
        holder.buttonAddTrainingBlock.setOnClickListener(v -> listener.onAddTrainingBlockClick(gymSession));
    }

    @Override
    public int getItemCount() {
        return gymSessions.size();
    }

    // ViewHolder для тренувальних днів
    static class GymDayViewHolder extends RecyclerView.ViewHolder {
        TextView dayName;
        ImageButton
                buttonDeleteDay,
                buttonAddTrainingBlock;

        public GymDayViewHolder(@NonNull View itemView) {
            super(itemView);
            dayName = itemView.findViewById(R.id.textViewGymDayName);
            buttonDeleteDay = itemView.findViewById(R.id.buttonDeleteGymDay);
            buttonAddTrainingBlock = itemView.findViewById(R.id.buttonAddTrainingBlock);
        }
    }
}
