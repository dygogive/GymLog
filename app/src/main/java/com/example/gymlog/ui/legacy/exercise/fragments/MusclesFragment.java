package com.example.gymlog.ui.legacy.exercise.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.gymlog.R;
import com.example.gymlog.domain.model.legacy.attribute.AttributesForFilterExercises;
import com.example.gymlog.domain.model.legacy.attribute.ListHeaderAndAttribute;
import com.example.gymlog.domain.model.legacy.attribute.muscle.MuscleGroup;

import java.util.List;

public class MusclesFragment extends BaseListFragment<MuscleGroup> {
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Встановлюємо заголовок фрагмента
        TextView title = view.findViewById(R.id.textViewTitle);
        title.setText(R.string.head_muscle_group);
    }

    @Override
    protected Class<MuscleGroup> getClassEnumAttribute() {
        return MuscleGroup.class;
    }

    @Override
    public List<ListHeaderAndAttribute> getItems() {
        return MuscleGroup.getGroupedEquipmentItems(requireContext());
    }

    @Override
    protected void onItemSelected(Object item) {
        if (!(item instanceof MuscleGroup)) {
            return;
        }

        MuscleGroup muscleGroup = (MuscleGroup) item;
        openExercisesFragment(AttributesForFilterExercises.MUSCLE_GROUP, muscleGroup);
    }

    private void openExercisesFragment(AttributesForFilterExercises attributeType, MuscleGroup muscleGroup) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, ExercisesFragment.newInstance(attributeType, muscleGroup))
                .addToBackStack(null)
                .commit();
    }
}