package com.example.gymlog.ui.plan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymlog.R;
import com.example.gymlog.data.plan.TrainingBlock;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

// Активність для редагування тренувальних блоків
public class TrainingBlockEditActivity extends AppCompatActivity {

    private RecyclerView recyclerViewTrainingBlocks;
    private FloatingActionButton buttonAddTrainingBlock;
    private TrainingBlockAdapter trainingBlockAdapter;
    private List<TrainingBlock> trainingBlocks;
    private long gymDayId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_block_edit);

        // Отримуємо переданий gymDayId
        gymDayId = getIntent().getLongExtra("gym_day_id", -1);
        if (gymDayId == -1) {
            Toast.makeText(this, "Помилка: Невідомий день тренування", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Ініціалізація UI елементів
        recyclerViewTrainingBlocks = findViewById(R.id.recyclerViewTrainingBlocks);
        buttonAddTrainingBlock = findViewById(R.id.buttonAddTrainingBlock);

        // Налаштування RecyclerView
        recyclerViewTrainingBlocks.setLayoutManager(new LinearLayoutManager(this));
        trainingBlocks = new ArrayList<>();
        trainingBlockAdapter = new TrainingBlockAdapter(trainingBlocks, new TrainingBlockAdapter.OnTrainingBlockClickListener() {
            @Override
            public void onBlockClick(TrainingBlock block) {
                // Логіка відкриття блоку для редагування
                Toast.makeText(TrainingBlockEditActivity.this, "Редагування блоку: " + block.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeleteBlockClick(TrainingBlock block) {
                trainingBlocks.remove(block);
                trainingBlockAdapter.notifyDataSetChanged();
                Toast.makeText(TrainingBlockEditActivity.this, "Блок видалено", Toast.LENGTH_SHORT).show();
            }
        });
        recyclerViewTrainingBlocks.setAdapter(trainingBlockAdapter);

        // Обробник натискання кнопки "Додати тренувальний блок"
        buttonAddTrainingBlock.setOnClickListener(v -> openBlockCreationDialog());
    }

    // Метод для відкриття діалогу створення блоку
    private void openBlockCreationDialog() {
        // Пізніше тут буде реальний діалог
        Toast.makeText(this, "Діалог створення блоку (ще не реалізовано)", Toast.LENGTH_SHORT).show();
    }
}
