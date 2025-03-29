package com.example.gymlog.ui.dialogs;

import android.app.AlertDialog;
import android.content.Context;


import com.example.gymlog.R;

public class ConfirmDeleteDialog {
    public interface OnDeleteConfirmedListener {
        void onDeleteConfirmed();
    }


    public static void show(Context context, String itemName, final OnDeleteConfirmedListener listener) {
        AlertDialog dialog = new AlertDialog.Builder(context, R.style.RoundedDialogTheme2)
                .setTitle(R.string.confirm_deletion)
                .setMessage(context.getString(R.string.do_you_really_want_to_delete) + itemName + "\"?")
                .setPositiveButton(R.string.delete, (d, which) -> {
                    if (listener != null) {
                        listener.onDeleteConfirmed();
                    }
                })
                .setNegativeButton(R.string.cancel, (d, which) -> d.dismiss())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .create();

        // Стилізація кнопок після показу
        dialog.setOnShowListener(dialogInterface ->
                DialogStyler.styleButtonsInDialog(context,
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE),
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                ));

        dialog.show();
    }


}
