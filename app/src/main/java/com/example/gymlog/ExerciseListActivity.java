package com.example.gymlog;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExerciseListActivity extends AppCompatActivity {

    //створити посилання RecyclerView
    private RecyclerView recyclerView = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_exercise_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //знайти RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewExercises);
        //назначити Layaut Manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //список вправ
        List<Exercise> exerciseList = Arrays.asList(
                new Exercise("Жим штанги лежачи",
                        Exercise.Motion.PRESS,
                        Arrays.asList(Exercise.MuscleGroup.CHEST_FULL,Exercise.MuscleGroup.TRICEPS),
                        Exercise.Equipment.BARBELL),
                new Exercise("Підтягування",
                        Exercise.Motion.PULL,
                        Arrays.asList(Exercise.MuscleGroup.LATS,Exercise.MuscleGroup.BICEPS_ARMS),
                        Exercise.Equipment.BODY_WEIGHT),
                new Exercise("Присідання з штангою",
                        Exercise.Motion.PRESS,
                        Arrays.asList(Exercise.MuscleGroup.QUADRICEPS,Exercise.MuscleGroup.GLUTES),
                        Exercise.Equipment.BODY_WEIGHT)
        );


//        recyclerView.setAdapter(ExerciseAdapter(exercises));

    }



}