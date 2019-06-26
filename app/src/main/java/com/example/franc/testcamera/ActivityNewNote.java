package com.example.franc.testcamera;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.franc.testcamera.SQLiteDatabases.NotesDatabaseHelper;

/**
 * Created by franc on 18/6/2019.
 */

public class ActivityNewNote extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newnote);

        final Button createNoteButton = (Button) findViewById(R.id.createNoteButton);
        final EditText noteDesText = (EditText) findViewById(R.id.noteDesText);
        final EditText noteTitleText = (EditText) findViewById(R.id.noteTitleText);

        createNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = noteTitleText.getText().toString().trim();
                String des = noteDesText.getText().toString().trim();

                //If title and description are not empty
                if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(des)) {
                    createNote(title, des);
                    Intent intent = new Intent(ActivityNewNote.this, ActivityMain.class);
                    startActivity(intent);
                }
                else {
                    Snackbar.make(view, "You have empty fills..", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createNote(String title, String des) {
        //INSERT DATABASE: new row/note
        NotesDatabaseHelper myNotesDB = new NotesDatabaseHelper(this);
        boolean hasInsertedData = myNotesDB.insertData(title, des,
                0, 0, 0, 0, 400, 400);

        if (hasInsertedData) {
            Toast.makeText(ActivityNewNote.this, "Note successfully created", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(ActivityNewNote.this, "Error creating note", Toast.LENGTH_LONG).show();
        }
    }
}
