package com.example.gymlog.ui.plan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymlog.R;
import com.example.gymlog.data.db.PlanManagerDAO;
import com.example.gymlog.data.plan.GymDay;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

// Активність для редагування тренувальних днів плану
public class PlanEditActivity extends AppCompatActivity {

    private RecyclerView recyclerViewDays;
    private GymDayAdapter gymDayAdapter;
    private List<GymDay> gymDays;
    private PlanManagerDAO planManagerDAO;
    private long planId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_edit);

        planManagerDAO = new PlanManagerDAO(this);
        planId = getIntent().getLongExtra("plan_id", -1);

        recyclerViewDays = findViewById(R.id.recyclerViewDays);
        FloatingActionButton buttonAddDay = findViewById(R.id.buttonAddDay);

        recyclerViewDays.setLayoutManager(new LinearLayoutManager(this));
        gymDays = new ArrayList<>();
        gymDayAdapter = new GymDayAdapter(gymDays, new GymDayAdapter.OnGymDayClickListener() {
            @Override
            public void onDayClick(GymDay gymDay) {
                Intent intent = new Intent(PlanEditActivity.this, TrainingBlockEditActivity.class);
                intent.putExtra("gym_day_id", Long.valueOf(gymDay.getId()));
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

        buttonAddDay.setOnClickListener(v -> openDayCreationDialog());

        if (planId != -1) {
            loadPlanData();
        }
    }

    // Завантаження даних плану для редагування
    private void loadPlanData() {
        gymDays.clear();
        gymDays.addAll(planManagerDAO.getGymDaysByPlanId(planId));
        gymDayAdapter.notifyDataSetChanged();
    }

    // Відкриваємо діалог для додавання тренувального дня
    private void openDayCreationDialog() {
        GymDayDialog dialog = new GymDayDialog(this, planId, () -> {
            loadPlanData(); // оновлюємо список після додавання
        });
        dialog.show();
    }
}
