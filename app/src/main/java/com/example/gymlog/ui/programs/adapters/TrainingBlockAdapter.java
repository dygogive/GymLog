package com.example.gymlog.ui.programs.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymlog.R;
import com.example.gymlog.model.exercise.ExerciseInBlock;
import com.example.gymlog.model.plan.TrainingBlock;
import com.example.gymlog.sqlopenhelper.PlanManagerDAO;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Адаптер для відображення списку тренувальних блоків (TrainingBlock).
 * Відповідає за створення, оновлення та взаємодію з елементами списку.
 */
public class TrainingBlockAdapter extends RecyclerView.Adapter<TrainingBlockAdapter.TrainingBlockViewHolder> {

    public interface OnTrainingBlockClickListener {
        void onEditClick(TrainingBlock block);       // Редагувати блок
        void onDeleteClick(TrainingBlock block);     // Видалити блок
        void onAddExercise(TrainingBlock block);     // Додати вправу
        void onEditExercises(TrainingBlock block);   // Редагувати вправи
        void onCloneTrainingBlock(TrainingBlock block);
    }

    private final Context context;
    private final List<TrainingBlock> trainingBlocks;
    private final PlanManagerDAO planManagerDAO;
    private final OnTrainingBlockClickListener listener;

    public TrainingBlockAdapter(
            Context context,
            List<TrainingBlock> trainingBlocks,
            PlanManagerDAO planManagerDAO,
            OnTrainingBlockClickListener listener
    ) {
        this.context = context;
        // Якщо trainingBlocks == null, створюємо пустий список (захист від NPE)
        this.trainingBlocks = (trainingBlocks != null) ? trainingBlocks : new ArrayList<>();
        this.planManagerDAO = planManagerDAO;
        this.listener = listener;

        setHasStableIds(true);
    }

    @NonNull
    @Override
    public TrainingBlockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_training_block, parent, false);
        return new TrainingBlockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrainingBlockViewHolder holder, int position) {
        TrainingBlock block = trainingBlocks.get(position);
        holder.textViewBlockName.setText(block.getName());
        holder.textViewBlockDescription.setText(block.getDescription());

        // Налаштовуємо контекстне меню (три крапки)
        setupBlockMenu(holder, block);

        // Налаштовуємо внутрішній список вправ
        setupExercisesRecyclerView(holder, block);
    }

    /**
     * Відображає список вправ, прив'язаних до конкретного TrainingBlock.
     */
    private void setupExercisesRecyclerView(@NonNull TrainingBlockViewHolder holder, @NonNull TrainingBlock block) {
        // Завантажуємо вправи з бази
        List<ExerciseInBlock> exercises = planManagerDAO.getBlockExercises(block.getId());

        // Створюємо адаптер (AdapterExercisesInTrainingBlock)
        AdapterExercisesInTrainingBlock exerciseAdapter = new AdapterExercisesInTrainingBlock(
                context,
                exercises,
                exercise -> {
                    // Просто покажемо Toast із назвою вправи
                    Toast.makeText(context, exercise.getNameOnly(context), Toast.LENGTH_SHORT).show();
                }
        );

        holder.recyclerViewExercises.setAdapter(exerciseAdapter);

        // Налаштовуємо drag&drop і swipe для вправ
        setupItemTouchHelper(holder, block, exerciseAdapter);
    }

    /**
     * Налаштовує ItemTouchHelper для drag & drop (зміна позицій) і swipe (видалення).
     */
    private void setupItemTouchHelper(@NonNull TrainingBlockViewHolder holder,
                                      @NonNull TrainingBlock block,
                                      @NonNull AdapterExercisesInTrainingBlock exerciseAdapter) {
        if (holder.itemTouchHelper != null) {
            // Від'єднуємо, щоб не було дублювання
            holder.itemTouchHelper.attachToRecyclerView(null);
        }

        ItemTouchHelper.Callback callback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT
        ) {
            @Override
            public boolean onMove(@NonNull RecyclerView rv,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                int fromPos = viewHolder.getBindingAdapterPosition();
                int toPos   = target.getBindingAdapterPosition();

                // Змінюємо позиції в локальному списку
                exerciseAdapter.moveItem(fromPos, toPos, planManagerDAO, block.getId());
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.LEFT) {
                    int pos = viewHolder.getBindingAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        // Видаляємо вправу з бази і локального списку
                        ExerciseInBlock removedEx = exerciseAdapter.getItems().get(pos);
                        planManagerDAO.removeExerciseFromBlock(block.getId(), removedEx.getId(), removedEx.getPosition());

                        // Видаляємо з адаптера
                        exerciseAdapter.getItems().remove(pos);
                        exerciseAdapter.notifyItemRemoved(pos);

                        // Перепризначимо позиції решті вправ
                        for (int i = pos; i < exerciseAdapter.getItems().size(); i++) {
                            exerciseAdapter.getItems().get(i).setPosition(i);
                        }
                        planManagerDAO.updateTrainingBlockExercises(block.getId(), exerciseAdapter.getItems());
                    }
                }
            }
        };

        holder.itemTouchHelper = new ItemTouchHelper(callback);
        holder.itemTouchHelper.attachToRecyclerView(holder.recyclerViewExercises);
    }

    /**
     * Налаштовує контекстне меню (три крапки) із опціями блоку: редагувати, видалити, додати вправу, клонувати тощо.
     */

    private void setupBlockMenu(@NonNull TrainingBlockViewHolder holder, @NonNull TrainingBlock block) {
        holder.buttonMenu.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, holder.buttonMenu);
            popup.getMenuInflater().inflate(R.menu.training_block_menu, popup.getMenu());

            // Якщо ти хочеш, щоб іконки показувалися завжди (у т.ч. на старих пристроях) — Reflection hack
            forceShowIcons(popup);

            popup.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_edit_block) {
                    listener.onEditClick(block);
                    return true;
                } else if (itemId == R.id.menu_delete_block) {
                    listener.onDeleteClick(block);
                    return true;
                } else if (itemId == R.id.menu_add_exercise) {
                    listener.onAddExercise(block);
                    return true;
                } else if (itemId == R.id.menu_edit_exercises) {
                    listener.onEditExercises(block);
                    return true;
                } else if (itemId == R.id.menu_clone_block) {
                    listener.onCloneTrainingBlock(block);
                    return true;
                }
                return false;
            });

            // Програмно встановимо колір іконок (не завжди потрібно)
            tintMenuIcons(popup);

            popup.show();
        });
    }

    /**
     * Метод-допоміжник: “хак” через Reflection, аби іконки відображалися у PopupMenu навіть на старих Android.
     */
    private void forceShowIcons(PopupMenu popup) {
        try {
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Змінює колір іконок у меню, якщо іконки взагалі є.
     */
    private void tintMenuIcons(@NonNull PopupMenu popup) {
        Menu menu = popup.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            Drawable icon = item.getIcon();
            if (icon != null) {
                icon.mutate();
                icon.setTint(ContextCompat.getColor(context, R.color.text_color));
                item.setIcon(icon);
            }
        }
    }

    @Override
    public long getItemId(int position) {
        // Повертаємо унікальний ID для стабільних ідентифікаторів
        return trainingBlocks.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return trainingBlocks.size();
    }

    /**
     * Зміна позиції блоку у списку (для drag&drop між блоками).
     */
    public void moveItem(int fromPosition, int toPosition) {
        TrainingBlock removed = trainingBlocks.remove(fromPosition);
        trainingBlocks.add(toPosition, removed);
        notifyItemMoved(fromPosition, toPosition);
    }

    public static class TrainingBlockViewHolder extends RecyclerView.ViewHolder {
        final TextView textViewBlockName;
        final TextView textViewBlockDescription;
        final ImageButton buttonMenu;
        final RecyclerView recyclerViewExercises;
        ItemTouchHelper itemTouchHelper;

        public TrainingBlockViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewBlockName = itemView.findViewById(R.id.textViewBlockName);
            textViewBlockDescription = itemView.findViewById(R.id.textViewBlockDescription);
            buttonMenu = itemView.findViewById(R.id.buttonMenu);

            recyclerViewExercises = itemView.findViewById(R.id.recyclerViewExercises);
            recyclerViewExercises.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        }
    }
}
