package com.example.gymlog.ui.screens.main

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gymlog.R
import com.example.gymlog.ui.exercises.activities.ExerciseManagementActivity
import com.example.gymlog.ui.programs.FitnessProgramsActivity




val sizeElevationCards = 20

@SuppressLint("NotConstructor")
@Composable
fun MainScreen(navController: NavHostController) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(WindowInsets.systemBars.asPaddingValues()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
    )
    {
        // Заголовок
        Text(
            text = stringResource(id = R.string.my_gym_log),
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.labelLarge,  // Використовуємо стиль displayLarge з кастомною типографікою
            modifier = Modifier.padding(top = 16.dp)
        )


        // Картка: Редактор вправ
        Card(
            modifier = Modifier
                .width(250.dp)
                .height(100.dp)
                .clickable { //слухач натискань
                    Log.d("LogTag", "New Exercises clicked")
                    context.startActivity(
                        Intent(context, ExerciseManagementActivity::class.java)
                    )
                },
            shape = RoundedCornerShape(100.dp),
            //тіні?
            elevation = CardDefaults.cardElevation(defaultElevation = sizeElevationCards.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_exercise),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = stringResource(id = R.string.exe_editor),
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.displayLarge,  // Використовуємо стиль displayLarge з кастомною типографікою
                )
            }
        }



        // Картка: Програми тренувань
        Card(
            modifier = Modifier
                .width(250.dp)
                .height(100.dp)
                .clickable { //слухач натискань
                    Log.d("LogTag", "Programs clicked")
                    context.startActivity(
                        Intent(context, FitnessProgramsActivity::class.java)
                    )
                },
            shape = RoundedCornerShape(100.dp),
            //тіні?
            elevation = CardDefaults.cardElevation(defaultElevation = sizeElevationCards.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_exercise),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = stringResource(id = R.string.edit_programs),
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.displayLarge,  // Використовуємо стиль displayLarge з кастомною типографікою
                )
            }
        }

        // Картка: Почати тренування
        Card(
            modifier = Modifier
                .width(250.dp)
                .height(100.dp)
                .clickable { //слухач натискань
                    Log.d("LogTag", "Workout screen start")
                    //треба стартувати показ нового екрану з секундоміром та WorkoutGymDay, що відповідає вибраному GymDay
                    navController.navigate("workoutScreen")
                },
            shape = RoundedCornerShape(100.dp),
            //тіні?
            elevation = CardDefaults.cardElevation(defaultElevation = sizeElevationCards.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_exercise),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = stringResource(id = R.string.workout),
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.displayLarge,  // Використовуємо стиль displayLarge з кастомною типографікою
                )
            }
        }
    }

}

