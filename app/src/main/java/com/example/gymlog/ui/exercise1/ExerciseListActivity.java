package com.example.gymlog.ui.exercise1;

import static com.example.gymlog.data.Exercise.getExercisesByMuscle;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gymlog.data.Exercise;
import com.example.gymlog.R;
import com.example.gymlog.data.MuscleGroup;

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
        MuscleGroup muscleGroup = MuscleGroup.valueOf(muscleGroupName);

        List<Exercise> sortedExercisesByMuscles = getExercisesByMuscle(muscleGroup);


        ArrayAdapter<Exercise> exeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sortedExercisesByMuscles);
        listViewExercises.setAdapter(exeAdapter);

    }



}