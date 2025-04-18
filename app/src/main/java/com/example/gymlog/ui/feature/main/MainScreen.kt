package com.example.gymlog.ui.feature.main

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gymlog.R
import com.example.gymlog.ui.legacy.exercise.ExerciseManagementActivity
import com.example.gymlog.ui.legacy.program.FitnessProgramsActivity


private const val CARD_WIDTH = 250
private const val CARD_HEIGHT = 100

val sizeElevationCards = 20


@Composable
fun MainScreen(navController: NavController) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(WindowInsets.systemBars.asPaddingValues()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    )
    {
        // Заголовок
        Text(
            text = stringResource(id = R.string.my_gym_log),
            //fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onBackground,
            //style = MaterialTheme.typography.labelLarge,  // Використовуємо стиль displayLarge з кастомною типографікою
            style = MaterialTheme.typography.headlineMedium,  // Використовуємо стиль displayLarge з кастомною типографікою
            //modifier = Modifier.padding(top = 16.dp)
        )

        // Редактор вправ
        HomeCard(
            title = stringResource(R.string.exe_editor),
            onClick = {
                context.startActivity(
                    Intent(context, ExerciseManagementActivity::class.java)
                )
            }
        )

        // Програми
        HomeCard(
            title = stringResource(R.string.edit_programs),
            onClick = {
                context.startActivity(
                    Intent(context, FitnessProgramsActivity::class.java)
                )
            }
        )

        // Почати тренування
        HomeCard(
            title = stringResource(R.string.workout),
            onClick = { navController.navigate("workout") }
        )
    }

}



@Composable
private fun HomeCard(title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(CARD_WIDTH.dp)
            .height(CARD_HEIGHT.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(100.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = sizeElevationCards.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            Text(title, style = MaterialTheme.typography.titleLarge)
        }
    }
}

