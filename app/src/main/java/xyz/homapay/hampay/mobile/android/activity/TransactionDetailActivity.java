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
import xyz.homapay.hampay.mobile.android.img.ImageHelper;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.CurrencyFormatter;
import xyz.homapay.hampay.mobile.android.util.JalaliConvert;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

public class TransactionDetailActivity extends AppCompatActivity {

    private TransactionDTO transaction;
    private PersianEnglishDigit persian;
    private Context context;
    private Activity activity;
    private HamPayDialog hamPayDialog;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private TnxDetailDTO tnxDetail = null;
    private TransactionDetailRequest transactionDetailRequest;
    private RequestTransactionDetail requestTransactionDetail;
    private CurrencyFormatter formatter;
    private FacedTextView status_text;
    private FacedTextView callee_name;
    private ImageView image;
    private FacedTextView total_amount_value;
    private FacedTextView amount_value;
    private LinearLayout moreAmount;
    private FacedTextView vat_value;
    private FacedTextView fee_charge_value;
    private FacedTextView fee_charge_text;
    private FacedTextView payment_request_code;
    private FacedTextView reference_code;
    private FacedTextView date_time;
    private FacedTextView cell_number;
    private FacedTextView message;
    private FacedTextView detail_text;
    private LinearLayout pay_button;
    private LinearLayout billsInfo;
    private FacedTextView billsId;
    private FacedTextView payId;

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

        persian = new PersianEnglishDigit();
        formatter = new CurrencyFormatter();


        context = this;
        activity = TransactionDetailActivity.this;

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();

        hamPayDialog = new HamPayDialog(activity);

        status_text = (FacedTextView) findViewById(R.id.status_text);
        callee_name = (FacedTextView) findViewById(R.id.callee_name);
        image = (ImageView) findViewById(R.id.image);
        total_amount_value = (FacedTextView) findViewById(R.id.total_amount_value);
        amount_value = (FacedTextView) findViewById(R.id.amount_value);
        moreAmount = (LinearLayout) findViewById(R.id.more_amount);
        vat_value = (FacedTextView) findViewById(R.id.vat_value);
        fee_charge_value = (FacedTextView) findViewById(R.id.fee_charge_value);
        fee_charge_text = (FacedTextView) findViewById(R.id.fee_charge_text);
        payment_request_code = (FacedTextView) findViewById(R.id.payment_request_code);
        reference_code = (FacedTextView) findViewById(R.id.reference_code);
        date_time = (FacedTextView) findViewById(R.id.date_time);
        cell_number = (FacedTextView) findViewById(R.id.cell_number);
        message = (FacedTextView) findViewById(R.id.message);
        billsInfo = (LinearLayout) findViewById(R.id.bills_info);
        billsId = (FacedTextView) findViewById(R.id.billsId);
        payId = (FacedTextView) findViewById(R.id.payId);
        pay_button = (LinearLayout) findViewById(R.id.pay_button);
        detail_text = (FacedTextView) findViewById(R.id.detail_text);

        Intent intent = getIntent();
        transaction = (TransactionDTO) intent.getSerializableExtra(Constants.USER_TRANSACTION_DTO);
        if (transaction.getImageId() != null) {
            image.setTag(transaction.getImageId());
            ImageHelper.getInstance(context).imageLoader(transaction.getImageId(), image, R.drawable.user_placeholder);
        } else {
            image.setImageResource(R.drawable.user_placeholder);
        }

        callee_name.setText(transaction.getPersonName());

        if (transaction.getTransactionStatus() == TransactionDTO.TransactionStatus.SUCCESS) {
            if (transaction.getTransactionType() == TransactionDTO.TransactionType.CREDIT) {
                status_text.setText(context.getString(R.string.credit));
                status_text.setTextColor(ContextCompat.getColor(context, R.color.register_btn_color));
            } else if (transaction.getTransactionType() == TransactionDTO.TransactionType.DEBIT) {
                status_text.setText(context.getString(R.string.debit));
                status_text.setTextColor(ContextCompat.getColor(context, R.color.user_change_status));
            }

        }
        else {
            status_text.setText(transaction.getTransactionType().equals(TransactionDTO.TransactionType.CREDIT) ? R.string.fail_credit : R.string.fail_debit);
            status_text.setTextColor(ContextCompat.getColor(context, R.color.failed_transaction));
            detail_text.setText(transaction.getTransactionStatus().getDescription());
            detail_text.setTextColor(ContextCompat.getColor(context, R.color.normal_text));
        }

        transactionDetailRequest = new TransactionDetailRequest();
        transactionDetailRequest.setReference(transaction.getReference());
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
                    reference_code.setText(persian.E2P(transaction.getReference()));
                    tnxDetail = transactionDetailResponseMessage.getService().getTransactionDetail();
                    if (tnxDetail.getImageId() != null) {
                        image.setTag(tnxDetail.getImageId());
                        ImageHelper.getInstance(context).imageLoader(tnxDetail.getImageId(), image, R.drawable.user_placeholder);
                    } else {
                        image.setImageResource(R.drawable.user_placeholder);
                    }
                    if (transaction.getPaymentType() == TransactionDTO.PaymentType.PAYMENT) {
                        if (tnxDetail.getUserStatus() == TnxDetailDTO.UserStatus.ACTIVE) {
                            pay_button.setVisibility(View.VISIBLE);
                            pay_button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent();
                                    intent.setClass(activity, PaymentRequestDetailActivity.class);
                                    intent.putExtra(Constants.CONTACT_NAME, transaction.getPersonName());
                                    intent.putExtra(Constants.CONTACT_PHONE_NO, tnxDetail.getCellNumber());
                                    intent.putExtra(Constants.IMAGE_ID, transaction.getImageId());
                                    startActivity(intent);
                                }
                            });
                        }
                        if (tnxDetail.getName() != null) {
                            callee_name.setText(tnxDetail.getName());
                        }
                        moreAmount.setVisibility(View.VISIBLE);
                        payment_request_code.setText(persian.E2P(tnxDetail.getCode()));
                        date_time.setText(persian.E2P(new JalaliConvert().GregorianToPersian(tnxDetail.getDate())));
                        if (transaction.getPersonType() == TransactionDTO.PersonType.INDIVIDUAL) {
                            if (tnxDetail.getCellNumber() != null) {
                                cell_number.setText(persian.E2P(tnxDetail.getCellNumber()));
                            }
                        }
                        if (tnxDetail.getMessage() != null) {
                            message.setText(tnxDetail.getMessage());
                            message.setVisibility(View.VISIBLE);
                        }
                    } else if (transaction.getPaymentType() == TransactionDTO.PaymentType.PURCHASE) {
                        callee_name.setText(tnxDetail.getName());
                        payment_request_code.setText(tnxDetail.getCode().substring(0, 3) + " " + tnxDetail.getCode().substring(3, 6));
                        date_time.setText(persian.E2P(new JalaliConvert().GregorianToPersian(tnxDetail.getDate())));
                        moreAmount.setVisibility(View.VISIBLE);
                    }else if (transaction.getPaymentType() == TransactionDTO.PaymentType.UTILITY_BILL){
                        payment_request_code.setText(persian.E2P(tnxDetail.getCode()));
                        billsInfo.setVisibility(View.VISIBLE);
                        billsId.setText(persian.E2P(tnxDetail.getBillId()));
                        payId.setText(persian.E2P(tnxDetail.getPayId()));
                        date_time.setText(persian.E2P(new JalaliConvert().GregorianToPersian(tnxDetail.getDate())));
                    }

                    if (transaction.getTransactionType() == TransactionDTO.TransactionType.CREDIT) {
                        total_amount_value.setText(persian.E2P(formatter.format(tnxDetail.getAmount() + tnxDetail.getVat())));
                        amount_value.setText(persian.E2P(formatter.format(tnxDetail.getAmount())));

                        vat_value.setText(persian.E2P(formatter.format(tnxDetail.getVat())));
                        fee_charge_value.setVisibility(View.INVISIBLE);
                        fee_charge_text.setVisibility(View.INVISIBLE);
                        fee_charge_value.setText(persian.E2P(formatter.format(tnxDetail.getFeeCharge())));
                    } else if (transaction.getTransactionType() == TransactionDTO.TransactionType.DEBIT) {
                        total_amount_value.setText(persian.E2P(formatter.format(tnxDetail.getAmount() + tnxDetail.getFeeCharge() + tnxDetail.getVat())));
                        amount_value.setText(persian.E2P(formatter.format(tnxDetail.getAmount())));
                        vat_value.setText(persian.E2P(formatter.format(tnxDetail.getVat())));
                        fee_charge_value.setText(persian.E2P(formatter.format(tnxDetail.getFeeCharge())));
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