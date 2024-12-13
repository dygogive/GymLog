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
import com.example.gymlog.ui.workout.WorkoutActivity;

public class MainActivity extends AppCompatActivity {

    private Button
            buttonStartGym,
            buttonHistory,
            buttonExercises,
            exercicesNew;

    @SuppressLint("MissingInflatedId")
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

        //find buttons
        buttonStartGym   = (Button) findViewById(R.id.buttonStartGym  );
        buttonHistory    = (Button) findViewById(R.id.buttonHistory   );
        buttonExercises  = (Button) findViewById(R.id.buttonExercises );
        exercicesNew     = (Button) findViewById(R.id.exercicesNew   );

        //button listeners
        buttonStartGym.setOnClickListener(v -> {
            Log.d("LogTag","test buttonStartGym");
            Intent intent = new Intent(MainActivity.this, WorkoutActivity.class);
            startActivity(intent);
        });
        buttonHistory.setOnClickListener(v -> {
            Log.d("LogTag","test buttonHistory");
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        });
        buttonExercises.setOnClickListener(v -> {
            Log.d("LogTag","test buttonExercises");
            Intent intent = new Intent(MainActivity.this, MuscleGroupActivity.class);
            startActivity(intent);
        });

        exercicesNew.setOnClickListener(v -> {
            Log.d("LogTag","test buttonExercises");
            Intent intent = new Intent(MainActivity.this, ExerciseManagementActivity.class);
            startActivity(intent);
        });



    }



}