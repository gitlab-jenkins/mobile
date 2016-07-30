package xyz.homapay.hampay.mobile.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import java.util.HashMap;

import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.service.KeyExchangeService;

/**
 * Created by amir on 7/10/15.
 */

@ReportsCrashes(mailTo = "defects@homapay.com")

public class HamPayApplication extends MultiDexApplication {

    public static int GENERAL_TRACKER = 0;

    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public static final String TAG = HamPayApplication.class.getSimpleName();


    @Override
    public void onCreate()
    {
        super.onCreate();
        ACRA.init(this);

        Intent intent = new Intent(getApplicationContext(), KeyExchangeService.class);
        getApplicationContext().startService(intent);


    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }


    public synchronized Tracker getTracker(TrackerName trackerId) {
        Log.d(TAG, "getTracker()");
        if (!mTrackers.containsKey(trackerId)) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);

            // Global GA Settings
            // <!-- Google Analytics SDK V4 BUG20141213 Using a GA global xml freezes the app! Do config by coding. -->
            analytics.setDryRun(false);

            analytics.getLogger().setLogLevel(Logger.LogLevel.ERROR);
            //analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);

            // Create a new tracker
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(R.xml.app_tracker) : null;
            if (t != null) {
                t.enableAdvertisingIdCollection(true);
            }
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }

    static AppState applicationState;


    public static AppState getAppState(){
        return applicationState;
    }

    public static void setAppSate(AppState appState){
        applicationState = appState;
    }



}
