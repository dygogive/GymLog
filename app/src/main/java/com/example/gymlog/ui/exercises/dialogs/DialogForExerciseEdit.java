package com.example.gymlog.ui.exercises.dialogs;

import androidx.appcompat.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.gymlog.R;
import com.example.gymlog.model.exercise.Equipment;
import com.example.gymlog.model.exercise.Exercise;
import com.example.gymlog.model.exercise.MuscleGroup;
import com.example.gymlog.model.exercise.Motion;
import com.example.gymlog.sqlopenhelper.ExerciseDAO;
import com.example.gymlog.ui.dialogs.DialogStyler;

import java.util.ArrayList;
import java.util.List;

public class DialogForExerciseEdit {

    private final Context context;
    private final ExerciseDAO exerciseDAO;
    private final ExerciseDialogListener listener;
    private OnExerciseCreatedListener createdListener;

    private Motion preselectedMotion = null;
    private List<MuscleGroup> preselectedMuscleGroups = new ArrayList<>();
    private Equipment preselectedEquipment = null;

    public interface ExerciseDialogListener {
        void onExerciseSaved();
    }

    public interface OnExerciseCreatedListener {
        void onExerciseCreated(Exercise exercise);
    }

    public void setOnExerciseCreatedListener(OnExerciseCreatedListener listener) {
        this.createdListener = listener;
    }

    public DialogForExerciseEdit(Context context, ExerciseDialogListener listener) {
        this.context = context;
        this.exerciseDAO = new ExerciseDAO(context);
        this.listener = listener;
    }

    public void show(@Nullable Exercise exercise) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_edit_exercise_new, null);

        EditText editTextExerciseName = dialogView.findViewById(R.id.editTextExerciseName);
        EditText editTextExerciseDescription = dialogView.findViewById(R.id.editTextExerciseDescription);

        Button buttonSelectMotion = dialogView.findViewById(R.id.buttonSelectMotion);
        Button buttonSelectMuscleGroups = dialogView.findViewById(R.id.buttonSelectMuscleGroups);
        Button buttonSelectEquipment = dialogView.findViewById(R.id.buttonSelectEquipment);

        // Зберігаємо базовий текст для м'язів, наприклад "М'язи"
        buttonSelectMuscleGroups.setTag(buttonSelectMuscleGroups.getText().toString());

        if (exercise != null) {
            // Якщо редагуємо існуючу вправу, беремо дані з неї
            editTextExerciseName.setText(exercise.getName());
            editTextExerciseDescription.setText(exercise.getDescription());
            preselectedMotion = exercise.getMotion();
            preselectedEquipment = exercise.getEquipment();
            preselectedMuscleGroups = new ArrayList<>(exercise.getMuscleGroupList());
        }

        // Якщо для діалогу задано попередній вибір (як при створенні нової вправи з фільтром),
        // або якщо редагуємо існуючу вправу, оновлюємо текст кнопок:
        if (preselectedMotion != null) {
            buttonSelectMotion.setText(preselectedMotion.getDescription(context));
        }
        if (preselectedEquipment != null) {
            buttonSelectEquipment.setText(preselectedEquipment.getDescription(context));
        }
        if (preselectedMuscleGroups != null && !preselectedMuscleGroups.isEmpty()) {
            if (preselectedMuscleGroups.size() == 1) {
                // Якщо обрано лише один м'яз – показуємо його назву
                buttonSelectMuscleGroups.setText(preselectedMuscleGroups.get(0).getDescription(context));
            } else {
                // Якщо вибрано 2 і більше – показуємо базовий текст + кількість
                String baseText = (String) buttonSelectMuscleGroups.getTag();
                buttonSelectMuscleGroups.setText(baseText + " (" + preselectedMuscleGroups.size() + ")");
            }
        }

        // Налаштування обробників натискань кнопок (залишаємо вашу поточну логіку)
        buttonSelectMotion.setOnClickListener(v -> {
            AlertDialog dialog = new AlertDialog.Builder(context, R.style.RoundedDialogTheme2)
                    .setTitle(R.string.select_motion)
                    .setSingleChoiceItems(Motion.getAllDescriptions(context),
                            preselectedMotion != null ? preselectedMotion.ordinal() : -1,
                            (d, which) -> preselectedMotion = Motion.values()[which])
                    .setPositiveButton(R.string.ok, (d, w) -> {
                        if (preselectedMotion != null) {
                            buttonSelectMotion.setText(preselectedMotion.getDescription(context));
                        }
                    })
                    .setNegativeButton(R.string.cancel, (d, w) -> {
                        // Якщо скасування – можна повернути базовий текст або зберегти попередній вибір
                        buttonSelectMotion.setText(preselectedMotion != null
                                ? preselectedMotion.getDescription(context)
                                : Motion.getAllDescriptions(context)[0]);
                    })
                    .create();
            dialog.setOnShowListener(d -> DialogStyler.applyAlertDialogStyle(context, dialog));
            dialog.show();
        });

        buttonSelectMuscleGroups.setOnClickListener(v -> {
            String[] muscleDescriptions = MuscleGroup.getAllDescriptions(context);
            boolean[] checkedItems = new boolean[muscleDescriptions.length];
            List<Integer> selectedIndices = new ArrayList<>();

            for (int i = 0; i < muscleDescriptions.length; i++) {
                if (preselectedMuscleGroups.contains(MuscleGroup.values()[i])) {
                    checkedItems[i] = true;
                    selectedIndices.add(i);
                }
            }

            AlertDialog dialog = new AlertDialog.Builder(context, R.style.RoundedDialogTheme2)
                    .setTitle(R.string.select_muscle_groups)
                    .setMultiChoiceItems(muscleDescriptions, checkedItems, (d, which, isChecked) -> {
                        if (isChecked) selectedIndices.add(which);
                        else selectedIndices.remove((Integer) which);
                    })
                    .setPositiveButton(R.string.ok, (d, w) -> {
                        preselectedMuscleGroups.clear();
                        for (int index : selectedIndices) {
                            preselectedMuscleGroups.add(MuscleGroup.values()[index]);
                        }
                        // Оновлюємо текст кнопки залежно від кількості вибраних м'язів
                        if (preselectedMuscleGroups.size() == 1) {
                            buttonSelectMuscleGroups.setText(preselectedMuscleGroups.get(0).getDescription(context));
                        } else {
                            String baseText = (String) buttonSelectMuscleGroups.getTag();
                            buttonSelectMuscleGroups.setText(baseText + " (" + preselectedMuscleGroups.size() + ")");
                        }
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .create();
            dialog.setOnShowListener(d -> DialogStyler.applyAlertDialogStyle(context, dialog));
            dialog.show();
        });

        buttonSelectEquipment.setOnClickListener(v -> {
            AlertDialog dialog = new AlertDialog.Builder(context, R.style.RoundedDialogTheme2)
                    .setTitle(R.string.select_equipment)
                    .setSingleChoiceItems(Equipment.getEquipmentDescriptions(context),
                            preselectedEquipment != null ? preselectedEquipment.ordinal() : -1,
                            (d, which) -> preselectedEquipment = Equipment.values()[which])
                    .setPositiveButton(R.string.ok, (d, w) -> {
                        if (preselectedEquipment != null) {
                            buttonSelectEquipment.setText(preselectedEquipment.getDescription(context));
                        }
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .create();
            dialog.setOnShowListener(d -> DialogStyler.applyAlertDialogStyle(context, dialog));
            dialog.show();
        });

        AlertDialog alertDialog = new AlertDialog.Builder(context, R.style.RoundedDialogTheme2)
                .setTitle(exercise == null ? R.string.add_exercise : R.string.edit_exercise)
                .setView(dialogView)
                .create();

        // Інші кнопки діалогу
        Button buttonCancel = dialogView.findViewById(R.id.buttonCancel);
        Button buttonDeleteExercise = dialogView.findViewById(R.id.buttonDeleteExercise);
        Button buttonSaveExercise = dialogView.findViewById(R.id.buttonSaveExercise);

        buttonCancel.setOnClickListener(v -> alertDialog.dismiss());

        if (exercise != null) {
            buttonDeleteExercise.setVisibility(View.VISIBLE);
            buttonDeleteExercise.setOnClickListener(v -> {
                AlertDialog confirmDialog = new AlertDialog.Builder(context, R.style.RoundedDialogTheme2)
                        .setTitle(R.string.confirm_delete_title)
                        .setMessage(R.string.confirm_delete_message)
                        .setPositiveButton(R.string.yes, (dialog, which) -> {
                            if (exerciseDAO.deleteExercise(exercise)) {
                                Toast.makeText(context, R.string.exercise_deleted, Toast.LENGTH_SHORT).show();
                                listener.onExerciseSaved();
                                alertDialog.dismiss();
                            } else {
                                Toast.makeText(context, R.string.delete_failed, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(R.string.no, null)
                        .create();
                confirmDialog.setOnShowListener(d -> DialogStyler.applyAlertDialogStyle(context, confirmDialog));
                confirmDialog.show();
            });
        } else {
            buttonDeleteExercise.setVisibility(View.GONE);
        }

        buttonSaveExercise.setOnClickListener(v -> {
            // Ваша логіка збереження вправи
            String name = editTextExerciseName.getText().toString().trim();
            String description = editTextExerciseDescription.getText().toString().trim();

            if (name.isEmpty()) {
                editTextExerciseName.setError(context.getString(R.string.name_required));
                editTextExerciseName.requestFocus();
                return;
            }

            if (preselectedMotion == null || preselectedEquipment == null || preselectedMuscleGroups.isEmpty()) {
                Toast.makeText(context, R.string.please_select_all_filters, Toast.LENGTH_SHORT).show();
                return;
            }

            if (exercise == null) {
                Exercise newExercise = new Exercise(-1L, name, description, preselectedMotion, preselectedMuscleGroups, preselectedEquipment);
                long newId = exerciseDAO.addExercise(newExercise);
                if (newId != -1) {
                    newExercise.setId(newId);
                    Toast.makeText(context, R.string.exercise_added, Toast.LENGTH_SHORT).show();
                    listener.onExerciseSaved();
                    if (createdListener != null) {
                        createdListener.onExerciseCreated(newExercise);
                    }
                    alertDialog.dismiss();
                } else {
                    Toast.makeText(context, R.string.add_failed, Toast.LENGTH_SHORT).show();
                }
            } else {
                exercise.setName(name);
                exercise.setDescription(description);
                exercise.setMotion(preselectedMotion);
                exercise.setMuscleGroupList(preselectedMuscleGroups);
                exercise.setEquipment(preselectedEquipment);
                if (exerciseDAO.updateExercise(exercise)) {
                    Toast.makeText(context, R.string.exercise_updated, Toast.LENGTH_SHORT).show();
                    listener.onExerciseSaved();
                    alertDialog.dismiss();
                } else {
                    Toast.makeText(context, R.string.update_failed, Toast.LENGTH_SHORT).show();
                }
            }
        });

        alertDialog.show();
        DialogStyler.styleButtonsInDialog(context, buttonCancel, buttonSaveExercise, buttonDeleteExercise);
    }


    public void showWithPreselectedFilters(@Nullable Exercise exercise, Motion motion, List<MuscleGroup> muscleGroupList, Equipment equipment) {
        this.preselectedMotion = motion;
        this.preselectedMuscleGroups = muscleGroupList;
        this.preselectedEquipment = equipment;
        show(exercise);
    }
}
