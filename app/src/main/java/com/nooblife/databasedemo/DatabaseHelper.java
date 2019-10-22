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

    // Name of The Database
    private static final String NAME = "Database";
    // Version of The Database. Increment when Schema is altered.
    private static final int version = 69;
    private static DatabaseHelper instance;

    // Table Name
    private final String TODO_TABLE_NAME = "todos";
    // Table Columns
    private final String TODO_ID = "id";
    private final String TODO_CONTENT = "todo";
    // Not used Yet!
    private final String TODO_TIME = "timestamp";

    /**
     * Helper function to create Instance of the Helper
     * @param context Android Context required for Initializing Database
     * @return The Instance of DatabaseHelper
     */
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
        // Create a new Table anyways, so Drop the existing table. Me Lazy
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE_NAME);
        // Create a new table execSQL gets raw SQL queries executed for ya!
        sqLiteDatabase.execSQL("CREATE TABLE " + TODO_TABLE_NAME +
                " (" +
                TODO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                TODO_CONTENT + " VARCHAR"+
//                TODO_TIME + " DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // If version is changed, then drop 'em tables!
        if (i != i1)
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE_NAME);
    }

    public boolean insertTodo(Todo todo) {
        SQLiteDatabase db = getWritableDatabase();
        // ContentValues is more like a Key-Value Data Structure for Mapping a Column
        // with it's corresponding value
        ContentValues contentValues = new ContentValues();
        contentValues.put(TODO_CONTENT, todo.getContent());
        // Inserts a Row in the Database, with the column values inside ContentValues mapping
        // Returns ID of the row if properly inserted. If failed, then it returns -1
        long result = db.insert(TODO_TABLE_NAME, null, contentValues);
        // Yeah we're done, close the DB Access Object
        db.close();
        // Return the status accordingly
        return result != -1;
    }

    public boolean editTodo(Todo todo) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TODO_CONTENT, todo.getContent());
        // Update TODO_TABLE_NAME
        // With Values present in the mapping 'contentValues'
        // Where TODO_ID = todo.getId()
        // Side Note: 3rd Parameter is the Where Clause
        //            4th Parameter is the list of arguments, values from it will be substituted
        //                in place of question marks present in 3rd parameter
        long result = db.update(TODO_TABLE_NAME, contentValues, TODO_ID + "= ?", new String[]{ ""+todo.getId() });
        // Yeah we're done, close the DB Access Object
        db.close();
        return result != -1;
    }

    public boolean removeTodo(Todo todo) {
        SQLiteDatabase db = getWritableDatabase();
        long result = db.delete(TODO_TABLE_NAME, TODO_ID + "= ?", new String[]{ ""+todo.getId() });
        // Yeah we're done, close the DB Access Object
        db.close();
        return result != -1;
    }

    public List<Todo> getTodos() {
        ArrayList<Todo> todos = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        // If the Query runs successfully, it returns an iterator object.
        // We'd require to use the iterator to get the tuples obtained from the query
        Cursor cursor = db.rawQuery("SELECT * FROM " + TODO_TABLE_NAME, null);
        // Move to the first record
        cursor.moveToFirst();
        // While we don't reach the end
        while (!cursor.isAfterLast()) {
            // Get the data of a record by the cursor.getX methods
            // The argument for each method is the index of the column whose value is required
            // There's a method called getColumnIndex which helps you get the index of a column by it's name
            int id = cursor.getInt(cursor.getColumnIndex(TODO_ID));
            String content = cursor.getString(cursor.getColumnIndex(TODO_CONTENT));
            todos.add(new Todo(id, content));
            // Move to the next record
            cursor.moveToNext();
        }
        // Close the Cursor and DB when work's done
        // Helps in proper garbage collection
        cursor.close();
        db.close();
        return todos;
    }
}
