package com.example.asus.happispellcrossword.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.happispellcrossword.R;
import com.example.asus.happispellcrossword.adapter.Gridview2Adapter;
import com.example.asus.happispellcrossword.adapter.GridviewAdapter;
import com.example.asus.happispellcrossword.model.StaticVariable;
import com.example.asus.happispellcrossword.model.WordObject;
import com.example.asus.happispellcrossword.model.WordObjectsManager;
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
public class GameActivity extends Activity{
    public static final int AD_HEIGHT = 50;
    public static final int NUM_OF_COLLUMN = 10;
    public static final int NUM_OF_ROW = NUM_OF_COLLUMN;
    public static final double WEIGHT_COEFF = 2.5;
//    public static final int MAX_NUM_OF_KEYBOARD_BTN_PER_ROW = 10;
    private static GridView gridView;
    private static GridView gridView2;
    //    public int PARENT_VERTICAL_MARGIN;
//    private int LINE_HEIGHT;
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
    private Gridview2Adapter adapter2;
//    private ImageButton btCheckAnswer;
    //    private Button btSolve;
//    private ImageButton btClear;
    private DisplayImageOptions opt;
    private ImageLoader imageLoader;
    private AdView mAdView;
    //    private ArrayList<WordObject> listQuestion;
//    private ArrayList<Bitmap> listBitmapImageQuestion;
    private StaticVariable staticVariable;
    private String prefName = "data";
    private int doneLevel;
    private int currentLevel=1;
    private int timeStartLevel = 0;
    private int timeCompleteLevel = 0;
    private boolean allLevelDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        staticVariable = StaticVariable.getInstance();
        timeStartLevel = (int) (System.currentTimeMillis() / 1000);
//        context = this;
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
        setupGridView();
        setupGridView2();
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

    private void initializeQuestion() {
        objManger.setObjectArrayList(new ArrayList<WordObject>());
        objManger.setObjectArrayList(staticVariable.getAllStage().get(currentLevel - 1).getListQuestion());
    }

    private void setupGridView() {
        initializeQuestion();
        //Reset gridview
        for (int i = 0; i < gridViewData.length; i++) {
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
//        gridView.setMinimumHeight(5);
//        ViewGroup.LayoutParams layoutParams = gridView.getLayoutParams();
//        layoutParams.height = screenWidth;
//        gridView.setLayoutParams(layoutParams);
        gridView.setAdapter(adapter);
        gridView.setNumColumns(NUM_OF_COLLUMN);
//        gridView.setColumnWidth((int) ((gridView.getWidth() / NUM_OF_COLLUMN) * 0.9));
//        gridView.setMinimumWidth(screenWidth);
    }

    private void setupGridView2() {

        adapter2 = new Gridview2Adapter(this);
        gridView2 = (GridView) findViewById(R.id.gridview2_puzzle);
//        gridView2.setMinimumHeight(screenWidth);
        gridView2.setAdapter(adapter2);
//        gridView2.setNumColumns(3);
//        gridView2.setColumnWidth((int) ((gridView.getWidth() / NUM_OF_COLLUMN) * 0.9));
//        gridView2.setMinimumWidth(screenWidth);
        //TODO change here
        gridView2.setNumColumns((int)(NUM_OF_COLLUMN/WEIGHT_COEFF));

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


}
