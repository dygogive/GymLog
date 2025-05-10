package com.example.gymlog.ui.legacy.exercise.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymlog.R;
import com.example.gymlog.domain.model.legacy.attribute.DescriptionAttribute;
import com.example.gymlog.domain.model.legacy.attribute.ListHeaderAndAttribute;
import com.example.gymlog.ui.legacy.exercise.adapters.AttributeAdapter;

import java.util.List;

public abstract class BaseListFragment<E extends Enum<E> & DescriptionAttribute> extends Fragment {
    private RecyclerView recyclerView;
    private AttributeAdapter adapter;

    /**
     * Повертає клас перерахування атрибутів
     */
    protected abstract Class<E> getClassEnumAttribute();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutResource(), container, false);
        setupRecyclerView(view);
        return view;
    }

    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new AttributeAdapter(getItems(), this::onItemSelected);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Повертає список елементів для відображення
     */
    public abstract List<ListHeaderAndAttribute> getItems();

    /**
     * Повертає ID макету для фрагмента
     */
    protected int getLayoutResource() {
        return R.layout.fragment_item_exercises;
    }

    /**
     * Обробляє вибір елемента у списку
     */
    protected abstract void onItemSelected(Object item);
}