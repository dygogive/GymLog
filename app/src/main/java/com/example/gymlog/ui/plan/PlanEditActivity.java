package com.example.gymlog.ui.plan;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymlog.R;
import com.example.gymlog.data.db.PlanManagerDAO;
import com.example.gymlog.data.plan.GymDay;
import com.example.gymlog.data.plan.PlanCycle;

import java.util.ArrayList;
import java.util.List;

// Активність для редагування плану тренувань
public class PlanEditActivity extends AppCompatActivity {

    private EditText editTextPlanName, editTextPlanDescription;
    private Button buttonSavePlan, buttonAddDay;
    private RecyclerView recyclerViewDays;
    private GymDayAdapter gymDayAdapter;
    private List<GymDay> gymDays;
    private PlanManagerDAO planManagerDAO;
    private long planId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_edit);

        // Ініціалізація об'єкта для роботи з базою даних
        planManagerDAO = new PlanManagerDAO(this);

        // Ініціалізація елементів інтерфейсу
        editTextPlanName = findViewById(R.id.editTextPlanName);
        editTextPlanDescription = findViewById(R.id.editTextPlanDescription);
        buttonSavePlan = findViewById(R.id.buttonSavePlan);
        buttonAddDay = findViewById(R.id.buttonAddDay);
        recyclerViewDays = findViewById(R.id.recyclerViewDays);

        // Налаштування RecyclerView для списку тренувальних днів
        recyclerViewDays.setLayoutManager(new LinearLayoutManager(this));
        gymDays = new ArrayList<>();
        gymDayAdapter = new GymDayAdapter(gymDays, new GymDayAdapter.OnGymDayClickListener() {
            @Override
            public void onDayClick(GymDay gymDay) {
                // Відкриваємо редактор тренувального блоку
                Intent intent = new Intent(PlanEditActivity.this, TrainingBlockEditActivity.class);
                intent.putExtra("gym_day_id", Long.valueOf(gymDay.getId()));// Переконайся, що передаєш як long
                startActivity(intent);
            }

            @Override
            public void onDeleteDayClick(GymDay gymDay) {
                gymDays.remove(gymDay);
                gymDayAdapter.notifyDataSetChanged();
                Toast.makeText(PlanEditActivity.this, "День видалено", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAddTrainingBlockClick(GymDay gymDay) {
                Toast.makeText(PlanEditActivity.this, "Кнопка +", Toast.LENGTH_SHORT).show();
            }
        });
        recyclerViewDays.setAdapter(gymDayAdapter);

        // Отримуємо переданий planId
        planId = getIntent().getLongExtra("plan_id", -1);
        if (planId != -1) {
            loadPlanData();
        }

        // Збереження плану
        buttonSavePlan.setOnClickListener(v -> savePlan());

        // Додавання нового тренувального дня
        buttonAddDay.setOnClickListener(v -> addNewDay());
    }

    // Завантаження даних плану для редагування
    private void loadPlanData() {
        PlanCycle planCycle = planManagerDAO.getPlanById(planId);
        if (planCycle != null) {
            editTextPlanName.setText(planCycle.getName());
            editTextPlanDescription.setText(planCycle.getDescription());
            gymDays.addAll(planCycle.getGymDays());
            gymDayAdapter.notifyDataSetChanged();
        }
    }

    // Метод для збереження змін у плані
    private void savePlan() {
        String name = editTextPlanName.getText().toString();
        String description = editTextPlanDescription.getText().toString();

        // Оновлюємо сам план
        PlanCycle updatedPlan = new PlanCycle(planId, name, description, gymDays);
        planManagerDAO.updatePlan(updatedPlan);

        // Оновлюємо список днів у базі
        planManagerDAO.deleteGymDaysByPlanId(planId);
        planManagerDAO.addGymDays(planId, gymDays);

        Toast.makeText(this, "План оновлено!", Toast.LENGTH_SHORT).show();
        finish();
    }


    // Метод для додавання нового тренувального дня
    private void addNewDay() {
        int dayOrder = gymDays.size() + 1;
        String description = "Опис дня " + dayOrder;

        GymDay newDay = new GymDay(-1, (int) planId, new ArrayList<>());
        newDay.setDescription(description); // якщо є сеттер опису
        gymDays.add(newDay);
        gymDayAdapter.notifyDataSetChanged();

        Toast.makeText(this, "Новий тренувальний день додано", Toast.LENGTH_SHORT).show();
    }


}
