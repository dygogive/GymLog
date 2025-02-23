package com.example.gymlog.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.gymlog.data.plan.PlanCycle;

import java.util.ArrayList;
import java.util.List;

public class PlanManagerDAO {

    private final DBHelper dbHelper;

    public PlanManagerDAO(Context context) {
        this.dbHelper = new DBHelper(context);
    }

    // Додаємо новий план у базу
    public long addPlan(PlanCycle planCycle) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", planCycle.getName());
        values.put("description", planCycle.getDescription());
        values.put("creation_date", System.currentTimeMillis());
        values.put("is_active", 0);

        long id = db.insert("PlanCycles", null, values);
        db.close();
        return id;
    }

    // Отримуємо всі плани з бази
    public List<PlanCycle> getAllPlans() {
        List<PlanCycle> plans = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT id, name, description FROM PlanCycles", null);
        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(0);
                String name = cursor.getString(1);
                String description = cursor.getString(2);
                plans.add(new PlanCycle(id, name, description, new ArrayList<>()));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return plans;
    }


    // Видаляємо план
    public void deletePlan(long planId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("PlanCycles", "id = ?", new String[]{String.valueOf(planId)});
        db.close();
    }

}
