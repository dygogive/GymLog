package com.example.gymlog.ui.plan;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.gymlog.R;
import com.example.gymlog.data.db.PlanManagerDAO;

// Діалог для створення нового тренувального дня
public class GymSessionDialog extends Dialog {

    private final long planId;
    private final OnGymDayCreatedListener listener;
    private PlanManagerDAO planManagerDAO;

    private EditText editTextDayName;
    private EditText editTextDayDescription;
    private Button buttonCancel, buttonSave;

    // Інтерфейс для оновлення списку після створення дня
    public interface OnGymDayCreatedListener {
        void onGymDayCreated();
    }

    public GymSessionDialog(@NonNull Context context, long planId, OnGymDayCreatedListener listener) {
        super(context);
        this.planId = planId;
        this.listener = listener;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_gym_day);

        if (getWindow() != null) {
            getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        planManagerDAO = new PlanManagerDAO(getContext());

        editTextDayName = findViewById(R.id.editTextDayName);
        editTextDayDescription = findViewById(R.id.editTextDayDescription);
        buttonCancel = findViewById(R.id.buttonCancel);
        buttonSave = findViewById(R.id.buttonSave);

        buttonCancel.setOnClickListener(v -> dismiss());

        buttonSave.setOnClickListener(v -> saveGymDay());
    }

    private void saveGymDay() {
        String dayName = editTextDayName.getText().toString().trim();
        String description = editTextDayDescription.getText().toString().trim();

        if (dayName.isEmpty()) {
            editTextDayName.setError("Введіть назву дня");
            return;
        }

        if (description.isEmpty()) {
            editTextDayDescription.setError("Введіть опис дня");
            return;
        }

        Log.d("DB_DEBUG", "Намагаємося додати GymSession: planId=" + planId + ", Name=" + dayName + ", Desc=" + description);

        long gymDayId = planManagerDAO.addGymDay(planId, dayName, description);

        if (gymDayId != -1) {
            Toast.makeText(getContext(), "Новий день тренувань додано", Toast.LENGTH_SHORT).show();
            if (listener != null) {
                listener.onGymDayCreated();
            }
            dismiss();
        } else {
            Log.e("DB_ERROR", "Помилка при додаванні дня");
            Toast.makeText(getContext(), "Помилка при додаванні дня", Toast.LENGTH_SHORT).show();
        }
    }

}