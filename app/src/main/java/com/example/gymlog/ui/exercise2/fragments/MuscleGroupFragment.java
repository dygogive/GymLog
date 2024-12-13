package com.example.gymlog.ui.exercise2.fragments;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.example.gymlog.R;
import com.example.gymlog.data.exercise.AttributeType;
import com.example.gymlog.data.exercise.MuscleGroup;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


// MuscleGroupFragment.java
public class MuscleGroupFragment extends BaseListFragment<String> {


    //ресурс для ітема фрагмента
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_item_exercises;
    }

    //власне самі ітеми
    @Override
    protected List<String> getItems() {
        Context context = requireContext(); // Гарантовано отримуємо контекст фрагмента

        // Отримуємо список описів м'язових груп
        return Arrays.stream(MuscleGroup.values())
                .map(muscleGroup -> muscleGroup.getDescription(context)) // закидаємо функціональний інтерфейс для кожного елементу енам
                .collect(Collectors.toList());

    }

    //що робити якщо ітем вибраний
    @Override
    protected void onItemSelected(String muscleGroup) {
        MuscleGroup[] enums = MuscleGroup.values();
        MuscleGroup enumMuscleGroup = null;

        int count = 0;
        for(String item : getItems()) {
            if(muscleGroup.equals(item)) enumMuscleGroup = enums[count];
            else count++;
        }


        Fragment fragment = ExercisesFragment.newInstance(AttributeType.MUSCLE_GROUP, enumMuscleGroup);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}