package com.example.asus.happispellcrossword.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asus.happispellcrossword.R;
import com.example.asus.happispellcrossword.activity.GameActivity;
import com.example.asus.happispellcrossword.model.WordObject;
import com.example.asus.happispellcrossword.model.WordObjectsManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.ArrayList;

/**
 * Created by ThangDuong on 12-May-16.
 */
public class GridviewAdapter extends BaseAdapter {
    public static final String DISABLE = "0";
    public static final String ENABLE = "";
    public static final int NORMAL = 2;
    public static final int MAIN_CLICKED = 3;
    public static final int SUB_CLICKED = 4;
    public static final int TRANSPARENT = 5;
    private static int color[][];//The board must be rectangular + data[x][y]
    private static boolean isAnimation[][];//The board must be rectangular + data[x][y]
    private static WordObject clickedOject = null;
    ObjectAnimator scaleAnimationX = new ObjectAnimator();
    ObjectAnimator scaleAnimationY = new ObjectAnimator();
    //Constructor to resetColor values
    WordObjectsManager objManager = WordObjectsManager.getInstance();
    private Context context;
    private String data[][];//The board must be rectangular + data[x][y]
    private ArrayList<WordObject> listWord = new ArrayList<WordObject>();
    private OnItemGridViewClick gridViewClickListener;
    private Animation animation;
    private DisplayImageOptions opt;

    public GridviewAdapter(Activity context, String[][] data) {

        this.gridViewClickListener = (OnItemGridViewClick) context;
        this.context = context;
        this.data = data;
        color = new int[data.length][data[0].length];
        isAnimation = new boolean[data.length][data[0].length];
        for (int i = 0; i < color.length; i++) {
            for (int j = 0; j < color[0].length; j++)//the board is rectangular
            {
                isAnimation[i][j] = false;
            }
        }
        resetColor();
        onClickCell(objManager.get(0).startX, objManager.get(0).startY);
    }

    public void resetColor() {
        for (int i = 0; i < color.length; i++) {
            for (int j = 0; j < color[0].length; j++)//the board is rectangular
            {
                WordObject obj = objManager.getObjectAt(i, j);
                if (obj != null)
                    color[i][j] = NORMAL;
                else
                    color[i][j] = TRANSPARENT;
            }
        }
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
//        Log.e("GridviewAdapter","data.length = "+data.length+" , data[0].length = "+data[0].length);
        return data.length * data[0].length;//The board must be rectangular
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // LayoutInflater to call external grid_item.xml file

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View gridView;

        if (convertView == null) {
            // get layout from grid_item.xml ( Defined Below )
            gridView = inflater.inflate(R.layout.grid_puzzle_item, null);
        } else {
            gridView = convertView;

        }

        final Button cell = (Button) gridView.findViewById(R.id.button);
        final TextView textViewNumberQuestion = (TextView) gridView.findViewById(R.id.tvItemSTT);

        //set Row Height
        DisplayMetrics metrics = new DisplayMetrics();
        metrics = context.getResources().getDisplayMetrics();
        cell.setMinimumHeight(0);
//        cell.setHeight(MainActivity.getRowHeight());
        cell.setHeight((int) ((metrics.widthPixels / GameActivity.NUM_OF_ROW) * 0.9));

        //set default text
//        cell.setText(Integer.toString(position));
        cell.setTextColor(Color.BLACK);
        final int positionX = position % GameActivity.NUM_OF_COLLUMN;
        final int positionY = position / GameActivity.NUM_OF_COLLUMN;
        if(data[positionX][positionY]==GridviewAdapter.DISABLE){
            cell.setVisibility(View.INVISIBLE);
        }
        for(WordObject wordObject:listWord){
            int positionImage=0;
            if(wordObject.getOrientation()==WordObject.HORIZONTAL){
                positionImage=wordObject.startX+wordObject.startY*10+wordObject.getResult().length();
            }else
                positionImage=wordObject.startX+(wordObject.startY+wordObject.getResult().length())*10;
            if(position==positionImage){
                cell.setVisibility(View.VISIBLE);
                cell.setBackgroundResource(R.drawable.ic_action_word);
            }
            cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Animation animationScale = AnimationUtils.loadAnimation(context, R.anim.scale);
                    gridView.startAnimation(animationScale);
                }
            });

        }
        return gridView;
    }

    private void onClickCell(int x, int y) {
        //Reset color of the grid
        resetColor();

        WordObject obj = objManager.getObjectAt(x, y);
        if (obj != null)// If clicked inside a word
        {
            obj.setClickedPosition(x, y);
            clickedOject = obj;
            colorSurroundCells(x, y);

        }
    }

    private void colorSurroundCells(int x, int y)// x,y is the starting point, cell is the work obj to check its length
    {
        color[x][y] = MAIN_CLICKED;
        int tempX = x + 1;// color the subclicked on the right
        while (tempX < data.length && clickedOject.isInsideWord(tempX, y)) {
            color[tempX][y] = SUB_CLICKED;
            tempX++;
        }
        tempX = x - 1;// color the subclicked on the right
        while (tempX >= 0 && clickedOject.isInsideWord(tempX, y)) {
            color[tempX][y] = SUB_CLICKED;
            tempX--;
        }

        int tempY = y + 1;// color the subclicked downward
        while (tempY < data[0].length && clickedOject.isInsideWord(x, tempY)) {
            color[x][tempY] = SUB_CLICKED;
            tempY++;
        }
        tempY = y - 1;// color the subclicked upward
        while (tempY >= 0 && clickedOject.isInsideWord(x, tempY)) {
            color[x][tempY] = SUB_CLICKED;
            tempY--;
        }
        notifyDataSetChanged();
    }

    public void nextClickedPosition() {
        int lastClickedX = WordObject.getClickedPositionX();
        int lastClickedY = WordObject.getClickedPositionY();
        //Horizontal and stop at last digit
        if (clickedOject.getOrientation() == WordObject.HORIZONTAL
                && WordObject.getClickedPositionX() <= clickedOject.startX + clickedOject.getResult().length() - 2) {
            if (lastClickedX < clickedOject.startX + clickedOject.getResult().length())
                lastClickedX++;
        }

        //Vertical and stop at last digit
        if (clickedOject.getOrientation() == WordObject.VERTICAL
                && WordObject.getClickedPositionY() <= clickedOject.startY + clickedOject.getResult().length() - 2) {
            if (lastClickedY < clickedOject.startY + clickedOject.getResult().length())
                lastClickedY++;
        }
        clickedOject.setClickedPosition(lastClickedX, lastClickedY);

//        onClickCell(WordObject.getClickedPositionX(), WordObject.getClickedPositionY());

        colorSurroundCells(lastClickedX, lastClickedY);
    }

    public void backClickedPosition() {
        int lastClickedX = WordObject.getClickedPositionX();
        int lastClickedY = WordObject.getClickedPositionY();
        //Horizontal and stop at last digit
        if (clickedOject.getOrientation() == WordObject.HORIZONTAL
                && WordObject.getClickedPositionX() < clickedOject.startX + clickedOject.getResult().length()) {
            if (lastClickedX > clickedOject.startX)
                lastClickedX--;
        }

        //Vertical and stop at last digit
        if (clickedOject.getOrientation() == WordObject.VERTICAL
                && WordObject.getClickedPositionY() < clickedOject.startY + clickedOject.getResult().length()) {
            if (lastClickedY > clickedOject.startY)
                lastClickedY--;
        }
        clickedOject.setClickedPosition(lastClickedX, lastClickedY);

//        onClickCell(WordObject.getClickedPositionX(), WordObject.getClickedPositionY());

        colorSurroundCells(lastClickedX, lastClickedY);
    }

    private void settingAnimation() {
        scaleAnimationX.setStartDelay(0);
        scaleAnimationX.setRepeatCount(0);
        scaleAnimationX.setRepeatMode(ValueAnimator.REVERSE);
        scaleAnimationX.setInterpolator(new LinearInterpolator());
        scaleAnimationY.setStartDelay(0);
        scaleAnimationY.setRepeatCount(0);
        scaleAnimationY.setRepeatMode(ValueAnimator.REVERSE);
        scaleAnimationY.setInterpolator(new LinearInterpolator());
    }

    private void startScaleAnimation(View view, int duration) {
        scaleAnimationX = ObjectAnimator.ofFloat(view, "scaleX", new float[]{0.5f, 1.0f}).setDuration(duration);
        scaleAnimationY = ObjectAnimator.ofFloat(view, "scaleY", new float[]{0.5f, 1.0f}).setDuration(duration);
        final AnimatorSet animation = new AnimatorSet();
        animation.playTogether(scaleAnimationX, scaleAnimationY);
        animation.start();
    }

    public void setUpListWord(ArrayList<WordObject> listWord) {
        this.listWord = listWord;
    }

    public interface OnItemGridViewClick {
        void onItemGridViewClick(int position);
    }
}
