package xyz.homapay.hampay.mobile.android.analytics;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import xyz.homapay.hampay.mobile.android.HamPayApplication;

/**
 * Created by amir on 9/13/15.
 */
public class GaAnalyticsEvent {

    private Tracker hamPayGaTracker;

    public GaAnalyticsEvent(Activity activity){

        hamPayGaTracker = ((HamPayApplication) activity.getApplicationContext())
                .getTracker(HamPayApplication.TrackerName.APP_TRACKER);
    }

    public void GaTrackMobileEvent(String category, String action, String label){

        hamPayGaTracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .build());
    }
}
