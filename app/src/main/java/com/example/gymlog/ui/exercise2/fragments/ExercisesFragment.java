package com.example.gymlog.ui.exercise2.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gymlog.R;
import com.example.gymlog.data.db.ExerciseDAO;
import com.example.gymlog.data.exercise.AttributeType;
import com.example.gymlog.data.exercise.BundleKeys;
import com.example.gymlog.data.exercise.Equipment;
import com.example.gymlog.data.exercise.Exercise;
import com.example.gymlog.data.exercise.Motion;
import com.example.gymlog.data.exercise.MuscleGroup;
import com.example.gymlog.ui.exercise2.adapters.ExerciseAdapter;
import com.example.gymlog.ui.exercise2.factories.DefaultExercisesFactory;

import java.util.List;


public class ExercisesFragment extends Fragment {

    private RecyclerView recyclerView;
    private ExerciseAdapter adapter;

    public static ExercisesFragment newInstance(AttributeType attributeType, Enum attribute) {
        ExercisesFragment fragment = new ExercisesFragment();

        // Передача аргументів до фрагмента
        Bundle args = new Bundle();
        args.putString(BundleKeys.ATTRIBUTE_TYPE, attributeType.name());
        args.putString(BundleKeys.ATTRIBUTE, attribute.name());

        fragment.setArguments(args);
        return fragment;

    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_exercises, container, false);

        // Налаштовуємо RecyclerView
        setupRecyclerView(view);

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Отримуємо аргументи
        AttributeType attributeType = AttributeType.valueOf(requireArguments().getString(BundleKeys.ATTRIBUTE_TYPE));
        String attributeName = requireArguments().getString(BundleKeys.ATTRIBUTE);

        TextView title = view.findViewById(R.id.textViewTitle);

        // Отримуємо опис enum
        String description = getEnumDescription(attributeType, attributeName);

        // Встановлюємо заголовок
        title.setText(getString(R.string.exercises) + " - " + description);
    }

    // Метод для отримання опису елемента enum
    private String getEnumDescription(AttributeType attributeType, String attributeName) {
        Context context = requireContext();

        switch (attributeType) {
            case MOTION:
                for (Motion motion : Motion.values()) {
                    if (motion.name().equals(attributeName)) {
                        return motion.getDescription(context);
                    }
                }
                break;

            case MUSCLE_GROUP:
                for (MuscleGroup muscleGroup : MuscleGroup.values()) {
                    if (muscleGroup.name().equals(attributeName)) {
                        return muscleGroup.getDescription(context);
                    }
                }
                break;

            case EQUIPMENT:
                for (Equipment equipment : Equipment.values()) {
                    if (equipment.name().equals(attributeName)) {
                        return equipment.getDescription(context);
                    }
                }
                break;
        }

        return attributeName; // Якщо опис не знайдено, повертаємо назву константи
    }

    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Отримуємо список вправ
        List<Exercise> exercises = getExercises();

        // Ініціалізуємо адаптер і встановлюємо його в RecyclerView
        adapter = new ExerciseAdapter(exercises, new ExerciseAdapter.OnExerciseClickListener() {
            @Override
            public void onExerciseClick(Exercise exercise) {
                Toast.makeText(getContext(), "Selected: " + exercise.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onEditClick(Exercise exercise) {
                // Обробка редагування
                openExerciseDialog(exercise);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private List<Exercise> getExercises() {
        AttributeType attributeType = AttributeType.valueOf(requireArguments().getString(BundleKeys.ATTRIBUTE_TYPE));
        String attribute = requireArguments().getString(BundleKeys.ATTRIBUTE);

        // Ініціалізація DAO
        ExerciseDAO exerciseDAO = new ExerciseDAO(getContext());

        // Виклик методу для отримання вправ
        List<Exercise> exercises = DefaultExercisesFactory.getExercisesForAttribute(
                exerciseDAO,
                attributeType, // Тип атрибуту
                attribute // Значення атрибуту
        );

        // Логування результатів
        for (Exercise exercise : exercises) {
            Log.d("ExerciseList", "Exercise: " + exercise.getName());
        }

        return exercises;
    }


    private void openExerciseDialog(@Nullable Exercise exercise) {
        ui.exercise2.dialogs.ExerciseDialog dialog = new ui.exercise2.dialogs.ExerciseDialog(getContext(), () -> {
            refreshExerciseList(); // Оновлення списку після збереження
        });

        dialog.show(exercise);
    }



    private void updateExercise(Exercise exercise, String newName, Motion newMotion, List<MuscleGroup> newMuscleGroups, Equipment newEquipment) {
        ExerciseDAO exerciseDAO = new ExerciseDAO(getContext());

        // Оновлюємо поля вправи
        exercise.setName(newName);
        exercise.setMotion(newMotion);
        exercise.setMuscleGroupList(newMuscleGroups);
        exercise.setEquipment(newEquipment);

        // Оновлення в базі
        boolean success = exerciseDAO.updateExercise(exercise);

        if (success) {
            Toast.makeText(getContext(), R.string.exercise_updated, Toast.LENGTH_SHORT).show();
            refreshExerciseList();
        } else {
            Toast.makeText(getContext(), R.string.update_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshExerciseList() {
        // Оновлюємо список вправ
        List<Exercise> updatedExercises = getExercises();

        // Оновлюємо адаптер з новими даними
        adapter.updateExercises(updatedExercises);
    }


}