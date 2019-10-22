package com.nooblife.databasedemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String NAME = "Database";
    private static final int version = 69;
    private static DatabaseHelper instance;

    private final String TODO_TABLE_NAME = "todos";
    private final String TODO_ID = "id";
    private final String TODO_CONTENT = "todo";
    private final String TODO_TIME = "timestamp";

    public static DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context, NAME, null, version);
        }
        return instance;
    }

    private DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE_NAME);
        sqLiteDatabase.execSQL("CREATE TABLE " + TODO_TABLE_NAME +
                " (" +
                TODO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                TODO_CONTENT + " VARCHAR"+
//                TODO_TIME + " DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean insertTodo(Todo todo) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TODO_CONTENT, todo.getContent());
        long result = db.insert(TODO_TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public boolean editTodo(Todo todo) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TODO_CONTENT, todo.getContent());
        long result = db.update(TODO_TABLE_NAME, contentValues, TODO_ID + "= ?", new String[]{ ""+todo.getId() });
        return result != -1;
    }

    public boolean removeTodo(Todo todo) {
        SQLiteDatabase db = getWritableDatabase();
        long result = db.delete(TODO_TABLE_NAME, TODO_ID + "= ?", new String[]{ ""+todo.getId() });
        return result != -1;
    }

    public List<Todo> getTodos() {
        ArrayList<Todo> todos = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TODO_TABLE_NAME, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int id = cursor.getInt(cursor.getColumnIndex(TODO_ID));
            String content = cursor.getString(cursor.getColumnIndex(TODO_CONTENT));
            todos.add(new Todo(id, content));
            cursor.moveToNext();
        }
        return todos;
    }
}
