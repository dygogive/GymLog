package com.example.gymlog.ui.exercise2.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.gymlog.R;
import com.example.gymlog.data.exercise.Exercise;
import com.example.gymlog.data.exercise.Equipment;
import com.example.gymlog.data.exercise.MuscleGroup;
import com.example.gymlog.data.exercise.Motion;
import com.example.gymlog.data.db.ExerciseDAO;

import java.util.ArrayList;
import java.util.List;

public class DialogForExerciseEdit {

    // Інтерфейс для зворотного виклику після збереження вправи
    public interface ExerciseDialogListener {
        void onExerciseSaved(); // Викликається при успішному збереженні або видаленні вправи
    }

    // Поля класу
    private final Context context; // Контекст для доступу до ресурсів та відображення UI
    private final ExerciseDAO exerciseDAO; // DAO для взаємодії з базою даних
    private final ExerciseDialogListener listener; // Слухач для подій діалогу

    // Конструктор, ініціалізує необхідні залежності
    public DialogForExerciseEdit(Context context, ExerciseDialogListener listener) {
        this.context = context;
        this.exerciseDAO = new ExerciseDAO(context);
        this.listener = listener;
    }

    // Метод для відображення діалогу (додає або редагує вправу залежно від переданого об'єкта Exercise)
    public void show(@Nullable Exercise exercise) {
        // Інфлейтинг макета діалогу
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_edit_exercise, null);

        // Пошук елементів інтерфейсу
        EditText editTextName = dialogView.findViewById(R.id.editTextExerciseName); // Поле для введення назви вправи
        Spinner spinnerMotion = dialogView.findViewById(R.id.spinnerMotion); // Спінер для вибору руху
        Spinner spinnerEquipment = dialogView.findViewById(R.id.spinnerEquipment); // Спінер для вибору обладнання
        ListView listViewMuscleGroups = dialogView.findViewById(R.id.listViewMuscleGroups); // Список для вибору м'язевих груп

        // Отримання описів для Motion та Equipment (перекладені строки)
        String[] motionDescriptions = Motion.getAllDescriptions(context);
        String[] equipmentDescriptions = Equipment.getEquipmentDescriptions(context);

        // Налаштування адаптерів для спінерів
        spinnerMotion.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, motionDescriptions));
        spinnerEquipment.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, equipmentDescriptions));

        // Адаптер для списку м'язевих груп
        listViewMuscleGroups.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_multiple_choice, MuscleGroup.getAllDescriptions(context)));

        // Якщо передано об'єкт Exercise (режим редагування)
        if (exercise != null) {
            // Заповнення назви вправи
            editTextName.setText(exercise.getName());

            // Встановлення обраного значення для Motion
            Motion motion = exercise.getMotion();
            if (motion != null) {
                int motionPosition = Motion.getObjectByDescription(context, motion.getDescription(context)).ordinal();
                spinnerMotion.setSelection(motionPosition);
            }

            // Встановлення обраного значення для Equipment
            Equipment equipment = exercise.getEquipment();
            if (equipment != null) {
                int equipmentPosition = Equipment.getEquipmentByDescription(context, equipment.getDescription(context)).ordinal();
                spinnerEquipment.setSelection(equipmentPosition);
            }

            // Встановлення виділення для м'язевих груп
            for (int i = 0; i < MuscleGroup.values().length; i++) {
                if (exercise.getMuscleGroupList().contains(MuscleGroup.values()[i])) {
                    listViewMuscleGroups.setItemChecked(i, true);
                }
            }
        }

        // Побудова діалогу
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(exercise == null ? R.string.add_exercise : R.string.edit_exercise) // Назва залежить від режиму
                .setView(dialogView) // Встановлення кастомного вигляду
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    // Отримання введених користувачем даних
                    String name                 = editTextName.getText().toString().trim();
                    String motionDescription    = (String) spinnerMotion.getSelectedItem();
                    String equipmentDescription = (String) spinnerEquipment.getSelectedItem();

                    Motion selectedMotion       = Motion.getObjectByDescription(context, motionDescription);
                    Equipment selectedEquipment = Equipment.getEquipmentByDescription(context, equipmentDescription);

                    List<MuscleGroup> selectedMuscleGroups = new ArrayList<>();
                    for (int i = 0; i < listViewMuscleGroups.getCount(); i++) {
                        if (listViewMuscleGroups.isItemChecked(i)) {
                            String muscleGroupDescription = (String) listViewMuscleGroups.getItemAtPosition(i);
                            MuscleGroup selectedMuscleGroup = MuscleGroup.getObjectByDescription(context, muscleGroupDescription);
                            selectedMuscleGroups.add(selectedMuscleGroup);
                        }
                    }

                    // Перевірка наявності назви та додавання/оновлення вправи
                    if (!name.isEmpty()) {
                        if (exercise == null) {
                            addNewExercise(name, selectedMotion, selectedMuscleGroups, selectedEquipment);
                        } else {
                            updateExercise(exercise, name, selectedMotion, selectedMuscleGroups, selectedEquipment);
                        }
                    } else {
                        Toast.makeText(context, R.string.name_required, Toast.LENGTH_SHORT).show(); // Повідомлення про помилку
                    }
                })
                .setNegativeButton(R.string.cancel, null); // Закриття діалогу без дій

        // Додавання кнопки видалення у режимі редагування
        if (exercise != null) {
            builder.setNeutralButton(R.string.delete, (dialog, which) -> {
                deleteExerciseWithConfirmation(exercise); // Виклик підтвердження видалення
            });
        }
        AlertDialog dialog = builder.create();
        // Встановлюємо колір фону
        dialog.setOnShowListener(d -> {
            dialog.getWindow().setBackgroundDrawableResource(R.color.background); // Вкажи свій колір в res/values/colors.xml
        });

        dialog.show();
    }

    // Метод для видалення вправи з підтвердженням
    private void deleteExerciseWithConfirmation(Exercise exercise) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(R.string.confirm_delete_title)
                .setMessage(R.string.confirm_delete_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    if (exerciseDAO.deleteExercise(exercise)) {
                        Toast.makeText(context, R.string.exercise_deleted, Toast.LENGTH_SHORT).show();
                        listener.onExerciseSaved(); // Виклик після успішного видалення
                    } else {
                        Toast.makeText(context, R.string.delete_failed, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.no, null); // Нічого не робимо, якщо "Ні"

        AlertDialog dialog = builder.create();
        // Встановлюємо колір фону
        dialog.setOnShowListener(d -> {
            dialog.getWindow().setBackgroundDrawableResource(R.color.primary); // Вкажи свій колір в res/values/colors.xml
        });

        dialog.show();
    }

    // Метод для додавання нової вправи
    private void addNewExercise(String name, Motion motion, List<MuscleGroup> muscleGroups, Equipment equipment) {
        Exercise newExercise = new Exercise((long) -1, name, motion, muscleGroups, equipment);
        long result = exerciseDAO.addExercise(newExercise);
        if (result != -1) {
            Toast.makeText(context, R.string.exercise_added, Toast.LENGTH_SHORT).show();
            listener.onExerciseSaved(); // Оновлення списку
        } else {
            Toast.makeText(context, R.string.add_failed, Toast.LENGTH_SHORT).show();
        }
    }

    // Метод для оновлення існуючої вправи
    private void updateExercise(Exercise exercise, String name, Motion motion, List<MuscleGroup> muscleGroups, Equipment equipment) {
        exercise.setName(name);
        exercise.setMotion(motion);
        exercise.setMuscleGroupList(muscleGroups);
        exercise.setEquipment(equipment);

        if (exerciseDAO.updateExercise(exercise)) {
            Toast.makeText(context, R.string.exercise_updated, Toast.LENGTH_SHORT).show();
            listener.onExerciseSaved(); // Оновлення списку
        } else {
            Toast.makeText(context, R.string.update_failed, Toast.LENGTH_SHORT).show();
        }
    }
}