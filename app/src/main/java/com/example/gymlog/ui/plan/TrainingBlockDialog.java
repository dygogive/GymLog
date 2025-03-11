package com.example.gymlog.ui.plan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.gymlog.R;
import com.example.gymlog.data.db.ExerciseDAO;
import com.example.gymlog.data.db.PlanManagerDAO;
import com.example.gymlog.data.exercise.Equipment;
import com.example.gymlog.data.exercise.Exercise;
import com.example.gymlog.data.exercise.Motion;
import com.example.gymlog.data.exercise.MuscleGroup;
import com.example.gymlog.data.plan.TrainingBlock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Діалог для створення/редагування тренувального блоку
public class TrainingBlockDialog extends Dialog {

    private EditText editTextBlockName, editTextBlockDescription;
    private CheckBox checkBoxFilterMotion, checkBoxFilterMuscle, checkBoxFilterEquipment;
    private Button buttonSelectMotion, buttonSelectMuscle, buttonSelectEquipment, buttonCancel, buttonSaveBlock;

    private boolean[] selectedMotions, selectedMuscles, selectedEquipment;
    private final List<String> chosenMotions = new ArrayList<>();
    private final List<String> chosenMuscles = new ArrayList<>();
    private final List<String> chosenEquipment = new ArrayList<>();

    private PlanManagerDAO planManagerDAO;
    private ExerciseDAO exercisesDAO;
    private TrainingBlock blockToEdit;
    private final OnTrainingBlockCreatedListener listener;
    private final long gymDayId;

    // Інтерфейс для передачі нового блоку у активність
    public interface OnTrainingBlockCreatedListener {
        void onBlockAdded();
    }

    public TrainingBlockDialog(@NonNull Context context, long gymDayId, OnTrainingBlockCreatedListener listener) {
        super(context);
        this.gymDayId = gymDayId;
        this.listener = listener;
    }

    public TrainingBlockDialog(@NonNull Context context, long gymDayId, TrainingBlock block, OnTrainingBlockCreatedListener listener) {
        super(context);
        this.gymDayId = gymDayId;
        this.blockToEdit = block;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_training_block);

        planManagerDAO = new PlanManagerDAO(getContext());
        exercisesDAO = new ExerciseDAO(getContext());

        // Розширюємо діалог на всю ширину екрану
        if (getWindow() != null) {
            getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getWindow().setBackgroundDrawableResource(R.color.dialog_background);
        }

        // Ініціалізація UI елементів
        editTextBlockName = findViewById(R.id.editTextBlockName);
        editTextBlockDescription = findViewById(R.id.editTextBlockDescription);
        checkBoxFilterMotion = findViewById(R.id.checkBoxFilterMotion);
        checkBoxFilterMuscle = findViewById(R.id.checkBoxFilterMuscle);
        checkBoxFilterEquipment = findViewById(R.id.checkBoxFilterEquipment);
        buttonSelectMotion = findViewById(R.id.buttonSelectMotion);
        buttonSelectMuscle = findViewById(R.id.buttonSelectMuscle);
        buttonSelectEquipment = findViewById(R.id.buttonSelectEquipment);
        buttonCancel = findViewById(R.id.buttonCancel);
        buttonSaveBlock = findViewById(R.id.buttonSaveBlock);

        // Ініціалізація масивів доступних фільтрів
        selectedMotions = new boolean[Motion.values().length];
        selectedMuscles = new boolean[MuscleGroup.values().length];
        selectedEquipment = new boolean[Equipment.values().length];

        // Завантажуємо дані, якщо блок редагується
        if (blockToEdit != null) {
            loadBlockData();
        }

        // Обробники натискання на кнопки вибору фільтрів
        buttonSelectMotion.setOnClickListener(v -> showMultiSelectDialog(
                "Оберіть рухи",
                Motion.getMotionDescriptions(getContext()),
                selectedMotions,
                chosenMotions,
                checkBoxFilterMotion,
                buttonSelectMotion
        ));

        buttonSelectMuscle.setOnClickListener(v -> showMultiSelectDialog(
                "Оберіть м’язи",
                MuscleGroup.getMuscleGroupDescriptions(getContext()),
                selectedMuscles,
                chosenMuscles,
                checkBoxFilterMuscle,
                buttonSelectMuscle
        ));

        buttonSelectEquipment.setOnClickListener(v -> showMultiSelectDialog(
                "Оберіть обладнання",
                Equipment.getEquipmentDescriptions(getContext()),
                selectedEquipment,
                chosenEquipment,
                checkBoxFilterEquipment,
                buttonSelectEquipment
        ));


        // Обробник кнопки "Скасувати"
        buttonCancel.setOnClickListener(v -> dismiss());

        // Обробник кнопки "Зберегти"
        buttonSaveBlock.setOnClickListener(v -> saveTrainingBlock());
    }


    private void loadBlockData() {
        editTextBlockName.setText(blockToEdit.getName());
        editTextBlockDescription.setText(blockToEdit.getDescription());

        // Отримуємо фільтри з бази
        List<String> savedMotions = planManagerDAO.getTrainingBlockFilters(blockToEdit.getId(), "motionType");
        List<String> savedMuscles = planManagerDAO.getTrainingBlockFilters(blockToEdit.getId(), "muscleGroup");
        List<String> savedEquipment = planManagerDAO.getTrainingBlockFilters(blockToEdit.getId(), "equipment");

        Log.d("DB_DEBUG_FILTERS", "Loaded Motions: " + savedMotions);
        Log.d("DB_DEBUG_FILTERS", "Loaded Muscles: " + savedMuscles);
        Log.d("DB_DEBUG_FILTERS", "Loaded Equipment: " + savedEquipment);

        // Відзначаємо чекбокси, якщо є збережені фільтри
        checkBoxFilterMotion.setChecked(!savedMotions.isEmpty());
        checkBoxFilterMuscle.setChecked(!savedMuscles.isEmpty());
        checkBoxFilterEquipment.setChecked(!savedEquipment.isEmpty());

        // Відзначаємо вибрані елементи у списках
        updateSelections(savedMotions, chosenMotions, selectedMotions, Motion.values(), getContext());
        updateSelections(savedMuscles, chosenMuscles, selectedMuscles, MuscleGroup.values(), getContext());
        updateSelections(savedEquipment, chosenEquipment, selectedEquipment, Equipment.values(), getContext());


        // Оновлюємо текст кнопок
        updateButtonText(buttonSelectMotion, chosenMotions);
        updateButtonText(buttonSelectMuscle, chosenMuscles);
        updateButtonText(buttonSelectEquipment, chosenEquipment);
    }



    private <T extends Enum<T>> void updateSelections(
            List<String> savedEnumNames,
            List<String> chosenItems,
            boolean[] selectedArray,
            T[] enumValues,
            Context context
    ) {
        chosenItems.clear();
        Arrays.fill(selectedArray, false);

        for (int i = 0; i < enumValues.length; i++) {
            if (savedEnumNames.contains(enumValues[i].name())) {
                selectedArray[i] = true;
                chosenItems.add(enumValues[i] instanceof Motion ?
                        ((Motion) enumValues[i]).getDescription(context) :
                        enumValues[i] instanceof MuscleGroup ?
                                ((MuscleGroup) enumValues[i]).getDescription(context) :
                                ((Equipment) enumValues[i]).getDescription(context));
            }
        }
    }




    private void updateButtonText(Button button, List<String> selectedItems) {
        if (selectedItems.isEmpty()) {
            button.setText("Оберіть");
        } else {
            button.setText("Вибрано: " + selectedItems.size());
        }
    }



    private void saveTrainingBlock() {
        String name = editTextBlockName.getText().toString().trim();
        String description = editTextBlockDescription.getText().toString().trim();

        if (name.isEmpty()) {
            editTextBlockName.setError("Введіть назву блоку");
            return;
        }

        long blockId;
        List<Exercise> exercises;

        if (blockToEdit == null) {
            TrainingBlock block = new TrainingBlock(0, gymDayId, name, description, new ArrayList<>());
            blockId = planManagerDAO.addTrainingBlock(block);
        } else {
            blockToEdit.setName(name);
            blockToEdit.setDescription(description);
            planManagerDAO.updateTrainingBlock(blockToEdit);
            blockId = blockToEdit.getId();
        }

        // ✅ Очистити старі фільтри та зберегти нові
        planManagerDAO.clearTrainingBlockFilters(blockId);
        saveFilters(blockId);


        // Дебаг: перевіряємо чи збереглися фільтри
        List<String> savedMotions = planManagerDAO.getTrainingBlockFilters(blockId, "motionType");
        List<String> savedMuscles = planManagerDAO.getTrainingBlockFilters(blockId, "muscleGroup");
        List<String> savedEquipment = planManagerDAO.getTrainingBlockFilters(blockId, "equipment");

        Log.d("DB_DEBUG_FILTERS", "Saved Motions: " + savedMotions);
        Log.d("DB_DEBUG_FILTERS", "Saved Muscles: " + savedMuscles);
        Log.d("DB_DEBUG_FILTERS", "Saved Equipment: " + savedEquipment);



        // ✅ Оновити список вправ для блоку
        exercises = planManagerDAO.getExercisesForTrainingBlock(blockId);




        List<Exercise> allExercises = exercisesDAO.getAllExercises();
        for (Exercise e : allExercises) {
            Log.d("DB_DEBUG_EXERCISES", "Exercise: " + e.getName() + ", Motion: " + e.getMotion() + ", Muscles: " + e.getMuscleGroupList() + ", Equipment: " + e.getEquipment());
        }




        // 🔥 **Оновлюємо об'єкт `TrainingBlock` з новим списком вправ**
        if (blockToEdit != null) {
            blockToEdit.setExercises(exercises);
        } else {
            blockToEdit = new TrainingBlock(blockId, gymDayId, name, description, exercises);
        }

        // ✅ Передаємо оновлений `blockToEdit`
        if (listener != null) {
            listener.onBlockAdded();
        }


        // ✅ Додаємо логування для перевірки
        for (Exercise e : blockToEdit.getExercises()) {
            Log.d("DB_DEBUG_BLOCK_EXERCISES", "Block ID: " + blockId +
                    " | Exercise: " + e.getName() +
                    " | Motion: " + e.getMotion() +
                    " | Muscles: " + e.getMuscleGroupList() +
                    " | Equipment: " + e.getEquipment());
        }



        dismiss();
    }



    private void saveFilters(long blockId) {
        // Зберігаємо Motion
        for (String motionText : chosenMotions) {
            Motion motion = Motion.getMotionByDescription(getContext(), motionText);
            if (motion != null) {
                Log.d("DB_DEBUG_SAVE", "Saving Motion: " + motion.name() + " for Block ID: " + blockId);
                planManagerDAO.addTrainingBlockFilter(blockId, "motionType", motion.name());
            }
        }

        // Зберігаємо MuscleGroup
        for (String muscleText : chosenMuscles) {
            MuscleGroup muscleGroup = MuscleGroup.getMuscleGroupByDescription(getContext(), muscleText);
            if (muscleGroup != null) {
                Log.d("DB_DEBUG_SAVE", "Saving MuscleGroup: " + muscleGroup.name() + " for Block ID: " + blockId);
                planManagerDAO.addTrainingBlockFilter(blockId, "muscleGroup", muscleGroup.name());
            }
        }

        // Зберігаємо Equipment
        for (String equipmentText : chosenEquipment) {
            Equipment equipment = Equipment.getEquipmentByDescription(getContext(), equipmentText);
            if (equipment != null) {
                Log.d("DB_DEBUG_SAVE", "Saving Equipment: " + equipment.name() + " for Block ID: " + blockId);
                planManagerDAO.addTrainingBlockFilter(blockId, "equipment", equipment.name());
            }
        }
    }






    // Метод для показу діалогу вибору значень
    private void showMultiSelectDialog(
            String title,
            String[] options,
            boolean[] selectedOptions,
            List<String> selectedItems,
            CheckBox checkBox,
            Button button
    ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);

        builder.setMultiChoiceItems(options, selectedOptions, (dialog, which, isChecked) -> {
            selectedOptions[which] = isChecked;
            if (isChecked) {
                selectedItems.add(options[which]);
            } else {
                selectedItems.remove(options[which]);
            }
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
            // Оновлюємо текст кнопки
            updateButtonText(button, selectedItems);

            // Оновлюємо чекбокс: якщо щось вибрали — відмічаємо
            checkBox.setChecked(!selectedItems.isEmpty());
        });

        builder.setNegativeButton("Скасувати", (dialog, which) -> dialog.dismiss());

        builder.show();
    }





}
