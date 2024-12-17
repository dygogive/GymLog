package com.example.gymlog.ui.exercise2.fragments;

import android.app.AlertDialog;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gymlog.R;
import com.example.gymlog.data.db.ExerciseDAO;
import com.example.gymlog.data.exercise.AttributeType;
import com.example.gymlog.data.exercise.Equipment;
import com.example.gymlog.data.exercise.Exercise;
import com.example.gymlog.data.exercise.Motion;
import com.example.gymlog.data.exercise.MuscleGroup;
import com.example.gymlog.ui.exercise2.adapters.ExerciseAdapter;
import com.example.gymlog.ui.exercise2.factories.ExerciseFactory;

import java.util.ArrayList;
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
        List<Exercise> exercises = ExerciseFactory.getExercisesForAttribute(
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


/*    private void openEditExerciseDialog(Exercise exercise){
        //створити в'ю діалогу
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView         = inflater.inflate(R.layout.dialog_edit_exercise, null);

        // Ініціалізація полів редагування
        EditText editTextName         = dialogView.findViewById(R.id.editTextExerciseName);
        Spinner spinnerMotion         = dialogView.findViewById(R.id.spinnerMotion);
        ListView listViewMuscleGroups = dialogView.findViewById(R.id.listViewMuscleGroups);
        Spinner spinnerEquipment      = dialogView.findViewById(R.id.spinnerEquipment);

        // Заповнюємо поля поточними значеннями
        editTextName.setText(exercise.getName());

        // Motion (Spinner)
        ArrayAdapter<Motion> motionAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, Motion.values());
        spinnerMotion.setAdapter(motionAdapter);
        spinnerMotion.setSelection(motionAdapter.getPosition(exercise.getMotion()));

        // Muscle Groups (ListView з мультивибором)
        ArrayAdapter<MuscleGroup> muscleGroupAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_multiple_choice, MuscleGroup.values());
        listViewMuscleGroups.setAdapter(muscleGroupAdapter);

        //розставити чекнуті м'язеві групи
        for (int i = 0; i < MuscleGroup.values().length; i++) {
            if (exercise.getMuscleGroupList().contains(MuscleGroup.values()[i])) {
                listViewMuscleGroups.setItemChecked(i, true);
            }
        }

        // Equipment (Spinner)
        ArrayAdapter<Equipment> equipmentAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, Equipment.values());
        spinnerEquipment.setAdapter(equipmentAdapter);
        spinnerEquipment.setSelection(equipmentAdapter.getPosition(exercise.getEquipment()));

        // Створення діалогу і показати його на екран
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.edit_exercise)
                .setView(dialogView)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    // Зчитування нових значень
                    String newName = editTextName.getText().toString().trim();
                    Motion newMotion = (Motion) spinnerMotion.getSelectedItem();
                    Equipment newEquipment = (Equipment) spinnerEquipment.getSelectedItem();

                    // Отримання вибраних Muscle Groups
                    List<MuscleGroup> selectedMuscleGroups = new ArrayList<>();
                    for (int i = 0; i < listViewMuscleGroups.getCount(); i++) {
                        if (listViewMuscleGroups.isItemChecked(i)) {
                            selectedMuscleGroups.add((MuscleGroup) listViewMuscleGroups.getItemAtPosition(i));
                        }
                    }

                    if (!newName.isEmpty()) {
                        updateExercise(exercise, newName, newMotion, selectedMuscleGroups, newEquipment);
                    } else {
                        Toast.makeText(getContext(), R.string.name_required, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();

    };*/



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