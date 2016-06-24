package com.example.asus.happispellcrossword.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.asus.happispellcrossword.R;
import com.example.asus.happispellcrossword.adapter.GridSpacingItemDecoration;
import com.example.asus.happispellcrossword.adapter.GridViewLevelAdapter;
import com.example.asus.happispellcrossword.adapter.RecyclerItemClickListener;
import com.example.asus.happispellcrossword.model.Stage;
import com.example.asus.happispellcrossword.model.StaticVariable;
import com.example.asus.happispellcrossword.utils.DBHelper;
import com.example.asus.happispellcrossword.utils.SoundEffect;

import java.util.ArrayList;

/**
 * Created by Asus on 6/8/2016.
 */
public class HomeActivity extends Activity {
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
    private StaticVariable staticVariable;
    private DBHelper databse;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity_layout);
        staticVariable=StaticVariable.getInstance();
        getTimeCompleteStage();
        getdoneLevel();
        setDefaultDataLevel();
        setUpView();
        setAdapterForRecyclerView();
        registerEvent();
        if(!checkAlreadyDatabase())
            setupDatabse();
        playSoundBackGround();
    }

    @Override
    protected void onResume() {
        super.onResume();
        playSoundBackGround();
    }

    private void playSoundBackGround() {
        SoundEffect.getInstance().playStartSound(HomeActivity.this, mediaPlayer);
    }

    private void setupDatabse() {
        databse=new DBHelper(this);
        for(Stage stage:stageItems){
            databse.insertStage(stage);
            databse.insertQuestion(stage);
        }
        SharedPreferences pre = getSharedPreferences
                (StaticVariable.PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor=pre.edit();
        editor.putBoolean(StaticVariable.DATABASE,true);
        editor.commit();
    }

    private boolean checkAlreadyDatabase(){
        SharedPreferences pre = getSharedPreferences
                (StaticVariable.PREF_NAME, MODE_PRIVATE);
        boolean alreadyDatabase=pre.getBoolean(StaticVariable.DATABASE,false);
        return alreadyDatabase;
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
                (StaticVariable.PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        //editor.clear();
        editor.commit();
        doneLevel = pre.getInt(StaticVariable.DONE_LEVEL, 0);
    }

    private void getTimeCompleteStage() {
        SharedPreferences pre = getSharedPreferences
                (StaticVariable.PREF_NAME, MODE_PRIVATE);
        for (Stage stage : StaticVariable.getInstance().getAllStage()) {
            int timeComplete = pre.getInt(stage.getDescriptionStage() + "", 0);
            stage.setSecondComplete(timeComplete);
        }
    }

    private void registerEvent() {
        recyclerViewLevel.addOnItemTouchListener(new RecyclerItemClickListener(HomeActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(final View view, final int position) {
                        if (position < doneLevel+1) {
                            // TODO Handle item click
                            Animation animationHorizontalScale = AnimationUtils.loadAnimation(HomeActivity.this, R.anim.horizontal_scale);
                            animationHorizontalScale.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {
                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    Intent intent = new Intent(HomeActivity.this, GameActivity.class);
                                    intent.putExtra("levelposition", position + 1);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });
                            view.startAnimation(animationHorizontalScale);


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

