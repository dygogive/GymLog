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
import com.example.gymlog.model.exercise.Motion;
import com.example.gymlog.model.exercise.TypeAttributeExercises;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MotionsFragment extends BaseListFragment {


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Встановлюємо заголовок фрагмента
        TextView title = view.findViewById(R.id.textViewTitle);
        title.setText(R.string.head_motion);
    }

    @Override
    protected Class getClassEnumAttribute() {
        return Motion.class;
    }

    //що робити якщо ітем вибраний
    @Override
    protected void onItemSelected(Enum motion) {
        Motion[] enums = Motion.values();
        Motion enumMotion = null;

        int count = 0;
        for(Enum item : enums) {
            if(motion.equals(item)) enumMotion = enums[count];
            else count++;
        }


        Fragment fragment = ExercisesFragment.newInstance(AttributeType.MOTION, enumMotion);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}