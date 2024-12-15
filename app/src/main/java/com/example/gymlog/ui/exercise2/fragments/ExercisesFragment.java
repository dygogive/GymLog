package com.example.gymlog.ui.exercise2.fragments;

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
import android.widget.Toast;

import com.example.gymlog.R;
import com.example.gymlog.data.db.ExerciseDAO;
import com.example.gymlog.data.exercise.AttributeType;
import com.example.gymlog.data.exercise.Exercise;
import com.example.gymlog.ui.exercise2.adapters.ExerciseAdapter;
import com.example.gymlog.ui.exercise2.factories.ExerciseFactory;

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

    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Отримуємо список вправ
        List<Exercise> exercises = getExercises();

        // Ініціалізуємо адаптер і встановлюємо його в RecyclerView
        adapter = new ExerciseAdapter(exercises, this::onExerciseSelected);
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

    private void onExerciseSelected(Exercise exercise) {
        // Обробка натискання на елемент
        Toast.makeText(getContext(), "Selected: " + exercise.getName(), Toast.LENGTH_SHORT).show();
    }
}