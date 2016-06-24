package com.example.asus.happispellcrossword.utils;

import android.app.Activity;
import android.media.MediaPlayer;

import com.example.asus.happispellcrossword.R;

/**
 * Created by SON on 2/12/2016.
 */
public class SoundEffect implements MediaPlayer.OnPreparedListener{
    public static SoundEffect instance;
    public static SoundEffect getInstance(){
        if(instance==null){
            instance=new SoundEffect();
        }
        return instance;
    }
    public void playWrongWord(Activity activity,MediaPlayer mediaPlayer){
        if(mediaPlayer!=null){
            mediaPlayer.release();
        }
        mediaPlayer=MediaPlayer.create(activity, R.raw.incorrect);
        mediaPlayer.setLooping(false);
        mediaPlayer.setOnPreparedListener(this);
    }
    public void playCorrectWord(Activity activity,MediaPlayer mediaPlayer){
        if(mediaPlayer!=null){
            mediaPlayer.release();
        }
        mediaPlayer=MediaPlayer.create(activity, R.raw.right_answer);
        mediaPlayer.setLooping(false);
        mediaPlayer.setOnPreparedListener(this);
    }
    public void playLevelDone(Activity activity,MediaPlayer mediaPlayer){
        if(mediaPlayer!=null){
            mediaPlayer.release();
        }
        mediaPlayer=MediaPlayer.create(activity, R.raw.done_level);
        mediaPlayer.setLooping(false);
        mediaPlayer.setOnPreparedListener(this);
    }
    public void playStartSound(Activity activity,MediaPlayer mediaPlayer){
        if(mediaPlayer!=null){
            mediaPlayer.release();
        }
        mediaPlayer=MediaPlayer.create(activity, R.raw.happiness);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setLooping(true);
    }
    public void playSplashSound(Activity activity,MediaPlayer mediaPlayer){
        if(mediaPlayer!=null){
            mediaPlayer.release();
        }
        mediaPlayer=MediaPlayer.create(activity, R.raw.happiness);
        mediaPlayer.setOnPreparedListener(this);
    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }
}
