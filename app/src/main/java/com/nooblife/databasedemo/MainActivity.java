package com.nooblife.databasedemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button todoButton;
    EditText todoInput;
    List<Todo> todos;
    ArrayAdapter arrayAdapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing Views made in XML
        listView = findViewById(R.id.listview);
        todoButton = findViewById(R.id.todo_button);
        todoInput = findViewById(R.id.todo_input);

        // By default, make the UI look like a To-Do is ready to be added
        makeAddTodoDialog();

        // Initialize the List of ToDos from the Database0
        todos = DatabaseHelper.getInstance(this).getTodos();

        // Initialize the Adapter, which is responsible for creating UI for this list of Items
        // With the Layout supplied in the second parameter --------v   This one
        arrayAdapter = new ArrayAdapter<Todo>(this, R.layout.todo_layout, todos) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                // If the view at that position is not created yet, get it created
                if (convertView == null)
                    convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.todo_layout, parent, false);
                // With the view created, update the contents of the View with the Data
                Todo todo = getItem(position);
                TextView todoContent = convertView.findViewById(R.id.todo_content);
                todoContent.setText(todo.getContent());
                return convertView;
            }
        };
        // If set to True, any changes to the list that I make through arrayAdapter will
        // affect the UI immediately!
        arrayAdapter.setNotifyOnChange(true);

        // Set the Adapter to this ListView
        listView.setAdapter(arrayAdapter);
        // I want to show a divider line below each item
        listView.setFooterDividersEnabled(true);

        // Setting a listener for every item clicked in the ListView
        // On Clicking an Item, it can be edited. Changes in UI are made accordingly
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                makeUpdateTodoDialog(todos.get(i));
            }
        });

        // Setting a listener for every item long-clicked in the listview
        // On Long-Clicking an Item, it is removed
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                DatabaseHelper dbHelper = DatabaseHelper.getInstance(MainActivity.this);
                boolean isTodoRemoved = dbHelper.removeTodo(todos.get(i));
                if (isTodoRemoved)
                    showToast("ToDo removed successfully");
                // Get Updated DB Data
                arrayAdapter.clear();
                arrayAdapter.addAll(dbHelper.getTodos());
                // Reset the UI
                makeAddTodoDialog();
                return true;
            }
        });
    }


    /**
     * Convenience function to show Toast to the User
     * @param text The Text to be displayed in the Toast
     */
    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    /**
     * Function to make the dialog at the bottom look like a Add To-Do dialog
     */
    private void makeAddTodoDialog() {
        // Hide Soft Input (Keyboard)
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(listView.getWindowToken(), 0);

        // Set the UI accordingly
        todoButton.setText("Add ToDo");
        todoInput.setText("");
        todoInput.setHint("Anything you wanna do?");
        // On Clicking the button, Whatever is in the EditText should be added in the DB
        todoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Make a To-Do Object. ID is given by DB, so I set it to -1
                Todo todo = new Todo(-1, todoInput.getText().toString());
                // Get DatabaseHelper instance
                DatabaseHelper dbHelper = DatabaseHelper.getInstance(MainActivity.this);
                // Add To-Do to DB and get status via return type
                boolean isTodoAdded = dbHelper.insertTodo(todo);
                // If successful, show the toast
                if (isTodoAdded)
                    showToast("ToDo added successfully");
                // Reset The Data, since data in DB is changed
                arrayAdapter.clear();
                arrayAdapter.addAll(dbHelper.getTodos());
                // Reset the UI
                makeAddTodoDialog();
            }
        });
    }

    /**
     * Function to make the UI look like a To-Do is being updated
     * @param todo The TODO which is to be updated
     */
    private void makeUpdateTodoDialog(final Todo todo) {
        todoInput.setText(todo.getContent());
        todoButton.setText("Update ToDo");
        // On clicking the button, The To-Do should be updated
        todoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Same comments as Add (Except that ID remains the same (not -1), it's from the DB)
                Todo updatedTodo = new Todo(todo.getId(), todoInput.getText().toString());
                DatabaseHelper dbHelper = DatabaseHelper.getInstance(MainActivity.this);
                boolean isTodoUpdated = dbHelper.editTodo(updatedTodo);
                if (isTodoUpdated)
                    showToast("ToDo updated successfully");
                arrayAdapter.clear();
                arrayAdapter.addAll(dbHelper.getTodos());
                makeAddTodoDialog();
            }
        });
    }

}
