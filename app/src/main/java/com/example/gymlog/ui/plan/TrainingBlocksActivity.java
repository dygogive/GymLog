package com.example.gymlog.ui.plan;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymlog.R;
import com.example.gymlog.data.db.PlanManagerDAO;
import com.example.gymlog.data.exercise.Exercise;
import com.example.gymlog.data.exercise.ExerciseInBlock;
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
    private final List<TrainingBlock> trainingBlocks = new ArrayList<>();

    // DAO для роботи з базою, а також ідентифікатор тренувального дня
    private PlanManagerDAO planManagerDAO;
    private long gymDayId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);  // Забезпечує більш сучасний вигляд (повноекранне відображення)
        setContentView(R.layout.activity_training_block_edit);

        // 1) Перевіряємо, чи передано коректний gymDayId
        gymDayId = getIntent().getLongExtra("gym_day_id", -1);
        if (gymDayId == -1) {
            Toast.makeText(this, "Помилка: Невідомий день тренування", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 2) Ініціалізуємо DAO
        planManagerDAO = new PlanManagerDAO(this);

        // 3) Налаштовуємо UI
        initUI();
        setupRecyclerView();
        setupDragAndDrop();

        // 4) Завантажуємо блоки з бази даних
        loadTrainingBlocks();
    }

    /**
     * Ініціалізація UI елементів і навішування обробників.
     */
    private void initUI() {
        recyclerViewTrainingBlocks = findViewById(R.id.recyclerViewTrainingBlocks);
        buttonAddTrainingBlock = findViewById(R.id.buttonAddTrainingBlock);

        // Клік для створення нового тренувального блоку
        buttonAddTrainingBlock.setOnClickListener(v -> openBlockCreationDialogByFAB());
    }

    /**
     * Налаштовуємо RecyclerView і створюємо адаптер.
     * (Опційно можна увімкнути setHasStableIds(true) + перевизначити getItemId() в адаптері.)
     */
    private void setupRecyclerView() {
        recyclerViewTrainingBlocks.setLayoutManager(new LinearLayoutManager(this));
        trainingBlockAdapter = new TrainingBlockAdapter(
                this,
                trainingBlocks,
                planManagerDAO,
                new TrainingBlockListener()
        );

        // Якщо бажаєш стабільні ID:
        // trainingBlockAdapter.setHasStableIds(true);

        recyclerViewTrainingBlocks.setAdapter(trainingBlockAdapter);
    }

    /**
     * Створюємо SimpleCallback для drag & drop і прикріплюємо його до RecyclerView.
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

                // Міняємо місцями елементи в адаптері (через “вирізати й вставити”)
                trainingBlockAdapter.moveItem(fromPosition, toPosition);

                // Оновлюємо порядок у базі, якщо це важливо зберегти
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
     * Зберігаємо оновлені позиції блоків у базі.
     */
    private void updateTrainingBlockPositionsInDB() {
        planManagerDAO.updateTrainingBlockPositions(trainingBlocks);
    }

    /**
     * Метод для відкриття діалогу створення нового тренувального блоку.
     */
    private void openBlockCreationDialogByFAB() {
        TrainingBlockDialog dialog = new TrainingBlockDialog(
                this,
                gymDayId,
                this::loadTrainingBlocks
        );
        dialog.show();
    }

    /**
     * Метод для відкриття діалогу редагування існуючого тренувального блоку.
     */
    public void openBlockEditDialog(TrainingBlock block) {
        TrainingBlockDialog dialog = new TrainingBlockDialog(
                this,
                gymDayId,
                block,
                this::loadTrainingBlocks
        );
        dialog.show();
    }

    /**
     * Завантажуємо тренувальні блоки із БД та оновлюємо адаптер.
     */
    public void loadTrainingBlocks() {
        trainingBlocks.clear();
        trainingBlocks.addAll(planManagerDAO.getTrainingBlocksByDayId(gymDayId));
        trainingBlockAdapter.notifyDataSetChanged();
    }

    /**
     * Внутрішній клас для обробки подій на елементах списку:
     * - Редагування
     * - Видалення
     * - Додавання вправи
     * - Редагування списку вправ
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
            // Діалог для створення нової вправи
            DialogForExerciseEdit dialog = new DialogForExerciseEdit(
                    TrainingBlocksActivity.this,
                    TrainingBlocksActivity.this::loadTrainingBlocks
            );

            dialog.setOnExerciseCreatedListener(newExercise -> {
                // Додаємо нову вправу в блок
                planManagerDAO.addExerciseToBlock(block.getId(), newExercise.getId());
                loadTrainingBlocks(); // Оновлюємо UI

                Toast.makeText(
                        TrainingBlocksActivity.this,
                        "Вправу додано в блок: " + newExercise.getName(),
                        Toast.LENGTH_SHORT
                ).show();
            });

            // Тут можна попередньо встановити фільтри (рух, група м’язів, обладнання)
            dialog.showWithPreselectedFilters(
                    null,
                    block.getMotion(),
                    block.getMuscleGroupList(),
                    block.getEquipment()
            );
        }

        @Override
        public void onEditExercises(TrainingBlock block) {
            showExerciseSelectionDialog(block);
        }
    }

    /**
     * Діалог із MultiChoice-списком можливих вправ для блоку.
     */
    private void showExerciseSelectionDialog(TrainingBlock block) {
        // 1. Отримуємо рекомендовані вправи + вже додані
        List<Exercise>        recommendedExercises = planManagerDAO.getExercisesForTrainingBlock(block.getId());
        List<ExerciseInBlock> selectedExercises    = planManagerDAO.getBlockExercises(block.getId());

        Set<Long> oldSelectedIds = new HashSet<>();
        for (ExerciseInBlock oldEx : selectedExercises) {
            oldSelectedIds.add(oldEx.getId());
        }

        // 2. Готуємо дані для Multiple-Choice
        String[] exerciseNames = new String[recommendedExercises.size()];
        boolean[] checkedItems = new boolean[recommendedExercises.size()];
        for (int i = 0; i < recommendedExercises.size(); i++) {
            Exercise e        = recommendedExercises.get(i);
            exerciseNames[i]  = e.getNameOnly(TrainingBlocksActivity.this);
            checkedItems[i]   = oldSelectedIds.contains(e.getId());
        }

        // 3. Будуємо AlertDialog
        new AlertDialog.Builder(this)
                .setTitle("Редагувати вправи блоку: " + block.getName())
                .setMultiChoiceItems(exerciseNames, checkedItems, (dialog, which, isChecked) -> {
                    // Оновлюємо локальний масив “checkedItems”, при потребі
                    checkedItems[which] = isChecked;
                })
                .setPositiveButton("Зберегти", (dialog, whichBtn) -> {
                    // 3.1 Сортуємо старий список, щоб зберегти порядок
                    selectedExercises.sort((a, b) -> Integer.compare(a.getPosition(), b.getPosition()));

                    // 3.2 Формуємо оновлений список
                    List<ExerciseInBlock> updatedExerciseList = new ArrayList<>();

                    // Додаємо “старі” вправи, які залишаються відміченими
                    for (ExerciseInBlock oldEx : selectedExercises) {
                        long oldId = oldEx.getId();
                        for (int i = 0; i < recommendedExercises.size(); i++) {
                            if (recommendedExercises.get(i).getId() == oldId && checkedItems[i]) {
                                updatedExerciseList.add(oldEx); // зберігає position
                                break;
                            }
                        }
                    }

                    // Додаємо “нові” відмічені вправи
                    for (int i = 0; i < recommendedExercises.size(); i++) {
                        if (checkedItems[i]) {
                            Exercise e = recommendedExercises.get(i);
                            if (!oldSelectedIds.contains(e.getId())) {
                                // Нова вправа в блоці
                                updatedExerciseList.add(
                                        new ExerciseInBlock(e.getId(), e.getName(),
                                                e.getMotion(), e.getMuscleGroupList(),
                                                e.getEquipment(), 0
                                        )
                                );
                            }
                        }
                    }

                    // 3.3 Переприсвоюємо position (0..N-1)
                    for (int i = 0; i < updatedExerciseList.size(); i++) {
                        updatedExerciseList.get(i).setPosition(i);
                    }

                    // 3.4 Оновлюємо базу + UI
                    planManagerDAO.updateTrainingBlockExercises(block.getId(), updatedExerciseList);
                    loadTrainingBlocks();
                })
                .setNegativeButton("Скасувати", null)
                .show();
    }
}
