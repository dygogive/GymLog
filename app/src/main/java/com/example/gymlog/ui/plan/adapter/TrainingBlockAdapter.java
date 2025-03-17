package com.example.gymlog.ui.plan.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymlog.R;
import com.example.gymlog.data.db.PlanManagerDAO;
import com.example.gymlog.data.exercise.Exercise;
import com.example.gymlog.data.plan.TrainingBlock;

import java.util.Collections;
import java.util.List;

/**
 * Адаптер для відображення списку тренувальних блоків (TrainingBlock).
 * Здійснює:
 *  - Відображення назви та опису
 *  - Кнопки "редагувати" і "видалити"
 *  - Перетягування (drag & drop) через метод moveItem()
 *  - Підвантаження та відображення списку вправ (Exercise) у кожному блоці
 */
public class TrainingBlockAdapter extends RecyclerView.Adapter<TrainingBlockAdapter.TrainingBlockViewHolder> {

    // Інтерфейс, що обробляє натискання на "редагувати" / "видалити"
    public interface OnTrainingBlockClickListener {
        void onEditClick(TrainingBlock block);
        void onDeleteClick(TrainingBlock block);
    }

    // Контекст, список блоків, DAO, слухач
    private final Context context;
    private final List<TrainingBlock> trainingBlocks;
    private final PlanManagerDAO planManagerDAO;
    private final OnTrainingBlockClickListener listener;

    /**
     * Конструктор адаптера.
     *
     * @param context         Поточний контекст
     * @param trainingBlocks  Список тренувальних блоків
     * @param planManagerDAO  DAO для роботи з базою даних
     * @param listener        Слухач натискань на "редагувати" і "видалити"
     */
    public TrainingBlockAdapter(
            Context context,
            List<TrainingBlock> trainingBlocks,
            PlanManagerDAO planManagerDAO,
            OnTrainingBlockClickListener listener
    ) {
        this.context = context;
        this.trainingBlocks = trainingBlocks;
        this.planManagerDAO = planManagerDAO;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TrainingBlockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Інфлейтимо лейаут item_training_block
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_training_block, parent, false);
        return new TrainingBlockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrainingBlockViewHolder holder, int position) {
        // Отримуємо поточний блок
        TrainingBlock block = trainingBlocks.get(position);

        // Назва та опис блоку
        holder.textViewBlockName.setText(block.getName());
        holder.textViewBlockDescription.setText(block.getDescription());

        // Кнопки: "Редагувати" і "Видалити"
        holder.buttonEdit.setOnClickListener(v -> listener.onEditClick(block));
        holder.buttonDelete.setOnClickListener(v -> listener.onDeleteClick(block));

        // Якщо опис довгий, при натисканні відкриваємо AlertDialog з повним описом
        holder.textViewBlockDescription.setOnClickListener(v -> {
            String desc = block.getDescription();
            if (desc.length() > 45) {
                new AlertDialog.Builder(context, R.style.RoundedDialogTheme)
                        .setTitle(block.getName())
                        .setMessage(desc)
                        .setPositiveButton("OK", null)
                        .show();
            }
        });

        // Завантажуємо вправи, які відфільтровані для цього блоку
        List<Exercise> exercises = planManagerDAO.getExercisesForTrainingBlock(block.getId());
        AdapterExercisesInTrainingBlock exerciseAdapter = new AdapterExercisesInTrainingBlock(
                context,
                exercises,
                exe -> Toast.makeText(context,
                        "Exercise Info: " + exe.getNameOnly(context),
                        Toast.LENGTH_SHORT).show()
        );

        // Налаштовуємо внутрішній RecyclerView для списку вправ
        holder.recyclerViewExercises.setLayoutManager(
                new LinearLayoutManager(holder.itemView.getContext())
        );
        holder.recyclerViewExercises.setAdapter(exerciseAdapter);
        holder.recyclerViewExercises.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return trainingBlocks.size();
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
                Collections.swap(trainingBlocks, i, i + 1);
            }
        } else {
            // зсув вгору
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(trainingBlocks, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    /**
     * ViewHolder для одного тренувального блоку.
     * Містить поля для:
     *  - назви, опису
     *  - кнопки редагування / видалення
     *  - внутрішнього RecyclerView зі списком вправ
     */
    public static class TrainingBlockViewHolder extends RecyclerView.ViewHolder {
        TextView textViewBlockName, textViewBlockDescription;
        ImageButton buttonEdit, buttonDelete;
        RecyclerView recyclerViewExercises;

        public TrainingBlockViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewBlockName        = itemView.findViewById(R.id.textViewBlockName);
            textViewBlockDescription = itemView.findViewById(R.id.textViewBlockDescription);
            buttonEdit               = itemView.findViewById(R.id.buttonEditBlock);
            buttonDelete             = itemView.findViewById(R.id.buttonDeleteBlock);
            recyclerViewExercises    = itemView.findViewById(R.id.recyclerViewExercises);
        }
    }
}
