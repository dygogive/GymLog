package com.example.gymlog.ui.exercise2.fragments;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.example.gymlog.R;
import com.example.gymlog.data.exercise.AttributeType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class AttributeListFragment extends BaseListFragment<String> {



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
    protected void onItemSelected(String attribute) {
        AttributeType attributeType = AttributeType.valueOf(attribute); // Перетворення String на AttributeType
        Fragment fragment = null;

        switch (attributeType) {
            case EQUIPMENT:
                fragment = new EquipmentTypeFragment();
                break;
            case MUSCLE_GROUP:
                fragment = new MuscleFragment();
                break;
            case MOTION:
                fragment = new MotionFragment();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + attribute);
        }

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}