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

import com.example.gymlog.R;
import com.example.gymlog.ui.exercise2.adapters.RecyclerViewAdapter;

import java.util.Arrays;
import java.util.List;


public class AttributeListFragment extends BaseListFragment<String> {

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_item_exercises;
    }

    @Override
    protected List<String> getItems() {
        return Arrays.asList(
                "Equipment Type",
                "Muscle Group"
        );
    }

    @Override
    protected void onItemSelected(String attribute) {
        Fragment fragment;
        switch (attribute) {
            case "Equipment Type":
                fragment = new EquipmentTypeFragment();
                break;
            case "Muscle Group":
                fragment = new MuscleGroupFragment();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + attribute);
        }

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}