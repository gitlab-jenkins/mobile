package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.PurchaseInfoRequest;
import xyz.homapay.hampay.common.core.model.response.PurchaseInfoResponse;
import xyz.homapay.hampay.common.core.model.response.dto.PspInfoDTO;
import xyz.homapay.hampay.common.core.model.response.dto.PurchaseInfoDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.animation.Collapse;
import xyz.homapay.hampay.mobile.android.animation.Expand;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestPurchaseInfo;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.firebase.LogEvent;
import xyz.homapay.hampay.mobile.android.firebase.service.ServiceEvent;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;

public class BusinessPurchaseActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.keyboard)
    LinearLayout keyboard;
    String inputPurchaseCode = "";
    @BindView(R.id.input_digit_1)
    FacedTextView input_digit_1;
    @BindView(R.id.input_digit_2)
    FacedTextView input_digit_2;
    @BindView(R.id.input_digit_3)
    FacedTextView input_digit_3;
    @BindView(R.id.input_digit_4)
    FacedTextView input_digit_4;
    @BindView(R.id.input_digit_5)
    FacedTextView input_digit_5;
    @BindView(R.id.input_digit_6)
    FacedTextView input_digit_6;
    SharedPreferences prefs;
    HamPayDialog hamPayDialog;
    @BindView(R.id.letter_layout)
    LinearLayout letter_layout;
    @BindView(R.id.digit_layout)
    LinearLayout digit_layout;
    private PurchaseInfoDTO purchaseInfoDTO = null;
    private PspInfoDTO pspInfoDTO = null;
    private SharedPreferences.Editor editor;
    private Context context;
    private Activity activity;
    private RequestPurchaseInfo requestPurchaseInfo;
    private PurchaseInfoRequest purchaseInfoRequest;

    public void backActionBar(View view) {
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
        InputMethodManager inputMethodManager = (InputMethodManager) this.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
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

        if (keyboard.getVisibility() == View.VISIBLE) {
            new Collapse(keyboard).animate();
        } else {
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        HamPayApplication.setAppSate(AppState.Stoped);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_purchase);
        ButterKnife.bind(this);

        context = this;
        activity = BusinessPurchaseActivity.this;
        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
        hamPayDialog = new HamPayDialog(activity);
    }

    @Override
    public void onClick(View v) {

        Intent intent;

        switch (v.getId()) {

            case R.id.businesses_list:
                intent = new Intent();
                intent.setClass(activity, BusinessesListActivity.class);
                startActivity(intent);
                break;

            case R.id.payment_button:

                new Collapse(keyboard).animate();

                if (inputPurchaseCode.length() == 6) {
                    requestPurchaseInfo = new RequestPurchaseInfo(activity, new RequestPurchaseInfoTaskCompleteListener());
                    purchaseInfoRequest = new PurchaseInfoRequest();
                    purchaseInfoRequest.setPurchaseCode(inputPurchaseCode);
                    requestPurchaseInfo.execute(purchaseInfoRequest);
                    input_digit_1.setText("");
                    input_digit_2.setText("");
                    input_digit_3.setText("");
                    input_digit_4.setText("");
                    input_digit_5.setText("");
                    input_digit_6.setText("");
                    inputPurchaseCode = "";
                } else {
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

    private void inputDigit(String digit) {
        if (inputPurchaseCode.length() <= 5) {

            switch (inputPurchaseCode.length()) {
                case 0:
                    if (digit.equals("d")) {
                        input_digit_1.setText("");
                    } else {
                        input_digit_1.setText(digit);
                    }
                    input_digit_2.setText("");
                    input_digit_3.setText("");
                    input_digit_4.setText("");
                    input_digit_5.setText("");
                    input_digit_6.setText("");
                    break;

                case 1:
                    if (digit.equals("d")) {
                        input_digit_2.setText("");
                    } else {
                        input_digit_2.setText(digit);
                    }
                    input_digit_3.setText("");
                    input_digit_4.setText("");
                    input_digit_5.setText("");
                    input_digit_6.setText("");
                    break;
                case 2:
                    if (digit.equals("d")) {
                        input_digit_3.setText("");
                    } else {
                        input_digit_3.setText(digit);
                    }
                    input_digit_4.setText("");
                    input_digit_5.setText("");
                    input_digit_6.setText("");
                    break;
                case 3:
                    if (digit.equals("d")) {
                        input_digit_4.setText("");
                    } else {
                        input_digit_4.setText(digit);
                    }
                    input_digit_5.setText("");
                    input_digit_6.setText("");
                    break;
                case 4:
                    if (digit.equals("d")) {
                        input_digit_5.setText("");
                    } else {
                        input_digit_5.setText(digit);
                    }
                    input_digit_6.setText("");
                    break;
                case 5:
                    if (digit.equals("d")) {
                        input_digit_6.setText("");
                    } else {
                        input_digit_6.setText(digit);
                    }
                    break;
            }

        }

        if (digit.contains("d")) {
            if (inputPurchaseCode.length() > 0) {
                inputPurchaseCode = inputPurchaseCode.substring(0, inputPurchaseCode.length() - 1);
                if (inputPurchaseCode.length() == 5) {
                    input_digit_6.setText("");
                }
                if (inputPurchaseCode.length() == 4) {
                    input_digit_5.setText("");
                } else if (inputPurchaseCode.length() == 3) {
                    input_digit_4.setText("");
                } else if (inputPurchaseCode.length() == 2) {
                    input_digit_3.setText("");
                } else if (inputPurchaseCode.length() == 1) {
                    input_digit_2.setText("");
                } else if (inputPurchaseCode.length() == 0) {
                    input_digit_1.setText("");
                }
            }
        } else {
            if (inputPurchaseCode.length() <= 5) {
                inputPurchaseCode += digit;
            }
        }
    }

    public void pressKey(View view) {
        if (view.getTag().toString().equals("-")) {
            letter_layout.setVisibility(View.GONE);
            digit_layout.setVisibility(View.VISIBLE);
        } else if (view.getTag().toString().equals("+")) {
            letter_layout.setVisibility(View.VISIBLE);
            digit_layout.setVisibility(View.GONE);
        } else {
            inputDigit(view.getTag().toString());
        }
    }

    private void forceLogout() {
        editor.remove(Constants.LOGIN_TOKEN_ID);
        editor.commit();
        Intent intent = new Intent();
        intent.setClass(context, HamPayLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (activity != null) {
            finish();
            startActivity(intent);
        }
    }

    public class RequestPurchaseInfoTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<PurchaseInfoResponse>> {

        @Override
        public void onTaskComplete(ResponseMessage<PurchaseInfoResponse> purchaseInfoResponseMessage) {

            hamPayDialog.dismisWaitingDialog();
            ServiceEvent serviceName;
            LogEvent logEvent = new LogEvent(context);

            if (purchaseInfoResponseMessage != null) {
                if (purchaseInfoResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    serviceName = ServiceEvent.PURCHASE_INFO_SUCCESS;
                    purchaseInfoDTO = purchaseInfoResponseMessage.getService().getPurchaseInfo();
                    pspInfoDTO = purchaseInfoResponseMessage.getService().getPurchaseInfo().getPspInfo();

                    if (purchaseInfoDTO != null) {
                        Intent intent = new Intent();
                        intent.putExtra(Constants.PURCHASE_INFO, purchaseInfoDTO);
                        intent.putExtra(Constants.PSP_INFO, pspInfoDTO);
                        intent.setClass(context, RequestBusinessPayDetailActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(context, getString(R.string.msg_not_found_pending_payment_code), Toast.LENGTH_LONG).show();
                        finish();
                    }

                } else if (purchaseInfoResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    serviceName = ServiceEvent.PURCHASE_INFO_FAILURE;
                    forceLogout();
                } else {
                    serviceName = ServiceEvent.PURCHASE_INFO_FAILURE;
                    requestPurchaseInfo = new RequestPurchaseInfo(context, new RequestPurchaseInfoTaskCompleteListener());
                    new HamPayDialog(activity).showFailPurchaseInfoDialog(
                            purchaseInfoResponseMessage.getService().getResultStatus().getCode(),
                            purchaseInfoResponseMessage.getService().getResultStatus().getDescription());
                }
            } else {
                serviceName = ServiceEvent.PURCHASE_INFO_FAILURE;
                requestPurchaseInfo = new RequestPurchaseInfo(context, new RequestPurchaseInfoTaskCompleteListener());
                new HamPayDialog(activity).showFailPurchaseInfoDialog(
                        Constants.LOCAL_ERROR_CODE,
                        getString(R.string.msg_fail_fetch_latest_payment));
            }
            logEvent.log(serviceName);

        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }

}
