package com.example.gymlog.ui.exercises.fragments;

import android.content.Context;
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
import com.example.gymlog.model.exercise.AttributeType;
import com.example.gymlog.model.exercise.ListHeaderAndAttribute;
import com.example.gymlog.model.exercise.TypeAttributeExercises;
import com.example.gymlog.ui.exercises.adapters.AttributeAdapter;

import java.util.List;


//базовий фрагмент для екрану з вправами
public abstract class BaseListFragment<E extends Enum<E> & TypeAttributeExercises> extends Fragment {

    //рециклер що буде у фрагменті
    private RecyclerView recyclerView;
    //адаптер для рециклера
    private AttributeAdapter adapter;

    protected abstract Class<E> getClassEnumAttribute();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutResource(), container, false);

        //ініціалізація recyclerView
        setupRecyclerView(view);


        return view;
    }


    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new AttributeAdapter(
                getItems(),
                this::onItemSelected
        );
        recyclerView.setAdapter(adapter);
    }

    public abstract List<ListHeaderAndAttribute> getItems();


    //взяти ресурс - це канва на якій буде прорисовано елемент списку
    protected int getLayoutResource() {
        return R.layout.fragment_item_exercises;
    }

    //Реалізація методу слухача натискання на елемент списку
    protected abstract void onItemSelected(Object item);

}
