package com.example.gymlog;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.List;


public class EquipmentTypeFragment extends Fragment {


    private RecyclerView recyclerView;
    private AttributeAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_equipment_type, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_equipment);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<String> equipmentTypes = Arrays.asList("Dumbbell", "Barbell", "Smith Machine", "Bench", "Pull-Up Bar");
        adapter = new AttributeAdapter(equipmentTypes, this::onEquipmentSelected);
        recyclerView.setAdapter(adapter);

        return view;

    }


    private void onEquipmentSelected(String equipment) {
        // Відкриваємо Fragment зі списком вправ для вибраного типу обладнання
        Fragment fragment = ExerciseListFragment.newInstance(equipment);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}