package com.example.gymlog.data.local.roomtesting

import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


const val TAG = "RoomTest"

// Основна suspend-функція (залишаємо без змін)
suspend fun testDatabase(context: Context) {
    val db = AppDatabase.getInstance(context)
    val dao = db.getExerciseDao()

    // Вставка даних
    dao.insert(
        ExerciseResult(
            id = 0,
            exerciseId = 2,
            trainingBlockId = 3,
            timestamp = System.currentTimeMillis(),
            weight = 100f,
            repetitions = 10,
            notes = "Exercise done"
        )
    )

    val allExerciseResults = dao.getAll()

    allExerciseResults.forEach { e ->
        Log.d(TAG, "ID: ${e.id}; exeID: ${e.exerciseId}; Notes: ${e.notes ?: "no notes"}")
    }
}

// Новий варіант з lifecycleScope (рекомендований)
fun testDatabaseLifecycleAware(
    owner: LifecycleOwner,
    context: Context,
    onSuccess: () -> Unit = {},
    onError: (Throwable) -> Unit = {}
) {
    owner.lifecycleScope.launch {
        try {
            // Виконуємо операції з БД у IO потоці
            withContext(Dispatchers.IO) {
                testDatabase(context)
            }
            // Повертаємось на головний потік для callback
            onSuccess()
        } catch (e: Exception) {
            Log.e(TAG, "Database error", e)
            onError(e)
        }
    }
}

// Для Java-сумісності (якщо потрібно)
fun testDatabaseJavaWrapper(
    owner: LifecycleOwner,
    context: Context,
    callback: () -> Unit
) {
    testDatabaseLifecycleAware(owner, context, callback)
}


