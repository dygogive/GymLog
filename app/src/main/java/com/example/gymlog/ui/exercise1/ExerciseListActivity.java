package com.example.gymlog.ui.exercise1;


import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gymlog.data.db.DBHelper;
import com.example.gymlog.data.db.ExerciseDAO;
import com.example.gymlog.data.exercise.Exercise;
import com.example.gymlog.R;
import com.example.gymlog.data.exercise.MuscleGroup;

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
        MuscleGroup targetMuscle = MuscleGroup.valueOf(muscleGroupName);



        ExerciseDAO exerciseDAO = new ExerciseDAO(this);



        List<Exercise> sortedExercisesByMuscles = exerciseDAO.getExercisesByMuscle(targetMuscle);


        ArrayAdapter<Exercise> exeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sortedExercisesByMuscles);
        listViewExercises.setAdapter(exeAdapter);

    }



}