package com.example.asus.happispellcrossword.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.asus.happispellcrossword.R;
import com.example.asus.happispellcrossword.adapter.GridSpacingItemDecoration;
import com.example.asus.happispellcrossword.adapter.GridViewLevelAdapter;
import com.example.asus.happispellcrossword.adapter.RecyclerItemClickListener;
import com.example.asus.happispellcrossword.model.Stage;
import com.example.asus.happispellcrossword.model.StaticVariable;

import java.util.ArrayList;

/**
 * Created by Asus on 6/8/2016.
 */
public class HomeActivity extends AppCompatActivity {
    int column = 2;
    int spacing = 0;
    boolean includeEdge = false;
    GridViewLevelAdapter rcAdapter;
    private RecyclerView recyclerViewLevel;
    private ArrayList<Stage> stageItems;
    private LinearLayoutManager horizontalLayoutManager;
    private int screenWidth;
    private int screenHeight;
    private int doneLevel = 0;
    private String prefName = "data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity_layout);
        getTimeCompleteStage();
        getdoneLevel();
        setDefaultDataLevel();
        setUpView();
        setAdapterForRecyclerView();
        registerEvent();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getTimeCompleteStage();
        getdoneLevel();
        rcAdapter.changeCurrentLevel(doneLevel);
        rcAdapter.notifyDataSetChanged();
    }

    private void getdoneLevel() {
        SharedPreferences pre = getSharedPreferences
                (prefName, MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        //editor.clear();
        editor.commit();
        doneLevel = pre.getInt("doneLevel", 0);
    }

    private void getTimeCompleteStage() {
        SharedPreferences pre = getSharedPreferences
                (prefName, MODE_PRIVATE);
        for (Stage stage : StaticVariable.getInstance().getAllStage()) {
            int timeComplete = pre.getInt(stage.getDescriptionStage() + "", 0);
            stage.setSecondComplete(timeComplete);
        }
    }

    private void registerEvent() {
        recyclerViewLevel.addOnItemTouchListener(new RecyclerItemClickListener(HomeActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (position < doneLevel+1) {
                            // TODO Handle item click
                            Intent intent = new Intent(HomeActivity.this, GameActivity.class);
                            intent.putExtra("levelposition", position + 1);
                            startActivity(intent);
                        }
                    }
                })
        );
    }

    private void setAdapterForRecyclerView() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
        column=stageItems.size();
        spacing = (int) ((screenWidth-getResources().getDimension(R.dimen.recycleview_stage_margin_left)*2)/10);
        horizontalLayoutManager = new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.HORIZONTAL,false);
        recyclerViewLevel.setHasFixedSize(true);
        recyclerViewLevel.setLayoutManager(horizontalLayoutManager);
        rcAdapter = new GridViewLevelAdapter(HomeActivity.this, stageItems, doneLevel+1);
        recyclerViewLevel.setAdapter(rcAdapter);
        recyclerViewLevel.addItemDecoration(new GridSpacingItemDecoration(column, spacing, includeEdge));
    }

    private void setDefaultDataLevel() {
        stageItems = StaticVariable.getInstance().getAllStage();

    }

    private void setUpView() {
        recyclerViewLevel = (RecyclerView) findViewById(R.id.recyclerview_stage);
    }
}

