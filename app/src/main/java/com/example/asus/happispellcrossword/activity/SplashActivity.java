package com.example.asus.happispellcrossword.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.content.IntentCompat;
import android.widget.ProgressBar;

import com.example.asus.happispellcrossword.R;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Asus on 6/17/2016.
 */
public class SplashActivity extends Activity {
    Handler handler;
    //dùng AtomicBoolean để thay thế cho boolean
    AtomicBoolean isrunning=new AtomicBoolean(false);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        handler=new Handler(){
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //msg.arg1 là giá trị được trả về trong message
                //của tiến trình con
                if(msg.arg1==100){
                    Intent intent=new Intent(SplashActivity.this,HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
                }
            }
        };
        doStart();
    }

    public void doStart()
    {
        isrunning.set(false);
        //tạo 1 tiến trình CON
        Thread th=new Thread(new Runnable() {
            @Override
            public void run() {
                //vòng lặp chạy 100 lần
                for(int i=1;i<=100 && isrunning.get();i++)
                {
                    SystemClock.sleep(20);
                    //lấy message từ Main thread
                    Message msg=handler.obtainMessage();
                    //gán giá trị vào cho arg1 để gửi về Main thread
                    msg.arg1=i;
                    //gửi lại Message này về cho Main Thread
                    handler.sendMessage(msg);
                }
            }
        });
        isrunning.set(true);
        //kích hoạt tiến trình
        th.start();
    }
}
