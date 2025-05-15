package com.example.gymlog.ui.legacy.program;

import android.content.Intent;
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
import com.example.gymlog.domain.model.legacy.plan.GymDay;
import com.example.gymlog.ui.legacy.dialogs.ConfirmDeleteDialog;
import com.example.gymlog.ui.legacy.dialogs.DialogCreateEditNameDesc;
import com.example.gymlog.ui.legacy.program.adapters.BasePlanAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Активність для відображення та редагування списку тренувальних днів (GymDay)
 * у рамках обраної програми (plan).
 */
public class GymSessionsActivity extends AppCompatActivity {

    // UI компоненти
    private RecyclerView recyclerViewDays;
    private TextView tvProgramTitle, tvProgramDescription;
    private FloatingActionButton buttonAddDay;

    // DAO і список GymDay
    private PlanManagerDAO planManagerDAO;
    private BasePlanAdapter<GymDay> gymSessionAdapter;
    private List<GymDay> gymDays;

    // Ідентифікатор плану + назва/опис програми
    private long planId;
    private String programName, programDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gym_sessions);

        // Ініціалізація DAO
        planManagerDAO = new PlanManagerDAO(this);

        // Отримуємо дані з Intent (plan_id, programName, programDescription)
        initIntentData();

        // Ініціалізація UI компонентів
        initUI();

        // Налаштовуємо список днів
        setupRecyclerView();

        // Створюємо ItemTouchHelper для drag & drop
        setupDragAndDrop();

        // Якщо planId валідний, завантажимо дні
        if (planId != -1) {
            loadGymSessions();
        } else {
            Toast.makeText(this, "Помилка завантаження програми", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Зчитуємо з Intent ідентифікатор плану і назву/опис програми.
     * Якщо їх немає, завершуємо активність.
     */
    private void initIntentData() {
        Intent intent = getIntent();
        planId = intent.getLongExtra("plan_id", -1);
        programName = intent.getStringExtra("program_name");
        programDescription = intent.getStringExtra("program_description");

        // Якщо дані не передані або некоректні
        if (planId == -1 || programName == null || programDescription == null) {
            Toast.makeText(this, "Помилка завантаження програми", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Ініціалізуємо візуальні елементи і виводимо назву/опис програми
     */
    private void initUI() {
        // TextView для назви/опису програми
        tvProgramTitle = findViewById(R.id.tvProgramTitle);
        tvProgramDescription = findViewById(R.id.tvProgramDescription);

        // Встановлюємо назву програми
        tvProgramTitle.setText(programName);

        // Обрізаємо довгий опис і додаємо три крапки (…)
        if (programDescription.length() > 50) {
            tvProgramDescription.setText(programDescription.substring(0, 50) + "...");
        } else {
            tvProgramDescription.setText(programDescription);
        }

        // При натисканні на обрізаний опис показуємо AlertDialog з повним текстом
        tvProgramDescription.setOnClickListener(v -> {
            new AlertDialog.Builder(this, R.style.RoundedDialogTheme)
                    .setTitle(programName)
                    .setMessage(programDescription)
                    .setPositiveButton("OK", null)
                    .show();
        });

        // Кнопка додавання нового дня
        buttonAddDay = findViewById(R.id.buttonAddDay);
        buttonAddDay.setOnClickListener(v -> createGymSession());
    }

    /**
     * Налаштовуємо RecyclerView для списку GymDay
     */
    private void setupRecyclerView() {
        recyclerViewDays = findViewById(R.id.recyclerViewDays);
        recyclerViewDays.setLayoutManager(new LinearLayoutManager(this));

        gymDays = new ArrayList<>();
        gymSessionAdapter = new BasePlanAdapter<>(gymDays, new PlanItemClickListener());
        recyclerViewDays.setAdapter(gymSessionAdapter);
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
                gymSessionAdapter.moveItem(fromPosition, toPosition);

                // За бажанням зберігаємо новий порядок у базі
                planManagerDAO.updateGymDaysPositions(gymSessionAdapter.getItems());


                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Swipe ігнорується, бо напрямок = 0
            }
        };
        new ItemTouchHelper(callback).attachToRecyclerView(recyclerViewDays);
    }



    /**
     * Завантажуємо список GymDay з бази даних і оновлюємо адаптер
     */
    private void loadGymSessions() {
        gymDays.clear();
        gymDays.addAll(planManagerDAO.getGymDaysByPlanId(planId));
        gymSessionAdapter.notifyDataSetChanged();
    }

    /**
     * Відкриваємо діалог для створення нового тренувального дня (GymDay)
     */
    private void createGymSession() {
        DialogCreateEditNameDesc dialog = new DialogCreateEditNameDesc(
                this,
                getString(R.string.create_gym_session),
                "",
                "",
                (dayName, description) -> {
                    long gymDayId = planManagerDAO.addGymDay(planId, dayName, description);
                    if (gymDayId != -1) {
                        Toast.makeText(this, "Новий день тренувань додано", Toast.LENGTH_SHORT).show();
                    }
                    loadGymSessions();
                }
        );
        dialog.show();
    }

    /**
     * Внутрішній клас-слухач подій на кожному GymDay:
     *  - редагувати
     *  - видалити
     *  - натиснути (перехід до TrainingBlocks)
     */
    private class PlanItemClickListener implements BasePlanAdapter.OnPlanItemClickListener<GymDay> {

        @Override
        public void onEditClick(GymDay gymDay) {
            // Діалог редагування назви та опису
            DialogCreateEditNameDesc editDialog = new DialogCreateEditNameDesc(
                    GymSessionsActivity.this,
                    gymDay.getName(),
                    gymDay.getDescription(),
                    (newName, newDescription) -> {
                        gymDay.setName(newName);
                        gymDay.setDescription(newDescription);
                        Log.d("find_bag_gymSession","1");
                        planManagerDAO.updateGymSession(gymDay);
                        gymSessionAdapter.notifyDataSetChanged();
                    }
            );
            editDialog.show();
        }

        @Override
        public void onCloneClick(GymDay item) {

            loadGymSessions();

            // Клонування елемента
            GymDay copiedItem = new GymDay(
                    (int) item.getId(), // ID буде згенеровано базою даних
                    item.getPlanId(),
                    item.getName() + " (Копія)",
                    item.getDescription(),
                    new ArrayList<>(item.getTrainingBlocks())
            );

            // Додаємо клонований елемент до бази даних
            GymDay clonedItem = planManagerDAO.onStartCloneGymSession(copiedItem);
            planManagerDAO.getAllPlans();
            if (clonedItem != null) {
                gymDays.add(clonedItem);
                loadGymSessions();
                Toast.makeText(GymSessionsActivity.this, "План клоновано!", Toast.LENGTH_SHORT).show();
            } else Toast.makeText(GymSessionsActivity.this, "Помилка при клонуванні плану", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDeleteClick(GymDay gymDay) {
            // Підтвердження перед видаленням
            ConfirmDeleteDialog.OnDeleteConfirmedListener onDeleteConfirmedListener = () -> {
                planManagerDAO.deleteGymSession(gymDay.getId());
                loadGymSessions();
                Toast.makeText(GymSessionsActivity.this,
                        getString(R.string.deleted_day),
                        Toast.LENGTH_SHORT).show();
            };

            ConfirmDeleteDialog.show(
                    GymSessionsActivity.this,
                    gymDay.getName(),
                    onDeleteConfirmedListener
            );
        }

        @Override
        public void onItemClick(GymDay gymDay) {
            // Переходимо до списку блоків (TrainingBlocksActivity)
            Intent intent = new Intent(GymSessionsActivity.this, TrainingBlocksActivity.class);
            intent.putExtra("gym_day_id", gymDay.getId());
            intent.putExtra("gym_day_name", gymDay.getName());
            intent.putExtra("gym_day_description", gymDay.getDescription());
            startActivity(intent);
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        loadGymSessions(); // метод, який заново бере дні з бази
    }

}
