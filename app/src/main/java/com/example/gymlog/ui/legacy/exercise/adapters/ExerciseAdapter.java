package com.example.gymlog.ui.legacy.exercise.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymlog.R;
import com.example.gymlog.domain.model.exercise.Exercise;
import com.example.gymlog.domain.model.attribute.muscle.MuscleGroup;

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

        // Початково приховати деталі
        holder.setExpandedState(exercise.isExpanded());
        holder.itemView.setOnClickListener(v -> {
            exercise.setExpanded(!exercise.isExpanded());
            notifyItemChanged(position);  // перерисовує айтем
        });

        holder.exerciseName.setText(exercise.getName());
        String description = exercise.getDescription();


        holder.exerciseDescription.setText(description != null ? description : "");
        holder.exerciseMotion.setText(context.getString(R.string.motion) + ": " + exercise.getMotion().getDescription(context));

        List<MuscleGroup> muscles = exercise.getMuscleGroupList();
        StringBuilder musclesTxt = new StringBuilder();
        for(MuscleGroup mg : muscles){
            musclesTxt.append(mg.getDescription(context)).append(", ");
        }
        if (musclesTxt.length() > 0)
            musclesTxt.setLength(musclesTxt.length() - 2);

        holder.exerciseMuscles.setText(context.getString(R.string.muscle_group) + ": " + musclesTxt);
        holder.exerciseEquipment.setText(context.getString(R.string.equipment) + ": " + exercise.getEquipment().getDescription(context));

        holder.imageButton.setOnClickListener(v -> listener.onEditClick(exercise));

    }


    @Override
    public int getItemCount() {
        return exercises.size();
    }

    // ViewHolder клас для адаптера
    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView exerciseName, exerciseDescription, exerciseMotion, exerciseMuscles, exerciseEquipment;
        ImageButton imageButton;
        ImageView imageIsExpand;

        private boolean isExpanded = false;


        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.nameExercise);
            exerciseDescription = itemView.findViewById(R.id.exerciseDescription);
            exerciseMotion = itemView.findViewById(R.id.exerciseMotion);
            exerciseMuscles = itemView.findViewById(R.id.exerciseMuscles);
            exerciseEquipment = itemView.findViewById(R.id.exerciseEquipment);
            imageButton = itemView.findViewById(R.id.buttonInfo);
            imageIsExpand = itemView.findViewById(R.id.imageIsExpand);
        }

        public void setExpandedState(boolean expanded) {
            this.isExpanded = expanded;

            int visibility = expanded ? View.VISIBLE : View.GONE;
            exerciseMotion.setVisibility(visibility);
            exerciseMuscles.setVisibility(visibility);
            exerciseEquipment.setVisibility(visibility);

            imageIsExpand.setImageResource(expanded ?
                    android.R.drawable.arrow_up_float :
                    android.R.drawable.arrow_down_float);
        }
    }



}
