package com.example.gymlog.ui.plan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymlog.R;
import com.example.gymlog.data.exercise.Exercise;
import com.example.gymlog.ui.exercise2.adapters.ExerciseAdapter;

import java.util.List;

public class AdapterExercisesTrainingBlock extends  RecyclerView.Adapter<AdapterExercisesTrainingBlock.ViewHolder> {

    List<Exercise> exercises;

    ExerciseListener listener;

    interface ExerciseListener {
        void onClickListener();
    }

    public AdapterExercisesTrainingBlock(List<Exercise> exercises, ExerciseListener listener) {
        this.exercises = exercises;
        this.listener = listener;
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
        holder.nameExercise.setText(exercise.getName());
        //слухач для кнопки
        holder.buttonInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickListener();
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
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameExercise;
        ImageButton buttonInfo;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            buttonInfo = itemView.findViewById(R.id.buttonInfo); //кнопка показати інформацію прро вправу
            nameExercise = itemView.findViewById(R.id.nameExercise); //назва вправи
        }


    }
}
