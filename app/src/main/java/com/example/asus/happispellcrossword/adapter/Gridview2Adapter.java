package com.example.asus.happispellcrossword.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.asus.happispellcrossword.R;
import com.example.asus.happispellcrossword.activity.GameActivity;
import com.example.asus.happispellcrossword.model.WordObject;
import com.example.asus.happispellcrossword.model.WordObjectsManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * Created by ThangDuong on 12-May-16.
 */
public class Gridview2Adapter extends BaseAdapter {
    public static final String DISABLE = "0";
    public static final String ENABLE = "";
    WordObjectsManager objManager = WordObjectsManager.getInstance();
    private Context context;
    private String data = new String();

    public Gridview2Adapter(Activity context) {
        this.context = context;
        //assign data
        for(WordObject object:objManager.getObjectArrayList())
        {
            if(object!=null)
                data+=object.getResult();
        }
    }

    @Override
    public int getCount() {
        return data.length();
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
            gridView = inflater.inflate(R.layout.grid2_puzzle_item, null);
        } else {
            gridView = convertView;
        }

        final Button cell = (Button) gridView.findViewById(R.id.button2);
        //set Row Height the same as in Gridview 1
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        ViewGroup.LayoutParams layoutParams = cell.getLayoutParams();
        layoutParams.height = (int) ((metrics.heightPixels / GameActivity.NUM_OF_ROW)* 0.95);
        cell.setLayoutParams(layoutParams);
//        cell.setHeight(MainActivity.getRowHeight());
        cell.setHeight((int) ((metrics.widthPixels / GameActivity.NUM_OF_ROW) * 0.9));
        cell.setText(Character.toString(data.charAt(position)));
        cell.setTextColor(Color.BLACK);
        return gridView;
    }

    private void onClickCell(int x, int y) {
        //Reset color of the grid
    }
}
