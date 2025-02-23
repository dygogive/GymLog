package com.example.gymlog.ui.plan;

import android.os.Bundle;

import com.example.gymlog.data.db.PlanManagerDAO;
import com.example.gymlog.data.plan.PlanCycle;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymlog.R;

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

        //знайти екранні елементи
        recyclerView = findViewById(R.id.recyclerViewPlans);
        addPlanButton = findViewById(R.id.buttonAddPlan);

        //ініціалізація ДАО для доступу до бази
        planManagerDAO = new PlanManagerDAO(this);

        //список планів
        planCycles = new ArrayList<>();

        //ініціалізація адаптера з слухачем у ньому
        planAdapter = new PlanAdapter(planCycles, new PlanAdapter.OnPlanCycleClickListener() {
            @Override
            public void onEditClick(PlanCycle planCycle) {
                onEditPlan(planCycle);
            }

            @Override
            public void onDeleteClick(PlanCycle planCycle) {
                onDeletePlan(planCycle);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(planAdapter);

        addPlanButton.setOnClickListener(v -> {
            // Логіка для додавання нового плану

            PlanCycle newPlan = new PlanCycle(
                    0,
                    "New Plan",
                    "New training program",
                    new ArrayList<>()
            );

            long newPlanId = planManagerDAO.addPlan(newPlan); // Додаємо у базу

            if (newPlanId != -1) {
                newPlan = new PlanCycle(newPlanId, newPlan.getName(), newPlan.getDescription(), new ArrayList<>());
                planCycles.add(newPlan); // Додаємо у список
                planAdapter.notifyDataSetChanged(); // Оновлюємо UI
                Toast.makeText(this, "Plan added!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error adding plan", Toast.LENGTH_SHORT).show();
            }

        });

        // Завантаження даних
        loadPlanCycles();
    }

    //оновити список планів і оновити екран
    private void loadPlanCycles() {
        planCycles.clear(); // Очистимо список перед завантаженням з бази
        planCycles.addAll(planManagerDAO.getAllPlans()); // Завантажуємо реальні дані
        planAdapter.notifyDataSetChanged(); // Оновлюємо UI
    }

    private void onEditPlan(PlanCycle planCycle) {
        // Логіка для редагування плану
        Toast.makeText(this, "Edit Plan: " + planCycle.getName(), Toast.LENGTH_SHORT).show();
    }

    private void onDeletePlan(PlanCycle planCycle) {
        // Логіка для видалення плану
        planManagerDAO.deletePlan(planCycle.getId()); // Видаляємо з бази
        planCycles.remove(planCycle); // Видаляємо зі списку
        planAdapter.notifyDataSetChanged(); // Оновлюємо UI
        Toast.makeText(this, "Deleted Plan: " + planCycle.getName(), Toast.LENGTH_SHORT).show();
    }
}