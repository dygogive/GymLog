package com.example.gymlog.ui.plan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymlog.R;
import com.example.gymlog.data.db.PlanManagerDAO;
import com.example.gymlog.data.exercise.Exercise;
import com.example.gymlog.data.plan.TrainingBlock;
import com.example.gymlog.ui.exercise2.adapters.ExerciseAdapter;

import java.util.List;

public class TrainingBlockAdapter extends RecyclerView.Adapter<TrainingBlockAdapter.TrainingBlockViewHolder> {


    public interface OnTrainingBlockClickListener {
        void onBlockClick(TrainingBlock block);
        void onDeleteBlockClick(TrainingBlock block);
    }

    private final List<TrainingBlock> trainingBlocks;
    private final OnTrainingBlockClickListener listener;
    private final PlanManagerDAO planManagerDAO;

    // Конструктор
    public TrainingBlockAdapter(List<TrainingBlock> trainingBlocks, PlanManagerDAO planManagerDAO, OnTrainingBlockClickListener listener) {
        this.trainingBlocks = trainingBlocks;
        this.listener = listener;
        this.planManagerDAO = planManagerDAO;
    }

    @NonNull
    @Override
    public TrainingBlockAdapter.TrainingBlockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_training_block, parent, false);
        return new TrainingBlockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrainingBlockAdapter.TrainingBlockViewHolder holder, int position) {
        TrainingBlock block = trainingBlocks.get(position);
        holder.textViewBlockName.setText(block.getName());
        holder.textViewBlockDescription.setText(block.getDescription());

        holder.buttonEdit.setOnClickListener(v -> listener.onBlockClick(block));
        holder.buttonDelete.setOnClickListener(v -> listener.onDeleteBlockClick(block));

        // Завантажуємо вправи для блоку
        List<Exercise> exercises = planManagerDAO.getExercisesForTrainingBlock(block.getId());
        ExerciseAdapter exerciseAdapter = new ExerciseAdapter(exercises, new ExerciseAdapter.OnExerciseClickListener() {
            @Override
            public void onExerciseClick(Exercise exercise) {

            }

            @Override
            public void onEditClick(Exercise exercise) {

            }
        });
        holder.recyclerViewExercises.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.recyclerViewExercises.setAdapter(exerciseAdapter);
        holder.recyclerViewExercises.setVisibility(View.GONE);


        // Кнопка розгортання списку вправ
        holder.buttonExpand.setOnClickListener(v -> {
            if (holder.recyclerViewExercises.getVisibility() == View.GONE) {
                holder.recyclerViewExercises.setVisibility(View.VISIBLE);
                holder.buttonExpand.setImageResource(R.drawable.ic_collapse);
            } else {
                holder.recyclerViewExercises.setVisibility(View.GONE);
                holder.buttonExpand.setImageResource(R.drawable.ic_expand);
            }
        });

    }

    @Override
    public int getItemCount() {
        return trainingBlocks.size();
    }


    // ViewHolder для тренувальних блоків
    static class TrainingBlockViewHolder extends RecyclerView.ViewHolder {
        TextView textViewBlockName, textViewBlockDescription;
        ImageButton buttonEdit, buttonDelete, buttonExpand;
        RecyclerView recyclerViewExercises;

        public TrainingBlockViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewBlockName = itemView.findViewById(R.id.textViewBlockName);
            textViewBlockDescription = itemView.findViewById(R.id.textViewBlockDescription);
            buttonEdit = itemView.findViewById(R.id.buttonEditBlock);
            buttonDelete = itemView.findViewById(R.id.buttonDeleteBlock);
            buttonExpand = itemView.findViewById(R.id.buttonExpand);
            recyclerViewExercises = itemView.findViewById(R.id.recyclerViewExercises);
        }
    }
}
