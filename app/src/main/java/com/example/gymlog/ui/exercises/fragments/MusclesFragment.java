package com.example.gymlog.ui.exercises.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gymlog.R;
import com.example.gymlog.model.exercise.MuscleGroup;


// MusclesFragment.java
public class MusclesFragment extends BaseListFragment {


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Встановлюємо заголовок фрагмента
        TextView title = view.findViewById(R.id.textViewTitle);
        title.setText(R.string.head_muscle_group);
    }

    @Override
    protected Class getClassEnumAttribute() {
        return MuscleGroup.class;
    }



    //що робити якщо ітем вибраний
    @Override
    protected void onItemSelected(Enum muscleGroup) {
        MuscleGroup[] enums = MuscleGroup.values();
        MuscleGroup enumMuscleGroup = null;

        int count = 0;
        for(Enum item : enums) {
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