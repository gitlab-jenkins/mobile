package com.hampay.mobile.android;

import android.app.Application;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

/**
 * Created by amir on 7/10/15.
 */

@ReportsCrashes(mailTo = "ahooman@gmail.com,amirh.sharafkar@gmail.com")

public class HamPayApplication extends Application {

    @Override
    public void onCreate()
    {
        super.onCreate();
//        ACRA.init(this);
    }

}
