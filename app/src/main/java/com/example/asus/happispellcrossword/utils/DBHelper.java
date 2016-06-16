package com.example.asus.happispellcrossword.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.asus.happispellcrossword.model.Stage;
import com.example.asus.happispellcrossword.model.WordObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Asus on 6/10/2016.
 */
public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "puzzle3.db";
    public static final String STAGE_TABLE_NAME = "stage_table";
    public static final String STAGE_POSITION = "stage_position";
    public static final String STAGE_IMG_LINK = "stage_img_link";
    public static final String STAGE_DESCRIPTION="stage_description";
    public static final String STAGE_SECOND_COMPLETE="stage_second_complete";
    public static final String QUESTION_TABLE_NAME="question_table";
    public static final String QUESTION_STAGE_POSITION="stage_position";
    public static final String QUESTION_STARTX="starX";
    public static final String QUESTION_STARTY="startY";
    public static final String QUESTION_QUIZ="quiz";
    public static final String QUESTION_RESULT="result";
    public static final String QUESTION_ORIENTATION="orientation";
    public static final String QUESTION_IMG_LINK="img_link";
    public static final String QUESTION_POSITION="position";
    private HashMap hp;

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("create table stage_table (" +
                STAGE_POSITION + " integer primary key, " + STAGE_IMG_LINK + " text, " + STAGE_DESCRIPTION + " text, " + STAGE_SECOND_COMPLETE + " integer)");
        db.execSQL("create table question_table (" +
                QUESTION_STAGE_POSITION + " integer, " + QUESTION_QUIZ + " text, " + QUESTION_RESULT + " text, " + QUESTION_IMG_LINK + " text, " +
                QUESTION_ORIENTATION + " integer, " + QUESTION_STARTX + " integer, " + QUESTION_STARTY + " integer, " + QUESTION_POSITION + " integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS stage_table");
        db.execSQL("DROP TABLE IF EXISTS question_table");
        onCreate(db);
    }

    public boolean insertStage(Stage stage){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(STAGE_POSITION, stage.getPositionStage());
        contentValues.put(STAGE_IMG_LINK, stage.getImageStageLink());
        contentValues.put(STAGE_DESCRIPTION, stage.getDescriptionStage());
        contentValues.put(STAGE_SECOND_COMPLETE, stage.getSecondComplete());
        db.insert(STAGE_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean insertQuestion(Stage stage){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<WordObject> listQuestion=stage.getListQuestion();
        for(WordObject wordObject:listQuestion){
            ContentValues contentValues = new ContentValues();
            contentValues.put(QUESTION_STAGE_POSITION,stage.getPositionStage());
            contentValues.put(QUESTION_QUIZ,wordObject.getQuestion());
            contentValues.put(QUESTION_RESULT,wordObject.getResult());
            contentValues.put(QUESTION_IMG_LINK,wordObject.getImageLink());
            contentValues.put(QUESTION_ORIENTATION,wordObject.getOrientation());
            contentValues.put(QUESTION_STARTX,wordObject.startX);
            contentValues.put(QUESTION_STARTY,wordObject.startY);
            contentValues.put(QUESTION_POSITION,wordObject.getPosition());
            db.insert(QUESTION_TABLE_NAME,null,contentValues);
        }
        return true;
    }


    public ArrayList<Stage> getAllStage(){
        ArrayList<Stage> listStage=new ArrayList<Stage>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from stage_table", null);
        res.moveToFirst();
        while (res.isAfterLast()==false){
            Stage stage=new Stage();
            stage.setPositionStage(res.getInt(res.getColumnIndex(STAGE_POSITION)));
            stage.setImageStageLink(res.getString(res.getColumnIndex(STAGE_IMG_LINK)));
            stage.setDescriptionStage(res.getString(res.getColumnIndex(STAGE_DESCRIPTION)));
            stage.setSecondComplete(res.getInt(res.getColumnIndex(STAGE_SECOND_COMPLETE)));
            listStage.add(stage);
            res.moveToNext();
        }
        return listStage;
    }
    public ArrayList<WordObject> getListQuestion(int positionStage){
        ArrayList<WordObject> listQuestion=new ArrayList<WordObject>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from question_table", null);
        res.moveToFirst();
        while (res.isAfterLast()==false){
            WordObject wordObject=new WordObject();
            int stagePosition=res.getInt(res.getColumnIndex(QUESTION_STAGE_POSITION));
            if(stagePosition==positionStage){
                wordObject.setQuestion(res.getString(res.getColumnIndex(QUESTION_QUIZ)));
                wordObject.setResult(res.getString(res.getColumnIndex(QUESTION_RESULT)));
                wordObject.setImageLink(res.getString(res.getColumnIndex(QUESTION_IMG_LINK)));
                wordObject.setOrientation(res.getInt(res.getColumnIndex(QUESTION_ORIENTATION)));
                wordObject.setPosition(res.getInt(res.getColumnIndex(QUESTION_POSITION)));
                wordObject.startX=res.getInt(res.getColumnIndex(QUESTION_STARTX));
                wordObject.startY=res.getInt(res.getColumnIndex(QUESTION_STARTY));
                listQuestion.add(wordObject);
            }
            res.moveToNext();
        }
        return listQuestion;
    }

    public boolean updateStage(Stage stage){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(STAGE_IMG_LINK, stage.getImageStageLink());
        contentValues.put(STAGE_DESCRIPTION, stage.getDescriptionStage());
        contentValues.put(STAGE_SECOND_COMPLETE, stage.getSecondComplete());
        if(db.update(STAGE_TABLE_NAME, contentValues, STAGE_POSITION + "=?", new String[]{Integer.toString(stage.getPositionStage())})!=0)
            return true;
        else
            return false;
    }

    public boolean updateQuestion(int stagePosition,WordObject wordObject){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(QUESTION_QUIZ,wordObject.getQuestion());
        contentValues.put(QUESTION_RESULT,wordObject.getResult());
        contentValues.put(QUESTION_IMG_LINK,wordObject.getImageLink());
        contentValues.put(QUESTION_ORIENTATION,wordObject.getOrientation());
        contentValues.put(QUESTION_STARTX,wordObject.startX);
        contentValues.put(QUESTION_STARTY,wordObject.startY);
        contentValues.put(QUESTION_POSITION,wordObject.getPosition());
        if(db.update(QUESTION_TABLE_NAME, contentValues, QUESTION_STAGE_POSITION + "=?", new String[]{Integer.toString(stagePosition)})!=0)
            return true;
        else
            return false;

    }
}
