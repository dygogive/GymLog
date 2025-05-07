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

        final TextView name, description, muscleGroups, motions, equipment;
        final ImageButton menu;
        final RecyclerView exercisesRecycler;
        ItemTouchHelper exercisesItemTouchHelper;

        TrainingBlockViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textViewBlockName);
            description = itemView.findViewById(R.id.textViewBlockDescription);
            muscleGroups = itemView.findViewById(R.id.textViewMuscleGroups);
            motions = itemView.findViewById(R.id.textViewMotions);
            equipment = itemView.findViewById(R.id.textViewEquipment);
            menu = itemView.findViewById(R.id.buttonMenu);
            exercisesRecycler = itemView.findViewById(R.id.recyclerViewExercises);
            exercisesRecycler.setLayoutManager(new LinearLayoutManager(context));
        }

        void bind(TrainingBlock block) {
            name.setText(block.getName());
            setupDescriptionAndAttributes(block);
            setupMenu(block);
            setupExercises(block);
        }

        // Налаштовує текст опису та атрибутів блоку
        void setupDescriptionAndAttributes(TrainingBlock block) {
            // Основний опис
            String desc = block.getDescription().trim();
            if (desc.isEmpty()) {
                description.setVisibility(View.GONE);
            } else {
                description.setVisibility(View.VISIBLE);
                description.setText(desc);

                // Обмеження основного опису до 3 рядків
                if (desc.length() > 150) { // приблизно 150 символів на 3 рядки
                    String shortenedDesc = desc.substring(0, Math.min(desc.length(), 147)) + "...";
                    description.setText(shortenedDesc);
                }
            }

            // Встановлення атрибутів у окремі TextView
            setupMuscleGroups(block);
            setupMotions(block);
            setupEquipment(block);

            // Налаштування обробників натискання для всіх TextView
            View.OnClickListener descriptionClickListener = v -> showFullDescription(block);
            description.setOnClickListener(descriptionClickListener);
            muscleGroups.setOnClickListener(descriptionClickListener);
            motions.setOnClickListener(descriptionClickListener);
            equipment.setOnClickListener(descriptionClickListener);
        }

        // Налаштування групи м'язів
        private void setupMuscleGroups(TrainingBlock block) {
            List<MuscleGroup> muscleGroupList = block.getMuscleGroupList();

            if (muscleGroupList == null || muscleGroupList.isEmpty()) {
                muscleGroups.setVisibility(View.GONE);
                return;
            }

            muscleGroups.setVisibility(View.VISIBLE);
            StringBuilder sb = new StringBuilder();
            sb.append(context.getString(R.string.muscles)).append(": ");

            // Розрахунок максимальної довжини тексту
            int maxLength = 100; // Приблизна кількість символів, що поміщається в TextView з maxLines=3
            int currentLength = sb.length();
            boolean addedEllipsis = false;

            for (int i = 0; i < muscleGroupList.size(); i++) {
                MuscleGroup item = muscleGroupList.get(i);

                // Підготовка тексту опису
                String description = item.getDescription(context);
                if (!description.isEmpty()) {
                    description = description.substring(0, 1).toLowerCase() + description.substring(1);
                }

                // Текст для додавання (з комою, якщо не перший елемент)
                String textToAdd = (i > 0 ? ", " : "") + description;

                // Перевірка, чи поміститься новий текст + "..." якщо потрібно
                if (currentLength + textToAdd.length() > maxLength - 4) { // 4 символи для ", ..."
                    if (!addedEllipsis && i < muscleGroupList.size() - 1) {
                        sb.append(", ...");
                        addedEllipsis = true;
                    }
                    break;
                }

                // Додавання тексту
                if (i > 0) sb.append(", ");
                sb.append(description);
                currentLength += textToAdd.length();
            }

            muscleGroups.setText(sb.toString());
        }

        // Налаштування типів рухів
        private void setupMotions(TrainingBlock block) {
            List<Motion> motionsList = block.getMotions();

            if (motionsList == null || motionsList.isEmpty()) {
                motions.setVisibility(View.GONE);
                return;
            }

            motions.setVisibility(View.VISIBLE);
            StringBuilder sb = new StringBuilder();
            sb.append(context.getString(R.string.motion)).append(": ");

            // Розрахунок максимальної довжини тексту
            int maxLength = 100; // Приблизна кількість символів, що поміщається в TextView з maxLines=3
            int currentLength = sb.length();
            boolean addedEllipsis = false;

            for (int i = 0; i < motionsList.size(); i++) {
                Motion item = motionsList.get(i);

                // Підготовка тексту опису
                String description = item.getDescription(context);
                if (!description.isEmpty()) {
                    description = description.substring(0, 1).toLowerCase() + description.substring(1);
                }

                // Текст для додавання (з комою, якщо не перший елемент)
                String textToAdd = (i > 0 ? ", " : "") + description;

                // Перевірка, чи поміститься новий текст + "..." якщо потрібно
                if (currentLength + textToAdd.length() > maxLength - 4) { // 4 символи для ", ..."
                    if (!addedEllipsis && i < motionsList.size() - 1) {
                        sb.append(", ...");
                        addedEllipsis = true;
                    }
                    break;
                }

                // Додавання тексту
                if (i > 0) sb.append(", ");
                sb.append(description);
                currentLength += textToAdd.length();
            }

            motions.setText(sb.toString());
        }

        // Налаштування обладнання
        private void setupEquipment(TrainingBlock block) {
            List<Equipment> equipmentList = block.getEquipmentList();

            if (equipmentList == null || equipmentList.isEmpty()) {
                equipment.setVisibility(View.GONE);
                return;
            }

            equipment.setVisibility(View.VISIBLE);
            StringBuilder sb = new StringBuilder();
            sb.append(context.getString(R.string.equipment)).append(": ");

            // Розрахунок максимальної довжини тексту
            int maxLength = 100; // Приблизна кількість символів, що поміщається в TextView з maxLines=3
            int currentLength = sb.length();
            boolean addedEllipsis = false;

            for (int i = 0; i < equipmentList.size(); i++) {
                Equipment item = equipmentList.get(i);

                // Підготовка тексту опису
                String description = item.getDescription(context);
                if (!description.isEmpty()) {
                    description = description.substring(0, 1).toLowerCase() + description.substring(1);
                }

                // Текст для додавання (з комою, якщо не перший елемент)
                String textToAdd = (i > 0 ? ", " : "") + description;

                // Перевірка, чи поміститься новий текст + "..." якщо потрібно
                if (currentLength + textToAdd.length() > maxLength - 4) { // 4 символи для ", ..."
                    if (!addedEllipsis && i < equipmentList.size() - 1) {
                        sb.append(", ...");
                        addedEllipsis = true;
                    }
                    break;
                }

                // Додавання тексту
                if (i > 0) sb.append(", ");
                sb.append(description);
                currentLength += textToAdd.length();
            }

            equipment.setText(sb.toString());
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