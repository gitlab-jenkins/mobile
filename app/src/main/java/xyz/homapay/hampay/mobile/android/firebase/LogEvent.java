package xyz.homapay.hampay.mobile.android.firebase;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import xyz.homapay.hampay.mobile.android.firebase.app.AppEvent;
import xyz.homapay.hampay.mobile.android.firebase.service.ServiceName;

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
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, appEvent.name());
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, appEvent.name());
        bundle.putString(FirebaseAnalytics.Param.VALUE, appEvent.name());
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, appEvent.name());
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, appEvent.getClass().getSimpleName());
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    public void log(ServiceName serviceName){
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, serviceName.name());
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, serviceName.name());
        bundle.putString(FirebaseAnalytics.Param.VALUE, serviceName.name());
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, serviceName.name());
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, serviceName.getClass().getSimpleName());
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

}
