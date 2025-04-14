package com.example.gymlog.presentation.dialogs_xml;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import com.example.gymlog.R;

public class DialogCreateEditNameDesc extends Dialog {

    private final String currentName;
    private final String currentDescription;
    private String titleDialog = "Edit template";
    private final OnEditConfirmedListener listener;

    private EditText editTextName, editTextDescription;

    // Інтерфейс для передачі оновлених значень
    public interface OnEditConfirmedListener {
        void onEditConfirmed(String newName, String newDescription);
    }

    public DialogCreateEditNameDesc(@NonNull Context context, String currentName, String currentDescription, OnEditConfirmedListener listener) {
        super(context, R.style.RoundedDialogTheme);
        this.currentName = currentName;
        this.currentDescription = currentDescription;
        this.listener = listener;
        this.titleDialog = context.getString(R.string.edit_plan);
    }
    public DialogCreateEditNameDesc(@NonNull Context context, String title, String currentName, String currentDescription, OnEditConfirmedListener listener) {
        super(context, R.style.RoundedDialogTheme);
        this.currentName = currentName;
        this.currentDescription = currentDescription;
        this.listener = listener;
        this.titleDialog = title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit_name_description);

        // Не обов’язково, якщо стиль вже задає фон
        if (getWindow() != null) {
            getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            // getWindow().setBackgroundDrawableResource(R.drawable.dialog_background); // можна прибрати
        }

        TextView idtextViewDialogTitle = findViewById(R.id.textViewDialogTitle);
        editTextName = findViewById(R.id.editTextName);
        editTextDescription = findViewById(R.id.editTextDescription);
        Button buttonCancel = findViewById(R.id.buttonCancel);
        Button buttonSave = findViewById(R.id.buttonSave);

        // Заголовок
        idtextViewDialogTitle.setText(titleDialog);

        // Початкові значення
        editTextName.setText(currentName);
        editTextDescription.setText(currentDescription);

        // Кнопки
        buttonCancel.setOnClickListener(v -> dismiss());

        buttonSave.setOnClickListener(v -> {
            String newName = editTextName.getText().toString().trim();
            if (newName.isEmpty()) {
                editTextName.setError(getContext().getString(R.string.set_name));
                return;
            }

            String newDescription = editTextDescription.getText().toString().trim();
            if (newDescription.isEmpty()) newDescription = currentDescription;

            listener.onEditConfirmed(newName, newDescription);
            dismiss();
        });

        // 🎨 Стилізуємо кнопки через DialogStyler
        DialogStyler.styleButtonsInDialog(getContext(), buttonSave, buttonCancel);
    }

}
