package xyz.homapay.hampay.mobile.android.firebase;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import xyz.homapay.hampay.mobile.android.firebase.app.AppEvent;
import xyz.homapay.hampay.mobile.android.firebase.service.ServiceEvent;

/**
 * Created by mohammad on 1/28/2017 AD.
 */

public class EventLogger {

    private static EventLogger instance;
    private Bundle bundle;
    private Context ctx;

    private EventLogger(Context ctx) {
        this.ctx = ctx;
    }

    public static EventLogger getInstance(Context ctx) {
        if (instance == null)
            instance = new EventLogger(ctx);
        return instance;
    }

    public void log(AppEvent appEvent) {
        bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, appEvent.name());
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, appEvent.name());
        bundle.putString(FirebaseAnalytics.Param.VALUE, appEvent.name());
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, appEvent.name());
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, appEvent.getClass().getSimpleName());
        FirebaseAnalytics.getInstance(ctx).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    public void log(ServiceEvent serviceName) {
        bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, serviceName.name());
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, serviceName.name());
        bundle.putString(FirebaseAnalytics.Param.VALUE, serviceName.name());
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, serviceName.name());
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, serviceName.getClass().getSimpleName());
        FirebaseAnalytics.getInstance(ctx).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

}
