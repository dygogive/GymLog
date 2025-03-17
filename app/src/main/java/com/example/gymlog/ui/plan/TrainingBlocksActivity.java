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

// Активність для редагування тренувальних блоків
public class TrainingBlocksActivity extends AppCompatActivity {

    private RecyclerView recyclerViewTrainingBlocks;
    private FloatingActionButton buttonAddTrainingBlock;
    public TrainingBlockAdapter trainingBlockAdapter;
    private List<TrainingBlock> trainingBlocks;
    private PlanManagerDAO planManagerDAO;
    private long gymDayId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
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
        trainingBlockAdapter = new TrainingBlockAdapter(
                TrainingBlocksActivity.this,
                trainingBlocks,
                planManagerDAO,
                new TrainingBlockListener()
                );


        recyclerViewTrainingBlocks.setAdapter(trainingBlockAdapter);


        // Створюємо колбек (callback), який керує drag & drop
        ItemTouchHelper.Callback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN,  // drag flags (вгору/вниз)
                0                                           // swipe flags (0 – без свайпів)
        ) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {

                int fromPosition = viewHolder.getBindingAdapterPosition();
                int toPosition = target.getBindingAdapterPosition();

                // Викликаємо метод, що міняє місцями елементи в адаптері
                trainingBlockAdapter.moveItem(fromPosition, toPosition);

                // За бажанням можна зберегти оновлений порядок у базі
                updateTrainingBlockPositionsInDB();

                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Тут нічого не робимо, бо swipeFlags = 0
            }
        };


        // Під'єднуємо цей колбек до RecyclerView
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewTrainingBlocks);


        //Завантажити блоки з бази
        loadTrainingBlocks();

        // Обробник натискання кнопки "Додати тренувальний блок"
        buttonAddTrainingBlock.setOnClickListener(v -> openBlockCreationDialog());
    }

    private void updateTrainingBlockPositionsInDB() {
        planManagerDAO.updateTrainingBlockPositions(trainingBlocks);
    }

    // Метод для відкриття діалогу створення блоку
    public void openBlockCreationDialog() {
        TrainingBlockDialog dialog = new TrainingBlockDialog(this, gymDayId, this::loadTrainingBlocks);
        dialog.show();
    }


    // Метод для відкриття діалогу редагування блоку
    public void openBlockEditDialog(TrainingBlock block) {
        TrainingBlockDialog dialog = new TrainingBlockDialog(this, gymDayId, block, this::loadTrainingBlocks);
        dialog.show();
    }


    // Метод для завантаження списку тренувальних блоків
    public void loadTrainingBlocks() {
        trainingBlocks.clear();
        trainingBlocks.addAll(planManagerDAO.getTrainingBlocksByDayId(gymDayId));
        trainingBlockAdapter.notifyDataSetChanged();
    }


    //Слухач натискань на елементі списка
    private class TrainingBlockListener implements TrainingBlockAdapter.OnTrainingBlockClickListener{

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
    }
}
