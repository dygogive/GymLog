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
import com.example.gymlog.data.plan.BasePlanItem;

import java.util.List;

// Узагальнений адаптер для списку програм тренувань або тренувальних днів
public class BasePlanAdapter<T extends BasePlanItem> extends RecyclerView.Adapter<BasePlanAdapter.BasePlanViewHolder> {

    public interface OnPlanItemClickListener<T> {
        void onEditClick(T item);
        void onDeleteClick(T item);
        void onItemClick(T item);
    }

    private final List<T> items;
    public final OnPlanItemClickListener<T> listener;

    // Конструктор
    public BasePlanAdapter(List<T> items, OnPlanItemClickListener<T> listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BasePlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_plan_day, parent, false);
        return new BasePlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BasePlanViewHolder holder, int position) {
        T item = items.get(position);

        String description = item.getDescription();

        holder.textViewName.setText(item.getName());

        holder.textViewDescription.setText(description);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        holder.buttonEdit.setOnClickListener(v -> listener.onEditClick(item));
        holder.buttonDelete.setOnClickListener(v -> listener.onDeleteClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // ViewHolder
    public static class BasePlanViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewDescription;
        ImageButton buttonEdit, buttonDelete;

        public BasePlanViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewDayName);
            textViewDescription = itemView.findViewById(R.id.tvDayDescription);
            buttonEdit = itemView.findViewById(R.id.buttonEditDay);
            buttonDelete = itemView.findViewById(R.id.buttonDeleteDay);
        }
    }
}
