package xyz.homapay.hampay.mobile.android;

import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.p.security.KeyExchangerImpl;
import xyz.homapay.hampay.mobile.android.util.ModelLayerImpl;

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
        new KeyExchangerImpl(new ModelLayerImpl(this), (state, data, message) -> System.out.println(message)).exchange();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

}
