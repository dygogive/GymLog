package com.example.gymlog.ui.exercises.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymlog.R;
import com.example.gymlog.model.exercise.TypeAttributeExercises;

import java.util.List;

public class AttributeAdapter<E extends Enum<E> & TypeAttributeExercises> extends RecyclerView.Adapter<AttributeAdapter.ViewHolder> {

    private final List<E> items;
    private final OnItemClickListener listener;

    Context context;

    public interface OnItemClickListener {
        void onItemClick(Enum item);
    }

    public AttributeAdapter(List<E> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    // 1) Створюємо View з вашого item_attribute.xml
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_attribute, parent, false);
        return new ViewHolder(view);
    }

    // 2) Прив’язуємо дані (іконка + текст) до ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        E attribute = items.get(position);

        // Якщо у вашому Enum є метод для отримання опису та іконки:
        holder.titleTextView.setText(attribute.getDescription(context));

        // Обробник кліку по всьому ітемі
        holder.itemView.setOnClickListener(v -> listener.onItemClick(attribute));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // 3) ViewHolder знаходить View за ID
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.titleTextView);
        }
    }
}
