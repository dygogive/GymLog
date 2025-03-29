package com.example.gymlog.ui.exercises.fragments;

import static com.example.gymlog.model.exercise.AttributeType.EQUIPMENT;
import static com.example.gymlog.model.exercise.AttributeType.MOTION;
import static com.example.gymlog.model.exercise.AttributeType.MUSCLE_GROUP;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gymlog.R;
import com.example.gymlog.model.exercise.AttributeItem;
import com.example.gymlog.model.exercise.AttributeType;
import com.example.gymlog.model.exercise.ListHeaderAndAttribute;

import java.util.Collections;
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
        return AttributeType.class;
    }

    @Override
    public List<ListHeaderAndAttribute> getItems() {
        return AttributeType.getGroupedEquipmentItems(requireContext());
    }

    @Override
    protected void onItemSelected(Object item) {
        if (!(item instanceof AttributeType)) {
            Log.d("enumTest", "Clicked header or unknown item type");
            return;  // Пропускаємо, якщо натиснуто заголовок
        }

        AttributeType attributeType = (AttributeType) item;

        Fragment fragment;

        if (attributeType.equals(MUSCLE_GROUP)) {
            fragment = new MusclesFragment();
        } else if (attributeType.equals(EQUIPMENT)) {
            fragment = new EquipmentFragment();
        } else if (attributeType.equals(MOTION)) {
            fragment = new MotionsFragment();
        } else {
            throw new IllegalStateException("Несподіване значення: " + attributeType);
        }

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

}