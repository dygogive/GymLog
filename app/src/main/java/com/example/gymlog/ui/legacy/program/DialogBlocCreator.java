package com.example.gymlog.ui.legacy.program;

import androidx.appcompat.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.example.gymlog.R;
import com.example.gymlog.data.local.legacy.PlanManagerDAO;
import com.example.gymlog.domain.model.legacy.attribute.equipment.Equipment;
import com.example.gymlog.domain.model.legacy.exercise.Exercise;
import com.example.gymlog.domain.model.legacy.exercise.ExerciseInBlock;
import com.example.gymlog.domain.model.legacy.attribute.motion.Motion;
import com.example.gymlog.domain.model.legacy.attribute.muscle.MuscleGroup;
import com.example.gymlog.domain.model.legacy.plan.TrainingBlock;
import com.example.gymlog.ui.legacy.dialogs.DialogStyler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * DialogBlocCreator – діалог для створення/редагування блоку тренування.
 * Дозволяє задати назву, опис та вибрати фільтри (рухи, м’язи, обладнання) для блоку.
 */
public class DialogBlocCreator extends Dialog {

    // Контекст виклику
    private final Context context;
    // Елементи UI: поля введення для назви та опису блоку
    private EditText editTextBlockName, editTextBlockDescription;
    // Кнопки для вибору фільтрів
    private Button buttonSelectMotion, buttonSelectMuscle, buttonSelectEquipment;
    // Логічні масиви для збереження стану вибраних елементів у діалогах
    private boolean[] booleansMotions, booleansMuscles, booleansEquipment;
    // Списки для збереження текстових описів обраних фільтрів
    private final List<String> chosenTxtMotions = new ArrayList<>();
    private final List<String> chosenTxtMuscles = new ArrayList<>();
    private final List<String> chosenTxtEquipment = new ArrayList<>();

    // DAO для роботи з планами тренувань (базові операції з БД)
    private PlanManagerDAO planManagerDAO;
    // Об'єкт блоку тренування (якщо редагується)
    private TrainingBlock trainingBlock;
    // Набір ID вправ, які не треба додавати (для blacklist)
    private final Set<Long> idExercisesBlacklist = new HashSet<>();

    // Callback для сповіщення про створення/редагування блоку
    private final OnTrainingBlockCreatedListener listener;
    // ID дня тренувань, до якого буде прикріплено блок
    private final long gymDayId;

    /**
     * Інтерфейс зворотного виклику для повідомлення про успішне створення/редагування блоку.
     */
    public interface OnTrainingBlockCreatedListener {
        void onBlockAdded();
    }

    /**
     * Конструктор для створення нового блоку тренування.
     *
     * @param context Контекст.
     * @param gymDayId ID дня тренувань.
     * @param listener Callback для сповіщення.
     */
    public DialogBlocCreator(@NonNull Context context, long gymDayId, OnTrainingBlockCreatedListener listener) {
        super(context, R.style.RoundedDialogTheme2);
        this.context = context;
        this.gymDayId = gymDayId;
        this.listener = listener;
    }

    /**
     * Конструктор для редагування існуючого блоку тренування.
     *
     * @param context Контекст.
     * @param gymDayId ID дня тренувань.
     * @param block Існуючий блок тренування.
     * @param listener Callback для сповіщення.
     */
    public DialogBlocCreator(@NonNull Context context, long gymDayId, TrainingBlock block, OnTrainingBlockCreatedListener listener) {
        super(context, R.style.RoundedDialogTheme2);
        this.context = context;
        this.gymDayId = gymDayId;
        this.trainingBlock = block;
        this.listener = listener;
    }

    /**
     * Метод створення діалогу. Ініціалізує UI, DAO та завантажує дані блоку, якщо він існує.
     */
    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        // Встановлюємо layout діалогу
        setContentView(R.layout.dialog_training_block);

        // Ініціалізація DAO для роботи з планами
        planManagerDAO = new PlanManagerDAO(getContext());

        // Налаштування параметрів вікна діалогу
        if (getWindow() != null) {
            getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        }

        // Ініціалізація UI компонентів та фільтрів
        initUI();
        Log.d("openBlockDialog", "initUI: 10");
        initFilters();
        Log.d("openBlockDialog", "initUI: 11" + (trainingBlock != null));

        // Якщо редагується існуючий блок, завантажити його дані
        if (trainingBlock != null) {
            loadBlockData();
        }

    }

    /**
     * Ініціалізація UI компонентів діалогу.
     */
    private void initUI() {
        // Знаходимо поля введення за ID
        editTextBlockName = findViewById(R.id.editTextBlockName);
        editTextBlockDescription = findViewById(R.id.editTextBlockDescription);
        Log.d("openBlockDialog", "initUI: 1");
        // Встановлюємо обмеження на кількість символів
        editTextBlockName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
        editTextBlockDescription.setFilters(new InputFilter[]{new InputFilter.LengthFilter(300)});
        Log.d("openBlockDialog", "initUI: 2");
        // Знаходимо кнопки для вибору фільтрів та дії
        buttonSelectMotion = findViewById(R.id.buttonSelectMotion);
        buttonSelectMuscle = findViewById(R.id.buttonSelectMuscle);
        buttonSelectEquipment = findViewById(R.id.buttonSelectEquipment);
        Button buttonCancel = findViewById(R.id.buttonCancel);
        Button buttonSaveBlock = findViewById(R.id.buttonSaveBlock);
        Log.d("openBlockDialog", "initUI: 3");
        // Зберігаємо базовий текст кнопок (для подальшого оновлення тексту)
        buttonSelectMotion.setTag(buttonSelectMotion.getText().toString());
        buttonSelectMuscle.setTag(buttonSelectMuscle.getText().toString());
        buttonSelectEquipment.setTag(buttonSelectEquipment.getText().toString());
        Log.d("openBlockDialog", "initUI: 4");
        // Призначення обробників кліків для відкриття діалогів мультивибору
        buttonSelectMotion.setOnClickListener(v ->
                showMultiSelectDialog("Оберіть рухи", Motion.getAllDescriptions(getContext()), booleansMotions, chosenTxtMotions, buttonSelectMotion)
        );
        Log.d("openBlockDialog", "initUI: 5");
        buttonSelectMuscle.setOnClickListener(v ->
                showMultiSelectDialog("Оберіть м’язи", MuscleGroup.getAllDescriptions(getContext()), booleansMuscles, chosenTxtMuscles, buttonSelectMuscle)
        );
        Log.d("openBlockDialog", "initUI: 6");
        buttonSelectEquipment.setOnClickListener(v ->
                showMultiSelectDialog("Оберіть обладнання", Equipment.getEquipmentDescriptions(getContext()), booleansEquipment, chosenTxtEquipment, buttonSelectEquipment)
        );
        Log.d("openBlockDialog", "initUI: 7");
        // Обробники для кнопок "Скасувати" та "Зберегти"
        buttonCancel.setOnClickListener(v -> dismiss());
        buttonSaveBlock.setOnClickListener(v -> saveTrainingBlock());
        Log.d("openBlockDialog", "initUI: 8");
        // Стилізація кнопок діалогу
        DialogStyler.styleButtonsInDialog(context, buttonCancel, buttonSaveBlock);
        Log.d("openBlockDialog", "initUI: 9");
    }

    /**
     * Ініціалізація логічних масивів для фільтрів.
     */
    private void initFilters() {
        booleansMotions = new boolean[Motion.values().length];
        booleansMuscles = new boolean[MuscleGroup.values().length];
        booleansEquipment = new boolean[Equipment.values().length];
    }

    /**
     * Завантаження даних існуючого блоку тренування у UI.
     */
    private void loadBlockData() {
        // Встановлення тексту з існуючого блоку
        editTextBlockName.setText(trainingBlock.getName());
        editTextBlockDescription.setText(trainingBlock.getDescription());
        Log.d("openBlockDialog", "initUI: 12");
        // Отримання збережених фільтрів з БД
        List<String> savedMotionsInDB = planManagerDAO.getTrainingBlockFilters(trainingBlock.getId(), "motionType");
        Log.d("openBlockDialog", "initUI: 13");
        List<String> savedMusclesInDB = planManagerDAO.getTrainingBlockFilters(trainingBlock.getId(), "muscleGroup");
        Log.d("openBlockDialog", "initUI: 14");
        List<String> savedEquipmentInDB = planManagerDAO.getTrainingBlockFilters(trainingBlock.getId(), "equipment");
        Log.d("openBlockDialog", "initUI: 15");

        // Оновлення локальних виборів на основі збережених даних
        updateSelections(savedMotionsInDB, chosenTxtMotions, booleansMotions, Motion.values(), Motion::getDescription);
        updateSelections(savedMusclesInDB, chosenTxtMuscles, booleansMuscles, MuscleGroup.values(), MuscleGroup::getDescription);
        updateSelections(savedEquipmentInDB, chosenTxtEquipment, booleansEquipment, Equipment.values(), Equipment::getDescription);
        Log.d("openBlockDialog", "initUI: 14");
        // Оновлення тексту кнопок із зазначенням кількості вибраних фільтрів
        updateButtonText(buttonSelectMotion, chosenTxtMotions);
        updateButtonText(buttonSelectMuscle, chosenTxtMuscles);
        updateButtonText(buttonSelectEquipment, chosenTxtEquipment);
        Log.d("openBlockDialog", "initUI: 15");
    }

    /**
     * Оновлення вибраних елементів фільтра.
     *
     * @param savedEnumNamesInDB Список збережених назв enum з БД.
     * @param chosenItems Список для збереження описів вибраних елементів.
     * @param selectedBooleans Логічний масив, що відображає вибір.
     * @param enums Масив enum елементів.
     * @param descriptionFunc Функція отримання опису enum.
     */
    private <T extends Enum<T>> void updateSelections(List<String> savedEnumNamesInDB, List<String> chosenItems, boolean[] selectedBooleans, T[] enums, BiFunction<T, Context, String> descriptionFunc) {
        // Очищення списку та скидання стану вибору
        chosenItems.clear();
        Arrays.fill(selectedBooleans, false);
        // Проходимо через всі enum-елементи
        for (int i = 0; i < enums.length; i++) {
            if (savedEnumNamesInDB.contains(enums[i].name())) {
                selectedBooleans[i] = true;
                chosenItems.add(descriptionFunc.apply(enums[i], context));
            }
        }
    }

    /**
     * Оновлює текст кнопки вибору фільтра, додаючи кількість вибраних елементів.
     *
     * @param button Кнопка, текст якої оновлюється.
     * @param selectedItems Список вибраних елементів.
     */
    private void updateButtonText(Button button, List<String> selectedItems) {
        String baseText = button.getTag().toString();
        button.setText(!selectedItems.isEmpty() ? baseText + " (" + selectedItems.size() + ")" : baseText);
    }

    /**
     * Збереження блоку тренування та оновлення пов’язаних даних.
     */
    private void saveTrainingBlock() {
        // Отримання введених даних
        String name = editTextBlockName.getText().toString().trim();
        String description = editTextBlockDescription.getText().toString().trim();

        // Перевірка, що ім'я не порожнє
        if (name.isEmpty()) {
            editTextBlockName.setError(context.getString(R.string.set_name));
            return;
        }

        long blockId;
        if (trainingBlock == null) {
            // Створення нового блоку тренування
            TrainingBlock block = new TrainingBlock(
                    0,
                    gymDayId,
                    name,
                    description,
                    new ArrayList<>(),
                    UUID.randomUUID().toString()
            );

            blockId = planManagerDAO.addTrainingBlock(block);
            trainingBlock = block;
            trainingBlock.setId(blockId);
        } else {
            // Оновлення існуючого блоку тренування
            trainingBlock.setName(name);
            trainingBlock.setDescription(description);
            planManagerDAO.updateTrainingBlock(trainingBlock);
            blockId = trainingBlock.getId();

            // Оновлення чорного списку ID вправ
            List<Exercise> recommendedExes = planManagerDAO.recommendExercisesForTrainingBlock(blockId);
            List<ExerciseInBlock> blockExercises = planManagerDAO.getBlockExercises(blockId);
            idExercisesBlacklist.clear();
            Set<Long> oldSelectedIds = new HashSet<>();
            for (ExerciseInBlock ex : blockExercises) {
                oldSelectedIds.add(ex.getId());
            }
            for (Exercise ex : recommendedExes) {
                if (!oldSelectedIds.contains(ex.getId())) {
                    idExercisesBlacklist.add(ex.getId());
                }
            }
        }

        // Очищення попередніх фільтрів у БД та збереження нових
        planManagerDAO.clearTrainingBlockFilters(blockId);
        saveFilters(blockId);

        // Оновлення вправ у блоці на основі рекомендованих
        List<Exercise> recommendedExes = planManagerDAO.recommendExercisesForTrainingBlock(blockId);
        List<ExerciseInBlock> exerciseInBlockList = new ArrayList<>();
        int position = 0;
        for (Exercise recommended : recommendedExes) {
            if (!idExercisesBlacklist.contains(recommended.getId())) {
                exerciseInBlockList.add(new ExerciseInBlock(-1, recommended.getId(), recommended.getName(),
                        recommended.getDescription(), recommended.getMotion(), recommended.getMuscleGroupList(),
                        recommended.getEquipment(), position++));
            }
        }

        // Оновлення вправ блоку в БД та сповіщення через callback
        try {
            planManagerDAO.updateTrainingBlockExercises(blockId, exerciseInBlockList);
            if (listener != null)  {
                listener.onBlockAdded();
            }
            dismiss();
        } catch (Exception e) {
            Log.e("DB_CRASH", "Помилка при оновленні вправ у блоці", e);
        }
    }

    /**
     * Збереження обраних фільтрів блоку тренування в БД.
     *
     * @param blockId ID блоку тренування.
     */
    private void saveFilters(long blockId) {
        saveFilterItems(blockId, chosenTxtMotions, motionText -> Motion.getObjectByDescription(context, motionText), "motionType");
        saveFilterItems(blockId, chosenTxtMuscles, muscleText -> MuscleGroup.getObjectByDescription(context, muscleText), "muscleGroup");
        saveFilterItems(blockId, chosenTxtEquipment, equipmentText -> Equipment.getEquipmentByDescription(context, equipmentText), "equipment");
    }

    /**
     * Збереження окремої групи фільтрів у БД.
     *
     * @param blockId ID блоку тренування.
     * @param chosenItems Список вибраних елементів.
     * @param converter Функція конвертації тексту у відповідний enum.
     * @param filterType Тип фільтра (motionType, muscleGroup, equipment).
     */
    private <E extends Enum<E>> void saveFilterItems(long blockId, List<String> chosenItems, Function<String, E> converter, String filterType) {
        for (String itemText : chosenItems) {
            E enumValue = converter.apply(itemText);
            if (enumValue != null) {
                planManagerDAO.addTrainingBlockFilter(blockId, filterType, enumValue.name());
            }
        }
    }

    /**
     * Відображення діалогу мультивибору для фільтрів.
     *
     * @param title Заголовок діалогу.
     * @param allItems Массив усіх можливих значень (описів).
     * @param booleansOfItems Логічний масив, що відображає стан вибору для кожного елементу.
     * @param selectedItems Список для збереження вибраних елементів.
     * @param button Кнопка, текст якої буде оновлено після вибору.
     */
    private void showMultiSelectDialog(String title, String[] allItems, boolean[] booleansOfItems, List<String> selectedItems, Button button) {
        AlertDialog dialog = new AlertDialog.Builder(getContext(), R.style.RoundedDialogTheme2)
                .setTitle(title)
                .setMultiChoiceItems(allItems, booleansOfItems, (d, which, isChecked) -> {
                    // Оновлення стану вибору для відповідного елементу
                    booleansOfItems[which] = isChecked;
                    if (isChecked) {
                        selectedItems.add(allItems[which]);
                    } else {
                        selectedItems.remove(allItems[which]);
                    }
                })
                .setPositiveButton("OK", (d, which) -> updateButtonText(button, selectedItems))
                .setNegativeButton("Скасувати", (d, which) -> d.dismiss())
                .create();
        // Стилізація діалогу перед показом
        dialog.setOnShowListener(d -> DialogStyler.applyAlertDialogStyle(context, dialog));
        dialog.show();
    }
}
