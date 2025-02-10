package com.example.gymlog.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gymlog.ui.exercise2.activities.ExerciseManagementActivity;
import com.example.gymlog.R;
import com.example.gymlog.ui.exercise1.MuscleGroupActivity;
import com.example.gymlog.ui.history.HistoryActivity;
import com.example.gymlog.ui.programs.PlanManagementActivity;
import com.example.gymlog.ui.workout.WorkoutActivity;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Плитки
        findViewById(R.id.cardStartGym).setOnClickListener(v -> {
            Log.d("LogTag", "Start Gym clicked");
            startActivity(new Intent(MainActivity.this, WorkoutActivity.class));
        });

        findViewById(R.id.cardHistory).setOnClickListener(v -> {
            Log.d("LogTag", "History clicked");
            startActivity(new Intent(MainActivity.this, HistoryActivity.class));
        });

        findViewById(R.id.cardExercises).setOnClickListener(v -> {
            Log.d("LogTag", "Exercises clicked");
            startActivity(new Intent(MainActivity.this, MuscleGroupActivity.class));
        });

        findViewById(R.id.cardNewExercises).setOnClickListener(v -> {
            Log.d("LogTag", "New Exercises clicked");
            startActivity(new Intent(MainActivity.this, ExerciseManagementActivity.class));
        });

        findViewById(R.id.cardPrograms).setOnClickListener(v -> {
            Log.d("LogTag", "New Exercises clicked");
            startActivity(new Intent(MainActivity.this, PlanManagementActivity.class));
        });



    }
}