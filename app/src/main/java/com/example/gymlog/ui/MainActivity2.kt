package com.example.gymlog.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.example.gymlog.database.ExerciseDAO
import com.example.gymlog.database.testroom.testDatabaseJavaWrapper
import com.example.gymlog.ui.screens.main.MainScreen
import com.example.gymlog.ui.screens.workout.WorkoutScreen
import com.example.gymlog.ui.theme.MyAppTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity2 : ComponentActivity() {

    /**
     * Ключові моменти:
     *
     * WindowCompat.setDecorFitsSystemWindows(window, false) - встановлює edge-to-edge режим, коли контент відображається під системними панелями.
     *
     * ExerciseDAO(this) - ініціалізує доступ до бази даних.
     *
     * testDatabaseJavaWrapper - тестує роботу бази даних Room.
     *
     * setContent - встановлює UI за допомогою Jetpack Compose.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        // Включаємо edge-to-edge режим
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        // Ініціалізація бази даних
        val exerciseDAO = ExerciseDAO(this)
        exerciseDAO.logAllExercises()

        // Тест бази Room
        testDatabaseJavaWrapper(this, applicationContext) {
            runOnUiThread {
                Toast.makeText(this, "Операція завершена", Toast.LENGTH_SHORT).show()
            }
            null
        }


        setContent {
            MyAppTheme {
                val navController = rememberNavController()
                NavHost(navController, startDestination = "mainScreen") {
                    composable("mainScreen") { MainScreen(navController) }
                    composable("workoutScreen") { WorkoutScreen() }
                }
            }
        }
    }
}







