package com.example.gymlog.ui.legacy.program;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymlog.R;
import com.example.gymlog.data.local.legacy.PlanManagerDAO;
import com.example.gymlog.data.repository.plan.TrainingBlockRepository;
import com.example.gymlog.data.repository.plan.TrainingBlockRepositoryAdapter;
import com.example.gymlog.data.repository.plan.TrainingBlocksCallback;
import com.example.gymlog.domain.model.exercise.Exercise;
import com.example.gymlog.domain.model.exercise.ExerciseInBlock;
import com.example.gymlog.domain.model.plan.TrainingBlock;
import com.example.gymlog.ui.legacy.dialogs.ConfirmDeleteDialog;
import com.example.gymlog.ui.legacy.exercise.DialogForExerciseEdit;
import com.example.gymlog.ui.legacy.program.adapters.TrainingBlockAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Активність для редагування тренувальних блоків (створення, видалення, переміщення).
 */
@AndroidEntryPoint
public class TrainingBlocksActivity extends AppCompatActivity {

    private RecyclerView recyclerViewTrainingBlocks;
    private TrainingBlockAdapter trainingBlockAdapter;
    private ItemTouchHelper itemTouchHelper = null;
    private final List<TrainingBlock> trainingBlocks = new ArrayList<>();
    private PlanManagerDAO planManagerDAO;
    @Inject
    protected TrainingBlockRepository trainingBlockRepository; // Ініціалізуйте як потрібно
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

        loadTrainingBlocks(); // Завантаження даних
        setupDragAndDrop(); // Налаштування drag & drop

    }

    // Ініціалізація UI
    private void initUI() {
        recyclerViewTrainingBlocks = findViewById(R.id.recyclerViewTrainingBlocks);
        FloatingActionButton buttonAddTrainingBlock = findViewById(R.id.buttonAddTrainingBlock);
        buttonAddTrainingBlock.setOnClickListener(v -> openBlockCreationDialogByFAB());

        //заголовок актівіті
        TextView textViewBlockTitle = findViewById(R.id.textViewBlockTitle);
        TextView textViewBlockDescr = findViewById(R.id.textViewBlockDescription);

        //заповнити інфою з попереднього актівіті
        String name = getIntent().getStringExtra("gym_day_name");
        String description = getIntent().getStringExtra("gym_day_description");

        // Обрізаємо довгий опис і додаємо три крапки (…)
        if (description.length() > 50) {
            textViewBlockDescr.setText(description.substring(0, 50) + "...");
        } else {
            textViewBlockDescr.setText(description);
        }

        // При натисканні на обрізаний опис показуємо AlertDialog з повним текстом
        textViewBlockDescr.setOnClickListener(v -> {
            new AlertDialog.Builder(this, R.style.RoundedDialogTheme)
                    .setTitle(name)
                    .setMessage(description)
                    .setPositiveButton("OK", null)
                    .show();
        });

        textViewBlockTitle.setText(name);
        textViewBlockDescr.setText(description);

        //діалог для повної інформації
        textViewBlockDescr.setOnClickListener(v -> {
            new AlertDialog.Builder(this, R.style.RoundedDialogTheme)
                    .setTitle(name)
                    .setMessage(description)
                    .setPositiveButton("OK", null)
                    .show();
        });
    }

    // Налаштування RecyclerView
    private void setupRecyclerView() {
        recyclerViewTrainingBlocks.setLayoutManager(new LinearLayoutManager(this));
        trainingBlockAdapter = new TrainingBlockAdapter(
                this,
                trainingBlocks,
                planManagerDAO,
                new MenuTrainingBlockListener(),
                blockViewHolder -> {
                    itemTouchHelper.startDrag(blockViewHolder);
                });
        recyclerViewTrainingBlocks.setAdapter(trainingBlockAdapter);
    }

    // Налаштування drag & drop
    private void setupDragAndDrop() {
        //відкріпити recyclerViewTrainingBlocks від старого exercisesItemTouchHelper
        if (itemTouchHelper != null)
            itemTouchHelper.attachToRecyclerView(null);


        ItemTouchHelper.Callback callback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getBindingAdapterPosition();
                int toPosition = target.getBindingAdapterPosition();
                trainingBlockAdapter.moveItem(fromPosition, toPosition);
                planManagerDAO.updateTrainingBlockPositions(trainingBlocks);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Swipe ігнорується
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return false; // Вимкни longPressDrag зовнішнього RecyclerView
            }
        };
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerViewTrainingBlocks);
    }


    // Відкриття діалогу створення нового блоку
    private void openBlockCreationDialogByFAB() {
        DialogBlocCreator dialog = new DialogBlocCreator(this, gymDayId, this::loadTrainingBlocks);
        dialog.show();
    }

    // Відкриття діалогу редагування блоку
    public void openBlockEditDialog(TrainingBlock block) {
        DialogBlocCreator dialog = new DialogBlocCreator(this, gymDayId, block, this::loadTrainingBlocks);
        dialog.show();
    }

    // Завантаження тренувальних блоків з бази даних
//    @SuppressLint("NotifyDataSetChanged")
//    public void loadTrainingBlocks() {
//        trainingBlocks.clear();
//        trainingBlocks.addAll(planManagerDAO.getTrainingBlocksByDayId(gymDayId));
//        trainingBlockAdapter.notifyDataSetChanged();
//    }
    //Альтернативний метод Завантаження тренувальних блоків з бази даних Room
    // Припустимо, у вас є спосіб отримати репозиторій, наприклад, через конструктор або DI


    public void loadTrainingBlocks() {
        // Очищаємо поточний список блоків, щоб уникнути дублювання
        trainingBlocks.clear();

        // Викликаємо метод з адаптера. Передаємо gymDayId, екземпляр репозиторію та реалізацію callback
        TrainingBlockRepositoryAdapter.loadTrainingBlocks(gymDayId, trainingBlockRepository, new TrainingBlocksCallback() {
              @Override
            public void onResult(@NotNull List<? extends @NotNull TrainingBlock> blocks) {
                trainingBlocks.addAll(blocks);
                trainingBlockAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(@NonNull Throwable exception) {
                Toast.makeText(TrainingBlocksActivity.this, "Помилка: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("TrainingBlocksActivity", "Error loading training blocks", exception);
            }
        });

    }

    // Обробник подій для тренувальних блоків
    private class MenuTrainingBlockListener implements TrainingBlockAdapter.OnMenuTrainingBlockListener {
        @Override
        public void onEditClick(TrainingBlock block) {
            openBlockEditDialog(block);
        }

        @SuppressLint("NotifyDataSetChanged")
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
            // Передаємо повні списки фільтрів із TrainingBlock
            dialog.showWithPreselectedFilters(null, block.getMotions(), block.getMuscleGroupList(), block.getEquipmentList());
        }


        @Override
        public void onEditExercises(TrainingBlock block) {
            showExerciseSelectionDialog(block);
        }

        @Override
        public void onCloneTrainingBlock(TrainingBlock block) {
//            loadTrainingBlocks();
            // Додаємо клонований елемент до бази даних
            TrainingBlock clonedBlock = planManagerDAO.onStartCloneTrainingBlock(block);
            if (clonedBlock != null) {
                trainingBlocks.add(clonedBlock);
                Log.d("trainingBlocksError", "3");
                loadTrainingBlocks();
                Log.d("trainingBlocksError", "4");
                Toast.makeText(TrainingBlocksActivity.this, "План клоновано!", Toast.LENGTH_SHORT).show();
            } else Toast.makeText(TrainingBlocksActivity.this, "Помилка при клонуванні", Toast.LENGTH_SHORT).show();
        }
    }

    // Діалог вибору вправ для блоку
    private void showExerciseSelectionDialog(TrainingBlock block) {
        // 1) Отримуємо потенційно рекомендовані вправи
        List<Exercise> recommendedExercises = planManagerDAO.recommendExercisesForTrainingBlock(block.getId());
        // 2) Отримуємо вже вибрані вправи
        List<ExerciseInBlock> oldSelected = planManagerDAO.getBlockExercises(block.getId());

        // Зберігаємо їх ID у set, щоб швидко перевіряти
        Set<Long> idsExeInBlock = new HashSet<>();
        for (ExerciseInBlock ex : oldSelected) {
            idsExeInBlock.add(ex.getId());
        }

        // Формуємо масив назв і масив checked
        final String[] recommendNames = new String[recommendedExercises.size()];
        final boolean[] recommendChkBoxes = new boolean[recommendedExercises.size()];

        for (int i = 0; i < recommendedExercises.size(); i++) {
            Exercise e = recommendedExercises.get(i);
            recommendNames[i] = e.getNameOnly(this);

            // Позначимо галочкою, якщо ця вправа була у блоці (ID у idsExeInBlock)
            recommendChkBoxes[i] = idsExeInBlock.contains(e.getId());
        }

        new AlertDialog.Builder(this,R.style.RoundedDialogTheme2)
                .setTitle("Редагувати вправи блоку: " + block.getName())
                .setMultiChoiceItems(recommendNames, recommendChkBoxes,
                        (dialog, which, isChecked) ->
                        recommendChkBoxes[which] = isChecked
                ).setPositiveButton("Зберегти", (dialog, whichBtn) -> {
                    // 1) Збираємо ID всіх вправ, що стали позначені
                    Set<Long> newIds = new HashSet<>();
                    for (int i = 0; i < recommendedExercises.size(); i++) {
                        if (recommendChkBoxes[i]) {
                            newIds.add(recommendedExercises.get(i).getId());
                        }
                    }

                    // 2) Формуємо підсумковий список ExerciseInBlock (зберігаючи порядки старих)
                    List<ExerciseInBlock> updatedList = new ArrayList<>();

                    // (а) Додаємо старі вправи, якщо вони досі в newIds
                    //     і зберігаємо стару position
                    for (ExerciseInBlock oldEx : oldSelected) {
                        if (newIds.contains(oldEx.getId())) {
                            updatedList.add(oldEx);
                            // при цьому oldEx уже має свою стару position
                            // при бажанні можна заново призначати position
                            newIds.remove(oldEx.getId());
                        }
                    }

                    // (б) Додаємо нові вправи (яких раніше не було)
                    for (Exercise recEx : recommendedExercises) {
                        if (newIds.contains(recEx.getId())) {
                            // Створюємо ExerciseInBlock зі позицією = 0 або будь-якою
                            ExerciseInBlock newEIB = new ExerciseInBlock(
                                    -1, //буде в базі оновлено
                                    recEx.getId(),
                                    recEx.getName(),
                                    recEx.getDescription(),
                                    recEx.getMotion(),
                                    recEx.getMuscleGroupList(),
                                    recEx.getEquipment(),
                                    -1 //буде далі оновлено
                            );
                            updatedList.add(newEIB);
                        }
                    }

                    // 3) Перепризначимо position, щоб вони йшли 0..N
                    for (int i = 0; i < updatedList.size(); i++) {
                        updatedList.get(i).setPosition(i);
                    }

                    // 4) Оновлюємо базу
                    planManagerDAO.updateTrainingBlockExercises(block.getId(), updatedList);

                    // 5) Перевантажуємо блоки з бази (щоб отримати актуальний стан)
                    loadTrainingBlocks();
                })
                .setNegativeButton("Скасувати", null)
                .show();
    }

}