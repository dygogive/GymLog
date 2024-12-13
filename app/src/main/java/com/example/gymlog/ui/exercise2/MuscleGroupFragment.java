package com.example.gymlog.ui.exercise2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.gymlog.R;

import java.util.ArrayList;
import java.util.List;


public class MuscleGroupFragment extends Fragment {

    private RecyclerView recyclerView;
    private AttributeAdapter adapter;
    private List<String> muscleGroups;

    private boolean isPrimaryMuscleGroup; // Визначає, primary чи secondary показуємо

    public MuscleGroupFragment(boolean isPrimaryMuscleGroup) {
        this.isPrimaryMuscleGroup = isPrimaryMuscleGroup;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_muscle_group, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);

        // Ініціалізація списку м'язів
        muscleGroups = isPrimaryMuscleGroup
                ? getPrimaryMuscleGroups()
                : getSecondaryMuscleGroups();

        setupRecyclerView();

        return view;
    }

    private void setupRecyclerView() {
        adapter = new AttributeAdapter(muscleGroups, this::onMuscleGroupClicked);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }


    private void onMuscleGroupClicked(String muscleGroup) {
        Toast.makeText(requireContext(), "Clicked: " + muscleGroup, Toast.LENGTH_SHORT).show();

        // Реалізація переходу до списку вправ для цієї м'язової групи
        ExerciseListFragment exerciseListFragment = ExerciseListFragment.newInstance(muscleGroup);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, exerciseListFragment)
                .addToBackStack(null)
                .commit();
    }

    private List<String> getPrimaryMuscleGroups() {
        // Замініть на отримання даних із Enum чи бази даних
        return new ArrayList<>(List.of(
                "Chest Upper", "Chest Lower", "Back Lats", "Quads", "Hamstrings"
        ));
    }

    private List<String> getSecondaryMuscleGroups() {
        // Замініть на отримання даних із Enum чи бази даних
        return new ArrayList<>(List.of(
                "Triceps", "Biceps", "Forearms", "Calves", "Deltoids"
        ));
    }

}