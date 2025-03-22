package com.example.gymlog.ui.plan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymlog.R;
import com.example.gymlog.data.db.PlanManagerDAO;
import com.example.gymlog.data.exercise.Exercise;
import com.example.gymlog.data.exercise.ExerciseInBlock;

import java.util.Collections;
import java.util.List;

public class AdapterExercisesInTrainingBlock extends RecyclerView.Adapter<AdapterExercisesInTrainingBlock.ViewHolder> {








    private final Context context;
    private final List<ExerciseInBlock> exercises;
    public List<ExerciseInBlock> getItems() {
        return exercises;
    }


    public interface ExerciseListener {
        void onClickListener(Exercise exercise);
    }
    private final ExerciseListener exerciseListener;


    public AdapterExercisesInTrainingBlock(
            Context context,
            List<ExerciseInBlock> exercises,
            ExerciseListener exerciseListener
    ) {
        this.exercises = exercises;
        this.exerciseListener = exerciseListener;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise_for_training_block, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExerciseInBlock exercise = exercises.get(position);
        holder.nameExercise.setText(exercise.getNameOnly(context));

        holder.buttonInfo.setOnClickListener(v -> {
            exerciseListener.onClickListener(exercise);
        });

    }

    @Override
    public int getItemCount() {
        return exercises != null ? exercises.size() : 0;
    }

    public void moveItem(int fromPosition, int toPosition, PlanManagerDAO dao, long blockID) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(exercises, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(exercises, i, i - 1);
            }
        }
        for (int i = 0; i < exercises.size(); i++) {
            exercises.get(i).setPosition(i);
        }
        dao.updateTrainingBlockExercises(blockID, exercises);

        notifyItemMoved(fromPosition, toPosition);

        dao.logAllData();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameExercise;
        ImageButton buttonInfo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameExercise = itemView.findViewById(R.id.nameExercise);
            buttonInfo = itemView.findViewById(R.id.buttonInfo);
        }
    }
}
