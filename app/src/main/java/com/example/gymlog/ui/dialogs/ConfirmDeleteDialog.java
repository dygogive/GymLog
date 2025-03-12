package com.example.gymlog.ui.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.example.gymlog.R;

public class ConfirmDeleteDialog {
    public interface OnDeleteConfirmedListener {
        void onDeleteConfirmed();
    }


    public static void show(Context context, String itemName, final OnDeleteConfirmedListener listener) {
        new AlertDialog.Builder(context)
                .setTitle("Підтвердження видалення")
                .setMessage("Ви дійсно бажаєте видалити \"" + itemName + "\"?")
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    if (listener != null) {
                        listener.onDeleteConfirmed();
                    }
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}
