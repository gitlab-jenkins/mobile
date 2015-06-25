package com.hampay.mobile.android.util;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;

/**
 * Created by amir on 6/22/15.
 */
public class DeviceInfo {

    Context context;

    public DeviceInfo(Context context){

        this.context = context;

    }

    public String getIMEI(){

        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();

    }

    public String getDeviceId(){
       return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }



}
