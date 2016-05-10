package xyz.homapay.hampay.mobile.android.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.edittext.FacedEditText;
import xyz.homapay.hampay.mobile.android.component.material.ButtonRectangle;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;

public class ChangeMemorableActivity extends AppCompatActivity {

    FacedTextView keepOn_button;
    SharedPreferences prefs;
    FacedEditText memorable_value;

    String currentMemorable = "";
    String newMemorable = "";

    FacedTextView memorable_text;
    FacedTextView keepOn_text;

    public void contactUs(View view){
        new HamPayDialog(this).showHelpDialog(Constants.HTTPS_SERVER_IP + "/help/changeMemorableWord.html");
    }

    @Override
    protected void onPause() {
        super.onPause();
        HamPayApplication.setAppSate(AppState.Paused);
    }

    @Override
    protected void onStop() {
        super.onStop();
        HamPayApplication.setAppSate(AppState.Stoped);
    }

    @Override
    protected void onResume() {
        super.onResume();
        HamPayApplication.setAppSate(AppState.Resumed);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_memorable);

        memorable_value = (FacedEditText)findViewById(R.id.memorable_value);

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);

        keepOn_button = (FacedTextView) findViewById(R.id.keepOn_button);
        keepOn_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (memorable_value.getText().toString().length() > 0){
                    Intent intent = new Intent();
                    intent.setClass(ChangeMemorableActivity.this, ChangeMemorablePassActivity.class);
                    intent.putExtra("currentMemorable", prefs.getString(Constants.MEMORABLE_WORD, ""));
                    intent.putExtra("newMemorable", memorable_value.getText().toString());
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(getApplicationContext(), getString(R.string.enter_new_memorable_word), Toast.LENGTH_SHORT).show();
                }

            }
        });



    }



}
