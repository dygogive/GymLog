package com.example.gymlog.ui.plan;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymlog.R;
import com.example.gymlog.data.db.PlanManagerDAO;
import com.example.gymlog.data.plan.TrainingBlock;
import com.example.gymlog.ui.dialogs.ConfirmDeleteDialog;
import com.example.gymlog.ui.plan.adapter.TrainingBlockAdapter;
import com.example.gymlog.ui.plan.dialogs.TrainingBlockDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Активність для редагування (створення, видалення, переміщення) тренувальних блоків у списку.
 */
public class TrainingBlocksActivity extends AppCompatActivity {

    // UI компоненти
    private RecyclerView recyclerViewTrainingBlocks;
    private FloatingActionButton buttonAddTrainingBlock;

    // Адаптер + список тренувальних блоків
    private TrainingBlockAdapter trainingBlockAdapter;
    private List<TrainingBlock> trainingBlocks;

    // DAO для роботи з базою, а також ідентифікатор тренувального дня
    private PlanManagerDAO planManagerDAO;
    private long gymDayId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);  // Забезпечує більш сучасний вигляд (займає повний екран)
        setContentView(R.layout.activity_training_block_edit);

        // Отримуємо gymDayId, переданий із попередньої активності
        gymDayId = getIntent().getLongExtra("gym_day_id", -1);
        if (gymDayId == -1) {
            Toast.makeText(this, "Помилка: Невідомий день тренування", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Ініціалізуємо DAO і список
        planManagerDAO = new PlanManagerDAO(this);
        trainingBlocks = new ArrayList<>();

        // Налаштовуємо UI
        initUI();
        setupRecyclerView();

        // Створюємо ItemTouchHelper для drag & drop
        setupDragAndDrop();

        // Завантажуємо блоки з бази даних
        loadTrainingBlocks();
    }

    /**
     * Ініціалізація UI елементів і навішування обробників
     */
    private void initUI() {
        recyclerViewTrainingBlocks = findViewById(R.id.recyclerViewTrainingBlocks);
        buttonAddTrainingBlock = findViewById(R.id.buttonAddTrainingBlock);

        // Додаємо клік для створення нового тренувального блоку
        buttonAddTrainingBlock.setOnClickListener(v -> openBlockCreationDialogByFAB());
    }

    /**
     * Налаштовуємо RecyclerView і створюємо адаптер
     */
    private void setupRecyclerView() {
        recyclerViewTrainingBlocks.setLayoutManager(new LinearLayoutManager(this));
        trainingBlockAdapter = new TrainingBlockAdapter(
                this,
                trainingBlocks,
                planManagerDAO,
                new TrainingBlockListener() // Слухач подій на елементах
        );
        recyclerViewTrainingBlocks.setAdapter(trainingBlockAdapter);
    }

    /**
     * Створюємо SimpleCallback для drag & drop і прикріплюємо його до RecyclerView
     */
    private void setupDragAndDrop() {
        ItemTouchHelper.Callback callback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN,  // Дозволяємо перетягування вгору/вниз
                0  // Вимкнено swipe
        ) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {

                // Отримуємо позиції
                int fromPosition = viewHolder.getBindingAdapterPosition();
                int toPosition = target.getBindingAdapterPosition();

                // Міняємо місцями елементи в адаптері
                trainingBlockAdapter.moveItem(fromPosition, toPosition);

                // За бажанням зберігаємо новий порядок у базі
                updateTrainingBlockPositionsInDB();
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Swipe ігнорується, бо напрямок = 0
            }
        };
        new ItemTouchHelper(callback).attachToRecyclerView(recyclerViewTrainingBlocks);
    }

    /**
     * Метод викликається для збереження оновлених позицій у базі
     */
    private void updateTrainingBlockPositionsInDB() {
        planManagerDAO.updateTrainingBlockPositions(trainingBlocks);
    }

    /**
     * Метод для відкриття діалогу створення нового тренувального блоку
     */
    private void openBlockCreationDialogByFAB() {
        TrainingBlockDialog dialog =
                new TrainingBlockDialog(this, gymDayId, this::loadTrainingBlocks);
        dialog.show();
    }

    /**
     * Метод для відкриття діалогу редагування існуючого тренувального блоку
     */
    public void openBlockEditDialog(TrainingBlock block) {
        TrainingBlockDialog dialog =
                new TrainingBlockDialog(this, gymDayId, block, this::loadTrainingBlocks);
        dialog.show();
    }

    /**
     * Завантажуємо тренувальні блоки із БД та оновлюємо адаптер
     */
    public void loadTrainingBlocks() {
        trainingBlocks.clear();
        trainingBlocks.addAll(planManagerDAO.getTrainingBlocksByDayId(gymDayId));
        trainingBlockAdapter.notifyDataSetChanged();
    }

    /**
     * Внутрішній клас для обробки подій на елементах списку:
     * 1) Редагування
     * 2) Видалення
     */
    private class TrainingBlockListener implements TrainingBlockAdapter.OnTrainingBlockClickListener {
        @Override
        public void onEditClick(TrainingBlock block) {
            openBlockEditDialog(block);
        }

        @Override
        public void onDeleteClick(TrainingBlock block) {
            ConfirmDeleteDialog.OnDeleteConfirmedListener onDeleteConfirmedListener = () -> {
                planManagerDAO.deleteTrainingBlock(block.getId());
                trainingBlocks.remove(block);
                trainingBlockAdapter.notifyDataSetChanged();
                Toast.makeText(TrainingBlocksActivity.this, "Блок видалено", Toast.LENGTH_SHORT).show();
            };

            ConfirmDeleteDialog.show(
                    TrainingBlocksActivity.this,
                    block.getName(),
                    onDeleteConfirmedListener
            );
        }

        @Override
        public void onAddExercise(TrainingBlock block) {
            Toast.makeText(TrainingBlocksActivity.this, "Add Exercise New! ",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onEditExercises(TrainingBlock block) {
            Toast.makeText(TrainingBlocksActivity.this, "Chose Exercises! ",Toast.LENGTH_SHORT).show();
        }
    }
}
