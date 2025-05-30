package com.example.gymlog.ui.legacy.program;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymlog.R;
import com.example.gymlog.data.local.legacy.PlanManagerDAO;
import com.example.gymlog.domain.model.legacy.plan.FitnessProgram;
import com.example.gymlog.ui.legacy.dialogs.ConfirmDeleteDialog;
import com.example.gymlog.ui.legacy.dialogs.DialogCreateEditNameDesc;
import com.example.gymlog.ui.legacy.program.adapters.BasePlanAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Активність для відображення і редагування (створення, редагування, видалення) списку
 * програм тренувань (FitnessProgram).
 */
public class FitnessProgramsActivity extends AppCompatActivity {

    // DAO для роботи з базою
    private PlanManagerDAO planManagerDAO;






    // RecyclerView + адаптер
    private RecyclerView recyclerView;
    private BasePlanAdapter<FitnessProgram> fitnessProgramAdapter;
    private List<FitnessProgram> fitnessPrograms = new ArrayList<>();;











    // FloatingActionButton для додавання нової програми
    private FloatingActionButton floatingActionButton;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Увімкнути розширене відображення (edge-to-edge)
        EdgeToEdge.enable(this);

        // Встановити лейаут
        setContentView(R.layout.activity_fitness_programs);

        // Ініціалізація UI
        initializeUI();



        // Створюємо список програм і налаштовуємо адаптер
        setupRecyclerView();



        // Створюємо ItemTouchHelper для drag & drop
        setupDragAndDrop();

        // Завантажуємо програми з бази
        loadPlanCycles();

        planManagerDAO.logAllData();

    }

    /**
     * Завантажуємо всі програми (FitnessProgram) із бази даних
     *
     */
    private void loadPlanCycles() {
        fitnessPrograms.clear();
        fitnessPrograms.addAll(planManagerDAO.getAllPlans());
        fitnessProgramAdapter.notifyDataSetChanged();
    }

    /**
     * Ініціалізація UI елементів (RecyclerView, FloatingActionButton) та обробників натискання
     */
    private void initializeUI() {
        // init  DAO
        planManagerDAO = new PlanManagerDAO(this);

        //init UI
        recyclerView = findViewById(R.id.recyclerViewPlans);
        floatingActionButton = findViewById(R.id.fabAddPlan);

        // setOnClickListener fot fab
        floatingActionButton.setOnClickListener(v -> addNewPlanByFAB());
    }

    /**
     * Налаштовуємо RecyclerView:
     * - створюємо адаптер BasePlanAdapter
     * - передаємо слухач подій (редагувати, видалити, натиснути)
     */
    private void setupRecyclerView() {
        // Створюємо адаптер із  слухачем OnItemsRecyclerListener()
        fitnessProgramAdapter = new BasePlanAdapter<>(fitnessPrograms, new OnItemsRecyclerListener());
        // Прив'язуємо адаптер до RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(fitnessProgramAdapter);
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
                fitnessProgramAdapter.moveItem(fromPosition, toPosition);

                // За бажанням зберігаємо новий порядок у базі
                planManagerDAO.updatePlansPositions(fitnessProgramAdapter.getItems());

                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Swipe ігнорується, бо напрямок = 0
            }
        };
        new ItemTouchHelper(callback).attachToRecyclerView(recyclerView);
    }





    /**
     * Створення нової програми тренувань через діалог
     */
    @SuppressLint("NotifyDataSetChanged")
    private void addNewPlanByFAB() {
        // Створюємо тимчасовий об'єкт (поки без назви/опису)
        FitnessProgram newFitnessProgram = new FitnessProgram(
                0,
                "",
                "",
                new ArrayList<>(),
                UUID.randomUUID().toString());


        DialogCreateEditNameDesc editDialog = new DialogCreateEditNameDesc(
                this,
                getString(R.string.new_program),
                newFitnessProgram.getName(),
                newFitnessProgram.getDescription(),
                //send anonim class to dialog for apply new name and description to object of new program
                (newName, newDescription) -> {
                    // Оновлюємо тимчасовий об'єкт і записуємо в базу

                    // set new name and description
                    newFitnessProgram.setName(newName);
                    newFitnessProgram.setDescription(newDescription);

                    //send new program to SQLite database
                    long newPlanId = planManagerDAO.addFitProgram(newFitnessProgram);
                    if (newPlanId != -1) {
                        // Якщо успішно додано, створюємо об'єкт із реальним id
                        newFitnessProgram.setId(newPlanId);
                        //add plat to Recycler Adapter
                        fitnessPrograms.add(newFitnessProgram);
                        fitnessProgramAdapter.notifyDataSetChanged();
                        Toast.makeText(this, "План додано!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Помилка при додаванні плану", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        editDialog.show();
    }

    /**
     * Перехід до екрану GymSessionsActivity,
     * де відображаються дні тренувань обраної програми
     */
    private void openEditPlanActivity(FitnessProgram fitnessProgram) {
        Intent intent = new Intent(this, GymSessionsActivity.class);
        // Передаємо id та назву/опис програми на GymSessionsActivity
        intent.putExtra("plan_id", fitnessProgram.getId());
        intent.putExtra("program_name", fitnessProgram.getName());
        intent.putExtra("program_description", fitnessProgram.getDescription());
        startActivity(intent);
    }

    /**
     * Видаляємо план із бази, видаляємо з локального списку і оновлюємо UI
     */
    private void deletePlan(FitnessProgram fitnessProgram) {
        planManagerDAO.deletePlan(fitnessProgram.getId());
        fitnessPrograms.remove(fitnessProgram);
        fitnessProgramAdapter.notifyDataSetChanged();
        Toast.makeText(this,
                "План видалено: " + fitnessProgram.getName(),
                Toast.LENGTH_SHORT).show();
    }


    /**
     * Слухач натискань на елементи RecyclerView
     */
    private class OnItemsRecyclerListener implements BasePlanAdapter.OnPlanItemClickListener<FitnessProgram> {
        @Override
        public void onEditClick(FitnessProgram fitnessProgram) {
            // Діалог редагування назви та опису
            DialogCreateEditNameDesc editDialog = new DialogCreateEditNameDesc(
                    FitnessProgramsActivity.this,
                    fitnessProgram.getName(),
                    fitnessProgram.getDescription(),
                    (newName, newDescription) -> {
                        // Оновлюємо план і зберігаємо в базу
                        fitnessProgram.setName(newName);
                        fitnessProgram.setDescription(newDescription);
                        planManagerDAO.updatePlan(fitnessProgram);
                        fitnessProgramAdapter.notifyDataSetChanged();
                    }
            );
            editDialog.show();
        }

        @Override
        public void onCloneClick(FitnessProgram fitnessProgram) {

            // Додаємо клонований елемент до бази даних
            FitnessProgram clonedProgram = planManagerDAO.onStartCloneFitProgram(fitnessProgram);
            if (clonedProgram != null) {
                fitnessPrograms.add(clonedProgram);
                fitnessProgramAdapter.notifyDataSetChanged();
                Toast.makeText(FitnessProgramsActivity.this, "План клоновано!", Toast.LENGTH_SHORT).show();
            } else Toast.makeText(FitnessProgramsActivity.this, "Помилка при клонуванні плану", Toast.LENGTH_SHORT).show();

        }



        @Override
        public void onDeleteClick(FitnessProgram fitnessProgram) {
            // Діалог підтвердження перед видаленням
            ConfirmDeleteDialog.OnDeleteConfirmedListener onDeleteConfirmedListener = () ->
                    deletePlan(fitnessProgram);

            ConfirmDeleteDialog.show(
                    FitnessProgramsActivity.this,
                    fitnessProgram.getName(),
                    onDeleteConfirmedListener
            );
        }

        @Override
        public void onItemClick(FitnessProgram fitnessProgram) {
            // Відкриваємо екран із списком днів тренувань
            openEditPlanActivity(fitnessProgram);
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        loadPlanCycles(); // метод, який заново бере дні з бази
    }
}
