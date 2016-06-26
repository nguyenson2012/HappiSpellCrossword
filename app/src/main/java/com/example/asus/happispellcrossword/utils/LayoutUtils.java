package com.example.asus.happispellcrossword.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;

/**
 * Created by ThangDuong on 26-Jun-16.
 */
public class LayoutUtils {
    private static LayoutUtils ourInstance;

    public static LayoutUtils getInstance(Activity c) {
        if(context==null)
            context = c;
        if(ourInstance == null)
            ourInstance = new LayoutUtils();
        return ourInstance;
    }

    private static final int NUM_OF_GRID_COLLUMN = 10;
    private static final int NUM_OF_GRID_ROW = NUM_OF_GRID_COLLUMN;
    private DisplayMetrics metrics = new DisplayMetrics();
    private static Context context;
    private int screenWidth;
    private int screenHeight;
    private int cellWidth;
    private int cellHeight;
    private int cellTextSize;
    private int cellTextColor;
    private int keyboardWidth;
    private int keyboardHeight;
    private int keyboardTextSize;
    private int keyboardTextColor;
    private int keyboardMargin;

    private LayoutUtils() {
        metrics = context.getResources().getDisplayMetrics();
        screenHeight=metrics.heightPixels;
        screenWidth=metrics.widthPixels;

        //setup Gridview Layout
        setupGrid();
        setupKeyboard();
    }

    private void setupGrid()
    {
        cellHeight = (int)(screenHeight/10*0.9);
        cellWidth = cellHeight;
        cellTextSize = screenHeight/45;
        cellTextColor = Color.BLACK;
    }

    private void setupKeyboard()
    {
        keyboardTextSize = cellTextSize;
        keyboardHeight = cellHeight;
        keyboardWidth = cellWidth;
        keyboardTextColor = Color.WHITE;
        keyboardMargin = 3;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public int getCellWidth() {
        return cellWidth;
    }

    public int getCellHeight() {
        return cellHeight;
    }

    public int getCellTextSize() {
        return cellTextSize;
    }

    public int getCellTextColor() {
        return cellTextColor;
    }

    public static int getNumOfGridRow() {
        return NUM_OF_GRID_ROW;
    }

    public static int getNumOfGridCollumn() {
        return NUM_OF_GRID_COLLUMN;
    }

    public int getKeyboardTextColor() {
        return keyboardTextColor;
    }

    public int getKeyboardWidth() {
        return keyboardWidth;
    }

    public int getKeyboardHeight() {
        return keyboardHeight;
    }

    public int getKeyboardTextSize() {
        return keyboardTextSize;
    }

    public int getKeyboardMargin() {
        return keyboardMargin;
    }
}
