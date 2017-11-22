package com.caniplay.caniplay;

import android.app.Application;
import android.content.Context;

/**
 * Created by francesc on 30/09/17.
 */

public class MyApplication extends Application {
    private static MyApplication sInstance;

    @Override
    public void onCreate(){
        super.onCreate();
        sInstance = this;
    }

    public static MyApplication getsInstance(){
        return sInstance;
    }

    public static Context getAppContext(){
        return sInstance.getApplicationContext();
    }
}
