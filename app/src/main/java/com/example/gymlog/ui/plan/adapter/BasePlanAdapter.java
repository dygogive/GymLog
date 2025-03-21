package com.example.gymlog.ui.plan.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymlog.R;
import com.example.gymlog.data.plan.BasePlanItem;

import java.util.Collections;
import java.util.List;

/**
 * Узагальнений адаптер для відображення списку BasePlanItem (наприклад, FitnessProgram, GymSession).
 * Містить:
 *  - Поля назви та опису
 *  - Кнопки "Редагувати" та "Видалити"
 *  - Метод "onItemClick" для переходу чи детального перегляду
 */
public class BasePlanAdapter<T extends BasePlanItem> extends RecyclerView.Adapter<BasePlanAdapter.BasePlanViewHolder> {

    public List<T> getItems() {
        return items;
    }

    /**
     * Інтерфейс для обробки кліків:
     * - onEditClick()   => редагування
     * - onDeleteClick() => видалення
     * - onItemClick()   => простий клік на весь елемент
     */
    public interface OnPlanItemClickListener<T> {
        void onEditClick(T item);
        void onDeleteClick(T item);
        void onItemClick(T item);
    }

    // Список елементів та слухач кліків
    private final List<T> items;
    public final OnPlanItemClickListener<T> listener;

    /**
     * Конструктор адаптера.
     *
     * @param items    Список об'єктів, що реалізують BasePlanItem
     * @param listener Слухач подій (редагування, видалення, клік)
     */
    public BasePlanAdapter(List<T> items, OnPlanItemClickListener<T> listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BasePlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Інфлюємо item_plan_day.xml для відображення назв та описів
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_plan_day, parent, false);
        return new BasePlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BasePlanViewHolder holder, int position) {
        // Поточний об'єкт (наприклад, FitnessProgram чи GymSession)
        T item = items.get(position);

        // Відображення назви та опису
        holder.textViewName.setText(item.getName());
        holder.textViewDescription.setText(item.getDescription());

        // Обробники натискань
        // 1) натискання на весь item
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        // 2) редагувати
        holder.buttonEdit.setOnClickListener(v -> listener.onEditClick(item));
        // 3) видалити
        holder.buttonDelete.setOnClickListener(v -> listener.onDeleteClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Метод для переміщення елементів (Drag & Drop).
     * Викликається з ItemTouchHelper.
     *
     * @param fromPosition Поточна позиція
     * @param toPosition   Цільова позиція
     */
    public void moveItem(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            // зсув вниз
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(items, i, i + 1);
            }
        } else {
            // зсув вгору
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(items, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);


    }



    /**
     * ViewHolder містить посилання на назву, опис і кнопки (редагувати, видалити).
     */
    public static class BasePlanViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewDescription;
        ImageButton buttonEdit, buttonDelete;

        public BasePlanViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName        = itemView.findViewById(R.id.textViewDayName);
            textViewDescription = itemView.findViewById(R.id.tvDayDescription);
            buttonEdit          = itemView.findViewById(R.id.buttonEditDay);
            buttonDelete        = itemView.findViewById(R.id.buttonDeleteDay);
        }
    }
}
