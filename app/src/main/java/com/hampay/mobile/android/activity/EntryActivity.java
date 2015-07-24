package com.hampay.mobile.android.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.hampay.mobile.android.util.Constants;

public class EntryActivity extends ActionBarActivity {

    SharedPreferences prefs;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);

        intent = new Intent();

        if (prefs.getBoolean(Constants.REGISTERED_USER, false)){
            intent.setClass(EntryActivity.this, HamPayLoginActivity.class);
        }else {
            intent.setClass(EntryActivity.this, AppSliderActivity.class);
        }

        finish();

        startActivity(intent);

    }
}
