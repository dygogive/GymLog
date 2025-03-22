package com.example.gymlog.ui.plan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymlog.R;
import com.example.gymlog.data.db.PlanManagerDAO;
import com.example.gymlog.data.exercise.ExerciseInBlock;
import com.example.gymlog.data.plan.TrainingBlock;

import java.util.ArrayList;
import java.util.List;

/**
 * Адаптер для відображення списку тренувальних блоків (TrainingBlock).
 */
public class TrainingBlockAdapter extends RecyclerView.Adapter<TrainingBlockAdapter.TrainingBlockViewHolder> {

    public interface OnTrainingBlockClickListener {
        void onEditClick(TrainingBlock block);
        void onDeleteClick(TrainingBlock block);
        void onAddExercise(TrainingBlock block);
        void onEditExercises(TrainingBlock block);
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
        this.trainingBlocks = trainingBlocks != null ? trainingBlocks : new ArrayList<>();
        this.planManagerDAO = planManagerDAO;
        this.listener = listener;

        // Увімкнення стабільних ідентифікаторів (щоб уникати “блимання” при переміщеннях)
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public TrainingBlockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_training_block, parent, false);
        return new TrainingBlockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrainingBlockViewHolder holder, int position) {
        TrainingBlock block = trainingBlocks.get(position);

        holder.textViewBlockName.setText(block.getName());
        holder.textViewBlockDescription.setText(block.getDescription());

        // Контекстне меню блоку
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

        // Завантажуємо список вправ блоку:
        List<ExerciseInBlock> exercises = planManagerDAO.getBlockExercises(block.getId());

        // Створюємо внутрішній адаптер для вправ
        AdapterExercisesInTrainingBlock exerciseAdapter = new AdapterExercisesInTrainingBlock(
                context,
                exercises,
                // Клік на вправу (короткий тап)
                exercise -> Toast.makeText(context, exercise.getNameOnly(context), Toast.LENGTH_SHORT).show()
        );

        holder.recyclerViewExercises.setAdapter(exerciseAdapter);

        // Якщо раніше до цього ViewHolder уже було прикріплено ItemTouchHelper – відчепимо його
        if (holder.itemTouchHelper != null) {
            holder.itemTouchHelper.attachToRecyclerView(null);
        }

        // Створюємо новий callback, щоб передати blockId і забезпечити
        // Drag (UP | DOWN) + Swipe (LEFT).
        ItemTouchHelper.Callback callback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT // <-- Додано swipe вліво
        ) {
            @Override
            public boolean onMove(@NonNull RecyclerView rv,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getBindingAdapterPosition();
                int toPosition = target.getBindingAdapterPosition();
                // Оновити позиції в exercises:
                exerciseAdapter.moveItem(fromPosition, toPosition, planManagerDAO, block.getId());
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Ловимо свайп вліво, щоб видалити вправу
                if (direction == ItemTouchHelper.LEFT) {
                    int pos = viewHolder.getBindingAdapterPosition();
                    // Перевіримо, що позиція валідна
                    if (pos != RecyclerView.NO_POSITION) {
                        ExerciseInBlock removedExercise = exerciseAdapter.getItems().get(pos);

                        // 1) Видаляємо з БД
                        //    Можна напряму використати removeExerciseFromBlock(), якщо хочеш зберегти
                        //    унікальний position. Але, щоб було простіше, показую варіант "повного" оновлення:
                        planManagerDAO.removeExerciseFromBlock(
                                block.getId(),
                                removedExercise.getId(),
                                removedExercise.getPosition()
                        );

                        // 2) Видаляємо з локального списку
                        exerciseAdapter.getItems().remove(pos);
                        exerciseAdapter.notifyItemRemoved(pos);

                        // 3) Перепризначаємо позиції решті елементів (і оновлюємо в БД)
                        for (int i = pos; i < exerciseAdapter.getItems().size(); i++) {
                            exerciseAdapter.getItems().get(i).setPosition(i);
                        }
                        planManagerDAO.updateTrainingBlockExercises(block.getId(), exerciseAdapter.getItems());
                    }
                }
            }
        };

        // Прикріплюємо ItemTouchHelper один раз (на поточний ViewHolder)
        holder.itemTouchHelper = new ItemTouchHelper(callback);
        holder.itemTouchHelper.attachToRecyclerView(holder.recyclerViewExercises);
    }

    /**
     * Увімкнувши setHasStableIds(true), ми мусимо перевизначити getItemId.
     * Тут використовується унікальний id самого TrainingBlock.
     */
    @Override
    public long getItemId(int position) {
        return trainingBlocks.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return trainingBlocks.size();
    }

    /**
     * Метод для переміщення блоків (Drag & Drop між самими блоками).
     * Викликається з ItemTouchHelper, який налаштований в іншому місці (для блоків).
     */
    public void moveItem(int fromPosition, int toPosition) {
        // “Вирізати” і “вставити” (замість Collections.swap)
        TrainingBlock removed = trainingBlocks.remove(fromPosition);
        trainingBlocks.add(toPosition, removed);
        notifyItemMoved(fromPosition, toPosition);
    }

    /**
     * ViewHolder для одного тренувального блоку.
     */
    public static class TrainingBlockViewHolder extends RecyclerView.ViewHolder {
        final TextView textViewBlockName;
        final TextView textViewBlockDescription;
        final ImageButton buttonMenu;
        final RecyclerView recyclerViewExercises;

        // Зберігаємо ItemTouchHelper, щоб не створювати його кілька разів
        ItemTouchHelper itemTouchHelper;

        public TrainingBlockViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewBlockName = itemView.findViewById(R.id.textViewBlockName);
            textViewBlockDescription = itemView.findViewById(R.id.textViewBlockDescription);
            buttonMenu = itemView.findViewById(R.id.buttonMenu);

            // Налаштуємо LayoutManager один раз в конструкторі ViewHolder
            recyclerViewExercises = itemView.findViewById(R.id.recyclerViewExercises);
            recyclerViewExercises.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        }
    }
}
