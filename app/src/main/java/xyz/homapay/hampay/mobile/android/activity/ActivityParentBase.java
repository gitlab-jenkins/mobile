package xyz.homapay.hampay.mobile.android.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import br.com.goncalves.pugnotification.notification.PugNotification;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;

/**
 * Created by mohammad on 1/23/2017 AD.
 */

public class ActivityParentBase extends AppCompatActivity {

    protected Context ctx = this;

    @Override
    protected void onResume() {
        super.onResume();
        PugNotification.with(ctx).cancel(Constants.PAYMENT_NOTIFICATION_IDENTIFIER);
        HamPayApplication.setAppSate(AppState.Resumed);
    }

    @Override
    protected void onPause() {
        super.onPause();
        HamPayApplication.setAppSate(AppState.Paused);
    }

    @Override
    protected void onStop() {
        super.onStop();
        HamPayApplication.setAppSate(AppState.Stoped);
    }

}
