package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.nineoldandroids.animation.ObjectAnimator;

import java.io.Serializable;

import br.com.goncalves.pugnotification.notification.PugNotification;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.PSPResultRequest;
import xyz.homapay.hampay.common.core.model.request.SignToPayRequest;
import xyz.homapay.hampay.common.core.model.request.UtilityBillDetailRequest;
import xyz.homapay.hampay.common.core.model.response.PSPResultResponse;
import xyz.homapay.hampay.common.core.model.response.SignToPayResponse;
import xyz.homapay.hampay.common.core.model.response.TopUpDetailResponse;
import xyz.homapay.hampay.common.core.model.response.dto.TopUpInfoDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.Helper.DatabaseHelper;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.animation.Collapse;
import xyz.homapay.hampay.mobile.android.animation.Expand;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestPSPResult;
import xyz.homapay.hampay.mobile.android.async.task.RequestTokenBills;
import xyz.homapay.hampay.mobile.android.async.task.SignToPayTask;
import xyz.homapay.hampay.mobile.android.async.task.UtilityBillDetailTask;
import xyz.homapay.hampay.mobile.android.async.task.impl.OnTaskCompleted;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.dialog.card.CardAction;
import xyz.homapay.hampay.mobile.android.dialog.card.CardNumberDialog;
import xyz.homapay.hampay.mobile.android.firebase.LogEvent;
import xyz.homapay.hampay.mobile.android.firebase.service.ServiceEvent;
import xyz.homapay.hampay.mobile.android.img.ImageHelper;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.model.BillsTokenDoWork;
import xyz.homapay.hampay.mobile.android.model.PaymentType;
import xyz.homapay.hampay.mobile.android.model.SucceedPayment;
import xyz.homapay.hampay.mobile.android.model.SyncPspResult;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.CurrencyFormatter;
import xyz.homapay.hampay.mobile.android.util.JalaliConvert;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;
import xyz.homapay.hampay.mobile.android.util.PspCode;
import xyz.homapay.hampay.mobile.android.webservice.psp.bills.MKAArrayOfKeyValueOfstringstring;
import xyz.homapay.hampay.mobile.android.webservice.psp.bills.MKAArrayOfKeyValueOfstringstring_KeyValueOfstringstring;

public class ServiceTopUpDetailActivity extends AppCompatActivity implements View.OnClickListener, CardNumberDialog.SelectCardDialogListener, OnTaskCompleted {

    private DatabaseHelper dbHelper;
    private ImageView pay_button;
    private ImageView imgOperator;
    private FacedTextView tvTopUpName;
    private FacedTextView topUpDate;
    private FacedTextView tvCellNumber;
    private FacedTextView tvAmount;
    private FacedTextView tvTopUpTax;
    private FacedTextView hampayFee;
    private FacedTextView tvTopUpTotalAmount;
    private PSPResultRequest pspResultRequest;
    private RequestPSPResult requestPSPResult;
    private RequestTokenBills requestTokenBills;
    private FacedTextView bankName;
    private FacedTextView cardNumberValue;
    private CurrencyFormatter currencyFormatter;
    private Context context;
    private Activity activity;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private LinearLayout keyboard;
    private RelativeLayout pinLayout;
    private FacedTextView pinText;
    private RelativeLayout cvvLayout;
    private FacedTextView cvvText;
    private boolean pinCodeFocus = false;
    private boolean cvvFocus = false;
    private String userPinCode = "";
    private String userCVV2 = "";
    private ScrollView paymentScroll;
    private PersianEnglishDigit persian = new PersianEnglishDigit();
    private HamPayDialog hamPayDialog;
    private TopUpInfoDTO topUpInfo = null;
    private BillsTokenDoWork billsTokenDoWork;
    private RelativeLayout cardPlaceHolder;
    private int selectedCardIdIndex = -1;
    private FacedTextView selectCardText;
    private LinearLayout cardSelect;
    private String signature;
    private String providerId = null;
    private String authToken = "";

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
        PugNotification.with(context).cancel(Constants.INVOICE_NOTIFICATION_IDENTIFIER);
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
        PugNotification.with(context).cancel(Constants.INVOICE_NOTIFICATION_IDENTIFIER);
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
        setContentView(R.layout.activity_service_top_up_detail);

        context = this;
        activity = ServiceTopUpDetailActivity.this;
        dbHelper = new DatabaseHelper(context);


        PugNotification.with(context).cancel(Constants.INVOICE_NOTIFICATION_IDENTIFIER);

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
        authToken = prefs.getString(Constants.LOGIN_TOKEN_ID, "");

        String LOGIN_TOKEN = prefs.getString(Constants.LOGIN_TOKEN_ID, null);
        if (LOGIN_TOKEN == null) {
            Intent intent = new Intent();
            intent.setClass(context, HamPayLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
            return;
        }

        keyboard = (LinearLayout) findViewById(R.id.keyboard);
        pinLayout = (RelativeLayout) findViewById(R.id.pin_layout);
        pinText = (FacedTextView) findViewById(R.id.pin_text);
        keyboard = (LinearLayout) findViewById(R.id.keyboard);
        pinLayout = (RelativeLayout) findViewById(R.id.pin_layout);
        pinText = (FacedTextView) findViewById(R.id.pin_text);
        pinText.setOnClickListener(this);
        cvvLayout = (RelativeLayout) findViewById(R.id.cvv_layout);
        cvvText = (FacedTextView) findViewById(R.id.cvv_text);
        cvvText.setOnClickListener(this);
        paymentScroll = (ScrollView) findViewById(R.id.paymentScroll);
        hamPayDialog = new HamPayDialog(activity);

        currencyFormatter = new CurrencyFormatter();
        imgOperator = (ImageView) findViewById(R.id.imgOperator);
        tvTopUpName = (FacedTextView) findViewById(R.id.tvTopUpName);
        topUpDate = (FacedTextView) findViewById(R.id.topUpDate);
        tvCellNumber = (FacedTextView) findViewById(R.id.tvCellNumber);
        tvAmount = (FacedTextView) findViewById(R.id.tvAmount);
        tvTopUpTax = (FacedTextView) findViewById(R.id.tvTopUpTax);
        hampayFee = (FacedTextView) findViewById(R.id.hampayFee);
        tvTopUpTotalAmount = (FacedTextView) findViewById(R.id.tvTopUpTotalAmount);


        bankName = (FacedTextView) findViewById(R.id.bankName);
        cardNumberValue = (FacedTextView) findViewById(R.id.cardNumberValue);
        selectCardText = (FacedTextView) findViewById(R.id.selectCardText);
        cardSelect = (LinearLayout) findViewById(R.id.cardSelect);

        Intent intent = getIntent();
        topUpInfo = (TopUpInfoDTO) intent.getSerializableExtra(Constants.TOP_UP_INFO);
        providerId = intent.getStringExtra(Constants.PROVIDER_ID);

        if (topUpInfo != null) {
            fillUI(topUpInfo);
        } else if (providerId != null) {
            UtilityBillDetailRequest utilityBillDetailRequest = new UtilityBillDetailRequest();
            utilityBillDetailRequest.setProviderId(providerId);
            new UtilityBillDetailTask(activity, ServiceTopUpDetailActivity.this, utilityBillDetailRequest, authToken).execute();
        }

        cardPlaceHolder = (RelativeLayout) findViewById(R.id.cardPlaceHolder);
        cardPlaceHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CardNumberDialog cardNumberDialog = new CardNumberDialog();
                if (topUpInfo == null) return;
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.CARD_LIST, (Serializable) topUpInfo.getCardList());
                cardNumberDialog.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.add(cardNumberDialog, null);
                fragmentTransaction.commitAllowingStateLoss();
            }
        });


        pay_button = (ImageView) findViewById(R.id.pay_button);
        pay_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (topUpInfo == null) return;

                if (selectedCardIdIndex == -1 || (topUpInfo.getCardList().get(selectedCardIdIndex) != null && topUpInfo.getCardList().get(selectedCardIdIndex).getCardId() == null) || (topUpInfo.getAmount() + topUpInfo.getFeeCharge() >= Constants.SOAP_AMOUNT_MAX)) {
                    Intent intent = new Intent();
                    intent.setClass(activity, BankWebPaymentActivity.class);
                    intent.putExtra(Constants.BILL_INFO, topUpInfo);
                    startActivityForResult(intent, 47);
                } else {
                    if (pinText.getText().toString().length() <= 4) {
                        Toast.makeText(context, getString(R.string.msg_pin2_incorrect), Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (!(cvvText.getText().toString().length() >= 3 && cvvText.getText().toString().length() <= 4)) {
                        Toast.makeText(context, getString(R.string.msg_cvv2_incorrect), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                    editor.commit();
                    requestTokenBills = new RequestTokenBills(activity, new RequestPurchaseTaskCompleteListener(), topUpInfo.getPspInfo().getPayURL());

                    billsTokenDoWork = new BillsTokenDoWork();
                    billsTokenDoWork.setUserName("appstore");
                    billsTokenDoWork.setPassword("sepapp");
                    billsTokenDoWork.setCellNumber(topUpInfo.getPspInfo().getCellNumber().substring(1, topUpInfo.getPspInfo().getCellNumber().length()));
                    billsTokenDoWork.setLangAByte((byte) 0);
                    billsTokenDoWork.setLangABoolean(false);
                    MKAArrayOfKeyValueOfstringstring vectorstring2stringMapEntry = new MKAArrayOfKeyValueOfstringstring();
                    MKAArrayOfKeyValueOfstringstring_KeyValueOfstringstring s2sMapEntry = new MKAArrayOfKeyValueOfstringstring_KeyValueOfstringstring();

                    s2sMapEntry.Key = "Amount";
                    s2sMapEntry.Value = String.valueOf(topUpInfo.getAmount() + topUpInfo.getFeeCharge());
                    vectorstring2stringMapEntry.add(s2sMapEntry);

                    s2sMapEntry = new MKAArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                    s2sMapEntry.Key = "Pin2";
                    s2sMapEntry.Value = userPinCode;
                    vectorstring2stringMapEntry.add(s2sMapEntry);

                    s2sMapEntry = new MKAArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                    s2sMapEntry.Key = "ThirdParty";
                    s2sMapEntry.Value = topUpInfo.getProductCode();
                    vectorstring2stringMapEntry.add(s2sMapEntry);

                    s2sMapEntry = new MKAArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                    s2sMapEntry.Key = "TerminalId";
                    s2sMapEntry.Value = topUpInfo.getPspInfo().getTerminalId();
                    vectorstring2stringMapEntry.add(s2sMapEntry);

                    s2sMapEntry = new MKAArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                    s2sMapEntry.Key = "SenderTerminalId";
                    s2sMapEntry.Value = topUpInfo.getPspInfo().getSenderTerminalId();
                    vectorstring2stringMapEntry.add(s2sMapEntry);

                    s2sMapEntry = new MKAArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                    s2sMapEntry.Key = "Email";
                    s2sMapEntry.Value = "";
                    vectorstring2stringMapEntry.add(s2sMapEntry);

                    s2sMapEntry = new MKAArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                    s2sMapEntry.Key = "IPAddress";
                    s2sMapEntry.Value = topUpInfo.getPspInfo().getIpAddress();
                    vectorstring2stringMapEntry.add(s2sMapEntry);

                    s2sMapEntry = new MKAArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                    s2sMapEntry.Key = "CVV2";
                    s2sMapEntry.Value = userCVV2;
                    vectorstring2stringMapEntry.add(s2sMapEntry);

                    s2sMapEntry = new MKAArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                    s2sMapEntry.Key = "ExpDate";
                    s2sMapEntry.Value = topUpInfo.getCardList().get(selectedCardIdIndex).getExpireDate();
                    vectorstring2stringMapEntry.add(s2sMapEntry);

                    s2sMapEntry = new MKAArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                    s2sMapEntry.Key = "CardId";
                    s2sMapEntry.Value = topUpInfo.getCardList().get(selectedCardIdIndex).getCardId();
                    vectorstring2stringMapEntry.add(s2sMapEntry);

                    s2sMapEntry = new MKAArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                    s2sMapEntry.Key = "ResNum";
                    s2sMapEntry.Value = topUpInfo.getProductCode();
                    vectorstring2stringMapEntry.add(s2sMapEntry);

                    s2sMapEntry = new MKAArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                    s2sMapEntry.Key = "BillId";
//                    s2sMapEntry.Value = topUpInfo.getBillId();
                    vectorstring2stringMapEntry.add(s2sMapEntry);

                    s2sMapEntry = new MKAArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                    s2sMapEntry.Key = "PayId";
//                    s2sMapEntry.Value = topUpInfo.getPayId();
                    vectorstring2stringMapEntry.add(s2sMapEntry);

                    s2sMapEntry = new MKAArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                    s2sMapEntry.Key = "Signature";
                    s2sMapEntry.Value = signature;
                    vectorstring2stringMapEntry.add(s2sMapEntry);


                    billsTokenDoWork.setVectorstring2stringMapEntry(vectorstring2stringMapEntry);
                    requestTokenBills.execute(billsTokenDoWork);

                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (keyboard.getVisibility() == View.VISIBLE) {
            new Collapse(keyboard).animate();
            ObjectAnimator.ofInt(paymentScroll, "scrollY", paymentScroll.getTop()).setDuration(400).start();
            return;
        }
        finish();
    }

    @Override
    public void onClick(View view) {
        ObjectAnimator.ofInt(paymentScroll, "scrollY", paymentScroll.getBottom()).setDuration(400).start();
        if (keyboard.getVisibility() == View.GONE) {
            new Expand(keyboard).animate();
        }
        switch (view.getId()) {
            case R.id.pin_text:
                pinLayout.setBackgroundResource(R.drawable.card_info_entry_placeholder);
                cvvLayout.setBackgroundResource(R.drawable.card_info_empty_placeholder);
                pinCodeFocus = true;
                cvvFocus = false;
                break;

            case R.id.cvv_text:
                pinLayout.setBackgroundResource(R.drawable.card_info_empty_placeholder);
                cvvLayout.setBackgroundResource(R.drawable.card_info_entry_placeholder);
                pinCodeFocus = false;
                cvvFocus = true;
                break;
        }
    }

    @Override
    public void onFinishEditDialog(CardAction cardAction, int position) {
        switch (cardAction) {
            case SELECT:
                userCVV2 = "";
                userPinCode = "";
                pinText.setText("");
                cvvText.setText("");
                if (topUpInfo != null) {
                    selectedCardIdIndex = position;
                    cardNumberValue.setText(persian.E2P(topUpInfo.getCardList().get(position).getLast4Digits()));
                    bankName.setText(topUpInfo.getCardList().get(position).getBankName());
                    selectCardText.setVisibility(View.GONE);
                    cardSelect.setVisibility(View.VISIBLE);
                    if (topUpInfo.getCardList().get(position).getDigitalSignature() != null && topUpInfo.getCardList().get(position).getDigitalSignature().length() > 0) {
                        signature = topUpInfo.getCardList().get(position).getDigitalSignature();
                    } else {
                        SignToPayRequest signToPayRequest = new SignToPayRequest();
                        signToPayRequest.setCardId(topUpInfo.getCardList().get(position).getCardId());
                        signToPayRequest.setProductCode(topUpInfo.getProductCode());
                        new SignToPayTask(activity, ServiceTopUpDetailActivity.this, signToPayRequest, authToken).execute();
                    }
                }
                break;

            case ADD:
                Intent intent = new Intent();
                intent.setClass(activity, BankWebPaymentActivity.class);
                intent.putExtra(Constants.BILL_INFO, topUpInfo);
                startActivityForResult(intent, 47);
                break;
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
                final ResponseMessage responseMessage = (ResponseMessage) object;
                switch (responseMessage.getService().getServiceDefinition()) {
                    case SIGN_DATA_TO_PAY:
                        ResponseMessage<SignToPayResponse> signToPayResponse = (ResponseMessage) object;
                        switch (responseMessage.getService().getResultStatus()) {
                            case SUCCESS:
                                signature = signToPayResponse.getService().getSignedData();
                                break;
                            default:
                                break;
                        }
                        break;

                    case UTILITY_BILL_DETAIL:
                        ResponseMessage<TopUpDetailResponse> utilityBillDetail = (ResponseMessage) object;
                        switch (responseMessage.getService().getResultStatus()) {
                            case SUCCESS:
                                topUpInfo = utilityBillDetail.getService().getTopUpInfoDTO();
                                fillUI(topUpInfo);
                                break;
                            default:
                                break;
                        }
                        break;
                }
            }
        }
    }

    private void fillUI(TopUpInfoDTO topUInfo) {
        tvTopUpName.setText(topUInfo.getTopUpName());
        topUpDate.setText(persian.E2P(new JalaliConvert().GregorianToPersian(topUInfo.getExpirationDate())));
        if (topUInfo.getImageId() != null) {
            imgOperator.setTag(topUInfo.getImageId());
            ImageHelper.getInstance(context).imageLoader(topUInfo.getImageId(), imgOperator, R.drawable.user_placeholder);
        } else {
            imgOperator.setImageResource(R.drawable.user_placeholder);
        }

        tvCellNumber.setText(persian.E2P(topUInfo.getCellNumber()));
        tvAmount.setText(persian.E2P(persian.E2P(currencyFormatter.format(topUInfo.getAmount()))));
        tvTopUpTax.setText(persian.E2P(currencyFormatter.format(topUInfo.getVat())));
        hampayFee.setText(persian.E2P(currencyFormatter.format(topUInfo.getFeeCharge())));

        tvTopUpTotalAmount.setText(persian.E2P(currencyFormatter.format(topUInfo.getAmount() + topUInfo.getFeeCharge())));

//        if (topUInfo.getCardList().size() > 0) {
//            if (topUInfo.getCardList().get(0).getCardId() != null && (topUInfo.getAmount() + topUInfo.getFeeCharge() < Constants.SOAP_AMOUNT_MAX)) {
//                cardNumberValue.setText(persian.E2P(topUInfo.getCardList().get(0).getLast4Digits()));
//                bankName.setText(topUInfo.getCardList().get(0).getBankName());
//            }
//        }
//
//
//        if (topUInfo.getCardList().size() > 0) {
//            if (topUInfo.getCardList().get(0).getCardId() != null && (topUInfo.getAmount() + topUInfo.getFeeCharge() < Constants.SOAP_AMOUNT_MAX)) {
//                cardNumberValue.setText(persian.E2P(topUInfo.getCardList().get(0).getLast4Digits()));
//                bankName.setText(topUInfo.getCardList().get(0).getBankName());
//                selectedCardIdIndex = 0;
//                selectCardText.setVisibility(View.GONE);
//                cardSelect.setVisibility(View.VISIBLE);
//                if (topUInfo.getCardList().get(0).getDigitalSignature() != null && topUInfo.getCardList().get(0).getDigitalSignature().length() > 0) {
//                    signature = topUInfo.getCardList().get(0).getDigitalSignature();
//                }
//            }
//        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 47) {
            if (resultCode == Activity.RESULT_OK) {
                int result = data.getIntExtra(Constants.ACTIVITY_RESULT, -1);
                if (result == 0) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(Constants.ACTIVITY_RESULT, ResultStatus.SUCCESS.ordinal());
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }

            }
            if (resultCode == Activity.RESULT_CANCELED) {
            }
        }
    }

    public void pressKey(View view) {
        if (view.getTag().toString().equals("*")) {
            new Collapse(keyboard).animate();
            ObjectAnimator.ofInt(paymentScroll, "scrollY", paymentScroll.getTop()).setDuration(400).start();
        } else if (view.getTag().toString().equals("|")) {
            new Expand(keyboard).animate();
        } else {
            inputDigit(view.getTag().toString());
        }
    }

    private void inputDigit(String digit) {
        if (digit.endsWith("d")) {
        }
        if (pinCodeFocus) {
            if (userPinCode.length() == 0) {
                pinText.setText("");
            }
            String pinCode = pinText.getText().toString();
            if (digit.endsWith("d")) {
                if (pinCode.length() == 0) return;
                pinText.setText(pinCode.substring(0, pinCode.length() - 1));
                userPinCode = userPinCode.substring(0, userPinCode.length() - 1);
            } else {
                pinText.setText(persian.E2P(pinCode + "●"));
                userPinCode += digit;
            }
        } else if (cvvFocus) {
            String cvvCode = cvvText.getText().toString();
            if (digit.endsWith("d")) {
                if (cvvCode.length() == 0) return;
                cvvText.setText(cvvCode.substring(0, cvvCode.length() - 1));
            } else {
                cvvText.setText(persian.E2P(cvvCode + "●"));
                userCVV2 += digit;
            }
        }
    }

    public class RequestPSPResultTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<PSPResultResponse>> {

        ServiceEvent serviceName;
        LogEvent logEvent = new LogEvent(context);
        private String SWTrace;

        public RequestPSPResultTaskCompleteListener(String SWTrace) {
            this.SWTrace = SWTrace;
        }

        @Override
        public void onTaskComplete(ResponseMessage<PSPResultResponse> pspResultResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            if (pspResultResponseMessage != null) {
                if (pspResultResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    serviceName = ServiceEvent.PSP_RESULT_SUCCESS;
                    if (SWTrace != null) {
                        dbHelper.syncPspResult(SWTrace);
                    }
                } else if (pspResultResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    serviceName = ServiceEvent.PSP_RESULT_FAILURE;
                    forceLogout();
                } else {
                    serviceName = ServiceEvent.PSP_RESULT_FAILURE;
                }
                logEvent.log(serviceName);
            }
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }

    public class RequestPurchaseTaskCompleteListener implements AsyncTaskCompleteListener<MKAArrayOfKeyValueOfstringstring> {

        @Override
        public void onTaskComplete(MKAArrayOfKeyValueOfstringstring purchaseResponseResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            String responseCode = null;
            String description = null;
            String SWTraceNum = null;
            ResultStatus resultStatus = ResultStatus.FAILURE;
            ServiceEvent serviceName = ServiceEvent.PSP_PAYMENT_FAILURE;
            LogEvent logEvent = new LogEvent(context);

            if (purchaseResponseResponseMessage != null) {
                pspResultRequest = new PSPResultRequest();
                for (MKAArrayOfKeyValueOfstringstring_KeyValueOfstringstring s2sMapEntry : purchaseResponseResponseMessage) {
                    if (s2sMapEntry.Key.equalsIgnoreCase("ResponseCode")) {
                        responseCode = s2sMapEntry.Value;
                    } else if (s2sMapEntry.Key.equalsIgnoreCase("Description")) {
                        description = s2sMapEntry.Value;
                    } else if (s2sMapEntry.Key.equalsIgnoreCase("SWTraceNum")) {
                        SWTraceNum = s2sMapEntry.Value;
                    }
                }

                if (responseCode != null) {
                    if (responseCode.equalsIgnoreCase("1")) {
                        serviceName = ServiceEvent.PSP_PAYMENT_SUCCESS;
                        if (topUpInfo != null) {
                            Intent intent = new Intent(context, PaymentCompletedActivity.class);
                            SucceedPayment succeedPayment = new SucceedPayment();
                            succeedPayment.setAmount(topUpInfo.getAmount() + topUpInfo.getFeeCharge());
                            //TODO
                            succeedPayment.setCode(topUpInfo.getCellNumber());
                            succeedPayment.setTrace(topUpInfo.getPspInfo().getProviderId());
                            succeedPayment.setPaymentType(PaymentType.BILLS);
                            intent.putExtra(Constants.SUCCEED_PAYMENT_INFO, succeedPayment);
                            startActivityForResult(intent, 46);
                        }
                        resultStatus = ResultStatus.SUCCESS;
                    } else {
                        serviceName = ServiceEvent.PSP_PAYMENT_FAILURE;
                        PspCode pspCode = new PspCode(context);
                        new HamPayDialog(activity).pspFailResultDialog(responseCode, pspCode.getDescription(SWTraceNum));
                        resultStatus = ResultStatus.FAILURE;
                    }
                    logEvent.log(serviceName);

                    SyncPspResult syncPspResult = new SyncPspResult();
                    syncPspResult.setResponseCode(responseCode);
                    syncPspResult.setProductCode(topUpInfo.getProductCode());
                    syncPspResult.setType("UTILITY_BILL");
                    syncPspResult.setSwTrace(SWTraceNum);
                    syncPspResult.setTimestamp(System.currentTimeMillis());
                    syncPspResult.setStatus(0);
                    dbHelper.createSyncPspResult(syncPspResult);

                    pspResultRequest.setPspResponseCode(responseCode);
                    pspResultRequest.setProductCode(topUpInfo.getProductCode());
                    pspResultRequest.setTrackingCode(SWTraceNum);
                    requestPSPResult = new RequestPSPResult(context, new RequestPSPResultTaskCompleteListener(SWTraceNum), 2);
                    requestPSPResult.execute(pspResultRequest);

                } else {
                    new HamPayDialog(activity).pspFailResultDialog(Constants.LOCAL_ERROR_CODE, getString(R.string.msg_soap_timeout));
                }
                editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                editor.commit();

                Intent returnIntent = new Intent();
                returnIntent.putExtra(Constants.ACTIVITY_RESULT, resultStatus.ordinal());
                setResult(Activity.RESULT_OK, returnIntent);
            } else {
                new HamPayDialog(activity).pspFailResultDialog(Constants.LOCAL_ERROR_CODE, getString(R.string.msg_soap_timeout));
            }

        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }

}
