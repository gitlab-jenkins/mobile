package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.request.UtilityBillRequest;
import xyz.homapay.hampay.common.core.model.response.UtilityBillResponse;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.animation.Collapse;
import xyz.homapay.hampay.mobile.android.animation.Expand;
import xyz.homapay.hampay.mobile.android.async.task.UtilityBillTask;
import xyz.homapay.hampay.mobile.android.async.task.impl.OnTaskCompleted;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.BillsValidator;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

public class ServiceBillsActivity extends AppCompatActivity implements View.OnClickListener, OnTaskCompleted {

    @BindView(R.id.keyboard)
    LinearLayout keyboard;
    @BindView(R.id.billId)
    FacedTextView billIdText;
    @BindView(R.id.billIdLayout)
    RelativeLayout billIdLayout;
    @BindView(R.id.payId)
    FacedTextView payIdText;
    @BindView(R.id.payIdLayout)
    RelativeLayout payIdLayout;
    private SharedPreferences prefs;
    private Context context;
    private PersianEnglishDigit persian;
    private boolean billServiceIdFocus = false;
    private boolean billServicePaymentFocus = false;
    private Activity activity;
    private HamPayDialog hamPayDialog;
    private String authToken;

    public void backActionBar(View view) {
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
        ButterKnife.bind(this);

        context = this;
        activity = ServiceBillsActivity.this;
        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        authToken = prefs.getString(Constants.LOGIN_TOKEN_ID, "");
        persian = new PersianEnglishDigit();
        hamPayDialog = new HamPayDialog(activity);
        billServiceIdFocus = true;
        billServicePaymentFocus = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.BAR_CODE_RESULT:
                if (resultCode == RESULT_OK) {
                    Bundle barCodeResult = data.getExtras();
                    String scannedCode = barCodeResult.getString(Constants.BAR_CODE_SCAN_RESULT);
                    billIdText.setText(persian.E2P(scannedCode.substring(0, 13)));
                    payIdText.setText(persian.E2P(scannedCode.substring(13).replaceFirst(Constants.PAY_ID_REGEX, "")));
                }
                break;

            case 47:
                if (resultCode == Activity.RESULT_OK) {
                    billIdText.setText("");
                    payIdText.setText("");
                    billIdLayout.setBackgroundResource(R.drawable.iban_empty_placeholder);
                    payIdLayout.setBackgroundResource(R.drawable.iban_empty_placeholder);
                    new Collapse(keyboard).animate();
                }
                if (resultCode == Activity.RESULT_CANCELED) {
                }
                break;
        }

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.barCodeScanner:
                intent = new Intent(context, BarCodeScannerActivity.class);
                startActivityForResult(intent, Constants.BAR_CODE_RESULT);
                break;

            case R.id.billId:
                if (keyboard.getVisibility() == View.GONE) {
                    new Expand(keyboard).animate();
                }
                billServiceIdFocus = true;
                billServicePaymentFocus = false;
                billIdLayout.setBackgroundResource(R.drawable.iban_entry_placeholder);
                payIdLayout.setBackgroundResource(R.drawable.iban_empty_placeholder);
                break;

            case R.id.payId:
                if (keyboard.getVisibility() == View.GONE) {
                    new Expand(keyboard).animate();
                }
                billServiceIdFocus = false;
                billServicePaymentFocus = true;
                billIdLayout.setBackgroundResource(R.drawable.iban_empty_placeholder);
                payIdLayout.setBackgroundResource(R.drawable.iban_entry_placeholder);
                break;

            case R.id.billsMobileButton:
                String billId = billIdText.getText().toString();
                String payId = payIdText.getText().toString();

                BillsValidator billsValidator = new BillsValidator();
                if (!billsValidator.validateBillId(billId)) {
                    Toast.makeText(activity, getString(R.string.msg_invalid_bills_pay_id), Toast.LENGTH_SHORT).show();
                    return;
                } else if (!billsValidator.validatePayId(payId)) {
                    Toast.makeText(activity, getString(R.string.msg_invalid_bills_pay_id), Toast.LENGTH_SHORT).show();
                    return;
                } else if (!billsValidator.validatePayAndBillId(billId, payId)) {
                    Toast.makeText(activity, getString(R.string.msg_invalid_bills_pay_id), Toast.LENGTH_SHORT).show();
                    return;
                }

                UtilityBillRequest utilityBillRequest = new UtilityBillRequest();
                utilityBillRequest.setBillId(persian.P2E(billIdText.getText().toString()));
                utilityBillRequest.setPayId(persian.P2E(payIdText.getText().toString()));
                new UtilityBillTask(activity, ServiceBillsActivity.this, utilityBillRequest, authToken).execute();
                break;
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

    public void pressKey(View view) {
        if (view.getTag().toString().equals("*")) {
            new Collapse(keyboard).animate();
        } else if (view.getTag().toString().equals("|")) {
            new Expand(keyboard).animate();
        } else {
            inputDigit(view.getTag().toString());
        }
    }

    private void inputDigit(String digit) {
        if (digit.endsWith("d")) {
        }
        if (billServiceIdFocus) {
            String code = billIdText.getText().toString();
            if (digit.endsWith("d")) {
                if (code.length() == 0) return;
                billIdText.setText(code.substring(0, code.length() - 1));
            } else {
                billIdText.setText(persian.E2P(code + digit));
            }
        } else if (billServicePaymentFocus) {
            String code = payIdText.getText().toString();
            if (digit.endsWith("d")) {
                if (code.length() == 0) return;
                payIdText.setText(code.substring(0, code.length() - 1));
            } else {
                payIdText.setText(persian.E2P(code + digit));
            }
        }


    }

    @Override
    public void OnTaskPreExecute() {
        hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
    }

    @Override
    public void OnTaskExecuted(Object object) {
        hamPayDialog.dismisWaitingDialog();

        if (object != null) {
            if (object.getClass().equals(ResponseMessage.class)) {
                ResponseMessage responseMessage = (ResponseMessage) object;
                switch (responseMessage.getService().getServiceDefinition()) {
                    case UTILITY_BILL:
                        ResponseMessage<UtilityBillResponse> utilityBill = (ResponseMessage) object;
                        switch (utilityBill.getService().getResultStatus()) {
                            case SUCCESS:
                                Intent intent = new Intent();
                                intent.setClass(activity, ServiceBillsDetailActivity.class);
                                intent.putExtra(Constants.BILL_INFO, utilityBill.getService().getBillInfoDTO());
                                startActivityForResult(intent, 47);
                                break;
                            default:
                                hamPayDialog.showFailBillInfoDialog(utilityBill.getService().getResultStatus().getCode(), utilityBill.getService().getResultStatus().getDescription());
                                break;
                        }
                        break;
                }
            }
        } else {
            Toast.makeText(activity, getString(R.string.msg_failed_bill_info), Toast.LENGTH_SHORT).show();
        }
    }
}
