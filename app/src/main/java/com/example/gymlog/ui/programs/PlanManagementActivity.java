package com.example.gymlog.ui.programs;

import android.content.Intent;
import android.os.Bundle;

import com.example.gymlog.data.plan.PlanCycle;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymlog.databinding.ActivityPlanManagementBinding;

import com.example.gymlog.R;

import java.util.ArrayList;
import java.util.List;

public class PlanManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button addPlanButton;
    private PlanAdapter planAdapter;
    private List<PlanCycle> planCycles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_management);

        recyclerView = findViewById(R.id.recyclerViewPlans);
        addPlanButton = findViewById(R.id.buttonAddPlan);

        planCycles = new ArrayList<>();

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
            Toast.makeText(this, "Add Plan clicked", Toast.LENGTH_SHORT).show();
            // startActivity(new Intent(this, PlanCreationActivity.class));
        });

        // Завантаження даних
        loadPlanCycles();
    }

    private void loadPlanCycles() {
        // Placeholder для отримання даних з бази даних
        planCycles.add(new PlanCycle(1, "Strength Training", "A plan focused on strength gains", new ArrayList<>()));
        planCycles.add(new PlanCycle(2, "Endurance Training", "Build stamina and endurance", new ArrayList<>()));
        planAdapter.notifyDataSetChanged();
    }

    private void onEditPlan(PlanCycle planCycle) {
        // Логіка для редагування плану
        Toast.makeText(this, "Edit Plan: " + planCycle.getName(), Toast.LENGTH_SHORT).show();
    }

    private void onDeletePlan(PlanCycle planCycle) {
        // Логіка для видалення плану
        planCycles.remove(planCycle);
        planAdapter.notifyDataSetChanged();
        Toast.makeText(this, "Deleted Plan: " + planCycle.getName(), Toast.LENGTH_SHORT).show();
    }
}