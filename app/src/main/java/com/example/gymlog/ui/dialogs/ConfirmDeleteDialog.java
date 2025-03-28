package com.example.gymlog.ui.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Button;

import androidx.core.content.ContextCompat;

import com.example.gymlog.R;

public class ConfirmDeleteDialog {
    public interface OnDeleteConfirmedListener {
        void onDeleteConfirmed();
    }


    public static void show(Context context, String itemName, final OnDeleteConfirmedListener listener) {
        AlertDialog dialog1 = new AlertDialog.Builder(context,R.style.RoundedDialogTheme2)// ваш кастомний layout
                .setTitle(R.string.confirm_deletion)
                .setMessage(context.getString(R.string.do_you_really_want_to_delete) + itemName + "\"?")
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    if (listener != null) {
                        listener.onDeleteConfirmed();
                    }
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

        // Змінюємо колір кнопок після створення діалогу
        Button positiveButton = dialog1.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialog1.getButton(AlertDialog.BUTTON_NEGATIVE);

        if (positiveButton != null) {
            positiveButton.setTextColor(ContextCompat.getColor(context, R.color.my_primary)); // Червоний
        }
        if (negativeButton != null) {
            negativeButton.setTextColor(ContextCompat.getColor(context, R.color.my_on_primary)); // Сірий
        }
    }

}
