package com.example.gymlog.ui.legacy.exercise.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.gymlog.R;
import com.example.gymlog.domain.model.legacy.attribute.AttributesForFilterExercises;
import com.example.gymlog.domain.model.legacy.attribute.equipment.Equipment;
import com.example.gymlog.domain.model.legacy.attribute.ListHeaderAndAttribute;

import java.util.List;

public class EquipmentFragment extends BaseListFragment<Equipment> {
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Встановлюємо заголовок фрагмента
        TextView title = view.findViewById(R.id.textViewTitle);
        title.setText(R.string.head_weight_type);
    }

    @Override
    protected Class<Equipment> getClassEnumAttribute() {
        return Equipment.class;
    }

    @Override
    public List<ListHeaderAndAttribute> getItems() {
        return Equipment.getGroupedEquipmentItems(requireContext());
    }

    @Override
    protected void onItemSelected(Object item) {
        if (!(item instanceof Equipment)) {
            return;
        }

        Equipment equipment = (Equipment) item;
        openExercisesFragment(AttributesForFilterExercises.EQUIPMENT, equipment);
    }

    private void openExercisesFragment(AttributesForFilterExercises attributeType, Equipment equipment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, ExercisesFragment.newInstance(attributeType, equipment))
                .addToBackStack(null)
                .commit();
    }
}