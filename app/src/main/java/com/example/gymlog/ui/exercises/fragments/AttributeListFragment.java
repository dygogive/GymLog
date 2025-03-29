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
import com.example.gymlog.model.exercise.AttributeType;


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
    protected void onItemSelected(Enum attributeType) {
        if (attributeType == null) {
            throw new IllegalArgumentException("attributeType не може бути null");
        }

        Fragment fragment;

        if (attributeType.equals(MUSCLE_GROUP)) {
            fragment = new MusclesFragment();
        } else if (attributeType.equals(EQUIPMENT)) {
            fragment = new EquipmentFragment();
        } else if (attributeType.equals(MOTION)) {
            fragment = new MotionsFragment();
        } else {// Логування для відладки, якщо буде невідоме значення
            Log.d("enumTest", "Невідоме значення enum: " + attributeType);
            throw new IllegalStateException("Несподіване значення: " + attributeType);
        }

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}