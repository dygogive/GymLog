package com.example.gymlog.ui.exercise.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymlog.R;
import com.example.gymlog.model.exercise.Exercise;
import com.example.gymlog.model.exercise.MuscleGroup;

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
        Context context = holder.itemView.getContext();

        // Використання getDescription для отримання ресурсних рядків
        String motionDescription = exercise.getMotion().getDescription(context);
        String equipmentDescription = exercise.getEquipment().getDescription(context);
        List<MuscleGroup> muscles = exercise.getMuscleGroupList();
        StringBuilder musclesTxt = new StringBuilder();
        for(MuscleGroup muscleGroup : muscles){
            musclesTxt.append(muscleGroup.getDescription(context)).append("; \n    ");
        }


        String equipment = context.getString(R.string.equipment);
        String motion = context.getString(R.string.motion);
        String muscleGroup = context.getString(R.string.muscle_group);

        holder.exerciseName.setText(exercise.getName());
        holder.exerciseDetails.setText(motion + ": " +
                "\n    " + motionDescription +
                "\n" + equipment + ": " +
                "\n    " + equipmentDescription +
                "\n" + muscleGroup + ": " +
                "\n    " + musclesTxt.toString());

        holder.itemView.setOnClickListener(v -> listener.onExerciseClick(exercise));
        holder.editButton.setOnClickListener(v -> listener.onEditClick(exercise));

        // Передача тексту і стану в ViewHolder
        holder.bindExpandableDetails(holder.exerciseDetails.getText().toString());
    }



    @Override
    public int getItemCount() {
        return exercises.size();
    }

    // ViewHolder клас для адаптера
    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView exerciseName, exerciseDetails;
        ImageButton editButton;
        private final ImageButton expandButton;
        private boolean isExpanded = false; // Початковий стан

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.nameExercise);
            exerciseDetails = itemView.findViewById(R.id.textViewExerciseDetails);
            editButton = itemView.findViewById(R.id.buttonInfo);
            expandButton = itemView.findViewById(R.id.buttonExpandDetails); // Кнопка розширення
        }

        public void bindExpandableDetails(String fullDetails) {
            // Встановлення початкового стану
            setExpandableState();

            // Обробка кліку на кнопку розширення
            expandButton.setOnClickListener(v -> {
                isExpanded = !isExpanded;
                setExpandableState();
            });
        }

        private void setExpandableState() {
            if (isExpanded) {
                // Показуємо повний текст
                exerciseDetails.setMaxLines(Integer.MAX_VALUE);
                expandButton.setImageResource(R.drawable.ic_collapse); // Іконка для згортання
            } else {
                // Обмежуємо кількість рядків
                exerciseDetails.setMaxLines(2);
                expandButton.setImageResource(R.drawable.ic_expand); // Іконка для розширення
            }
        }
    }
}
