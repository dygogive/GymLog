package com.example.gymlog.ui.legacy.exercise.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gymlog.R;
import com.example.gymlog.domain.model.legacy.attribute.AttributesForFilterExercises;
import com.example.gymlog.domain.model.legacy.attribute.ListHeaderAndAttribute;

import java.util.List;

public class AttributeListFragment extends BaseListFragment<AttributesForFilterExercises> {
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Встановлюємо заголовок фрагмента
        TextView title = view.findViewById(R.id.textViewTitle);
        title.setText(R.string.head_sorting_exercises);
    }

    @Override
    protected Class<AttributesForFilterExercises> getClassEnumAttribute() {
        return AttributesForFilterExercises.class;
    }

    @Override
    public List<ListHeaderAndAttribute> getItems() {
        return AttributesForFilterExercises.getGroupedEquipmentItems(requireContext());
    }

    @Override
    protected void onItemSelected(Object item) {
        if (!(item instanceof AttributesForFilterExercises)) {
            Log.d("AttributesForFilterExercises", "Clicked header or unknown item type");
            return;
        }

        AttributesForFilterExercises attributesForFilterExercises = (AttributesForFilterExercises) item;
        Fragment fragment;

        switch (attributesForFilterExercises) {
            case MUSCLE_GROUP:
                fragment = new MusclesFragment();
                break;
            case EQUIPMENT:
                fragment = new EquipmentFragment();
                break;
            case MOTION:
                fragment = new MotionsFragment();
                break;
            default:
                throw new IllegalStateException("Несподіване значення: " + attributesForFilterExercises);
        }

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}