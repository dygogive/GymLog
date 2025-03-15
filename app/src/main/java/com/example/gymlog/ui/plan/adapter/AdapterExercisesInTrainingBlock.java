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
import com.example.gymlog.data.exercise.Exercise;

import java.util.List;

public class AdapterExercisesInTrainingBlock extends  RecyclerView.Adapter<AdapterExercisesInTrainingBlock.ViewHolder> {

    List<Exercise> exercises;
    Context context;
    ExerciseListener listener;

    public interface ExerciseListener {
        void onClickListener(Exercise exercise);
    }

    public AdapterExercisesInTrainingBlock(Context context, List<Exercise> exercises, ExerciseListener listener) {
        this.exercises = exercises;
        this.listener = listener;
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
        //взяти вправу для заповнення інформації в картці списку RecyclerView
        Exercise exercise = exercises.get(position);
        //задати назву вправі
        holder.nameExercise.setText(exercise.getNameOnly(context));
        //слухач для кнопки
        holder.buttonInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickListener(exercise);
            }
        });

    }

    @Override
    public int getItemCount() {
        if(exercises != null)
            return exercises.size();
        else return 0;
    }


    // ViewHolder клас для адаптера
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameExercise;
        ImageButton buttonInfo;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            buttonInfo = itemView.findViewById(R.id.buttonInfo); //кнопка показати інформацію прро вправу
            nameExercise = itemView.findViewById(R.id.nameExercise); //назва вправи
        }


    }
}
