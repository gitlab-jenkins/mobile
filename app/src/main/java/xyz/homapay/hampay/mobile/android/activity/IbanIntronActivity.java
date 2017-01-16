package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.IBANChangeRequest;
import xyz.homapay.hampay.common.core.model.request.IBANConfirmationRequest;
import xyz.homapay.hampay.common.core.model.response.IBANChangeResponse;
import xyz.homapay.hampay.common.core.model.response.IBANConfirmationResponse;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.animation.Collapse;
import xyz.homapay.hampay.mobile.android.animation.Expand;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestIBANChange;
import xyz.homapay.hampay.mobile.android.async.task.IBANConfirmationTask;
import xyz.homapay.hampay.mobile.android.async.task.impl.OnTaskCompleted;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.edittext.FacedEditText;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.dialog.iban.IbanAction;
import xyz.homapay.hampay.mobile.android.dialog.iban.IbanChangeDialog;
import xyz.homapay.hampay.mobile.android.firebase.LogEvent;
import xyz.homapay.hampay.mobile.android.firebase.service.ServiceEvent;
import xyz.homapay.hampay.mobile.android.img.ImageHelper;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

public class IbanIntronActivity extends AppCompatActivity implements OnTaskCompleted, IbanChangeDialog.IbanChangeDialogListener {

    private HamPayDialog hamPayDialog;
    private Activity activity;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;
    private FacedTextView ibanFirstSegmentText;
    private FacedTextView ibanSecondSegmentText;
    private FacedTextView ibanThirdSegmentText;
    private FacedTextView ibanFourthSegmentText;
    private FacedTextView ibanFifthSegmentText;
    private FacedTextView ibanSixthSegmentText;
    private FacedTextView ibanSeventhSegmentText;
    private RelativeLayout[] segmentRelativeLayouts = new RelativeLayout[7];
    private FacedEditText ibanUserName;
    private FacedEditText ibanUserFamily;
    private FacedTextView ibanVerifyButton;
    private String ibanValue = "";
    private IBANChangeRequest ibanChangeRequest;
    private RequestIBANChange requestIBANChange;
    private FacedTextView bankName;
    private ImageView bankLogo;
    private LinearLayout keyboard;
    private PersianEnglishDigit persian = new PersianEnglishDigit();
    private String authToken = "";

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

        Intent intent = getIntent();

        switch (intent.getExtras().getInt(Constants.IBAN_SOURCE_ACTION)) {
            case Constants.IBAN_SOURCE_PAYMENT:
                setContentView(R.layout.activity_intro_iban_payment);
                break;
            case Constants.IBAN_SOURCE_SETTING:
                setContentView(R.layout.activity_intro_iban_setting);
                break;
        }


        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
        activity = this;
        context = this;
        hamPayDialog = new HamPayDialog(activity);

        keyboard = (LinearLayout) findViewById(R.id.keyboard);
        authToken = prefs.getString(Constants.LOGIN_TOKEN_ID, "");

        ibanFirstSegmentText = (FacedTextView) findViewById(R.id.iban_first_segment);
        ibanSecondSegmentText = (FacedTextView) findViewById(R.id.iban_second_segment);
        ibanThirdSegmentText = (FacedTextView) findViewById(R.id.iban_third_segment);
        ibanFourthSegmentText = (FacedTextView) findViewById(R.id.iban_fourth_segment);
        ibanFifthSegmentText = (FacedTextView) findViewById(R.id.iban_fifth_segment);
        ibanSixthSegmentText = (FacedTextView) findViewById(R.id.iban_sixth_segment);
        ibanSeventhSegmentText = (FacedTextView) findViewById(R.id.iban_seventh_segment);

        segmentRelativeLayouts[0] = (RelativeLayout) findViewById(R.id.iban_first_segment_l);
        segmentRelativeLayouts[1] = (RelativeLayout) findViewById(R.id.iban_second_segment_l);
        segmentRelativeLayouts[2] = (RelativeLayout) findViewById(R.id.iban_third_segment_l);
        segmentRelativeLayouts[3] = (RelativeLayout) findViewById(R.id.iban_fourth_segment_l);
        segmentRelativeLayouts[4] = (RelativeLayout) findViewById(R.id.iban_fifth_segment_l);
        segmentRelativeLayouts[5] = (RelativeLayout) findViewById(R.id.iban_sixth_segment_l);
        segmentRelativeLayouts[6] = (RelativeLayout) findViewById(R.id.iban_seventh_segment_l);

        ibanUserName = (FacedEditText) findViewById(R.id.ibanUserName);
        ibanUserName.setOnTouchListener((v, event) -> {
            ibanUserName.setCursorVisible(true);
            ibanUserFamily.setCursorVisible(false);
            new Collapse(keyboard).animate();
            return false;
        });
        ibanUserFamily = (FacedEditText) findViewById(R.id.ibanUserFamily);
        ibanUserFamily.setOnTouchListener((v, event) -> {
            ibanUserName.setCursorVisible(false);
            ibanUserFamily.setCursorVisible(true);
            new Collapse(keyboard).animate();
            return false;
        });
        ibanUserFamily.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                ibanUserFamily.setCursorVisible(true);
            }else {
                ibanUserFamily.setCursorVisible(false);
            }
        });
        ibanUserFamily.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    ibanUserFamily.setCursorVisible(false);
                    new Expand(keyboard).animate();
                }
                return false;
            }
        });
        bankName = (FacedTextView) findViewById(R.id.bank_name);
        bankLogo = (ImageView) findViewById(R.id.bank_logo);
        ibanVerifyButton = (FacedTextView) findViewById(R.id.iban_verify_button);
        ibanVerifyButton.setOnClickListener(v -> {

            String userBank = bankName.getText().toString().trim();
            String userName = ibanUserName.getText().toString().trim();
            String userFamily = ibanUserFamily.getText().toString().trim();

            if (userName.length() <= 1) {
                Toast.makeText(activity, getString(R.string.iban_empty_name), Toast.LENGTH_SHORT).show();
                return;
            }

            if (userFamily.length() <= 1) {
                Toast.makeText(activity, getString(R.string.iban_empty_family), Toast.LENGTH_SHORT).show();
                return;
            }

            if (ibanValue.length() != 24){
                Toast.makeText(activity, getString(R.string.iban_value_empty), Toast.LENGTH_SHORT).show();
                return;
            }

            IbanChangeDialog cardNumberDialog = new IbanChangeDialog();
            Bundle bundle = new Bundle();
            bundle.putString(Constants.IBAN_NUMBER, ibanValue);
            bundle.putString(Constants.IBAN_OWNER_NAME, userName);
            bundle.putString(Constants.IBAN_OWNER_FAMILY, userFamily);
            bundle.putString(Constants.IBAN_BANK_NAME, userBank);
            cardNumberDialog.setArguments(bundle);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(cardNumberDialog, null);
            fragmentTransaction.commitAllowingStateLoss();
        });
    }


    @Override
    public void OnTaskPreExecute() {
        hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
    }

    @Override
    public void OnTaskExecuted(Object object) {

        hamPayDialog.dismisWaitingDialog();
        ibanUserFamily.setCursorVisible(false);
        ibanUserName.setCursorVisible(false);

        if (object != null) {
            if (object.getClass().equals(ResponseMessage.class)) {
                final ResponseMessage responseMessage = (ResponseMessage) object;
                switch (responseMessage.getService().getServiceDefinition()) {
                    case IBAN_CONFIRMATION:
                        ResponseMessage<IBANConfirmationResponse> ibanConfirmation = (ResponseMessage) object;
                        switch (responseMessage.getService().getResultStatus()) {
                            case SUCCESS:
                                ibanVerifyButton.setVisibility(View.VISIBLE);
                                editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                                editor.commit();
                                bankLogo.setVisibility(View.VISIBLE);
                                bankName.setVisibility(View.VISIBLE);
                                new Collapse(keyboard).animate();
                                if (ibanConfirmation.getService().getImageId() != null) {
                                    bankLogo.setTag(ibanConfirmation.getService().getImageId());
                                    ImageHelper.getInstance(activity).imageLoader(ibanConfirmation.getService().getImageId(), bankLogo, R.drawable.user_placeholder);
                                } else {
                                    bankLogo.setImageResource(R.drawable.user_placeholder);
                                }
                                bankName.setText(ibanConfirmation.getService().getBankName());

                                break;
                            default:
                                hamPayDialog.showFailIBANChangeDialog(
                                        responseMessage.getService().getResultStatus().getCode(),
                                        responseMessage.getService().getResultStatus().getDescription());
                                break;
                        }
                        break;
                }
            }
        }
    }

    @Override
    public void onFinishEditDialog(IbanAction ibanAction) {
        ibanUserFamily.setCursorVisible(true);
        ibanUserName.setCursorVisible(true);
        switch (ibanAction) {
            case ACCEPT:
                ibanChangeRequest = new IBANChangeRequest();
                ibanChangeRequest.setIban(ibanValue);
                ibanChangeRequest.setOwnerFirstName(ibanUserName.getText().toString().trim());
                ibanChangeRequest.setOwnerSurname(ibanUserFamily.getText().toString().trim());
                requestIBANChange = new RequestIBANChange(activity, new RequestIBANChangeTaskCompleteListener(ibanChangeRequest));
                requestIBANChange.execute(ibanChangeRequest);
                break;

            case REJECT:
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.IBAN_CHANGE_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra(Constants.RETURN_IBAN_CONFIRMED, data.getStringExtra(Constants.RETURN_IBAN_CONFIRMED));
                setResult(RESULT_OK, returnIntent);
                activity.finish();
            }
        }
    }

    public void pressKey(View view) {
        if (view.getTag().toString().equals("*")) {
            new Collapse(keyboard).animate();
        } else if (view.getTag().toString().equals("|")) {

            new Expand(keyboard).animate();
            View v = this.getCurrentFocus();
            if (v != null) {
                InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            ibanUserName.clearFocus();
            ibanUserFamily.clearFocus();
            ibanUserName.setCursorVisible(false);
            ibanUserFamily.setCursorVisible(false);
        } else {
            inputDigit(view.getTag().toString());
        }
    }

    private void inputDigit(String digit) {
        String ibanSegment = "";
        if (digit.endsWith("d")) {
            ibanVerifyButton.setVisibility(View.GONE);
            bankLogo.setVisibility(View.GONE);
            bankName.setVisibility(View.GONE);
        } else {
            if (ibanValue.length() > 23) return;
            ibanValue += digit;
        }
        switch (ibanValue.length()) {
            case 0:
            case 1:
            case 2:
                ibanSegment = ibanFirstSegmentText.getText().toString();
                if (digit.endsWith("d")) {
                    if (ibanSegment.length() == 0) return;
                    ibanFirstSegmentText.setText(ibanSegment.substring(0, ibanSegment.length() - 1));
                    ibanValue = ibanValue.substring(0, ibanValue.length() - 1);
                    setBorder(ibanValue.length());
                } else {
                    ibanFirstSegmentText.setText(persian.E2P(ibanSegment + digit));
                    setBorder(ibanValue.length() + 1);
                }
                break;

            case 3:
            case 4:
            case 5:
            case 6:
                ibanSegment = ibanSecondSegmentText.getText().toString();
                if (digit.endsWith("d")) {
                    if (ibanSegment.length() == 0) return;
                    ibanSecondSegmentText.setText(ibanSegment.substring(0, ibanSegment.length() - 1));
                    ibanValue = ibanValue.substring(0, ibanValue.length() - 1);
                    setBorder(ibanValue.length());
                } else {
                    ibanSecondSegmentText.setText(persian.E2P(ibanSegment + digit));
                    setBorder(ibanValue.length() + 1);
                }
                break;

            case 7:
            case 8:
            case 9:
            case 10:
                ibanSegment = ibanThirdSegmentText.getText().toString();
                if (digit.endsWith("d")) {
                    if (ibanSegment.length() == 0) return;
                    ibanThirdSegmentText.setText(ibanSegment.substring(0, ibanSegment.length() - 1));
                    ibanValue = ibanValue.substring(0, ibanValue.length() - 1);
                    setBorder(ibanValue.length());
                } else {
                    ibanThirdSegmentText.setText(persian.E2P(ibanSegment + digit));
                    setBorder(ibanValue.length() + 1);
                }
                break;

            case 11:
            case 12:
            case 13:
            case 14:
                ibanSegment = ibanFourthSegmentText.getText().toString();
                if (digit.endsWith("d")) {
                    if (ibanSegment.length() == 0) return;
                    ibanFourthSegmentText.setText(ibanSegment.substring(0, ibanSegment.length() - 1));
                    ibanValue = ibanValue.substring(0, ibanValue.length() - 1);
                } else {
                    ibanFourthSegmentText.setText(persian.E2P(ibanSegment + digit));
                }
                setBorder(ibanValue.length());
                break;

            case 15:
            case 16:
            case 17:
            case 18:
                ibanSegment = ibanFifthSegmentText.getText().toString();
                if (digit.endsWith("d")) {
                    if (ibanSegment.length() == 0) return;
                    ibanFifthSegmentText.setText(ibanSegment.substring(0, ibanSegment.length() - 1));
                    ibanValue = ibanValue.substring(0, ibanValue.length() - 1);
                    setBorder(ibanValue.length());
                } else {
                    ibanFifthSegmentText.setText(persian.E2P(ibanSegment + digit));
                    setBorder(ibanValue.length() + 1);
                }
                break;

            case 19:
            case 20:
            case 21:
            case 22:
                ibanSegment = ibanSixthSegmentText.getText().toString();
                if (digit.endsWith("d")) {
                    if (ibanSegment.length() == 0) return;
                    ibanSixthSegmentText.setText(ibanSegment.substring(0, ibanSegment.length() - 1));
                    ibanValue = ibanValue.substring(0, ibanValue.length() - 1);
                    setBorder(ibanValue.length());
                } else {
                    ibanSixthSegmentText.setText(persian.E2P(ibanSegment + digit));
                    setBorder(ibanValue.length() + 1);
                }
                break;

            case 23:
            case 24:
                ibanSegment = ibanSeventhSegmentText.getText().toString();
                if (digit.endsWith("d")) {
                    if (ibanSegment.length() == 0) return;
                    ibanSeventhSegmentText.setText(ibanSegment.substring(0, ibanSegment.length() - 1));
                    ibanValue = ibanValue.substring(0, ibanValue.length() - 1);
                    setBorder(ibanValue.length());
                } else {
                    ibanSeventhSegmentText.setText(persian.E2P(ibanSegment + digit));
                    setBorder(ibanValue.length() + 1);
                }
                break;
        }
        if (ibanValue.length() == 24) {
            ibanVerifyButton.setVisibility(View.GONE);
            bankLogo.setVisibility(View.GONE);
            bankName.setVisibility(View.GONE);
            segmentRelativeLayouts[6].setBackgroundResource(R.drawable.iban_entry_placeholder);
            new Collapse(keyboard).animate();
            IBANConfirmationRequest ibanConfirmationRequest = new IBANConfirmationRequest();
            ibanConfirmationRequest.setIban(ibanValue);
            new IBANConfirmationTask(activity, IbanIntronActivity.this, ibanConfirmationRequest, authToken).execute();
        }
    }

    private void setBorder(int length) {
        switch (length) {
            case 0:
            case 1:
            case 2:
                for (int i = 0; i < segmentRelativeLayouts.length; i++) {
                    segmentRelativeLayouts[i].setBackgroundResource(R.drawable.iban_empty_placeholder);
                }
                break;

            case 3:
            case 4:
            case 5:
            case 6:
                for (int i = 1; i < segmentRelativeLayouts.length; i++) {
                    segmentRelativeLayouts[i].setBackgroundResource(R.drawable.iban_empty_placeholder);
                }
                segmentRelativeLayouts[0].setBackgroundResource(R.drawable.iban_entry_placeholder);
                break;

            case 7:
            case 8:
            case 9:
            case 10:
                for (int i = 2; i < segmentRelativeLayouts.length; i++) {
                    segmentRelativeLayouts[i].setBackgroundResource(R.drawable.iban_empty_placeholder);
                }
                segmentRelativeLayouts[1].setBackgroundResource(R.drawable.iban_entry_placeholder);
                break;

            case 11:
            case 12:
            case 13:
            case 14:
                for (int i = 3; i < segmentRelativeLayouts.length; i++) {
                    segmentRelativeLayouts[i].setBackgroundResource(R.drawable.iban_empty_placeholder);
                }
                segmentRelativeLayouts[2].setBackgroundResource(R.drawable.iban_entry_placeholder);
                break;

            case 15:
            case 16:
            case 17:
            case 18:
                for (int i = 4; i < segmentRelativeLayouts.length; i++) {
                    segmentRelativeLayouts[i].setBackgroundResource(R.drawable.iban_empty_placeholder);
                }
                segmentRelativeLayouts[3].setBackgroundResource(R.drawable.iban_entry_placeholder);
                break;

            case 19:
            case 20:
            case 21:
            case 22:
                for (int i = 5; i < segmentRelativeLayouts.length; i++) {
                    segmentRelativeLayouts[i].setBackgroundResource(R.drawable.iban_empty_placeholder);
                }
                segmentRelativeLayouts[4].setBackgroundResource(R.drawable.iban_entry_placeholder);
                break;

            case 23:
            case 24:
                for (int i = 6; i < segmentRelativeLayouts.length; i++) {
                    segmentRelativeLayouts[i].setBackgroundResource(R.drawable.iban_empty_placeholder);
                }
                segmentRelativeLayouts[5].setBackgroundResource(R.drawable.iban_entry_placeholder);
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

    public class RequestIBANChangeTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<IBANChangeResponse>> {

        IBANChangeRequest ibanChangeRequest;
        RequestIBANChange requestIBANChange;
        ServiceEvent serviceName;
        LogEvent logEvent = new LogEvent(context);

        public RequestIBANChangeTaskCompleteListener(IBANChangeRequest ibanChangeRequest) {
            this.ibanChangeRequest = ibanChangeRequest;
        }

        @Override
        public void onTaskComplete(ResponseMessage<IBANChangeResponse> ibanChangeResponseMessage) {
            hamPayDialog.dismisWaitingDialog();
            ibanUserFamily.setCursorVisible(false);
            ibanUserName.setCursorVisible(false);
            if (ibanChangeResponseMessage != null) {
                if (ibanChangeResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    ibanVerifyButton.setVisibility(View.VISIBLE);
                    serviceName = ServiceEvent.IBAN_CHANGE_SUCCESS;
                    editor.putBoolean(Constants.SETTING_CHANGE_IBAN_STATUS, true);
                    editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                    editor.commit();
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(Constants.RETURN_IBAN_CONFIRMED, true);
                    setResult(RESULT_OK, returnIntent);
                    activity.finish();

                } else if (ibanChangeResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    serviceName = ServiceEvent.IBAN_CHANGE_FAILURE;
                    forceLogout();
                } else {
                    serviceName = ServiceEvent.IBAN_CHANGE_FAILURE;
                    requestIBANChange = new RequestIBANChange(activity, new RequestIBANChangeTaskCompleteListener(ibanChangeRequest));
                    hamPayDialog.showFailIBANChangeDialog(
                            ibanChangeResponseMessage.getService().getResultStatus().getCode(),
                            ibanChangeResponseMessage.getService().getResultStatus().getDescription());
                }
            } else {
                serviceName = ServiceEvent.IBAN_CHANGE_FAILURE;
                requestIBANChange = new RequestIBANChange(activity, new RequestIBANChangeTaskCompleteListener(ibanChangeRequest));
                hamPayDialog.showFailIBANChangeDialog(
                        Constants.LOCAL_ERROR_CODE,
                        activity.getString(R.string.msg_fail_iban_change));
            }
            logEvent.log(serviceName);
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }
}
