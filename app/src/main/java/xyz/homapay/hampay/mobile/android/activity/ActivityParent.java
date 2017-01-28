package xyz.homapay.hampay.mobile.android.activity;

import android.content.Intent;

import br.com.goncalves.pugnotification.notification.PugNotification;
import xyz.homapay.hampay.mobile.android.util.AppManager;
import xyz.homapay.hampay.mobile.android.util.Constants;

/**
 * Created by mohammad on 1/23/2017 AD.
 */

public class ActivityParent extends ActivityParentBase {

    @Override
    protected void onResume() {
        super.onResume();
        if ((System.currentTimeMillis() - AppManager.getMobileTimeout(ctx) > Constants.MOBILE_TIME_OUT_INTERVAL)) {
            Intent intent = new Intent();
            intent.setClass(ctx, HamPayLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        PugNotification.with(ctx).cancel(Constants.PAYMENT_NOTIFICATION_IDENTIFIER);
        if ((System.currentTimeMillis() - AppManager.getMobileTimeout(ctx) > Constants.MOBILE_TIME_OUT_INTERVAL)) {
            Intent intent = new Intent();
            intent.setClass(ctx, HamPayLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
        }
    }

}
