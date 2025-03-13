package com.example.gymlog.ui.plan;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymlog.R;
import com.example.gymlog.data.db.PlanManagerDAO;
import com.example.gymlog.data.plan.FitnessProgram;
import com.example.gymlog.ui.dialogs.ConfirmDeleteDialog;
import com.example.gymlog.ui.dialogs.DialogCreateEditNameDesc;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class FitnessProgramsActivity extends AppCompatActivity {
    private PlanManagerDAO planManagerDAO;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private BasePlanAdapter<FitnessProgram> fitnessProgramAdapter;
    private List<FitnessProgram> fitnessPrograms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_fitness_programs);



        initializeUI(); // Ініціалізація елементів інтерфейсу
        planManagerDAO = new PlanManagerDAO(this); // Ініціалізація об'єкта для роботи з базою даних
        fitnessPrograms = new ArrayList<>(); // Створення списку планів
        setupRecyclerView(); // Налаштування списку планів
        loadPlanCycles(); // Завантаження списку планів з бази даних
    }

    // Метод для ініціалізації UI елементів
    private void initializeUI() {
        recyclerView = findViewById(R.id.recyclerViewPlans);
        floatingActionButton = findViewById(R.id.fabAddPlan);
        floatingActionButton.setOnClickListener(v -> addNewPlan());
    }

    // Метод для налаштування RecyclerView (списку планів)
    private void setupRecyclerView() {
        fitnessProgramAdapter = new BasePlanAdapter<>(
                fitnessPrograms,
                new BasePlanAdapter.OnPlanItemClickListener<>() {
                    @Override
                    public void onEditClick(FitnessProgram fitnessProgram) {
                        DialogCreateEditNameDesc editDialog = new DialogCreateEditNameDesc(
                                FitnessProgramsActivity.this,
                                fitnessProgram.getName(),
                                fitnessProgram.getDescription(),
                                (newName, newDescription) -> {
                                    // Оновлення даних після редагування
                                    fitnessProgram.setName(newName);
                                    fitnessProgram.setDescription(newDescription);
                                    planManagerDAO.updatePlan(fitnessProgram);
                                    fitnessProgramAdapter.notifyDataSetChanged();
                                }
                        );
                        editDialog.show();
                    }

                    @Override
                    public void onDeleteClick(FitnessProgram fitnessProgram) {
                        ConfirmDeleteDialog.OnDeleteConfirmedListener onDeleteConfirmedListener = () -> {
                    deletePlan(fitnessProgram); // Видалення плану
                };
                        ConfirmDeleteDialog.show(
                        FitnessProgramsActivity.this,
                        fitnessProgram.getName(),
                        onDeleteConfirmedListener
                        );
                    }

                    @Override
                    public void onItemClick(FitnessProgram fitnessProgram) {
                        openEditPlanActivity(fitnessProgram); // Відкриття екрану редагування
                    }
                });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(fitnessProgramAdapter);
    }

    // Метод для завантаження планів з бази даних
    private void loadPlanCycles() {
        fitnessPrograms.clear(); // Очистимо список перед оновленням
        fitnessPrograms.addAll(planManagerDAO.getAllPlans()); // Додаємо плани з бази даних
        fitnessProgramAdapter.notifyDataSetChanged(); // Оновлюємо відображення
    }

    // Метод для додавання нового плану
    private void addNewPlan() {
        FitnessProgram newFitnessProgram = new FitnessProgram(0, "", "", new ArrayList<>());
        FitnessProgram finalNewFitnessProgram = newFitnessProgram;

        @SuppressLint("NotifyDataSetChanged")
        DialogCreateEditNameDesc editDialog = new DialogCreateEditNameDesc(
                FitnessProgramsActivity.this, FitnessProgramsActivity.this.getString(R.string.new_program),
                newFitnessProgram.getName(),
                newFitnessProgram.getDescription(),
                (newName, newDescription) -> {
                    // Оновлення даних після редагування
                    finalNewFitnessProgram.setName(newName);
                    finalNewFitnessProgram.setDescription(newDescription);
                    long newPlanId = planManagerDAO.addFitProgram(finalNewFitnessProgram);

                    if (newPlanId != -1) {
                        FitnessProgram newFitnessProgram1 = new FitnessProgram(newPlanId, finalNewFitnessProgram.getName(), finalNewFitnessProgram.getDescription(), new ArrayList<>());
                        fitnessPrograms.add(newFitnessProgram1);
                        fitnessProgramAdapter.notifyDataSetChanged();
                        Toast.makeText(this, "План додано!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Помилка при додаванні плану", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        editDialog.show();



    }

    // Метод для відкриття екрану редагування плану
    private void openEditPlanActivity(FitnessProgram fitnessProgram) {
        Intent intent = new Intent(this, GymSessionsActivity.class);
        intent.putExtra("plan_id", fitnessProgram.getId());
        startActivity(intent);
    }

    // Метод для видалення плану
    private void deletePlan(FitnessProgram fitnessProgram) {
        planManagerDAO.deletePlan(fitnessProgram.getId()); // Видаляємо план з бази даних
        fitnessPrograms.remove(fitnessProgram); // Видаляємо з локального списку
        fitnessProgramAdapter.notifyDataSetChanged(); // Оновлюємо UI
        Toast.makeText(this, "План видалено: " + fitnessProgram.getName(), Toast.LENGTH_SHORT).show();
    }
}
