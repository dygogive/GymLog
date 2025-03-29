package com.example.gymlog.ui.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.example.gymlog.R;

import java.util.Locale;

public class DialogStyler {

    /**
     * Застосовує кольори до стандартного AlertDialog після .show().
     */
    public static void applyAlertDialogStyle(@NonNull Context context, @NonNull AlertDialog dialog) {
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        Button neutralButton  = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);

        if (positiveButton != null) {
            positiveButton.setTextColor(ContextCompat.getColor(context, R.color.button_primary));
        }

        if (negativeButton != null) {
            negativeButton.setTextColor(ContextCompat.getColor(context, R.color.button_text_secondary));
        }

        if (neutralButton != null) {
            neutralButton.setTextColor(ContextCompat.getColor(context, R.color.accent_color));
        }
    }

    /**
     * Створює стилізований AlertDialog з кастомним стилем.
     */
    public static AlertDialog buildStandardDialog(@NonNull Context context,
                                                  @StringRes int title,
                                                  @NonNull String message,
                                                  @StringRes int positiveText,
                                                  DialogInterface.OnClickListener positiveListener,
                                                  @StringRes int negativeText,
                                                  DialogInterface.OnClickListener negativeListener) {

        AlertDialog dialog = new AlertDialog.Builder(context, R.style.RoundedDialogTheme2)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveText, positiveListener)
                .setNegativeButton(negativeText, negativeListener)
                .setIcon(android.R.drawable.ic_dialog_info)
                .create();

        dialog.setOnShowListener(d -> applyAlertDialogStyle(context, dialog));
        return dialog;
    }


    public static void styleButtonsInDialog(@NonNull Context context, @Nullable Button... buttons) {
        for (Button button : buttons) {
            if (button == null) continue;

            String text = button.getText().toString().toLowerCase(Locale.ROOT);
            boolean isPrimary = text.contains(context.getString(R.string.save).toLowerCase());


            if (isPrimary) {
                // Основна кнопка: синій фон + білий текст
                button.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.button_primary));
                button.setTextColor(ContextCompat.getColor(context, R.color.button_text_primary));
            } else {
                // Другорядна кнопка: сірий фон + темний текст
                button.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.button_secondary));
                button.setTextColor(ContextCompat.getColor(context, R.color.button_text_secondary));
            }
        }
    }



    /**
     * Показує підтверджувальний діалог з уніфікованим стилем.
     */
    public static void showConfirmDialog(@NonNull Context context,
                                         @StringRes int titleRes,
                                         @StringRes int messageRes,
                                         @StringRes int positiveTextRes,
                                         @Nullable Runnable onConfirmed) {

        AlertDialog dialog = new AlertDialog.Builder(context, R.style.RoundedDialogTheme2)
                .setTitle(titleRes)
                .setMessage(messageRes)
                .setPositiveButton(positiveTextRes, (d, which) -> {
                    if (onConfirmed != null) onConfirmed.run();
                })
                .setNegativeButton(R.string.cancel, (d, which) -> d.dismiss())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .create();

        dialog.setOnShowListener(d -> applyAlertDialogStyle(context, dialog));
        dialog.show();
    }


}
