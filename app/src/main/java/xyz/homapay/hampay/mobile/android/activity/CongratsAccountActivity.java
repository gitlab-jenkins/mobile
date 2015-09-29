package xyz.homapay.hampay.mobile.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.component.material.ButtonRectangle;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;

public class CongratsAccountActivity extends ActionBarActivity {

    ButtonRectangle keepOn_button;

    public void contactUs(View view){
        (new HamPayDialog(this)).showContactUsDialog();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congrats_account);


        keepOn_button = (ButtonRectangle)findViewById(R.id.keepOn_button);
        keepOn_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setClass(CongratsAccountActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivity(intent);

//                Intent returnIntent = new Intent();
//                returnIntent.putExtra("result", 1023);
//                setResult(1023);
//                finish();
            }
        });

    }


    @Override
    public void onBackPressed() {

        Intent intent = new Intent();
        intent.setClass(CongratsAccountActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);

//        Intent returnIntent = new Intent();
//        returnIntent.putExtra("result", 1023);
//        setResult(1023);
//
//        finish();
    }

}
