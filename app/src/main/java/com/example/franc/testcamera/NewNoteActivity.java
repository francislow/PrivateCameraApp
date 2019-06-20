package com.example.franc.testcamera;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by franc on 18/6/2019.
 */

public class NewNoteActivity extends Activity {
    EditText noteDesText;
    EditText noteTitleText;
    Button createNoteButton;
    RelativeLayout rlayout;
    DatabaseHelper mydb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newnote);

        createNoteButton = (Button) findViewById(R.id.createNoteButton);
        noteDesText = (EditText) findViewById(R.id.noteDesText);
        noteTitleText = (EditText) findViewById(R.id.noteTitleText);

        createNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = noteTitleText.getText().toString().trim();
                String des = noteDesText.getText().toString().trim();

                //If title and description are not empty
                if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(des)) {
                    createNote(title, des);
                    Intent intent = new Intent(NewNoteActivity.this, ActivityMain.class);
                    startActivity(intent);
                }
                else {
                    Snackbar.make(view, "YOU HAVE EMPTY FILLS", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void createNote(String title, String des) {
        //INSERT DATABASE: new row/note
        mydb = new DatabaseHelper(this);
        boolean hasInsertedData = mydb.insertData(title, des, 0, 0, 0, 0, 400, 400);
        if (hasInsertedData) {
            Toast.makeText(NewNoteActivity.this, "Note successfully created", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(NewNoteActivity.this, "Error creating note", Toast.LENGTH_LONG).show();
        }
    }
}
