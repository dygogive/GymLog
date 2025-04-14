package com.example.gymlog.data.local.roomtesting

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * @Entity
 * Що робить:
 * Анотація @Entity використовується для позначення класу як сутності бази даних. Це означає, що об'єкти цього класу будуть збережені в таблиці бази даних.
 *
 * Параметри:
 *
 * tableName: задає ім’я таблиці в базі даних. Якщо не вказати, то за замовчуванням ім’я таблиці буде таким же, як і ім’я класу.
 *
 * У більшості випадків обирають import androidx.room.Entity з androidx.room:room-common. Це стандартний пакет, який використовується в Android-проєктах для анотації Room.
 *
 * androidx.room:room-common-jvm — це варіант для JVM-проєктів, не типово для Android.
 *
 * androidx.room:room-common — те, що потрібно для Android, коли працюєш із Room.
 *
 * Якщо в тебе у build.gradle (Module) вже підключені залежності Room (наприклад, implementation "androidx.room:room-runtime:2.5.1" та kapt "androidx.room:room-compiler:2.5.1"), то IntelliJ/Android Studio зазвичай підтягне правильний import androidx.room.Entity автоматично.
 */




// ТАблиця з результатами в базі
//для зберігання об'єктів ExerciseResult
@Entity(tableName = "exercise_results",
        indices = [
            Index(value = ["exerciseId"]),
            Index(value = ["trainingBlockId"]),
            Index(value = ["timestamp"])
        ]
    )
data class ExerciseResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val exerciseId:         Long, // ід вправи в таблиці вправ
    val trainingBlockId:    Long, //ід тренувального блоку в таблиці з блоками
    val timestamp:          Long, // дата і час (Unix-час)
    @ColumnInfo(name = "weight") val weight: Float,  // використовуйте @ColumnInfo для явного вказання імені стовпця
    @ColumnInfo(name = "repetitions") val repetitions: Int,
    @ColumnInfo(name = "notes") val notes: String? = null
) {
    // Гетери для Java
    fun getid(): Long {return id}
    fun getexerciseId(): Long {return exerciseId}
    fun gettrainingBlockId(): Long {return trainingBlockId}
    fun gettimestamp(): Long {return timestamp}
    fun getweight(): Float {return weight}
    fun getrepetitions(): Int {return repetitions}
    fun getnotes(): String? {return notes}
}