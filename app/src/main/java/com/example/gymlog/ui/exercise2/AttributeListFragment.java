package com.example.gymlog.ui.exercise2;

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

import java.util.Arrays;
import java.util.List;


public class AttributeListFragment extends Fragment {

    private RecyclerView recyclerView;
    private AttributeAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attribute_list, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_attributes);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<String> attributes = Arrays.asList("Equipment Type", "Primary Muscle Group", "Secondary Muscle Group");
        adapter = new AttributeAdapter(attributes, this::onAttributeSelected);
        recyclerView.setAdapter(adapter);

        return view;
    }


    private void onAttributeSelected(String attribute) {
        Fragment fragment;
        switch (attribute) {
            case "Equipment Type":
                fragment = new EquipmentTypeFragment();
                break;
            case "Primary Muscle Group":
                fragment = new MuscleGroupFragment(true); // true = primary group
                break;
            case "Secondary Muscle Group":
                fragment = new MuscleGroupFragment(false); // false = secondary group
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + attribute);
        }

        // Замінюємо поточний Fragment на новий
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}