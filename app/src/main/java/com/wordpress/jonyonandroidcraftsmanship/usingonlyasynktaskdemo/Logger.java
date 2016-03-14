package com.wordpress.jonyonandroidcraftsmanship.usingonlyasynktaskdemo;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Logger {
    public static void toast(Context context,String string){
        Toast.makeText(context,string,Toast.LENGTH_LONG).show();
    }
    public static void log(String string){
        Log.d("Jony",string);
    }
}
