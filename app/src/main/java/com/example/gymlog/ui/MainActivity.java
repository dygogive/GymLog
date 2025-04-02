package com.example.gymlog.ui;


import static androidx.lifecycle.LifecycleOwnerKt.getLifecycleScope;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gymlog.R;
import com.example.gymlog.database.ExerciseDAO;
import com.example.gymlog.database.ExerciseResult;
import com.example.gymlog.database.ExerciseResultDao;
import com.example.gymlog.database.GymLogRoomDatabase;
import com.example.gymlog.database.RoomExtensionsKt;
import com.example.gymlog.ui.exercises.activities.ExerciseManagementActivity;
import com.example.gymlog.ui.programs.FitnessProgramsActivity;

import java.util.Date;
import java.util.List;

import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.CoroutineStart;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.Job;

public class MainActivity extends AppCompatActivity {

    private GymLogRoomDatabase db;
    private ExerciseResultDao dao;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        findViewById(R.id.cardNewExercises).setOnClickListener(v -> {
            Log.d("LogTag", "New Exercises clicked");
            startActivity(new Intent(MainActivity.this, ExerciseManagementActivity.class));
        });

        findViewById(R.id.cardPrograms).setOnClickListener(v -> {
            Log.d("LogTag", "New Exercises clicked");
            startActivity(new Intent(MainActivity.this, FitnessProgramsActivity.class));
        });


        ExerciseDAO exerciseDAO = new ExerciseDAO(this);
        exerciseDAO.logAllExercises();


        // 1. Отримуємо інстанс бази GymLogRoomDatabase
        db = GymLogRoomDatabase.Companion.getInstance(getApplicationContext());
        dao = db.exerciseResultDao();

        // Додайте цей код у onCreate() перед викликом insertAndLoadTestData()
        if (db == null) {
            Log.e("DB_ERROR", "Database instance is null!");
        } else {
            Log.d("DB_TEST", "Database instance created successfully");
        }


        // 2. Виконуємо Room-операції у фоновому потоці
        insertAndLoadTestData();



    }

    private void insertAndLoadTestData() {
        Log.d("DB_TEST", "Початок insertAndLoadTestData");

        ExerciseResult testResult = new ExerciseResult(
                0, 1, 1, System.currentTimeMillis(), 50.0f, 10, "Мій перший запис"
        );

        Log.d("DB_TEST", "Створено тестовий запис: " + testResult);

        new Thread(() -> {
            Log.d("DB_TEST", "Фоновий потік запущено");

            try {
                // 1. Вставка запису
                Log.d("DB_TEST", "Спроба вставки запису...");
                long newId = RoomExtensionsKt.insertExerciseResultBlocking(dao, testResult);

                if (newId == -1L) {
                    Log.e("DB_ERROR", "Не вдалося вставити запис");
                    return;
                }

                Log.d("DB_TEST", "Успішно додано запис з ID: " + newId);

                // 2. Отримання всіх записів
                Log.d("DB_TEST", "Спроба отримати всі записи...");
                List<ExerciseResult> allResults = RoomExtensionsKt.getAllExerciseResultsBlocking(dao);

                if (allResults.isEmpty()) {
                    Log.d("DB_TEST", "Отримано 0 записів");
                } else {
                    Log.d("DB_TEST", "Отримано " + allResults.size() + " записів");
                    for (ExerciseResult result : allResults) {
                        Log.d("DB_TEST",
                                "ID: " + result.getId() +
                                        ", Вправа: " + result.getExerciseId() +
                                        ", Блок: " + result.getTrainingBlockId() +
                                        ", Дата: " + new Date(result.getTimestamp()) +
                                        ", Вага: " + result.getWeight() +
                                        ", Повторення: " + result.getRepetitions() +
                                        ", Нотатки: " + (result.getNotes() != null ? result.getNotes() : "немає"));
                    }
                }

            } catch (Exception e) {
                Log.e("DB_ERROR", "Помилка роботи з БД", e);
                e.printStackTrace();
            }
        }).start();
    }

}