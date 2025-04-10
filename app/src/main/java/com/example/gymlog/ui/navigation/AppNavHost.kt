package com.example.gymlog.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gymlog.ui.screens.main.MainScreen
import com.example.gymlog.ui.screens.workout.WorkoutScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController, startDestination = "main") {

        composable("main") {
            MainScreen(navController)
        }

        composable("workout") {
            WorkoutScreen()
        }

        // тут легко додавати інші маршрути:
        // composable("templateSelect") { TemplateSelectScreen(navController) }
    }
}