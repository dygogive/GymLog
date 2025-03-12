package com.example.gymlog.ui.plan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymlog.R;
import com.example.gymlog.data.plan.FitnessProgram;

import java.util.List;

public class FitnessProgramAdapter extends RecyclerView.Adapter<FitnessProgramAdapter.PlanCycleViewHolder> {

    public interface OnPlanCycleClickListener {
        void onEditClick(FitnessProgram fitnessProgram);
        void onDeleteClick(FitnessProgram fitnessProgram);
        void onItemClick(FitnessProgram fitnessProgram);
    }

    private final List<FitnessProgram> fitnessPrograms;
    private final OnPlanCycleClickListener listener;

    // Конструктор
    public FitnessProgramAdapter(List<FitnessProgram> fitnessPrograms, OnPlanCycleClickListener listener) {
        this.fitnessPrograms = fitnessPrograms;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlanCycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_plan_day, parent, false);
        return new PlanCycleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanCycleViewHolder holder, int position) {
        FitnessProgram fitnessProgram = fitnessPrograms.get(position);
        Context context = holder.itemView.getContext();

        holder.itemView.setOnClickListener(v -> listener.onItemClick(fitnessProgram));

        // Заповнення даних
        holder.textViewDayName.setText(fitnessProgram.getName());
        holder.textViewDayDescription.setText(fitnessProgram.getDescription());

        // Обробка натискань на кнопки
        holder.buttonEdit.setOnClickListener(v -> listener.onEditClick(fitnessProgram));
        holder.buttonDelete.setOnClickListener(v -> listener.onDeleteClick(fitnessProgram));
    }

    @Override
    public int getItemCount() {
        return fitnessPrograms.size();
    }

    // ViewHolder
    static class PlanCycleViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDayName, textViewDayDescription;
        ImageButton buttonEdit, buttonDelete;

        public PlanCycleViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDayName = itemView.findViewById(R.id.textViewDayName);
            textViewDayDescription = itemView.findViewById(R.id.textViewDayDescription);
            buttonEdit = itemView.findViewById(R.id.buttonEditDay);
            buttonDelete = itemView.findViewById(R.id.buttonDeleteDay);
        }
    }
}
