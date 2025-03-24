package com.example.gymlog.ui.plan.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.gymlog.R;
import com.example.gymlog.data.db.ExerciseDAO;
import com.example.gymlog.data.db.PlanManagerDAO;
import com.example.gymlog.data.exercise.Equipment;
import com.example.gymlog.data.exercise.Exercise;
import com.example.gymlog.data.exercise.ExerciseInBlock;
import com.example.gymlog.data.exercise.Motion;
import com.example.gymlog.data.exercise.MuscleGroup;
import com.example.gymlog.data.plan.TrainingBlock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Діалог для створення/редагування тренувального блоку (TrainingBlock).
 * Містить логіку вибору фільтрів (рухи, м'язеві групи, обладнання).
 */
public class TrainingBlockDialog extends Dialog {

    // Контекст
    private final Context context;

    // Поля вводу для назви і опису
    private EditText editTextBlockName, editTextBlockDescription;

    // Три TextView для відображення підписів фільтрів (Motion, Muscle, Equipment)
    private TextView textViewFilterMotion, textViewFilterMuscle, textViewFilterEquipment;

    // Кнопки вибору фільтрів і керування діалогом
    private Button buttonSelectMotion, buttonSelectMuscle, buttonSelectEquipment;
    private Button buttonCancel, buttonSaveBlock;

    // Логіка вибраних елементів у фільтрах
    private boolean[] booleansMotions, booleansMuscles, booleansEquipment;
    private final List<String> chosenTxtMotions = new ArrayList<>();
    private final List<String> chosenTxtMuscles = new ArrayList<>();
    private final List<String> chosenTxtEquipment = new ArrayList<>();

    // DAO для роботи з базою
    private PlanManagerDAO planManagerDAO;
    private ExerciseDAO exercisesDAO;

    // Поточний блок (якщо редагуємо)
    private TrainingBlock trainingBlock;

    /**
     * Збереження/оновлення тренувального блоку в базі.
     * 1. Якщо нового блоку не було, створюємо
     * 2. Якщо існує, оновлюємо
     * 3. Очищаємо попередні фільтри та зберігаємо нові
     * 4. Оновлюємо список вправ у блоці
     */
    private Set<Long> exerciseBlacklist = new HashSet<>();

    // Колбек для оновлення списку після збереження
    private final OnTrainingBlockCreatedListener listener;

    // id дня тренування (gymDayId), до якого належить цей блок
    private final long gymDayId;

    /**
     * Інтерфейс для повідомлення про створення/оновлення нового блоку.
     */
    public interface OnTrainingBlockCreatedListener {
        void onBlockAdded();
    }


    public interface OnExerciseCreatedListener {
        void onExerciseCreated(Exercise exercise);
    }




    /**
     * Конструктор для створення нового блоку.
     *
     * @param context   поточний контекст
     * @param gymDayId  id дня тренування
     * @param listener  колбек, що буде викликаний після збереження
     */
    public TrainingBlockDialog(@NonNull Context context,
                               long gymDayId,
                               OnTrainingBlockCreatedListener listener) {
        super(context, R.style.RoundedDialogTheme2);
        this.context = context;
        this.gymDayId = gymDayId;
        this.listener = listener;
    }

    /**
     * Конструктор для редагування існуючого блоку.
     *
     * @param context   поточний контекст
     * @param gymDayId  id дня тренування
     * @param block     існуючий TrainingBlock
     * @param listener  колбек, що викликається після збереження
     */
    public TrainingBlockDialog(@NonNull Context context,
                               long gymDayId,
                               TrainingBlock block,
                               OnTrainingBlockCreatedListener listener) {
        super(context, R.style.RoundedDialogTheme2);
        this.context = context;
        this.gymDayId = gymDayId;
        this.trainingBlock = block;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_training_block);

        // Ініціалізація DAO
        planManagerDAO = new PlanManagerDAO(getContext());
        exercisesDAO = new ExerciseDAO(getContext());

        // Розтягнути діалог по ширині (WRAP_CONTENT x WRAP_CONTENT)
        if (getWindow() != null) {
            getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        }

        // Налаштування UI
        initUI();

        // Ініціалізація масивів фільтрів (Motion, Muscle, Equipment)
        initFilters();

        // Якщо редагуємо існуючий блок
        if (trainingBlock != null) {
            loadBlockData();
        }
    }

    /**
     * Ініціалізація UI елементів.
     * Налаштування кнопок: Обрати рухи / м'язи / обладнання,
     * Кнопок "Скасувати" та "Зберегти".
     */
    private void initUI() {
        // Поля вводу
        editTextBlockName = findViewById(R.id.editTextBlockName);
        editTextBlockDescription = findViewById(R.id.editTextBlockDescription);

        // Обмеження довжини тексту (наприклад, назва до 30 символів, опис до 300)
        editTextBlockName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
        editTextBlockDescription.setFilters(new InputFilter[]{new InputFilter.LengthFilter(300)});

        // TextView із підписами (зараз не мають великої функціональності, але залишаємо)
        textViewFilterMotion = findViewById(R.id.textViewFilterMotion);
        textViewFilterMuscle = findViewById(R.id.textViewFilterMuscle);
        textViewFilterEquipment = findViewById(R.id.textViewFilterEquipment);

        // Кнопки вибору фільтрів
        buttonSelectMotion = findViewById(R.id.buttonSelectMotion);
        buttonSelectMuscle = findViewById(R.id.buttonSelectMuscle);
        buttonSelectEquipment = findViewById(R.id.buttonSelectEquipment);

        // Кнопки керування діалогом
        buttonCancel = findViewById(R.id.buttonCancel);
        buttonSaveBlock = findViewById(R.id.buttonSaveBlock);

        // Вибір рухів
        buttonSelectMotion.setOnClickListener(v -> showMultiSelectDialog(
                "Оберіть рухи",
                Motion.getAllDescriptions(getContext()),
                booleansMotions,
                chosenTxtMotions,
                buttonSelectMotion
        ));

        // Вибір м'язів
        buttonSelectMuscle.setOnClickListener(v -> showMultiSelectDialog(
                "Оберіть м’язи",
                MuscleGroup.getAllDescriptions(getContext()),
                booleansMuscles,
                chosenTxtMuscles,
                buttonSelectMuscle
        ));

        // Вибір обладнання
        buttonSelectEquipment.setOnClickListener(v -> showMultiSelectDialog(
                "Оберіть обладнання",
                Equipment.getEquipmentDescriptions(getContext()),
                booleansEquipment,
                chosenTxtEquipment,
                buttonSelectEquipment
        ));

        // Кнопка "Скасувати"
        buttonCancel.setOnClickListener(v -> dismiss());

        // Кнопка "Зберегти"
        buttonSaveBlock.setOnClickListener(v -> saveTrainingBlock());
    }

    /**
     * Ініціалізація масивів фільтрів (Motion / MuscleGroup / Equipment).
     */
    private void initFilters() {
        booleansMotions = new boolean[Motion.values().length];
        booleansMuscles = new boolean[MuscleGroup.values().length];
        booleansEquipment = new boolean[Equipment.values().length];
    }

    /**
     * Завантажуємо дані існуючого блоку і відзначаємо обрані фільтри.
     */
    private void loadBlockData() {
        // Назва і опис
        editTextBlockName.setText(trainingBlock.getName());
        editTextBlockDescription.setText(trainingBlock.getDescription());

        // Завантажуємо фільтри з БД
        List<String> savedMotionsInDB = planManagerDAO.getTrainingBlockFilters(trainingBlock.getId(), "motionType");
        List<String> savedMusclesInDB = planManagerDAO.getTrainingBlockFilters(trainingBlock.getId(), "muscleGroup");
        List<String> savedEquipmentInDB = planManagerDAO.getTrainingBlockFilters(trainingBlock.getId(), "equipment");

        // Відзначаємо вибрані елементи
        updateSelections(savedMotionsInDB, chosenTxtMotions, booleansMotions, Motion.values(), getContext());
        updateSelections(savedMusclesInDB, chosenTxtMuscles, booleansMuscles, MuscleGroup.values(), getContext());
        updateSelections(savedEquipmentInDB, chosenTxtEquipment, booleansEquipment, Equipment.values(), getContext());

        // Оновлення кнопок (кількість обраних елементів)
        updateButtonText(buttonSelectMotion, chosenTxtMotions);
        updateButtonText(buttonSelectMuscle, chosenTxtMuscles);
        updateButtonText(buttonSelectEquipment, chosenTxtEquipment);
    }

    /**
     * Оновлюємо виділення у масивах (booleans) і текстових списках (chosenTxtX)
     */
    private <T extends Enum<T>> void updateSelections(
            List<String> savedEnumNamesInDB,
            List<String> chosenItems,
            boolean[] selectedBooleans,
            T[] enums,
            Context context
    ) {
        chosenItems.clear();
        Arrays.fill(selectedBooleans, false);

        for (int i = 0; i < enums.length; i++) {
            // Якщо enum.name() є у збережених в БД
            if (savedEnumNamesInDB.contains(enums[i].name())) {
                selectedBooleans[i] = true;

                // Додаємо локалізований опис (Motion/MuscleGroup/Equipment)
                if (enums[i] instanceof Motion) {
                    chosenItems.add(((Motion) enums[i]).getDescription(context));
                } else if (enums[i] instanceof MuscleGroup) {
                    chosenItems.add(((MuscleGroup) enums[i]).getDescription(context));
                } else if (enums[i] instanceof Equipment) {
                    chosenItems.add(((Equipment) enums[i]).getDescription(context));
                }
            }
        }
    }

    /**
     * Оновлює текст на кнопці (к-ть обраних елементів).
     */
    private void updateButtonText(Button button, List<String> selectedItems) {
        if (selectedItems.isEmpty()) {
            button.setText(R.string.chose);
        } else {
            button.setText(context.getString(R.string.chosed) + ": " + selectedItems.size());
        }
    }



    private void saveTrainingBlock() {
        String name = editTextBlockName.getText().toString().trim();
        String description = editTextBlockDescription.getText().toString().trim();

        if (name.isEmpty()) {
            editTextBlockName.setError(context.getString(R.string.set_name));
            return;
        }

        long blockId;

        if (trainingBlock == null) {
            // Створення нового блоку
            TrainingBlock block = new TrainingBlock(0, gymDayId, name, description, new ArrayList<>());
            blockId = planManagerDAO.addTrainingBlock(block);
            trainingBlock = block;
            trainingBlock.setId(blockId);
        } else {
            // Оновлення існуючого
            trainingBlock.setName(name);
            trainingBlock.setDescription(description);
            planManagerDAO.updateTrainingBlock(trainingBlock);
            blockId = trainingBlock.getId();

            // формуємо чорний список (які раніше виключені користувачем)
            List<ExerciseInBlock> oldRecommended = planManagerDAO.getExercisesForTrainingBlock(blockId);
            List<ExerciseInBlock> oldSelected = planManagerDAO.getBlockExercises(blockId);

            exerciseBlacklist.clear();
            Set<Long> oldSelectedIds = new HashSet<>();
            for (ExerciseInBlock ex : oldSelected) {
                oldSelectedIds.add(ex.getId());
            }

            for (Exercise ex : oldRecommended) {
                if (!oldSelectedIds.contains(ex.getId())) {  // Перевіряємо по ID
                    exerciseBlacklist.add(ex.getId());
                }
            }
        }

        // Очищаємо старі фільтри
        planManagerDAO.clearTrainingBlockFilters(blockId);
        saveFilters(blockId);

        // Оновлюємо список вправ (з урахуванням чорного списку)
        List<ExerciseInBlock> newRecommended = planManagerDAO.getExercisesForTrainingBlock(blockId);

        List<ExerciseInBlock> updatedExercises = new ArrayList<>();
        int position = 0; // Починаємо з 0 і проставляємо позиції

        for (Exercise newEx : newRecommended) {
            if (!exerciseBlacklist.contains(newEx.getId())) {
                updatedExercises.add(new ExerciseInBlock(
                        newEx.getId(),
                        newEx.getName(),
                        newEx.getMotion(),
                        newEx.getMuscleGroupList(),
                        newEx.getEquipment(),
                        position++  // Проставляємо позицію
                ));
            }
        }

        // Оновлюємо БД
        try {
            planManagerDAO.updateTrainingBlockExercises(blockId, updatedExercises);
            if (listener != null) {
                listener.onBlockAdded();
            }
            dismiss();
        } catch (Exception e) {
            Log.e("DB_CRASH", "Помилка при оновленні вправ у блоці", e);
        }
    }



    /**
     * Зберігаємо (motion/muscle/equipment) фільтри в таблицях
     */
    private void saveFilters(long blockId) {
        // Motion
        for (String motionText : chosenTxtMotions) {
            Motion motion = Motion.getObjectByDescription(getContext(), motionText);
            if (motion != null) {
                planManagerDAO.addTrainingBlockFilter(blockId, "motionType", motion.name());
            }
        }

        // MuscleGroup
        for (String muscleText : chosenTxtMuscles) {
            MuscleGroup muscleGroup = MuscleGroup.getObjectByDescription(getContext(), muscleText);
            if (muscleGroup != null) {
                planManagerDAO.addTrainingBlockFilter(blockId, "muscleGroup", muscleGroup.name());
            }
        }

        // Equipment
        for (String equipmentText : chosenTxtEquipment) {
            Equipment equipment = Equipment.getEquipmentByDescription(getContext(), equipmentText);
            if (equipment != null) {
                planManagerDAO.addTrainingBlockFilter(blockId, "equipment", equipment.name());
            }
        }
    }

    /**
     * Показує діалог із мультивибором (список елементів + checkbox).
     * Обираємо потрібні речі: рухи, м'язи, обладнання.
     */
    private void showMultiSelectDialog(
            String title,
            String[] AllItems,
            boolean[] booleansOfItems,
            List<String> selectedItems,
            Button button
    ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);

        // Мультивибір
        builder.setMultiChoiceItems(AllItems, booleansOfItems, (dialog, which, isChecked) -> {
            booleansOfItems[which] = isChecked;
            if (isChecked) {
                selectedItems.add(AllItems[which]);
            } else {
                selectedItems.remove(AllItems[which]);
            }
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
            // Оновлюємо відображення кнопки
            updateButtonText(button, selectedItems);
        });
        builder.setNegativeButton("Скасувати", (dialog, which) -> dialog.dismiss());

        builder.show();
    }
}
