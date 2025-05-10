package com.example.gymlog.ui.legacy.exercise.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.gymlog.R;
import com.example.gymlog.domain.model.legacy.attribute.AttributesForFilterExercises;
import com.example.gymlog.domain.model.legacy.attribute.ListHeaderAndAttribute;
import com.example.gymlog.domain.model.legacy.attribute.motion.Motion;

import java.util.List;

public class MotionsFragment extends BaseListFragment<Motion> {
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Встановлюємо заголовок фрагмента
        TextView title = view.findViewById(R.id.textViewTitle);
        title.setText(R.string.head_motion);
    }

    @Override
    protected Class<Motion> getClassEnumAttribute() {
        return Motion.class;
    }

    @Override
    public List<ListHeaderAndAttribute> getItems() {
        return Motion.getGroupedEquipmentItems(requireContext());
    }

    @Override
    protected void onItemSelected(Object item) {
        if (!(item instanceof Motion)) {
            return;
        }

        Motion motion = (Motion) item;
        openExercisesFragment(AttributesForFilterExercises.MOTION, motion);
    }

    private void openExercisesFragment(AttributesForFilterExercises attributeType, Motion motion) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, ExercisesFragment.newInstance(attributeType, motion))
                .addToBackStack(null)
                .commit();
    }
}
