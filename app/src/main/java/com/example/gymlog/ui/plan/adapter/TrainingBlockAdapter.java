package com.example.gymlog.ui.plan.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymlog.R;
import com.example.gymlog.data.db.PlanManagerDAO;
import com.example.gymlog.data.exercise.Exercise;
import com.example.gymlog.data.plan.TrainingBlock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        void onAddExercise(TrainingBlock block);
        void onEditExercises(TrainingBlock block);
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
        TrainingBlock block = trainingBlocks.get(position);

        holder.textViewBlockName.setText(block.getName());
        holder.textViewBlockDescription.setText(block.getDescription());

        holder.buttonMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.buttonMenu);
            popupMenu.getMenuInflater().inflate(R.menu.training_block_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_edit_block) {
                    listener.onEditClick(block);
                } else if (item.getItemId() == R.id.menu_delete_block) {
                    listener.onDeleteClick(block);
                } else if (item.getItemId() == R.id.menu_add_exercise) {
                    listener.onAddExercise(block);
                } else if (item.getItemId() == R.id.menu_edit_exercises) {
                    listener.onEditExercises(block);
                }
                return true;
            });

            popupMenu.show();
        });

        // Тепер все просто і швидко:
        List<Exercise> exercises = planManagerDAO.getBlockExercises(block.getId());

        AdapterExercisesInTrainingBlock exerciseAdapter = new AdapterExercisesInTrainingBlock(
                context,
                exercises,
                exe -> Toast.makeText(context, exe.getNameOnly(context), Toast.LENGTH_SHORT).show()
        );

        holder.recyclerViewExercises.setLayoutManager(new LinearLayoutManager(context));
        holder.recyclerViewExercises.setAdapter(exerciseAdapter);
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
        ImageButton buttonMenu;
        RecyclerView recyclerViewExercises;

        public TrainingBlockViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewBlockName        = itemView.findViewById(R.id.textViewBlockName);
            textViewBlockDescription = itemView.findViewById(R.id.textViewBlockDescription);
            recyclerViewExercises    = itemView.findViewById(R.id.recyclerViewExercises);
            buttonMenu               = itemView.findViewById(R.id.buttonMenu); // Контекстне меню
        }
    }
}
