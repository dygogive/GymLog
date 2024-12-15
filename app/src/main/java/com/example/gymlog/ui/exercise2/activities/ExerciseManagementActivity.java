package com.example.gymlog.ui.exercise2.activities;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gymlog.R;
import com.example.gymlog.data.db.DBHelper;
import com.example.gymlog.data.db.ExerciseDAO;
import com.example.gymlog.ui.exercise2.factories.ExerciseFactory;
import com.example.gymlog.ui.exercise2.fragments.AttributeListFragment;

public class ExerciseManagementActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_management);


        // Ініціалізація бази даних
        ExerciseDAO exerciseDAO = new ExerciseDAO(this);

        // Додавання базових вправ, якщо їх ще немає
        ExerciseFactory.initializeDefaultExercises(this, exerciseDAO);
        exerciseDAO.allExercisesInLog(this);


        // Відображення початкового списку атрибутів у Fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new AttributeListFragment())
                    .commit();
        }
    }

}