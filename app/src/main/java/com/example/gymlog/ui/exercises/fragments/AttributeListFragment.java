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


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class AttributeListFragment extends BaseListFragment<String> {


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Встановлюємо заголовок фрагмента
        TextView title = view.findViewById(R.id.textViewTitle);
        title.setText(R.string.head_sorting_exercises);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_item_exercises;
    }

    @Override
    protected List<String> getItems() {
        Context context = requireContext(); // Гарантовано отримуємо контекст фрагмента

        // Отримуємо список описів м'язових груп
        return Arrays.stream(AttributeType.values())
                .map(attributeType -> attributeType.getDescription(context)) // закидаємо функціональний інтерфейс для кожного елементу енам
                .collect(Collectors.toList());
    }

    @Override
    protected void onItemSelected(String attributeType) {
        AttributeType[] enums = AttributeType.values();
        AttributeType enumAttributeType = null;

        int count = 0;
        for(String item : getItems()) {
            if(attributeType.equals(item)) enumAttributeType = enums[count];
            else count++;
        }

        Fragment fragment;

        switch (enumAttributeType){
            case MUSCLE_GROUP:
                fragment = new MusclesFragment();
                break;
            case EQUIPMENT:
                fragment = new EquipmentFragment();
                break;
            case MOTION:
                fragment = new MotionsFragment();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + attributeType);
        }



        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}