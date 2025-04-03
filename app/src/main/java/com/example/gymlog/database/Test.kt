package com.example.gymlog.database

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.Continuation;
import kotlin.jvm.functions.Function2;


const val TAG = "RoomTest"

// Test.kt
suspend fun testDatabase(context: Context) {
    val db = AppDatabase.getInstance(context)
    val dao = db.getExerciseDao()

    // Вставка даних
    val id = dao.insert(
        ExerciseResult(
            id = 0,
            exerciseId = 2,
            trainingBlockId = 3,
            timestamp = System.currentTimeMillis(),
            weight = 100,
            repetitions = 10,
            notes = "Exercise done"
        )
    );

    val allExerciseResults = dao.getAll();


    allExerciseResults.forEach { e ->
        run {
            Log.d(TAG, "ID: $id; exeID: ${e.exerciseId}; Notes: ${e.notes}")
        }
    }
}


//для використання suspend
fun testDatabaseWithCallback(context: Context, callback: () -> Unit) {
    runBlocking {
        testDatabase(context)
        callback()
    }
}



//для використання suspend використовується функція-обгортка
fun testDatabaseJavaWrapper(context: Context, callback: () -> Unit) {

    CoroutineScope(Dispatchers.IO).launch {
        testDatabase(context)
        callback()
    }

}