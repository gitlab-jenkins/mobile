package xyz.homapay.hampay.mobile.android.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.animation.Collapse;
import xyz.homapay.hampay.mobile.android.animation.Expand;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

public class ServiceBillsActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences prefs;
    private Context context;
    private PersianEnglishDigit persian;
    private LinearLayout barCodeScanner;
    private LinearLayout keyboard;
    private FacedTextView billServiceId;
    private FacedTextView billServicePayment;
    private boolean billServiceIdFocus = false;
    private boolean billServicePaymentFocus = false;
    private FacedTextView billsMobileButton;

    public void backActionBar(View view){
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
        if ((System.currentTimeMillis() - prefs.getLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis()) > Constants.MOBILE_TIME_OUT_INTERVAL)) {
            Intent intent = new Intent();
            intent.setClass(context, HamPayLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        if ((System.currentTimeMillis() - prefs.getLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis()) > Constants.MOBILE_TIME_OUT_INTERVAL)) {
            Intent intent = new Intent();
            intent.setClass(context, HamPayLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_bills);

        context = this;
        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        persian = new PersianEnglishDigit();
        barCodeScanner = (LinearLayout)findViewById(R.id.barCodeScanner);
        barCodeScanner.setOnClickListener(this);

        keyboard = (LinearLayout)findViewById(R.id.keyboard);
        billServiceId = (FacedTextView)findViewById(R.id.billServiceId);
        billServiceId.setOnClickListener(this);
        billServicePayment = (FacedTextView)findViewById(R.id.billServicePayment);
        billServicePayment.setOnClickListener(this);
        billsMobileButton = (FacedTextView)findViewById(R.id.bills_mobile_button);
        billsMobileButton.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case Constants.BAR_CODE_RESULT:
                if (resultCode == RESULT_OK) {
                    Bundle barCodeResult = data.getExtras();
                    Log.e("Result", barCodeResult.getString(Constants.BAR_CODE_SCAN_RESULT));
                }
                break;
        }

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.barCodeScanner:
                intent = new Intent(context, BarCodeScannerActivity.class);
                startActivityForResult(intent, Constants.BAR_CODE_RESULT);
                break;

            case R.id.billServiceId:
                if (keyboard.getVisibility() == View.GONE) {
                    new Expand(keyboard).animate();
                }
                billServiceIdFocus = true;
                billServicePaymentFocus = false;
                break;

            case R.id.billServicePayment:
                if (keyboard.getVisibility() == View.GONE) {
                    new Expand(keyboard).animate();
                }
                billServiceIdFocus = false;
                billServicePaymentFocus = true;
                break;

            case R.id.bills_mobile_button:
                intent = new Intent(context, BillsPaymentActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (keyboard.getVisibility() == View.VISIBLE){
            new Collapse(keyboard).animate();
        }
        else {
            finish();
        }
    }

    public void pressKey(View view) {
        if (view.getTag().toString().equals("*")) {
            new Collapse(keyboard).animate();
        } else if (view.getTag().toString().equals("|")) {
            new Expand(keyboard).animate();
        } else {
            inputDigit(view.getTag().toString());
        }
    }

    private void inputDigit(String digit){
        if (digit.endsWith("d")){
        }
        if (billServiceIdFocus){
            String code = billServiceId.getText().toString();
            if (digit.endsWith("d")){
                if (code.length() == 0) return;
                billServiceId.setText(code.substring(0, code.length() - 1));
            }else {
                billServiceId.setText(persian.E2P(code + digit));
            }
        }else if (billServicePaymentFocus){
            String code = billServicePayment.getText().toString();
            if (digit.endsWith("d")){
                if (code.length() == 0) return;
                billServicePayment.setText(code.substring(0, code.length() - 1));
            }else {
                billServicePayment.setText(persian.E2P(code + digit));
            }
        }


    }
}
