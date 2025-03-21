package com.example.gymlog.ui.exercise2.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gymlog.R;
import com.example.gymlog.data.db.ExerciseDAO;
import com.example.gymlog.ui.exercise2.dialogs.DialogForExerciseEdit;
import com.example.gymlog.ui.exercise2.dialogs.DialogSelectTrainingBlocks;
import com.example.gymlog.ui.exercise2.factories.DefaultExercisesFactory;
import com.example.gymlog.ui.exercise2.fragments.AttributeListFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ExerciseManagementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_exercise_management);


        // Ініціалізація бази даних
        ExerciseDAO exerciseDAO = new ExerciseDAO(this);

        // Ініціалізація базових вправ
        DefaultExercisesFactory.initializeDefaultExercises(exerciseDAO);

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

        //exerciseDAO.logAllExercises();
    }

    private void openAddExerciseDialog() {
        DialogForExerciseEdit dialog = new DialogForExerciseEdit(this, this::refreshFragment);

        dialog.setOnExerciseCreatedListener(newExercise -> {
            // Відкриваємо діалог ДОДАВАННЯ ДО БЛОКІВ після збереження вправи в БД
            DialogSelectTrainingBlocks.addExerciseToBlocks(this, newExercise, this::refreshFragment);
        });

        dialog.show(null); // Викликаємо діалог для створення нової вправи
    }

    private void refreshFragment() {
        // Перезавантаження фрагмента для оновлення списку вправ
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new AttributeListFragment())
                .commit();
    }
}
