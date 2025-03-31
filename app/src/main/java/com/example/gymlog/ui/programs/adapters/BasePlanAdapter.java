package com.example.gymlog.ui.programs.adapters;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymlog.R;
import com.example.gymlog.model.plan.BasePlanItem;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * Адаптер для відображення списку елементів, що наслідують BasePlanItem.
 * Підтримує редагування, клонування та видалення через контекстне меню.
 * Простий та зрозумілий для розуміння навіть для джуніорів.
 */
public class BasePlanAdapter<T extends BasePlanItem> extends RecyclerView.Adapter<BasePlanAdapter.BasePlanViewHolder> {

    private final List<T> items;
    private final OnPlanItemClickListener<T> listener;

    /**
     * Інтерфейс для обробки кліків на елементи списку.
     */
    public interface OnPlanItemClickListener<T> {
        void onItemClick(T item);
        void onEditClick(T item);
        void onCloneClick(T item);
        void onDeleteClick(T item);
    }

    /**
     * Конструктор адаптера.
     *
     * @param items    Список елементів для відображення.
     * @param listener Слухач подій (редагування, клонування, видалення, простий клік).
     */
    public BasePlanAdapter(List<T> items, OnPlanItemClickListener<T> listener) {
        this.items = items;
        this.listener = listener;
    }

    public List<T> getItems() {
        return items;
    }

    @NonNull
    @Override
    public BasePlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Інфлюємо розмітку елемента списку
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plan_day, parent, false);
        return new BasePlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BasePlanViewHolder holder, int position) {
        T item = items.get(position);
        // Встановлюємо назву та опис
        holder.textViewName.setText(item.getName());
        holder.textViewDescription.setText(item.getDescription());

        // Обробка простого кліку по елементу
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));

        // Налаштування контекстного меню при кліку на кнопку
        holder.buttonMenu.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), v);
            popup.getMenuInflater().inflate(R.menu.program_context_menu, popup.getMenu());
            forceShowIcons(popup); // Примусове відображення іконок у меню
            popup.setOnMenuItemClickListener(menuItem -> {
                int id = menuItem.getItemId();
                if (id == R.id.menu_edit) {
                    listener.onEditClick(item);
                    return true;
                } else if (id == R.id.menu_clone) {
                    listener.onCloneClick(item);
                    return true;
                } else if (id == R.id.menu_delete) {
                    listener.onDeleteClick(item);
                    return true;
                }
                return false;
            });
            tintMenuIcons(popup.getMenu(), holder);
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Переміщення елемента в списку (Drag & Drop).
     *
     * @param fromPosition Початкова позиція.
     * @param toPosition   Цільова позиція.
     */
    public void moveItem(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(items, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(items, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    /**
     * Примусове відображення іконок у PopupMenu за допомогою reflection.
     *
     * @param popup PopupMenu, для якого потрібно показати іконки.
     */
    public static void forceShowIcons(PopupMenu popup) {
        try {
            Field field = popup.getClass().getDeclaredField("mPopup");
            field.setAccessible(true);
            Object menuPopupHelper = field.get(popup);
            Method setForceShowIcon = menuPopupHelper.getClass().getMethod("setForceShowIcon", boolean.class);
            setForceShowIcon.invoke(menuPopupHelper, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Накладання тінту на іконки меню.
     *
     * @param menu   Меню з іконками.
     * @param holder ViewHolder для отримання контексту.
     */
    public static  void tintMenuIcons(Menu menu, RecyclerView.ViewHolder holder) {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            Drawable icon = menuItem.getIcon();
            if (icon != null) {
                icon.mutate();
                icon.setTint(ContextCompat.getColor(holder.itemView.getContext(), R.color.text_hint));
                menuItem.setIcon(icon);
            }
        }
    }

    /**
     * ViewHolder для елемента списку.
     * Містить посилання на текстові поля та кнопку меню.
     */
    public static class BasePlanViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewDescription;
        ImageButton buttonMenu;

        public BasePlanViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewDayName);
            textViewDescription = itemView.findViewById(R.id.tvDayDescription);
            buttonMenu = itemView.findViewById(R.id.buttonMenu);
        }
    }
}
