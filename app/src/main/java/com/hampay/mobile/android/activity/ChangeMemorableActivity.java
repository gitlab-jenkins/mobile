package com.hampay.mobile.android.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.hampay.mobile.android.R;
import com.hampay.mobile.android.component.FacedTextView;
import com.hampay.mobile.android.component.edittext.FacedEditText;
import com.hampay.mobile.android.component.material.ButtonRectangle;
import com.hampay.mobile.android.dialog.HamPayDialog;
import com.hampay.mobile.android.util.Constants;

public class ChangeMemorableActivity extends ActionBarActivity {

    ButtonRectangle keepOn_button;
    SharedPreferences prefs;
    FacedEditText memorable_value;

    String currentMemorable = "";
    String newMemorable = "";

    FacedTextView memorable_text;
    FacedTextView keepOn_text;

    public void contactUs(View view){
        (new HamPayDialog(this)).showContactUsDialog();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_memorable);

        memorable_text = (FacedTextView)findViewById(R.id.memorable_text);
        keepOn_text = (FacedTextView)findViewById(R.id.keepOn_text);

        memorable_value = (FacedEditText)findViewById(R.id.memorable_value);

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);

        keepOn_button = (ButtonRectangle) findViewById(R.id.keepOn_button);
        keepOn_button.setOnClickListener(new View.OnClickListener() {
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
                    intent.putExtra("currentMemorable", prefs.getString(Constants.MEMORABLE_WORD, ""));
                    intent.putExtra("newMemorable", newMemorable);
                    startActivity(intent);
                    finish();

                }

            }
        });



    }



}
