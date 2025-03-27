package com.example.gymlog.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.example.gymlog.R;
import com.example.gymlog.sqlopenhelper.PlanManagerDAO;
import com.example.gymlog.model.exercise.Equipment;
import com.example.gymlog.model.exercise.Exercise;
import com.example.gymlog.model.exercise.ExerciseInBlock;
import com.example.gymlog.model.exercise.Motion;
import com.example.gymlog.model.exercise.MuscleGroup;
import com.example.gymlog.model.plan.TrainingBlock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Діалог для створення/редагування тренувального блоку (TrainingBlock).
 * Містить логіку вибору фільтрів (рухи, м'язеві групи, обладнання).
 */
public class DialogExeBlocCreator extends Dialog {

    private final Context context;
    private EditText editTextBlockName, editTextBlockDescription;
    private Button buttonSelectMotion, buttonSelectMuscle, buttonSelectEquipment;
    private Button buttonSaveBlock;
    private boolean[] booleansMotions, booleansMuscles, booleansEquipment;
    private final List<String> chosenTxtMotions = new ArrayList<>();
    private final List<String> chosenTxtMuscles = new ArrayList<>();
    private final List<String> chosenTxtEquipment = new ArrayList<>();

    private PlanManagerDAO planManagerDAO;
    private TrainingBlock trainingBlock;
    private Set<Long> idExercisesBlacklist = new HashSet<>();

    private final OnTrainingBlockCreatedListener listener;
    private final long gymDayId;

    public interface OnTrainingBlockCreatedListener {
        void onBlockAdded();
    }

    // Конструктор для створення нового блоку
    public DialogExeBlocCreator(@NonNull Context context,
                                long gymDayId,
                                OnTrainingBlockCreatedListener listener) {
        super(context, R.style.RoundedDialogTheme2);
        this.context = context;
        this.gymDayId = gymDayId;
        this.listener = listener;
    }

    // Конструктор для редагування існуючого блоку
    public DialogExeBlocCreator(@NonNull Context context,
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

        planManagerDAO = new PlanManagerDAO(getContext());

        if (getWindow() != null) {
            getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        }

        initUI();
        initFilters();

        if (trainingBlock != null) {
            loadBlockData();
        }
    }

    /**
     * Ініціалізація UI елементів та їх обробників.
     */
    private void initUI() {
        editTextBlockName = findViewById(R.id.editTextBlockName);
        editTextBlockDescription = findViewById(R.id.editTextBlockDescription);
        editTextBlockName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
        editTextBlockDescription.setFilters(new InputFilter[]{new InputFilter.LengthFilter(300)});



        buttonSelectMotion = findViewById(R.id.buttonSelectMotion);
        buttonSelectMuscle = findViewById(R.id.buttonSelectMuscle);
        buttonSelectEquipment = findViewById(R.id.buttonSelectEquipment);
        Button buttonCancel = findViewById(R.id.buttonCancel);
        buttonSaveBlock = findViewById(R.id.buttonSaveBlock);

        // Обробники кліків для кнопок вибору фільтрів
        buttonSelectMotion.setOnClickListener(v -> showMultiSelectDialog(
                "Оберіть рухи",
                Motion.getAllDescriptions(getContext()),
                booleansMotions,
                chosenTxtMotions,
                buttonSelectMotion
        ));
        buttonSelectMuscle.setOnClickListener(v -> showMultiSelectDialog(
                "Оберіть м’язи",
                MuscleGroup.getAllDescriptions(getContext()),
                booleansMuscles,
                chosenTxtMuscles,
                buttonSelectMuscle
        ));
        buttonSelectEquipment.setOnClickListener(v -> showMultiSelectDialog(
                "Оберіть обладнання",
                Equipment.getEquipmentDescriptions(getContext()),
                booleansEquipment,
                chosenTxtEquipment,
                buttonSelectEquipment
        ));

        buttonCancel.setOnClickListener(v -> dismiss());
        buttonSaveBlock.setOnClickListener(v -> saveTrainingBlock());
    }

    /**
     * Ініціалізація масивів для фільтрів.
     */
    private void initFilters() {
        booleansMotions = new boolean[Motion.values().length];
        booleansMuscles = new boolean[MuscleGroup.values().length];
        booleansEquipment = new boolean[Equipment.values().length];
    }

    /**
     * Завантажуємо дані існуючого блоку та встановлюємо вибрані фільтри.
     */
    private void loadBlockData() {
        editTextBlockName.setText(trainingBlock.getName());
        editTextBlockDescription.setText(trainingBlock.getDescription());

        List<String> savedMotionsInDB = planManagerDAO.getTrainingBlockFilters(trainingBlock.getId(), "motionType");
        List<String> savedMusclesInDB = planManagerDAO.getTrainingBlockFilters(trainingBlock.getId(), "muscleGroup");
        List<String> savedEquipmentInDB = planManagerDAO.getTrainingBlockFilters(trainingBlock.getId(), "equipment");

        // Використовуємо спільний метод updateSelections з лямбдами для отримання опису
        updateSelections(savedMotionsInDB, chosenTxtMotions, booleansMotions,
                Motion.values(), (motion, ctx) -> motion.getDescription(ctx));
        updateSelections(savedMusclesInDB, chosenTxtMuscles, booleansMuscles,
                MuscleGroup.values(), (muscle, ctx) -> muscle.getDescription(ctx));
        updateSelections(savedEquipmentInDB, chosenTxtEquipment, booleansEquipment,
                Equipment.values(), (equipment, ctx) -> equipment.getDescription(ctx));

        updateButtonText(buttonSelectMotion, chosenTxtMotions);
        updateButtonText(buttonSelectMuscle, chosenTxtMuscles);
        updateButtonText(buttonSelectEquipment, chosenTxtEquipment);
    }

    /**
     * Оновлює вибір фільтрів, використовуючи спільний алгоритм.
     *
     * @param savedEnumNamesInDB збережені імена enum із БД
     * @param chosenItems        список для вибраних описів
     * @param selectedBooleans   булевий масив для позначення вибраного
     * @param enums              перелік enum значень
     * @param descriptionFunc    функція для отримання локалізованого опису
     *                           (приймає enum і контекст)
     */
    private <T extends Enum<T>> void updateSelections(List<String> savedEnumNamesInDB,
                                                      List<String> chosenItems,
                                                      boolean[] selectedBooleans,
                                                      T[] enums,
                                                      BiFunction<T, Context, String> descriptionFunc) {
        chosenItems.clear();
        Arrays.fill(selectedBooleans, false);
        for (int i = 0; i < enums.length; i++) {
            if (savedEnumNamesInDB.contains(enums[i].name())) {
                selectedBooleans[i] = true;
                chosenItems.add(descriptionFunc.apply(enums[i], context));
            }
        }
    }

    /**
     * Оновлює текст кнопки згідно з кількістю вибраних елементів.
     */
    private void updateButtonText(Button button, List<String> selectedItems) {
        if (selectedItems.isEmpty()) {
            button.setText(context.getString(R.string.chose));
        } else {
            button.setText(context.getString(R.string.chosed) + ": " + selectedItems.size());
        }
    }

    /**
     * Зберігає або оновлює тренувальний блок у базі, фільтри та список вправ.
     */
    private void saveTrainingBlock() {
        String name = editTextBlockName.getText().toString().trim();
        String description = editTextBlockDescription.getText().toString().trim();

        if (name.isEmpty()) {
            editTextBlockName.setError(context.getString(R.string.set_name));
            return;
        }

        long blockId;
        if (trainingBlock == null) {
            TrainingBlock block = new TrainingBlock(0, gymDayId, name, description, new ArrayList<>());
            blockId = planManagerDAO.addTrainingBlock(block);
            trainingBlock = block;
            trainingBlock.setId(blockId);
        } else {
            trainingBlock.setName(name);
            trainingBlock.setDescription(description);
            planManagerDAO.updateTrainingBlock(trainingBlock);
            blockId = trainingBlock.getId();

            // Формування чорного списку для вже виключених вправ
            List<Exercise> recommendedExes = planManagerDAO.getExercisesForTrainingBlock(blockId);
            List<ExerciseInBlock> blockExercises = planManagerDAO.getBlockExercises(blockId);
            idExercisesBlacklist.clear();
            Set<Long> oldSelectedIds = new HashSet<>();
            for (ExerciseInBlock ex : blockExercises) {
                oldSelectedIds.add(ex.getId());
            }
            for (Exercise ex : recommendedExes) {
                if (!oldSelectedIds.contains(ex.getId())) {
                    idExercisesBlacklist.add(ex.getId());
                }
            }
        }

        planManagerDAO.clearTrainingBlockFilters(blockId);
        saveFilters(blockId);

        // Оновлюємо список вправ
        List<Exercise> recommendedExes = planManagerDAO.getExercisesForTrainingBlock(blockId);
        List<ExerciseInBlock> exerciseInBlockList = new ArrayList<>();
        int position = 0;
        for (Exercise recommended : recommendedExes) {
            if (!idExercisesBlacklist.contains(recommended.getId())) {
                exerciseInBlockList.add(new ExerciseInBlock(
                        -1,
                        recommended.getId(),
                        recommended.getName(),
                        recommended.getMotion(),
                        recommended.getMuscleGroupList(),
                        recommended.getEquipment(),
                        position++
                ));
            }
        }
        try {
            planManagerDAO.updateTrainingBlockExercises(blockId, exerciseInBlockList);
            if (listener != null) {
                listener.onBlockAdded();
            }
            dismiss();
        } catch (Exception e) {
            Log.e("DB_CRASH", "Помилка при оновленні вправ у блоці", e);
        }
    }

    /**
     * Спільний метод для збереження фільтрів.
     */
    private void saveFilters(long blockId) {
        saveFilterItems(blockId, chosenTxtMotions,
                motionText -> Motion.getObjectByDescription(context, motionText),
                "motionType");
        saveFilterItems(blockId, chosenTxtMuscles,
                muscleText -> MuscleGroup.getObjectByDescription(context, muscleText),
                "muscleGroup");
        saveFilterItems(blockId, chosenTxtEquipment,
                equipmentText -> Equipment.getEquipmentByDescription(context, equipmentText),
                "equipment");
    }

    /**
     * Загальний метод збереження фільтра для будь-якого типу.
     */
    private <E extends Enum<E>> void saveFilterItems(long blockId,
                                                     List<String> chosenItems,
                                                     Function<String, E> converter,
                                                     String filterType) {
        for (String itemText : chosenItems) {
            E enumValue = converter.apply(itemText);
            if (enumValue != null) {
                planManagerDAO.addTrainingBlockFilter(blockId, filterType, enumValue.name());
            }
        }
    }

    /**
     * Показує діалог з мультивибором для вибору елементів (рухи, м’язи, обладнання).
     */
    private void showMultiSelectDialog(String title,
                                       String[] allItems,
                                       boolean[] booleansOfItems,
                                       List<String> selectedItems,
                                       Button button) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);
        builder.setMultiChoiceItems(allItems, booleansOfItems, (dialog, which, isChecked) -> {
            booleansOfItems[which] = isChecked;
            if (isChecked) {
                selectedItems.add(allItems[which]);
            } else {
                selectedItems.remove(allItems[which]);
            }
        });
        builder.setPositiveButton("OK", (dialog, which) -> updateButtonText(button, selectedItems));
        builder.setNegativeButton("Скасувати", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
