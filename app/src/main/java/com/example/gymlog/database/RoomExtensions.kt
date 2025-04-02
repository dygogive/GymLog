package com.example.gymlog.database

import kotlinx.coroutines.runBlocking

fun insertExerciseResultBlocking(dao: ExerciseResultDao, result: ExerciseResult): Long {
    return try {
        runBlocking {
            dao.insert(result)
        }
    } catch (e: Exception) {
        -1L // Повертаємо -1 у разі помилки
    }
}

fun getAllExerciseResultsBlocking(dao: ExerciseResultDao): List<ExerciseResult> {
    return try {
        runBlocking {
            dao.getAllResults()
        }
    } catch (e: Exception) {
        emptyList() // Повертаємо пустий список у разі помилки
    }
}