package com.hifit.zz.activity;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.hifit.zz.hifit.R;

public class StepHistroyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);

        HifitApp app = (HifitApp)getApplication();
        Cursor cursor = app.getStepsDAO().queryCursor();

        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(
                this,android.R.layout.simple_expandable_list_item_2, cursor,
                new String[]{"date","step" },
                new int[]{android.R.id.text1,android.R.id.text2});

        ListView listView = (ListView) findViewById(R.id.list_steps);
        listView.setAdapter(simpleCursorAdapter);
        //setListAdapter(simpleCursorAdapter);
    }
}
