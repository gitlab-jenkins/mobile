package com.hampay.mobile.android.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.ChangeMemorableWordRequest;
import com.hampay.common.core.model.request.RegistrationMemorableWordEntryRequest;
import com.hampay.common.core.model.response.ChangeMemorableWordResponse;
import com.hampay.common.core.model.response.RegistrationMemorableWordEntryResponse;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.component.FacedEditText;
import com.hampay.mobile.android.component.FacedTextView;
import com.hampay.mobile.android.util.DeviceInfo;
import com.hampay.mobile.android.webservice.WebServices;

import java.util.UUID;

public class ChangeMemorableActivity extends ActionBarActivity {

    CardView keepOn_CardView;
    SharedPreferences prefs;
    FacedEditText memorable_value;

    String currentMemorable = "";
    String newMemorable = "";

    FacedTextView memorable_text;
    FacedTextView keepOn_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_memorable);

        memorable_text = (FacedTextView)findViewById(R.id.memorable_text);
        keepOn_text = (FacedTextView)findViewById(R.id.keepOn_text);

        memorable_value = (FacedEditText)findViewById(R.id.memorable_value);

        prefs = getPreferences(MODE_PRIVATE);

        keepOn_CardView = (CardView) findViewById(R.id.keepOn_CardView);
        keepOn_CardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (memorable_value.getText().toString().length() > 0){
                    if (currentMemorable.length() == 0){
                        currentMemorable = memorable_value.getText().toString();
                        memorable_text.setText(getString(R.string.new_memorable));
                        keepOn_text.setText(getString(R.string.get_new_memorable));
                        memorable_value.setText("");
                    }
                    else if (currentMemorable.length() > 0){
                        newMemorable = memorable_value.getText().toString();
                    }
                }

                if (currentMemorable.length() > 0 && newMemorable.length() > 0){

                    Intent intent = new Intent();
                    intent.setClass(ChangeMemorableActivity.this, ChangeMemorablePassActivity.class);
                    intent.putExtra("currentMemorable", currentMemorable);
                    intent.putExtra("newMemorable", newMemorable);
                    startActivity(intent);
                    finish();

                }

            }
        });



    }



}
