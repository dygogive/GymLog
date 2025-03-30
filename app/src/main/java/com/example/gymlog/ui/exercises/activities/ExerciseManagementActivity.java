package com.example.gymlog.ui.exercises.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.gymlog.R;
import com.example.gymlog.model.exercise.AttributeFilter;
import com.example.gymlog.model.exercise.BundleKeys;
import com.example.gymlog.model.exercise.Equipment;
import com.example.gymlog.model.exercise.Motion;
import com.example.gymlog.model.exercise.MuscleGroup;
import com.example.gymlog.sqlopenhelper.ExerciseDAO;
import com.example.gymlog.ui.exercises.dialogs.DialogForExerciseEdit;
import com.example.gymlog.ui.exercises.factories.DefaultExercisesFactory;
import com.example.gymlog.ui.exercises.fragments.AttributeListFragment;
import com.example.gymlog.ui.exercises.fragments.ExercisesFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

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

        exerciseDAO.logAllExercises();
        exerciseDAO.voidGetAllDatabaseInLog();
    }

    private void openAddExerciseDialog() {

        DialogForExerciseEdit dialog = new DialogForExerciseEdit(this, this::updateCurrentFragment);

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof ExercisesFragment) {
            Bundle args = currentFragment.getArguments();
            if (args != null) {
                String attributeType = args.getString(BundleKeys.ATTRIBUTE_TYPE);
                String attributeValue = args.getString(BundleKeys.ATTRIBUTE);

                // В залежності від типу фільтра передаємо відповідні параметри в діалог
                if (AttributeFilter.MOTION.name().equals(attributeType)) {
                    Motion motion = Motion.valueOf(attributeValue);
                    // Для інших параметрів передаємо дефолтні значення (null або пустий список)
                    dialog.showWithPreselectedFilters(null, motion, new ArrayList<>(), null);
                    return;
                } else if (AttributeFilter.MUSCLE_GROUP.name().equals(attributeType)) {
                    MuscleGroup muscleGroup = MuscleGroup.valueOf(attributeValue);
                    List<MuscleGroup> muscleGroupList = new ArrayList<>();
                    muscleGroupList.add(muscleGroup);
                    dialog.showWithPreselectedFilters(null, null, muscleGroupList, null);
                    return;
                } else if (AttributeFilter.EQUIPMENT.name().equals(attributeType)) {
                    Equipment equipment = Equipment.valueOf(attributeValue);
                    dialog.showWithPreselectedFilters(null, null, new ArrayList<>(), equipment);
                    return;
                }
            }
        }

        dialog.show(null); // Викликаємо діалог для створення нової вправи

    }


    private void updateCurrentFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment instanceof ExercisesFragment) {
            // Припускаємо, що в ExercisesFragment є метод для оновлення списку
            ((ExercisesFragment) fragment).refreshExerciseList();
        } else {
            // Альтернативний варіант – замінити фрагмент
            refreshFragment();
        }
    }

    private void refreshFragment() {
        // Перезавантаження фрагмента для оновлення списку вправ
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new AttributeListFragment())
                .commit();
    }
}
