package com.example.gymlog.ui.exercise2.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gymlog.R;
import com.example.gymlog.ui.exercise2.fragments.AttributeListFragment;

public class AttributeSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attribute_selection);

        // Відображення початкового списку атрибутів у Fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new AttributeListFragment())
                    .commit();
        }
    }

}