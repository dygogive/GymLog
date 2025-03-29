package com.example.gymlog.ui.exercises.fragments;

import static com.example.gymlog.model.exercise.AttributeFilter.EQUIPMENT;
import static com.example.gymlog.model.exercise.AttributeFilter.MOTION;
import static com.example.gymlog.model.exercise.AttributeFilter.MUSCLE_GROUP;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gymlog.R;
import com.example.gymlog.model.exercise.AttributeFilter;
import com.example.gymlog.model.exercise.ListHeaderAndAttribute;

import java.util.List;


public class AttributeListFragment extends BaseListFragment {


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Встановлюємо заголовок фрагмента
        TextView title = view.findViewById(R.id.textViewTitle);
        title.setText(R.string.head_sorting_exercises);
    }


    @Override
    protected Class getClassEnumAttribute() {
        return AttributeFilter.class;
    }

    @Override
    public List<ListHeaderAndAttribute> getItems() {
        return AttributeFilter.getGroupedEquipmentItems(requireContext());
    }

    @Override
    protected void onItemSelected(Object item) {
        if (!(item instanceof AttributeFilter)) {
            Log.d("enumTest", "Clicked header or unknown item type");
            return;  // Пропускаємо, якщо натиснуто заголовок
        }

        AttributeFilter attributeFilter = (AttributeFilter) item;

        Fragment fragment;

        if (attributeFilter.equals(MUSCLE_GROUP)) {
            fragment = new MusclesFragment();
        } else if (attributeFilter.equals(EQUIPMENT)) {
            fragment = new EquipmentFragment();
        } else if (attributeFilter.equals(MOTION)) {
            fragment = new MotionsFragment();
        } else {
            throw new IllegalStateException("Несподіване значення: " + attributeFilter);
        }

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

}