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
import com.example.gymlog.sqlopenhelper.PlanManagerDAO;
import com.example.gymlog.model.exercise.Exercise;
import com.example.gymlog.model.exercise.ExerciseInBlock;
import com.example.gymlog.model.plan.TrainingBlock;
import com.example.gymlog.ui.dialogs.ConfirmDeleteDialog;
import com.example.gymlog.ui.exercise.dialogs.DialogForExerciseEdit;
import com.example.gymlog.ui.plan.adapter.TrainingBlockAdapter;
import com.example.gymlog.ui.plan.dialogs.TrainingBlockDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Активність для редагування тренувальних блоків (створення, видалення, переміщення).
 */
public class TrainingBlocksActivity extends AppCompatActivity {

    private RecyclerView recyclerViewTrainingBlocks;
    private FloatingActionButton buttonAddTrainingBlock;
    private TrainingBlockAdapter trainingBlockAdapter;
    private final List<TrainingBlock> trainingBlocks = new ArrayList<>();
    private PlanManagerDAO planManagerDAO;
    private long gymDayId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Повноекранний режим
        setContentView(R.layout.activity_training_block_edit);

        // Перевірка переданого ідентифікатора дня тренування
        gymDayId = getIntent().getLongExtra("gym_day_id", -1);
        if (gymDayId == -1) {
            Toast.makeText(this, "Помилка: Невідомий день тренування", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        planManagerDAO = new PlanManagerDAO(this); // Ініціалізація DAO
        initUI(); // Налаштування UI
        setupRecyclerView(); // Налаштування RecyclerView
        setupDragAndDrop(); // Налаштування drag & drop
        loadTrainingBlocks(); // Завантаження даних
    }

    // Ініціалізація UI
    private void initUI() {
        recyclerViewTrainingBlocks = findViewById(R.id.recyclerViewTrainingBlocks);
        buttonAddTrainingBlock = findViewById(R.id.buttonAddTrainingBlock);
        buttonAddTrainingBlock.setOnClickListener(v -> openBlockCreationDialogByFAB());
    }

    // Налаштування RecyclerView
    private void setupRecyclerView() {
        recyclerViewTrainingBlocks.setLayoutManager(new LinearLayoutManager(this));
        trainingBlockAdapter = new TrainingBlockAdapter(this, trainingBlocks, planManagerDAO, new TrainingBlockListener());
        recyclerViewTrainingBlocks.setAdapter(trainingBlockAdapter);
    }

    // Налаштування drag & drop
    private void setupDragAndDrop() {
        ItemTouchHelper.Callback callback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getBindingAdapterPosition();
                int toPosition = target.getBindingAdapterPosition();
                trainingBlockAdapter.moveItem(fromPosition, toPosition);
                updateTrainingBlockPositionsInDB();
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Swipe ігнорується
            }
        };
        new ItemTouchHelper(callback).attachToRecyclerView(recyclerViewTrainingBlocks);
    }

    // Оновлення позицій блоків у базі даних
    private void updateTrainingBlockPositionsInDB() {
        planManagerDAO.updateTrainingBlockPositions(trainingBlocks);
    }

    // Відкриття діалогу створення нового блоку
    private void openBlockCreationDialogByFAB() {
        TrainingBlockDialog dialog = new TrainingBlockDialog(this, gymDayId, this::loadTrainingBlocks);
        dialog.show();
    }

    // Відкриття діалогу редагування блоку
    public void openBlockEditDialog(TrainingBlock block) {
        TrainingBlockDialog dialog = new TrainingBlockDialog(this, gymDayId, block, this::loadTrainingBlocks);
        dialog.show();
    }

    // Завантаження тренувальних блоків з бази даних
    public void loadTrainingBlocks() {
        trainingBlocks.clear();
        trainingBlocks.addAll(planManagerDAO.getTrainingBlocksByDayId(gymDayId));
        trainingBlockAdapter.notifyDataSetChanged();
    }

    // Обробник подій для тренувальних блоків
    private class TrainingBlockListener implements TrainingBlockAdapter.OnTrainingBlockClickListener {
        @Override
        public void onEditClick(TrainingBlock block) {
            openBlockEditDialog(block);
        }

        @Override
        public void onDeleteClick(TrainingBlock block) {
            ConfirmDeleteDialog.show(TrainingBlocksActivity.this, block.getName(), () -> {
                planManagerDAO.deleteTrainingBlock(block.getId());
                trainingBlocks.remove(block);
                trainingBlockAdapter.notifyDataSetChanged();
                Toast.makeText(TrainingBlocksActivity.this, "Блок видалено", Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public void onAddExercise(TrainingBlock block) {
            DialogForExerciseEdit dialog = new DialogForExerciseEdit(TrainingBlocksActivity.this, TrainingBlocksActivity.this::loadTrainingBlocks);
            dialog.setOnExerciseCreatedListener(newExercise -> {
                planManagerDAO.addExerciseToBlock(block.getId(), newExercise.getId());
                loadTrainingBlocks();
                Toast.makeText(TrainingBlocksActivity.this, "Вправу додано в блок: " + newExercise.getName(), Toast.LENGTH_SHORT).show();
            });
            dialog.showWithPreselectedFilters(null, block.getMotion(), block.getMuscleGroupList(), block.getEquipment());
        }

        @Override
        public void onEditExercises(TrainingBlock block) {
            showExerciseSelectionDialog(block);
        }
    }

    // Діалог вибору вправ для блоку
    private void showExerciseSelectionDialog(TrainingBlock block) {
        List<ExerciseInBlock> recommendedExercises = planManagerDAO.getExercisesForTrainingBlock(block.getId());
        List<ExerciseInBlock> selectedExercises = planManagerDAO.getBlockExercises(block.getId());

        Set<Long> oldSelectedIds = new HashSet<>();
        for (ExerciseInBlock oldEx : selectedExercises) {
            oldSelectedIds.add(oldEx.getId());
        }

        String[] exerciseNames = new String[recommendedExercises.size()];
        boolean[] checkedItems = new boolean[recommendedExercises.size()];
        for (int i = 0; i < recommendedExercises.size(); i++) {
            Exercise e = recommendedExercises.get(i);
            exerciseNames[i] = e.getNameOnly(TrainingBlocksActivity.this);
            checkedItems[i] = oldSelectedIds.contains(e.getId());
        }

        new AlertDialog.Builder(this)
                .setTitle("Редагувати вправи блоку: " + block.getName())
                .setMultiChoiceItems(exerciseNames, checkedItems, (dialog, which, isChecked) -> checkedItems[which] = isChecked)
                .setPositiveButton("Зберегти", (dialog, whichBtn) -> {
                    selectedExercises.sort((a, b) -> Integer.compare(a.getPosition(), b.getPosition()));
                    List<ExerciseInBlock> updatedExerciseList = new ArrayList<>();

                    for (ExerciseInBlock oldEx : selectedExercises) {
                        long oldId = oldEx.getId();
                        for (int i = 0; i < recommendedExercises.size(); i++) {
                            if (recommendedExercises.get(i).getId() == oldId && checkedItems[i]) {
                                updatedExerciseList.add(oldEx);
                                break;
                            }
                        }
                    }

                    for (int i = 0; i < recommendedExercises.size(); i++) {
                        if (checkedItems[i] && !oldSelectedIds.contains(recommendedExercises.get(i).getId())) {
                            Exercise e = recommendedExercises.get(i);
                            updatedExerciseList.add(new ExerciseInBlock(e.getId(), e.getName(), e.getMotion(), e.getMuscleGroupList(), e.getEquipment(), 0));
                        }
                    }

                    for (int i = 0; i < updatedExerciseList.size(); i++) {
                        updatedExerciseList.get(i).setPosition(i);
                    }

                    planManagerDAO.updateTrainingBlockExercises(block.getId(), updatedExerciseList);
                    loadTrainingBlocks();
                })
                .setNegativeButton("Скасувати", null)
                .show();
    }
}