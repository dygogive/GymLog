package com.example.gymlog.ui.plan;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gymlog.R;
import com.example.gymlog.data.plan.TrainingBlock;

// Активність для створення та редагування тренувального блоку
public class TrainingBlockEditActivity extends AppCompatActivity {

    private EditText editTextBlockName, editTextBlockDescription;
    private Button buttonSaveBlock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_block_edit);

        // Ініціалізація елементів UI
        editTextBlockName = findViewById(R.id.editTextBlockName);
        editTextBlockDescription = findViewById(R.id.editTextBlockDescription);
        buttonSaveBlock = findViewById(R.id.buttonSaveBlock);

        // Обробник натискання кнопки "Зберегти блок"
        buttonSaveBlock.setOnClickListener(v -> saveTrainingBlock());
    }

    // Збереження тренувального блоку
    private void saveTrainingBlock() {
        String blockName = editTextBlockName.getText().toString();
        String blockDescription = editTextBlockDescription.getText().toString();

        if (blockName.isEmpty()) {
            Toast.makeText(this, "Введіть назву тренувального блоку!", Toast.LENGTH_SHORT).show();
            return;
        }

        TrainingBlock newBlock = new TrainingBlock(-1, -1, blockName, blockDescription, null);

        // Поки що просто показуємо повідомлення (пізніше додамо в базу)
        Toast.makeText(this, "Блок '" + newBlock.getName() + "' створено!", Toast.LENGTH_SHORT).show();

        finish();
    }
}
