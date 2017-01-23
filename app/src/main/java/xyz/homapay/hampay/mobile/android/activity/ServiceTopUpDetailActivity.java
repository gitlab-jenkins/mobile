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
import xyz.homapay.hampay.common.core.model.response.TopUpDetailResponse;
import xyz.homapay.hampay.common.core.model.response.dto.FundDTO;
import xyz.homapay.hampay.common.core.model.response.dto.TopUpInfoDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.Helper.DatabaseHelper;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.animation.Collapse;
import xyz.homapay.hampay.mobile.android.animation.Expand;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestPSPResult;
import xyz.homapay.hampay.mobile.android.async.RequestTokenTopUp;
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
import xyz.homapay.hampay.mobile.android.model.PaymentType;
import xyz.homapay.hampay.mobile.android.model.SucceedPayment;
import xyz.homapay.hampay.mobile.android.model.SyncPspResult;
import xyz.homapay.hampay.mobile.android.model.TopUpTokenDoWork;
import xyz.homapay.hampay.mobile.android.util.AppManager;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.CurrencyFormatter;
import xyz.homapay.hampay.mobile.android.util.JalaliConvert;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;
import xyz.homapay.hampay.mobile.android.util.PspCode;
import xyz.homapay.hampay.mobile.android.webservice.psp.topup.HHBArrayOfKeyValueOfstringstring;
import xyz.homapay.hampay.mobile.android.webservice.psp.topup.HHBArrayOfKeyValueOfstringstring_KeyValueOfstringstring;

public class ServiceTopUpDetailActivity extends AppCompatActivity implements View.OnClickListener, CardNumberDialog.SelectCardDialogListener, OnTaskCompleted {

    @BindView(R.id.pay_button)
    ImageView pay_button;
    @BindView(R.id.imgOperator)
    ImageView imgOperator;
    @BindView(R.id.tvTopUpName)
    FacedTextView tvTopUpName;
    @BindView(R.id.topUpDate)
    FacedTextView topUpDate;
    @BindView(R.id.tvCellNumber)
    FacedTextView tvCellNumber;
    @BindView(R.id.tvAmount)
    FacedTextView tvAmount;
    @BindView(R.id.tvTopUpTax)
    FacedTextView tvTopUpTax;
    @BindView(R.id.hampayFee)
    FacedTextView hampayFee;
    @BindView(R.id.tvTopUpTotalAmount)
    FacedTextView tvTopUpTotalAmount;
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
    private RequestTokenTopUp requestTokenBills;
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
    private TopUpInfoDTO topUpInfo = null;
    private TopUpTokenDoWork topUpTokenDoWork;
    private int selectedCardIdIndex = -1;
    private String signature;
    private String providerId = null;
    private String authToken = "";
    private FundDTO fundDTO;

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
        ButterKnife.bind(this);

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
        pinText.setOnClickListener(this);
        cvvText.setOnClickListener(this);
        hamPayDialog = new HamPayDialog(activity);
        currencyFormatter = new CurrencyFormatter();
        Intent intent = getIntent();
        topUpInfo = (TopUpInfoDTO) intent.getSerializableExtra(Constants.TOP_UP_INFO);
        fundDTO = (FundDTO) intent.getSerializableExtra(Constants.FUND_DTO);
        providerId = intent.getStringExtra(Constants.PROVIDER_ID);

        if (topUpInfo != null) {
            fillUI(topUpInfo);
        } else if (providerId != null) {
            UtilityBillDetailRequest utilityBillDetailRequest = new UtilityBillDetailRequest();
            utilityBillDetailRequest.setProviderId(providerId);
            new UtilityBillDetailTask(activity, ServiceTopUpDetailActivity.this, utilityBillDetailRequest, authToken).execute();
        }

        cardPlaceHolder.setOnClickListener(v -> {
            CardNumberDialog cardNumberDialog = new CardNumberDialog();
            if (topUpInfo == null) return;
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.CARD_LIST, (Serializable) topUpInfo.getCardList());
            cardNumberDialog.setArguments(bundle);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(cardNumberDialog, null);
            fragmentTransaction.commitAllowingStateLoss();
        });

        pay_button.setOnClickListener(v -> {

            if (topUpInfo == null) return;

            if (selectedCardIdIndex == -1 || (topUpInfo.getCardList().get(selectedCardIdIndex) != null && topUpInfo.getCardList().get(selectedCardIdIndex).getCardId() == null) || (topUpInfo.getChargePackage().getAmount() + topUpInfo.getFeeCharge() >= Constants.SOAP_AMOUNT_MAX)) {
                Intent intent1 = new Intent();
                intent1.setClass(activity, BankWebPaymentActivity.class);
                intent1.putExtra(Constants.TOP_UP_INFO, topUpInfo);
                startActivityForResult(intent1, 48);
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
                requestTokenBills = new RequestTokenTopUp(activity, new RequestPurchaseTaskCompleteListener(), topUpInfo.getPspInfo().getPayURL());

                topUpTokenDoWork = new TopUpTokenDoWork();
                topUpTokenDoWork.setUserName("appstore");
                topUpTokenDoWork.setPassword("sepapp");
                topUpTokenDoWork.setCellNumber(topUpInfo.getPspInfo().getCellNumber().substring(1, topUpInfo.getPspInfo().getCellNumber().length()));
                topUpTokenDoWork.setLangAByte((byte) 0);
                topUpTokenDoWork.setLangABoolean(false);
                HHBArrayOfKeyValueOfstringstring vectorstring2stringMapEntry = new HHBArrayOfKeyValueOfstringstring();
                HHBArrayOfKeyValueOfstringstring_KeyValueOfstringstring s2sMapEntry = new HHBArrayOfKeyValueOfstringstring_KeyValueOfstringstring();

                s2sMapEntry = new HHBArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                s2sMapEntry.Key = "Pin2";
                s2sMapEntry.Value = userPinCode;
                vectorstring2stringMapEntry.add(s2sMapEntry);

                s2sMapEntry = new HHBArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                s2sMapEntry.Key = "Amount";
                s2sMapEntry.Value = String.valueOf(topUpInfo.getChargePackage().getAmount() + topUpInfo.getFeeCharge());
                vectorstring2stringMapEntry.add(s2sMapEntry);

                s2sMapEntry = new HHBArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                s2sMapEntry.Key = "OtherCellNumber";
                s2sMapEntry.Value = topUpInfo.getCellNumber().substring(1, topUpInfo.getCellNumber().length());
                vectorstring2stringMapEntry.add(s2sMapEntry);

                s2sMapEntry = new HHBArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                s2sMapEntry.Key = "ChargeType";
                s2sMapEntry.Value = topUpInfo.getChargeType();
                vectorstring2stringMapEntry.add(s2sMapEntry);


                s2sMapEntry = new HHBArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                s2sMapEntry.Key = "ThirdParty";
                s2sMapEntry.Value = topUpInfo.getProductCode();
                vectorstring2stringMapEntry.add(s2sMapEntry);


                s2sMapEntry = new HHBArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                s2sMapEntry.Key = "SenderTerminalId";
                s2sMapEntry.Value = topUpInfo.getPspInfo().getTerminalId();
                vectorstring2stringMapEntry.add(s2sMapEntry);

                s2sMapEntry = new HHBArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                s2sMapEntry.Key = "Email";
                s2sMapEntry.Value = "";
                vectorstring2stringMapEntry.add(s2sMapEntry);

                s2sMapEntry = new HHBArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                s2sMapEntry.Key = "IPAddress";
                s2sMapEntry.Value = topUpInfo.getPspInfo().getIpAddress();
                vectorstring2stringMapEntry.add(s2sMapEntry);

                s2sMapEntry = new HHBArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                s2sMapEntry.Key = "CVV2";
                s2sMapEntry.Value = userCVV2;
                vectorstring2stringMapEntry.add(s2sMapEntry);

                s2sMapEntry = new HHBArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                s2sMapEntry.Key = "ExpDate";
                s2sMapEntry.Value = topUpInfo.getCardList().get(selectedCardIdIndex).getExpireDate();
                vectorstring2stringMapEntry.add(s2sMapEntry);

                s2sMapEntry = new HHBArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                s2sMapEntry.Key = "CardId";
                s2sMapEntry.Value = topUpInfo.getCardList().get(selectedCardIdIndex).getCardId();
                vectorstring2stringMapEntry.add(s2sMapEntry);

                s2sMapEntry = new HHBArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                s2sMapEntry.Key = "ResNum";
                s2sMapEntry.Value = topUpInfo.getProductCode();
                vectorstring2stringMapEntry.add(s2sMapEntry);

                s2sMapEntry = new HHBArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                s2sMapEntry.Key = "MNPOperatorId";
                s2sMapEntry.Value = topUpInfo.getOperator().getId();
                vectorstring2stringMapEntry.add(s2sMapEntry);

                s2sMapEntry = new HHBArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                s2sMapEntry.Key = "Signature";
                s2sMapEntry.Value = signature;
                vectorstring2stringMapEntry.add(s2sMapEntry);

                topUpTokenDoWork.setVectorstring2stringMapEntry(vectorstring2stringMapEntry);
                requestTokenBills.execute(topUpTokenDoWork);

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
                pinText.setText("");
                userPinCode = "";
                break;

            case R.id.cvv_text:
                pinLayout.setBackgroundResource(R.drawable.card_info_empty_placeholder);
                cvvLayout.setBackgroundResource(R.drawable.card_info_entry_placeholder);
                pinCodeFocus = false;
                cvvFocus = true;
                cvvText.setText("");
                userCVV2 = "";
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
                        signToPayRequest.setFundType(FundType.TOP_UP);
                        new SignToPayTask(activity, ServiceTopUpDetailActivity.this, signToPayRequest, authToken).execute();
                    }
                }
                break;

            case ADD:
                Intent intent = new Intent();
                intent.setClass(activity, BankWebPaymentActivity.class);
                intent.putExtra(Constants.TOP_UP_INFO, topUpInfo);
                startActivityForResult(intent, 48);
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
        topUpDate.setText(persian.E2P(new JalaliConvert().GregorianToPersian(topUInfo.getCreatedBy())));
        if (fundDTO == null) {
            if (topUInfo.getImageId() != null) {
                imgOperator.setTag(topUInfo.getImageId());
                if (topUInfo.getImageId().equals("MCI")) {
                    imgOperator.setImageResource(R.mipmap.hamrah_active);
                } else if (topUInfo.getImageId().equals("MTN")) {
                    imgOperator.setImageResource(R.mipmap.irancell_active);
                } else if (topUInfo.getImageId().equals("RAYTEL")) {
                    imgOperator.setImageResource(R.mipmap.rightel_active);
                }
            } else {
                imgOperator.setImageResource(R.drawable.user_placeholder);
            }
        } else
            ImageHelper.getInstance(activity).imageLoader(fundDTO.getImageId(), imgOperator, R.drawable.user_placeholder);

        tvCellNumber.setText(persian.E2P(topUInfo.getCellNumber()));
        tvAmount.setText(persian.E2P(persian.E2P(currencyFormatter.format(topUInfo.getChargePackage().getAmount()))));
        tvTopUpTax.setText(persian.E2P(currencyFormatter.format(topUInfo.getVat())));
        hampayFee.setText(persian.E2P(currencyFormatter.format(topUInfo.getFeeCharge())));

        tvTopUpTotalAmount.setText(persian.E2P(currencyFormatter.format(topUInfo.getChargePackage().getAmount() + topUInfo.getFeeCharge())));

        if (topUInfo.getCardList().size() > 0) {
            if (topUInfo.getCardList().get(0).getCardId() != null && (topUInfo.getChargePackage().getAmount() + topUInfo.getFeeCharge() < Constants.SOAP_AMOUNT_MAX)) {
                cardNumberValue.setText(persian.E2P(topUInfo.getCardList().get(0).getLast4Digits()));
                bankName.setText(topUInfo.getCardList().get(0).getBankName());
            }
        }

        if (topUInfo.getCardList().size() > 0) {
            if (topUInfo.getCardList().get(0).getCardId() != null && (topUInfo.getChargePackage().getAmount() + topUInfo.getFeeCharge() < Constants.SOAP_AMOUNT_MAX)) {
                cardNumberValue.setText(persian.E2P(topUInfo.getCardList().get(0).getLast4Digits()));
                bankName.setText(topUInfo.getCardList().get(0).getBankName());
                selectedCardIdIndex = 0;
                selectCardText.setVisibility(View.GONE);
                cardSelect.setVisibility(View.VISIBLE);
                if (topUInfo.getCardList().get(0).getDigitalSignature() != null && topUInfo.getCardList().get(0).getDigitalSignature().length() > 0) {
                    signature = topUInfo.getCardList().get(0).getDigitalSignature();
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

        if (requestCode == 48) {
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

    public class RequestPurchaseTaskCompleteListener implements AsyncTaskCompleteListener<HHBArrayOfKeyValueOfstringstring> {

        @Override
        public void onTaskComplete(HHBArrayOfKeyValueOfstringstring purchaseResponseResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            String responseCode = null;
            String description = null;
            String SWTraceNum = null;
            ResultStatus resultStatus = ResultStatus.FAILURE;
            ServiceEvent serviceName = ServiceEvent.PSP_PAYMENT_FAILURE;
            LogEvent logEvent = new LogEvent(context);

            if (purchaseResponseResponseMessage != null) {
                pspResultRequest = new PSPResultRequest();
                for (HHBArrayOfKeyValueOfstringstring_KeyValueOfstringstring s2sMapEntry : purchaseResponseResponseMessage) {
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
                        if (topUpInfo != null) {
                            Intent intent = new Intent(context, PaymentCompletedActivity.class);
                            SucceedPayment succeedPayment = new SucceedPayment();
                            succeedPayment.setAmount(topUpInfo.getChargePackage().getAmount() + topUpInfo.getFeeCharge());
                            succeedPayment.setCode(topUpInfo.getCellNumber());
                            succeedPayment.setTrace(topUpInfo.getPspInfo().getProviderId());
                            succeedPayment.setPaymentType(PaymentType.TOP_UP);
                            intent.putExtra(Constants.SUCCEED_PAYMENT_INFO, succeedPayment);
                            startActivityForResult(intent, 48);
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
                    syncPspResult.setProductCode(topUpInfo.getProductCode());
                    syncPspResult.setType("TOP_UP");
                    syncPspResult.setSwTrace(SWTraceNum);
                    syncPspResult.setTimestamp(System.currentTimeMillis());
                    syncPspResult.setStatus(0);
                    syncPspResult.setPspName(PSPName.SAMAN.getCode());
                    syncPspResult.setCardId(topUpInfo.getCardList().get(selectedCardIdIndex).getCardId());
                    dbHelper.createSyncPspResult(syncPspResult);

                    pspResultRequest.setPspResponseCode(responseCode);
                    pspResultRequest.setProductCode(topUpInfo.getProductCode());
                    pspResultRequest.setTrackingCode(SWTraceNum);
                    pspResultRequest.setResultType(PSPResultRequest.ResultType.TOP_UP);
                    pspResultRequest.setCardDTO(topUpInfo.getCardList().get(selectedCardIdIndex));
                    pspResultRequest.setPspName(PSPName.SAMAN);
                    requestPSPResult = new RequestPSPResult(context, new RequestPSPResultTaskCompleteListener(topUpInfo.getProductCode()));
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
