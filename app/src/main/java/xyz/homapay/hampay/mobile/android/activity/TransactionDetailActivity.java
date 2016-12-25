package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.TransactionDetailRequest;
import xyz.homapay.hampay.common.core.model.response.TransactionDetailResponse;
import xyz.homapay.hampay.common.core.model.response.dto.TnxDetailDTO;
import xyz.homapay.hampay.common.core.model.response.dto.TransactionDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestTransactionDetail;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.firebase.LogEvent;
import xyz.homapay.hampay.mobile.android.firebase.service.ServiceEvent;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.CurrencyFormatter;
import xyz.homapay.hampay.mobile.android.util.ImageManager;
import xyz.homapay.hampay.mobile.android.util.JalaliConvert;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

public class TransactionDetailActivity extends AppCompatActivity {

    Bundle bundle;
    TransactionDTO transactionDTO;
    PersianEnglishDigit persianEnglishDigit;
    Context context;
    Activity activity;
    HamPayDialog hamPayDialog;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    private TnxDetailDTO tnxDetailDTO = null;
    private TransactionDetailRequest transactionDetailRequest;
    private RequestTransactionDetail requestTransactionDetail;
    private CurrencyFormatter formatter;
    private FacedTextView status_text;
    private FacedTextView callee_name;
    private ImageView image;
    private FacedTextView total_amount_value;
    private FacedTextView amount_value;
    private FacedTextView vat_value;
    private FacedTextView fee_charge_value;
    private FacedTextView fee_charge_text;
    private FacedTextView payment_request_code;
    private FacedTextView reference_code;
    private FacedTextView date_time;
    private FacedTextView card_number;
    private FacedTextView cell_number;
    private FacedTextView bank_name;
    private FacedTextView message;
    private FacedTextView detail_text;
    private LinearLayout pay_button;
    private LinearLayout creditInfo;
    private String authToken = "";
    private ImageManager imageManager;


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
        setContentView(R.layout.activity_transaction_detail);

        persianEnglishDigit = new PersianEnglishDigit();
        formatter = new CurrencyFormatter();


        context = this;
        activity = TransactionDetailActivity.this;
        imageManager = new ImageManager(activity, 200000, false);

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
        authToken = prefs.getString(Constants.LOGIN_TOKEN_ID, "");

        hamPayDialog = new HamPayDialog(activity);

        status_text = (FacedTextView) findViewById(R.id.status_text);
        callee_name = (FacedTextView) findViewById(R.id.callee_name);
        image = (ImageView) findViewById(R.id.image);
        total_amount_value = (FacedTextView) findViewById(R.id.total_amount_value);
        amount_value = (FacedTextView) findViewById(R.id.amount_value);
        vat_value = (FacedTextView) findViewById(R.id.vat_value);
        fee_charge_value = (FacedTextView) findViewById(R.id.fee_charge_value);
        fee_charge_text = (FacedTextView) findViewById(R.id.fee_charge_text);
        payment_request_code = (FacedTextView) findViewById(R.id.payment_request_code);
        reference_code = (FacedTextView) findViewById(R.id.reference_code);
        date_time = (FacedTextView) findViewById(R.id.date_time);
        card_number = (FacedTextView) findViewById(R.id.card_number);
        cell_number = (FacedTextView) findViewById(R.id.cell_number);
        bank_name = (FacedTextView) findViewById(R.id.bank_name);
        message = (FacedTextView) findViewById(R.id.message);
        pay_button = (LinearLayout) findViewById(R.id.pay_button);
        creditInfo = (LinearLayout) findViewById(R.id.creditInfo);
        detail_text = (FacedTextView) findViewById(R.id.detail_text);

        bundle = getIntent().getExtras();
        Intent intent = getIntent();
        transactionDTO = (TransactionDTO) intent.getSerializableExtra(Constants.USER_TRANSACTION_DTO);
        if (transactionDTO.getImageId() != null) {
            image.setTag(transactionDTO.getImageId());
            imageManager.displayImage(transactionDTO.getImageId(), image, R.drawable.user_placeholder);
        } else {
            image.setImageResource(R.drawable.user_placeholder);
        }

        callee_name.setText(transactionDTO.getPersonName());

        if (transactionDTO.getTransactionStatus() == TransactionDTO.TransactionStatus.SUCCESS) {
            if (transactionDTO.getTransactionType() == TransactionDTO.TransactionType.CREDIT) {
                status_text.setText(context.getString(R.string.credit));
                status_text.setTextColor(ContextCompat.getColor(context, R.color.register_btn_color));
            } else if (transactionDTO.getTransactionType() == TransactionDTO.TransactionType.DEBIT) {
                status_text.setText(context.getString(R.string.debit));
                status_text.setTextColor(ContextCompat.getColor(context, R.color.user_change_status));
            }

        }
//        else if (transactionDTO.getTransactionStatus() == TransactionDTO.TransactionStatus.PENDING) {
//            status_text.setText(context.getString(R.string.pending));
//            status_text.setTextColor(ContextCompat.getColor(context, R.color.pending_transaction));
//        }
        else {
            status_text.setText(transactionDTO.getTransactionType().equals(TransactionDTO.TransactionType.CREDIT) ? R.string.fail_credit : R.string.fail_debit);
            status_text.setTextColor(ContextCompat.getColor(context, R.color.failed_transaction));
            //TODO Came from service
            detail_text.setText(transactionDTO.getTransactionStatus().getDescription());
            detail_text.setTextColor(ContextCompat.getColor(context, R.color.normal_text));
        }

        transactionDetailRequest = new TransactionDetailRequest();
        transactionDetailRequest.setReference(transactionDTO.getReference());
        requestTransactionDetail = new RequestTransactionDetail(activity, new RequestTransactionDetailTaskCompleteListener());
        requestTransactionDetail.execute(transactionDetailRequest);

    }

    public class RequestTransactionDetailTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<TransactionDetailResponse>> {

        @Override
        public void onTaskComplete(ResponseMessage<TransactionDetailResponse> transactionDetailResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            ServiceEvent serviceName;
            LogEvent logEvent = new LogEvent(context);

            if (transactionDetailResponseMessage != null) {
                if (transactionDetailResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    serviceName = ServiceEvent.TRANSACTION_DETAIL_SUCCESS;
                    reference_code.setText(persianEnglishDigit.E2P(transactionDTO.getReference()));
                    tnxDetailDTO = transactionDetailResponseMessage.getService().getTransactionDetail();
                    if (tnxDetailDTO.getImageId() != null) {
                        image.setTag(tnxDetailDTO.getImageId());
                        imageManager.displayImage(tnxDetailDTO.getImageId(), image, R.drawable.user_placeholder);
                    } else {
                        image.setImageResource(R.drawable.user_placeholder);
                    }
                    if (transactionDTO.getPaymentType() == TransactionDTO.PaymentType.PAYMENT) {
                        if (tnxDetailDTO.getUserStatus() == TnxDetailDTO.UserStatus.ACTIVE) {
                            pay_button.setVisibility(View.VISIBLE);
                            pay_button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent();
                                    intent.setClass(activity, PaymentRequestDetailActivity.class);
                                    intent.putExtra(Constants.CONTACT_NAME, transactionDTO.getPersonName());
                                    intent.putExtra(Constants.CONTACT_PHONE_NO, tnxDetailDTO.getCellNumber());
                                    intent.putExtra(Constants.IMAGE_ID, transactionDTO.getImageId());
                                    startActivity(intent);
                                }
                            });
                        }
                        if (tnxDetailDTO.getName() != null) {
                            callee_name.setText(tnxDetailDTO.getName());
                        }
                        payment_request_code.setText(persianEnglishDigit.E2P(tnxDetailDTO.getCode()));
                        date_time.setText(persianEnglishDigit.E2P(new JalaliConvert().GregorianToPersian(tnxDetailDTO.getDate())));
                        if (transactionDTO.getPersonType() == TransactionDTO.PersonType.INDIVIDUAL) {
                            if (tnxDetailDTO.getCellNumber() != null) {
                                cell_number.setText(persianEnglishDigit.E2P(tnxDetailDTO.getCellNumber()));
                            }
                        }
                        if (tnxDetailDTO.getMessage() != null) {
                            message.setText(tnxDetailDTO.getMessage());
                            message.setVisibility(View.VISIBLE);
                        }
                        if (tnxDetailDTO.getAppliedCard() != null) {
                            creditInfo.setVisibility(View.VISIBLE);
                            card_number.setText(persianEnglishDigit.E2P(tnxDetailDTO.getAppliedCard().getMaskedCardNumber()));
                            bank_name.setText(tnxDetailDTO.getAppliedCard().getBankName());
                        }
                    } else if (transactionDTO.getPaymentType() == TransactionDTO.PaymentType.PURCHASE) {
                        callee_name.setText(tnxDetailDTO.getName());
                        payment_request_code.setText(tnxDetailDTO.getCode().substring(0, 3) + " " + tnxDetailDTO.getCode().substring(3, 6));
                        date_time.setText(persianEnglishDigit.E2P(new JalaliConvert().GregorianToPersian(tnxDetailDTO.getDate())));
                        if (tnxDetailDTO.getAppliedCard() != null) {
                            creditInfo.setVisibility(View.VISIBLE);
                            card_number.setText(persianEnglishDigit.E2P(tnxDetailDTO.getAppliedCard().getMaskedCardNumber()));
                            bank_name.setText(tnxDetailDTO.getAppliedCard().getBankName());
                        }
                    }

                    if (transactionDTO.getTransactionType() == TransactionDTO.TransactionType.CREDIT) {
                        total_amount_value.setText(persianEnglishDigit.E2P(formatter.format(tnxDetailDTO.getAmount() + tnxDetailDTO.getVat())));
                        amount_value.setText(persianEnglishDigit.E2P(formatter.format(tnxDetailDTO.getAmount())));
                        vat_value.setText(persianEnglishDigit.E2P(formatter.format(tnxDetailDTO.getVat())));
                        fee_charge_value.setVisibility(View.INVISIBLE);
                        fee_charge_text.setVisibility(View.INVISIBLE);
                        fee_charge_value.setText(persianEnglishDigit.E2P(formatter.format(tnxDetailDTO.getFeeCharge())));
                    } else if (transactionDTO.getTransactionType() == TransactionDTO.TransactionType.DEBIT) {
                        total_amount_value.setText(persianEnglishDigit.E2P(formatter.format(tnxDetailDTO.getAmount() + tnxDetailDTO.getFeeCharge() + tnxDetailDTO.getVat())));
                        amount_value.setText(persianEnglishDigit.E2P(formatter.format(tnxDetailDTO.getAmount())));
                        vat_value.setText(persianEnglishDigit.E2P(formatter.format(tnxDetailDTO.getVat())));
                        fee_charge_value.setText(persianEnglishDigit.E2P(formatter.format(tnxDetailDTO.getFeeCharge())));
                        fee_charge_value.setVisibility(View.VISIBLE);
                        fee_charge_text.setVisibility(View.VISIBLE);
                    }
                } else if (transactionDetailResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    serviceName = ServiceEvent.TRANSACTION_DETAIL_FAILURE;
                    forceLogout();
                } else {
                    serviceName = ServiceEvent.TRANSACTION_DETAIL_FAILURE;
                }
                logEvent.log(serviceName);
            }
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
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
}