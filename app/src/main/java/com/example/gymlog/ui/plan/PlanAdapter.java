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
import com.example.gymlog.data.plan.PlanCycle;

import java.util.List;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.PlanCycleViewHolder> {

    public interface OnPlanCycleClickListener {
        void onEditClick(PlanCycle planCycle);
        void onDeleteClick(PlanCycle planCycle);
    }

    private final List<PlanCycle> planCycles;
    private final OnPlanCycleClickListener listener;

    // Конструктор
    public PlanAdapter(List<PlanCycle> planCycles, OnPlanCycleClickListener listener) {
        this.planCycles = planCycles;
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
        PlanCycle planCycle = planCycles.get(position);
        Context context = holder.itemView.getContext();

        // Заповнення даних
        holder.textViewDayName.setText(planCycle.getName());
        holder.textViewDayDescription.setText(planCycle.getDescription());

        // Обробка натискань на кнопки
        holder.buttonEdit.setOnClickListener(v -> listener.onEditClick(planCycle));
        holder.buttonDelete.setOnClickListener(v -> listener.onDeleteClick(planCycle));
    }

    @Override
    public int getItemCount() {
        return planCycles.size();
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
