package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.IBANChangeRequest;
import xyz.homapay.hampay.common.core.model.request.PendingPOListRequest;
import xyz.homapay.hampay.common.core.model.response.IBANChangeResponse;
import xyz.homapay.hampay.common.core.model.response.PendingPOListResponse;
import xyz.homapay.hampay.common.core.model.response.dto.PaymentInfoDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.adapter.PendingPOAdapter;
import xyz.homapay.hampay.mobile.android.animation.Collapse;
import xyz.homapay.hampay.mobile.android.animation.Expand;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestIBANChange;
import xyz.homapay.hampay.mobile.android.async.RequestPendingPOList;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.edittext.FacedEditText;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.firebase.LogEvent;
import xyz.homapay.hampay.mobile.android.firebase.service.ServiceEvent;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.ImageManager;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

public class PaymentRequestListActivity extends AppCompatActivity{

    private Context context;
    private Activity activity;
    private ListView paymentRequestList;
    private List<PaymentInfoDTO> paymentInfoList;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private String authToken = "";
    private PendingPOAdapter pendingPOAdapter;
    private RequestPendingPOList requestPendingPOList;
    private PendingPOListRequest pendingPOListRequest;
    private HamPayDialog hamPayDialog;
    private InputMethodManager inputMethodManager;
    private ImageView hampay_contacts;
    private FacedEditText search_text;
    private SwipeRefreshLayout pullToRefresh;
    private FacedTextView nullPendingText;
    private RelativeLayout search_bar;
    private FacedTextView ibanVerifyButton;
    private String ibanValue = "";
    private RelativeLayout introIbanLayout;
    private ImageView bankLogo;
    private FacedTextView bankName;
    private IBANChangeRequest ibanChangeRequest;
    private RequestIBANChange requestIBANChange;
    private ImageManager imageManager;
    private FacedTextView ibanFirstSegmentText;
    private FacedTextView ibanSecondSegmentText;
    private FacedTextView ibanThirdSegmentText;
    private FacedTextView ibanFourthSegmentText;
    private FacedTextView ibanFifthSegmentText;
    private FacedTextView ibanSixthSegmentText;
    private FacedTextView ibanSeventhSegmentText;
    private RelativeLayout[] segmentRelativeLayouts = new RelativeLayout[7];

    private LinearLayout keyboard;
    private PersianEnglishDigit persian = new PersianEnglishDigit();

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_request_list);

        context = this;
        activity = PaymentRequestListActivity.this;
        hamPayDialog = new HamPayDialog(activity);

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
        authToken =  prefs.getString(Constants.LOGIN_TOKEN_ID, "");

        inputMethodManager = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);

        nullPendingText = (FacedTextView)findViewById(R.id.nullPendingText);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(getString(R.string.payment_request_null_message));
        ImageSpan is = new ImageSpan(context, R.drawable.add_payment_note);
        spannableStringBuilder.setSpan(is, 39, 42, 0);
        nullPendingText.setText(spannableStringBuilder);

        search_bar = (RelativeLayout)findViewById(R.id.search_bar);
        keyboard = (LinearLayout)findViewById(R.id.keyboard);

        introIbanLayout = (RelativeLayout) findViewById(R.id.intro_iban_layout) ;
        ibanFirstSegmentText = (FacedTextView)findViewById(R.id.iban_first_segment);
        ibanSecondSegmentText = (FacedTextView)findViewById(R.id.iban_second_segment);
        ibanThirdSegmentText = (FacedTextView)findViewById(R.id.iban_third_segment);
        ibanFourthSegmentText = (FacedTextView)findViewById(R.id.iban_fourth_segment);
        ibanFifthSegmentText = (FacedTextView)findViewById(R.id.iban_fifth_segment);
        ibanSixthSegmentText = (FacedTextView)findViewById(R.id.iban_sixth_segment);
        ibanSeventhSegmentText = (FacedTextView)findViewById(R.id.iban_seventh_segment);

        segmentRelativeLayouts[0] = (RelativeLayout)findViewById(R.id.iban_first_segment_l);
        segmentRelativeLayouts[1] = (RelativeLayout)findViewById(R.id.iban_second_segment_l);
        segmentRelativeLayouts[2] = (RelativeLayout)findViewById(R.id.iban_third_segment_l);
        segmentRelativeLayouts[3] = (RelativeLayout)findViewById(R.id.iban_fourth_segment_l);
        segmentRelativeLayouts[4] = (RelativeLayout)findViewById(R.id.iban_fifth_segment_l);
        segmentRelativeLayouts[5] = (RelativeLayout)findViewById(R.id.iban_sixth_segment_l);
        segmentRelativeLayouts[6] = (RelativeLayout)findViewById(R.id.iban_seventh_segment_l);


        if (prefs.getBoolean(Constants.SETTING_CHANGE_IBAN_STATUS, false)){
            introIbanLayout.setVisibility(View.GONE);
            editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
            editor.commit();
            pendingPOListRequest = new PendingPOListRequest();
            requestPendingPOList = new RequestPendingPOList(activity, new RequestPendingPOListTaskCompleteListener());
            requestPendingPOList.execute(pendingPOListRequest);
        }else {
//            new Expand(keyboard).animate();
        }

        ibanVerifyButton = (FacedTextView)findViewById(R.id.iban_verify_button);
        ibanVerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                introIbanLayout.setVisibility(View.GONE);
                pendingPOListRequest = new PendingPOListRequest();
                requestPendingPOList = new RequestPendingPOList(activity, new RequestPendingPOListTaskCompleteListener());
                requestPendingPOList.execute(pendingPOListRequest);
            }
        });

        pullToRefresh = (SwipeRefreshLayout)findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pendingPOListRequest = new PendingPOListRequest();
                new Collapse(keyboard).animate();
                requestPendingPOList = new RequestPendingPOList(activity, new RequestPendingPOListTaskCompleteListener());
                requestPendingPOList.execute(pendingPOListRequest);
            }
        });
        paymentRequestList = (ListView)findViewById(R.id.paymentRequestList);
        hampay_contacts = (ImageView)findViewById(R.id.hampay_contacts);
        search_text = (FacedEditText)findViewById(R.id.search_text);
        search_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<PaymentInfoDTO> searchPaymentInfo = new ArrayList<>();
                if (paymentInfoList != null) {
                    for (PaymentInfoDTO paymentInfo : paymentInfoList) {
                        if (paymentInfo.getCalleeName().toLowerCase().contains(search_text.getText().toString().toLowerCase())) {
                            searchPaymentInfo.add(paymentInfo);
                        }
                    }
                    pendingPOAdapter = new PendingPOAdapter(activity, searchPaymentInfo, authToken);
                    paymentRequestList.setAdapter(pendingPOAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        search_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    inputMethodManager.hideSoftInputFromWindow(search_text.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        paymentRequestList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(activity, PendingPODetailActivity.class);
                intent.putExtra(Constants.PAYMENT_INFO, paymentInfoList.get(position));
                startActivityForResult(intent, 1024);
            }
        });

        hampay_contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(activity, HamPayContactsActivity.class);
                startActivity(intent);
            }
        });
    }


    public class RequestPendingPOListTaskCompleteListener implements
            AsyncTaskCompleteListener<ResponseMessage<PendingPOListResponse>> {
        @Override
        public void onTaskComplete(ResponseMessage<PendingPOListResponse> pendingPOListResponseResponseMessage) {
            hamPayDialog.dismisWaitingDialog();
            paymentRequestList.setAdapter(null);
            pullToRefresh.setRefreshing(false);
            if (pendingPOListResponseResponseMessage != null){
                if (pendingPOListResponseResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS){
                    paymentInfoList = pendingPOListResponseResponseMessage.getService().getPendingList();
                    if (paymentInfoList.size() > 0) {
                        pendingPOAdapter = new PendingPOAdapter(activity, paymentInfoList, authToken);
                        paymentRequestList.setAdapter(pendingPOAdapter);
                        nullPendingText.setVisibility(View.GONE);
                        search_bar.setVisibility(View.VISIBLE);
                    }else {
                        nullPendingText.setVisibility(View.VISIBLE);
                        search_bar.setVisibility(View.INVISIBLE);
                    }
                }else if (pendingPOListResponseResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    forceLogout();
                }
                else {
                    nullPendingText.setVisibility(View.VISIBLE);
                }
            }else {
                nullPendingText.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
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
            if (ibanChangeResponseMessage != null) {
                if (ibanChangeResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    ibanVerifyButton.setVisibility(View.VISIBLE);
                    serviceName = ServiceEvent.IBAN_CHANGE_SUCCESS;
                    editor.putBoolean(Constants.SETTING_CHANGE_IBAN_STATUS, true);
                    editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                    editor.commit();
                    bankName = (FacedTextView)findViewById(R.id.bank_name);
                    bankLogo = (ImageView)findViewById(R.id.bank_logo);
                    bankLogo.setVisibility(View.VISIBLE);
                    bankName.setVisibility(View.VISIBLE);

//                    if (ibanChangeResponseMessage.getService().getImageId() != null) {
//                        bankLogo.setTag(ibanChangeResponseMessage.getService().getImageId());
//                        imageManager.displayImage(ibanChangeResponseMessage.getService().getImageId(),bankLogo, R.drawable.user_placeholder);
//                    }else {
//                        bankLogo.setImageResource(R.drawable.user_placeholder);
//                    }
//                    bankName.setText(ibanChangeResponseMessage.getService().getBankName());
                }else if (ibanChangeResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    serviceName = ServiceEvent.IBAN_CHANGE_FAILURE;
                    forceLogout();
                }
                else {
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


    public void pressKey(View view) {
        if (view.getTag().toString().equals("*")) {
            new Collapse(keyboard).animate();
        }else if (view.getTag().toString().equals("|")) {
            new Expand(keyboard).animate();
        }else {
            inputDigit(view.getTag().toString());
        }
    }

    private void inputDigit(String digit){
        String ibanSegment = "";
        if (digit.endsWith("d")){
        }else {
            if (ibanValue.length() > 23) return;
            ibanValue += digit;
        }
        switch (ibanValue.length()){
            case 0:
            case 1:
            case 2:
                ibanSegment = ibanFirstSegmentText.getText().toString();
                if (digit.endsWith("d")){
                    if (ibanSegment.length() == 0) return;
                    ibanFirstSegmentText.setText(ibanSegment.substring(0, ibanSegment.length() - 1));
                    ibanValue = ibanValue.substring(0, ibanValue.length() - 1);
                    setBorder(ibanValue.length());
                }else {
                    ibanFirstSegmentText.setText(persian.E2P(ibanSegment + digit));
                    setBorder(ibanValue.length() + 1);
                }
                break;

            case 3:
            case 4:
            case 5:
            case 6:
                ibanSegment = ibanSecondSegmentText.getText().toString();
                if (digit.endsWith("d")){
                    if (ibanSegment.length() == 0) return;
                    ibanSecondSegmentText.setText(ibanSegment.substring(0, ibanSegment.length() - 1));
                    ibanValue = ibanValue.substring(0, ibanValue.length() - 1);
                    setBorder(ibanValue.length());
                }else {
                    ibanSecondSegmentText.setText(persian.E2P(ibanSegment + digit));
                    setBorder(ibanValue.length() + 1);
                }
                break;

            case 7:
            case 8:
            case 9:
            case 10:
                ibanSegment = ibanThirdSegmentText.getText().toString();
                if (digit.endsWith("d")){
                    if (ibanSegment.length() == 0) return;
                    ibanThirdSegmentText.setText(ibanSegment.substring(0, ibanSegment.length() - 1));
                    ibanValue = ibanValue.substring(0, ibanValue.length() - 1);
                    setBorder(ibanValue.length());
                }else {
                    ibanThirdSegmentText.setText(persian.E2P(ibanSegment + digit));
                    setBorder(ibanValue.length() + 1);
                }
                break;

            case 11:
            case 12:
            case 13:
            case 14:
                ibanSegment = ibanFourthSegmentText.getText().toString();
                if (digit.endsWith("d")){
                    if (ibanSegment.length() == 0) return;
                    ibanFourthSegmentText.setText(ibanSegment.substring(0, ibanSegment.length() - 1));
                    ibanValue = ibanValue.substring(0, ibanValue.length() - 1);
                }else {
                    ibanFourthSegmentText.setText(persian.E2P(ibanSegment + digit));
                }
                setBorder(ibanValue.length());
                break;

            case 15:
            case 16:
            case 17:
            case 18:
                ibanSegment = ibanFifthSegmentText.getText().toString();
                if (digit.endsWith("d")){
                    if (ibanSegment.length() == 0) return;
                    ibanFifthSegmentText.setText(ibanSegment.substring(0, ibanSegment.length() - 1));
                    ibanValue = ibanValue.substring(0, ibanValue.length() - 1);
                    setBorder(ibanValue.length());
                }else {
                    ibanFifthSegmentText.setText(persian.E2P(ibanSegment + digit));
                    setBorder(ibanValue.length() + 1);
                }
                break;

            case 19:
            case 20:
            case 21:
            case 22:
                ibanSegment = ibanSixthSegmentText.getText().toString();
                if (digit.endsWith("d")){
                    if (ibanSegment.length() == 0) return;
                    ibanSixthSegmentText.setText(ibanSegment.substring(0, ibanSegment.length() - 1));
                    ibanValue = ibanValue.substring(0, ibanValue.length() - 1);
                    setBorder(ibanValue.length());
                }else {
                    ibanSixthSegmentText.setText(persian.E2P(ibanSegment + digit));
                    setBorder(ibanValue.length() + 1);
                }
                break;

            case 23:
            case 24:
                ibanSegment = ibanSeventhSegmentText.getText().toString();
                if (digit.endsWith("d")){
                    if (ibanSegment.length() == 0) return;
                    ibanSeventhSegmentText.setText(ibanSegment.substring(0, ibanSegment.length() - 1));
                    ibanValue = ibanValue.substring(0, ibanValue.length() - 1);
                    setBorder(ibanValue.length());
                }else {
                    ibanSeventhSegmentText.setText(persian.E2P(ibanSegment + digit));
                    setBorder(ibanValue.length() + 1);
                }
                break;
        }
        if (ibanValue.length() == 24){
            ibanChangeRequest = new IBANChangeRequest();
            ibanChangeRequest.setIban(new PersianEnglishDigit().P2E(ibanValue));
            requestIBANChange = new RequestIBANChange(activity, new RequestIBANChangeTaskCompleteListener(ibanChangeRequest));
            requestIBANChange.execute(ibanChangeRequest);
        }
    }

    private void setBorder(int length){
        switch (length){
            case 0:
            case 1:
            case 2:
                for (RelativeLayout relativeLayout : segmentRelativeLayouts){
                    relativeLayout.setBackgroundResource(R.drawable.iban_empty_placeholder);
                }
                segmentRelativeLayouts[0].setBackgroundResource(R.drawable.iban_entry_placeholder);
                break;

            case 3:
            case 4:
            case 5:
            case 6:
                for (RelativeLayout relativeLayout : segmentRelativeLayouts){
                    relativeLayout.setBackgroundResource(R.drawable.iban_empty_placeholder);
                }
                segmentRelativeLayouts[1].setBackgroundResource(R.drawable.iban_entry_placeholder);
                break;

            case 7:
            case 8:
            case 9:
            case 10:
                for (RelativeLayout relativeLayout : segmentRelativeLayouts){
                    relativeLayout.setBackgroundResource(R.drawable.iban_empty_placeholder);
                }
                segmentRelativeLayouts[2].setBackgroundResource(R.drawable.iban_entry_placeholder);
                break;

            case 11:
            case 12:
            case 13:
            case 14:
                for (RelativeLayout relativeLayout : segmentRelativeLayouts){
                    relativeLayout.setBackgroundResource(R.drawable.iban_empty_placeholder);
                }
                segmentRelativeLayouts[3].setBackgroundResource(R.drawable.iban_entry_placeholder);
                break;

            case 15:
            case 16:
            case 17:
            case 18:
                for (RelativeLayout relativeLayout : segmentRelativeLayouts){
                    relativeLayout.setBackgroundResource(R.drawable.iban_empty_placeholder);
                }
                segmentRelativeLayouts[4].setBackgroundResource(R.drawable.iban_entry_placeholder);
                break;

            case 19:
            case 20:
            case 21:
            case 22:
                for (RelativeLayout relativeLayout : segmentRelativeLayouts){
                    relativeLayout.setBackgroundResource(R.drawable.iban_empty_placeholder);
                }
                segmentRelativeLayouts[5].setBackgroundResource(R.drawable.iban_entry_placeholder);
                break;

            case 23:
            case 24:
                for (RelativeLayout relativeLayout : segmentRelativeLayouts){
                    relativeLayout.setBackgroundResource(R.drawable.iban_empty_placeholder);
                }
                segmentRelativeLayouts[6].setBackgroundResource(R.drawable.iban_entry_placeholder);
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
}



