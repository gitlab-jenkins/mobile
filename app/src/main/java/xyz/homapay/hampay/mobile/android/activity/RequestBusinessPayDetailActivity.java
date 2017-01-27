package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import xyz.homapay.hampay.common.core.model.request.LatestPurchaseRequest;
import xyz.homapay.hampay.common.core.model.request.PSPResultRequest;
import xyz.homapay.hampay.common.core.model.request.PurchaseDetailRequest;
import xyz.homapay.hampay.common.core.model.request.PurchaseInfoRequest;
import xyz.homapay.hampay.common.core.model.request.SignToPayRequest;
import xyz.homapay.hampay.common.core.model.response.LatestPurchaseResponse;
import xyz.homapay.hampay.common.core.model.response.PSPResultResponse;
import xyz.homapay.hampay.common.core.model.response.PurchaseDetailResponse;
import xyz.homapay.hampay.common.core.model.response.PurchaseInfoResponse;
import xyz.homapay.hampay.common.core.model.response.SignToPayResponse;
import xyz.homapay.hampay.common.core.model.response.dto.PspInfoDTO;
import xyz.homapay.hampay.common.core.model.response.dto.PurchaseInfoDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.Helper.DatabaseHelper;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.animation.Collapse;
import xyz.homapay.hampay.mobile.android.animation.Expand;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestLatestPurchase;
import xyz.homapay.hampay.mobile.android.async.RequestPSPResult;
import xyz.homapay.hampay.mobile.android.async.RequestPurchase;
import xyz.homapay.hampay.mobile.android.async.RequestPurchaseDetail;
import xyz.homapay.hampay.mobile.android.async.RequestPurchaseInfo;
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
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;
import xyz.homapay.hampay.mobile.android.util.PspCode;
import xyz.homapay.hampay.mobile.android.webservice.psp.CBUArrayOfKeyValueOfstringstring;
import xyz.homapay.hampay.mobile.android.webservice.psp.CBUArrayOfKeyValueOfstringstring_KeyValueOfstringstring;

public class RequestBusinessPayDetailActivity extends AppCompatActivity implements View.OnClickListener, CardNumberDialog.SelectCardDialogListener, OnTaskCompleted {

    @BindView(R.id.pay_to_business_button)
    ImageView pay_to_business_button;
    @BindView(R.id.business_name)
    FacedTextView business_name;
    @BindView(R.id.business_image)
    ImageView business_image;
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
    @BindView(R.id.purchase_status_layout)
    LinearLayout purchase_status_layout;
    @BindView(R.id.purchase_payer_name_layout)
    LinearLayout purchase_payer_name_layout;
    @BindView(R.id.purchase_payer_cell_layout)
    LinearLayout purchase_payer_cell_layout;
    @BindView(R.id.purchase_status)
    FacedTextView purchase_status;
    @BindView(R.id.purchase_payer_name)
    FacedTextView purchase_payer_name;
    @BindView(R.id.purchase_payer_cell)
    FacedTextView purchase_payer_cell;
    @BindView(R.id.paymentPriceValue)
    FacedTextView paymentPriceValue;
    @BindView(R.id.paymentVAT)
    FacedTextView paymentVAT;
    @BindView(R.id.paymentFeeValue)
    FacedTextView paymentFeeValue;
    @BindView(R.id.paymentTotalValue)
    FacedTextView paymentTotalValue;
    @BindView(R.id.cardNumberValue)
    FacedTextView cardNumberValue;
    @BindView(R.id.bankName)
    FacedTextView bankName;
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
    private Context context;
    private Activity activity;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private HamPayDialog hamPayDialog;
    private RequestLatestPurchase requestLatestPurchase;
    private LatestPurchaseRequest latestPurchaseRequest;
    private PurchaseInfoDTO purchaseInfo = null;
    private PspInfoDTO pspInfoDTO = null;
    private String purchaseCode = null;
    private String providerId = null;
    private RequestPSPResult requestPSPResult;
    private PSPResultRequest pspResultRequest;
    private RequestPurchase requestPurchase;
    private RequestPurchaseInfo requestPurchaseInfo;
    private PurchaseInfoRequest purchaseInfoRequest;
    private CurrencyFormatter currencyFormatter;
    private boolean pinCodeFocus = false;
    private boolean cvvFocus = false;
    private String userPinCode = "";
    private String userCVV2 = "";
    private int selectedCardIdIndex = -1;
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
        PugNotification.with(context).cancel(Constants.MERCHANT_NOTIFICATION_IDENTIFIER);
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
        PugNotification.with(context).cancel(Constants.MERCHANT_NOTIFICATION_IDENTIFIER);
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
        setContentView(R.layout.activity_requets_business_pay_detail);
        ButterKnife.bind(this);
        context = this;
        activity = RequestBusinessPayDetailActivity.this;
        PugNotification.with(context).cancel(Constants.MERCHANT_NOTIFICATION_IDENTIFIER);
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

        pinText.setOnClickListener(this);
        cvvText.setOnClickListener(this);

        hamPayDialog = new HamPayDialog(activity);

        currencyFormatter = new CurrencyFormatter();

        cardPlaceHolder.setOnClickListener(v -> {
            CardNumberDialog cardNumberDialog = new CardNumberDialog();
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.CARD_LIST, (Serializable) purchaseInfo.getCardList());
            cardNumberDialog.setArguments(bundle);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(cardNumberDialog, null);
            fragmentTransaction.commitAllowingStateLoss();
        });

        Intent intent = getIntent();

        purchaseInfo = (PurchaseInfoDTO) intent.getSerializableExtra(Constants.PURCHASE_INFO);
        pspInfoDTO = (PspInfoDTO) intent.getSerializableExtra(Constants.PSP_INFO);
        purchaseCode = intent.getStringExtra(Constants.BUSINESS_PURCHASE_CODE);
        providerId = intent.getStringExtra(Constants.PROVIDER_ID);


        if (purchaseInfo != null) {
            fillPurchase(purchaseInfo);
        } else if (purchaseCode != null) {
            AppManager.setMobileTimeout(context);
            editor.commit();
            requestPurchaseInfo = new RequestPurchaseInfo(activity, new RequestPurchaseInfoTaskCompleteListener());
            purchaseInfoRequest = new PurchaseInfoRequest();
            purchaseInfoRequest.setPurchaseCode(purchaseCode);
            requestPurchaseInfo.execute(purchaseInfoRequest);
        } else if (providerId != null) {
            PurchaseDetailRequest purchaseDetailRequest = new PurchaseDetailRequest();
            purchaseDetailRequest.setProviderId(providerId);
            RequestPurchaseDetail requestPurchaseDetail = new RequestPurchaseDetail(activity, new RequestPurchaseDetailTaskCompleteListener());
            requestPurchaseDetail.execute(purchaseDetailRequest);

        } else {
            AppManager.setMobileTimeout(context);
            editor.commit();
            requestLatestPurchase = new RequestLatestPurchase(activity, new RequestLatestPurchaseTaskCompleteListener());
            latestPurchaseRequest = new LatestPurchaseRequest();
            requestLatestPurchase.execute(latestPurchaseRequest);
        }

        pay_to_business_button.setOnClickListener(v -> {

            if (pspInfoDTO == null) return;

            if (cvvText.getText().toString().trim().length() < 3) {
                Toast.makeText(context, R.string.err_cvv_lenght, Toast.LENGTH_SHORT).show();
                return;
            }

            if (pinText.getText().toString().trim().length() < 5) {
                Toast.makeText(context, R.string.err_pin_lenght, Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedCardIdIndex == -1 || (purchaseInfo.getCardList().get(selectedCardIdIndex) != null && purchaseInfo.getCardList().get(selectedCardIdIndex).getCardId() == null) || (purchaseInfo.getAmount() + purchaseInfo.getFeeCharge() + purchaseInfo.getVat() >= Constants.SOAP_AMOUNT_MAX)) {
                Intent intent1 = new Intent();
                intent1.setClass(activity, BankWebPaymentActivity.class);
                intent1.putExtra(Constants.PURCHASE_INFO, purchaseInfo);
                intent1.putExtra(Constants.PSP_INFO, pspInfoDTO);
                startActivityForResult(intent1, 45);
            } else {
                pay_to_business_button.setEnabled(false);
                if (pinText.getText().toString().length() <= 4) {
                    Toast.makeText(context, getString(R.string.msg_pin2_incorrect), Toast.LENGTH_LONG).show();
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
                requestPurchase = new RequestPurchase(activity, new RequestPurchaseTaskCompleteListener(), purchaseInfo.getPspInfo().getPayURL());

                DoWorkInfo doWorkInfo = new DoWorkInfo();
                doWorkInfo.setUserName("appstore");
                doWorkInfo.setPassword("sepapp");
                doWorkInfo.setCellNumber(pspInfoDTO.getCellNumber().substring(1, pspInfoDTO.getCellNumber().length()));
                doWorkInfo.setLangAByte((byte) 0);
                doWorkInfo.setLangABoolean(false);
                CBUArrayOfKeyValueOfstringstring vectorstring2stringMapEntry = new CBUArrayOfKeyValueOfstringstring();
                CBUArrayOfKeyValueOfstringstring_KeyValueOfstringstring s2sMapEntry = new CBUArrayOfKeyValueOfstringstring_KeyValueOfstringstring();

                s2sMapEntry.Key = "Amount";
                s2sMapEntry.Value = String.valueOf(purchaseInfo.getAmount() + purchaseInfo.getFeeCharge() + purchaseInfo.getVat());
                vectorstring2stringMapEntry.add(s2sMapEntry);

                s2sMapEntry = new CBUArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                s2sMapEntry.Key = "Pin2";
                s2sMapEntry.Value = userPinCode;
                vectorstring2stringMapEntry.add(s2sMapEntry);

                s2sMapEntry = new CBUArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                s2sMapEntry.Key = "ThirdParty";
                s2sMapEntry.Value = purchaseInfo.getProductCode();
                vectorstring2stringMapEntry.add(s2sMapEntry);

                s2sMapEntry = new CBUArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                s2sMapEntry.Key = "TerminalId";
                s2sMapEntry.Value = pspInfoDTO.getTerminalId();
                vectorstring2stringMapEntry.add(s2sMapEntry);

                s2sMapEntry = new CBUArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                s2sMapEntry.Key = "CardId";
                s2sMapEntry.Value = purchaseInfo.getCardList().get(selectedCardIdIndex).getCardId();
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
                s2sMapEntry.Value = purchaseInfo.getCardList().get(selectedCardIdIndex).getExpireDate();
                vectorstring2stringMapEntry.add(s2sMapEntry);

                s2sMapEntry = new CBUArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                s2sMapEntry.Key = "ResNum";
                s2sMapEntry.Value = purchaseInfo.getProductCode();
                vectorstring2stringMapEntry.add(s2sMapEntry);

                s2sMapEntry = new CBUArrayOfKeyValueOfstringstring_KeyValueOfstringstring();
                s2sMapEntry.Key = "Signature";
                s2sMapEntry.Value = signature;
                vectorstring2stringMapEntry.add(s2sMapEntry);

                doWorkInfo.setVectorstring2stringMapEntry(vectorstring2stringMapEntry);
                requestPurchase.execute(doWorkInfo);

            }
        });
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        Log.e("EXIT", "onUserLeaveHint");
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
                if (purchaseInfo != null) {
                    selectedCardIdIndex = position;
                    cardNumberValue.setText(persian.E2P(purchaseInfo.getCardList().get(position).getLast4Digits()));
                    bankName.setText(purchaseInfo.getCardList().get(position).getBankName());
                    selectCardText.setVisibility(View.GONE);
                    cardSelect.setVisibility(View.VISIBLE);
                    if (purchaseInfo.getCardList().get(position).getDigitalSignature() != null && purchaseInfo.getCardList().get(position).getDigitalSignature().length() > 0) {
                        signature = purchaseInfo.getCardList().get(position).getDigitalSignature();
                    } else {
                        SignToPayRequest signToPayRequest = new SignToPayRequest();
                        signToPayRequest.setCardId(purchaseInfo.getCardList().get(position).getCardId());
                        signToPayRequest.setProductCode(purchaseInfo.getProductCode());
                        signToPayRequest.setFundType(FundType.PURCHASE);
                        new SignToPayTask(activity, RequestBusinessPayDetailActivity.this, signToPayRequest, authToken).execute();
                    }
                }
                break;

            case ADD:
                Intent intent = new Intent();
                intent.setClass(activity, BankWebPaymentActivity.class);
                intent.putExtra(Constants.PURCHASE_INFO, purchaseInfo);
                intent.putExtra(Constants.PSP_INFO, pspInfoDTO);
                startActivityForResult(intent, 45);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 45) {
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

    private void fillPurchase(PurchaseInfoDTO purchaseInfo) {

        pspInfoDTO = purchaseInfo.getPspInfo();

        input_digit_1.setText(String.valueOf(purchaseInfo.getPurchaseCode().charAt(0)));
        input_digit_2.setText(String.valueOf(purchaseInfo.getPurchaseCode().charAt(1)));
        input_digit_3.setText(String.valueOf(purchaseInfo.getPurchaseCode().charAt(2)));
        input_digit_4.setText(String.valueOf(purchaseInfo.getPurchaseCode().charAt(3)));
        input_digit_5.setText(String.valueOf(purchaseInfo.getPurchaseCode().charAt(4)));
        input_digit_6.setText(String.valueOf(purchaseInfo.getPurchaseCode().charAt(5)));

        switch (purchaseInfo.getStatus()) {
            case SUCCESSFUL:
                purchase_status.setText(getString(R.string.purchase_status_succeed));
                if (purchaseInfo.getPayerName() != null) {
                    purchase_payer_name.setText(purchaseInfo.getPayerName());
                    purchase_payer_cell.setText(persian.E2P(purchaseInfo.getPayerCellNumber()));
                }
                purchase_status_layout.setVisibility(View.VISIBLE);
                purchase_payer_name_layout.setVisibility(View.VISIBLE);
                purchase_payer_cell_layout.setVisibility(View.VISIBLE);
                pay_to_business_button.setVisibility(View.GONE);
                break;
            case FAILED:
                purchase_status.setText(getString(R.string.purchase_status_failed));
                if (purchaseInfo.getPayerName() != null) {
                    purchase_payer_name.setText(purchaseInfo.getPayerName());
                    purchase_payer_cell.setText(persian.E2P(purchaseInfo.getPayerCellNumber()));
                }
                purchase_status_layout.setVisibility(View.VISIBLE);
                purchase_payer_name_layout.setVisibility(View.GONE);
                purchase_payer_cell_layout.setVisibility(View.GONE);
                pay_to_business_button.setVisibility(View.GONE);
                break;
            case PROCESSING:
                purchase_status.setText(getString(R.string.purchase_status_processing));
                if (purchaseInfo.getPayerName() != null) {
                    purchase_payer_name.setText(purchaseInfo.getPayerName());
                    purchase_payer_cell.setText(persian.E2P(purchaseInfo.getPayerCellNumber()));
                }
                purchase_status_layout.setVisibility(View.VISIBLE);
                purchase_payer_name_layout.setVisibility(View.GONE);
                purchase_payer_cell_layout.setVisibility(View.GONE);
                pay_to_business_button.setVisibility(View.GONE);
                break;
            case PENDING:
                purchase_status.setText(getString(R.string.purchase_status_pending));
                if (purchaseInfo.getPayerName() != null) {
                    purchase_payer_name.setText(purchaseInfo.getPayerName());
                    purchase_payer_cell.setText(persian.E2P(purchaseInfo.getPayerCellNumber()));
                }
                purchase_status_layout.setVisibility(View.GONE);
                purchase_payer_name_layout.setVisibility(View.GONE);
                purchase_payer_cell_layout.setVisibility(View.GONE);
                pay_to_business_button.setVisibility(View.VISIBLE);
                if (purchaseInfo.getCardList().size() > 0) {
                    if (purchaseInfo.getCardList().get(0).getCardId() != null && (this.purchaseInfo.getAmount() + this.purchaseInfo.getFeeCharge() + this.purchaseInfo.getVat() < Constants.SOAP_AMOUNT_MAX)) {
                        cardNumberValue.setText(persian.E2P(purchaseInfo.getCardList().get(0).getLast4Digits()));
                        bankName.setText(purchaseInfo.getCardList().get(0).getBankName());
                        selectedCardIdIndex = 0;
                        selectCardText.setVisibility(View.GONE);
                        cardSelect.setVisibility(View.VISIBLE);
                        if (this.purchaseInfo.getCardList().get(0).getDigitalSignature() != null && this.purchaseInfo.getCardList().get(0).getDigitalSignature().length() > 0) {
                            signature = this.purchaseInfo.getCardList().get(0).getDigitalSignature();
                        }
                    }
                }
                break;
        }

        paymentPriceValue.setText(persian.E2P(currencyFormatter.format(purchaseInfo.getAmount())));
        paymentVAT.setText(persian.E2P(currencyFormatter.format(purchaseInfo.getVat())));
        paymentFeeValue.setText(persian.E2P(currencyFormatter.format(purchaseInfo.getFeeCharge())));
        paymentTotalValue.setText(persian.E2P(currencyFormatter.format(purchaseInfo.getAmount() + purchaseInfo.getFeeCharge() + purchaseInfo.getVat())));
        business_name.setText(persian.E2P(purchaseInfo.getMerchantName()));

        if (purchaseInfo.getMerchantImageId() != null) {
            AppManager.setMobileTimeout(context);
            editor.commit();
            business_image.setTag(purchaseInfo.getMerchantImageId());
            ImageHelper.getInstance(context).imageLoader(purchaseInfo.getMerchantImageId(), business_image, R.drawable.user_placeholder);
        } else {
            business_image.setImageResource(R.drawable.user_placeholder);
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
            ServiceEvent serviceName = ServiceEvent.PSP_PAYMENT_FAILURE;
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
                        serviceName = ServiceEvent.PSP_PAYMENT_SUCCESS;
                        logEvent.log(serviceName);
                        if (purchaseInfo != null) {
                            Intent intent = new Intent(context, PaymentCompletedActivity.class);
                            SucceedPayment succeedPayment = new SucceedPayment();
                            succeedPayment.setAmount(purchaseInfo.getAmount() + purchaseInfo.getVat() + purchaseInfo.getFeeCharge());
                            succeedPayment.setCode(purchaseInfo.getPurchaseCode());
                            succeedPayment.setTrace(pspInfoDTO.getProviderId());
                            succeedPayment.setPaymentType(PaymentType.PURCHASE);
                            intent.putExtra(Constants.SUCCEED_PAYMENT_INFO, succeedPayment);
                            startActivityForResult(intent, 45);
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
                    syncPspResult.setProductCode(purchaseInfo.getProductCode());
                    syncPspResult.setType("PURCHASE");
                    syncPspResult.setSwTrace(SWTraceNum);
                    syncPspResult.setTimestamp(System.currentTimeMillis());
                    syncPspResult.setStatus(0);
                    syncPspResult.setPspName(PSPName.SAMAN.getCode());
                    syncPspResult.setCardId(purchaseInfo.getCardList().get(selectedCardIdIndex).getCardId());
                    dbHelper.createSyncPspResult(syncPspResult);

                    pspResultRequest.setPspResponseCode(responseCode);
                    pspResultRequest.setProductCode(purchaseInfo.getProductCode());
                    pspResultRequest.setTrackingCode(SWTraceNum);
                    pspResultRequest.setResultType(PSPResultRequest.ResultType.PURCHASE);
                    pspResultRequest.setCardDTO(purchaseInfo.getCardList().get(selectedCardIdIndex));
                    pspResultRequest.setPspName(PSPName.SAMAN);
                    requestPSPResult = new RequestPSPResult(context, new RequestPSPResultTaskCompleteListener(purchaseInfo.getProductCode()));
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
            pay_to_business_button.setEnabled(true);
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }

    public class RequestPSPResultTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<PSPResultResponse>> {

        private String productCode;

        public RequestPSPResultTaskCompleteListener(String productCode) {
            this.productCode = productCode;
        }


        @Override
        public void onTaskComplete(ResponseMessage<PSPResultResponse> pspResultResponseMessage) {

            hamPayDialog.dismisWaitingDialog();
            ServiceEvent serviceName;
            LogEvent logEvent = new LogEvent(context);

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

            pay_to_business_button.setEnabled(true);
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }

    public class RequestLatestPurchaseTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<LatestPurchaseResponse>> {

        @Override
        public void onTaskComplete(ResponseMessage<LatestPurchaseResponse> latestPurchaseResponseMessage) {

            hamPayDialog.dismisWaitingDialog();
            ServiceEvent serviceName;
            LogEvent logEvent = new LogEvent(context);

            if (latestPurchaseResponseMessage != null) {
                if (latestPurchaseResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    serviceName = ServiceEvent.GET_LATEST_PURCHASE_SUCCESS;
                    purchaseInfo = latestPurchaseResponseMessage.getService().getPurchaseInfo();

                    dbHelper.createViewedPurchaseRequest(purchaseInfo.getProductCode());

                    if (purchaseInfo == null) {
                        new HamPayDialog(activity).showFailPendingPurchaseDialog(requestLatestPurchase, latestPurchaseRequest,
                                Constants.LOCAL_ERROR_CODE,
                                getString(R.string.msg_pending_not_found));
                        return;
                    }

                    pspInfoDTO = latestPurchaseResponseMessage.getService().getPurchaseInfo().getPspInfo();

                    if (purchaseInfo != null) {

                        fillPurchase(purchaseInfo);

                    } else {
                        Toast.makeText(context, getString(R.string.msg_not_found_pending_payment_code), Toast.LENGTH_LONG).show();
                        finish();
                    }

                } else if (latestPurchaseResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    serviceName = ServiceEvent.GET_LATEST_PURCHASE_FAILURE;
                    forceLogout();
                } else {
                    serviceName = ServiceEvent.GET_LATEST_PURCHASE_FAILURE;
                    requestLatestPurchase = new RequestLatestPurchase(context, new RequestLatestPurchaseTaskCompleteListener());
                    new HamPayDialog(activity).showFailPendingPurchaseDialog(requestLatestPurchase, latestPurchaseRequest,
                            latestPurchaseResponseMessage.getService().getResultStatus().getCode(), "");
                }
            } else {
                serviceName = ServiceEvent.GET_LATEST_PURCHASE_FAILURE;
                requestLatestPurchase = new RequestLatestPurchase(context, new RequestLatestPurchaseTaskCompleteListener());
                new HamPayDialog(activity).showFailPendingPurchaseDialog(requestLatestPurchase, latestPurchaseRequest,
                        Constants.LOCAL_ERROR_CODE,
                        getString(R.string.msg_fail_fetch_latest_payment));
            }
            logEvent.log(serviceName);
            pay_to_business_button.setEnabled(true);

        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
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
                    purchaseInfo = purchaseInfoResponseMessage.getService().getPurchaseInfo();
                    pspInfoDTO = purchaseInfoResponseMessage.getService().getPurchaseInfo().getPspInfo();

                    if (purchaseInfo != null) {
                        fillPurchase(purchaseInfo);
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
            pay_to_business_button.setEnabled(true);

        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }

    public class RequestPurchaseDetailTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<PurchaseDetailResponse>> {

        ServiceEvent serviceName;
        LogEvent logEvent = new LogEvent(context);

        @Override
        public void onTaskComplete(ResponseMessage<PurchaseDetailResponse> purchaseDetailResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            if (purchaseDetailResponseMessage != null) {

                if (purchaseDetailResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    serviceName = ServiceEvent.PURCHASE_DETAIL_SUCCESS;
                    purchaseInfo = purchaseDetailResponseMessage.getService().getpurchaseInfo();
                    fillPurchase(purchaseInfo);
                } else if (purchaseDetailResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    serviceName = ServiceEvent.PURCHASE_DETAIL_FAILURE;
                    forceLogout();
                } else {
                    serviceName = ServiceEvent.PURCHASE_DETAIL_FAILURE;
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
