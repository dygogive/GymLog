package com.example.gymlog;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;


public class MuscleGroupActivity extends AppCompatActivity {


    private ListView listMuscles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_muscle_group);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        //знайти список
        listMuscles = (ListView) findViewById(R.id.listMuscles);

        //список груп м'язів
        Exercise.MuscleGroup [] muscleGroups = Exercise.MuscleGroup.values();

        //список назв груп
        List<String> muscleDescriptions = new ArrayList<>(muscleGroups.length);
        for(Exercise.MuscleGroup muscleGroup : muscleGroups) {
            muscleDescriptions.add(muscleGroup.getDescription(this));
        }

        //адаптер з списком груп
        ArrayAdapter<String> adapterMuscles = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                muscleDescriptions
        );

        listMuscles.setAdapter(adapterMuscles);


        listMuscles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //визначити яка група вибрана
                Exercise.MuscleGroup selectedMuscleGroup = muscleGroups[position];
                //інтент для переходу на ExerciseList Activity
                Intent intent = new Intent(MuscleGroupActivity.this, ExerciseListActivity.class);
                //екстра назва групи
                intent.putExtra("MUSCLE_GROUP",selectedMuscleGroup.name());
                //старт нового актівіті
                startActivity(intent);
            }
        });
    }
}