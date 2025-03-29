package com.example.gymlog.ui.programs;

import androidx.appcompat.app.AlertDialog;
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
import com.example.gymlog.ui.dialogs.DialogStyler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DialogBlocCreator extends Dialog {

    private final Context context;
    private EditText editTextBlockName, editTextBlockDescription;
    private Button buttonSelectMotion, buttonSelectMuscle, buttonSelectEquipment;
    private boolean[] booleansMotions, booleansMuscles, booleansEquipment;
    private final List<String> chosenTxtMotions = new ArrayList<>();
    private final List<String> chosenTxtMuscles = new ArrayList<>();
    private final List<String> chosenTxtEquipment = new ArrayList<>();

    private PlanManagerDAO planManagerDAO;
    private TrainingBlock trainingBlock;
    private final Set<Long> idExercisesBlacklist = new HashSet<>();

    private final OnTrainingBlockCreatedListener listener;
    private final long gymDayId;

    public interface OnTrainingBlockCreatedListener {
        void onBlockAdded();
    }

    public DialogBlocCreator(@NonNull Context context, long gymDayId, OnTrainingBlockCreatedListener listener) {
        super(context, R.style.RoundedDialogTheme2);
        this.context = context;
        this.gymDayId = gymDayId;
        this.listener = listener;
    }

    public DialogBlocCreator(@NonNull Context context, long gymDayId, TrainingBlock block, OnTrainingBlockCreatedListener listener) {
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

    private void initUI() {
        editTextBlockName = findViewById(R.id.editTextBlockName);
        editTextBlockDescription = findViewById(R.id.editTextBlockDescription);
        editTextBlockName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
        editTextBlockDescription.setFilters(new InputFilter[]{new InputFilter.LengthFilter(300)});

        buttonSelectMotion = findViewById(R.id.buttonSelectMotion);
        buttonSelectMuscle = findViewById(R.id.buttonSelectMuscle);
        buttonSelectEquipment = findViewById(R.id.buttonSelectEquipment);
        Button buttonCancel = findViewById(R.id.buttonCancel);
        Button buttonSaveBlock = findViewById(R.id.buttonSaveBlock);

        buttonSelectMotion.setTag(buttonSelectMotion.getText().toString());
        buttonSelectMuscle.setTag(buttonSelectMuscle.getText().toString());
        buttonSelectEquipment.setTag(buttonSelectEquipment.getText().toString());

        buttonSelectMotion.setOnClickListener(v -> showMultiSelectDialog("Оберіть рухи", Motion.getAllDescriptions(getContext()), booleansMotions, chosenTxtMotions, buttonSelectMotion));
        buttonSelectMuscle.setOnClickListener(v -> showMultiSelectDialog("Оберіть м’язи", MuscleGroup.getAllDescriptions(getContext()), booleansMuscles, chosenTxtMuscles, buttonSelectMuscle));
        buttonSelectEquipment.setOnClickListener(v -> showMultiSelectDialog("Оберіть обладнання", Equipment.getEquipmentDescriptions(getContext()), booleansEquipment, chosenTxtEquipment, buttonSelectEquipment));

        buttonCancel.setOnClickListener(v -> dismiss());
        buttonSaveBlock.setOnClickListener(v -> saveTrainingBlock());

        DialogStyler.styleButtonsInDialog(context, buttonCancel, buttonSaveBlock);
    }

    private void initFilters() {
        booleansMotions = new boolean[Motion.values().length];
        booleansMuscles = new boolean[MuscleGroup.values().length];
        booleansEquipment = new boolean[Equipment.values().length];
    }

    private void loadBlockData() {
        editTextBlockName.setText(trainingBlock.getName());
        editTextBlockDescription.setText(trainingBlock.getDescription());

        List<String> savedMotionsInDB = planManagerDAO.getTrainingBlockFilters(trainingBlock.getId(), "motionType");
        List<String> savedMusclesInDB = planManagerDAO.getTrainingBlockFilters(trainingBlock.getId(), "muscleGroup");
        List<String> savedEquipmentInDB = planManagerDAO.getTrainingBlockFilters(trainingBlock.getId(), "equipment");

        updateSelections(savedMotionsInDB, chosenTxtMotions, booleansMotions, Motion.values(), Motion::getDescription);
        updateSelections(savedMusclesInDB, chosenTxtMuscles, booleansMuscles, MuscleGroup.values(), MuscleGroup::getDescription);
        updateSelections(savedEquipmentInDB, chosenTxtEquipment, booleansEquipment, Equipment.values(), Equipment::getDescription);

        updateButtonText(buttonSelectMotion, chosenTxtMotions);
        updateButtonText(buttonSelectMuscle, chosenTxtMuscles);
        updateButtonText(buttonSelectEquipment, chosenTxtEquipment);
    }

    private <T extends Enum<T>> void updateSelections(List<String> savedEnumNamesInDB, List<String> chosenItems, boolean[] selectedBooleans, T[] enums, BiFunction<T, Context, String> descriptionFunc) {
        chosenItems.clear();
        Arrays.fill(selectedBooleans, false);
        for (int i = 0; i < enums.length; i++) {
            if (savedEnumNamesInDB.contains(enums[i].name())) {
                selectedBooleans[i] = true;
                chosenItems.add(descriptionFunc.apply(enums[i], context));
            }
        }
    }

    private void updateButtonText(Button button, List<String> selectedItems) {
        String baseText = button.getTag().toString();
        button.setText(!selectedItems.isEmpty() ? baseText + " (" + selectedItems.size() + ")" : baseText);
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
            TrainingBlock block = new TrainingBlock(0, gymDayId, name, description, new ArrayList<>());
            blockId = planManagerDAO.addTrainingBlock(block);
            trainingBlock = block;
            trainingBlock.setId(blockId);
        } else {
            trainingBlock.setName(name);
            trainingBlock.setDescription(description);
            planManagerDAO.updateTrainingBlock(trainingBlock);
            blockId = trainingBlock.getId();

            List<Exercise> recommendedExes = planManagerDAO.recommendExercisesForTrainingBlock(blockId);
            List<ExerciseInBlock> blockExercises = planManagerDAO.getBlockExercises(blockId);
            idExercisesBlacklist.clear();
            Set<Long> oldSelectedIds = new HashSet<>();
            for (ExerciseInBlock ex : blockExercises) oldSelectedIds.add(ex.getId());
            for (Exercise ex : recommendedExes)
                if (!oldSelectedIds.contains(ex.getId())) idExercisesBlacklist.add(ex.getId());
        }

        planManagerDAO.clearTrainingBlockFilters(blockId);
        saveFilters(blockId);

        List<Exercise> recommendedExes = planManagerDAO.recommendExercisesForTrainingBlock(blockId);
        List<ExerciseInBlock> exerciseInBlockList = new ArrayList<>();
        int position = 0;
        for (Exercise recommended : recommendedExes) {
            if (!idExercisesBlacklist.contains(recommended.getId())) {
                exerciseInBlockList.add(new ExerciseInBlock(-1, recommended.getId(), recommended.getName(), recommended.getDescription(), recommended.getMotion(), recommended.getMuscleGroupList(), recommended.getEquipment(), position++));
            }
        }

        try {
            planManagerDAO.updateTrainingBlockExercises(blockId, exerciseInBlockList);
            if (listener != null) listener.onBlockAdded();
            dismiss();
        } catch (Exception e) {
            Log.e("DB_CRASH", "Помилка при оновленні вправ у блоці", e);
        }
    }

    private void saveFilters(long blockId) {
        saveFilterItems(blockId, chosenTxtMotions, motionText -> Motion.getObjectByDescription(context, motionText), "motionType");
        saveFilterItems(blockId, chosenTxtMuscles, muscleText -> MuscleGroup.getObjectByDescription(context, muscleText), "muscleGroup");
        saveFilterItems(blockId, chosenTxtEquipment, equipmentText -> Equipment.getEquipmentByDescription(context, equipmentText), "equipment");
    }

    private <E extends Enum<E>> void saveFilterItems(long blockId, List<String> chosenItems, Function<String, E> converter, String filterType) {
        for (String itemText : chosenItems) {
            E enumValue = converter.apply(itemText);
            if (enumValue != null) {
                planManagerDAO.addTrainingBlockFilter(blockId, filterType, enumValue.name());
            }
        }
    }

    private void showMultiSelectDialog(String title, String[] allItems, boolean[] booleansOfItems, List<String> selectedItems, Button button) {
        AlertDialog dialog = new AlertDialog.Builder(getContext(), R.style.RoundedDialogTheme2)
                .setTitle(title)
                .setMultiChoiceItems(allItems, booleansOfItems, (d, which, isChecked) -> {
                    booleansOfItems[which] = isChecked;
                    if (isChecked) selectedItems.add(allItems[which]);
                    else selectedItems.remove(allItems[which]);
                })
                .setPositiveButton("OK", (d, which) -> updateButtonText(button, selectedItems))
                .setNegativeButton("Скасувати", (d, which) -> d.dismiss())
                .create();
        dialog.setOnShowListener(d -> DialogStyler.applyAlertDialogStyle(context, dialog));
        dialog.show();
    }
}
