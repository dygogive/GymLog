package com.example.gymlog.ui.exercises.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gymlog.R;
import com.example.gymlog.domain.model.exercise.AttributeFilter;
import com.example.gymlog.domain.model.exercise.ListHeaderAndAttribute;
import com.example.gymlog.domain.model.exercise.Motion;

import java.util.List;

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

    @Override
    public List<ListHeaderAndAttribute> getItems() {
        return Motion.getGroupedEquipmentItems(requireContext());
    }

    //що робити якщо ітем вибраний
    @Override
    protected void onItemSelected(Object item) {
        Motion motion = (Motion) item;

        Motion[] enums = Motion.values();
        Motion enumMotion = null;

        int count = 0;
        for(Motion motion1 : enums) {
            if(motion1.equals(item)) enumMotion = enums[count];
            else count++;
        }


        Fragment fragment = ExercisesFragment.newInstance(AttributeFilter.MOTION, enumMotion);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}