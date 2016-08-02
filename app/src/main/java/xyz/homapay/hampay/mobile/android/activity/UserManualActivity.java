package xyz.homapay.hampay.mobile.android.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.util.Constants;

public class UserManualActivity extends AppCompatActivity {

    FacedTextView close_user_manual;
    FacedTextView user_manual_text;
    FacedTextView user_manual_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_manual);

        Bundle bundle = getIntent().getExtras();

        user_manual_text = (FacedTextView)findViewById(R.id.user_manual_text);
        user_manual_title = (FacedTextView)findViewById(R.id.user_manual_title);
        user_manual_text.setText(getString(bundle.getInt(Constants.USER_MANUAL_TEXT)));
        user_manual_title.setText(getString(bundle.getInt(Constants.USER_MANUAL_TITLE)));

        close_user_manual = (FacedTextView) findViewById(R.id.close_user_manual);
        close_user_manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
