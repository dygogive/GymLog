package com.example.gymlog.ui.plan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymlog.R;
import com.example.gymlog.data.db.PlanManagerDAO;
import com.example.gymlog.data.plan.PlanCycle;

import java.util.ArrayList;
import java.util.List;

public class PlanManagementActivity extends AppCompatActivity {
    private PlanManagerDAO planManagerDAO;
    private RecyclerView recyclerView;
    private Button addPlanButton;
    private PlanAdapter planAdapter;
    private List<PlanCycle> planCycles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_management);

        initializeUI(); // Ініціалізація елементів інтерфейсу
        planManagerDAO = new PlanManagerDAO(this); // Ініціалізація об'єкта для роботи з базою даних
        planCycles = new ArrayList<>(); // Створення списку планів
        setupRecyclerView(); // Налаштування списку планів
        loadPlanCycles(); // Завантаження списку планів з бази даних
    }

    // Метод для ініціалізації UI елементів
    private void initializeUI() {
        recyclerView = findViewById(R.id.recyclerViewPlans);
        addPlanButton = findViewById(R.id.buttonAddPlan);
        addPlanButton.setOnClickListener(v -> addNewPlan());
    }

    // Метод для налаштування RecyclerView (списку планів)
    private void setupRecyclerView() {
        planAdapter = new PlanAdapter(planCycles, new PlanAdapter.OnPlanCycleClickListener() {
            @Override
            public void onEditClick(PlanCycle planCycle) {
                openEditPlanActivity(planCycle); // Відкриття екрану редагування
            }

            @Override
            public void onDeleteClick(PlanCycle planCycle) {
                deletePlan(planCycle); // Видалення плану
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(planAdapter);
    }

    // Метод для завантаження планів з бази даних
    private void loadPlanCycles() {
        planCycles.clear(); // Очистимо список перед оновленням
        planCycles.addAll(planManagerDAO.getAllPlans()); // Додаємо плани з бази даних
        planAdapter.notifyDataSetChanged(); // Оновлюємо відображення
    }

    // Метод для додавання нового плану
    private void addNewPlan() {
        PlanCycle newPlan = new PlanCycle(0, getString(R.string.new_plan), getString(R.string.new_program), new ArrayList<>());
        long newPlanId = planManagerDAO.addPlan(newPlan);

        if (newPlanId != -1) {
            newPlan = new PlanCycle(newPlanId, newPlan.getName(), newPlan.getDescription(), new ArrayList<>());
            planCycles.add(newPlan);
            planAdapter.notifyDataSetChanged();
            Toast.makeText(this, "План додано!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Помилка при додаванні плану", Toast.LENGTH_SHORT).show();
        }
    }

    // Метод для відкриття екрану редагування плану
    private void openEditPlanActivity(PlanCycle planCycle) {
        Intent intent = new Intent(this, PlanEditActivity.class);
        intent.putExtra("plan_id", planCycle.getId());
        startActivity(intent);
    }

    // Метод для видалення плану
    private void deletePlan(PlanCycle planCycle) {
        planManagerDAO.deletePlan(planCycle.getId()); // Видаляємо план з бази даних
        planCycles.remove(planCycle); // Видаляємо з локального списку
        planAdapter.notifyDataSetChanged(); // Оновлюємо UI
        Toast.makeText(this, "План видалено: " + planCycle.getName(), Toast.LENGTH_SHORT).show();
    }
}
