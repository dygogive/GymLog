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
import com.example.gymlog.data.plan.TrainingBlock;


// Діалог для створення/редагування тренувального блоку
public class TrainingBlockDialog extends Dialog {

    private EditText editTextBlockName, editTextBlockDescription;
    private CheckBox checkBoxFilterMotion, checkBoxFilterMuscle, checkBoxFilterEquipment;
    private Spinner spinnerMotionType, spinnerMuscleGroup, spinnerEquipment;
    private Button buttonCancel, buttonSaveBlock;

    private final OnTrainingBlockCreatedListener listener;
    private final long gymDayId;

    // Інтерфейс для передачі нового блоку у активність
    public interface OnTrainingBlockCreatedListener {
        void onTrainingBlockCreated(TrainingBlock block);
    }

    public TrainingBlockDialog(@NonNull Context context, long gymDayId, OnTrainingBlockCreatedListener listener) {
        super(context);
        this.gymDayId = gymDayId;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_training_block);
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
    }

    // Метод для збереження нового тренувального блоку
    private void saveTrainingBlock() {
        String blockName = editTextBlockName.getText().toString().trim();
        String blockDescription = editTextBlockDescription.getText().toString().trim();

        if (blockName.isEmpty()) {
            Toast.makeText(getContext(), "Введіть назву блоку!", Toast.LENGTH_SHORT).show();
            return;
        }

        TrainingBlock newBlock = new TrainingBlock(-1, gymDayId, blockName, blockDescription, null);

        listener.onTrainingBlockCreated(newBlock);
        dismiss();
    }
}

