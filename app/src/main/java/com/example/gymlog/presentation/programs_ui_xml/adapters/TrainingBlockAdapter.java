package com.example.gymlog.presentation.programs_ui_xml.adapters;

import static com.example.gymlog.presentation.programs_ui_xml.adapters.BasePlanAdapter.forceShowIcons;
import static com.example.gymlog.presentation.programs_ui_xml.adapters.BasePlanAdapter.tintMenuIcons;

import android.content.Context;
import android.text.Html;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymlog.R;
import com.example.gymlog.domain.model.exercise.Equipment;
import com.example.gymlog.domain.model.exercise.ExerciseInBlock;
import com.example.gymlog.domain.model.exercise.Motion;
import com.example.gymlog.domain.model.exercise.MuscleGroup;
import com.example.gymlog.domain.model.plan.TrainingBlock;
import com.example.gymlog.data.local.legacy.PlanManagerDAO;

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
            setupDescription(block);
            setupMenu(block);
            setupExercises(block);
        }

        // Налаштовує текст опису блоку з урахуванням фільтрів
        void setupDescription(TrainingBlock block) {
            StringBuilder sb = new StringBuilder();
            String string = block.getDescription();

            // Основний опис
            if (!string.isEmpty()) {
                sb.append(string).append(" • ");
            }

            int count = 0;

            // М'язи
            if (!block.getMuscleGroupList().isEmpty()) {
                sb.append(context.getString(R.string.muscles)).append(": ");
                for (MuscleGroup muscle : block.getMuscleGroupList()) {
                    sb.append(muscle.getDescription(context)).append("; ");
                    if(count++ >= 1) break;
                }
                sb.append(" • ");
                count = 0;
            }

            // Тип руху
            if (!block.getMotions().isEmpty()) {
                sb.append(context.getString(R.string.motion)).append(": ");
                for (Motion motion : block.getMotions()) {
                    sb.append(motion.getDescription(context)).append("; ");
                    if(count++ >= 1) break;
                }
                sb.append(" • ");
                count = 0;
            }

            // Обладнання
            if (!block.getEquipmentList().isEmpty()) {
                sb.append(context.getString(R.string.equipment)).append(": ");
                for (Equipment eq : block.getEquipmentList()) {
                    sb.append(eq.getDescription(context)).append("; ");
                    if(count++ >= 1) break;
                }
                sb.append(" • ");
                count = 0;
            }

            description.setText(sb.toString().trim());

            // При натисканні на обрізаний опис показуємо AlertDialog з повним текстом
            description.setOnClickListener(v -> {
                new AlertDialog.Builder(context, R.style.RoundedDialogTheme)
                        .setTitle(context.getString(R.string.description))
                        .setMessage(sb.toString().trim())
                        .setPositiveButton("OK", null)
                        .show();
            });

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
                    TrainingBlockAdapter.this::onClickExerciseInBlock,
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


    private void onClickExerciseInBlock(ExerciseInBlock exerciseInBlock) {
//        Toast.makeText(context, exerciseInBlock.getNameOnly(context), Toast.LENGTH_SHORT).show();

        new AlertDialog.Builder(context, R.style.RoundedDialogTheme)
                .setTitle(context.getString(R.string.exercise_description))
                .setMessage(buildExerciseFullDescription(exerciseInBlock))
                .setPositiveButton("OK", null)
                .show();
    }

    // окремий метод для побудови повного опису вправи
    private CharSequence buildExerciseFullDescription(ExerciseInBlock exercise) {
        StringBuilder sb = new StringBuilder();

        // Назва вправи (жирний шрифт)
        sb.append("<b>").append(exercise.getNameOnly(context)).append("</b><br><br>");

        // Опис (перевірка на порожній рядок)
        if (!exercise.getDescription().isEmpty()) {
            sb.append(exercise.getDescription()).append("<br><br>");
        }

        // Обладнання
        if (exercise.getEquipment() != null) {
            sb.append("<b>")
                    .append(context.getString(R.string.equipment))
                    .append(": </b>")
                    .append(exercise.getEquipment().getDescription(context))
                    .append("<br><br>");
        }

        // Тип руху
        if (exercise.getMotion() != null) {
            sb.append("<b>")
                    .append(context.getString(R.string.motion))
                    .append(": </b>")
                    .append(exercise.getMotion().getDescription(context))
                    .append("<br><br>");
        }

        // Групи м'язів
        if (!exercise.getMuscleGroupList().isEmpty()) {
            sb.append("<b>")
                    .append(context.getString(R.string.muscle_groups))
                    .append(": </b><br>");

            for (MuscleGroup muscleGroup : exercise.getMuscleGroupList()) {
                sb.append("• ").append(muscleGroup.getDescription(context)).append("<br>");
            }
        }

        // Підтримка HTML-тексту для AlertDialog
        return Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_LEGACY);
    }

}
