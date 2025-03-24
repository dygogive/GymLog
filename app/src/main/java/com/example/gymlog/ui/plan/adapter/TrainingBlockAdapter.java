package com.example.gymlog.ui.plan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymlog.R;
import com.example.gymlog.sqlopenhelper.PlanManagerDAO;
import com.example.gymlog.model.exercise.ExerciseInBlock;
import com.example.gymlog.model.plan.TrainingBlock;
import java.util.ArrayList;
import java.util.List;

/**
 * Адаптер для відображення списку тренувальних блоків (TrainingBlock).
 * Відповідає за створення, оновлення та взаємодію з елементами списку.
 */
public class TrainingBlockAdapter extends RecyclerView.Adapter<TrainingBlockAdapter.TrainingBlockViewHolder> {

    // Інтерфейс для обробки подій на тренувальних блоках
    public interface OnTrainingBlockClickListener {
        void onEditClick(TrainingBlock block); // Редагувати блок
        void onDeleteClick(TrainingBlock block); // Видалити блок
        void onAddExercise(TrainingBlock block); // Додати вправу
        void onEditExercises(TrainingBlock block); // Редагувати вправи
    }

    private final Context context; // Контекст для доступу до ресурсів
    private final List<TrainingBlock> trainingBlocks; // Список тренувальних блоків
    private final PlanManagerDAO planManagerDAO; // DAO для роботи з базою даних
    private final OnTrainingBlockClickListener listener; // Обробник подій

    /**
     * Конструктор адаптера.
     *
     * @param context          Контекст.
     * @param trainingBlocks   Список тренувальних блоків.
     * @param planManagerDAO   DAO для роботи з базою даних.
     * @param listener         Обробник подій для блоків.
     */
    public TrainingBlockAdapter(
            Context context,
            List<TrainingBlock> trainingBlocks,
            PlanManagerDAO planManagerDAO,
            OnTrainingBlockClickListener listener
    ) {
        this.context = context;
        this.trainingBlocks = trainingBlocks != null ? trainingBlocks : new ArrayList<>();
        this.planManagerDAO = planManagerDAO;
        this.listener = listener;
        setHasStableIds(true); // Увімкнення стабільних ідентифікаторів для уникнення "блимання"
    }

    /**
     * Створює новий ViewHolder для елемента списку.
     */
    @NonNull
    @Override
    public TrainingBlockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_training_block, parent, false);
        return new TrainingBlockViewHolder(view);
    }

    /**
     * Прив'язує дані до ViewHolder на певній позиції.
     */
    @Override
    public void onBindViewHolder(@NonNull TrainingBlockViewHolder holder, int position) {
        TrainingBlock block = trainingBlocks.get(position);

        // Встановлюємо назву та опис блоку
        holder.textViewBlockName.setText(block.getName());
        holder.textViewBlockDescription.setText(block.getDescription());

        // Налаштування контекстного меню для блоку
        setupBlockMenu(holder, block);

        // Завантаження та відображення списку вправ у блоці
        setupExercisesRecyclerView(holder, block);
    }

    /**
     * Налаштовує контекстне меню для блоку.
     */
    private void setupBlockMenu(TrainingBlockViewHolder holder, TrainingBlock block) {
        holder.buttonMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.buttonMenu);
            popupMenu.getMenuInflater().inflate(R.menu.training_block_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_edit_block) {
                    listener.onEditClick(block); // Редагувати блок
                } else if (item.getItemId() == R.id.menu_delete_block) {
                    listener.onDeleteClick(block); // Видалити блок
                } else if (item.getItemId() == R.id.menu_add_exercise) {
                    listener.onAddExercise(block); // Додати вправу
                } else if (item.getItemId() == R.id.menu_edit_exercises) {
                    listener.onEditExercises(block); // Редагувати вправи
                }
                return true;
            });

            popupMenu.show();
        });
    }

    /**
     * Налаштовує RecyclerView для відображення вправ у блоці.
     */
    private void setupExercisesRecyclerView(TrainingBlockViewHolder holder, TrainingBlock block) {
        List<ExerciseInBlock> exercises = planManagerDAO.getBlockExercises(block.getId());

        // Створення адаптера для вправ
        AdapterExercisesInTrainingBlock exerciseAdapter = new AdapterExercisesInTrainingBlock(
                context,
                exercises,
                exercise -> Toast.makeText(context, exercise.getNameOnly(context), Toast.LENGTH_SHORT).show()
        );

        holder.recyclerViewExercises.setAdapter(exerciseAdapter);

        // Налаштування drag & drop для вправ
        setupItemTouchHelper(holder, block, exerciseAdapter);
    }

    /**
     * Налаштовує ItemTouchHelper для переміщення та видалення вправ.
     */
    private void setupItemTouchHelper(TrainingBlockViewHolder holder, TrainingBlock block, AdapterExercisesInTrainingBlock exerciseAdapter) {
        if (holder.itemTouchHelper != null) {
            holder.itemTouchHelper.attachToRecyclerView(null); // Від'єднати попередній ItemTouchHelper
        }

        ItemTouchHelper.Callback callback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN, // Дозволяємо переміщення вгору/вниз
                ItemTouchHelper.LEFT // Дозволяємо свайп вліво для видалення
        ) {
            @Override
            public boolean onMove(@NonNull RecyclerView rv, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getBindingAdapterPosition();
                int toPosition = target.getBindingAdapterPosition();
                exerciseAdapter.moveItem(fromPosition, toPosition, planManagerDAO, block.getId()); // Оновити позиції
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.LEFT) {
                    int pos = viewHolder.getBindingAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        ExerciseInBlock removedExercise = exerciseAdapter.getItems().get(pos);

                        // Видалити вправу з бази даних
                        planManagerDAO.removeExerciseFromBlock(block.getId(), removedExercise.getId(), removedExercise.getPosition());

                        // Видалити вправу з локального списку
                        exerciseAdapter.getItems().remove(pos);
                        exerciseAdapter.notifyItemRemoved(pos);

                        // Оновити позиції решти вправ
                        for (int i = pos; i < exerciseAdapter.getItems().size(); i++) {
                            exerciseAdapter.getItems().get(i).setPosition(i);
                        }
                        planManagerDAO.updateTrainingBlockExercises(block.getId(), exerciseAdapter.getItems());
                    }
                }
            }
        };

        holder.itemTouchHelper = new ItemTouchHelper(callback);
        holder.itemTouchHelper.attachToRecyclerView(holder.recyclerViewExercises);
    }

    /**
     * Повертає унікальний ідентифікатор для елемента на позиції.
     */
    @Override
    public long getItemId(int position) {
        return trainingBlocks.get(position).getId();
    }

    /**
     * Повертає кількість елементів у списку.
     */
    @Override
    public int getItemCount() {
        return trainingBlocks.size();
    }

    /**
     * Переміщує елемент у списку (для drag & drop між блоками).
     */
    public void moveItem(int fromPosition, int toPosition) {
        TrainingBlock removed = trainingBlocks.remove(fromPosition);
        trainingBlocks.add(toPosition, removed);
        notifyItemMoved(fromPosition, toPosition);
    }

    /**
     * ViewHolder для одного тренувального блоку.
     */
    public static class TrainingBlockViewHolder extends RecyclerView.ViewHolder {
        final TextView textViewBlockName; // Назва блоку
        final TextView textViewBlockDescription; // Опис блоку
        final ImageButton buttonMenu; // Кнопка меню
        final RecyclerView recyclerViewExercises; // Список вправ
        ItemTouchHelper itemTouchHelper; // Для drag & drop вправ

        public TrainingBlockViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewBlockName = itemView.findViewById(R.id.textViewBlockName);
            textViewBlockDescription = itemView.findViewById(R.id.textViewBlockDescription);
            buttonMenu = itemView.findViewById(R.id.buttonMenu);
            recyclerViewExercises = itemView.findViewById(R.id.recyclerViewExercises);
            recyclerViewExercises.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        }
    }
}

/**
 * Конструктор (TrainingBlockAdapter):
     *
     * Приймає контекст, список тренувальних блоків, DAO для роботи з базою даних та обробник подій.
     *
     * Ініціалізує поля класу та увімкнення стабільних ідентифікаторів.
     *
 * Метод onCreateViewHolder:
     *
     * Створює новий ViewHolder для елемента списку, використовуючи XML-розмітку (item_training_block).
     *
 * Метод onBindViewHolder:
     *
     * Прив'язує дані до елемента списку на певній позиції.
     *
     * Встановлює назву та опис блоку, налаштовує контекстне меню та список вправ.
     *
 * Метод setupBlockMenu:
     *
     * Налаштовує контекстне меню для блоку з опціями редагування, видалення, додавання вправ тощо.
     *
 * Метод setupExercisesRecyclerView:
     *
     * Завантажує список вправ для блоку та налаштовує адаптер для їх відображення.
     *
 * Метод setupItemTouchHelper:
     *
     * Налаштовує можливість переміщення та видалення вправ за допомогою drag & drop.
     *
 * Метод moveItem:
     *
     * Переміщує блок у списку (використовується для drag & drop між блоками).
     *
 * Клас TrainingBlockViewHolder:
     *
     * Зберігає посилання на елементи UI (назва блоку, опис, кнопка меню, список вправ).
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
         * ItemTouchHelper: Дозволяє реалізувати drag & drop та свайп для елементів списку.
 */