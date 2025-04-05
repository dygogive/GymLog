package com.example.gymlog.ui;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gymlog.R;
import com.example.gymlog.database.AppDatabase;
import com.example.gymlog.database.ExerciseDAO;
import com.example.gymlog.database.ExerciseResultDao;
import com.example.gymlog.database.TestKt;
import com.example.gymlog.ui.exercises.activities.ExerciseManagementActivity;
import com.example.gymlog.ui.programs.FitnessProgramsActivity;

import java.util.concurrent.Executors;


public class OldMainActivity extends AppCompatActivity {

    private AppDatabase db;
    private ExerciseResultDao resultDao;


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



        findViewById(R.id.cardNewExercises).setOnClickListener(v -> {
            Log.d("LogTag", "New Exercises clicked");
            startActivity(new Intent(OldMainActivity.this, ExerciseManagementActivity.class));
        });

        findViewById(R.id.cardPrograms).setOnClickListener(v -> {
            Log.d("LogTag", "New Exercises clicked");
            startActivity(new Intent(OldMainActivity.this, FitnessProgramsActivity.class));
        });


        ExerciseDAO exerciseDAO = new ExerciseDAO(this);
        exerciseDAO.logAllExercises();



        // У MainActivity.java
        TestKt.testDatabaseJavaWrapper(
                this, // LifecycleOwner
                getApplicationContext(),
                () -> {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Операція завершена", Toast.LENGTH_SHORT).show();
                    });
                    return null;
                }
        );




    }



}