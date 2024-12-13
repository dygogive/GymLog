package com.example.gymlog.ui.exercise2.fragments;

import android.content.Context;
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
import com.example.gymlog.data.exercise.AttributeType;
import com.example.gymlog.data.exercise.Equipment;
import com.example.gymlog.data.exercise.MuscleGroup;
import com.example.gymlog.ui.exercise2.adapters.RecyclerViewAdapter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class EquipmentTypeFragment extends BaseListFragment<String> {

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_item_exercises;
    }

    @Override
    protected List<String> getItems() {
        Context context = requireContext(); // Гарантовано отримуємо контекст фрагмента
        return Arrays.stream(Equipment.values()).
                map(it -> it.getDescription(context)).
                collect(Collectors.toList());
    }

    @Override
    protected void onItemSelected(String equipment) {
        Equipment[] enums = Equipment.values();
        Equipment enumEquip = null;

        int count = 0;
        for(String item : getItems()) {
            if(equipment.equals(item)) enumEquip = enums[count];
            else count++;
        }



        Fragment fragment = ExerciseListFragment.newInstance(AttributeType.EQUIPMENT, enumEquip);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}