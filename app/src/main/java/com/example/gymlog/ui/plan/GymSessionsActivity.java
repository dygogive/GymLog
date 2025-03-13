package com.example.gymlog.ui.plan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymlog.R;
import com.example.gymlog.data.db.PlanManagerDAO;
import com.example.gymlog.data.plan.GymSession;
import com.example.gymlog.ui.dialogs.ConfirmDeleteDialog;
import com.example.gymlog.ui.dialogs.DialogCreateEditNameDesc;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

// Активність для редагування тренувальних днів плану
public class GymSessionsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewDays;
    private BasePlanAdapter<GymSession> gymSessionAdapter;
    private List<GymSession> gymSessions;
    private PlanManagerDAO planManagerDAO;
    private long planId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gym_sessions);

        planManagerDAO = new PlanManagerDAO(this);
        planId = getIntent().getLongExtra("plan_id", -1);

        recyclerViewDays = findViewById(R.id.recyclerViewDays);
        FloatingActionButton buttonAddDay = findViewById(R.id.buttonAddDay);

        recyclerViewDays.setLayoutManager(new LinearLayoutManager(this));
        gymSessions = new ArrayList<>();
        gymSessionAdapter = new BasePlanAdapter<>(
                gymSessions,
                new BasePlanAdapter.OnPlanItemClickListener<>() {
                    @Override
                public void onItemClick(GymSession gymSession) {
                    Intent intent = new Intent(GymSessionsActivity.this, TrainingBlocksActivity.class);
                    intent.putExtra("gym_day_id", Long.valueOf(gymSession.getId()));
                    startActivity(intent);
                }

                @Override
                public void onDeleteClick(GymSession gymSession) {

                    ConfirmDeleteDialog.OnDeleteConfirmedListener onDeleteConfirmedListener = () -> {
                        planManagerDAO.deleteGymSession(gymSession.getId());
                        updateGymSessionsInRecycler();
                        Toast.makeText(GymSessionsActivity.this, GymSessionsActivity.this.getString(R.string.deleted_day), Toast.LENGTH_SHORT).show();
                    };


                    ConfirmDeleteDialog.show(
                            GymSessionsActivity.this,
                            gymSession.getName(),
                            onDeleteConfirmedListener
                    );

                }

                @Override
                public void onEditClick(GymSession gymSession) {
                    DialogCreateEditNameDesc editDialog = new DialogCreateEditNameDesc(
                            GymSessionsActivity.this,
                            gymSession.getName(),
                            gymSession.getDescription(),
                            (newName, newDescription) -> {
                                // Оновлення даних після редагування
                                gymSession.setName(newName);
                                gymSession.setDescription(newDescription);
                                planManagerDAO.updateGymSession(gymSession);
                                gymSessionAdapter.notifyDataSetChanged();
                            }
                    );
                    editDialog.show();
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
