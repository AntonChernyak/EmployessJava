package ru.educationalwork.employessjava.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import ru.educationalwork.employessjava.pojo.Employee;

@Database(entities = {Employee.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    // Паттерн Singleton
    private static AppDatabase database;
    private static final String DB_NAME = "employees.db";

    // Объект синхронизации, чтобы не было проблем при одновременном обращении разных потоков
    private static final Object LOCK = new Object();

    public static AppDatabase getInstance(Context context) {
        synchronized (LOCK) {
            if (database == null) {
                database = Room.databaseBuilder(context, AppDatabase.class, DB_NAME).build();
            }
            return database;
        }
    }

    public abstract EmployeeDao employeeDao();
}