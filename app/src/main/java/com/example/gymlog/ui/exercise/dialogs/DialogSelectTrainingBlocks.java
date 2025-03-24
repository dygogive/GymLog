package com.example.gymlog.ui.exercise.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.gymlog.R;
import com.example.gymlog.sqlopenhelper.PlanManagerDAO;
import com.example.gymlog.model.exercise.Exercise;
import com.example.gymlog.model.plan.TrainingBlock;

import java.util.ArrayList;
import java.util.List;

public class DialogSelectTrainingBlocks {


    public interface OnBlocksSelectedListener {
        void onBlocksSelected();
    }


    public static void addExerciseToBlocks(Context context, Exercise exercise, OnBlocksSelectedListener listener) {
        PlanManagerDAO planManagerDAO = new PlanManagerDAO(context);

        // Отримуємо тренувальні блоки, які відповідають фільтрам вправи
        List<TrainingBlock> allBlocks = planManagerDAO.getTrainingBlocksForExercise(exercise);

        if (allBlocks.isEmpty()) {
            Toast.makeText(context, R.string.no_training_blocks_available, Toast.LENGTH_SHORT).show();
            return;
        }

        // Масиви назв блоків і чекбоксів
        String[] blockNames = new String[allBlocks.size()];
        boolean[] checkedItems = new boolean[allBlocks.size()];

        for (int i = 0; i < allBlocks.size(); i++) {
            TrainingBlock block = allBlocks.get(i);

            // Отримуємо дані про програму і день
            String programName = planManagerDAO.getProgramNameByGymDayId(block.getGymDayId());
            String gymDayName = planManagerDAO.getGymDayNameById(block.getGymDayId());

            // Обрізаємо опис до 30 символів
            String description = block.getDescription();
            if (description == null) description = "";
            if (description.length() > 40) {
                description = description.substring(0, 40) + "...";
            }

            // Формуємо красивий рядок для чекбокса
            blockNames[i] = String.format("📌 %s\n📋 %s\n📅 %s ➝ %s\n",
                    block.getName(),   // Назва блоку
                    description,       // Короткий опис
                    programName,       // Назва програми
                    gymDayName         // День тренувальної програми
            );
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.add_exercise_to_blocks);

        // Додаємо мультивибірний список блоків з описами
        builder.setMultiChoiceItems(blockNames, checkedItems, (dialog, which, isChecked) -> {
            checkedItems[which] = isChecked;
        });

        builder.setPositiveButton(R.string.add, (dialog, which) -> {
            List<Long> selectedBlockIds = new ArrayList<>();
            for (int i = 0; i < allBlocks.size(); i++) {
                if (checkedItems[i]) {
                    selectedBlockIds.add(allBlocks.get(i).getId());
                }
            }

            if (!selectedBlockIds.isEmpty()) {
                for (long blockId : selectedBlockIds) {
                    planManagerDAO.addExerciseToBlock(blockId, exercise.getId());
                }
                Toast.makeText(context, R.string.exercise_added_to_selected_blocks, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, R.string.no_block_selected, Toast.LENGTH_SHORT).show();
            }

            if (listener != null) {
                listener.onBlocksSelected();
            }
        });

        builder.setNegativeButton("Скасувати", null);
        builder.show();
    }






    //альтернативний метод
    public static void show(Context context, Exercise exercise, OnBlocksSelectedListener listener) {
        PlanManagerDAO planManagerDAO = new PlanManagerDAO(context);

        Log.d("debug1", "1");

        // ✅ Переконуємося, що вправа збережена і має коректний ID
        if (exercise.getId() == -1) {
            Toast.makeText(context, "Помилка: Вправа ще не збережена", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("debug1", "2");
        List<TrainingBlock> allBlocks = planManagerDAO.getTrainingBlocksForExercise(exercise);
        Log.d("debug1", "3");
        if (allBlocks.isEmpty()) {
            Log.d("debug1", "4");
            Toast.makeText(context, R.string.no_training_blocks_available, Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("debug1", "5");

        String[] blockNames = new String[allBlocks.size()];
        boolean[] checkedItems = new boolean[allBlocks.size()];



        for (int i = 0; i < allBlocks.size(); i++) {
            TrainingBlock block = allBlocks.get(i);

            // Отримуємо дані про програму і день
            String programName = planManagerDAO.getProgramNameByGymDayId(block.getGymDayId());
            String gymDayName = planManagerDAO.getGymDayNameById(block.getGymDayId());

            // Обрізаємо опис до 30 символів
            String description = block.getDescription();
            if (description == null) description = "";
            if (description.length() > 30) {
                description = description.substring(0, 30) + "...";
            }

            // Формуємо красивий рядок для чекбокса
            blockNames[i] = String.format("📌 %s\n📋 %s\n📅 %s ➝ %s",
                    block.getName(),   // Назва блоку
                    description,       // Короткий опис
                    programName,       // Назва програми
                    gymDayName         // День тренувальної програми
            );
        }
        Log.d("debug1", "5");



        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.add_exercise_to_blocks);

        builder.setMultiChoiceItems(blockNames, checkedItems, (dialog, which, isChecked) -> {
            checkedItems[which] = isChecked;
        });
        Log.d("debug1", "6");
        //при натисканні кнопки Додати
        builder.setPositiveButton(R.string.add, (dialog, which) -> {
            List<Long> selectedBlockIds = new ArrayList<>();
            Log.d("debug1", "7");
            for (int i = 0; i < allBlocks.size(); i++) {
                if (checkedItems[i]) {
                    selectedBlockIds.add(allBlocks.get(i).getId());
                }
            }
            Log.d("debug1", "8");
            if (!selectedBlockIds.isEmpty()) {
                for (long blockId : selectedBlockIds) {
                    planManagerDAO.addExerciseToBlock(blockId, exercise.getId());
                }
                Toast.makeText(context, R.string.exercise_added_to_selected_blocks, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, R.string.no_block_selected, Toast.LENGTH_SHORT).show();
            }
            Log.d("debug1", "9");
            if (listener != null) {
                listener.onBlocksSelected();
            }
        });

        Log.d("debug1", "10");
        builder.setNegativeButton(R.string.do_not_add, null);
        Log.d("debug1", "11");
        builder.show();
    }

}
