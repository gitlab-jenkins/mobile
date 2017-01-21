package xyz.homapay.hampay.mobile.android.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.util.Constants;

public class UserManualActivity extends AppCompatActivity {

    @BindView(R.id.close_user_manual)
    FacedTextView close_user_manual;
    @BindView(R.id.user_manual_text)
    FacedTextView user_manual_text;
    @BindView(R.id.user_manual_title)
    FacedTextView user_manual_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_manual);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        user_manual_text.setText(getString(bundle.getInt(Constants.USER_MANUAL_TEXT)));
        user_manual_title.setText(getString(bundle.getInt(Constants.USER_MANUAL_TITLE)));
        close_user_manual.setOnClickListener(v -> finish());
    }

}
