package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.material.ButtonRectangle;
import xyz.homapay.hampay.mobile.android.component.material.progressbar.ProgressView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.util.Constants;

public class StartActivity extends Activity {

    ButtonRectangle start_button;

    Activity activity;
    SharedPreferences.Editor editor;

    FacedTextView tc_privacy_text;
    CheckBox tc_privacy_confirm;

//    ProgressView waitingProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        activity = StartActivity.this;

        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();

        tc_privacy_text = (FacedTextView)findViewById(R.id.tc_privacy_text);
        tc_privacy_confirm = (CheckBox)findViewById(R.id.tc_privacy_confirm);

        Spannable tcPrivacySpannable = new SpannableString(getString(R.string.tc_privacy_text));

        ClickableSpan tcClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent intent = new Intent();
                intent.setClass(StartActivity.this, GuideDetailActivity.class);
                intent.putExtra(Constants.WEB_PAGE_ADDRESS, "https://192.168.1.102/hampay/users/tac-file");
                startActivity(intent);
            }
        };

        tcPrivacySpannable.setSpan(tcClickableSpan, 3, 35, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tc_privacy_text.setText(tcPrivacySpannable);
        tc_privacy_text.setMovementMethod(LinkMovementMethod.getInstance());



        ClickableSpan privacySpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent intent = new Intent();
                intent.setClass(StartActivity.this, GuideDetailActivity.class);
                intent.putExtra(Constants.WEB_PAGE_ADDRESS, "https://192.168.1.102/hampay/users/tac-file");
                startActivity(intent);
            }
        };

        tcPrivacySpannable.setSpan(privacySpan, 38, 59, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tc_privacy_text.setText(tcPrivacySpannable);
        tc_privacy_text.setMovementMethod(LinkMovementMethod.getInstance());


//        waitingProgress = (ProgressView)findViewById(R.id.waitingProgress);
//        waitingProgress.start();

        start_button = (ButtonRectangle)findViewById(R.id.start_button);
        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(StartActivity.this, PostStartActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivity(intent);
            }
        });

        start_button.setEnabled(false);
        tc_privacy_confirm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    start_button.setEnabled(true);
                }else {
                    start_button.setEnabled(false);
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        new HamPayDialog(activity).showExitRegistrationDialog();
    }

}
