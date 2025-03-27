package com.example.gymlog.ui.exercises.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import java.util.ArrayList;
import java.util.List;

public class DialogForExerciseEdit {

    private final Context context;
    private final ExerciseDAO exerciseDAO;
    private final ExerciseDialogListener listener;
    private OnExerciseCreatedListener createdListener;

    // Поля для попереднього вибору фільтрів
    private Motion preselectedMotion = null;
    private List<MuscleGroup> preselectedMuscleGroups = new ArrayList<>();
    private Equipment preselectedEquipment = null;

    public interface ExerciseDialogListener {
        void onExerciseSaved(); // Викликається після успішного додавання/оновлення/видалення
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

    /**
     * Відображає діалог для створення/редагування вправи.
     *
     * @param exercise об'єкт вправи; якщо null – режим створення
     */
    public void show(@Nullable Exercise exercise) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_edit_exercise_new, null);

        // Знаходимо елементи з розмітки
        EditText editTextExerciseName = dialogView.findViewById(R.id.editTextExerciseName);
        EditText editTextExerciseDescription = dialogView.findViewById(R.id.editTextExerciseDescription);
        Button buttonSelectMotion = dialogView.findViewById(R.id.buttonSelectMotion);
        Button buttonSelectMuscleGroups = dialogView.findViewById(R.id.buttonSelectMuscleGroups);
        Button buttonSelectEquipment = dialogView.findViewById(R.id.buttonSelectEquipment);
        Button buttonCancel = dialogView.findViewById(R.id.buttonCancel);
        Button buttonDeleteExercise = dialogView.findViewById(R.id.buttonDeleteExercise);
        Button buttonSaveExercise = dialogView.findViewById(R.id.buttonSaveExercise);

        // Якщо об'єкт вправи не null – режим редагування
        if (exercise != null) {
            editTextExerciseName.setText(exercise.getName());
            editTextExerciseDescription.setText(exercise.getDescription());
            preselectedMotion = exercise.getMotion();
            preselectedEquipment = exercise.getEquipment();
            preselectedMuscleGroups = new ArrayList<>(exercise.getMuscleGroupList());
            // Якщо потрібно, можна встановити текст кнопок відповідно до вибору
            if (preselectedMotion != null) {
                buttonSelectMotion.setText(Motion.getAllDescriptions(context)[preselectedMotion.ordinal()]);
            }
            if (preselectedEquipment != null) {
                buttonSelectEquipment.setText(Equipment.getEquipmentDescriptions(context)[preselectedEquipment.ordinal()]);
            }
            if (!preselectedMuscleGroups.isEmpty()) {
                String txt = context.getString(R.string.chosed) + ": " + preselectedMuscleGroups.size();
                buttonSelectMuscleGroups.setText(txt);
            }
        }

        // Налаштовуємо вибір Motion через AlertDialog з одиничним вибором
        buttonSelectMotion.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.RoundedDialogTheme2);
            builder.setTitle(R.string.select_motion);
            String[] motionDescriptions = Motion.getAllDescriptions(context);
            int initialSelection = preselectedMotion != null ? preselectedMotion.ordinal() : -1;
            final int[] tempSelectedIndex = {initialSelection};

            builder.setSingleChoiceItems(motionDescriptions, initialSelection, (dialog, which) -> tempSelectedIndex[0] = which);
            builder.setPositiveButton(R.string.ok, (dialog, which) -> {
                if (tempSelectedIndex[0] != -1) {
                    preselectedMotion = Motion.values()[tempSelectedIndex[0]];
                    buttonSelectMotion.setText(motionDescriptions[tempSelectedIndex[0]]);
                }
            });
            builder.setNegativeButton(R.string.cancel, null);
            builder.show();
        });

        // Налаштовуємо вибір Muscle Groups через AlertDialog з множинним вибором
        buttonSelectMuscleGroups.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.RoundedDialogTheme2);
            builder.setTitle(R.string.select_muscle_groups);
            String[] muscleDescriptions = MuscleGroup.getAllDescriptions(context);
            boolean[] checkedItems = new boolean[muscleDescriptions.length];
            // Встановлюємо початкове виділення
            for (int i = 0; i < muscleDescriptions.length; i++) {
                if (preselectedMuscleGroups.contains(MuscleGroup.values()[i])) {
                    checkedItems[i] = true;
                }
            }
            List<Integer> selectedIndices = new ArrayList<>();
            for (int i = 0; i < muscleDescriptions.length; i++) {
                if (checkedItems[i]) {
                    selectedIndices.add(i);
                }
            }
            builder.setMultiChoiceItems(muscleDescriptions, checkedItems, (dialog, which, isChecked) -> {
                if (isChecked) {
                    if (!selectedIndices.contains(which)) {
                        selectedIndices.add(which);
                    }
                } else {
                    selectedIndices.remove((Integer) which);
                }
            });
            builder.setPositiveButton(R.string.ok, (dialog, which) -> {
                preselectedMuscleGroups.clear();
                for (int index : selectedIndices) {
                    preselectedMuscleGroups.add(MuscleGroup.values()[index]);
                }
                buttonSelectMuscleGroups.setText(context.getString(R.string.chosed) + ": " + preselectedMuscleGroups.size());
            });
            builder.setNegativeButton(R.string.cancel, null);
            builder.show();
        });

        // Налаштовуємо вибір Equipment через AlertDialog з одиничним вибором
        buttonSelectEquipment.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.RoundedDialogTheme2);
            builder.setTitle(R.string.select_equipment);
            String[] equipmentDescriptions = Equipment.getEquipmentDescriptions(context);
            int initialSelection = preselectedEquipment != null ? preselectedEquipment.ordinal() : -1;
            final int[] tempSelectedIndex = {initialSelection};

            builder.setSingleChoiceItems(equipmentDescriptions, initialSelection, (dialog, which) -> tempSelectedIndex[0] = which);
            builder.setPositiveButton(R.string.ok, (dialog, which) -> {
                if (tempSelectedIndex[0] != -1) {
                    preselectedEquipment = Equipment.values()[tempSelectedIndex[0]];
                    buttonSelectEquipment.setText(equipmentDescriptions[tempSelectedIndex[0]]);
                }
            });
            builder.setNegativeButton(R.string.cancel, null);
            builder.show();
        });

        // Створюємо AlertDialog із кастомним виглядом
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context, R.style.RoundedDialogTheme2)
                .setTitle(exercise == null ? R.string.add_exercise : R.string.edit_exercise)
                .setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();


        // Кнопка Cancel – просто закриває діалог
        buttonCancel.setOnClickListener(v -> alertDialog.dismiss());

        // Кнопка Delete – відображається тільки при редагуванні
        if (exercise != null) {
            buttonDeleteExercise.setVisibility(View.VISIBLE);
            buttonDeleteExercise.setOnClickListener(v -> new AlertDialog.Builder(context,R.style.RoundedDialogTheme2)
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
                    .show());
        } else {
            buttonDeleteExercise.setVisibility(View.GONE);
        }

        buttonSaveExercise.setOnClickListener(v -> {
            String name = editTextExerciseName.getText().toString().trim();
            String description = editTextExerciseDescription.getText().toString().trim();

            if (name.isEmpty()) {
                editTextExerciseName.setError(context.getString(R.string.name_required));
                editTextExerciseName.requestFocus(); // (опціонально) фокус на полі
                return;
            }

            // 🔒 Перевірка вибору всіх фільтрів
            if (preselectedMotion == null || preselectedEquipment == null || preselectedMuscleGroups.isEmpty()) {
                Toast.makeText(context, R.string.please_select_all_filters, Toast.LENGTH_SHORT).show();
                return;
            }


            if (exercise == null) {
                // Створення нової вправи
                Exercise newExercise = new Exercise((long) -1, name, description, preselectedMotion, preselectedMuscleGroups, preselectedEquipment);
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
                // Оновлення існуючої вправи
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
    }

    /**
     * Метод для виклику діалогу з попередньо вибраними фільтрами.
     */
    public void showWithPreselectedFilters(@Nullable Exercise exercise, Motion motion, List<MuscleGroup> muscleGroupList, Equipment equipment) {
        this.preselectedMotion = motion;
        this.preselectedMuscleGroups = muscleGroupList;
        this.preselectedEquipment = equipment;
        show(exercise);
    }
}



















//package com.example.gymlog.ui.exercises.dialogs;
//
//import android.app.AlertDialog;
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.ArrayAdapter;
//import android.widget.EditText;
//import android.widget.ListView;
//import android.widget.Spinner;
//import android.widget.Toast;
//
//import androidx.annotation.Nullable;
//
//import com.example.gymlog.R;
//import com.example.gymlog.model.exercise.Exercise;
//import com.example.gymlog.model.exercise.Equipment;
//import com.example.gymlog.model.exercise.MuscleGroup;
//import com.example.gymlog.model.exercise.Motion;
//import com.example.gymlog.sqlopenhelper.ExerciseDAO;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//
//public class DialogForExerciseEdit {
//
//    private Motion preselectedMotion = null;
//    private List<MuscleGroup> preselectedMuscleGroups = new ArrayList<>();
//    private Equipment preselectedEquipment = null;
//
//
//
//
//    // Поля класу
//    private final Context context; // Контекст для доступу до ресурсів та відображення UI
//    private final ExerciseDAO exerciseDAO; // DAO для взаємодії з базою даних
//    private final ExerciseDialogListener listener; // Слухач для подій діалогу
//    // Інтерфейс для зворотного виклику після збереження вправи
//    public interface ExerciseDialogListener {
//        void onExerciseSaved(); // Викликається при успішному збереженні або видаленні вправи
//    }
//
//
//    private OnExerciseCreatedListener createdListener;
//    public interface OnExerciseCreatedListener {
//        void onExerciseCreated(Exercise exercise);
//    }
//
//
//    public void setOnExerciseCreatedListener(OnExerciseCreatedListener listener) {
//        this.createdListener = listener;
//    }
//
//
//
//    // Конструктор, ініціалізує необхідні залежності
//    public DialogForExerciseEdit(Context context, ExerciseDialogListener listener) {
//        this.context = context;
//        this.exerciseDAO = new ExerciseDAO(context);
//        this.listener = listener;
//    }
//
//    // Метод для відображення діалогу (додає або редагує вправу залежно від переданого об'єкта Exercise)
//    public void show(@Nullable Exercise exercise) {
//
//        // Інфлейтинг макета діалогу
//        LayoutInflater inflater = LayoutInflater.from(context);
//        View dialogView = inflater.inflate(R.layout.dialog_edit_exercise, null);
//
//        // Пошук елементів інтерфейсу
//        EditText editTextName = dialogView.findViewById(R.id.editTextExerciseName); // Поле для введення назви вправи
//        Spinner spinnerMotion = dialogView.findViewById(R.id.spinnerMotion); // Спінер для вибору руху
//        Spinner spinnerEquipment = dialogView.findViewById(R.id.spinnerEquipment); // Спінер для вибору обладнання
//        ListView listViewMuscleGroups = dialogView.findViewById(R.id.listViewMuscleGroups); // Список для вибору м'язевих груп
//
//        // Отримання описів для Motion та Equipment (перекладені строки)
//        String[] motionDescriptions = Motion.getAllDescriptions(context);
//        String[] equipmentDescriptions = Equipment.getEquipmentDescriptions(context);
//
//        // Налаштування адаптерів для спінерів
//        spinnerMotion.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, motionDescriptions));
//        spinnerEquipment.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, equipmentDescriptions));
//
//        // Адаптер для списку м'язевих груп
//        listViewMuscleGroups.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_multiple_choice, MuscleGroup.getAllDescriptions(context)));
//
//        // Якщо передано об'єкт Exercise (режим редагування)
//        if (exercise != null) {
//            // Заповнення назви вправи
//            editTextName.setText(exercise.getName());
//
//            // Встановлення обраного значення для Motion
//            Motion motion = exercise.getMotion();
//            if (motion != null) {
//                int motionPosition = Objects.requireNonNull(Motion.getObjectByDescription(context, motion.getDescription(context))).ordinal();
//                spinnerMotion.setSelection(motionPosition);
//            }
//
//            // Встановлення обраного значення для Equipment
//            Equipment equipment = exercise.getEquipment();
//            if (equipment != null) {
//                int equipmentPosition = Objects.requireNonNull(Equipment.getEquipmentByDescription(context, equipment.getDescription(context))).ordinal();
//                spinnerEquipment.setSelection(equipmentPosition);
//            }
//
//            // Встановлення виділення для м'язевих груп
//            if (preselectedMuscleGroups != null) {
//                for (int i = 0; i < MuscleGroup.values().length; i++) {
//                    if (preselectedMuscleGroups.contains(MuscleGroup.values()[i]))
//                        listViewMuscleGroups.setItemChecked(i, true);
//                }
//            }
//
//        } else {
//
//            // Встановлення обраного значення для Motion
//            Motion motion = preselectedMotion;
//            if (motion != null) {
//                int motionPosition = Objects.requireNonNull(Motion.getObjectByDescription(context, motion.getDescription(context))).ordinal();
//                spinnerMotion.setSelection(motionPosition);
//            }
//
//            // Встановлення обраного значення для Equipment
//            Equipment equipment = preselectedEquipment;
//            if (equipment != null) {
//                int equipmentPosition = Objects.requireNonNull(Equipment.getEquipmentByDescription(context, equipment.getDescription(context))).ordinal();
//                spinnerEquipment.setSelection(equipmentPosition);
//            }
//
//            // Встановлення виділення для м'язевих груп
//            for (int i = 0; i < MuscleGroup.values().length; i++) {
//                if (preselectedMuscleGroups.contains(MuscleGroup.values()[i])) {
//                    listViewMuscleGroups.setItemChecked(i, true);
//                }
//            }
//        }
//
//        // Побудова діалогу
//        AlertDialog.Builder builder = new AlertDialog.Builder(context)
//                .setTitle(exercise == null ? R.string.add_exercise : R.string.edit_exercise) // Назва залежить від режиму
//                .setView(dialogView) // Встановлення кастомного вигляду
//                .setPositiveButton(R.string.ok, (dialog, which) -> {
//                    // Отримання введених користувачем даних
//                    String name                 = editTextName.getText().toString().trim();
//                    String motionDescription    = (String) spinnerMotion.getSelectedItem();
//                    String equipmentDescription = (String) spinnerEquipment.getSelectedItem();
//
//                    Motion selectedMotion       = Motion.getObjectByDescription(context, motionDescription);
//                    Equipment selectedEquipment = Equipment.getEquipmentByDescription(context, equipmentDescription);
//
//                    List<MuscleGroup> selectedMuscleGroups = new ArrayList<>();
//                    for (int i = 0; i < listViewMuscleGroups.getCount(); i++) {
//                        if (listViewMuscleGroups.isItemChecked(i)) {
//                            String muscleGroupDescription = (String) listViewMuscleGroups.getItemAtPosition(i);
//                            MuscleGroup selectedMuscleGroup = MuscleGroup.getObjectByDescription(context, muscleGroupDescription);
//                            selectedMuscleGroups.add(selectedMuscleGroup);
//                        }
//                    }
//
//                    // Перевірка наявності назви та додавання/оновлення вправи
//                    if (!name.isEmpty()) {
//                        if (exercise == null) {
//                            addNewExercise(name, selectedMotion, selectedMuscleGroups, selectedEquipment);
//                        } else {
//                            updateExercise(exercise, name, selectedMotion, selectedMuscleGroups, selectedEquipment);
//                        }
//                    } else {
//                        Toast.makeText(context, R.string.name_required, Toast.LENGTH_SHORT).show(); // Повідомлення про помилку
//                    }
//                })
//                .setNegativeButton(R.string.cancel, null); // Закриття діалогу без дій
//
//        // Додавання кнопки видалення у режимі редагування
//        if (exercise != null) {
//            builder.setNeutralButton(R.string.delete, (dialog, which) -> {
//                deleteExerciseWithConfirmation(exercise); // Виклик підтвердження видалення
//            });
//        }
//        AlertDialog dialog = builder.create();
//        // Встановлюємо колір фону
//        dialog.setOnShowListener(d -> {
//            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.color.my_primary_container); // Вкажи свій колір в res/values/colors.xml
//        });
//
//        dialog.show();
//    }
//
//    public void showWithPreselectedFilters(@Nullable Exercise exercise, Motion motion, List<MuscleGroup> muscleGroupList, Equipment equipment) {
//        this.preselectedMotion = motion;
//        this.preselectedMuscleGroups = muscleGroupList;
//        this.preselectedEquipment = equipment;
//        show(exercise);
//    }
//
//
//    // Метод для видалення вправи з підтвердженням
//    private void deleteExerciseWithConfirmation(Exercise exercise) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context)
//                .setTitle(R.string.confirm_delete_title)
//                .setMessage(R.string.confirm_delete_message)
//                .setPositiveButton(R.string.yes, (dialog, which) -> {
//                    if (exerciseDAO.deleteExercise(exercise)) {
//                        Toast.makeText(context, R.string.exercise_deleted, Toast.LENGTH_SHORT).show();
//                        listener.onExerciseSaved(); // Виклик після успішного видалення
//                    } else {
//                        Toast.makeText(context, R.string.delete_failed, Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .setNegativeButton(R.string.no, null); // Нічого не робимо, якщо "Ні"
//
//        AlertDialog dialog = builder.create();
//        // Встановлюємо колір фону
//        dialog.setOnShowListener(d -> {
//            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.color.primary); // Вкажи свій колір в res/values/colors.xml
//        });
//
//        dialog.show();
//    }
//
//    // Метод для додавання нової вправи
//    // Метод для додавання нової вправи
//    private void addNewExercise(String name, Motion motion, List<MuscleGroup> muscleGroups, Equipment equipment) {
//        Exercise newExercise = new Exercise((long)-1, name, motion, muscleGroups, equipment);
//        long newExerciseId = exerciseDAO.addExercise(newExercise);
//
//        if (newExerciseId != -1) {
//            //Встановлюємо реальний ID вправи після додавання у БД
//            newExercise.setId(newExerciseId);
//
//            Toast.makeText(context, R.string.exercise_added, Toast.LENGTH_SHORT).show();
//            listener.onExerciseSaved(); // Оновлення списку
//
//            if (createdListener != null) {
//                createdListener.onExerciseCreated(newExercise); //Викликаємо діалог вибору блоків ТІЛЬКИ після того, як отримали ID!
//            }
//        } else {
//            Toast.makeText(context, R.string.add_failed, Toast.LENGTH_SHORT).show();
//        }
//    }
//
//
//
//    // Метод для оновлення існуючої вправи**
//    private void updateExercise(Exercise exercise, String name, Motion motion, List<MuscleGroup> muscleGroups, Equipment equipment) {
//        exercise.setName(name);
//        exercise.setMotion(motion);
//        exercise.setMuscleGroupList(muscleGroups);
//        exercise.setEquipment(equipment);
//
//        if (exerciseDAO.updateExercise(exercise)) {
//            Toast.makeText(context, R.string.exercise_updated, Toast.LENGTH_SHORT).show();
//            listener.onExerciseSaved(); // Оновлення списку
//        } else {
//            Toast.makeText(context, R.string.update_failed, Toast.LENGTH_SHORT).show();
//        }
//    }
//}