package com.example.gymlog.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.example.gymlog.data.exercise.AttributeType;
import com.example.gymlog.data.exercise.Equipment;
import com.example.gymlog.data.exercise.Exercise;
import com.example.gymlog.data.exercise.Motion;
import com.example.gymlog.data.exercise.MuscleGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DAO (Data Access Object) для роботи з таблицею "Exercise":
 * - Додавання/оновлення/видалення вправ
 * - Пошук за атрибутами (Motion, MuscleGroup, Equipment)
 * - Отримання всіх вправ
 */
public class ExerciseDAO {

    // База даних (readable / writable) + контекст
    private final SQLiteDatabase database;
    private final Context context;

    /**
     * Конструктор, що створює DAO на базі DBHelper.
     * Ми беремо readableDatabase, але можна й writable (залежить від операцій).
     *
     * @param context Поточний контекст
     */
    public ExerciseDAO(Context context) {
        this.context = context;
        this.database = new DBHelper(context).getReadableDatabase();
    }

    /**
     * Додає вправу в таблицю "Exercise".
     *
     * @param exerciseName   Назва вправи
     * @param motion         Рух (Motion)
     * @param muscleGroups   Список м'язевих груп
     * @param equipment      Обладнання
     * @param isCustom       true, якщо вправа створена користувачем
     * @return ID доданого запису (або -1, якщо сталася помилка)
     */
    public long addExercise(
            String exerciseName,
            Motion motion,
            List<MuscleGroup> muscleGroups,
            Equipment equipment,
            boolean isCustom
    ) {
        ContentValues values = new ContentValues();
        values.put("name", exerciseName);
        values.put("motion", motion.name());
        values.put(
                "muscleGroups",
                TextUtils.join(
                        ",",
                        muscleGroups.stream()
                                .map(Enum::name)
                                .toArray(String[]::new)
                )
        );
        values.put("equipment", equipment.name());
        values.put("isCustom", isCustom ? 1 : 0);

        // Вставляємо запис
        return database.insert("Exercise", null, values);
    }

    /**
     * Перевантажений метод для додавання вправи через об'єкт Exercise.
     * Позначаємо як кастомну за замовчуванням.
     */
    public long addExercise(Exercise exercise) {
        return addExercise(
                exercise.getName(),
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
        ContentValues values = new ContentValues();
        values.put("name", exercise.getName());
        values.put("motion", exercise.getMotion().name());
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
        values.put("equipment", exercise.getEquipment().name());
        values.put("isCustom", 1); // позначаємо як кастомну

        // Оновлюємо запис за ID вправи
        int rowsAffected = database.update(
                "Exercise",
                values,
                "id = ?",
                new String[]{String.valueOf(exercise.getId())}
        );

        return rowsAffected > 0;
    }

    /**
     * Отримуємо список усіх вправ із таблиці "Exercise".
     */
    public List<Exercise> getAllExercises() {
        // Простий запит SELECT * FROM Exercise
        return getExercisesFromCursor(
                database.query("Exercise", null, null, null, null, null, null)
        );
    }

    /**
     * Пошук вправ за певним атрибутом (Motion, MuscleGroup, Equipment).
     */
    public List<Exercise> getExercisesByAttribute(AttributeType attributeType, String attribute) {
        String query;
        switch (attributeType) {
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
        return getExercisesFromCursor(database.rawQuery(query, new String[]{attribute}));
    }

    /**
     * Спеціальний метод для пошуку за конкретною м’язевою групою.
     */
    public List<Exercise> getExercisesByMuscle(MuscleGroup muscleGroup) {
        return getExercisesByAttribute(AttributeType.MUSCLE_GROUP, muscleGroup.name());
    }

    /**
     * Логуємо всі вправи, що є в таблиці Exercise.
     */
    public void logAllExercises() {
        for (Exercise exercise : getAllExercises()) {
            Log.d("ExerciseLog",
                    "Name: " + exercise.getName() + "---" +
                            "Motion: " + exercise.getMotion() + "---" +
                            "Equipment: " + exercise.getEquipment() + "---" +
                            "Muscle Groups: " + exercise.getMuscleGroupList() + "---" +
                            "is custom: " + exercise.getIsCustom());
        }
    }

    /**
     * Видаляємо конкретну вправу з таблиці "Exercise".
     *
     * @param exercise Об’єкт вправи, яку видаляємо
     * @return true, якщо успішно видалено
     */
    public boolean deleteExercise(Exercise exercise) {
        int rowsDeleted = database.delete(
                "Exercise",
                "id = ?",
                new String[]{String.valueOf(exercise.getId())}
        );
        // Закриваємо базу (залежить від твоєї архітектури, можливо краще було б у finalize)
        database.close();
        return rowsDeleted > 0;
    }

    /**
     * Внутрішній метод для формування списку вправ із курсора.
     *
     * @param cursor Результат запиту до таблиці Exercise
     * @return Список об’єктів Exercise
     */
    private List<Exercise> getExercisesFromCursor(Cursor cursor) {
        List<Exercise> exerciseList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                // Зчитуємо поля
                boolean isCustom = cursor.getInt(cursor.getColumnIndexOrThrow("isCustom")) == 1;
                long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                Motion motion = Motion.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("motion")));
                String muscleGroupsString = cursor.getString(cursor.getColumnIndexOrThrow("muscleGroups"));
                Equipment equipment = Equipment.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("equipment")));

                // Формуємо список MuscleGroup
                List<MuscleGroup> muscleGroups;
                if (muscleGroupsString == null || muscleGroupsString.isEmpty()) {
                    muscleGroups = new ArrayList<>();
                } else {
                    // Розбиваємо рядок на елементи та перетворюємо у enum
                    muscleGroups = List.of(muscleGroupsString.split(","))
                            .stream()
                            .map(String::trim)
                            .map(MuscleGroup::valueOf)
                            .collect(Collectors.toList());
                }

                // Якщо вправа НЕ кастомна, підвантажуємо назву з ресурсів (локалізація)
                if (!isCustom) {
                    int resId = context.getResources().getIdentifier(
                            name,
                            "string",
                            context.getPackageName()
                    );
                    if (resId != 0) {
                        name = context.getString(resId);
                    }
                }

                // Створюємо об’єкт Exercise
                Exercise exercise = new Exercise(id, name, motion, muscleGroups, equipment);
                // Якщо потрібно, позначаємо isCustom в об’єкті
                exercise.setCustom(isCustom);

                // Додаємо у результуючий список
                exerciseList.add(exercise);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return exerciseList;
    }
}
