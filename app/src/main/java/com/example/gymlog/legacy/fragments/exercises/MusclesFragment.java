package com.example.gymlog.legacy.fragments.exercises;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gymlog.R;
import com.example.gymlog.domain.model.attribute.AttributeFilter;
import com.example.gymlog.domain.model.attribute.ListHeaderAndAttribute;
import com.example.gymlog.domain.model.attribute.muscle.MuscleGroup;

import java.util.List;


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

    @Override
    public List<ListHeaderAndAttribute> getItems() {
        return MuscleGroup.getGroupedEquipmentItems(requireContext());
    }

    //що робити якщо ітем вибраний
    @Override
    protected void onItemSelected(Object item) {
        MuscleGroup muscleGroup  = (MuscleGroup) item;


        MuscleGroup[] enums = MuscleGroup.values();
        MuscleGroup enumMuscleGroup = null;

        int count = 0;
        for(MuscleGroup muscleGroup1 : enums) {
            if(muscleGroup1.equals(muscleGroup)) enumMuscleGroup = enums[count];
            else count++;
        }


        Fragment fragment = ExercisesFragment.newInstance(AttributeFilter.MUSCLE_GROUP, enumMuscleGroup);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}