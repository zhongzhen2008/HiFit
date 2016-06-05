package com.hifit.zz.activity;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.hifit.zz.db.StepItem;
import com.hifit.zz.hifit.R;
import com.hifit.zz.utils.LogUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StepHistroyActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter mAdapter;
    private ListView mListView;
    private HifitApp mApp;
    private int mTodayStep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);

        initViews();

 /*       HifitApp app = (HifitApp)getApplication();
        Cursor cursor = app.getStepsDAO().queryCursorDateDesc();

        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(
                this, R.layout.step_list_item, cursor,
                new String[]{"step","date" },
                new int[]{R.id.tv_step, R.id.tv_date}, 0);*/
        mApp = (HifitApp)getApplication();
        Intent intent = getIntent();
        mTodayStep = intent.getIntExtra("step", 0);
        LogUtil.d("StepHistroyActivity mTodayStep = " + mTodayStep);

        mAdapter = new SimpleCursorAdapter(
                this, R.layout.step_list_item, null,
                new String[]{"step","date" },
                new int[]{R.id.tv_step, R.id.tv_date}, 0);

        mListView = (ListView) findViewById(R.id.list_steps);
        mListView.setAdapter(mAdapter);

        mListView.setVisibility(View.INVISIBLE);

        getLoaderManager().initLoader(0, null, this);
    }

    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(R.string.step_detail);

        toolbar.inflateMenu(R.menu.menu_step_graph);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int menuItemId = item.getItemId();
                if (menuItemId == R.id.action_step_graph) {
                    //Toast.makeText(StepHistroyActivity.this, "点击菜单", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(StepHistroyActivity.this, StepHistroyGraphActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });
    }

    private void updateTodayStep() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String s = sdf.format(date);

        StepItem stepItem = new StepItem();
        stepItem.date = s;
        stepItem.step = mTodayStep;

        mApp.getStepsDAO().update(stepItem);

        LogUtil.d("刷新今天步数 updateTodayStep, step = " + mTodayStep + ", Thread = " + android.os.Process.myTid());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        LogUtil.d("开始加载数据库 onCreateLoader, Thread = " + android.os.Process.myTid());
        return new CursorLoader(this)
        {
            @Override
            public Cursor loadInBackground()
            {
                updateTodayStep();

                LogUtil.d("开始加载数据库 loadInBackground, Thread = " + android.os.Process.myTid());
                Cursor cursor = mApp.getStepsDAO().queryCursorDateDesc();
                return cursor;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        LogUtil.d("数据库加载完毕 onLoadFinished");
        mAdapter.swapCursor(data);
        mListView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        LogUtil.d("数据库复位 onLoaderReset");
        mAdapter.swapCursor(null);
    }

}
