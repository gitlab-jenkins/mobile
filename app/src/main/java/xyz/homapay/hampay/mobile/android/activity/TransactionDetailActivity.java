package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.TransactionDetailRequest;
import xyz.homapay.hampay.common.core.model.response.PaymentDetailResponse;
import xyz.homapay.hampay.common.core.model.response.PurchaseDetailResponse;
import xyz.homapay.hampay.common.core.model.response.TransactionDetailResponse;
import xyz.homapay.hampay.common.core.model.response.dto.PaymentInfoDTO;
import xyz.homapay.hampay.common.core.model.response.dto.PurchaseInfoDTO;
import xyz.homapay.hampay.common.core.model.response.dto.TnxDetailDTO;
import xyz.homapay.hampay.common.core.model.response.dto.TransactionDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestTransactionDetail;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.CurrencyFormatter;
import xyz.homapay.hampay.mobile.android.util.JalaliConvert;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

public class TransactionDetailActivity extends AppCompatActivity {

    Bundle bundle;
    TransactionDTO transactionDTO;
    private TnxDetailDTO tnxDetailDTO = null;
    private PaymentInfoDTO paymentInfo = null;
    private PurchaseInfoDTO purchaseInfo = null;
    private TransactionDetailRequest transactionDetailRequest;
    private RequestTransactionDetail requestTransactionDetail;


    PersianEnglishDigit persianEnglishDigit;
    Context context;
    Activity activity;
    private CurrencyFormatter formatter;
    HamPayDialog hamPayDialog;
    private FacedTextView caller_name;
    private FacedTextView callee_name;
    private FacedTextView total_amount_value;
    private FacedTextView amount_value;
    private FacedTextView vat_value;
    private FacedTextView fee_charge_value;
    private FacedTextView payment_request_code;
    private FacedTextView date_time;
    private FacedTextView card_number;
    private FacedTextView cell_number;
    private FacedTextView bank_name;
    private FacedTextView message;
    private LinearLayout pay_button;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;


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

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();

        hamPayDialog = new HamPayDialog(activity);

        caller_name = (FacedTextView) findViewById(R.id.caller_name);
        callee_name = (FacedTextView) findViewById(R.id.callee_name);
        total_amount_value = (FacedTextView) findViewById(R.id.total_amount_value);
        amount_value = (FacedTextView) findViewById(R.id.amount_value);
        vat_value = (FacedTextView) findViewById(R.id.vat_value);
        fee_charge_value = (FacedTextView) findViewById(R.id.fee_charge_value);
        payment_request_code = (FacedTextView) findViewById(R.id.payment_request_code);
        date_time = (FacedTextView) findViewById(R.id.date_time);
        card_number = (FacedTextView) findViewById(R.id.card_number);
        cell_number = (FacedTextView) findViewById(R.id.cell_number);
        bank_name = (FacedTextView) findViewById(R.id.bank_name);
        message = (FacedTextView) findViewById(R.id.message);
        pay_button = (LinearLayout) findViewById(R.id.pay_button);
        pay_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (paymentInfo != null) {
                    Intent intent = new Intent(activity, PaymentRequestDetailActivity.class);
                    intent.putExtra(Constants.PAYMENT_INFO, paymentInfo);
                    startActivity(intent);
                }
            }
        });


        bundle = getIntent().getExtras();
        Intent intent = getIntent();
        transactionDTO = (TransactionDTO) intent.getSerializableExtra(Constants.USER_TRANSACTION_DTO);

        if (transactionDTO.getTransactionStatus() == TransactionDTO.TransactionStatus.SUCCESS) {
            if (transactionDTO.getTransactionType() == TransactionDTO.TransactionType.CREDIT) {
                caller_name.setText(context.getString(R.string.credit));
                caller_name.setTextColor(ContextCompat.getColor(context, R.color.register_btn_color));
//                status_icon.setImageResource(R.drawable.arrow_r);
            } else if (transactionDTO.getTransactionType() == TransactionDTO.TransactionType.DEBIT) {
                caller_name.setText(context.getString(R.string.debit));
                caller_name.setTextColor(ContextCompat.getColor(context, R.color.user_change_status));
//                status_icon.setImageResource(R.drawable.arrow_p);
            }

        } else if (transactionDTO.getTransactionStatus() == TransactionDTO.TransactionStatus.PENDING) {
            caller_name.setText(context.getString(R.string.pending));
            caller_name.setTextColor(ContextCompat.getColor(context, R.color.pending_transaction));
//            viewHolder.status_icon.setImageResource(R.drawable.pending);
        } else {
            caller_name.setText(context.getString(R.string.fail));
            caller_name.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
//            viewHolder.status_icon.setImageResource(R.drawable.arrow_f);
        }


        transactionDetailRequest = new TransactionDetailRequest();
        transactionDetailRequest.setReference(transactionDTO.getReference());
        requestTransactionDetail = new RequestTransactionDetail(activity, new RequestTransactionDetailTaskCompleteListener());
        requestTransactionDetail.execute(transactionDetailRequest);


//        if (transactionDTO.getPaymentType() == TransactionDTO.PaymentType.PAYMENT) {
//            editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
//            editor.commit();
//            paymentDetailRequest = new PaymentDetailRequest();
//            paymentDetailRequest.setProviderId(transactionDTO.getReference());
//            requestPaymentDetail = new RequestPaymentDetail(activity, new RequestPaymentDetailTaskCompleteListener());
//            requestPaymentDetail.execute(paymentDetailRequest);
//        }else if (transactionDTO.getPaymentType() == TransactionDTO.PaymentType.PURCHASE){
//            editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
//            editor.commit();
//            purchaseDetailRequest = new PurchaseDetailRequest();
//            purchaseDetailRequest.setProviderId(transactionDTO.getReference());
//            requestPurchaseDetail = new RequestPurchaseDetail(activity, new RequestPurchaseDetailTaskCompleteListener());
//            requestPurchaseDetail.execute(purchaseDetailRequest);
//        }

    }

    public class RequestTransactionDetailTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<TransactionDetailResponse>> {

        @Override
        public void onTaskComplete(ResponseMessage<TransactionDetailResponse> transactionDetailResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            if (transactionDetailResponseMessage != null) {
                if (transactionDetailResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    tnxDetailDTO = transactionDetailResponseMessage.getService().getTransactionDetail();
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
                        callee_name.setText(tnxDetailDTO.getName());
                        total_amount_value.setText(persianEnglishDigit.E2P(formatter.format(tnxDetailDTO.getAmount() + tnxDetailDTO.getFeeCharge() + tnxDetailDTO.getVat())));
                        amount_value.setText(persianEnglishDigit.E2P(formatter.format(tnxDetailDTO.getAmount())));
                        vat_value.setText(persianEnglishDigit.E2P(formatter.format(tnxDetailDTO.getVat())));
                        fee_charge_value.setText(persianEnglishDigit.E2P(formatter.format(tnxDetailDTO.getFeeCharge())));
                        payment_request_code.setText(persianEnglishDigit.E2P(tnxDetailDTO.getCode()));
                        date_time.setText(persianEnglishDigit.E2P(new JalaliConvert().GregorianToPersian(tnxDetailDTO.getDate())));
                        if (transactionDTO.getPersonType() == TransactionDTO.PersonType.INDIVIDUAL) {
                            cell_number.setText(persianEnglishDigit.E2P(tnxDetailDTO.getCellNumber()));
                        }
                        if (tnxDetailDTO.getMessage() != null) {
                            message.setText(tnxDetailDTO.getMessage());
                        }
                        if (tnxDetailDTO.getAppliedCard() != null) {
                            card_number.setText(persianEnglishDigit.E2P(tnxDetailDTO.getAppliedCard().getMaskedCardNumber()));
                            bank_name.setText(tnxDetailDTO.getAppliedCard().getBankName());
                        }
                    } else if (transactionDTO.getPaymentType() == TransactionDTO.PaymentType.PURCHASE) {
                        callee_name.setText(tnxDetailDTO.getName());
                        total_amount_value.setText(persianEnglishDigit.E2P(formatter.format(tnxDetailDTO.getAmount() + tnxDetailDTO.getFeeCharge() + tnxDetailDTO.getVat())));
                        amount_value.setText(persianEnglishDigit.E2P(formatter.format(tnxDetailDTO.getAmount())));
                        vat_value.setText(persianEnglishDigit.E2P(formatter.format(tnxDetailDTO.getVat())));
                        fee_charge_value.setText(persianEnglishDigit.E2P(formatter.format(tnxDetailDTO.getFeeCharge())));
                        payment_request_code.setText(persianEnglishDigit.E2P(tnxDetailDTO.getCode()));
                        date_time.setText(persianEnglishDigit.E2P(new JalaliConvert().GregorianToPersian(tnxDetailDTO.getDate())));
                        cell_number.setText(persianEnglishDigit.E2P(tnxDetailDTO.getCode()));
                        if (tnxDetailDTO.getAppliedCard() != null) {
                            card_number.setText(persianEnglishDigit.E2P(tnxDetailDTO.getAppliedCard().getMaskedCardNumber()));
                            bank_name.setText(tnxDetailDTO.getAppliedCard().getBankName());
                        }
                    }
                }
            }
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }
}