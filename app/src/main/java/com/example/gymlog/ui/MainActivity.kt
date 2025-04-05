package com.example.gymlog.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.core.view.WindowCompat
import com.example.gymlog.R
import com.example.gymlog.database.ExerciseDAO
import com.example.gymlog.database.testDatabaseJavaWrapper
import com.example.gymlog.ui.exercises.activities.ExerciseManagementActivity
import com.example.gymlog.ui.programs.FitnessProgramsActivity
import com.example.gymlog.ui.theme.MyAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Включаємо edge-to-edge режим
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        // Ініціалізація бази даних
        val exerciseDAO = ExerciseDAO(this)
        exerciseDAO.logAllExercises()

        //тест бази Room
        testDatabaseJavaWrapper(
            this,
            applicationContext
        ) {
            runOnUiThread {
                Toast.makeText(this, "Операція завершена", Toast.LENGTH_SHORT).show()
            }
            null
        }

        setContent {
            // Використовуємо MyAppTheme для автоматичного підбору кольорів
            MyAppTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current

    // Використовуємо кольори з MaterialTheme, які встановлюються через MyAppTheme
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(WindowInsets.systemBars.asPaddingValues()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        // Заголовок
        Text(
            text = stringResource(id = R.string.my_gym_log),
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 16.dp)
        )

        // Картка: Програми тренувань
        Card(
            modifier = Modifier
                .width(250.dp)
                .height(100.dp)
                .clickable {
                    Log.d("LogTag", "Programs clicked")
                    context.startActivity(
                        Intent(context, FitnessProgramsActivity::class.java)
                    )
                },
            shape = RoundedCornerShape(100.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Картка: Редактор вправ
        Card(
            modifier = Modifier
                .width(250.dp)
                .height(100.dp)
                .clickable {
                    Log.d("LogTag", "New Exercises clicked")
                    context.startActivity(
                        Intent(context, ExerciseManagementActivity::class.java)
                    )
                },
            shape = RoundedCornerShape(100.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
