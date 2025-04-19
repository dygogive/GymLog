package com.example.gymlog.ui.legacy.exercise

import androidx.appcompat.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.Nullable

import com.example.gymlog.R
import com.example.gymlog.domain.model.attribute.equipment.Equipment
import com.example.gymlog.domain.model.exercise.Exercise
import com.example.gymlog.domain.model.attribute.muscle.MuscleGroup
import com.example.gymlog.domain.model.attribute.motion.Motion
import com.example.gymlog.data.local.legacy.ExerciseDAO
import com.example.gymlog.ui.legacy.dialogs.DialogStyler

/**
 * Dialog for creating and editing exercise entries.
 * Handles the UI for adding new exercises or modifying existing ones.
 */
class DialogForExerciseEdit(
    private val context: Context,
    private val onExerciseEditedListener: OnExerciseEditedListener
) {

    // Interfaces for callback listeners
    interface OnExerciseEditedListener {
        fun onExerciseSaved()
    }

    interface OnExerciseCreatedListener {
        fun onExerciseCreated(exercise: Exercise)
    }

    // Class variables
    private val exerciseDAO = ExerciseDAO(context)
    private var createdListener: OnExerciseCreatedListener? = null

    // Selected attribute values
    // For Exercise we store single values for motion and equipment,
    // and a list for muscleGroups
    private var preselectedMotion: Motion? = null
    private var preselectedMuscleGroups: MutableList<MuscleGroup> = ArrayList()
    private var preselectedEquipment: Equipment? = null

    /**
     * Sets the listener for exercise creation events
     */
    fun setOnExerciseCreatedListener(listener: OnExerciseCreatedListener) {
        this.createdListener = listener
    }

    /**
     * Shows the dialog for creating or editing an exercise
     *
     * @param exercise Exercise to edit, or null for creating a new one
     */
    fun show(exercise: Exercise?) {
        // Inflate the dialog layout
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.dialog_edit_exercise_new, null)

        // Initialize UI elements
        val editTextExerciseName = dialogView.findViewById<EditText>(R.id.editTextExerciseName)
        val editTextExerciseDescription = dialogView.findViewById<EditText>(R.id.editTextExerciseDescription)
        val buttonSelectMotion = dialogView.findViewById<Button>(R.id.buttonSelectMotion)
        val buttonSelectMuscleGroups = dialogView.findViewById<Button>(R.id.buttonSelectMuscleGroups)
        val buttonSelectEquipment = dialogView.findViewById<Button>(R.id.buttonSelectEquipment)

        // Save base text for muscle groups button (e.g., "Muscles")
        buttonSelectMuscleGroups.tag = buttonSelectMuscleGroups.text.toString()

        // Populate fields if editing an existing exercise
        exercise?.let {
            editTextExerciseName.setText(it.name)
            editTextExerciseDescription.setText(it.description)
            preselectedMotion = it.motion
            preselectedEquipment = it.equipment
            preselectedMuscleGroups = ArrayList(it.muscleGroupList)
        }

        // Update button text based on preselected values
        updateAttributeButtonsText(buttonSelectMotion, buttonSelectMuscleGroups, buttonSelectEquipment)

        // Setup button click listeners for attribute selection
        setupMotionSelectionButton(buttonSelectMotion)
        setupMuscleGroupsSelectionButton(buttonSelectMuscleGroups)
        setupEquipmentSelectionButton(buttonSelectEquipment)

        // Create the main dialog
        val alertDialog = AlertDialog.Builder(context, R.style.RoundedDialogTheme2)
            .setTitle(if (exercise == null) R.string.add_exercise else R.string.edit_exercise)
            .setView(dialogView)
            .create()

        // Setup action buttons
        val buttonCancel = dialogView.findViewById<Button>(R.id.buttonCancel)
        val buttonDeleteExercise = dialogView.findViewById<Button>(R.id.buttonDeleteExercise)
        val buttonSaveExercise = dialogView.findViewById<Button>(R.id.buttonSaveExercise)

        // Configure button behaviors
        setupActionButtons(exercise, alertDialog, buttonCancel, buttonDeleteExercise, buttonSaveExercise,
            editTextExerciseName, editTextExerciseDescription)

        // Show the dialog and style buttons
        alertDialog.show()
        DialogStyler.styleButtonsInDialog(context, buttonCancel, buttonSaveExercise, buttonDeleteExercise)
    }

    /**
     * Updates the text of attribute selection buttons based on current selections
     */
    private fun updateAttributeButtonsText(
        buttonSelectMotion: Button,
        buttonSelectMuscleGroups: Button,
        buttonSelectEquipment: Button
    ) {
        // Update motion button text
        preselectedMotion?.let {
            buttonSelectMotion.text = it.getDescription(context)
            DialogStyler.styleButtonsInDialog(context, buttonSelectMotion)
        }

        // Update equipment button text
        preselectedEquipment?.let {
            buttonSelectEquipment.text = it.getDescription(context)
            DialogStyler.styleButtonsInDialog(context, buttonSelectEquipment)
        }

        // Update muscle groups button text
        if (preselectedMuscleGroups.isNotEmpty()) {
            if (preselectedMuscleGroups.size == 1) {
                // If only one muscle is selected - show its name
                buttonSelectMuscleGroups.text = preselectedMuscleGroups[0].getDescription(context)
            } else {
                // If 2+ muscles selected - show base text with count
                val baseText = buttonSelectMuscleGroups.tag as String
                buttonSelectMuscleGroups.text = "$baseText (${preselectedMuscleGroups.size})"
            }
            DialogStyler.styleButtonsInDialog(context, buttonSelectMuscleGroups)
        }
    }

    /**
     * Sets up the motion selection button listener
     */
    private fun setupMotionSelectionButton(buttonSelectMotion: Button) {
        buttonSelectMotion.setOnClickListener {
            val dialog = AlertDialog.Builder(context, R.style.RoundedDialogTheme2)
                .setTitle(R.string.select_motion)
                .setSingleChoiceItems(
                    Motion.getAllDescriptions(context),
                    preselectedMotion?.ordinal ?: -1
                ) { _, which ->
                    preselectedMotion = Motion.values()[which]
                }
                .setPositiveButton(R.string.ok) { _, _ ->
                    preselectedMotion?.let {
                        buttonSelectMotion.text = it.getDescription(context)
                    }
                }
                .setNegativeButton(R.string.cancel) { _, _ ->
                    // When canceling, restore previous selection or base value
                    buttonSelectMotion.text = preselectedMotion?.getDescription(context)
                        ?: Motion.getAllDescriptions(context)[0]
                }
                .create()

            dialog.setOnShowListener { DialogStyler.applyAlertDialogStyle(context, dialog) }
            dialog.show()
        }
    }

    /**
     * Sets up the muscle groups selection button listener
     */
    private fun setupMuscleGroupsSelectionButton(buttonSelectMuscleGroups: Button) {
        buttonSelectMuscleGroups.setOnClickListener {
            val muscleDescriptions = MuscleGroup.getAllDescriptions(context)
            val checkedItems = BooleanArray(muscleDescriptions.size)
            val selectedIndices = ArrayList<Int>()

            // Mark currently selected muscles as checked
            for (i in muscleDescriptions.indices) {
                if (preselectedMuscleGroups.contains(MuscleGroup.values()[i])) {
                    checkedItems[i] = true
                    selectedIndices.add(i)
                }
            }

            val dialog = AlertDialog.Builder(context, R.style.RoundedDialogTheme2)
                .setTitle(R.string.select_muscle_groups)
                .setMultiChoiceItems(muscleDescriptions, checkedItems) { _, which, isChecked ->
                    if (isChecked) selectedIndices.add(which)
                    else selectedIndices.remove(which)
                }
                .setPositiveButton(R.string.ok) { _, _ ->
                    preselectedMuscleGroups.clear()
                    for (index in selectedIndices) {
                        preselectedMuscleGroups.add(MuscleGroup.values()[index])
                    }
                    // Update button text based on selected count
                    if (preselectedMuscleGroups.size == 1) {
                        buttonSelectMuscleGroups.text = preselectedMuscleGroups[0].getDescription(context)
                    } else {
                        val baseText = buttonSelectMuscleGroups.tag as String
                        buttonSelectMuscleGroups.text = "$baseText (${preselectedMuscleGroups.size})"
                    }
                }
                .setNegativeButton(R.string.cancel, null)
                .create()

            dialog.setOnShowListener { DialogStyler.applyAlertDialogStyle(context, dialog) }
            dialog.show()
        }
    }

    /**
     * Sets up the equipment selection button listener
     */
    private fun setupEquipmentSelectionButton(buttonSelectEquipment: Button) {
        buttonSelectEquipment.setOnClickListener {
            val dialog = AlertDialog.Builder(context, R.style.RoundedDialogTheme2)
                .setTitle(R.string.select_equipment)
                .setSingleChoiceItems(
                    Equipment.getEquipmentDescriptions(context),
                    preselectedEquipment?.ordinal ?: -1
                ) { _, which ->
                    preselectedEquipment = Equipment.values()[which]
                }
                .setPositiveButton(R.string.ok) { _, _ ->
                    preselectedEquipment?.let {
                        buttonSelectEquipment.text = it.getDescription(context)
                    }
                }
                .setNegativeButton(R.string.cancel, null)
                .create()

            dialog.setOnShowListener { DialogStyler.applyAlertDialogStyle(context, dialog) }
            dialog.show()
        }
    }

    /**
     * Sets up the main action buttons (Cancel, Delete, Save)
     */
    private fun setupActionButtons(
        exercise: Exercise?,
        alertDialog: AlertDialog,
        buttonCancel: Button,
        buttonDeleteExercise: Button,
        buttonSaveExercise: Button,
        editTextExerciseName: EditText,
        editTextExerciseDescription: EditText
    ) {
        // Cancel button simply dismisses the dialog
        buttonCancel.setOnClickListener { alertDialog.dismiss() }

        // Delete button is only visible when editing an existing exercise
        if (exercise != null) {
            buttonDeleteExercise.visibility = View.VISIBLE
            buttonDeleteExercise.setOnClickListener { showDeleteConfirmationDialog(exercise, alertDialog) }
        } else {
            buttonDeleteExercise.visibility = View.GONE
        }

        // Save button validates input and saves the exercise
        buttonSaveExercise.setOnClickListener {
            if (validateAndSaveExercise(exercise, editTextExerciseName, editTextExerciseDescription, alertDialog)) {
                // Exercise was saved successfully
                alertDialog.dismiss()
            }
        }
    }

    /**
     * Shows a confirmation dialog before deleting an exercise
     */
    private fun showDeleteConfirmationDialog(exercise: Exercise, parentDialog: AlertDialog) {
        val confirmDialog = AlertDialog.Builder(context, R.style.RoundedDialogTheme2)
            .setTitle(R.string.confirm_delete_title)
            .setMessage(R.string.confirm_delete_message)
            .setPositiveButton(R.string.yes) { _, _ ->
                if (exerciseDAO.deleteExercise(exercise)) {
                    Toast.makeText(context, R.string.exercise_deleted, Toast.LENGTH_SHORT).show()
                    onExerciseEditedListener.onExerciseSaved()
                    parentDialog.dismiss()
                } else {
                    Toast.makeText(context, R.string.delete_failed, Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.no, null)
            .create()

        confirmDialog.setOnShowListener { DialogStyler.applyAlertDialogStyle(context, confirmDialog) }
        confirmDialog.show()
    }

    /**
     * Validates input and saves the exercise
     *
     * @return true if save was successful, false otherwise
     */
    private fun validateAndSaveExercise(
        exercise: Exercise?,
        editTextExerciseName: EditText,
        editTextExerciseDescription: EditText,
        alertDialog: AlertDialog
    ): Boolean {
        // Get input values
        val name = editTextExerciseName.text.toString().trim()
        val description = editTextExerciseDescription.text.toString().trim()

        // Validate name
        if (name.isEmpty()) {
            editTextExerciseName.error = context.getString(R.string.name_required)
            editTextExerciseName.requestFocus()
            return false
        }

        // Validate attribute selections
        if (preselectedMotion == null || preselectedEquipment == null || preselectedMuscleGroups.isEmpty()) {
            Toast.makeText(context, R.string.please_select_all_filters, Toast.LENGTH_SHORT).show()
            return false
        }

        // Either create a new exercise or update an existing one
        return if (exercise == null) {
            createNewExercise(name, description)
        } else {
            updateExistingExercise(exercise, name, description)
        }
    }

    /**
     * Creates a new exercise with the given parameters
     *
     * @return true if creation was successful, false otherwise
     */
    private fun createNewExercise(name: String, description: String): Boolean {
        val newExercise = Exercise(
            -1L,
            name,
            description,
            preselectedMotion!!,
            preselectedMuscleGroups,
            preselectedEquipment!!
        )

        val newId = exerciseDAO.addExercise(newExercise)
        return if (newId != -1L) {
            newExercise.id = newId
            Toast.makeText(context, R.string.exercise_added, Toast.LENGTH_SHORT).show()
            onExerciseEditedListener.onExerciseSaved()

            createdListener?.onExerciseCreated(newExercise)
            true
        } else {
            Toast.makeText(context, R.string.add_failed, Toast.LENGTH_SHORT).show()
            false
        }
    }

    /**
     * Updates an existing exercise with new values
     *
     * @return true if update was successful, false otherwise
     */
    private fun updateExistingExercise(exercise: Exercise, name: String, description: String): Boolean {
        exercise.name = name
        exercise.description = description
        exercise.motion = preselectedMotion!!
        exercise.muscleGroupList = preselectedMuscleGroups
        exercise.equipment = preselectedEquipment!!

        return if (exerciseDAO.updateExercise(exercise)) {
            Toast.makeText(context, R.string.exercise_updated, Toast.LENGTH_SHORT).show()
            onExerciseEditedListener.onExerciseSaved()
            true
        } else {
            Toast.makeText(context, R.string.update_failed, Toast.LENGTH_SHORT).show()
            false
        }
    }

    /**
     * Shows the dialog with preselected filters
     *
     * Updated method for preselecting filters. Now accepts lists for Motion and Equipment,
     * but since Exercise uses single fields for these filters, we select the first element from the list.
     *
     * @param exercise Exercise to edit, or null for creating a new one
     * @param motions List of preselected motions (we'll use the first one)
     * @param muscleGroupList List of preselected muscle groups
     * @param equipmentList List of preselected equipment (we'll use the first one)
     */
    fun showWithPreselectedFilters(
        exercise: Exercise?,
        motions: List<Motion>?,
        muscleGroupList: List<MuscleGroup>?,
        equipmentList: List<Equipment>?
    ) {
        this.preselectedMotion = motions?.firstOrNull()
        this.preselectedMuscleGroups = muscleGroupList?.toMutableList() ?: ArrayList()
        this.preselectedEquipment = equipmentList?.firstOrNull()

        show(exercise)
    }
}