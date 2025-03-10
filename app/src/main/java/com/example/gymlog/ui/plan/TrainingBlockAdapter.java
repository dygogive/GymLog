package com.example.gymlog.ui.plan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymlog.R;
import com.example.gymlog.data.plan.TrainingBlock;

import java.util.List;

public class TrainingBlockAdapter extends RecyclerView.Adapter<TrainingBlockAdapter.TrainingBlockViewHolder> {


    private final List<TrainingBlock> trainingBlocks;
    private final OnTrainingBlockClickListener listener;

    // Інтерфейс для обробки натискань
    public interface OnTrainingBlockClickListener {
        void onBlockClick(TrainingBlock block);
        void onDeleteBlockClick(TrainingBlock block);
    }

    // Конструктор
    public TrainingBlockAdapter(List<TrainingBlock> trainingBlocks, OnTrainingBlockClickListener listener) {
        this.trainingBlocks = trainingBlocks;
        this.listener = listener;
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
        holder.blockName.setText(block.getName());
        holder.blockDescription.setText(block.getDescription());

        // Натискання на блок для редагування
        holder.itemView.setOnClickListener(v -> listener.onBlockClick(block));

        // Натискання на кнопку видалення
        holder.buttonDeleteBlock.setOnClickListener(v -> listener.onDeleteBlockClick(block));
    }

    @Override
    public int getItemCount() {
        return trainingBlocks.size();
    }


    // ViewHolder для тренувальних блоків
    static class TrainingBlockViewHolder extends RecyclerView.ViewHolder {
        TextView blockName, blockDescription;
        ImageButton buttonDeleteBlock;

        public TrainingBlockViewHolder(@NonNull View itemView) {
            super(itemView);
            blockName = itemView.findViewById(R.id.textViewBlockName);
            blockDescription = itemView.findViewById(R.id.textViewBlockDescription);
            buttonDeleteBlock = itemView.findViewById(R.id.buttonDeleteBlock);
        }
    }
}
