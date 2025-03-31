package com.example.gymlog.ui.programs.adapters;

import static com.example.gymlog.ui.programs.adapters.BasePlanAdapter.forceShowIcons;
import static com.example.gymlog.ui.programs.adapters.BasePlanAdapter.tintMenuIcons;

import android.content.Context;
import android.view.HapticFeedbackConstants;
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
import com.example.gymlog.model.exercise.ExerciseInBlock;
import com.example.gymlog.model.plan.TrainingBlock;
import com.example.gymlog.sqlopenhelper.PlanManagerDAO;

import java.util.ArrayList;
import java.util.List;

/**
 * Адаптер для відображення тренувальних блоків із вправами та меню.
 */
public class TrainingBlockAdapter extends RecyclerView.Adapter<TrainingBlockAdapter.TrainingBlockViewHolder> {

    public interface OnMenuTrainingBlockListener {
        void onEditClick(TrainingBlock block);       // Редагувати блок
        void onDeleteClick(TrainingBlock block);     // Видалити блок
        void onAddExercise(TrainingBlock block);     // Додати вправу
        void onEditExercises(TrainingBlock block);   // Редагувати вправи
        void onCloneTrainingBlock(TrainingBlock block); // Клонувати блок
    }

    public interface OnLongMenuTrainBlockListener {
        void onLongPress(RecyclerView.ViewHolder viewHolder);
    }

    private final Context context;
    private final List<TrainingBlock> blocks;
    private final PlanManagerDAO dao;
    private final OnMenuTrainingBlockListener menuTrainingBlockListener;
    private final OnLongMenuTrainBlockListener longMenuTrainBlockListener;

    public TrainingBlockAdapter(Context context, List<TrainingBlock> blocks,
                                PlanManagerDAO dao, OnMenuTrainingBlockListener menuTrainingBlockListener, OnLongMenuTrainBlockListener longMenuTrainBlockListener) {
        this.context = context;
        this.blocks = blocks != null ? blocks : new ArrayList<>();
        this.dao = dao;
        this.menuTrainingBlockListener = menuTrainingBlockListener;
        this.longMenuTrainBlockListener = longMenuTrainBlockListener;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public TrainingBlockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TrainingBlockViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_training_block, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull TrainingBlockViewHolder holder, int position) {
        TrainingBlock block = blocks.get(position);
        holder.bind(block);
    }

    @Override
    public int getItemCount() {
        return blocks.size();
    }

    @Override
    public long getItemId(int position) {
        return blocks.get(position).getId();
    }

    /**
     * Переміщує блок (drag & drop) в списку блоків.
     */
    public void moveItem(int fromPosition, int toPosition) {
        TrainingBlock block = blocks.remove(fromPosition);
        blocks.add(toPosition, block);
        notifyItemMoved(fromPosition, toPosition);
    }

    // ViewHolder для блока тренування
    class TrainingBlockViewHolder extends RecyclerView.ViewHolder {

        final TextView name, description;
        final ImageButton menu;
        final RecyclerView exercisesRecycler;
        ItemTouchHelper exercisesItemTouchHelper;

        TrainingBlockViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textViewBlockName);
            description = itemView.findViewById(R.id.textViewBlockDescription);
            menu = itemView.findViewById(R.id.buttonMenu);
            exercisesRecycler = itemView.findViewById(R.id.recyclerViewExercises);
            exercisesRecycler.setLayoutManager(new LinearLayoutManager(context));
        }

        void bind(TrainingBlock block) {
            name.setText(block.getName());
            description.setText(block.getDescription());

            setupMenu(block);
            setupExercises(block);
        }

        // Налаштовує меню блоку (три крапки)
        void setupMenu(TrainingBlock block) {
            menu.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(context, menu);
                popup.inflate(R.menu.training_block_menu);
                forceShowIcons(popup); // Примусове відображення іконок у меню
                popup.setOnMenuItemClickListener(item -> {
                    int itemId = item.getItemId();
                    if (itemId == R.id.menu_edit_block) {
                        menuTrainingBlockListener.onEditClick(block);
                    } else if (itemId == R.id.menu_delete_block) {
                        menuTrainingBlockListener.onDeleteClick(block);
                    } else if (itemId == R.id.menu_add_exercise) {
                        menuTrainingBlockListener.onAddExercise(block);
                    } else if (itemId == R.id.menu_edit_exercises) {
                        menuTrainingBlockListener.onEditExercises(block);
                    } else if (itemId == R.id.menu_clone_block) {
                        menuTrainingBlockListener.onCloneTrainingBlock(block);
                    }
                    return true;
                });
                tintMenuIcons(popup.getMenu(), TrainingBlockViewHolder.this);
                popup.show();
            });


            // Повністю вимикаємо стандартні кліки та звуки для кнопки
            menu.setClickable(true);
            menu.setLongClickable(true);
            menu.setHapticFeedbackEnabled(false);
            menu.setSoundEffectsEnabled(false);
            menu.setOnLongClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS); //відрація довгого натискання
                longMenuTrainBlockListener.onLongPress(TrainingBlockViewHolder.this);
                return true;
            });
        }

        // Відображення списку вправ
        void setupExercises(TrainingBlock block) {
            List<ExerciseInBlock> exercises = dao.getBlockExercises(block.getId());

            AdapterExercisesInTrainingBlock adapterExercises = new AdapterExercisesInTrainingBlock(
                    context, exercises,
                    exercise -> Toast.makeText(context, exercise.getNameOnly(context), Toast.LENGTH_SHORT).show(),
                    viewHolder -> exercisesItemTouchHelper.startDrag(viewHolder)
            );

            exercisesRecycler.setAdapter(adapterExercises);
            setupExercisesTouchHelper(adapterExercises, block);
        }

        // Drag & Drop вправ всередині блоку
        void setupExercisesTouchHelper(AdapterExercisesInTrainingBlock adapter, TrainingBlock block) {

            //відкріпити exercisesRecycler від старого exercisesItemTouchHelper
            if (exercisesItemTouchHelper != null)
                exercisesItemTouchHelper.attachToRecyclerView(null);

            //налаштувати новий exercisesItemTouchHelper
            exercisesItemTouchHelper = new ItemTouchHelper(
                    new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {

                        @Override
                        public boolean isLongPressDragEnabled() {
                            return false; // тільки вручну через іконку
                        }

                        @Override
                        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                        }

                        @Override
                        public boolean onMove(@NonNull RecyclerView recyclerView,
                                              @NonNull RecyclerView.ViewHolder from,
                                              @NonNull RecyclerView.ViewHolder to) {
                            //в адаптері списку вправ у блоці міняю місцями ітеми (вправи), а також і в базі
                            adapter.moveItem(from.getBindingAdapterPosition(), to.getBindingAdapterPosition(), dao, block.getId());
                            return true;
                        }

                    }
            );
            exercisesItemTouchHelper.attachToRecyclerView(exercisesRecycler);
        }
    }
}
