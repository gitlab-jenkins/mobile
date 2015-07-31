package com.hampay.mobile.android.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.hampay.mobile.android.R;
import com.hampay.mobile.android.util.Constants;

public class ServerSelectActivity extends ActionBarActivity {


    Button sima;
    Button england;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_select);
        sima = (Button)findViewById(R.id.sima);
        england = (Button)findViewById(R.id.england);

        intent = new Intent();
        intent.setClass(ServerSelectActivity.this, AppSliderActivity.class);

        sima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Constants.SERVER_IP = "192.168.1.132";
//                finish();
//                startActivity(intent);
            }
        });

        england.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Constants.SERVER_IP = "176.58.104.158";
//                finish();
//                startActivity(intent);
            }
        });
    }


}
