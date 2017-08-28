package com.example.android.sunshine;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class AddActivity extends AppCompatActivity {

    private EditText mAddName;
    private EditText mDateAdded;
    private Button mSaveChanges;

    private boolean daysChanged = false;
    private boolean nameChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        mAddName = (EditText) findViewById(R.id.et_food_name);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mAddName, InputMethodManager.SHOW_IMPLICIT);

        mDateAdded = (EditText) findViewById(R.id.et_date);
        imm.showSoftInput(mAddName, InputMethodManager.SHOW_IMPLICIT);

        mSaveChanges = (Button) findViewById(R.id.but_save);
        mSaveChanges.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (daysChanged) {
                    // TODO: 8/21/2017 foodProvider update() method needs to be implemented

                }
            }
        });
    }
}
