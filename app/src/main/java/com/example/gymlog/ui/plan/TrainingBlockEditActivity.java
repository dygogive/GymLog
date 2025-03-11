package com.example.gymlog.ui.plan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymlog.R;
import com.example.gymlog.data.db.PlanManagerDAO;
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
    private PlanManagerDAO planManagerDAO;
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


        planManagerDAO = new PlanManagerDAO(this);
        trainingBlocks = new ArrayList<>();


        // Ініціалізація UI елементів
        recyclerViewTrainingBlocks = findViewById(R.id.recyclerViewTrainingBlocks);
        buttonAddTrainingBlock = findViewById(R.id.buttonAddTrainingBlock);

        // Налаштування RecyclerView
        recyclerViewTrainingBlocks.setLayoutManager(new LinearLayoutManager(this));
        trainingBlocks = new ArrayList<>();
        trainingBlockAdapter = new TrainingBlockAdapter(trainingBlocks, planManagerDAO, new TrainingBlockAdapter.OnTrainingBlockClickListener() {
            @Override
            public void onBlockClick(TrainingBlock block) {
                // Логіка відкриття блоку для редагування
                openBlockEditDialog(block);
            }

            @Override
            public void onDeleteBlockClick(TrainingBlock block) {
                planManagerDAO.deleteTrainingBlock(block.getId());
                trainingBlocks.remove(block);
                trainingBlockAdapter.notifyDataSetChanged();
                Toast.makeText(TrainingBlockEditActivity.this, "Блок видалено", Toast.LENGTH_SHORT).show();
            }
        });
        recyclerViewTrainingBlocks.setAdapter(trainingBlockAdapter);

        //Завантажити блоки з бази
        loadTrainingBlocks();

        // Обробник натискання кнопки "Додати тренувальний блок"
        buttonAddTrainingBlock.setOnClickListener(v -> openBlockCreationDialog());
    }

    // Метод для відкриття діалогу створення блоку
    private void openBlockCreationDialog() {
        TrainingBlockDialog dialog = new TrainingBlockDialog(this, gymDayId, this::loadTrainingBlocks);
        dialog.show();
    }


    // Метод для відкриття діалогу редагування блоку
    private void openBlockEditDialog(TrainingBlock block) {
        TrainingBlockDialog dialog = new TrainingBlockDialog(this, gymDayId, block, this::loadTrainingBlocks);
        dialog.show();
    }


    // Метод для завантаження списку тренувальних блоків
    private void loadTrainingBlocks() {
        trainingBlocks.clear();
        trainingBlocks.addAll(planManagerDAO.getTrainingBlocksByDayId(gymDayId));
        trainingBlockAdapter.notifyDataSetChanged();
    }
}
