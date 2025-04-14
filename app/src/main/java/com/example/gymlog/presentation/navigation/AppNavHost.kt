package com.example.gymlog.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gymlog.presentation.screens_compose.main.MainScreen
import com.example.gymlog.presentation.screens_compose.workout.WorkoutScreen

/**
 * Головний компонент навігації додатка, що використовує Jetpack Navigation.
 * Визначає граф навігації між екранами додатка.
 *
 * @param navController - контролер навігації, який керує переходами між екранами
 */
@Composable
fun AppNavHost(navController: NavHostController) {
    // Створення NavHost - контейнера для навігації
    NavHost(
        navController = navController,  // Контролер для управління навігацією
        startDestination = "main"      // Стартовий екран додатка
    ) {
        // Визначення точок навігації (екранів) додатка

        /**
         * Екран "main" - головний екран додатка
         *
         * @param navController передається для можливості навігації з цього екрана
         */
        composable("main") {
            MainScreen(navController)
        }

        /**
         * Екран "workout" - екран тренування
         *
         * Наразі не отримує navController, але може бути додано при необхідності
         */
        composable("workout") {
            WorkoutScreen()
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