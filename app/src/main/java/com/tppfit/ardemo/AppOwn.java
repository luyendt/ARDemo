package com.tppfit.ardemo;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by luyendt on 9/14/2016.
 */
public class AppOwn extends Application{
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public static Context getAppOwnContext(){
        return  context;
    }

    public static Bitmap getImageBm(){
        Bitmap bitmap = BitmapFactory.decodeResource(getAppOwnContext().getResources(), R.drawable.icon_android);
        return bitmap;
    }
}
