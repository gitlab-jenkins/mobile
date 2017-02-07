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

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.homapay.hampay.common.common.PSPName;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.enums.FundType;
import xyz.homapay.hampay.common.core.model.request.PSPResultRequest;
import xyz.homapay.hampay.common.core.model.request.SignToPayRequest;
import xyz.homapay.hampay.common.core.model.response.PSPResultResponse;
import xyz.homapay.hampay.common.core.model.response.SignToPayResponse;
import xyz.homapay.hampay.common.core.model.response.dto.PaymentInfoDTO;
import xyz.homapay.hampay.common.core.model.response.dto.PspInfoDTO;
import xyz.homapay.hampay.common.pspproxy.model.request.NetPayRequest;
import xyz.homapay.hampay.common.pspproxy.model.response.NetPayResponse;
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
import xyz.homapay.hampay.mobile.android.p.netpay.NetPay;
import xyz.homapay.hampay.mobile.android.p.netpay.NetPayImpl;
import xyz.homapay.hampay.mobile.android.p.netpay.NetPayView;
import xyz.homapay.hampay.mobile.android.util.AppManager;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.CurrencyFormatter;
import xyz.homapay.hampay.mobile.android.util.ModelLayerImpl;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

public class BusinessPaymentConfirmActivity extends AppCompatActivity implements NetPayView, View.OnClickListener, CardNumberDialog.SelectCardDialogListener, OnTaskCompleted {

    @BindView(R.id.business_name)
    FacedTextView business_name;
    @BindView(R.id.business_image)
    ImageView business_image;
    @BindView(R.id.paymentScroll)
    ScrollView paymentScroll;
    @BindView(R.id.paymentPriceValue)
    FacedTextView paymentPriceValue;
    @BindView(R.id.paymentFeeValue)
    FacedTextView paymentFeeValue;
    @BindView(R.id.paymentTotalValue)
    FacedTextView paymentTotalValue;
    @BindView(R.id.cardNumberValue)
    FacedTextView cardNumberValue;
    @BindView(R.id.bankName)
    FacedTextView bankName;
    @BindView(R.id.pin_layout)
    RelativeLayout pinLayout;
    @BindView(R.id.cardPlaceHolder)
    RelativeLayout cardPlaceHolder;
    @BindView(R.id.selectCardText)
    FacedTextView selectCardText;
    @BindView(R.id.cardSelect)
    LinearLayout cardSelect;
    @BindView(R.id.pay_to_business_button)
    ImageView pay_to_business_button;
    @BindView(R.id.keyboard)
    LinearLayout keyboard;
    @BindView(R.id.pin_text)
    FacedTextView pinText;
    @BindView(R.id.cvv_layout)
    RelativeLayout cvvLayout;
    @BindView(R.id.cvv_text)
    FacedTextView cvvText;
    private DatabaseHelper dbHelper;
    private Context context;
    private Activity activity;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private CurrencyFormatter formatter;
    private HamPayDialog hamPayDialog;
    private PaymentInfoDTO paymentInfo = null;
    private PspInfoDTO pspInfoDTO = null;
    private RequestPurchase requestPurchase;
    private DoWorkInfo doWorkInfo;
    private RequestPSPResult requestPSPResult;
    private PSPResultRequest pspResultRequest;
    private boolean pinCodeFocus = false;
    private boolean cvvFocus = false;
    private String userPinCode = "";
    private String userCVV2 = "";
    private int selectedCardIdIndex = -1;
    private PersianEnglishDigit persian = new PersianEnglishDigit();
    private String signature;
    private String authToken = "";
    private NetPay netPay;
    private NetPayRequest netPayRequest;

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
        ButterKnife.bind(this);

        context = this;
        activity = BusinessPaymentConfirmActivity.this;

        netPay = new NetPayImpl(new ModelLayerImpl(activity), this);
        netPayRequest = new NetPayRequest();

        dbHelper = new DatabaseHelper(context);
        formatter = new CurrencyFormatter();
        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
        authToken = prefs.getString(Constants.LOGIN_TOKEN_ID, "");
        hamPayDialog = new HamPayDialog(activity);

        Intent intent = getIntent();

        paymentInfo = (PaymentInfoDTO) intent.getSerializableExtra(Constants.PAYMENT_INFO);
        pspInfoDTO = (PspInfoDTO) intent.getSerializableExtra(Constants.PSP_INFO);


        if (paymentInfo != null) {
            PersianEnglishDigit persianEnglishDigit = new PersianEnglishDigit();

            business_name.setText(paymentInfo.getCallerName());
            paymentPriceValue.setText(persianEnglishDigit.E2P(String.valueOf(formatter.format(paymentInfo.getAmount()))));
            paymentFeeValue.setText(persianEnglishDigit.E2P(String.valueOf(formatter.format(paymentInfo.getFeeCharge()))));
            paymentTotalValue.setText(persianEnglishDigit.E2P(String.valueOf(formatter.format(paymentInfo.getAmount() + paymentInfo.getFeeCharge()))));

            if (paymentInfo.getImageId() != null) {
                AppManager.setMobileTimeout(context);
                editor.commit();
                business_image.setTag(paymentInfo.getImageId());
                ImageHelper.getInstance(activity).imageLoader(paymentInfo.getImageId(), business_image, R.drawable.user_placeholder);
            } else {
                business_image.setImageResource(R.drawable.user_placeholder);
            }
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
            case R.id.cardPlaceHolder:
                CardNumberDialog cardNumberDialog = new CardNumberDialog();
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.CARD_LIST, (Serializable) paymentInfo.getCardList());
                cardNumberDialog.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.add(cardNumberDialog, null);
                fragmentTransaction.commitAllowingStateLoss();
                break;
            case R.id.pay_to_business_button:
                if (pspInfoDTO == null) return;

                if (cvvText.getText().toString().trim().length() < 3) {
                    Toast.makeText(context, R.string.err_cvv_lenght, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (pinText.getText().toString().trim().length() < 5) {
                    Toast.makeText(context, R.string.err_pin_lenght, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (selectedCardIdIndex == -1 || (paymentInfo.getCardList().get(selectedCardIdIndex) != null && paymentInfo.getCardList().get(selectedCardIdIndex).getCardId() == null) || (paymentInfo.getAmount() + paymentInfo.getFeeCharge() >= Constants.SOAP_AMOUNT_MAX)) {
                    Intent intent = new Intent();
                    intent.setClass(activity, BankWebPaymentActivity.class);
                    intent.putExtra(Constants.PAYMENT_INFO, paymentInfo);
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

                    AppManager.setMobileTimeout(context);
                    editor.commit();

                    netPayRequest.setCardId(paymentInfo.getCardList().get(selectedCardIdIndex).getCardId());
                    netPayRequest.setCvv2(userCVV2);
                    netPayRequest.setExpirationDate(paymentInfo.getCardList().get(selectedCardIdIndex).getExpireDate());
                    netPayRequest.setAmount(paymentInfo.getAmount() + paymentInfo.getFeeCharge());
                    netPayRequest.setCellNumber(pspInfoDTO.getCellNumber());
                    netPayRequest.setDigitalSignature(signature);
                    netPayRequest.setIpAddress(pspInfoDTO.getIpAddress());
                    netPayRequest.setPin2(userPinCode);
                    netPayRequest.setProductCode(paymentInfo.getProductCode());
                    netPayRequest.setSenderTerminalId(pspInfoDTO.getSenderTerminalId());
                    netPayRequest.setTerminalId(pspInfoDTO.getTerminalId());
                    netPay.netPay(netPayRequest, AppManager.getAuthToken(context), paymentInfo.getPspInfo().getPspEncKey(), paymentInfo.getPspInfo().getIvKey());
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
                if (paymentInfo != null) {
                    selectedCardIdIndex = position;
                    cardNumberValue.setText(persian.E2P(paymentInfo.getCardList().get(position).getLast4Digits()));
                    bankName.setText(paymentInfo.getCardList().get(position).getBankName());
                    selectCardText.setVisibility(View.GONE);
                    cardSelect.setVisibility(View.VISIBLE);
                    if (paymentInfo.getCardList().get(position).getDigitalSignature() != null && paymentInfo.getCardList().get(position).getDigitalSignature().length() > 0) {
                        signature = paymentInfo.getCardList().get(position).getDigitalSignature();
                    } else {
                        SignToPayRequest signToPayRequest = new SignToPayRequest();
                        signToPayRequest.setCardId(paymentInfo.getCardList().get(position).getCardId());
                        signToPayRequest.setProductCode(paymentInfo.getProductCode());
                        signToPayRequest.setFundType(FundType.PAYMENT);
                        new SignToPayTask(activity, BusinessPaymentConfirmActivity.this, signToPayRequest, authToken).execute();
                    }
                }
                break;

            case ADD:
                Intent intent = new Intent();
                intent.setClass(activity, BankWebPaymentActivity.class);
                intent.putExtra(Constants.PAYMENT_INFO, paymentInfo);
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

    @Override
    public void showProgress() {
        hamPayDialog.showWaitingDialog("");
    }

    @Override
    public void cancelProgress() {
        hamPayDialog.dismisWaitingDialog();
    }

    @Override
    public void onError() {
        hamPayDialog.dismisWaitingDialog();
    }

    @Override
    public void onNetPayResponse(boolean state, ResponseMessage<NetPayResponse> data, String message) {

        String pspResponseCode = null;
        String description;
        String pspTrackingCode = null;
        ResultStatus resultStatus = ResultStatus.FAILURE;
        ServiceEvent serviceName = ServiceEvent.PSP_PAYMENT_FAILURE;
        LogEvent logEvent = new LogEvent(context);

        if (data != null) {
            pspResponseCode = data.getService().getPspResponseCode();
            pspTrackingCode = data.getService().getPspTrackingCode();
            description = data.getService().getResultStatus().getDescription();
            switch (data.getService().getResultStatus()) {
                case SUCCESS:
                    pspResultRequest = new PSPResultRequest();
                    serviceName = ServiceEvent.PSP_PAYMENT_SUCCESS;
                    if (paymentInfo != null) {
                        Intent intent = new Intent(context, PaymentCompletedActivity.class);
                        SucceedPayment succeedPayment = new SucceedPayment();
                        succeedPayment.setAmount(paymentInfo.getAmount() + paymentInfo.getFeeCharge());
                        succeedPayment.setCode(paymentInfo.getProductCode());
                        succeedPayment.setTrace(pspInfoDTO.getProviderId());
                        succeedPayment.setPaymentType(PaymentType.PAYMENT);
                        intent.putExtra(Constants.SUCCEED_PAYMENT_INFO, succeedPayment);
                        startActivityForResult(intent, 46);
                    }
                    resultStatus = ResultStatus.SUCCESS;
                    break;
                default:
                    new HamPayDialog(activity).pspFailResultDialog(pspResponseCode, description);
                    break;
            }
        }else {
            new HamPayDialog(activity).pspFailResultDialog(Constants.LOCAL_ERROR_CODE, getString(R.string.msg_soap_timeout));
        }

        logEvent.log(serviceName);
        SyncPspResult syncPspResult = new SyncPspResult();
        syncPspResult.setResponseCode(pspResponseCode);
        syncPspResult.setProductCode(paymentInfo.getProductCode());
        syncPspResult.setType("PAYMENT");
        syncPspResult.setSwTrace(pspTrackingCode);
        syncPspResult.setTimestamp(System.currentTimeMillis());
        syncPspResult.setStatus(0);
        syncPspResult.setPspName(PSPName.SAMAN.getCode());
        syncPspResult.setCardId(paymentInfo.getCardList().get(selectedCardIdIndex).getCardId());
        dbHelper.createSyncPspResult(syncPspResult);

        pspResultRequest.setPspResponseCode(pspResponseCode);
        pspResultRequest.setProductCode(paymentInfo.getProductCode());
        pspResultRequest.setTrackingCode(pspTrackingCode);
        pspResultRequest.setResultType(PSPResultRequest.ResultType.PAYMENT);
        pspResultRequest.setCardDTO(paymentInfo.getCardList().get(selectedCardIdIndex));
        pspResultRequest.setPspName(PSPName.SAMAN);
        requestPSPResult = new RequestPSPResult(context, new RequestPSPResultTaskCompleteListener(paymentInfo.getProductCode()));
        requestPSPResult.execute(pspResultRequest);


        AppManager.setMobileTimeout(context);
        editor.commit();
        Intent returnIntent = new Intent();
        returnIntent.putExtra(Constants.ACTIVITY_RESULT, resultStatus.ordinal());
        setResult(Activity.RESULT_OK, returnIntent);
    }

    @Override
    public void keyExchangeProblem() {

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
