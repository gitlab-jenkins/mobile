package com.hampay.mobile.android.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.hampay.mobile.android.R;

public class CongratsAccountActivity extends ActionBarActivity {

    CardView keepOn_CardView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congrats_account);


        keepOn_CardView = (CardView)findViewById(R.id.keepOn_CardView);
        keepOn_CardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


}
