package com.example.gymlog.ui.feature

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.gymlog.core.di.DatabaseModule.MIGRATION_21_22
import com.example.gymlog.data.local.legacy.DBHelper
import com.example.gymlog.data.local.legacy.ExerciseDAO
import com.example.gymlog.data.local.legacy.PlanManagerDAO
import com.example.gymlog.data.local.room.WorkoutDatabase
import com.example.gymlog.ui.theme.MyAppTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Головна Activity додатка, що використовує Jetpack Compose для UI.
 *
 * @AndroidEntryPoint - анотація Dagger Hilt для підтримки dependency injection в Activity
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var roomDb: WorkoutDatabase

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

        // 1. Спочатку ініціалізуємо Room для міграції
        initRoomDatabase()

        // 2. Потім ініціалізуємо OpenHelper
        initOpenHelper()


        // 2. Налаштування UI з Jetpack Compose
        setContent {
            // Застосування теми додатка
            MyAppTheme {
                // Ініціалізація контролера навігації
                val navController = rememberNavController()
                // Встановлення графа навігації
                AppNavHost(navHostController = navController)
            }
        }
    }



    private fun initRoomDatabase() {
        roomDb = Room.databaseBuilder(
            applicationContext,
            WorkoutDatabase::class.java,
            "GymLog.db"
        )
            .addMigrations(MIGRATION_21_22) // Додайте всі необхідні міграції
            .allowMainThreadQueries() // Тимчасово для ініціалізації
            .build()

        // Виконуємо простий запит, щоб активувати міграцію
        roomDb.query("SELECT 1", null).use { /* Просто щоб викликати міграцію */ }
    }

    private fun initOpenHelper() {
        dbHelper = DBHelper(this)

        // Відкриваємо БД для активації перевірки версії
        val db = dbHelper.writableDatabase
        db.close() // Закриваємо після перевірки
    }

    override fun onDestroy() {
        super.onDestroy()
        roomDb.close() // Не забудьте закрити Room базу
    }
}