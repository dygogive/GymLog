package com.example.gymlog.ui.plan;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.gymlog.R;
import com.example.gymlog.data.db.PlanManagerDAO;
import com.example.gymlog.data.plan.TrainingBlock;

import java.util.ArrayList;


// Діалог для створення/редагування тренувального блоку
public class TrainingBlockDialog extends Dialog {

    private EditText editTextBlockName, editTextBlockDescription;
    private CheckBox checkBoxFilterMotion, checkBoxFilterMuscle, checkBoxFilterEquipment;
    private Spinner spinnerMotionType, spinnerMuscleGroup, spinnerEquipment;
    private Button buttonCancel, buttonSaveBlock;
    private PlanManagerDAO planManagerDAO;

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


        // Розширюємо діалог на всю ширину екрану
        if (getWindow() != null) {
            getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getWindow().setBackgroundDrawableResource(R.color.dialog_background); // Темніший фон
        }

        // Ініціалізація елементів UI
        editTextBlockName = findViewById(R.id.editTextBlockName);
        editTextBlockDescription = findViewById(R.id.editTextBlockDescription);
        checkBoxFilterMotion = findViewById(R.id.checkBoxFilterMotion);
        checkBoxFilterMuscle = findViewById(R.id.checkBoxFilterMuscle);
        checkBoxFilterEquipment = findViewById(R.id.checkBoxFilterEquipment);
        spinnerMotionType = findViewById(R.id.spinnerMotionType);
        spinnerMuscleGroup = findViewById(R.id.spinnerMuscleGroup);
        spinnerEquipment = findViewById(R.id.spinnerEquipment);
        buttonCancel = findViewById(R.id.buttonCancel);
        buttonSaveBlock = findViewById(R.id.buttonSaveBlock);

        // Обробник кнопки "Скасувати"
        buttonCancel.setOnClickListener(v -> dismiss());

        // Обробник кнопки "Зберегти"
        buttonSaveBlock.setOnClickListener(v -> saveTrainingBlock());


        if (blockToEdit != null) {
            loadBlockData();
        }
    }


    private void loadBlockData() {
        editTextBlockName.setText(blockToEdit.getName());
        editTextBlockDescription.setText(blockToEdit.getDescription());
        // Додамо логіку завантаження вибраних фільтрів
    }

    // Метод для збереження нового тренувального блоку
    private void saveTrainingBlock() {
        String name = editTextBlockName.getText().toString().trim();
        String description = editTextBlockDescription.getText().toString().trim();

        if (name.isEmpty()) {
            editTextBlockName.setError("Введіть назву блоку");
            return;
        }

        if (blockToEdit == null) {
            // Додаємо новий блок
            TrainingBlock block = new TrainingBlock(0, gymDayId, name, description, new ArrayList<>());
            long blockId = planManagerDAO.addTrainingBlock(block);

            if (blockId != -1) {
                saveFilters(blockId);
            }
        } else {
            // Оновлюємо існуючий блок
            blockToEdit.setName(name);
            blockToEdit.setDescription(description);
            planManagerDAO.updateTrainingBlock(blockToEdit);
        }



        // Оновлюємо список блоків у головному активіті
        if (listener != null) {
            listener.onBlockAdded();
        }

        dismiss();


    }





    private void saveFilters(long blockId) {
        if (checkBoxFilterMotion.isChecked()) {
            String motionType = spinnerMotionType.getSelectedItem().toString();
            planManagerDAO.addTrainingBlockFilter(blockId, "motionType", motionType);
        }
        if (checkBoxFilterMuscle.isChecked()) {
            String muscleGroup = spinnerMuscleGroup.getSelectedItem().toString();
            planManagerDAO.addTrainingBlockFilter(blockId, "muscleGroup", muscleGroup);
        }
        if (checkBoxFilterEquipment.isChecked()) {
            String equipment = spinnerEquipment.getSelectedItem().toString();
            planManagerDAO.addTrainingBlockFilter(blockId, "equipment", equipment);
        }
    }

}

