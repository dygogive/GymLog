package com.example.gymlog.ui.plan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymlog.R;
import com.example.gymlog.data.db.PlanManagerDAO;
import com.example.gymlog.data.plan.FitnessProgram;
import com.example.gymlog.data.plan.GymSession;
import com.example.gymlog.ui.dialogs.DialogCreateEditNameDesc;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

// Активність для редагування тренувальних днів плану
public class GymSessionsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewDays;
    private GymSessionAdapter gymSessionAdapter;
    private List<GymSession> gymSessions;
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
        gymSessions = new ArrayList<>();
        gymSessionAdapter = new GymSessionAdapter(gymSessions, new GymSessionAdapter.OnGymDayClickListener() {
            @Override
            public void onDayClick(GymSession gymSession) {
                Intent intent = new Intent(GymSessionsActivity.this, TrainingBlocksActivity.class);
                intent.putExtra("gym_day_id", Long.valueOf(gymSession.getId()));
                startActivity(intent);
            }

            @Override
            public void onDeleteDayClick(GymSession gymSession) {
                gymSessions.remove(gymSession);
                gymSessionAdapter.notifyDataSetChanged();
                Toast.makeText(GymSessionsActivity.this, "День видалено", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAddTrainingBlockClick(GymSession gymSession) {
                Toast.makeText(GymSessionsActivity.this, "Кнопка +", Toast.LENGTH_SHORT).show();
            }
        });
        recyclerViewDays.setAdapter(gymSessionAdapter);

        buttonAddDay.setOnClickListener(v -> createGymSession());

        if (planId != -1) {
            updateGymSessionsInRecycler();
        }
    }

    // Завантаження даних плану для редагування
    private void updateGymSessionsInRecycler() {
        gymSessions.clear();
        gymSessions.addAll(planManagerDAO.getGymDaysByPlanId(planId));
        gymSessionAdapter.notifyDataSetChanged();
    }

    // Відкриваємо діалог для додавання тренувального дня
    private void createGymSession() {
        DialogCreateEditNameDesc dialog = new DialogCreateEditNameDesc(
                    GymSessionsActivity.this,
                    GymSessionsActivity.this.getString(R.string.create_gym_session),
                    "",
                    "",
                    (dayName, description) -> {
                        long gymDayId = planManagerDAO.addGymDay(planId, dayName, description);

                        if (gymDayId != -1) {
                            Toast.makeText(GymSessionsActivity.this, "Новий день тренувань додано", Toast.LENGTH_SHORT).show();

                        }
                        GymSessionsActivity.this.updateGymSessionsInRecycler();
                    }
                );
        dialog.show();

    }
}
