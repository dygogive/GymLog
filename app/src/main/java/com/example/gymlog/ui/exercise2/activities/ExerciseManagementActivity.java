package com.example.gymlog.ui.exercise2.activities;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gymlog.R;
import com.example.gymlog.data.db.DBHelper;
import com.example.gymlog.data.db.ExerciseDAO;
import com.example.gymlog.ui.exercise2.factories.ExerciseFactory;
import com.example.gymlog.ui.exercise2.fragments.AttributeListFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ExerciseManagementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_management);


        // Ініціалізація бази даних
        ExerciseDAO exerciseDAO = new ExerciseDAO(this);

        // Ініціалізація базових вправ
        ExerciseFactory.initializeDefaultExercises(exerciseDAO);

        // Відображення фрагмента
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new AttributeListFragment())
                    .commit();
        }

        // Ініціалізація кнопки FAB
        FloatingActionButton fabAddExercise = findViewById(R.id.fab_add_exercise);
        fabAddExercise.setOnClickListener(view -> openAddExerciseDialog());

        exerciseDAO.logAllExercises();
    }

    private void openAddExerciseDialog() {
        ui.exercise2.dialogs.ExerciseDialog dialog = new ui.exercise2.dialogs.ExerciseDialog(this, () -> {
            // Оновлення UI після збереження вправи
            refreshFragment();
        });
        dialog.show(null); // Передаємо null, щоб створити нову вправу
    }

    private void refreshFragment() {
        // Перезавантаження фрагмента для оновлення списку вправ
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new AttributeListFragment())
                .commit();
    }
}
