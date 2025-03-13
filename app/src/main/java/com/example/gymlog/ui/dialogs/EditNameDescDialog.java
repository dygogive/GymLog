package com.example.gymlog.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import com.example.gymlog.R;

public class EditNameDescDialog extends Dialog {

    private final String currentName;
    private final String currentDescription;
    private final OnEditConfirmedListener listener;

    private EditText editTextName, editTextDescription;
    private Button buttonCancel, buttonSave;

    // Інтерфейс для передачі оновлених значень
    public interface OnEditConfirmedListener {
        void onEditConfirmed(String newName, String newDescription);
    }

    public EditNameDescDialog(@NonNull Context context, String currentName, String currentDescription, OnEditConfirmedListener listener) {
        super(context);
        this.currentName = currentName;
        this.currentDescription = currentDescription;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit_name_description);

        // Розширюємо діалог на всю ширину екрану
        if (getWindow() != null) {
            getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        editTextName = findViewById(R.id.editTextName);
        editTextDescription = findViewById(R.id.editTextDescription);
        buttonCancel = findViewById(R.id.buttonCancel);
        buttonSave = findViewById(R.id.buttonSave);

        // Встановлюємо поточний текст, а не hint
        editTextName.setText(currentName);
        editTextDescription.setText(currentDescription);

        // Кнопка "Скасувати"
        buttonCancel.setOnClickListener(v -> dismiss());

        // Кнопка "Зберегти"
        buttonSave.setOnClickListener(v -> {
            String newName = editTextName.getText().toString().trim();
            String newDescription = editTextDescription.getText().toString().trim();

            // Якщо користувач залишив поле порожнім, залишаємо старе значення
            if (newName.isEmpty()) newName = currentName;
            if (newDescription.isEmpty()) newDescription = currentDescription;

            listener.onEditConfirmed(newName, newDescription);
            dismiss();
        });
    }
}
