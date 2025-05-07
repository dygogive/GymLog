package com.example.gymlog.ui.legacy.program.adapters;

import static com.example.gymlog.ui.legacy.program.adapters.BasePlanAdapter.forceShowIcons;
import static com.example.gymlog.ui.legacy.program.adapters.BasePlanAdapter.tintMenuIcons;

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
import com.example.gymlog.domain.model.legacy.attribute.equipment.Equipment;
import com.example.gymlog.domain.model.legacy.exercise.ExerciseInBlock;
import com.example.gymlog.domain.model.legacy.attribute.motion.Motion;
import com.example.gymlog.domain.model.legacy.attribute.muscle.MuscleGroup;
import com.example.gymlog.domain.model.legacy.plan.TrainingBlock;
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
        //changing position of a block
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
            String description = block.getDescription().trim();

            // Основний опис
            if (!description.isEmpty()) {
                sb.append(description).append("\n");
            }

            // Формування опису з обмеженням на 2 елементи в кожній категорії
            appendAttributeList(sb, R.string.muscles, block.getMuscleGroupList(), 2);
            appendAttributeList(sb, R.string.motion, block.getMotions(), 2);
            appendAttributeList(sb, R.string.equipment, block.getEquipmentList(), 2);

            String finalDescription = sb.toString().trim();

            this.description.setText(finalDescription);

            // Обробник кліку для показу повного опису
            this.description.setOnClickListener(v -> showFullDescription(block));
        }

        /**
         * Додає до опису перелік атрибутів (м'язів, рухів, обладнання) з обмеженням кількості
         * @param sb StringBuilder для додавання тексту
         * @param stringResId ID ресурсу з назвою категорії
         * @param items список атрибутів
         * @param maxItems максимальна кількість елементів для відображення
         */
        private <T> void appendAttributeList(StringBuilder sb, int stringResId, List<T> items, int maxItems) {
            if (items == null || items.isEmpty()) return;

            sb.append(context.getString(stringResId)).append(": ");

            int count = 0;
            for (T item : items) {
                if (count > 0) sb.append(", ");

                String description = "";
                if (item instanceof MuscleGroup) {
                    description = ((MuscleGroup) item).getDescription(context);
                } else if (item instanceof Motion) {
                    description = ((Motion) item).getDescription(context);
                } else if (item instanceof Equipment) {
                    description = ((Equipment) item).getDescription(context);
                }

                // Перетворення першої літери на малу
                if (!description.isEmpty()) {
                    description = description.substring(0, 1).toLowerCase() + description.substring(1);
                }

                sb.append(description);
                count++;

                if (count >= maxItems) {
                    if (items.size() > maxItems) {
                        sb.append(", ...");
                    }
                    break;
                }
            }

            sb.append("\n");
        }

        /**
         * Показує повний опис блоку в діалоговому вікні
         */
        private void showFullDescription(TrainingBlock block) {
            StringBuilder fullDescription = new StringBuilder();

            // Основний опис
            String description = block.getDescription().trim();
            if (!description.isEmpty()) {
                fullDescription.append(description).append("\n\n");
            }

            // Детальний опис кожної категорії
            appendFullCategoryDescription(fullDescription, R.string.muscles, block.getMuscleGroupList());
            appendFullCategoryDescription(fullDescription, R.string.motion, block.getMotions());
            appendFullCategoryDescription(fullDescription, R.string.equipment, block.getEquipmentList());

            new AlertDialog.Builder(context, R.style.RoundedDialogTheme)
                    .setTitle(block.getName())
                    .setMessage(fullDescription.toString().trim())
                    .setPositiveButton("OK", null)
                    .show();
        }

        /**
         * Додає до повного опису всі елементи категорії
         */
        private <T> void appendFullCategoryDescription(StringBuilder sb, int stringResId, List<T> items) {
            if (items == null || items.isEmpty()) return;

            sb.append(context.getString(stringResId)).append(":\n");

            for (T item : items) {
                sb.append("• ");
                if (item instanceof MuscleGroup) {
                    sb.append(((MuscleGroup) item).getDescription(context));
                } else if (item instanceof Motion) {
                    sb.append(((Motion) item).getDescription(context));
                } else if (item instanceof Equipment) {
                    sb.append(((Equipment) item).getDescription(context));
                }
                sb.append("\n");
            }
            sb.append("\n");
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

    //
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
