package com.example.gymlog.ui.programs.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymlog.R;
import com.example.gymlog.model.exercise.ExerciseInBlock;
import com.example.gymlog.sqlopenhelper.PlanManagerDAO;
import java.util.Collections;
import java.util.List;

public class AdapterExercisesInTrainingBlock extends RecyclerView.Adapter<AdapterExercisesInTrainingBlock.ViewHolder> {

    private final Context context;
    private final List<ExerciseInBlock> exercises;
    private final ExerciseListener exerciseListener;
    private final OnStartDragListener onStartDragListener;

    // Інтерфейс для кліку на елемент вправи
    public interface ExerciseListener {
        void onClick(ExerciseInBlock exercise);
    }

    // Інтерфейс для старту перетягування (натискання на drag handle)
    public interface OnStartDragListener {
        void onStartDrag(RecyclerView.ViewHolder viewHolder);
    }

    public AdapterExercisesInTrainingBlock(Context context, List<ExerciseInBlock> exercises,
                                           ExerciseListener exerciseListener, OnStartDragListener onStartDragListener) {
        this.context = context;
        this.exercises = exercises;
        this.exerciseListener = exerciseListener;
        this.onStartDragListener = onStartDragListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise_for_training_block, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExerciseInBlock exercise = exercises.get(position);
        holder.nameExercise.setText(exercise.getNameOnly(context));

        // Клік на назву вправи
        holder.nameExercise.setOnClickListener(v -> exerciseListener.onClick(exercise));


        // тільки цей слухач має бути, інших бути не повинно
        holder.buttonInfo.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                onStartDragListener.onStartDrag(holder);
                return true;
            }
            return false;
        });


    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    /**
     * Переміщує елемент у списку та оновлює позиції у базі.
     *
     * @param fromPosition Початкова позиція.
     * @param toPosition   Кінцева позиція.
     * @param dao          DAO для роботи з БД.
     * @param blockID      ID тренувального блоку.
     */
    public void moveItem(int fromPosition, int toPosition, PlanManagerDAO dao, long blockID) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(exercises, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(exercises, i, i - 1);
            }
        }
        // Оновлюємо позиції у списку
        for (int i = 0; i < exercises.size(); i++) {
            exercises.get(i).setPosition(i);
        }
        dao.updateTrainingBlockExercises(blockID, exercises);
        notifyItemMoved(fromPosition, toPosition);
    }

    public List<ExerciseInBlock> getItems() {
        return exercises;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameExercise;
        ImageButton buttonInfo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameExercise = itemView.findViewById(R.id.nameExercise);
            buttonInfo = itemView.findViewById(R.id.buttonInfo);
        }
    }
}
