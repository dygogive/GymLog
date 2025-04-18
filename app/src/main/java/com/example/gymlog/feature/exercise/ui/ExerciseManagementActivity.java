package com.example.gymlog.feature.exercise.ui;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.gymlog.R;
import com.example.gymlog.domain.model.attribute.AttributeFilter;
import com.example.gymlog.domain.model.attribute.BundleKeys;
import com.example.gymlog.domain.model.attribute.equipment.Equipment;
import com.example.gymlog.domain.model.attribute.motion.Motion;
import com.example.gymlog.domain.model.attribute.muscle.MuscleGroup;
import com.example.gymlog.data.local.legacy.ExerciseDAO;
import com.example.gymlog.legacy.dialogs.exercises.DialogForExerciseEdit;
import com.example.gymlog.domain.usecase.exercise.DefaultExercisesFactory;
import com.example.gymlog.legacy.fragments.exercises.AttributeListFragment;
import com.example.gymlog.legacy.fragments.exercises.ExercisesFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
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
                    // Передаємо список з одним елементом для Motion, а для інших параметрів дефолтні значення
                    dialog.showWithPreselectedFilters(null, Collections.singletonList(motion), new ArrayList<>(), null);
                    return;
                } else if (AttributeFilter.MUSCLE_GROUP.name().equals(attributeType)) {
                    MuscleGroup muscleGroup = MuscleGroup.valueOf(attributeValue);
                    List<MuscleGroup> muscleGroupList = new ArrayList<>();
                    muscleGroupList.add(muscleGroup);
                    dialog.showWithPreselectedFilters(null, null, muscleGroupList, null);
                    return;
                } else if (AttributeFilter.EQUIPMENT.name().equals(attributeType)) {
                    Equipment equipment = Equipment.valueOf(attributeValue);
                    // Передаємо список з одним елементом для Equipment
                    dialog.showWithPreselectedFilters(null, null, new ArrayList<>(), Collections.singletonList(equipment));
                    return;
                }
            }
        }

        dialog.show(null); // Викликаємо діалог для створення нової вправи, якщо фільтр не заданий
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
