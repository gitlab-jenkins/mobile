package xyz.homapay.hampay.mobile.android;

import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.github.tamir7.contacts.Contacts;

import io.fabric.sdk.android.Fabric;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.TelephonyUtils;

/**
 * Created by amir on 7/10/15.
 */

public class HamPayApplication extends MultiDexApplication {

    public static final String TAG = HamPayApplication.class.getSimpleName();
    static AppState applicationState;

    public static AppState getAppState() {
        return applicationState;
    }

    public static void setAppSate(AppState appState) {
        applicationState = appState;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Contacts.initialize(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

}
