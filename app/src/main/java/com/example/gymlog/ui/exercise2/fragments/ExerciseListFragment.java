package com.example.gymlog.ui.exercise2.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gymlog.R;
import com.example.gymlog.ui.exercise2.adapters.AttributeAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ExerciseListFragment extends Fragment {

    private static final String ARG_ATTRIBUTE = "attribute";
    private RecyclerView recyclerView;
    private AttributeAdapter adapter;


    public static ExerciseListFragment newInstance(String attribute) {
        ExerciseListFragment fragment = new ExerciseListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ATTRIBUTE, attribute);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise_list, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_exercises);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        String attribute = requireArguments().getString(ARG_ATTRIBUTE);
        List<String> exercises = getExercisesForAttribute(attribute);
        adapter = new AttributeAdapter(exercises, this::onExerciseSelected);
        recyclerView.setAdapter(adapter);


        return view;
    }


    private List<String> getExercisesForAttribute(String attribute) {
        // Повертаємо вправи на основі атрибута
        switch (attribute) {
            case "Dumbbell":
                return Arrays.asList("Dumbbell Press", "Dumbbell Row");
            case "Barbell":
                return Arrays.asList("Barbell Squat", "Barbell Deadlift");
            // Додайте інші атрибути
            default:
                return new ArrayList<>();
        }
    }

    private void onExerciseSelected(String exercise) {
        // Дії при виборі вправи (наприклад, перегляд деталей)
    }
}