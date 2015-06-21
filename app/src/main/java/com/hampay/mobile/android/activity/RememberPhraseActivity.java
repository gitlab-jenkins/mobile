package com.hampay.mobile.android.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.hampay.mobile.android.R;

public class RememberPhraseActivity extends ActionBarActivity {

    CardView keepOn_CardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remember_phrase);

        keepOn_CardView = (CardView) findViewById(R.id.keepOn_CardView);
        keepOn_CardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(RememberPhraseActivity.this, CompleteRegistrationActivity.class);
                startActivity(intent);
            }
        });
    }
}
