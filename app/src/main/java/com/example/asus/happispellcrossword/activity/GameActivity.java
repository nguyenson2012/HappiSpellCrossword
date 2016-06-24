package com.example.asus.happispellcrossword.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.happispellcrossword.R;
import com.example.asus.happispellcrossword.adapter.GridviewAdapter;
import com.example.asus.happispellcrossword.model.StaticVariable;
import com.example.asus.happispellcrossword.model.WordObject;
import com.example.asus.happispellcrossword.model.WordObjectsManager;
import com.example.asus.happispellcrossword.utils.DBHelper;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

/**
 * Created by Asus on 6/8/2016.
 */
public class GameActivity extends Activity implements GridviewAdapter.ChangeLevelInterface{
    public static final int AD_HEIGHT = 50;
    public static final int NUM_OF_COLLUMN = 10;
    public static final int NUM_OF_ROW = NUM_OF_COLLUMN;
//    public static final int MAX_NUM_OF_KEYBOARD_BTN_PER_ROW = 10;
    private static GridView gridView;
    //    public int PARENT_VERTICAL_MARGIN;
    private int LINE_HEIGHT;
//    private int BTN_KEYBOARD_MARGIN_LEFT_AND_RIGHT;
//    private int BTN_KEYBOARD_EDGE_SIZE;
    private int screenWidth = 0;
    private int screenHeight = 0;
//    private TextView txtView_question;
//    private ImageView imgView_question;
//    private ImageButton del_btn;
    private String[][] gridViewData = new String[NUM_OF_COLLUMN][NUM_OF_ROW];//gridViewData[x][y]
    private WordObjectsManager objManger = WordObjectsManager.getInstance();
    private GridviewAdapter adapter;
//    private ImageButton btCheckAnswer;
    //    private Button btSolve;
//    private ImageButton btClear;
    private DisplayImageOptions opt;
    private ImageLoader imageLoader;
    private AdView mAdView;
    private ArrayList<WordObject> listQuestion;
//    private ArrayList<Bitmap> listBitmapImageQuestion;
    private StaticVariable staticVariable;
    private int doneLevel;
    private int currentLevel=1;
    private int timeStartLevel = 0;
    private int timeCompleteLevel = 0;
    private boolean allLevelDone = false;
    private Button btCheckAnswer;
    private ArrayList<Button> listKeyboard=new ArrayList<Button>();
    private int[] arrayButtonKeyboard={R.id.bt_answer_A,R.id.bt_answer_B,R.id.bt_answer_C,R.id.bt_answer_D,
            R.id.bt_answer_D,R.id.bt_answer_E,R.id.bt_answer_F,R.id.bt_answer_G,R.id.bt_answer_H,R.id.bt_answer_I,
            R.id.bt_answer_J,R.id.bt_answer_K,R.id.bt_answer_L,R.id.bt_answer_M,R.id.bt_answer_N,R.id.bt_answer_O,
            R.id.bt_answer_P,R.id.bt_answer_Q,R.id.bt_answer_R,R.id.bt_answer_S,R.id.bt_answer_T,R.id.bt_answer_U,
            R.id.bt_answer_V,R.id.bt_answer_W,R.id.bt_answer_X,R.id.bt_answer_Y,R.id.bt_answer_Z};
    private DBHelper database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database=new DBHelper(this);
        staticVariable = StaticVariable.getInstance();
        getLevelPosition();
        getdoneLevel();
        timeStartLevel = (int) (System.currentTimeMillis() / 1000);
//        context = this;
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
        setupKeyboard();
        setupGridView();
        registerEvent();
        /*LINE_HEIGHT = (screenHeight - gridView.getMinimumHeight())/5;
        BTN_KEYBOARD_MARGIN_LEFT_AND_RIGHT = LINE_HEIGHT/8; // If need to adjust, change here
        BTN_KEYBOARD_EDGE_SIZE = LINE_HEIGHT-2*BTN_KEYBOARD_MARGIN_LEFT_AND_RIGHT;
        //If the size above is not correct
        if(BTN_KEYBOARD_EDGE_SIZE*MAX_NUM_OF_KEYBOARD_BTN_PER_ROW+
                BTN_KEYBOARD_MARGIN_LEFT_AND_RIGHT*(MAX_NUM_OF_KEYBOARD_BTN_PER_ROW+1)>screenWidth)
        {
            int temp = screenWidth/((MAX_NUM_OF_KEYBOARD_BTN_PER_ROW));
            BTN_KEYBOARD_MARGIN_LEFT_AND_RIGHT = temp/8; // If need to adjust, change here
            BTN_KEYBOARD_EDGE_SIZE = temp-2*BTN_KEYBOARD_MARGIN_LEFT_AND_RIGHT;
        }*/
    }

    private void getdoneLevel() {
        SharedPreferences pre = getSharedPreferences
                (StaticVariable.PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        //editor.clear();
        editor.commit();
        doneLevel = pre.getInt(StaticVariable.DONE_LEVEL, 0);
    }
    private void getLevelPosition() {
        Intent intent = getIntent();
        currentLevel = intent.getIntExtra("levelposition", 1);
    }

    private void registerEvent() {
        for(final Button button:listKeyboard){
            button.setOnTouchListener(new ChoiceTouchListener());
        }
        gridView.setOnDragListener(new ChoiceDragListener());
    }

    private void setupKeyboard() {
        for(int i=0;i<arrayButtonKeyboard.length;i++){
            Button button=(Button)findViewById(arrayButtonKeyboard[i]);
            listKeyboard.add(button);
        }
        /*btCheckAnswer=(Button)findViewById(R.id.btCheckAnswer);
        btCheckAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkAnswer())
                    increaseLevel();
            }
        });*/
    }

    private void initializeQuestion() {
        objManger.setObjectArrayList(new ArrayList<WordObject>());
        //objManger.setObjectArrayList(staticVariable.getAllStage().get(currentLevel - 1).getListQuestion());
        ArrayList<WordObject> listQuestion=database.getListQuestion(currentLevel);
        objManger.setObjectArrayList(listQuestion);
    }

    private void setupGridView() {
        initializeQuestion();
        //Reset gridview
        gridViewData = new String[NUM_OF_COLLUMN][NUM_OF_ROW];
        for (int i = 0; i < gridViewData[0].length; i++) {
            for (int j = 0; j < gridViewData[0].length; j++)//the board is rectangular
            {
                WordObject temp = objManger.getObjectAt(i, j);
                if (temp != null) {
                    gridViewData[i][j] = GridviewAdapter.ENABLE;
//                    Log.e("OBJ","i,j = "+i+" , "+j);
                } else {
                    gridViewData[i][j] = GridviewAdapter.DISABLE;
//                    Log.e("NULL","i,j = "+i+" , "+j);
                }
            }
        }

        adapter = new GridviewAdapter(this, gridViewData);
//        adapter.setUpListWord(objManger.getObjectArrayList());
        gridView = (GridView) findViewById(R.id.gridview_puzzle);
        gridView.setMinimumHeight(screenWidth);
        gridView.setAdapter(adapter);
        gridView.setNumColumns(NUM_OF_COLLUMN);
//        gridView.setColumnWidth((int) ((gridView.getWidth() / NUM_OF_COLLUMN) * 0.9));
        gridView.setMinimumWidth(screenWidth);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*//    @Override
    public void onItemGridViewClick(int position) {
        int positionX = position % NUM_OF_COLLUMN;
        int positionY = position / NUM_OF_COLLUMN;}*/

    private boolean checkAnswer() {
        boolean checkAnswer = true;
        for (WordObject question : objManger.getObjectArrayList()) {
            String answer = "";
            int firstX = question.startX;
            int firstY = question.startY;
            if (question.getOrientation() == WordObject.HORIZONTAL) {
                for (int i = 0; i < question.getResult().length(); i++) {
                    answer = answer.concat(gridViewData[firstX + i][firstY]);
                }
                if (!answer.equals(question.getResult()))
                    checkAnswer = false;
            } else {
                for (int i = 0; i < question.getResult().length(); i++) {
                    answer = answer.concat(gridViewData[firstX][firstY + i]);
                }
                if (!answer.equals(question.getResult()))
                    checkAnswer = false;
            }

        }
        return checkAnswer;
    }

    private void clearLevel() {
        for (WordObject question : objManger.getObjectArrayList()) {
            int firstX = question.startX;
            int firstY = question.startY;
            if (question.getOrientation() == WordObject.HORIZONTAL) {
                for (int i = 0; i < question.getResult().length(); i++) {
                    gridViewData[firstX + i][firstY] = "";
                }
            } else {
                for (int i = 0; i < question.getResult().length(); i++) {
                    gridViewData[firstX][firstY + i] = "";
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void increaseLevel() {
        increaseCurrentLevel();
        //initializeQuestion();
        if (!allLevelDone)
            setupGridView();
    }

    @Override
    public void notifyDoneQuestion() {
//            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//            alertDialogBuilder.setMessage("GO TO NEXT LEVEL");
//
//            alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface arg0, int arg1) {
//                    increaseLevel();
//                }
//            });
//
//            alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    finish();
//                }
//            });
//
//            AlertDialog alertDialog = alertDialogBuilder.create();
//            alertDialog.show();
        final Dialog dialog=new Dialog(GameActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_next_level);
        Button btLevel=(Button)dialog.findViewById(R.id.buttonLevel);
        Button btNextlevel=(Button)dialog.findViewById(R.id.buttonNextLevel);
        btLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
        btNextlevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                increaseLevel();
            }
        });
        dialog.getWindow().getAttributes().windowAnimations=R.style.dialog_animation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void increaseCurrentLevel() {
        SharedPreferences pre = getSharedPreferences
                (StaticVariable.PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        if(doneLevel==staticVariable.getAllStage().size()){
            allLevelDone = true;
        }
        if (doneLevel < staticVariable.getAllStage().size()&&currentLevel==doneLevel+1) {
            doneLevel++;
        }
        if(currentLevel<staticVariable.getAllStage().size())
            currentLevel++;

        if(doneLevel>pre.getInt(StaticVariable.DONE_LEVEL, 0)) {
            editor.putInt(StaticVariable.DONE_LEVEL, doneLevel);
            editor.putInt(StaticVariable.CURRENT_LEVEL,currentLevel);
            editor.commit();
        }
    }

    private final class ChoiceTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                //setup drag
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);

                //start dragging the item touched
                view.startDrag(data, shadowBuilder, view, 0);
                return true;
            }
            else {
                return false;
            }
        }
    }

    private class ChoiceDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent dragEvent) {
            switch (dragEvent.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    //no action necessary
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    //no action necessary
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    //no action necessary
                    break;
                case DragEvent.ACTION_DROP:
                    int newPosition = gridView.pointToPosition(
                            (int) (dragEvent.getX()), (int) dragEvent.getY());
                    View view = (View) dragEvent.getLocalState();
                    //stop displaying the view where it was before it was dragged
                    //view.setVisibility(View.INVISIBLE);
                    //view being dragged and dropped
                    Button droppedButton = (Button) view;
                    if(newPosition!=GridView.INVALID_POSITION) {
                        int positionX=newPosition%GameActivity.NUM_OF_COLLUMN;
                        int positionY=newPosition/GameActivity.NUM_OF_COLLUMN;
                        gridViewData[positionX][positionY]=droppedButton.getText()+"";
                        adapter.addNewButton();
                        adapter.setNewPosition(positionX,positionY);
                        adapter.notifyDataSetChanged();
                        gridView.invalidateViews();
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    //no action necessary
                    break;
                default:
                    break;
            }
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.zoom_enter,R.anim.zoom_exit);
    }
}
