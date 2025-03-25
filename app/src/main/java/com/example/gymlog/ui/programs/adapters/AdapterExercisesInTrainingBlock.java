package com.example.gymlog.ui.programs.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymlog.R;
import com.example.gymlog.sqlopenhelper.PlanManagerDAO;
import com.example.gymlog.model.exercise.ExerciseInBlock;
import java.util.Collections;
import java.util.List;

/**
 * Адаптер для відображення списку вправ у тренувальному блоці.
 * Відповідає за створення, оновлення та взаємодію з елементами списку.
 */
public class AdapterExercisesInTrainingBlock extends RecyclerView.Adapter<AdapterExercisesInTrainingBlock.ViewHolder> {

    private final Context context; // Контекст для доступу до ресурсів
    private final List<ExerciseInBlock> exercises; // Список вправ у блоці
    private final ExerciseListener exerciseListener; // Обробник подій для вправ

    /**
     * Інтерфейс для обробки кліків на вправи.
     */
    public interface ExerciseListener {
        void onClickListener(ExerciseInBlock exercise);
    }

    /**
     * Конструктор адаптера.
     *
     * @param context          Контекст.
     * @param exercises        Список вправ.
     * @param exerciseListener Обробник подій для вправ.
     */
    public AdapterExercisesInTrainingBlock(Context context, List<ExerciseInBlock> exercises, ExerciseListener exerciseListener) {
        this.context = context;
        this.exercises = exercises;
        this.exerciseListener = exerciseListener;
    }

    /**
     * Повертає список вправ.
     */
    public List<ExerciseInBlock> getItems() {
        return exercises;
    }

    /**
     * Створює новий ViewHolder для елемента списку.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise_for_training_block, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Прив'язує дані до ViewHolder на певній позиції.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExerciseInBlock exercise = exercises.get(position);

        // Встановлюємо назву вправи
        holder.nameExercise.setText(exercise.getNameOnly(context));

        // Обробник кліку на кнопку "Інформація"
        holder.buttonInfo.setOnClickListener(v -> exerciseListener.onClickListener(exercise));
    }

    /**
     * Повертає кількість елементів у списку.
     */
    @Override
    public int getItemCount() {
        return exercises != null ? exercises.size() : 0;
    }

    /**
     * Переміщує елемент у списку та оновлює позиції в базі даних.
     *
     * @param fromPosition Початкова позиція елемента.
     * @param toPosition   Кінцева позиція елемента.
     * @param dao          DAO для роботи з базою даних.
     * @param blockID      Ідентифікатор тренувального блоку.
     */
    public void moveItem(int fromPosition, int toPosition, PlanManagerDAO dao, long blockID) {
        // Переміщення елемента у списку
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(exercises, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(exercises, i, i - 1);
            }
        }

        // Оновлення позицій у списку
        for (int i = 0; i < exercises.size(); i++) {
            exercises.get(i).setPosition(i);
        }

        // Оновлення даних у базі
        dao.updateTrainingBlockExercises(blockID, exercises);
        notifyItemMoved(fromPosition, toPosition); // Сповіщення адаптера про зміну
    }

    /**
     * ViewHolder для елемента списку вправ.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameExercise; // Назва вправи
        ImageButton buttonInfo; // Кнопка "Інформація"

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameExercise = itemView.findViewById(R.id.nameExercise);
            buttonInfo = itemView.findViewById(R.id.buttonInfo);
        }
    }
}

/**
 * Основні пояснення:
 * Конструктор (AdapterExercisesInTrainingBlock):
     *
     *      Приймає контекст, список вправ і обробник подій для вправ.
     *
     *      Ініціалізує поля класу.
     *
 * Метод onCreateViewHolder:
     *
     * Створює новий ViewHolder для елемента списку, використовуючи XML-розмітку (item_exercise_for_training_block).
     *
 * Метод onBindViewHolder:
     *
             * Прив'язує дані до елемента списку на певній позиції.
             *
             * Встановлює назву вправи та обробник кліку на кнопку "Інформація".
     *
 * Метод getItemCount:
     *
     * Повертає кількість елементів у списку вправ.
     *
 * Метод moveItem:
     *
     * Переміщує елемент у списку з однієї позиції на іншу.
     *
     * Оновлює позиції в базі даних та сповіщає адаптер про зміну.
     *
 * Клас ViewHolder:
     *
     * Зберігає посилання на елементи UI (назва вправи та кнопка "Інформація").
     *
     * Використовується для оптимізації роботи з RecyclerView.
     *
 * Для новачків:
     * RecyclerView.Adapter: Відповідає за створення та оновлення елементів списку.
     *
     * ViewHolder: Зберігає посилання на елементи UI для швидкого доступу.
     *
     * onBindViewHolder: Викликається для прив'язки даних до елемента списку.
     *
     * moveItem: Дозволяє змінювати порядок елементів у списку та оновлювати дані в базі.
     *
 */