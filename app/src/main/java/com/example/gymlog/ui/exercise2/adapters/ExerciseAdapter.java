package com.example.gymlog.ui.exercise2.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymlog.R;
import com.example.gymlog.data.exercise.Exercise;

import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private final List<Exercise> exercises;
    private final OnItemClickListener listener;

    // Інтерфейс для обробки кліків
    public interface OnItemClickListener {
        void onItemClick(Exercise exercise);
    }

    // Конструктор адаптера
    public ExerciseAdapter(List<Exercise> exercises, OnItemClickListener listener) {
        this.exercises = exercises;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);

        if (exercise != null) {
            holder.nameTextView.setText(exercise.getName());
            holder.motionTextView.setText("Motion: " + exercise.getMotion().name());
            holder.equipmentTextView.setText("Equipment: " + exercise.getEquipment().name());

            // Форматуємо м'язові групи у рядок
            StringBuilder muscleGroups = new StringBuilder();
            for (int i = 0; i < exercise.getMuscleGroupList().size(); i++) {
                muscleGroups.append(exercise.getMuscleGroupList().get(i).name());
                if (i < exercise.getMuscleGroupList().size() - 1) {
                    muscleGroups.append(", ");
                }
            }
            holder.muscleGroupsTextView.setText("Muscle Groups: " + muscleGroups);

            // Обробка кліка по елементу
            holder.itemView.setOnClickListener(v -> listener.onItemClick(exercise));
        }
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    // ViewHolder клас для адаптера
    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView motionTextView;
        TextView equipmentTextView;
        TextView muscleGroupsTextView;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.exercise_name);
            motionTextView = itemView.findViewById(R.id.exercise_motion);
            equipmentTextView = itemView.findViewById(R.id.exercise_equipment);
            muscleGroupsTextView = itemView.findViewById(R.id.exercise_muscle_groups);
        }
    }
}
