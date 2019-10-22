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

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    private void makeAddTodoDialog() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(listView.getWindowToken(), 0);
        todoButton.setText("Add ToDo");
        todoInput.setText("");
        todoInput.setHint("Anything you wanna do?");
        todoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Todo todo = new Todo(-1, todoInput.getText().toString());
                DatabaseHelper dbHelper = DatabaseHelper.getInstance(MainActivity.this);
                boolean isTodoAdded = dbHelper.insertTodo(todo);
                if (isTodoAdded)
                    showToast("ToDo added successfully");
                arrayAdapter.clear();
                arrayAdapter.addAll(dbHelper.getTodos());
                makeAddTodoDialog();
            }
        });
    }

    private void makeUpdateTodoDialog(final Todo todo) {
        todoInput.setText(todo.getContent());
        todoButton.setText("Update ToDo");
        todoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

    Button todoButton;
    EditText todoInput;
    List<Todo> todos;
    ArrayAdapter arrayAdapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listview);
        todoButton = findViewById(R.id.todo_button);
        todoInput = findViewById(R.id.todo_input);

        makeAddTodoDialog();

        todos = DatabaseHelper.getInstance(this).getTodos();

        arrayAdapter = new ArrayAdapter<Todo>(this, R.layout.todo_layout, todos) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null)
                    convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.todo_layout, parent, false);
                Todo todo = getItem(position);
                TextView todoContent = convertView.findViewById(R.id.todo_content);
                todoContent.setText(todo.getContent());
                return convertView;
            }
        };
        arrayAdapter.setNotifyOnChange(true);

        listView.setAdapter(arrayAdapter);
        listView.setFooterDividersEnabled(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                makeUpdateTodoDialog(todos.get(i));
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                DatabaseHelper dbHelper = DatabaseHelper.getInstance(MainActivity.this);
                boolean isTodoRemoved = dbHelper.removeTodo(todos.get(i));
                if (isTodoRemoved)
                    showToast("ToDo removed successfully");
                arrayAdapter.clear();
                arrayAdapter.addAll(dbHelper.getTodos());
                makeAddTodoDialog();
                return true;
            }
        });
    }
}
