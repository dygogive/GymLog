package com.example.gymlog.ui.feature.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gymlog.ui.feature.main.MainScreen
import com.example.gymlog.ui.feature.workout.WorkoutScreen

/**
 * Головний компонент навігації додатка, що використовує Jetpack Navigation.
 * Визначає граф навігації між екранами додатка.
 *
 * @param navHostController - контролер навігації, який керує переходами між екранами
 */
@Composable
fun AppNavHost(navHostController: NavHostController) {
    // Створення NavHost - контейнера для навігації
    NavHost(
        navController = navHostController,  // Контролер для управління навігацією
        startDestination = "main"      // Стартовий екран додатка
    ) {
        // Визначення точок навігації (екранів) додатка

        /**
         * Екран "main" - головний екран додатка
         *
         * @param navHostController передається для можливості навігації з цього екрана
         */
        composable("main") {
            MainScreen(navHostController)
        }

        /**
         * Екран "workout" - екран тренування
         *
         * Наразі не отримує navController, але може бути додано при необхідності
         */
        composable("workout") {
            WorkoutScreen(navHostController)
        }

        /*
         * Приклад додавання нового екрана (закоментовано):
         *
         * composable("templateSelect") {
         *     TemplateSelectScreen(navController)
         * }
         */
    }
}