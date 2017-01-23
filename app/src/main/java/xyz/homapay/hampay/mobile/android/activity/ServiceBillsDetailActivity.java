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
import xyz.homapay.hampay.common.core.model.request.PSPResultRequest;
import xyz.homapay.hampay.common.core.model.request.SignToPayRequest;
import xyz.homapay.hampay.common.core.model.request.UtilityBillDetailRequest;
import xyz.homapay.hampay.common.core.model.response.PSPResultResponse;
import xyz.homapay.hampay.common.core.model.response.SignToPayResponse;
import xyz.homapay.hampay.common.core.model.response.UtilityBillDetailResponse;
import xyz.homapay.hampay.common.core.model.response.dto.BillInfoDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.Helper.DatabaseHelper;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.animation.Collapse;
import xyz.homapay.hampay.mobile.android.animation.Expand;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestPSPResult;
import xyz.homapay.hampay.mobile.android.async.RequestTokenBills;
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
import xyz.homapay.hampay.mobile.android.util.AppManager;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.CurrencyFormatter;
import xyz.homapay.hampay.mobile.android.util.JalaliConvert;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;
import xyz.homapay.hampay.mobile.android.util.PspCode;
import xyz.homapay.hampay.mobile.android.webservice.psp.bills.MKAArrayOfKeyValueOfstringstring;
import xyz.homapay.hampay.mobile.android.webservice.psp.bills.MKAArrayOfKeyValueOfstringstring_KeyValueOfstringstring;

public class ServiceBillsDetailActivity extends AppCompatActivity implements View.OnClickListener, CardNumberDialog.SelectCardDialogListener, OnTaskCompleted {

    @BindView(R.id.pay_button)
    ImageView pay_button;
    @BindView(R.id.billsImage)
    ImageView billsImage;
    @BindView(R.id.billsName)
    FacedTextView billsName;
    @BindView(R.id.billsDate)
    FacedTextView billsDate;
    @BindView(R.id.billsId)
    FacedTextView billsId;
    @BindView(R.id.payId)
    FacedTextView payId;
    @BindView(R.id.billsAmount)
    FacedTextView billsAmount;
    @BindView(R.id.hampayFee)
    FacedTextView hampayFee;
    @BindView(R.id.billsTotalAmount)
    FacedTextView billsTotalAmount;
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
    @BindView(R.id.cardPlaceHolder)
    RelativeLayout cardPlaceHolder;
    @BindView(R.id.selectCardText)
    FacedTextView selectCardText;
    @BindView(R.id.cardSelect)
    LinearLayout cardSelect;
    private DatabaseHelper dbHelper;
    private PSPResultRequest pspResultRequest;
    private RequestPSPResult requestPSPResult;
    private RequestTokenBills requestTokenBills;
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
    private BillInfoDTO billsInfo = null;
    private BillsTokenDoWork billsTokenDoWork;
    private int selectedCardIdIndex = -1;
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
        setContentView(R.layout.activity_service_bills_detail);
        ButterKnife.bind(this);
        context = this;
        activity = ServiceBillsDetailActivity.this;
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

        hamPayDialog = new HamPayDialog(activity);

        currencyFormatter = new CurrencyFormatter();

        Intent intent = getIntent();
        billsInfo = (BillInfoDTO) intent.getSerializableExtra(Constants.BILL_INFO);
        providerId = intent.getStringExtra(Constants.PROVIDER_ID);

        if (billsInfo != null) {
            fillUI(billsInfo);
        } else if (providerId != null) {
            UtilityBillDetailRequest utilityBillDetailRequest = new UtilityBillDetailRequest();
            utilityBillDetailRequest.setProviderId(providerId);
            new UtilityBillDetailTask(activity, ServiceBillsDetailActivity.this, utilityBillDetailRequest, authToken).execute();
        }

        cardPlaceHolder.setOnClickListener(v -> {
            CardNumberDialog cardNumberDialog = new CardNumberDialog();
            if (billsInfo == null) return;
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.CARD_LIST, (Serializable) billsInfo.getCardList());
            cardNumberDialog.setArguments(bundle);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(cardNumberDialog, null);
            fragmentTransaction.commitAllowingStateLoss();
        });

        pay_button.setOnClickListener(v -> {

            if (billsInfo == null) return;

            if (selectedCardIdIndex == -1 || (billsInfo.getCardList().get(selectedCardIdIndex) != null && billsInfo.getCardList().get(selectedCardIdIndex).getCardId() == null) || (billsInfo.getAmount() + billsInfo.getFeeCharge() >= Constants.SOAP_AMOUNT_MAX)) {
                Intent intent1 = new Intent();
                intent1.setClass(activity, BankWebPaymentActivity.class);
                intent1.putExtra(Constants.BILL_INFO, billsInfo);
                startActivityForResult(intent1, 47);
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
                requestTokenBills = new RequestTokenBills(activity, new RequestPurchaseTaskCompleteListener(), billsInfo.getPspInfo().getPayURL());

                billsTokenDoWork = new BillsTokenDoWork();
                billsTokenDoWork.setUserName("appstore");
                billsTokenDoWork.setPassword("sepapp");
                billsTokenDoWork.setCellNumber(billsInfo.getPspInfo().getCellNumber().substring(1, billsInfo.getPspInfo().getCellNumber().length()));
                billsTokenDoWork.setLangAByte((byte) 0);
                billsTokenDoWork.setLangABoolean(false);
                MKAArrayOfKeyValueOfstringstring vectorstring2stringMapEntry = new MKAArrayOfKeyValueOfstringstring();
                MKAArrayOfKeyValueOfstringstring_KeyValueOfstringstring s2sMapEntry = new MKAArrayOfKeyValueOfstringstring_KeyValueOfstringstring();

                s2sMapEntry.Key = "Amount";
                s2sMapEntry.Value = String.valueOf(billsInfo.getAmount() + billsInfo.getFeeCharge());
                vectorstring2stringMapEntry.add(s2sMapEntry);

                s2sMapEntry = new MKAArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                s2sMapEntry.Key = "Pin2";
                s2sMapEntry.Value = userPinCode;
                vectorstring2stringMapEntry.add(s2sMapEntry);

                s2sMapEntry = new MKAArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                s2sMapEntry.Key = "ThirdParty";
                s2sMapEntry.Value = billsInfo.getProductCode();
                vectorstring2stringMapEntry.add(s2sMapEntry);

                s2sMapEntry = new MKAArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                s2sMapEntry.Key = "TerminalId";
                s2sMapEntry.Value = billsInfo.getPspInfo().getTerminalId();
                vectorstring2stringMapEntry.add(s2sMapEntry);

                s2sMapEntry = new MKAArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                s2sMapEntry.Key = "SenderTerminalId";
                s2sMapEntry.Value = billsInfo.getPspInfo().getSenderTerminalId();
                vectorstring2stringMapEntry.add(s2sMapEntry);

                s2sMapEntry = new MKAArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                s2sMapEntry.Key = "Email";
                s2sMapEntry.Value = "";
                vectorstring2stringMapEntry.add(s2sMapEntry);

                s2sMapEntry = new MKAArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                s2sMapEntry.Key = "IPAddress";
                s2sMapEntry.Value = billsInfo.getPspInfo().getIpAddress();
                vectorstring2stringMapEntry.add(s2sMapEntry);

                s2sMapEntry = new MKAArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                s2sMapEntry.Key = "CVV2";
                s2sMapEntry.Value = userCVV2;
                vectorstring2stringMapEntry.add(s2sMapEntry);

                s2sMapEntry = new MKAArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                s2sMapEntry.Key = "ExpDate";
                s2sMapEntry.Value = billsInfo.getCardList().get(selectedCardIdIndex).getExpireDate();
                vectorstring2stringMapEntry.add(s2sMapEntry);

                s2sMapEntry = new MKAArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                s2sMapEntry.Key = "CardId";
                s2sMapEntry.Value = billsInfo.getCardList().get(selectedCardIdIndex).getCardId();
                vectorstring2stringMapEntry.add(s2sMapEntry);

                s2sMapEntry = new MKAArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                s2sMapEntry.Key = "ResNum";
                s2sMapEntry.Value = billsInfo.getProductCode();
                vectorstring2stringMapEntry.add(s2sMapEntry);

                s2sMapEntry = new MKAArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                s2sMapEntry.Key = "BillId";
                s2sMapEntry.Value = billsInfo.getBillId();
                vectorstring2stringMapEntry.add(s2sMapEntry);

                s2sMapEntry = new MKAArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                s2sMapEntry.Key = "PayId";
                s2sMapEntry.Value = billsInfo.getPayId();
                vectorstring2stringMapEntry.add(s2sMapEntry);

                s2sMapEntry = new MKAArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                s2sMapEntry.Key = "Signature";
                s2sMapEntry.Value = signature;
                vectorstring2stringMapEntry.add(s2sMapEntry);


                billsTokenDoWork.setVectorstring2stringMapEntry(vectorstring2stringMapEntry);
                requestTokenBills.execute(billsTokenDoWork);

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
                if (billsInfo != null) {
                    selectedCardIdIndex = position;
                    cardNumberValue.setText(persian.E2P(billsInfo.getCardList().get(position).getLast4Digits()));
                    bankName.setText(billsInfo.getCardList().get(position).getBankName());
                    selectCardText.setVisibility(View.GONE);
                    cardSelect.setVisibility(View.VISIBLE);
                    if (billsInfo.getCardList().get(position).getDigitalSignature() != null && billsInfo.getCardList().get(position).getDigitalSignature().length() > 0) {
                        signature = billsInfo.getCardList().get(position).getDigitalSignature();
                    } else {
                        SignToPayRequest signToPayRequest = new SignToPayRequest();
                        signToPayRequest.setCardId(billsInfo.getCardList().get(position).getCardId());
                        signToPayRequest.setProductCode(billsInfo.getProductCode());
                        signToPayRequest.setFundType(FundType.UTILITY_BILL);
                        new SignToPayTask(activity, ServiceBillsDetailActivity.this, signToPayRequest, authToken).execute();
                    }
                }
                break;

            case ADD:
                Intent intent = new Intent();
                intent.setClass(activity, BankWebPaymentActivity.class);
                intent.putExtra(Constants.BILL_INFO, billsInfo);
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
                        ResponseMessage<UtilityBillDetailResponse> utilityBillDetail = (ResponseMessage) object;
                        switch (responseMessage.getService().getResultStatus()) {
                            case SUCCESS:
                                billsInfo = utilityBillDetail.getService().getBillInfoDTO();
                                fillUI(billsInfo);
                                break;
                            default:
                                break;
                        }
                        break;
                }
            }
        }
    }

    private void fillUI(BillInfoDTO billInfo) {
        billsName.setText(billInfo.getUtilityName());
        billsDate.setText(persian.E2P(new JalaliConvert().GregorianToPersian(billInfo.getCreatedBy())));
        if (billInfo.getImageId() != null) {
            billsImage.setTag(billInfo.getImageId());
            ImageHelper.getInstance(context).imageLoader(billInfo.getImageId(), billsImage, R.drawable.user_placeholder);
        } else {
            billsImage.setImageResource(R.drawable.user_placeholder);
        }

        billsId.setText(persian.E2P(billInfo.getBillId()));
        payId.setText(persian.E2P(billInfo.getPayId()));
        billsAmount.setText(persian.E2P(currencyFormatter.format(billInfo.getAmount())));
        hampayFee.setText(persian.E2P(currencyFormatter.format(billInfo.getFeeCharge())));

        billsTotalAmount.setText(persian.E2P(currencyFormatter.format(billInfo.getAmount() + billInfo.getFeeCharge())));

        if (billInfo.getCardList().size() > 0) {
            if (billInfo.getCardList().get(0).getCardId() != null && (billInfo.getAmount() + billInfo.getFeeCharge() < Constants.SOAP_AMOUNT_MAX)) {
                cardNumberValue.setText(persian.E2P(billInfo.getCardList().get(0).getLast4Digits()));
                bankName.setText(billInfo.getCardList().get(0).getBankName());
            }
        }


        if (billInfo.getCardList().size() > 0) {
            if (billInfo.getCardList().get(0).getCardId() != null && (billInfo.getAmount() + billInfo.getFeeCharge() < Constants.SOAP_AMOUNT_MAX)) {
                cardNumberValue.setText(persian.E2P(billInfo.getCardList().get(0).getLast4Digits()));
                bankName.setText(billInfo.getCardList().get(0).getBankName());
                selectedCardIdIndex = 0;
                selectCardText.setVisibility(View.GONE);
                cardSelect.setVisibility(View.VISIBLE);
                if (billInfo.getCardList().get(0).getDigitalSignature() != null && billInfo.getCardList().get(0).getDigitalSignature().length() > 0) {
                    signature = billInfo.getCardList().get(0).getDigitalSignature();
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
                    if (responseCode.equalsIgnoreCase("1") && SWTraceNum != null) {
                        serviceName = ServiceEvent.PSP_PAYMENT_SUCCESS;
                        if (billsInfo != null) {
                            Intent intent = new Intent(context, PaymentCompletedActivity.class);
                            SucceedPayment succeedPayment = new SucceedPayment();
                            succeedPayment.setAmount(billsInfo.getAmount() + billsInfo.getFeeCharge());
                            succeedPayment.setCode(billsInfo.getBillId());
                            succeedPayment.setTrace(billsInfo.getPspInfo().getProviderId());
                            succeedPayment.setPaymentType(PaymentType.BILLS);
                            intent.putExtra(Constants.SUCCEED_PAYMENT_INFO, succeedPayment);
                            startActivityForResult(intent, 47);
                        }
                        resultStatus = ResultStatus.SUCCESS;
                    } else if (responseCode.equalsIgnoreCase("27")) {
                        new HamPayDialog(activity).pspFailResultDialog(responseCode, getString(R.string.bill_already_paid));
                        resultStatus = ResultStatus.FAILURE;
                    } else if (responseCode.equalsIgnoreCase("17") || responseCode.equalsIgnoreCase("25") || responseCode.equalsIgnoreCase("56")) {
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
                    syncPspResult.setProductCode(billsInfo.getProductCode());
                    syncPspResult.setType("UTILITY_BILL");
                    syncPspResult.setSwTrace(SWTraceNum);
                    syncPspResult.setTimestamp(System.currentTimeMillis());
                    syncPspResult.setStatus(0);
                    syncPspResult.setPspName(PSPName.SAMAN.getCode());
                    syncPspResult.setCardId(billsInfo.getCardList().get(selectedCardIdIndex).getCardId());
                    dbHelper.createSyncPspResult(syncPspResult);

                    pspResultRequest.setPspResponseCode(responseCode);
                    pspResultRequest.setProductCode(billsInfo.getProductCode());
                    pspResultRequest.setTrackingCode(SWTraceNum);
                    pspResultRequest.setResultType(PSPResultRequest.ResultType.UTILITY_BILL);
                    pspResultRequest.setCardDTO(billsInfo.getCardList().get(selectedCardIdIndex));
                    pspResultRequest.setPspName(PSPName.SAMAN);
                    syncPspResult.setPspName(PSPName.SAMAN.getCode());
                    syncPspResult.setCardId(billsInfo.getCardList().get(selectedCardIdIndex).getCardId());
                    requestPSPResult = new RequestPSPResult(context, new RequestPSPResultTaskCompleteListener(billsInfo.getProductCode()));
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

}
