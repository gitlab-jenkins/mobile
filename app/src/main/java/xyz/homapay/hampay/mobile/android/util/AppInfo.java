package xyz.homapay.hampay.mobile.android.util;

import android.content.Context;

import xyz.homapay.hampay.mobile.android.BuildConfig;

/**
 * Created by amir on 9/15/15.
 */
public class AppInfo {

    Context context;


    public AppInfo(Context context){

        this.context = context;
    }


    public int getVersionCode() {

        return BuildConfig.VERSION_CODE;
    }

    public String getVersionName(){
        return BuildConfig.VERSION_NAME;
    }

}
