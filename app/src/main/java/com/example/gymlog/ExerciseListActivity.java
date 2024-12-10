package com.example.gymlog;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
    private ListView listViewExercises = null;

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

        //знайти ListView
        listViewExercises = (ListView) findViewById(R.id.listViewExercises);

        //Назва групи м'язів
        String muscleGroupName = getIntent().getStringExtra("MUSCLE_GROUP");
        Exercise.MuscleGroup muscleGroup = Exercise.MuscleGroup.valueOf(muscleGroupName);

        List<Exercise> sortedExercisesByMuscles = Exercise.MuscleGroup.getExercisesByMuscle(muscleGroup);


        ArrayAdapter<Exercise> exeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sortedExercisesByMuscles);
        listViewExercises.setAdapter(exeAdapter);

    }



}