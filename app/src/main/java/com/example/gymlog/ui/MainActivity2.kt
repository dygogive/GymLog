package com.example.gymlog.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.example.gymlog.database.ExerciseDAO
import com.example.gymlog.database.testroom.testDatabaseJavaWrapper
import com.example.gymlog.ui.navigation.AppNavHost
import com.example.gymlog.ui.theme.MyAppTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Головна Activity додатка, що використовує Jetpack Compose для UI.
 *
 * @AndroidEntryPoint - анотація Dagger Hilt для підтримки dependency injection в Activity
 */
@AndroidEntryPoint
class MainActivity2 : ComponentActivity() {

    /**
     * Життєвий цикл Activity - створення.
     * Виконує наступні ключові дії:
     * 1. Налаштовує edge-to-edge відображення
     * 2. Ініціалізує та тестує роботу з базою даних
     * 3. Встановлює Compose UI з навігацією
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. Налаштування edge-to-edge режиму (контент під системними панелями)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        // 2. Робота з базою даних
        // Ініціалізація DAO для вправ
        val exerciseDAO = ExerciseDAO(this)
        // Логування всіх вправ для дебагінгу
        exerciseDAO.logAllExercises()

        // Тестування роботи Room бази даних через Java-обгортку
        testDatabaseJavaWrapper(this, applicationContext) {
            // Callback при завершенні тесту
            runOnUiThread {
                Toast.makeText(this, "Операція завершена", Toast.LENGTH_SHORT).show()
            }
            null
        }

        // 3. Налаштування UI з Jetpack Compose
        setContent {
            // Застосування теми додатка
            MyAppTheme {
                // Ініціалізація контролера навігації
                val navController = rememberNavController()
                // Встановлення графа навігації
                AppNavHost(navController = navController)
            }
        }
    }
}