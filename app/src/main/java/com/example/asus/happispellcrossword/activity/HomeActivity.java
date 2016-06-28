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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.asus.happispellcrossword.R;
import com.example.asus.happispellcrossword.adapter.GridSpacingItemDecoration;
import com.example.asus.happispellcrossword.adapter.GridViewLevelAdapter;
import com.example.asus.happispellcrossword.adapter.RecyclerItemClickListener;
import com.example.asus.happispellcrossword.model.Stage;
import com.example.asus.happispellcrossword.model.StaticVariable;
import com.example.asus.happispellcrossword.model.WordObject;
import com.example.asus.happispellcrossword.utils.DBHelper;
import com.example.asus.happispellcrossword.utils.SoundEffect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Asus on 6/8/2016.
 */
public class HomeActivity extends Activity implements MediaPlayer.OnPreparedListener{
    int column = 2;
    int spacing = 0;
    boolean includeEdge = false;
    GridViewLevelAdapter rcAdapter;
    private RecyclerView recyclerViewLevel;
    private ImageView buttonSoundBackground,buttonInfo;
    private boolean isSoundBackgroundOn=true;
    private ArrayList<Stage> stageItems;
    private LinearLayoutManager horizontalLayoutManager;
    private int screenWidth;
    private int screenHeight;
    private int doneLevel = 0;
    private StaticVariable staticVariable;
    private DBHelper databse;
    private MediaPlayer mediaPlayer;
    private ArrayList<WordObject> listQuestion=new ArrayList<WordObject>();

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

    private void playSoundBackGround() {
        mediaPlayer=MediaPlayer.create(HomeActivity.this,R.raw.happiness);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.stop();
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
        editor.putBoolean(StaticVariable.DATABASE, true);
        editor.commit();
    }

    private boolean checkAlreadyDatabase(){
        SharedPreferences pre = getSharedPreferences
                (StaticVariable.PREF_NAME, MODE_PRIVATE);
        boolean alreadyDatabase=pre.getBoolean(StaticVariable.DATABASE, false);
        return alreadyDatabase;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getTimeCompleteStage();
        getdoneLevel();
        rcAdapter.changeCurrentLevel(doneLevel);
        rcAdapter.notifyDataSetChanged();
        playSoundBackGround();
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
        buttonSoundBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSoundSetup();
            }
        });
        buttonInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this,"This game created by EarlyEducation",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changeSoundSetup() {
        if(isSoundBackgroundOn){
            buttonSoundBackground.setImageResource(R.drawable.ic_volume_off_white_48pt);
            isSoundBackgroundOn=false;
            mediaPlayer.pause();

        }else {
            buttonSoundBackground.setImageResource(R.drawable.ic_volume_up_white_48pt);
            isSoundBackgroundOn=true;
            mediaPlayer.start();
        }
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
        listQuestion=loadStageFromAssets();
        stageItems = StaticVariable.getInstance().getAllStage();
    }

    private void setUpView() {
        recyclerViewLevel = (RecyclerView) findViewById(R.id.recyclerview_stage);
        buttonSoundBackground=(ImageView)findViewById(R.id.ic_volume_start);
        buttonInfo=(ImageView)findViewById(R.id.ic_info_start);

    }

    public ArrayList<WordObject> loadStageFromAssets(){
        ArrayList<WordObject>listQuestion=new ArrayList<WordObject>();
        String json = null;
        try {
            InputStream is = getAssets().open("stage1.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        try {
            JSONObject obj = new JSONObject(json);
            JSONArray m_jArry = obj.getJSONArray("1");

            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                WordObject wordObject = new WordObject();
                wordObject.setStagePosition((int)jo_inside.getInt("stagePosition"));
                wordObject.startX=(int)jo_inside.getInt("startX");
                wordObject.startY=(int)jo_inside.getInt("startY");
                wordObject.setQuestion(jo_inside.getString("question"));
                wordObject.setResult(jo_inside.getString("result"));
                wordObject.setOrientation((int) jo_inside.getInt("orientation"));
                wordObject.setImageLink(jo_inside.getString("imageLink"));
                wordObject.setPosition((int)jo_inside.getInt("positionQuestion"));

                //Add your values in your `ArrayList` as below:
                listQuestion.add(wordObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listQuestion;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }
}

