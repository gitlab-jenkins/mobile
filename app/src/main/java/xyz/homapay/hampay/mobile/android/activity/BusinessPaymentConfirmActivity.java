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

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.PSPResultRequest;
import xyz.homapay.hampay.common.core.model.request.SignToPayRequest;
import xyz.homapay.hampay.common.core.model.response.PSPResultResponse;
import xyz.homapay.hampay.common.core.model.response.SignToPayResponse;
import xyz.homapay.hampay.common.core.model.response.dto.PaymentInfoDTO;
import xyz.homapay.hampay.common.core.model.response.dto.PspInfoDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.Helper.DatabaseHelper;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.animation.Collapse;
import xyz.homapay.hampay.mobile.android.animation.Expand;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestPSPResult;
import xyz.homapay.hampay.mobile.android.async.RequestPurchase;
import xyz.homapay.hampay.mobile.android.async.task.SignToPayTask;
import xyz.homapay.hampay.mobile.android.async.task.impl.OnTaskCompleted;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.dialog.card.CardAction;
import xyz.homapay.hampay.mobile.android.dialog.card.CardNumberDialog;
import xyz.homapay.hampay.mobile.android.firebase.LogEvent;
import xyz.homapay.hampay.mobile.android.firebase.service.ServiceEvent;
import xyz.homapay.hampay.mobile.android.img.ImageHelper;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.model.DoWorkInfo;
import xyz.homapay.hampay.mobile.android.model.PaymentType;
import xyz.homapay.hampay.mobile.android.model.SucceedPayment;
import xyz.homapay.hampay.mobile.android.model.SyncPspResult;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.CurrencyFormatter;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;
import xyz.homapay.hampay.mobile.android.util.PspCode;
import xyz.homapay.hampay.mobile.android.webservice.psp.CBUArrayOfKeyValueOfstringstring;
import xyz.homapay.hampay.mobile.android.webservice.psp.CBUArrayOfKeyValueOfstringstring_KeyValueOfstringstring;

public class BusinessPaymentConfirmActivity extends AppCompatActivity implements View.OnClickListener, CardNumberDialog.SelectCardDialogListener, OnTaskCompleted {

    private DatabaseHelper dbHelper;
    private ImageView pay_to_business_button;
    private Context context;
    private Activity activity;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private CurrencyFormatter formatter;
    private HamPayDialog hamPayDialog;
    private FacedTextView business_name;
    private ImageView business_image;
    private FacedTextView paymentPriceValue;
    private FacedTextView paymentFeeValue;
    private FacedTextView paymentVAT;
    private FacedTextView paymentTotalValue;
    private FacedTextView cardNumberValue;
    private FacedTextView bankName;
    private PaymentInfoDTO paymentInfoDTO = null;
    private PspInfoDTO pspInfoDTO = null;
    private RequestPurchase requestPurchase;
    private DoWorkInfo doWorkInfo;
    private RequestPSPResult requestPSPResult;
    private PSPResultRequest pspResultRequest;
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
    private RelativeLayout cardPlaceHolder;
    private int selectedCardIdIndex = -1;
    private FacedTextView selectCardText;
    private LinearLayout cardSelect;
    private PersianEnglishDigit persian = new PersianEnglishDigit();
    private String signature;
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
        setContentView(R.layout.activity_business_payment_confirm);

        context = this;
        activity = BusinessPaymentConfirmActivity.this;

        dbHelper = new DatabaseHelper(context);

        formatter = new CurrencyFormatter();

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();

        authToken = prefs.getString(Constants.LOGIN_TOKEN_ID, "");

        hamPayDialog = new HamPayDialog(activity);

        business_name = (FacedTextView) findViewById(R.id.business_name);
        business_image = (ImageView) findViewById(R.id.business_image);
        paymentPriceValue = (FacedTextView) findViewById(R.id.paymentPriceValue);
        paymentFeeValue = (FacedTextView) findViewById(R.id.paymentFeeValue);
        paymentVAT = (FacedTextView) findViewById(R.id.paymentVAT);
        paymentTotalValue = (FacedTextView) findViewById(R.id.paymentTotalValue);
        cardNumberValue = (FacedTextView) findViewById(R.id.cardNumberValue);
        bankName = (FacedTextView) findViewById(R.id.bankName);
        keyboard = (LinearLayout) findViewById(R.id.keyboard);
        pinLayout = (RelativeLayout) findViewById(R.id.pin_layout);
        pinText = (FacedTextView) findViewById(R.id.pin_text);
        pinText.setOnClickListener(this);
        cvvLayout = (RelativeLayout) findViewById(R.id.cvv_layout);
        cvvText = (FacedTextView) findViewById(R.id.cvv_text);
        cvvText.setOnClickListener(this);
        paymentScroll = (ScrollView) findViewById(R.id.paymentScroll);
        bankName = (FacedTextView) findViewById(R.id.bankName);
        cardNumberValue = (FacedTextView) findViewById(R.id.cardNumberValue);
        selectCardText = (FacedTextView) findViewById(R.id.selectCardText);
        cardSelect = (LinearLayout) findViewById(R.id.cardSelect);

        cardPlaceHolder = (RelativeLayout) findViewById(R.id.cardPlaceHolder);
        cardPlaceHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CardNumberDialog cardNumberDialog = new CardNumberDialog();
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.CARD_LIST, (Serializable) paymentInfoDTO.getCardList());
                cardNumberDialog.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.add(cardNumberDialog, null);
                fragmentTransaction.commitAllowingStateLoss();
            }
        });

        Intent intent = getIntent();

        paymentInfoDTO = (PaymentInfoDTO) intent.getSerializableExtra(Constants.PAYMENT_INFO);
        pspInfoDTO = (PspInfoDTO) intent.getSerializableExtra(Constants.PSP_INFO);


        if (paymentInfoDTO != null) {
            PersianEnglishDigit persianEnglishDigit = new PersianEnglishDigit();

            business_name.setText(paymentInfoDTO.getCallerName());
            paymentPriceValue.setText(persianEnglishDigit.E2P(String.valueOf(formatter.format(paymentInfoDTO.getAmount()))));
            paymentVAT.setText(persianEnglishDigit.E2P(String.valueOf(formatter.format(paymentInfoDTO.getVat()))));
            paymentFeeValue.setText(persianEnglishDigit.E2P(String.valueOf(formatter.format(paymentInfoDTO.getFeeCharge()))));
            paymentTotalValue.setText(persianEnglishDigit.E2P(String.valueOf(formatter.format(paymentInfoDTO.getAmount() + paymentInfoDTO.getVat() + paymentInfoDTO.getFeeCharge()))));

            if (paymentInfoDTO.getImageId() != null) {
                editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                editor.commit();
                business_image.setTag(paymentInfoDTO.getImageId());
                ImageHelper.getInstance(activity).imageLoader(paymentInfoDTO.getImageId(), business_image, R.drawable.user_placeholder);
            } else {
                business_image.setImageResource(R.drawable.user_placeholder);
            }
        }

        if (paymentInfoDTO.getCardList().size() > 0) {
            if (paymentInfoDTO.getCardList().get(0).getCardId() != null && (paymentInfoDTO.getAmount() + paymentInfoDTO.getFeeCharge() + paymentInfoDTO.getVat() < Constants.SOAP_AMOUNT_MAX)) {
                cardNumberValue.setText(persian.E2P(paymentInfoDTO.getCardList().get(0).getLast4Digits()));
                bankName.setText(paymentInfoDTO.getCardList().get(0).getBankName());
                selectedCardIdIndex = 0;
                selectCardText.setVisibility(View.GONE);
                cardSelect.setVisibility(View.VISIBLE);
                if (paymentInfoDTO.getCardList().get(0).getDigitalSignature() != null && paymentInfoDTO.getCardList().get(0).getDigitalSignature().length() > 0) {
                    signature = paymentInfoDTO.getCardList().get(0).getDigitalSignature();
                }
            }
        }

        pay_to_business_button = (ImageView) findViewById(R.id.pay_to_business_button);
        pay_to_business_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (pspInfoDTO == null) return;

                if (selectedCardIdIndex == -1 || (paymentInfoDTO.getCardList().get(selectedCardIdIndex) != null && paymentInfoDTO.getCardList().get(selectedCardIdIndex).getCardId() == null) || (paymentInfoDTO.getAmount() + paymentInfoDTO.getFeeCharge() + paymentInfoDTO.getVat() >= Constants.SOAP_AMOUNT_MAX)) {
                    Intent intent = new Intent();
                    intent.setClass(activity, BankWebPaymentActivity.class);
                    intent.putExtra(Constants.PAYMENT_INFO, paymentInfoDTO);
                    intent.putExtra(Constants.PSP_INFO, pspInfoDTO);
                    startActivityForResult(intent, 46);
                } else {

                    pay_to_business_button.setEnabled(false);

                    if (pinText.getText().toString().length() <= 4) {
                        Toast.makeText(context, getString(R.string.msg_pin2_incorrect), Toast.LENGTH_SHORT).show();
                        pay_to_business_button.setEnabled(true);
                        return;
                    }
                    if (!(cvvText.getText().toString().length() >= 3 && cvvText.getText().toString().length() <= 4)) {
                        Toast.makeText(context, getString(R.string.msg_cvv2_incorrect), Toast.LENGTH_SHORT).show();
                        pay_to_business_button.setEnabled(true);
                        return;
                    }

                    requestPurchase = new RequestPurchase(activity, new RequestPurchaseTaskCompleteListener(), paymentInfoDTO.getPspInfo().getPayURL());

                    doWorkInfo = new DoWorkInfo();
                    doWorkInfo.setUserName("appstore");
                    doWorkInfo.setPassword("sepapp");
                    doWorkInfo.setCellNumber(pspInfoDTO.getCellNumber().substring(1, pspInfoDTO.getCellNumber().length()));
                    doWorkInfo.setLangAByte((byte) 0);
                    doWorkInfo.setLangABoolean(false);
                    CBUArrayOfKeyValueOfstringstring vectorstring2stringMapEntry = new CBUArrayOfKeyValueOfstringstring();
                    CBUArrayOfKeyValueOfstringstring_KeyValueOfstringstring s2sMapEntry = new CBUArrayOfKeyValueOfstringstring_KeyValueOfstringstring();

                    s2sMapEntry.Key = "Amount";
                    s2sMapEntry.Value = String.valueOf(paymentInfoDTO.getAmount() + paymentInfoDTO.getFeeCharge() + paymentInfoDTO.getVat());
                    vectorstring2stringMapEntry.add(s2sMapEntry);

                    s2sMapEntry = new CBUArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                    s2sMapEntry.Key = "Pin2";
                    s2sMapEntry.Value = userPinCode;
                    vectorstring2stringMapEntry.add(s2sMapEntry);

                    s2sMapEntry = new CBUArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                    s2sMapEntry.Key = "ThirdParty";
                    s2sMapEntry.Value = paymentInfoDTO.getProductCode();
                    vectorstring2stringMapEntry.add(s2sMapEntry);

                    s2sMapEntry = new CBUArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                    s2sMapEntry.Key = "TerminalId";
                    s2sMapEntry.Value = pspInfoDTO.getTerminalId();
                    vectorstring2stringMapEntry.add(s2sMapEntry);

                    s2sMapEntry = new CBUArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                    s2sMapEntry.Key = "CardId";
                    s2sMapEntry.Value = paymentInfoDTO.getCardList().get(selectedCardIdIndex).getCardId();
                    vectorstring2stringMapEntry.add(s2sMapEntry);

                    s2sMapEntry = new CBUArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                    s2sMapEntry.Key = "SenderTerminalId";
                    s2sMapEntry.Value = pspInfoDTO.getSenderTerminalId();
                    vectorstring2stringMapEntry.add(s2sMapEntry);

                    s2sMapEntry = new CBUArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                    s2sMapEntry.Key = "IPAddress";
                    s2sMapEntry.Value = pspInfoDTO.getIpAddress();
                    vectorstring2stringMapEntry.add(s2sMapEntry);

                    s2sMapEntry = new CBUArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                    s2sMapEntry.Key = "Email";
                    s2sMapEntry.Value = "";
                    vectorstring2stringMapEntry.add(s2sMapEntry);

                    s2sMapEntry = new CBUArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                    s2sMapEntry.Key = "CVV2";
                    s2sMapEntry.Value = userCVV2;
                    vectorstring2stringMapEntry.add(s2sMapEntry);

                    s2sMapEntry = new CBUArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                    s2sMapEntry.Key = "ExpDate";
                    s2sMapEntry.Value = paymentInfoDTO.getCardList().get(selectedCardIdIndex).getExpireDate();
                    vectorstring2stringMapEntry.add(s2sMapEntry);

                    s2sMapEntry = new CBUArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                    s2sMapEntry.Key = "ResNum";
                    s2sMapEntry.Value = paymentInfoDTO.getProductCode();
                    vectorstring2stringMapEntry.add(s2sMapEntry);

                    s2sMapEntry = new CBUArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                    s2sMapEntry.Key = "Signature";
                    s2sMapEntry.Value = signature;
                    vectorstring2stringMapEntry.add(s2sMapEntry);

                    doWorkInfo.setVectorstring2stringMapEntry(vectorstring2stringMapEntry);
                    requestPurchase.execute(doWorkInfo);

                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 46) {
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
                pinText.setText("");
                pinLayout.setBackgroundResource(R.drawable.card_info_entry_placeholder);
                cvvLayout.setBackgroundResource(R.drawable.card_info_empty_placeholder);
                pinCodeFocus = true;
                cvvFocus = false;
                break;

            case R.id.cvv_text:
                cvvText.setText("");
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
                if (paymentInfoDTO != null) {
                    selectedCardIdIndex = position;
                    cardNumberValue.setText(persian.E2P(paymentInfoDTO.getCardList().get(position).getLast4Digits()));
                    bankName.setText(paymentInfoDTO.getCardList().get(position).getBankName());
                    selectCardText.setVisibility(View.GONE);
                    cardSelect.setVisibility(View.VISIBLE);
                    if (paymentInfoDTO.getCardList().get(position).getDigitalSignature() != null && paymentInfoDTO.getCardList().get(position).getDigitalSignature().length() > 0) {
                        signature = paymentInfoDTO.getCardList().get(position).getDigitalSignature();
                    } else {
                        SignToPayRequest signToPayRequest = new SignToPayRequest();
                        signToPayRequest.setCardId(paymentInfoDTO.getCardList().get(position).getCardId());
                        signToPayRequest.setProductCode(paymentInfoDTO.getProductCode());
                        new SignToPayTask(activity, BusinessPaymentConfirmActivity.this, signToPayRequest, authToken).execute();
                    }
                }
                break;

            case ADD:
                Intent intent = new Intent();
                intent.setClass(activity, BankWebPaymentActivity.class);
                intent.putExtra(Constants.PAYMENT_INFO, paymentInfoDTO);
                intent.putExtra(Constants.PSP_INFO, pspInfoDTO);
                startActivityForResult(intent, 46);
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
                }
            }
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

    public class RequestPurchaseTaskCompleteListener implements AsyncTaskCompleteListener<CBUArrayOfKeyValueOfstringstring> {

        @Override
        public void onTaskComplete(CBUArrayOfKeyValueOfstringstring purchaseResponseResponseMessage) {

            hamPayDialog.dismisWaitingDialog();
            pay_to_business_button.setEnabled(true);
            ServiceEvent serviceName = null;
            LogEvent logEvent = new LogEvent(context);

            String responseCode = null;
            String description = null;
            String SWTraceNum = null;
            ResultStatus resultStatus = ResultStatus.FAILURE;

            if (purchaseResponseResponseMessage != null) {
                pspResultRequest = new PSPResultRequest();
                for (CBUArrayOfKeyValueOfstringstring_KeyValueOfstringstring s2sMapEntry : purchaseResponseResponseMessage) {
                    if (s2sMapEntry.Key.equalsIgnoreCase("ResponseCode")) {
                        responseCode = s2sMapEntry.Value;
                    } else if (s2sMapEntry.Key.equalsIgnoreCase("Description")) {
                        description = s2sMapEntry.Value;
                    } else if (s2sMapEntry.Key.equalsIgnoreCase("SWTraceNum")) {
                        SWTraceNum = s2sMapEntry.Value;
                    }
                }

                if (responseCode != null) {
                    if (responseCode.equalsIgnoreCase("2000")) {
                        if (paymentInfoDTO != null) {
                            Intent intent = new Intent(context, PaymentCompletedActivity.class);
                            SucceedPayment succeedPayment = new SucceedPayment();
                            succeedPayment.setAmount(paymentInfoDTO.getAmount() + paymentInfoDTO.getVat() + paymentInfoDTO.getFeeCharge());
                            succeedPayment.setCode(paymentInfoDTO.getProductCode());
                            succeedPayment.setTrace(pspInfoDTO.getProviderId());
                            succeedPayment.setPaymentType(PaymentType.PAYMENT);
                            intent.putExtra(Constants.SUCCEED_PAYMENT_INFO, succeedPayment);
                            startActivityForResult(intent, 46);
                            serviceName = ServiceEvent.PSP_PAYMENT_SUCCESS;
                            logEvent.log(serviceName);
                        }
                        resultStatus = ResultStatus.SUCCESS;
                    } else if (responseCode.equalsIgnoreCase("51")) {
                        new HamPayDialog(activity).pspFailResultDialog(responseCode, getString(R.string.msg_insufficient_credit));
                        resultStatus = ResultStatus.FAILURE;
                        serviceName = ServiceEvent.PSP_PAYMENT_FAILURE;
                        logEvent.log(serviceName);
                    } else if (responseCode.equalsIgnoreCase("3000")) {
                        serviceName = ServiceEvent.PSP_PAYMENT_FAILURE;
                        new HamPayDialog(activity).pspFailResultDialog(responseCode, description);
                    } else {
                        PspCode pspCode = new PspCode(context);
                        new HamPayDialog(activity).pspFailResultDialog(responseCode, pspCode.getDescription(responseCode));
                        resultStatus = ResultStatus.FAILURE;
                        serviceName = ServiceEvent.PSP_PAYMENT_FAILURE;
                    }
                    logEvent.log(serviceName);

                    SyncPspResult syncPspResult = new SyncPspResult();
                    syncPspResult.setResponseCode(responseCode);
                    syncPspResult.setProductCode(paymentInfoDTO.getProductCode());
                    syncPspResult.setType("PAYMENT");
                    syncPspResult.setSwTrace(SWTraceNum);
                    syncPspResult.setTimestamp(System.currentTimeMillis());
                    syncPspResult.setStatus(0);
                    dbHelper.createSyncPspResult(syncPspResult);

                    pspResultRequest.setPspResponseCode(responseCode);
                    pspResultRequest.setProductCode(paymentInfoDTO.getProductCode());
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
                    forceLogout();
                    serviceName = ServiceEvent.PSP_RESULT_FAILURE;
                } else {
                    serviceName = ServiceEvent.PSP_RESULT_FAILURE;
                }
            }
            logEvent.log(serviceName);

            pay_to_business_button.setEnabled(true);
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }
}
