package xyz.homapay.hampay.mobile.android.firebase;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import xyz.homapay.hampay.mobile.android.firebase.app.AppEvent;
import xyz.homapay.hampay.mobile.android.firebase.service.ServiceEvent;

/**
 * Created by amir on 10/21/16.
 */
public class LogEvent {

    private Bundle bundle = new Bundle();
    private FirebaseAnalytics firebaseAnalytics;

    public LogEvent(Context context){
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public void log(AppEvent appEvent){
        bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, appEvent.ordinal());
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, appEvent.name());
        bundle.putString(FirebaseAnalytics.Param.VALUE, appEvent.name());
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, appEvent.name());
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, appEvent.getClass().getSimpleName());
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LEVEL_UP, bundle);
    }

    public void log(ServiceEvent serviceEvent){
        bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, serviceEvent.ordinal());
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, serviceEvent.name());
        bundle.putString(FirebaseAnalytics.Param.VALUE, serviceEvent.name());
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, serviceEvent.name());
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, serviceEvent.getClass().getSimpleName());
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

}
