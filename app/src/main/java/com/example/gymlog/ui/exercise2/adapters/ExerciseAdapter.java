package com.example.gymlog.ui.exercise2.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymlog.R;
import com.example.gymlog.data.exercise.Exercise;

import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private final List<Exercise> exercises;
    private final OnExerciseClickListener listener;

    // Інтерфейс для обробки кліків
    public interface OnExerciseClickListener {
        void onExerciseClick(Exercise exercise);
        void onEditClick(Exercise exercise);
    }

    public void updateExercises(List<Exercise> newExercises) {
        this.exercises.clear();
        this.exercises.addAll(newExercises);
        notifyDataSetChanged();
    }

    // Конструктор адаптера
    public ExerciseAdapter(List<Exercise> exercises, OnExerciseClickListener listener) {
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

        holder.exerciseName.setText(exercise.getName());
        holder.exerciseDetails.setText("Motion: " + exercise.getMotion() +
                "\nEquipment: " + exercise.getEquipment());

        holder.itemView.setOnClickListener(v -> listener.onExerciseClick(exercise));
        holder.editButton.setOnClickListener(v -> listener.onEditClick(exercise));
    }



    @Override
    public int getItemCount() {
        return exercises.size();
    }

    // ViewHolder клас для адаптера
    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView exerciseName, exerciseDetails;
        ImageButton editButton;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.textViewExerciseName);
            exerciseDetails = itemView.findViewById(R.id.textViewExerciseDetails);
            editButton = itemView.findViewById(R.id.buttonEditExercise);
        }
    }
}
