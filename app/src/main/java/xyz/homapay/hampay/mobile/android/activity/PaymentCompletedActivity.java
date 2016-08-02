package xyz.homapay.hampay.mobile.android.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.CurrencyFormatter;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

public class PaymentCompletedActivity extends AppCompatActivity {

    private Bundle bundle;
    private FacedTextView amountValue;
    private FacedTextView paymentCode;
    private FacedTextView traceCode;
    private FacedTextView confirmButton;
    private PersianEnglishDigit persianEnglishDigit;
    private CurrencyFormatter formatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_completed);

        bundle = getIntent().getExtras();

        amountValue = (FacedTextView)findViewById(R.id.amount_value);
        paymentCode = (FacedTextView)findViewById(R.id.payment_code);
        traceCode = (FacedTextView)findViewById(R.id.trace_code);
        confirmButton = (FacedTextView)findViewById(R.id.confirm_button);
        persianEnglishDigit = new PersianEnglishDigit();
        formatter = new CurrencyFormatter();

        amountValue.setText(persianEnglishDigit.E2P(formatter.format(bundle.getLong(Constants.SUCCESS_PAYMENT_AMOUNT))));
        paymentCode.setText(persianEnglishDigit.E2P(bundle.getString(Constants.SUCCESS_PAYMENT_CODE)));
        traceCode.setText(persianEnglishDigit.E2P(bundle.getString(Constants.SUCCESS_PAYMENT_TRACE)));

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
}
