package com.example.gymlog.ui.plan.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

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
    Context context;

    private EditText editTextBlockName, editTextBlockDescription;
    private TextView textViewFilterMotion, textViewFilterMuscle, textViewFilterEquipment;
    private Button buttonSelectMotion, buttonSelectMuscle, buttonSelectEquipment, buttonCancel, buttonSaveBlock;

    private boolean[] booleansMotions, booleansMuscles, booleansEquipment;
    private final List<String> chosenTxtMotions = new ArrayList<>();
    private final List<String> chosenTxtMuscles = new ArrayList<>();
    private final List<String> chosenTxtEquipment = new ArrayList<>();

    private PlanManagerDAO planManagerDAO;
    private ExerciseDAO exercisesDAO;
    private TrainingBlock trainingBlock;
    public final OnTrainingBlockCreatedListener listener;
    private final long gymDayId;

    // Інтерфейс для передачі нового блоку у активність
    public interface OnTrainingBlockCreatedListener {
        void onBlockAdded();
    }

    public TrainingBlockDialog(@NonNull Context context, long gymDayId, OnTrainingBlockCreatedListener listener) {
        super(context, R.style.RoundedDialogTheme2);
        this.context = context;
        this.gymDayId = gymDayId;
        this.listener = listener;
    }

    public TrainingBlockDialog(@NonNull Context context, long gymDayId, TrainingBlock block, OnTrainingBlockCreatedListener listener) {
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
        exercisesDAO = new ExerciseDAO(getContext());

        // Розширюємо діалог на всю ширину екрану
        if (getWindow() != null) {
            getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        }


        // Ініціалізація UI елементів
        editTextBlockName = findViewById(R.id.editTextBlockName);
        editTextBlockDescription = findViewById(R.id.editTextBlockDescription);
        textViewFilterMotion = findViewById(R.id.textViewFilterMotion);
        textViewFilterMuscle = findViewById(R.id.textViewFilterMuscle);
        textViewFilterEquipment = findViewById(R.id.textViewFilterEquipment);
        buttonSelectMotion = findViewById(R.id.buttonSelectMotion);
        buttonSelectMuscle = findViewById(R.id.buttonSelectMuscle);
        buttonSelectEquipment = findViewById(R.id.buttonSelectEquipment);
        buttonCancel = findViewById(R.id.buttonCancel);
        buttonSaveBlock = findViewById(R.id.buttonSaveBlock);

        //обмеження для EditText полів
        editTextBlockName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
        editTextBlockDescription.setFilters(new InputFilter[]{new InputFilter.LengthFilter(300)});


        // Ініціалізація масивів доступних фільтрів
        booleansMotions = new boolean[Motion.values().length];
        booleansMuscles = new boolean[MuscleGroup.values().length];
        booleansEquipment = new boolean[Equipment.values().length];

        // Завантажуємо дані, якщо блок редагується
        if (trainingBlock != null) {
            loadBlockData();
        }

        // Обробники натискання на кнопки вибору фільтрів
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


        // Обробник кнопки "Скасувати"
        buttonCancel.setOnClickListener(v -> dismiss());

        // Обробник кнопки "Зберегти"
        buttonSaveBlock.setOnClickListener(v -> saveTrainingBlock());



    }

    //Якщо блок редагується, то оновити діалог з бази даних
    private void loadBlockData() {
        editTextBlockName.setText(trainingBlock.getName());
        editTextBlockDescription.setText(trainingBlock.getDescription());

        // Отримуємо фільтри з бази
        List<String> savedMotionsInDB = planManagerDAO.getTrainingBlockFilters(trainingBlock.getId(), "motionType");
        List<String> savedMusclesInDB = planManagerDAO.getTrainingBlockFilters(trainingBlock.getId(), "muscleGroup");
        List<String> savedEquipmentInDB = planManagerDAO.getTrainingBlockFilters(trainingBlock.getId(), "equipment");


        // Відзначаємо вибрані елементи у списках
        updateSelections(savedMotionsInDB, chosenTxtMotions, booleansMotions, Motion.values(), getContext());
        updateSelections(savedMusclesInDB, chosenTxtMuscles, booleansMuscles, MuscleGroup.values(), getContext());
        updateSelections(savedEquipmentInDB, chosenTxtEquipment, booleansEquipment, Equipment.values(), getContext());

        // Оновлюємо текст кнопок
        updateButtonText(buttonSelectMotion, chosenTxtMotions);
        updateButtonText(buttonSelectMuscle, chosenTxtMuscles);
        updateButtonText(buttonSelectEquipment, chosenTxtEquipment);
    }


    // Відзначаємо вибрані елементи у списках
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
            if (savedEnumNamesInDB.contains(enums[i].name())) {
                selectedBooleans[i] = true;
                chosenItems.add(enums[i] instanceof Motion ?
                        ((Motion) enums[i]).getDescription(context) :
                        enums[i] instanceof MuscleGroup ?
                                ((MuscleGroup) enums[i]).getDescription(context) :
                                ((Equipment) enums[i]).getDescription(context));
            }
        }
    }



    //оновити назву кнопки згідно з к-стю вибраних елементів у списку
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
            TrainingBlock block = new TrainingBlock(
                    0,
                    gymDayId,
                    name,
                    description,
                    new ArrayList<>()
            );
            blockId = planManagerDAO.addTrainingBlock(block);
            trainingBlock = block; // Ініціалізуємо trainingBlock новим створеним блоком!
            trainingBlock.setId(blockId); // важливо оновити id після додавання
        } else {
            trainingBlock.setName(name);
            trainingBlock.setDescription(description);
            planManagerDAO.updateTrainingBlock(trainingBlock);
            blockId = trainingBlock.getId();
        }

        // Очистити старі фільтри та зберегти нові
        planManagerDAO.clearTrainingBlockFilters(blockId);
        saveFilters(blockId);

        // Оновити список вправ для блоку
        List<Exercise> exercises = planManagerDAO.getExercisesForTrainingBlock(blockId);

        if (trainingBlock == null) {
            trainingBlock = new TrainingBlock(blockId, gymDayId, name, description, exercises);
        } else {
            trainingBlock.setExercises(exercises);
        }

        // запускаємо оновлення у списку
        if (listener != null) {
            listener.onBlockAdded();
        }

        dismiss();
    }




    private void saveFilters(long blockId) {
        // Зберігаємо Motion якщо чекбокс активний
        for (String motionText : chosenTxtMotions) {
            Motion motion = Motion.getObjectByDescription(getContext(), motionText);
            if (motion != null) {
                planManagerDAO.addTrainingBlockFilter(blockId, "motionType", motion.name());
            }
        }

        // Зберігаємо MuscleGroup
        for (String muscleText : chosenTxtMuscles) {
            MuscleGroup muscleGroup = MuscleGroup.getObjectByDescription(getContext(), muscleText);
            if (muscleGroup != null) {
                planManagerDAO.addTrainingBlockFilter(blockId, "muscleGroup", muscleGroup.name());
            }
        }

        // Зберігаємо Equipment
        for (String equipmentText : chosenTxtEquipment) {
            Equipment equipment = Equipment.getEquipmentByDescription(getContext(), equipmentText);
            if (equipment != null) {
                planManagerDAO.addTrainingBlockFilter(blockId, "equipment", equipment.name());
            }
        }
    }






    // Метод для показу діалогу вибору значень
    private void showMultiSelectDialog(
            String title,
            String[] AllItems,
            boolean[] booleansOfItems,
            List<String> selectedItems,
            Button button
    ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle(title);

        builder.setMultiChoiceItems(AllItems, booleansOfItems, (dialog, which, isChecked) -> {
            //оновити список значень щодо списку
            booleansOfItems[which] = isChecked;
            if (isChecked) {
                selectedItems.add(AllItems[which]);
            } else {
                selectedItems.remove(AllItems[which]);
            }
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
            // Оновлюємо текст кнопки
            updateButtonText(button, selectedItems);
        });

        builder.setNegativeButton("Скасувати", (dialog, which) -> dialog.dismiss());

        builder.show();
    }





}
