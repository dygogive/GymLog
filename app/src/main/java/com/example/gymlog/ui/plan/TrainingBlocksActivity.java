package com.example.gymlog.ui.plan;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymlog.R;
import com.example.gymlog.data.db.ExerciseDAO;
import com.example.gymlog.data.db.PlanManagerDAO;
import com.example.gymlog.data.exercise.Exercise;
import com.example.gymlog.data.plan.TrainingBlock;
import com.example.gymlog.ui.dialogs.ConfirmDeleteDialog;
import com.example.gymlog.ui.exercise2.dialogs.DialogForExerciseEdit;
import com.example.gymlog.ui.plan.adapter.TrainingBlockAdapter;
import com.example.gymlog.ui.plan.dialogs.TrainingBlockDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
            DialogForExerciseEdit dialog = new DialogForExerciseEdit(
                    TrainingBlocksActivity.this,
                    () -> {
                        loadTrainingBlocks();
                    }
                    );

            dialog.setOnExerciseCreatedListener(newExercise -> {
                // Після створення вправи додаємо її до блоку
                planManagerDAO.addExerciseToBlock(block.getId(), newExercise.getId());

                // Перезавантажуємо блоки для оновлення списку
                loadTrainingBlocks();

                Toast.makeText(
                        TrainingBlocksActivity.this,
                        "Вправу додано в блок: " + newExercise.getName(),
                        Toast.LENGTH_SHORT
                ).show();
            });

            dialog.showWithPreselectedFilters(
                    null,
                    block.getMotion(),
                    block.getMuscleGroupList(),
                    block.getEquipment()
            );

            //тут добавити створену вправу у список вправ Тренувального блоку.
        }

        @Override
        public void onEditExercises(TrainingBlock block) {
            showExerciseSelectionDialog(block);
        }
    }

    private void showExerciseSelectionDialog(TrainingBlock block) {
        List<Exercise> recommendedExercises = planManagerDAO.getExercisesForTrainingBlock(block.getId());
        List<Exercise> selectedExercises = planManagerDAO.getBlockExercises(block.getId());

        // Створюємо множину ID обраних вправ для швидкого порівняння
        Set<Long> selectedExerciseIds = new HashSet<>();
        for (Exercise ex : selectedExercises) {
            selectedExerciseIds.add(ex.getId());
        }

        // Масив для діалогу: всі рекомендовані вправи, але з перевіркою, які вже були вибрані
        String[] exerciseNames = new String[recommendedExercises.size()];
        boolean[] checkedItems = new boolean[recommendedExercises.size()];

        for (int i = 0; i < recommendedExercises.size(); i++) {
            Exercise exercise = recommendedExercises.get(i);
            exerciseNames[i] = exercise.getName();
            checkedItems[i] = selectedExerciseIds.contains(exercise.getId()); // Чи вже вибрали раніше?
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(TrainingBlocksActivity.this);
        builder.setTitle("Редагувати вправи блоку: " + block.getName());

        builder.setMultiChoiceItems(exerciseNames, checkedItems, (dialog, which, isChecked) -> {
            checkedItems[which] = isChecked;
            Log.d("howExerciseSelectionDialog", "Вправа '" + exerciseNames[which] + "' " + (isChecked ? "додана" : "видалена"));
        });

        builder.setPositiveButton("Зберегти", (dialog, which) -> {
            List<Exercise> updatedExerciseList = new ArrayList<>();

            for (int i = 0; i < recommendedExercises.size(); i++) {
                if (checkedItems[i]) {
                    updatedExerciseList.add(recommendedExercises.get(i));
                }
            }

            // Логуємо обрані вправи
            Log.d("howExerciseSelectionDialog", "Оновлений список вправ для блоку ID: " + block.getId());
            for (Exercise ex : updatedExerciseList) {
                Log.d("howExerciseSelectionDialog", "✓ Вправа ID: " + ex.getId() + " | Назва: " + ex.getName());
            }

            // Оновлюємо список вправ у базі
            planManagerDAO.updateTrainingBlockExercises(block.getId(), updatedExerciseList);

            // Оновлюємо UI
            loadTrainingBlocks();
        });

        builder.setNegativeButton("Скасувати", null);
        builder.show();
    }




}
