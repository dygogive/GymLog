package com.example.gymlog.ui.exercise2.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.gymlog.R;
import com.example.gymlog.data.exercise.AttributeType;
import com.example.gymlog.ui.exercise2.adapters.RecyclerViewAdapter;
import com.example.gymlog.ui.exercise2.factories.ExerciseFactory;

import java.util.Collections;
import java.util.List;


public class ExercisesFragment extends BaseListFragment {


    public static ExercisesFragment newInstance(AttributeType attributeType, Enum attribute) {
        ExercisesFragment fragment = new ExercisesFragment();

        //створення аргументів для фрагменту
        Bundle args = new Bundle();
        args.putString(BundleKeys.ATTRIBUTE_TYPE, attributeType.name());
        args.putString(BundleKeys.ATTRIBUTE , attribute.name());

        //задати аргументи фрагменту
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_item_exercises;
    }

    @Override
    protected List<String> getItems() {

        AttributeType attributeType = AttributeType.valueOf(requireArguments().getString(BundleKeys.ATTRIBUTE_TYPE));
        String attribute = requireArguments().getString(BundleKeys.ATTRIBUTE);

        List<String> exercises = ExerciseFactory.getExercisesForAttribute(attributeType, attribute);

        return exercises;
    }

    @Override
    protected void onItemSelected(String item) {
        Toast.makeText(this.getContext(), "Exercise selected", Toast.LENGTH_SHORT).show();
    }

}