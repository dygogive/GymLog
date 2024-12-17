package ui.exercise2.dialogs;

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

public class ExerciseDialog {

    public interface ExerciseDialogListener {
        void onExerciseSaved();
    }

    private final Context context;
    private final ExerciseDAO exerciseDAO;
    private final ExerciseDialogListener listener;

    public ExerciseDialog(Context context, ExerciseDialogListener listener) {
        this.context = context;
        this.exerciseDAO = new ExerciseDAO(context);
        this.listener = listener;
    }

    public void show(@Nullable Exercise exercise) {
        // Створення діалогу
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_edit_exercise, null);

        EditText editTextName = dialogView.findViewById(R.id.editTextExerciseName);
        Spinner spinnerMotion = dialogView.findViewById(R.id.spinnerMotion);
        Spinner spinnerEquipment = dialogView.findViewById(R.id.spinnerEquipment);
        ListView listViewMuscleGroups = dialogView.findViewById(R.id.listViewMuscleGroups);

        // Адаптери для списків
        spinnerMotion.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, Motion.values()));
        spinnerEquipment.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, Equipment.values()));
        listViewMuscleGroups.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_multiple_choice, MuscleGroup.values()));

        // Заповнення полів, якщо це редагування
        if (exercise != null) {
            editTextName.setText(exercise.getName());
            spinnerMotion.setSelection(((ArrayAdapter) spinnerMotion.getAdapter()).getPosition(exercise.getMotion()));
            spinnerEquipment.setSelection(((ArrayAdapter) spinnerEquipment.getAdapter()).getPosition(exercise.getEquipment()));

            for (int i = 0; i < MuscleGroup.values().length; i++) {
                if (exercise.getMuscleGroupList().contains(MuscleGroup.values()[i])) {
                    listViewMuscleGroups.setItemChecked(i, true);
                }
            }
        }

        // Побудова діалогу
        new AlertDialog.Builder(context)
                .setTitle(exercise == null ? R.string.add_exercise : R.string.edit_exercise)
                .setView(dialogView)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    String name = editTextName.getText().toString().trim();
                    Motion motion = (Motion) spinnerMotion.getSelectedItem();
                    Equipment equipment = (Equipment) spinnerEquipment.getSelectedItem();

                    List<MuscleGroup> selectedMuscleGroups = new ArrayList<>();
                    for (int i = 0; i < listViewMuscleGroups.getCount(); i++) {
                        if (listViewMuscleGroups.isItemChecked(i)) {
                            selectedMuscleGroups.add((MuscleGroup) listViewMuscleGroups.getItemAtPosition(i));
                        }
                    }

                    if (!name.isEmpty()) {
                        if (exercise == null) {
                            addNewExercise(name, motion, selectedMuscleGroups, equipment);
                        } else {
                            updateExercise(exercise, name, motion, selectedMuscleGroups, equipment);
                        }
                    } else {
                        Toast.makeText(context, R.string.name_required, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void addNewExercise(String name, Motion motion, List<MuscleGroup> muscleGroups, Equipment equipment) {
        Exercise newExercise = new Exercise((long) -1, name, motion, muscleGroups, equipment);
        long result = exerciseDAO.addExercise(newExercise);
        if (result != -1) {
            Toast.makeText(context, R.string.exercise_added, Toast.LENGTH_SHORT).show();
            listener.onExerciseSaved();
        } else {
            Toast.makeText(context, R.string.add_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateExercise(Exercise exercise, String name, Motion motion, List<MuscleGroup> muscleGroups, Equipment equipment) {
        exercise.setName(name);
        exercise.setMotion(motion);
        exercise.setMuscleGroupList(muscleGroups);
        exercise.setEquipment(equipment);

        if (exerciseDAO.updateExercise(exercise)) {
            Toast.makeText(context, R.string.exercise_updated, Toast.LENGTH_SHORT).show();
            listener.onExerciseSaved();
        } else {
            Toast.makeText(context, R.string.update_failed, Toast.LENGTH_SHORT).show();
        }
    }
}
