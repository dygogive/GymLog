package com.example.gymlog.ui.history;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.gymlog.data.DBHelper;
import com.example.gymlog.R;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private ListView listViewHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DBHelper(this);
        listViewHistory = (ListView) findViewById(R.id.listViewHistory);
        showHistory();
    }

    private void showHistory() {
        Cursor cursor = dbHelper.getWorkoutHistory();
        StringBuilder sb = new StringBuilder();
        List<String> dataHistory = new ArrayList<>();

        try {
            if(cursor.moveToFirst()){
                do{
                    String name, exercise, reptype, weight, reps, date;
                    name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    exercise = cursor.getString(cursor.getColumnIndexOrThrow("exercise"));
                    reptype = cursor.getString(cursor.getColumnIndexOrThrow("reptype"));
                    weight = cursor.getString(cursor.getColumnIndexOrThrow("weight"));
                    reps = cursor.getString(cursor.getColumnIndexOrThrow("reps"));
                    date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                    //вивести в ListView
                    sb.append(name).append("=").append(exercise).append("=").append(reptype).append("=").append(weight)
                            .append("=").append(reps).append("=").append(date);
                    dataHistory.add(sb.toString());
                    sb.setLength(0);
                } while (cursor.moveToNext());
            } else Log.d("LogTag","no data");
        } catch (Exception e) {
            Log.d("LogTag","error");
        } finally {
            if (cursor != null) cursor.close();
        }

        ArrayAdapter<String> listHistoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataHistory);

        listViewHistory.setAdapter(listHistoryAdapter);
    }


    @Override
    protected void onStop() {
        dbHelper.close();
        super.onStop();
    }
}