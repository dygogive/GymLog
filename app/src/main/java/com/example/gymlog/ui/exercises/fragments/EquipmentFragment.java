package com.example.gymlog.ui.exercises.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gymlog.R;
import com.example.gymlog.model.exercise.AttributeType;
import com.example.gymlog.model.exercise.Equipment;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class EquipmentFragment extends BaseListFragment<String> {


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Встановлюємо заголовок фрагмента
        TextView title = view.findViewById(R.id.textViewTitle);
        title.setText(R.string.head_weight_type);
    }

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



        Fragment fragment = ExercisesFragment.newInstance(AttributeType.EQUIPMENT, enumEquip);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}