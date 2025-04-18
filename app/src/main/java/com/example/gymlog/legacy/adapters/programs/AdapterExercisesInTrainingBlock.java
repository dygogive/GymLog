package com.example.gymlog.legacy.adapters.programs;

import android.content.Context;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymlog.R;
import com.example.gymlog.domain.model.exercise.ExerciseInBlock;
import com.example.gymlog.data.local.legacy.PlanManagerDAO;

import java.util.Collections;
import java.util.List;

/**
 * Адаптер для списку вправ у конкретному тренувальному блоці.
 * Реалізує перетягування вправ тільки за іконку (довге натискання).
 */
public class AdapterExercisesInTrainingBlock extends RecyclerView.Adapter<AdapterExercisesInTrainingBlock.ViewHolder> {

    // Інтерфейс для кліку на вправу
    public interface ExerciseTouchListener {
        void onClick(ExerciseInBlock exercise);
    }

    // Інтерфейс для запуску перетягування (drag&drop)
    public interface OnLongPressImageListener {
        void onLongPressImage(RecyclerView.ViewHolder viewHolder);
    }

    private final Context context;
    private final List<ExerciseInBlock> exercises;
    private final ExerciseTouchListener clickListener;
    private final OnLongPressImageListener dragListener;

    public AdapterExercisesInTrainingBlock(Context context,
                                           List<ExerciseInBlock> exercises,
                                           ExerciseTouchListener clickListener,
                                           OnLongPressImageListener dragListener) {
        this.context = context;
        this.exercises = exercises;
        this.clickListener = clickListener;
        this.dragListener = dragListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_exercise_for_training_block, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExerciseInBlock exercise = exercises.get(position);
        holder.bind(exercise);
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    /**
     * Переміщує елементи списку вправ і оновлює їх позиції в БД.
     */
    public void moveItem(int fromPos, int toPos, PlanManagerDAO dao, long blockId) {
        Collections.swap(exercises, fromPos, toPos);

        // Оновлюємо індекси вправ після переміщення
        for (int i = 0; i < exercises.size(); i++) {
            exercises.get(i).setPosition(i);
        }

        dao.updateTrainingBlockExercises(blockId, exercises);
        notifyItemMoved(fromPos, toPos);
    }

    public List<ExerciseInBlock> getItems() {
        return exercises;
    }

    // ViewHolder, який представляє окрему вправу у списку
    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView nameExercise;
        final ImageButton buttonDragHandle;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameExercise = itemView.findViewById(R.id.nameExercise);
            buttonDragHandle = itemView.findViewById(R.id.buttonInfo);


        }

        void bind(ExerciseInBlock exercise) {
            nameExercise.setText(exercise.getNameOnly(context));
            setupListeners(exercise);
        }

        private void setupListeners(ExerciseInBlock exercise) {
            // Повністю вимикаємо стандартні кліки та звуки для кнопки
            buttonDragHandle.setClickable(false);
            buttonDragHandle.setLongClickable(true);
            buttonDragHandle.setHapticFeedbackEnabled(false);
            buttonDragHandle.setSoundEffectsEnabled(false);

            // Лише довге натискання активує перетягування
            buttonDragHandle.setOnLongClickListener(v -> {
                v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS); // вібрація
                dragListener.onLongPressImage(this); // запуск перетягування
                return true;
            });


            nameExercise.setOnClickListener(v -> clickListener.onClick(exercise));
        }
    }
}
