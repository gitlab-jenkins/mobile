package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

public class TransactionDetailActivity extends AppCompatActivity implements View.OnClickListener {

    Bundle bundle;

    TransactionDTO transactionDTO;

    PersianEnglishDigit persianEnglishDigit;

    FacedTextView from_to_text;
    ImageView status_icon;
    FacedTextView status_text;
    FacedTextView user_name;
    FacedTextView user_mobile_no;
    FacedTextView date_time;
    FacedTextView tracking_code;
    FacedTextView price_pay;
    LinearLayout more_payment_info;
    FacedTextView user_fee_value;
    FacedTextView total_payment_value;
    FacedTextView message;
    LinearLayout responseMessage_ll;
    FacedTextView reject_message;
    LinearLayout pay_to_one_ll;
    LinearLayout send_message;
    LinearLayout user_call;
    Context context;
    Activity activity;
    private CurrencyFormatter currencyFormatter;

    HamPayDialog hamPayDialog;

    RequestPaymentDetail requestPaymentDetail;
    PaymentDetailRequest paymentDetailRequest;

    RequestPurchaseDetail requestPurchaseDetail;
    PurchaseDetailRequest purchaseDetailRequest;

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
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);

        persianEnglishDigit = new PersianEnglishDigit();
        currencyFormatter = new CurrencyFormatter();


        context = this;
        activity = TransactionDetailActivity.this;

        hamPayDialog = new HamPayDialog(activity);

//        from_to_text = (FacedTextView) findViewById(R.id.from_to_text);
//        status_icon = (ImageView) findViewById(R.id.status_icon);
//        status_text = (FacedTextView) findViewById(R.id.status_text);
//        user_name = (FacedTextView) findViewById(R.id.user_name);
//        user_mobile_no = (FacedTextView) findViewById(R.id.user_mobile_no);
//        date_time = (FacedTextView) findViewById(R.id.date_time);
//        tracking_code = (FacedTextView) findViewById(R.id.tracking_code);
//        price_pay = (FacedTextView) findViewById(R.id.price_pay);
//        more_payment_info = (LinearLayout) findViewById(R.id.more_payment_info);
//        user_fee_value = (FacedTextView) findViewById(R.id.user_fee_value);
//        total_payment_value = (FacedTextView) findViewById(R.id.total_payment_value);
//        message = (FacedTextView) findViewById(R.id.message);
//        responseMessage_ll = (LinearLayout) findViewById(R.id.responseMessage_ll);
//        reject_message = (FacedTextView) findViewById(R.id.reject_message);
//        pay_to_one_ll = (LinearLayout) findViewById(R.id.pay_to_one_ll);
//        pay_to_one_ll.setOnClickListener(this);
//
//        send_message = (LinearLayout) findViewById(R.id.send_message);
//        send_message.setOnClickListener(this);
//
//        user_call = (LinearLayout) findViewById(R.id.user_call);
//        user_call.setOnClickListener(this);

        bundle = getIntent().getExtras();

        Intent intent = getIntent();

        transactionDTO = (TransactionDTO) intent.getSerializableExtra(Constants.USER_TRANSACTION_DTO);


        if (transactionDTO.getPaymentType() == TransactionDTO.PaymentType.PAYMENT) {
            paymentDetailRequest = new PaymentDetailRequest();
            paymentDetailRequest.setProviderId(transactionDTO.getReference());
            requestPaymentDetail = new RequestPaymentDetail(activity, new RequestPaymentDetailTaskCompleteListener());
            requestPaymentDetail.execute(paymentDetailRequest);
        }else if (transactionDTO.getPaymentType() == TransactionDTO.PaymentType.PURCHASE){
            purchaseDetailRequest = new PurchaseDetailRequest();
            purchaseDetailRequest.setProviderId(transactionDTO.getReference());
            requestPurchaseDetail = new RequestPurchaseDetail(activity, new RequestPurchaseDetailTaskCompleteListener());
            requestPurchaseDetail.execute(purchaseDetailRequest);
        }


//        if (transactionDTO.getPaymentType() == TransactionDTO.PaymentType.PAYMENT)

//        if (transactionDTO.getTransactionStatus() == TransactionDTO.TransactionStatus.SUCCESS) {
//
//            if (transactionDTO.getTransactionType() == TransactionDTO.TransactionType.CREDIT) {
//                from_to_text.setText(getString(R.string.transaction_from));
//                status_text.setText(getString(R.string.credit));
//                status_text.setTextColor(ContextCompat.getColor(context, R.color.register_btn_color));
//                status_icon.setImageResource(R.drawable.arrow_r);
//            } else if (transactionDTO.getTransactionType() == TransactionDTO.TransactionType.DEBIT) {
//                from_to_text.setText(getString(R.string.transaction_to));
//                status_text.setText(getString(R.string.debit));
//                status_text.setTextColor(ContextCompat.getColor(context, R.color.user_change_status));
//                status_icon.setImageResource(R.drawable.arrow_p);
//            }
//
//        } else if (transactionDTO.getTransactionStatus() == TransactionDTO.TransactionStatus.PENDING) {
//            from_to_text.setText(getString(R.string.transaction_to));
//            status_text.setText(context.getString(R.string.pending));
//            status_text.setTextColor(ContextCompat.getColor(context, R.color.pending_transaction));
//            status_icon.setImageResource(R.drawable.pending);
//        } else {
//            from_to_text.setText(getString(R.string.transaction_to));
//            status_text.setText(getString(R.string.fail));
//            status_text.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
//            status_icon.setImageResource(R.drawable.arrow_f);
//        }


//        user_name.setText(transactionDTO.getPersonName());
//        message.setText(transactionDTO.getMessage());
//        if (transactionDTO.getRejectReasonMessage() != null){
//        responseMessage_ll.setVisibility(View.VISIBLE);
//            reject_message.setText(transactionDTO.getRejectReasonMessage());
    }
//        price_pay.setText(persianEnglishDigit.E2P(currencyFormatter.format(transactionDTO.getAmount())));
//        if (transactionDTO.getAmount() == 0){
//            more_payment_info.setVisibility(View.GONE);
//        }else {
//            more_payment_info.setVisibility(View.VISIBLE);
//            user_fee_value.setText(persianEnglishDigit.E2P(currencyFormatter.format(transactionDTO.getFeeCharge())) + " ");
//            if (transactionDTO.getFeeCharge() != null && transactionDTO.getAmount() != null)
//                total_payment_value.setText(persianEnglishDigit.E2P(currencyFormatter.format(transactionDTO.getFeeCharge() + transactionDTO.getAmount())) + " ");
//        }
//
//        user_mobile_no.setText(persianEnglishDigit.E2P(transactionDTO.getPhoneNumber()));
//        date_time.setText(persianEnglishDigit.E2P((new JalaliConvert()).GregorianToPersian(transactionDTO.getTransactionDate())));
//        tracking_code.setText(persianEnglishDigit.E2P(transactionDTO.getReference()));

//    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.send_message:

//                if (transactionDTO.getPhoneNumber() != null)
//                    new HamPayDialog(activity).showCommunicateDialog(0, transactionDTO.getPhoneNumber());
//                break;
//
//            case R.id.user_call:
//                if (transactionDTO.getPhoneNumber() != null)
//                    new HamPayDialog(activity).showCommunicateDialog(1, transactionDTO.getPhoneNumber());
//                break;
        }

    }


    public class RequestPaymentDetailTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<PaymentDetailResponse>> {

        @Override
        public void onTaskComplete(ResponseMessage<PaymentDetailResponse> paymentDetailResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            if (paymentDetailResponseMessage != null) {

                if (paymentDetailResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {

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

                }
            }
        }

        @Override
        public void onTaskPreRun() {

        }
    }
}