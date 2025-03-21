package com.example.gymlog.ui.workout;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gymlog.R;
import com.example.gymlog.data.db.DBHelper;
import com.example.gymlog.data.db.WorkoutDAO;
import com.example.gymlog.data.db.WorkoutHistoryDAO;
import com.example.gymlog.data.db.WorkoutSetDAO;
import com.example.gymlog.data.set.WorkoutSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WorkoutActivity extends AppCompatActivity {

    private EditText
            editTextExercise,
            editTextWeight,
            editTextRepetitions;
    private Button
            buttonAddSet,
            buttonFinish;
    private ListView listViewSets;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> listItems = new ArrayList<>();

    private DBHelper dbHelper;
    private WorkoutDAO workoutDAO;
    private WorkoutSetDAO workoutSetDAO;
    private WorkoutHistoryDAO workoutHistoryDAO;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_workout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //ініціалізація екрану
        initUI();

        dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        workoutDAO = new WorkoutDAO(database);
        workoutSetDAO = new WorkoutSetDAO(database);
        workoutHistoryDAO = new WorkoutHistoryDAO(database);
    }

    private void initUI() {
        editTextExercise = (EditText) findViewById(R.id.editTextExercise);
        editTextWeight = (EditText) findViewById(R.id.editTextWeight);
        editTextRepetitions = (EditText) findViewById(R.id.editTextRepetitions);
        buttonFinish = (Button) findViewById(R.id.buttonFinish);
        buttonFinish.setOnClickListener(v -> {
            onClickFinishWorkout();
        });
        buttonAddSet = (Button) findViewById(R.id.buttonAddSet);
        buttonAddSet.setOnClickListener(v -> {
            onClickAddSet();
        });

        listViewSets = (ListView) findViewById(R.id.listViewSets);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listItems);
        listViewSets.setAdapter(arrayAdapter);
    }





    private void onClickAddSet() {
        //отримати тексти з полів
        String exercise = editTextExercise.getText().toString().trim();
        String weight = editTextWeight.getText().toString().trim();
        String repetitions = editTextRepetitions.getText().toString().trim();

        //перевірити чи текст не порожній
        if(exercise.isEmpty() | weight.isEmpty() | repetitions.isEmpty()) {
            Toast.makeText(WorkoutActivity.this,"Fill all rows", Toast.LENGTH_SHORT).show();
            return;
        }
        //створити строку
        StringBuilder sb = new StringBuilder();
        //добавити строку в список
        listItems.add(sb.append(exercise).append("; ").append(weight).append("; ")
                .append(repetitions).append("; ").toString());

        //оновити адаптер
        arrayAdapter.notifyDataSetChanged();
        //очистити текстові строки
        editTextWeight.getText().clear();
        editTextRepetitions.getText().clear();
    }

    //при натисканні кнопки Закінчити тренування
    private void onClickFinishWorkout() {

        //якщо немає записів то закінчити
        if(listItems.isEmpty()) {
            Toast.makeText(this,"List is Empty",Toast.LENGTH_LONG).show();
            //подивитися об'єднану таблицю
            workoutHistoryDAO.logJoinedTable();
            finish();
        }

        //Сформувати ім'я
        String workoutname = "Workout " +
                new SimpleDateFormat("dd", Locale.getDefault()).format(new Date());
        //Отримати дату
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        //список підходів на цьому тренуванні
        List<WorkoutSet> listOfSets = new ArrayList<>();
        //вправа
        String exercise;
        //вага повторення
        int weight, repetitions;
        //зробити список підходів
        for(String set: listItems){
            String[] parts = set.split("; ");
            exercise    = parts[0];
            weight      = Integer.parseInt(parts[1]);
            repetitions = Integer.parseInt(parts[2]);
            listOfSets.add(new WorkoutSet(exercise, weight, repetitions));
        }

        //занести тренування в базу
        workoutSetDAO.addWorkoutWithSets(date,workoutname,listOfSets);

        //подивитися об'єднану таблицю в Лог
        workoutHistoryDAO.logJoinedTable();

        //вийти в MainActivity
        finish();
    }



    @Override
    protected void onStop() {
        dbHelper.close();
        super.onStop();
    }
}
