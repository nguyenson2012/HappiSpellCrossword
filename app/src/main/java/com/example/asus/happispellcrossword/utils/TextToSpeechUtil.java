package com.example.asus.happispellcrossword.utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * Created by SON on 2/28/2016.
 */
public class TextToSpeechUtil {
    TextToSpeech textToSpeech;
    public static TextToSpeechUtil inst;
    public TextToSpeechUtil(Context applicationContext){
        textToSpeech=new TextToSpeech(applicationContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });
    }
    public void speakWordOrSentence(String wordOrSentence){
        textToSpeech.speak(wordOrSentence,TextToSpeech.QUEUE_FLUSH,null);
    }
    public void stopTextToSpeech(){
        if(textToSpeech!=null){
            textToSpeech.stop();
            textToSpeech.shutdown();
            inst=null;
        }
    }
    public static TextToSpeechUtil getInst(Context applicationContext){
        inst=new TextToSpeechUtil(applicationContext);
        return inst;
    }
}
