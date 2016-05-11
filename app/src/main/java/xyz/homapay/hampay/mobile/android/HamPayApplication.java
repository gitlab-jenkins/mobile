package xyz.homapay.hampay.mobile.android;

import android.app.Application;
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

@ReportsCrashes(mailTo = "amirh.sharafkar@gmail.com,defects@homapay.com")

public class HamPayApplication extends MultiDexApplication {

    private static final String PROPERTY_ID = "UA-67427017-1";

    public static int GENERAL_TRACKER = 0;

    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
        ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public static final String TAG = HamPayApplication.class.getSimpleName();

    private static HamPayApplication mInstance;


    @Override
    public void onCreate()
    {
        super.onCreate();

        mInstance = this;

        ACRA.init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.e("ee", "");
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
