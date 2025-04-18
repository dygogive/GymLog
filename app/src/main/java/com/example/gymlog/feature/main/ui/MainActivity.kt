package com.example.gymlog.feature.main.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.example.gymlog.data.local.legacy.ExerciseDAO
import com.example.gymlog.data.local.legacy.PlanManagerDAO
import com.example.gymlog.feature.navigation.AppNavHost
import com.example.gymlog.feature.theme.MyAppTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Головна Activity додатка, що використовує Jetpack Compose для UI.
 *
 * @AndroidEntryPoint - анотація Dagger Hilt для підтримки dependency injection в Activity
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

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
        //і для програм
        val planManagerDAO = PlanManagerDAO(this)
        // Логування всіх вправ для дебагінгу
        exerciseDAO.logAllExercises()
        planManagerDAO.logAllData()



        // 2. Налаштування UI з Jetpack Compose
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