package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;

import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.model.PaymentType;
import xyz.homapay.hampay.mobile.android.model.SucceedPayment;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.CurrencyFormatter;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

public class PaymentCompletedActivity extends AppCompatActivity {

    private FacedTextView amountValue;
    private FacedTextView paymentCode;
    private FacedTextView traceCode;
    private FacedTextView confirmButton;

    private FacedTextView tvCellPhoneOrChargeCodeTitle;
    private FacedTextView tvCellPhoneOrChargeCode;

    private PersianEnglishDigit persianEnglishDigit;
    private CurrencyFormatter formatter;
    private SucceedPayment succeedPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        succeedPayment = (SucceedPayment) intent.getSerializableExtra(Constants.SUCCEED_PAYMENT_INFO);

        if (succeedPayment.getPaymentType() == PaymentType.PAYMENT || succeedPayment.getPaymentType() == PaymentType.PURCHASE) {
            setContentView(R.layout.activity_payment_completed);
            initRegular();
        } else if (succeedPayment.getPaymentType() == PaymentType.BILLS) {
            setContentView(R.layout.activity_bills_completed);
            initRegular();
        } else if (succeedPayment.getPaymentType() == PaymentType.TOP_UP) {
            setContentView(R.layout.activity_top_up_completed);
            initCharge();
        }
    }

    private void initRegular() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(100);

        amountValue = (FacedTextView) findViewById(R.id.amount_value);
        paymentCode = (FacedTextView) findViewById(R.id.payment_code);
        traceCode = (FacedTextView) findViewById(R.id.trace_code);

        confirmButton = (FacedTextView) findViewById(R.id.confirm_button);
        persianEnglishDigit = new PersianEnglishDigit();
        formatter = new CurrencyFormatter();

        amountValue.setText(persianEnglishDigit.E2P(formatter.format(succeedPayment.getAmount())));
        paymentCode.setText(succeedPayment.getCode());
        traceCode.setText(persianEnglishDigit.E2P(succeedPayment.getTrace()));


        confirmButton.setOnClickListener(v -> {
            Intent returnIntent = new Intent();
            returnIntent.putExtra(Constants.ACTIVITY_RESULT, ResultStatus.SUCCESS.ordinal());
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        });
    }

    private void initCharge() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(100);

        tvCellPhoneOrChargeCode = (FacedTextView) findViewById(R.id.tvCellPhoneOrChargeCode);
        tvCellPhoneOrChargeCodeTitle = (FacedTextView) findViewById(R.id.tvCellPhoneOrChargeCodeTitle);

        amountValue = (FacedTextView) findViewById(R.id.amount_value);
        traceCode = (FacedTextView) findViewById(R.id.trace_code);

        confirmButton = (FacedTextView) findViewById(R.id.confirm_button);
        persianEnglishDigit = new PersianEnglishDigit();
        formatter = new CurrencyFormatter();

        amountValue.setText(persianEnglishDigit.E2P(formatter.format(succeedPayment.getAmount())));
        traceCode.setText(persianEnglishDigit.E2P(succeedPayment.getTrace()));

        tvCellPhoneOrChargeCodeTitle.setText(getString(R.string.cell_number));
        tvCellPhoneOrChargeCode.setText(succeedPayment.getCode());

        confirmButton.setOnClickListener(v -> {
            Intent returnIntent = new Intent();
            returnIntent.putExtra(Constants.ACTIVITY_RESULT, ResultStatus.SUCCESS.ordinal());
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(Constants.ACTIVITY_RESULT, ResultStatus.SUCCESS.ordinal());
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
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
