package com.example.gymlog.data.local.legacy;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.example.gymlog.R;
import com.example.gymlog.domain.model.attribute.AttributeFilter;
import com.example.gymlog.domain.model.attribute.equipment.Equipment;
import com.example.gymlog.domain.model.exercise.Exercise;
import com.example.gymlog.domain.model.attribute.motion.Motion;
import com.example.gymlog.domain.model.attribute.muscle.MuscleGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * DAO (Data Access Object) для роботи з таблицею "Exercise":
 * - Додавання/оновлення/видалення вправ
 * - Пошук за атрибутами (MotioStateList, MuscleGroup, EquipmentStateList)
 * - Отримання всіх вправ
 */
public class ExerciseDAO {

    // База даних (readable / writable) + контекст

    private final DBHelper dbHelper;
    Context context;


    /**
     * Конструктор, що створює DAO на базі DBHelper.
     * Ми беремо readableDatabase, але можна й writable (залежить від операцій).
     *
     * @param context Поточний контекст
     */
    public ExerciseDAO(Context context) {
        this.dbHelper = new DBHelper(context);
        this.context = context;
    }

    /**
     * Додає вправу в таблицю "Exercise".
     *
     * @param exerciseName Назва вправи
     * @param description  Опис вправи
     * @param motion       Рух (MotioStateList)
     * @param muscleGroups Список м'язевих груп
     * @param equipment    Обладнання
     * @param isCustom     true, якщо вправа створена користувачем
     * @return ID доданого запису (або -1, якщо сталася помилка)
     */
    public long addExercise(
            String exerciseName,
            String description,
            Motion motion,
            List<MuscleGroup> muscleGroups,
            Equipment equipment,
            boolean isCustom
    ) {
        try (SQLiteDatabase database = dbHelper.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put("name", exerciseName);
            values.put("description", description);
            values.put("motioState", motion.name());
            values.put(
                    "muscleGroups",
                    TextUtils.join(
                            ",",
                            muscleGroups.stream()
                                    .map(Enum::name)
                                    .toArray(String[]::new)
                    )
            );
            values.put("equipmentState", equipment.name());
            values.put("isCustom", isCustom ? 1 : 0);

            // Вставляємо запис
            return database.insert("Exercise", null, values);
        }
    }

    /**
     * Перевантажений метод для додавання вправи через об'єкт Exercise.
     * Позначаємо як кастомну за замовчуванням.
     */
    public long addExercise(Exercise exercise) {
        return addExercise(
                exercise.getName(),
                exercise.getDescription(),
                exercise.getMotion(),
                exercise.getMuscleGroupList(),
                exercise.getEquipment(),
                true
        );
    }

    /**
     * Оновлюємо дані вправи (Exercise).
     * Вважаємо, що ця вправа була створена користувачем (isCustom = 1).
     *
     * @param exercise Об’єкт вправи
     * @return true, якщо оновлено успішно
     */
    public boolean updateExercise(Exercise exercise) {
        int rowsAffected;
        try (SQLiteDatabase database = dbHelper.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put("name", exercise.getName());
            values.put("description", exercise.getDescription());
            values.put("motioState", exercise.getMotion().name());
            values.put(
                    "muscleGroups",
                    TextUtils.join(
                            ",",
                            exercise.getMuscleGroupList()
                                    .stream()
                                    .map(Enum::name)
                                    .toArray(String[]::new)
                    )
            );
            values.put("equipmentState", exercise.getEquipment().name());
            values.put("isCustom", 1); // позначаємо як кастомну

            // Оновлюємо запис за ID вправи
            rowsAffected = database.update(
                    "Exercise",
                    values,
                    "id = ?",
                    new String[]{String.valueOf(exercise.getId())}
            );
        }

        return rowsAffected > 0;
    }

    /**
     * Отримуємо список усіх вправ із таблиці "Exercise".
     */
    public List<Exercise> getAllExercises() {
        try (SQLiteDatabase database = dbHelper.getWritableDatabase()) {
            // Простий запит SELECT * FROM Exercise
            return getExercisesFromCursor(
                    database.query("Exercise", null, null, null, null, null, null)
            );
        }
    }

    /**
     * Пошук вправ за певним атрибутом (MotioStateList, MuscleGroup, EquipmentStateList).
     */
    public List<Exercise> getExercisesByAttribute(AttributeFilter attributeFilter, String attribute) {
        String query;
        switch (attributeFilter) {
            case EQUIPMENT:
                query = "SELECT * FROM Exercise WHERE equipment = ?";
                break;
            case MOTION:
                query = "SELECT * FROM Exercise WHERE motion = ?";
                break;
            case MUSCLE_GROUP:
                query = "SELECT * FROM Exercise WHERE muscleGroups LIKE ?";
                attribute = "%" + attribute + "%";
                break;
            default:
                return new ArrayList<>();
        }
        // Використовуємо rawQuery з готовим запитом
        try (SQLiteDatabase database = dbHelper.getWritableDatabase()) {
            return getExercisesFromCursor(database.rawQuery(query, new String[]{attribute}));
        }
    }


    /**
     * Логуємо всі вправи, що є в таблиці Exercise.
     */
    public void logAllExercises() {
        for (Exercise exercise : getAllExercises()) {
            Log.d("ExerciseLog",
                    "ID: " + exercise.getId() + " --- " +
                            "Name: " + exercise.getName() + " --- " +
                            "Description: " + exercise.getDescription() + " --- " +
                            "MotioStateList: " + exercise.getMotion() + " --- " +
                            "EquipmentStateList: " + exercise.getEquipment() + " --- " +
                            "Muscle Groups: " + exercise.getMuscleGroupList() + " --- " +
                            "isCustom: " + exercise.getIsCustom()
            );
        }
    }

    /**
     * Видаляємо конкретну вправу з таблиці "Exercise".
     *
     * @param exercise Об’єкт вправи, яку видаляємо
     * @return true, якщо успішно видалено
     */
    public boolean deleteExercise(Exercise exercise) {
        int rowsDeleted;
        try (SQLiteDatabase database = dbHelper.getWritableDatabase()) {
            rowsDeleted = database.delete(
                    "Exercise",
                    "id = ?",
                    new String[]{String.valueOf(exercise.getId())}
            );
        }
        return rowsDeleted > 0;
    }

    /**
     * Внутрішній метод для формування списку вправ із курсора.
     * Результат запиту до таблиці Exercise
     * Список об’єктів Exercise
     */
    // Створюємо статичну мапу для відповідності ключів до ресурсних ID
    private static final Map<String, Integer> EXERCISE_NAME_MAP;
    static {
        Map<String, Integer> map = new HashMap<>();
        map.put("exercise_dip_weighted", R.string.exercise_dip_weighted);
        map.put("exercise_incline_db_press", R.string.exercise_incline_db_press);
        map.put("exercise_pullup_weighted", R.string.exercise_pullup_weighted);
        map.put("exercise_db_row", R.string.exercise_db_row);
        map.put("exercise_squat_barbell", R.string.exercise_squat_barbell);
        map.put("exercise_deadlift", R.string.exercise_deadlift);
        // Додайте інші ключі за потребою
        EXERCISE_NAME_MAP = Collections.unmodifiableMap(map);
    }

    // Допоміжний метод для отримання ID ресурсу за ключем
    private Integer getExerciseNameResourceId(String key) {
        return EXERCISE_NAME_MAP.get(key);
    }

    private List<Exercise> getExercisesFromCursor(Cursor cursor) {
        List<Exercise> exerciseList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                // Зчитуємо поля
                boolean isCustom = cursor.getInt(cursor.getColumnIndexOrThrow("isCustom")) == 1;
                long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                Motion motion = Motion.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("motioState")));
                String muscleGroupsString = cursor.getString(cursor.getColumnIndexOrThrow("muscleGroups"));
                Equipment equipment = Equipment.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("equipmentState")));

                // Формуємо список MuscleGroup
                List<MuscleGroup> muscleGroups;
                if (muscleGroupsString == null || muscleGroupsString.isEmpty()) {
                    muscleGroups = new ArrayList<>();
                } else {
                    muscleGroups = Stream.of(muscleGroupsString.split(","))
                            .map(String::trim)
                            .map(MuscleGroup::valueOf)
                            .collect(Collectors.toList());
                }

                // Якщо вправа НЕ кастомна, підвантажуємо локалізовану назву з ресурсів
                if (!isCustom) {
                    Integer resId = getExerciseNameResourceId(name);
                    if (resId != null) {
                        name = context.getString(resId);
                    }
                }

                // Створюємо об’єкт Exercise з врахуванням description
                Exercise exercise = new Exercise(id, name, description, motion, muscleGroups, equipment);
                // Якщо потрібно, позначаємо isCustom в об’єкті
                exercise.setCustom(isCustom);

                // Додаємо у результуючий список
                exerciseList.add(exercise);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return exerciseList;
    }
    /**
     * Logs all exercises from the database to Logcat.
     */
    public void voidGetAllDatabaseInLog() {

        try (SQLiteDatabase database = dbHelper.getWritableDatabase()) {
            Cursor cursor = null;
            try {
                // Query all exercises from the database
                cursor = database.query(
                        "Exercise",
                        null, // всі стовпці
                        null, // без selection
                        null, // без selection args
                        null, // без group by
                        null, // без having
                        null  // без order by
                );

                // Перевіряємо, чи є записи
                if (cursor.getCount() > 0) {
                    Log.d("ExerciseDAO", "===== START OF EXERCISE DATABASE DUMP =====");

                    // Отримуємо індекси стовпців
                    int idIndex = cursor.getColumnIndex("id");
                    int nameIndex = cursor.getColumnIndex("name");
                    int descriptionIndex = cursor.getColumnIndex("description");
                    int motionIndex = cursor.getColumnIndex("motioState");
                    int muscleGroupsIndex = cursor.getColumnIndex("muscleGroups");
                    int equipmentIndex = cursor.getColumnIndex("equipmentState");
                    int isCustomIndex = cursor.getColumnIndex("isCustom");

                    // Ітеруємо всі рядки
                    while (cursor.moveToNext()) {
                        int id = cursor.getInt(idIndex);
                        String name = cursor.getString(nameIndex);
                        String description = cursor.getString(descriptionIndex);
                        String motion = cursor.getString(motionIndex);
                        String muscleGroups = cursor.getString(muscleGroupsIndex);
                        String equipment = cursor.getString(equipmentIndex);
                        int isCustom = cursor.getInt(isCustomIndex);

                        @SuppressLint("DefaultLocale") String logMessage = String.format(
                                "ID: %d | Name: %s | Description: %s | MotioStateList: %s | Muscle Groups: %s | EquipmentStateList: %s | Custom: %d",
                                id, name, description, motion, muscleGroups, equipment, isCustom
                        );

                        Log.d("ExerciseDAO", logMessage);
                    }
                    Log.d("ExerciseDAO", "===== END OF EXERCISE DATABASE DUMP =====");
                } else {
                    Log.d("ExerciseDAO", "Database is empty - no exercises found");
                }
            } catch (Exception e) {
                Log.e("ExerciseDAO", "Error while dumping database to log", e);
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }
        }
    }
}
