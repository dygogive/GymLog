package com.example.gymlog.ui.exercise2.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintAttribute;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gymlog.R;
import com.example.gymlog.data.exercise.AttributeType;
import com.example.gymlog.ui.exercise2.adapters.RecyclerViewAdapter;
import com.example.gymlog.ui.exercise2.factories.ExerciseFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ExerciseListFragment extends Fragment {

    private static final String ARG_ATTRIBUTE_TYPE = "attribute_type";
    private static final String ARG_ATTRIBUTE = "attribute";

    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;


    public static ExerciseListFragment newInstance(AttributeType attributeType, Enum attribute) {
        ExerciseListFragment fragment = new ExerciseListFragment();

        //створення аргументів для фрагменту
        Bundle args = new Bundle();
        args.putString(ARG_ATTRIBUTE_TYPE, attributeType.name());
        args.putString(ARG_ATTRIBUTE, attribute.name());

        //задати аргументи фрагменту
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_exercises, container, false);

        //Рециклер
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));


        AttributeType attributeType = AttributeType.valueOf(requireArguments().getString(ARG_ATTRIBUTE_TYPE));
        String attribute = requireArguments().getString(ARG_ATTRIBUTE);

        List<String> exercises = ExerciseFactory.getExercisesForAttribute(attributeType, attribute);


        adapter = new RecyclerViewAdapter(exercises, this::onExerciseSelected);
        recyclerView.setAdapter(adapter);

        return view;
    }



    private void onExerciseSelected(String exercise) {
        // Дії при виборі вправи (наприклад, перегляд деталей)
    }


}