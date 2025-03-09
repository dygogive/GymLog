package com.example.gymlog.ui.plan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymlog.R;
import com.example.gymlog.data.plan.GymDay;

import java.util.List;

// Адаптер для списку тренувальних днів
public class GymDayAdapter extends RecyclerView.Adapter<GymDayAdapter.GymDayViewHolder> {

    private final List<GymDay> gymDays;
    private final OnGymDayClickListener listener;

    // Інтерфейс для обробки натискань
    public interface OnGymDayClickListener {
        void onDayClick(GymDay gymDay);
        void onDeleteDayClick(GymDay gymDay);
        void onAddTrainingBlockClick(GymDay gymDay);
    }

    // Конструктор
    public GymDayAdapter(List<GymDay> gymDays, OnGymDayClickListener listener) {
        this.gymDays = gymDays;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GymDayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_gym_day, parent, false);
        return new GymDayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GymDayViewHolder holder, int position) {
        GymDay gymDay = gymDays.get(position);
        holder.dayName.setText("День тренування №" + gymDay.getId());

        // Натискання на елемент для редагування дня
        holder.itemView.setOnClickListener(v -> listener.onDayClick(gymDay));

        // Натискання на кнопку видалення дня
        holder.buttonDeleteDay.setOnClickListener(v -> listener.onDeleteDayClick(gymDay));

        // Додаємо обробку кнопки «Додати тренувальний блок»
        holder.buttonAddTrainingBlock.setOnClickListener(v -> listener.onAddTrainingBlockClick(gymDay));
    }

    @Override
    public int getItemCount() {
        return gymDays.size();
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
