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
import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.homapay.hampay.common.common.PSPName;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.enums.FundType;
import xyz.homapay.hampay.common.core.model.request.LatestPaymentRequest;
import xyz.homapay.hampay.common.core.model.request.PSPResultRequest;
import xyz.homapay.hampay.common.core.model.request.PaymentDetailRequest;
import xyz.homapay.hampay.common.core.model.request.SignToPayRequest;
import xyz.homapay.hampay.common.core.model.response.LatestPaymentResponse;
import xyz.homapay.hampay.common.core.model.response.PSPResultResponse;
import xyz.homapay.hampay.common.core.model.response.PaymentDetailResponse;
import xyz.homapay.hampay.common.core.model.response.SignToPayResponse;
import xyz.homapay.hampay.common.core.model.response.dto.PaymentInfoDTO;
import xyz.homapay.hampay.common.core.model.response.dto.PspInfoDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.Helper.DatabaseHelper;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.animation.Collapse;
import xyz.homapay.hampay.mobile.android.animation.Expand;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestLatestPayment;
import xyz.homapay.hampay.mobile.android.async.RequestPSPResult;
import xyz.homapay.hampay.mobile.android.async.RequestPaymentDetail;
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
import xyz.homapay.hampay.mobile.android.util.AppManager;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.CurrencyFormatter;
import xyz.homapay.hampay.mobile.android.util.JalaliConvert;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;
import xyz.homapay.hampay.mobile.android.util.PspCode;
import xyz.homapay.hampay.mobile.android.webservice.psp.CBUArrayOfKeyValueOfstringstring;
import xyz.homapay.hampay.mobile.android.webservice.psp.CBUArrayOfKeyValueOfstringstring_KeyValueOfstringstring;

public class InvoicePendingConfirmationActivity extends AppCompatActivity implements View.OnClickListener, CardNumberDialog.SelectCardDialogListener, OnTaskCompleted {

    @BindView(R.id.pay_button)
    ImageView pay_button;
    @BindView(R.id.user_image)
    ImageView user_image;
    @BindView(R.id.callerName)
    FacedTextView callerName;
    @BindView(R.id.paymentCode)
    FacedTextView paymentCode;
    @BindView(R.id.received_message_holder)
    LinearLayout received_message_holder;
    @BindView(R.id.received_message)
    FacedTextView received_message;
    @BindView(R.id.create_date)
    FacedTextView create_date;
    @BindView(R.id.paymentPriceValue)
    FacedTextView paymentPriceValue;
    @BindView(R.id.paymentFeeValue)
    FacedTextView paymentFeeValue;
    @BindView(R.id.paymentTotalValue)
    FacedTextView paymentTotalValue;
    @BindView(R.id.bankName)
    FacedTextView bankName;
    @BindView(R.id.cardNumberValue)
    FacedTextView cardNumberValue;
    @BindView(R.id.keyboard)
    LinearLayout keyboard;
    @BindView(R.id.pin_layout)
    RelativeLayout pinLayout;
    @BindView(R.id.pin_text)
    FacedTextView pinText;
    @BindView(R.id.cvv_layout)
    RelativeLayout cvvLayout;
    @BindView(R.id.cvv_text)
    FacedTextView cvvText;
    @BindView(R.id.paymentScroll)
    ScrollView paymentScroll;
    @BindView(R.id.selectCardText)
    FacedTextView selectCardText;
    @BindView(R.id.cardSelect)
    LinearLayout cardSelect;
    private DatabaseHelper dbHelper;
    private CurrencyFormatter currencyFormatter;
    private Context context;
    private Activity activity;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private boolean pinCodeFocus = false;
    private boolean cvvFocus = false;
    private String userPinCode = "";
    private String userCVV2 = "";
    private PersianEnglishDigit persian = new PersianEnglishDigit();
    private HamPayDialog hamPayDialog;
    private PaymentInfoDTO paymentInfoDTO = null;
    private PspInfoDTO pspInfoDTO = null;
    private String providerId = null;
    private RequestPurchase requestPurchase;
    private DoWorkInfo doWorkInfo;
    private PersianEnglishDigit persianEnglishDigit;
    private RequestPSPResult requestPSPResult;
    private PSPResultRequest pspResultRequest;
    private RequestLatestPayment requestLatestPayment;
    private LatestPaymentRequest latestPaymentRequest;
    private int selectedCardIdIndex = -1;
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
        setContentView(R.layout.activity_invoice_payment_pending);
        ButterKnife.bind(this);

        context = this;
        activity = InvoicePendingConfirmationActivity.this;
        PugNotification.with(context).cancel(Constants.INVOICE_NOTIFICATION_IDENTIFIER);
        dbHelper = new DatabaseHelper(context);
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

        hamPayDialog = new HamPayDialog(activity);

        persianEnglishDigit = new PersianEnglishDigit();
        currencyFormatter = new CurrencyFormatter();

        Intent intent = getIntent();
        providerId = intent.getStringExtra(Constants.PROVIDER_ID);

        if (paymentInfoDTO != null) {
            fillPayment(paymentInfoDTO, pspInfoDTO);
        } else if (providerId != null) {
            PaymentDetailRequest paymentDetailRequest = new PaymentDetailRequest();
            paymentDetailRequest.setProviderId(providerId);
            RequestPaymentDetail requestPaymentDetail = new RequestPaymentDetail(activity, new RequestPaymentDetailTaskCompleteListener());
            requestPaymentDetail.execute(paymentDetailRequest);
        } else {
            AppManager.setMobileTimeout(context);
            editor.commit();
            requestLatestPayment = new RequestLatestPayment(activity, new RequestLatestPaymentTaskCompleteListener());
            latestPaymentRequest = new LatestPaymentRequest();
            requestLatestPayment.execute(latestPaymentRequest);
        }
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
            case R.id.cardPlaceHolder:
                CardNumberDialog cardNumberDialog = new CardNumberDialog();
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.CARD_LIST, (Serializable) paymentInfoDTO.getCardList());
                cardNumberDialog.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.add(cardNumberDialog, null);
                fragmentTransaction.commitAllowingStateLoss();
                break;
            case R.id.pay_button:
                if (pspInfoDTO == null) return;

                if (cvvText.getText().toString().trim().length() < 3) {
                    Toast.makeText(context, R.string.err_cvv_lenght, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (pinText.getText().toString().trim().length() < 5) {
                    Toast.makeText(context, R.string.err_pin_lenght, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (selectedCardIdIndex == -1 || (paymentInfoDTO.getCardList().get(selectedCardIdIndex) != null && paymentInfoDTO.getCardList().get(selectedCardIdIndex).getCardId() == null) || (paymentInfoDTO.getAmount() + paymentInfoDTO.getFeeCharge() >= Constants.SOAP_AMOUNT_MAX)) {
                    Intent intent1 = new Intent();
                    intent1.setClass(activity, BankWebPaymentActivity.class);
                    intent1.putExtra(Constants.PAYMENT_INFO, paymentInfoDTO);
                    intent1.putExtra(Constants.PSP_INFO, pspInfoDTO);
                    startActivityForResult(intent1, 46);
                } else {
                    if (pinText.getText().toString().length() <= 4) {
                        Toast.makeText(context, getString(R.string.msg_pin2_incorrect), Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (!(cvvText.getText().toString().length() >= 3 && cvvText.getText().toString().length() <= 4)) {
                        Toast.makeText(context, getString(R.string.msg_cvv2_incorrect), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    AppManager.setMobileTimeout(context);
                    editor.commit();

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
                    s2sMapEntry.Value = String.valueOf(paymentInfoDTO.getAmount() + paymentInfoDTO.getFeeCharge());
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
                        signToPayRequest.setFundType(FundType.PAYMENT);
                        new SignToPayTask(activity, InvoicePendingConfirmationActivity.this, signToPayRequest, authToken).execute();
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

    private void fillPayment(PaymentInfoDTO paymentInfo, PspInfoDTO pspInfo) {
        callerName.setText(paymentInfo.getCallerName());
        paymentCode.setText(persianEnglishDigit.E2P(getString(R.string.payment_request_code) + paymentInfo.getProductCode()));
        create_date.setText(persianEnglishDigit.E2P(new JalaliConvert().GregorianToPersian(paymentInfo.getCreatedBy())));
        if (paymentInfo.getMessage() != null && paymentInfo.getMessage().length() > 0) {
            new Expand(received_message_holder).animate();
            received_message.setText(paymentInfo.getMessage().trim());
        }
        paymentPriceValue.setText(persianEnglishDigit.E2P(currencyFormatter.format(paymentInfo.getAmount())));
        paymentFeeValue.setText(persianEnglishDigit.E2P(currencyFormatter.format(paymentInfo.getFeeCharge())));
        paymentTotalValue.setText(persianEnglishDigit.E2P(currencyFormatter.format(paymentInfo.getAmount() + paymentInfo.getFeeCharge())));

        if (paymentInfo.getCardList().size() > 0) {
            if (paymentInfo.getCardList().get(0).getCardId() != null && (paymentInfo.getAmount() + paymentInfo.getFeeCharge() < Constants.SOAP_AMOUNT_MAX)) {
                cardNumberValue.setText(persian.E2P(paymentInfo.getCardList().get(0).getLast4Digits()));
                bankName.setText(paymentInfo.getCardList().get(0).getBankName());
            }
        }

        if (paymentInfo.getImageId() != null) {
            user_image.setTag(paymentInfo.getImageId());
            ImageHelper.getInstance(activity).imageLoader(paymentInfo.getImageId(), user_image, R.drawable.user_placeholder);
        } else {
            user_image.setImageResource(R.drawable.user_placeholder);
        }

        if (paymentInfo.getCardList().size() > 0) {
            if (paymentInfo.getCardList().get(0).getCardId() != null && (paymentInfo.getAmount() + paymentInfo.getFeeCharge() < Constants.SOAP_AMOUNT_MAX)) {
                cardNumberValue.setText(persian.E2P(paymentInfo.getCardList().get(0).getLast4Digits()));
                bankName.setText(paymentInfo.getCardList().get(0).getBankName());
                selectedCardIdIndex = 0;
                selectCardText.setVisibility(View.GONE);
                cardSelect.setVisibility(View.VISIBLE);
                if (paymentInfo.getCardList().get(0).getDigitalSignature() != null && paymentInfo.getCardList().get(0).getDigitalSignature().length() > 0) {
                    signature = paymentInfo.getCardList().get(0).getDigitalSignature();
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

            String responseCode = null;
            String description = null;
            String SWTraceNum = null;
            ResultStatus resultStatus = ResultStatus.FAILURE;
            ServiceEvent serviceName = ServiceEvent.PSP_PAYMENT_FAILURE;
            LogEvent logEvent = new LogEvent(context);

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
                        serviceName = ServiceEvent.PSP_PAYMENT_SUCCESS;
                        if (paymentInfoDTO != null) {
                            Intent intent = new Intent(context, PaymentCompletedActivity.class);
                            SucceedPayment succeedPayment = new SucceedPayment();
                            succeedPayment.setAmount(paymentInfoDTO.getAmount() + paymentInfoDTO.getFeeCharge());
                            succeedPayment.setCode(paymentInfoDTO.getProductCode());
                            succeedPayment.setTrace(pspInfoDTO.getProviderId());
                            succeedPayment.setPaymentType(PaymentType.PAYMENT);
                            intent.putExtra(Constants.SUCCEED_PAYMENT_INFO, succeedPayment);
                            startActivityForResult(intent, 46);
                        }
                        resultStatus = ResultStatus.SUCCESS;
                    } else if (responseCode.equalsIgnoreCase("17") || responseCode.equalsIgnoreCase("25") || responseCode.equalsIgnoreCase("27") || responseCode.equalsIgnoreCase("56")) {
                        new HamPayDialog(activity).pspFailResultDialog(responseCode, getString(R.string.token_special_issue));
                        resultStatus = ResultStatus.FAILURE;
                    } else {
                        serviceName = ServiceEvent.PSP_PAYMENT_FAILURE;
                        PspCode pspCode = new PspCode(context);
                        if (pspCode.getDescription(responseCode) == null) {
                            new HamPayDialog(activity).pspFailResultDialog(responseCode, getString(R.string.token_special_issue));
                        } else {
                            new HamPayDialog(activity).pspFailResultDialog(responseCode, pspCode.getDescription(responseCode));
                        }
                        resultStatus = ResultStatus.FAILURE;
                    }
                    logEvent.log(serviceName);

                    SyncPspResult syncPspResult = new SyncPspResult();
                    syncPspResult.setResponseCode(responseCode);
                    syncPspResult.setProductCode(paymentInfoDTO.getProductCode());
                    syncPspResult.setType("PAYMENT");
                    syncPspResult.setSwTrace(SWTraceNum);
                    syncPspResult.setTimestamp(System.currentTimeMillis());
                    syncPspResult.setStatus(0);
                    syncPspResult.setPspName(PSPName.SAMAN.getCode());
                    syncPspResult.setCardId(paymentInfoDTO.getCardList().get(selectedCardIdIndex).getCardId());
                    dbHelper.createSyncPspResult(syncPspResult);

                    pspResultRequest.setPspResponseCode(responseCode);
                    pspResultRequest.setProductCode(paymentInfoDTO.getProductCode());
                    pspResultRequest.setTrackingCode(SWTraceNum);
                    pspResultRequest.setResultType(PSPResultRequest.ResultType.PAYMENT);
                    pspResultRequest.setCardDTO(paymentInfoDTO.getCardList().get(selectedCardIdIndex));
                    pspResultRequest.setPspName(PSPName.SAMAN);
                    requestPSPResult = new RequestPSPResult(context, new RequestPSPResultTaskCompleteListener(paymentInfoDTO.getProductCode()));
                    requestPSPResult.execute(pspResultRequest);

                } else {
                    new HamPayDialog(activity).pspFailResultDialog(Constants.LOCAL_ERROR_CODE, getString(R.string.msg_soap_timeout));
                }
                AppManager.setMobileTimeout(context);
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
        private String productCode;

        public RequestPSPResultTaskCompleteListener(String productCode) {
            this.productCode = productCode;
        }

        @Override
        public void onTaskComplete(ResponseMessage<PSPResultResponse> pspResultResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            if (pspResultResponseMessage != null) {
                if (pspResultResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    serviceName = ServiceEvent.PSP_RESULT_SUCCESS;
                    if (productCode != null) {
                        dbHelper.syncPspResult(productCode);
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

    public class RequestLatestPaymentTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<LatestPaymentResponse>> {

        @Override
        public void onTaskComplete(ResponseMessage<LatestPaymentResponse> latestPaymentResponseMessage) {

            ServiceEvent serviceName;
            LogEvent logEvent = new LogEvent(context);
            hamPayDialog.dismisWaitingDialog();

            if (latestPaymentResponseMessage != null) {
                if (latestPaymentResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    serviceName = ServiceEvent.GET_LATEST_PAYMENT_SUCCESS;
                    paymentInfoDTO = latestPaymentResponseMessage.getService().getPaymentInfoDTO();
                    pspInfoDTO = latestPaymentResponseMessage.getService().getPaymentInfoDTO().getPspInfo();
                    if (paymentInfoDTO == null) {
                        new HamPayDialog(activity).showFailPendingPaymentDialog(requestLatestPayment, latestPaymentRequest,
                                Constants.LOCAL_ERROR_CODE,
                                getString(R.string.msg_pending_not_found));
                        return;
                    }

                    fillPayment(paymentInfoDTO, pspInfoDTO);

                    dbHelper.createViewedPaymentRequest(paymentInfoDTO.getProductCode());

                } else if (latestPaymentResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    serviceName = ServiceEvent.GET_LATEST_PAYMENT_FAILURE;
                    forceLogout();
                } else {
                    serviceName = ServiceEvent.GET_LATEST_PAYMENT_FAILURE;
                    requestLatestPayment = new RequestLatestPayment(context, new RequestLatestPaymentTaskCompleteListener());
                    new HamPayDialog(activity).showFailPendingPaymentDialog(requestLatestPayment, latestPaymentRequest,
                            latestPaymentResponseMessage.getService().getResultStatus().getCode(),
                            latestPaymentResponseMessage.getService().getResultStatus().getDescription());
                }
            } else {
                serviceName = ServiceEvent.GET_LATEST_PAYMENT_FAILURE;
                requestLatestPayment = new RequestLatestPayment(context, new RequestLatestPaymentTaskCompleteListener());
                new HamPayDialog(activity).showFailPendingPaymentDialog(requestLatestPayment, latestPaymentRequest,
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

    public class RequestPaymentDetailTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<PaymentDetailResponse>> {

        ServiceEvent serviceName;
        LogEvent logEvent = new LogEvent(context);

        @Override
        public void onTaskComplete(ResponseMessage<PaymentDetailResponse> paymentDetailResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            if (paymentDetailResponseMessage != null) {
                if (paymentDetailResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    serviceName = ServiceEvent.PAYMENT_DETAIL_SUCCESS;
                    pay_button.setVisibility(View.VISIBLE);
                    paymentInfoDTO = paymentDetailResponseMessage.getService().getPaymentInfo();
                    pspInfoDTO = paymentDetailResponseMessage.getService().getPaymentInfo().getPspInfo();
                    fillPayment(paymentInfoDTO, pspInfoDTO);
                } else if (paymentDetailResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    serviceName = ServiceEvent.PAYMENT_DETAIL_FAILURE;
                    forceLogout();
                } else {
                    serviceName = ServiceEvent.PAYMENT_DETAIL_FAILURE;
                }
                logEvent.log(serviceName);
            }
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }
}
