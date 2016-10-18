package xyz.homapay.hampay.mobile.android.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by amir on 10/18/16.
 */
public class PreferencesManager {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public PreferencesManager(Context context){
        pref = context.getSharedPreferences(Constants.APP_PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(Constants.IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(Constants.IS_FIRST_TIME_LAUNCH, true);
    }

    public void setRegistered(boolean isRegistered){
        editor.putBoolean(Constants.REGISTERED_USER, isRegistered);
        editor.commit();
    }

    public boolean isRegistered() {
        return pref.getBoolean(Constants.REGISTERED_USER, false);
    }

}
