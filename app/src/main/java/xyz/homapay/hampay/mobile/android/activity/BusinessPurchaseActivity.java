package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.List;

import xyz.homapay.hampay.common.core.model.response.dto.BusinessDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.account.Log;
import xyz.homapay.hampay.mobile.android.adapter.HamPayBusinessesAdapter;
import xyz.homapay.hampay.mobile.android.animation.Collapse;
import xyz.homapay.hampay.mobile.android.animation.Expand;
import xyz.homapay.hampay.mobile.android.async.RequestHamPayBusiness;
import xyz.homapay.hampay.mobile.android.async.RequestSearchHamPayBusiness;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.edittext.FacedEditText;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

public class BusinessPurchaseActivity extends AppCompatActivity implements View.OnClickListener {


    private LinearLayout letter_layout;
    private LinearLayout digit_layout;

    private Context context;
    private Activity activity;

    private PersianEnglishDigit persianEnglishDigit;

    ImageView payment_button;
    LinearLayout displayKeyboard;
    LinearLayout keyboard;
    String inputPurchaseCode = "";
    FacedTextView input_digit_1;
    FacedTextView input_digit_2;
    FacedTextView input_digit_3;
    FacedTextView input_digit_4;
    FacedTextView input_digit_5;
    FacedTextView input_digit_6;

    SharedPreferences prefs;

    HamPayDialog hamPayDialog;

    RelativeLayout businesses_list;

    FacedEditText searchPhraseText;

    InputMethodManager inputMethodManager;


    RequestSearchHamPayBusiness requestSearchHamPayBusiness;
    RequestHamPayBusiness requestHamPayBusiness;



    Tracker hamPayGaTracker;


    public void backActionBar(View view){
        finish();
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
    public void onBackPressed() {

        if (keyboard.getVisibility() == View.VISIBLE){
            new Collapse(keyboard).animate();
        }
        else {
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        HamPayApplication.setAppSate(AppState.Stoped);
        if (requestHamPayBusiness != null){
            if (!requestHamPayBusiness.isCancelled())
                requestHamPayBusiness.cancel(true);
        }

        if (requestSearchHamPayBusiness != null){
            if (!requestSearchHamPayBusiness.isCancelled())
                requestSearchHamPayBusiness.cancel(true);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_purchase);

        context = this;
        activity = BusinessPurchaseActivity.this;

        persianEnglishDigit = new PersianEnglishDigit();

        letter_layout = (LinearLayout)findViewById(R.id.letter_layout);
        digit_layout = (LinearLayout)findViewById(R.id.digit_layout);


        payment_button = (ImageView)findViewById(R.id.payment_button);
        payment_button.setOnClickListener(this);

        keyboard = (LinearLayout)findViewById(R.id.keyboard);
        displayKeyboard = (LinearLayout)findViewById(R.id.displayKeyboard);
        displayKeyboard.setOnClickListener(this);
        input_digit_1 = (FacedTextView)findViewById(R.id.input_digit_1);
        input_digit_2 = (FacedTextView) findViewById(R.id.input_digit_2);
        input_digit_3 = (FacedTextView) findViewById(R.id.input_digit_3);
        input_digit_4 = (FacedTextView) findViewById(R.id.input_digit_4);
        input_digit_5 = (FacedTextView) findViewById(R.id.input_digit_5);
        input_digit_6 = (FacedTextView) findViewById(R.id.input_digit_6);

        businesses_list = (RelativeLayout)findViewById(R.id.businesses_list);
        businesses_list.setOnClickListener(this);
        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);

        hamPayGaTracker = ((HamPayApplication) getApplication())
                .getTracker(HamPayApplication.TrackerName.APP_TRACKER);

        inputMethodManager = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);

        searchPhraseText = (FacedEditText) findViewById(R.id.searchPhraseText);

        hamPayDialog = new HamPayDialog(activity);

    }

    @Override
    public void onClick(View v) {

        Intent intent;

        switch (v.getId()){

            case R.id.businesses_list:
                intent = new Intent();
                intent.setClass(activity, BusinessesListActivity.class);
                startActivity(intent);
                break;

            case R.id.payment_button:

                new Collapse(keyboard).animate();

                if (inputPurchaseCode.length() == 6) {
                    intent = new Intent();
                    intent.putExtra(Constants.BUSINESS_PURCHASE_CODE, persianEnglishDigit.P2E(inputPurchaseCode));
                    intent.setClass(context, RequestBusinessPayDetailActivity.class);
                    startActivity(intent);
                    input_digit_1.setText("");
                    input_digit_2.setText("");
                    input_digit_3.setText("");
                    input_digit_4.setText("");
                    input_digit_5.setText("");
                    input_digit_6.setText("");
                    inputPurchaseCode = "";
                }else {
                    Toast.makeText(context, getString(R.string.msg_incorrect_pending_payment_code), Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.displayKeyboard:
                if (keyboard.getVisibility() != View.VISIBLE)
                    new Expand(keyboard).animate();
                break;

            case R.id.keyboard_dismiss:
                if (keyboard.getVisibility() == View.VISIBLE)
                    new Collapse(keyboard).animate();
                break;
        }
    }

    private void inputDigit(String digit){
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        if (inputPurchaseCode.length() <= 5) {

            switch (inputPurchaseCode.length()) {
                case 0:
                    if (digit.equalsIgnoreCase("d")) {
                        input_digit_1.setText("");
                    } else {
                        input_digit_1.setText(persianEnglishDigit.E2P(digit));
                    }
                    input_digit_2.setText("");
                    input_digit_3.setText("");
                    input_digit_4.setText("");
                    input_digit_5.setText("");
                    input_digit_6.setText("");
                    vibrator.vibrate(20);
                    break;

                case 1:
                    if (digit.equalsIgnoreCase("d")) {
                        input_digit_2.setText("");
                    } else {
                        input_digit_2.setText(persianEnglishDigit.E2P(digit));
                    }
                    input_digit_3.setText("");
                    input_digit_4.setText("");
                    input_digit_5.setText("");
                    input_digit_6.setText("");
                    vibrator.vibrate(20);

                    break;
                case 2:
                    if (digit.equalsIgnoreCase("d")) {
                        input_digit_3.setText("");
                    } else {
                        input_digit_3.setText(persianEnglishDigit.E2P(digit));
                    }
                    input_digit_4.setText("");
                    input_digit_5.setText("");
                    input_digit_6.setText("");
                    vibrator.vibrate(20);
                    break;
                case 3:
                    if (digit.equalsIgnoreCase("d")) {
                        input_digit_4.setText("");
                    } else {
                        input_digit_4.setText(persianEnglishDigit.E2P(digit));
                    }
                    input_digit_5.setText("");
                    input_digit_6.setText("");
                    vibrator.vibrate(20);
                    break;
                case 4:
                    if (digit.equalsIgnoreCase("d")) {
                        input_digit_5.setText("");
                    } else {
                        input_digit_5.setText(persianEnglishDigit.E2P(digit));
                    }
                    input_digit_6.setText("");
                    vibrator.vibrate(20);
                    break;
                case 5:
                    if (digit.equalsIgnoreCase("d")) {
                        input_digit_6.setText("");
                    } else {
                        input_digit_6.setText(persianEnglishDigit.E2P(digit));
                    }
                    vibrator.vibrate(20);
                    break;
            }

        }

        if (digit.contains("d")){
            if (inputPurchaseCode.length() > 0) {
                inputPurchaseCode = inputPurchaseCode.substring(0, inputPurchaseCode.length() - 1);
                if (inputPurchaseCode.length() == 5){
                    input_digit_6.setText("");
                }
                if (inputPurchaseCode.length() == 4){
                    input_digit_5.setText("");
                }
                else if (inputPurchaseCode.length() == 3){
                    input_digit_4.setText("");
                }
                else if (inputPurchaseCode.length() == 2){
                    input_digit_3.setText("");
                }
                else if (inputPurchaseCode.length() == 1){
                    input_digit_2.setText("");
                }
                else if (inputPurchaseCode.length() == 0){
                    input_digit_1.setText("");
                }
            }
        }
        else {
            if (inputPurchaseCode.length() <= 5) {
                inputPurchaseCode += digit;
            }
        }
    }

    public void pressKey(View view){
        if (view.getTag().toString().equalsIgnoreCase("-")){
            letter_layout.setVisibility(View.GONE);
            digit_layout.setVisibility(View.VISIBLE);
        }else if (view.getTag().toString().equalsIgnoreCase("+")) {
            letter_layout.setVisibility(View.VISIBLE);
            digit_layout.setVisibility(View.GONE);
        }
        else {
            inputDigit(view.getTag().toString());
        }
    }

}
