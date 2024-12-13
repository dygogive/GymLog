package com.example.gymlog.ui.exercise2.fragments;

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
import com.example.gymlog.ui.exercise2.adapters.RecyclerViewAdapter;

import java.util.List;


//базовий фрагмент для екрану з вправами
public abstract class BaseListFragment<T> extends Fragment {

    //рециклер що буде у фрагменті
    private RecyclerView recyclerView;
    //адаптер для рециклера
    private RecyclerViewAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutResource(), container, false);

        //ініціалізація recyclerView
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new RecyclerViewAdapter(getItems(), this::onItemSelected);
        recyclerView.setAdapter(adapter);

        return view;
    }

    //взяти ресурс - це канва на якій буде прорисовано елемент списку
    protected abstract int getLayoutResource();

    //взяти список ітемів для відображення в списку
    protected abstract List<String> getItems();

    //Реалізація методу слухача натискання на елемент списку
    protected abstract void onItemSelected(String item);

}
