package com.example.gymlog.ui.exercises.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gymlog.R;
import com.example.gymlog.domain.model.exercise.AttributeFilter;
import com.example.gymlog.domain.model.exercise.Equipment;
import com.example.gymlog.domain.model.exercise.ListHeaderAndAttribute;

import java.util.List;


public class EquipmentFragment extends BaseListFragment {


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Встановлюємо заголовок фрагмента
        TextView title = view.findViewById(R.id.textViewTitle);
        title.setText(R.string.head_weight_type);
    }

    @Override
    protected Class getClassEnumAttribute() {
        return Equipment.class;
    }

    @Override
    public List<ListHeaderAndAttribute> getItems() {
        return Equipment.getGroupedEquipmentItems(requireContext());
    }


    @Override
    protected void onItemSelected(Object item) {
        Equipment equipment = (Equipment) item;

        Equipment[] enums = Equipment.values();
        Equipment enumEquip = null;

        int count = 0;
        for(Equipment equipment1 : enums) {
            if(equipment1.equals(item)) enumEquip = enums[count];
            else count++;
        }



        Fragment fragment = ExercisesFragment.newInstance(AttributeFilter.EQUIPMENT, enumEquip);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}