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

import java.util.List;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.PaymentDetailRequest;
import xyz.homapay.hampay.common.core.model.request.PurchaseDetailRequest;
import xyz.homapay.hampay.common.core.model.response.PaymentDetailResponse;
import xyz.homapay.hampay.common.core.model.response.PurchaseDetailResponse;
import xyz.homapay.hampay.common.core.model.response.TransactionListResponse;
import xyz.homapay.hampay.common.core.model.response.dto.PaymentInfoDTO;
import xyz.homapay.hampay.common.core.model.response.dto.PurchaseInfoDTO;
import xyz.homapay.hampay.common.core.model.response.dto.TransactionDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestPaymentDetail;
import xyz.homapay.hampay.mobile.android.async.RequestPurchaseDetail;
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

    PersianEnglishDigit persianEnglishDigit;

    Context context;
    Activity activity;
    private CurrencyFormatter formatter;

    HamPayDialog hamPayDialog;

    RequestPaymentDetail requestPaymentDetail;
    PaymentDetailRequest paymentDetailRequest;

    RequestPurchaseDetail requestPurchaseDetail;
    PurchaseDetailRequest purchaseDetailRequest;

    private FacedTextView caller_name;
    private FacedTextView callee_name;
    private FacedTextView total_amount_value;
    private FacedTextView amount_value;
    private FacedTextView vat_value;
    private FacedTextView fee_charge_value;
    private FacedTextView payment_request_code;
    private FacedTextView date_time;
    private FacedTextView card_number_value;
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

        caller_name = (FacedTextView)findViewById(R.id.caller_name);
        callee_name = (FacedTextView)findViewById(R.id.callee_name);
        total_amount_value = (FacedTextView)findViewById(R.id.total_amount_value);
        amount_value = (FacedTextView)findViewById(R.id.amount_value);
        vat_value = (FacedTextView)findViewById(R.id.vat_value);
        fee_charge_value = (FacedTextView)findViewById(R.id.fee_charge_value);
        payment_request_code = (FacedTextView)findViewById(R.id.payment_request_code);
        date_time = (FacedTextView)findViewById(R.id.date_time);
        card_number_value = (FacedTextView)findViewById(R.id.card_number);
        cell_number = (FacedTextView)findViewById(R.id.cell_number);
        bank_name = (FacedTextView)findViewById(R.id.bank_name);
        message = (FacedTextView)findViewById(R.id.message);
        pay_button = (LinearLayout)findViewById(R.id.pay_button);



        bundle = getIntent().getExtras();
        Intent intent = getIntent();
        transactionDTO = (TransactionDTO) intent.getSerializableExtra(Constants.USER_TRANSACTION_DTO);

        if (transactionDTO.getTransactionStatus() == TransactionDTO.TransactionStatus.SUCCESS){
            if (transactionDTO.getTransactionType() == TransactionDTO.TransactionType.CREDIT){
                caller_name.setText(context.getString(R.string.credit));
                caller_name.setTextColor(ContextCompat.getColor(context, R.color.register_btn_color));
//                viewHolder.status_icon.setImageResource(R.drawable.arrow_r);
            }
            else if (transactionDTO.getTransactionType() == TransactionDTO.TransactionType.DEBIT){
                caller_name.setText(context.getString(R.string.debit));
                caller_name.setTextColor(ContextCompat.getColor(context, R.color.user_change_status));
//                viewHolder.status_icon.setImageResource(R.drawable.arrow_p);
            }

        }else if (transactionDTO.getTransactionStatus() == TransactionDTO.TransactionStatus.PENDING) {
            caller_name.setText(context.getString(R.string.pending));
            caller_name.setTextColor(ContextCompat.getColor(context, R.color.pending_transaction));
//            viewHolder.status_icon.setImageResource(R.drawable.pending);
        }
        else {
            caller_name.setText(context.getString(R.string.fail));
            caller_name.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
//            viewHolder.status_icon.setImageResource(R.drawable.arrow_f);
        }

        if (transactionDTO.getPaymentType() == TransactionDTO.PaymentType.PAYMENT) {
            editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
            editor.commit();
            paymentDetailRequest = new PaymentDetailRequest();
            paymentDetailRequest.setProviderId(transactionDTO.getReference());
            requestPaymentDetail = new RequestPaymentDetail(activity, new RequestPaymentDetailTaskCompleteListener());
            requestPaymentDetail.execute(paymentDetailRequest);
        }else if (transactionDTO.getPaymentType() == TransactionDTO.PaymentType.PURCHASE){
            editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
            editor.commit();
            purchaseDetailRequest = new PurchaseDetailRequest();
            purchaseDetailRequest.setProviderId(transactionDTO.getReference());
            requestPurchaseDetail = new RequestPurchaseDetail(activity, new RequestPurchaseDetailTaskCompleteListener());
            requestPurchaseDetail.execute(purchaseDetailRequest);
        }

    }


    public class RequestPaymentDetailTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<PaymentDetailResponse>> {

        @Override
        public void onTaskComplete(ResponseMessage<PaymentDetailResponse> paymentDetailResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            if (paymentDetailResponseMessage != null) {

                if (paymentDetailResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    PaymentInfoDTO paymentInfo = paymentDetailResponseMessage.getService().getPaymentInfo();
                    callee_name.setText(paymentInfo.getCalleeName());
                    total_amount_value.setText(persianEnglishDigit.E2P(formatter.format(paymentInfo.getAmount() + paymentInfo.getFeeCharge() + paymentInfo.getVat())));
                    amount_value.setText(persianEnglishDigit.E2P(formatter.format(paymentInfo.getAmount())));
                    vat_value.setText(persianEnglishDigit.E2P(formatter.format(paymentInfo.getVat())));
                    fee_charge_value.setText(persianEnglishDigit.E2P(formatter.format(paymentInfo.getFeeCharge())));
                    payment_request_code.setText(persianEnglishDigit.E2P(paymentInfo.getProductCode()));
                    date_time.setText(persianEnglishDigit.E2P(new JalaliConvert().GregorianToPersian(paymentInfo.getCreatedBy())));
                    cell_number.setText(persianEnglishDigit.E2P(paymentInfo.getCallerPhoneNumber()));
                    message.setText(paymentInfo.getMessage());
                }
            }
        }

        @Override
        public void onTaskPreRun() {

        }
    }

    public class RequestPurchaseDetailTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<PurchaseDetailResponse>> {

        @Override
        public void onTaskComplete(ResponseMessage<PurchaseDetailResponse> purchaseDetailResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            if (purchaseDetailResponseMessage != null) {

                if (purchaseDetailResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    PurchaseInfoDTO purchaseInfo = purchaseDetailResponseMessage.getService().getpurchaseInfo();
                    callee_name.setText(purchaseInfo.getMerchantName());
                    total_amount_value.setText(persianEnglishDigit.E2P(formatter.format(purchaseInfo.getAmount() + purchaseInfo.getFeeCharge() + purchaseInfo.getVat())));
                    amount_value.setText(persianEnglishDigit.E2P(formatter.format(purchaseInfo.getAmount())));
                    vat_value.setText(persianEnglishDigit.E2P(formatter.format(purchaseInfo.getVat())));
                    fee_charge_value.setText(persianEnglishDigit.E2P(formatter.format(purchaseInfo.getFeeCharge())));
                    payment_request_code.setText(persianEnglishDigit.E2P(purchaseInfo.getProductCode()));
                    date_time.setText(persianEnglishDigit.E2P(new JalaliConvert().GregorianToPersian(purchaseInfo.getCreatedBy())));
                    cell_number.setText(persianEnglishDigit.E2P(purchaseInfo.getPurchaseCode()));
                    card_number_value.setText(persianEnglishDigit.E2P(purchaseInfo.getPspInfo().getCardDTO().getMaskedCardNumber()));
                    bank_name.setText(purchaseInfo.getPspInfo().getCardDTO().getBankName());
                }
            }
        }

        @Override
        public void onTaskPreRun() {

        }
    }
}