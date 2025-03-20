package com.example.gymlog.ui.plan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymlog.R;
import com.example.gymlog.data.exercise.Exercise;
import com.example.gymlog.data.exercise.ExerciseInBlock;

import java.util.List;

/**
 * Адаптер для відображення списку вправ (Exercise) всередині одного TrainingBlock.
 * Використовується в TrainingBlockAdapter (у внутрішньому RecyclerView).
 */
public class AdapterExercisesInTrainingBlock
        extends RecyclerView.Adapter<AdapterExercisesInTrainingBlock.ViewHolder> {

    // Список вправ, поточний context, і слухач (на клік інформації)
    private final List<ExerciseInBlock> exercises;
    private final Context context;
    private final ExerciseListener listener;

    /**
     * Інтерфейс, що обробляє клік на конкретну вправу
     */
    public interface ExerciseListener {
        void onClickListener(Exercise exercise);
    }

    /**
     * Конструктор, де передаємо:
     * @param context   – поточний Context
     * @param exercises – список вправ
     * @param listener  – слухач натискання на кнопку інформації
     */
    public AdapterExercisesInTrainingBlock(
            Context context,
            List<ExerciseInBlock> exercises,
            ExerciseListener listener
    ) {
        this.exercises = exercises;
        this.listener = listener;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Інфлюємо item_exercise_for_training_block.xml
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise_for_training_block, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Зв’язуємо поля ViewHolder із даними конкретної вправи.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Отримуємо вправу
        ExerciseInBlock exercise = exercises.get(position);

        // Встановлюємо назву (локалізовану чи кастомну)
        holder.nameExercise.setText(exercise.getNameOnly(context));

        // На кнопку info вішаємо виклик listener.onClickListener(...)
        holder.buttonInfo.setOnClickListener(v -> listener.onClickListener(exercise));
    }

    @Override
    public int getItemCount() {
        return (exercises != null) ? exercises.size() : 0;
    }

    /**
     * ViewHolder для одного елемента списку вправ:
     *  - nameExercise: TextView з назвою вправи
     *  - buttonInfo: ImageButton для показу додаткової інформації.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameExercise;
        ImageButton buttonInfo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            buttonInfo   = itemView.findViewById(R.id.buttonInfo);   // кнопка info
            nameExercise = itemView.findViewById(R.id.nameExercise); // назва вправи
        }
    }
}
