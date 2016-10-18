package xyz.homapay.hampay.mobile.android;

import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import java.util.HashMap;

import xyz.homapay.hampay.mobile.android.model.AppState;

/**
 * Created by amir on 7/10/15.
 */

@ReportsCrashes(mailTo = "defects@homapay.com")

public class HamPayApplication extends MultiDexApplication {

    public static final String TAG = HamPayApplication.class.getSimpleName();

    @Override
    public void onCreate()
    {
        super.onCreate();
        ACRA.init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    static AppState applicationState;


    public static AppState getAppState(){
        return applicationState;
    }

    public static void setAppSate(AppState appState){
        applicationState = appState;
    }



}
