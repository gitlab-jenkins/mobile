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

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.TransactionDetailRequest;
import xyz.homapay.hampay.common.core.model.response.TransactionDetailResponse;
import xyz.homapay.hampay.common.core.model.response.dto.TnxDetailDTO;
import xyz.homapay.hampay.common.core.model.response.dto.TransactionDTO;
import xyz.homapay.hampay.common.core.model.response.dto.UserProfileDTO;
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

    @BindView(R.id.status_text)
    FacedTextView status_text;
    @BindView(R.id.callee_name)
    FacedTextView callee_name;
    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.total_amount_value)
    FacedTextView total_amount_value;
    @BindView(R.id.amount_value)
    FacedTextView amount_value;
    @BindView(R.id.more_amount)
    LinearLayout moreAmount;
    @BindView(R.id.fee_charge_value)
    FacedTextView fee_charge_value;
    @BindView(R.id.fee_charge_text)
    FacedTextView fee_charge_text;
    @BindView(R.id.payment_request_code)
    FacedTextView payment_request_code;
    @BindView(R.id.reference_code)
    FacedTextView reference_code;
    @BindView(R.id.date_time)
    FacedTextView date_time;
    @BindView(R.id.cell_number)
    FacedTextView cell_number;
    @BindView(R.id.message)
    FacedTextView message;
    @BindView(R.id.detail_text)
    FacedTextView detail_text;
    @BindView(R.id.pay_button)
    LinearLayout pay_button;
    @BindView(R.id.bills_info)
    LinearLayout billsInfo;
    @BindView(R.id.billsId)
    FacedTextView billsId;
    @BindView(R.id.payId)
    FacedTextView payId;
    @BindView(R.id.tvCellNumber)
    FacedTextView tvCellNumber;
    @BindView(R.id.tvChargeType)
    FacedTextView tvChargeType;
    @BindView(R.id.llCellNumber)
    LinearLayout llCellNumber;
    @BindView(R.id.llChargeType)
    LinearLayout llChrageType;
    @BindView(R.id.indicatorChargeType)
    View indicatorChargeType;
    @BindView(R.id.indicatorCellNumber)
    View indicatorCellNumber;
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
    private UserProfileDTO userProfile;

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
        ButterKnife.bind(this);

        persian = new PersianEnglishDigit();
        formatter = new CurrencyFormatter();
        userProfile = (UserProfileDTO) getIntent().getSerializableExtra(Constants.USER_PROFILE);
        context = this;
        activity = TransactionDetailActivity.this;
        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
        hamPayDialog = new HamPayDialog(activity);

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

        } else {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.IBAN_PAYMENT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent();
                intent.setClass(activity, PaymentRequestDetailActivity.class);
                intent.putExtra(Constants.CONTACT_NAME, transaction.getPersonName());
                intent.putExtra(Constants.CONTACT_PHONE_NO, tnxDetail.getCellNumber());
                intent.putExtra(Constants.IMAGE_ID, transaction.getImageId());
                startActivity(intent);
            }

        }
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
                            pay_button.setOnClickListener(v -> {

                                if ((prefs.getBoolean(Constants.SETTING_CHANGE_IBAN_STATUS, false)) || userProfile.getIbanDTO() != null && userProfile.getIbanDTO().getIban() != null && userProfile.getIbanDTO().getIban().length() > 0) {
                                    Intent intent = new Intent();
                                    intent.setClass(activity, PaymentRequestDetailActivity.class);
                                    intent.putExtra(Constants.CONTACT_NAME, transaction.getPersonName());
                                    intent.putExtra(Constants.CONTACT_PHONE_NO, tnxDetail.getCellNumber());
                                    intent.putExtra(Constants.IMAGE_ID, transaction.getImageId());
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent();
                                    intent.setClass(activity, IbanIntronActivity.class);
                                    intent.putExtra(Constants.IBAN_SOURCE_ACTION, Constants.IBAN_SOURCE_PAYMENT);
                                    startActivityForResult(intent, Constants.IBAN_PAYMENT_REQUEST_CODE);
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
                            moreAmount.setVisibility(View.GONE);
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
                    } else if (transaction.getPaymentType() == TransactionDTO.PaymentType.UTILITY_BILL) {
                        payment_request_code.setText(persian.E2P(tnxDetail.getCode()));
                        billsInfo.setVisibility(View.VISIBLE);
                        billsId.setText(persian.E2P(tnxDetail.getBillId()));
                        payId.setText(persian.E2P(tnxDetail.getPayId()));
                        date_time.setText(persian.E2P(new JalaliConvert().GregorianToPersian(tnxDetail.getDate())));
                    } else if (transaction.getPaymentType() == TransactionDTO.PaymentType.TOP_UP) {
                        String name = transactionDetailResponseMessage.getService().getTransactionDetail().getName();
                        callee_name.setText(name);
                        payment_request_code.setText(persian.E2P(tnxDetail.getCode()));
                        date_time.setText(persian.E2P(new JalaliConvert().GregorianToPersian(tnxDetail.getDate())));
                        llCellNumber.setVisibility(View.VISIBLE);
                        llChrageType.setVisibility(View.VISIBLE);
                        indicatorCellNumber.setVisibility(View.VISIBLE);
                        indicatorChargeType.setVisibility(View.VISIBLE);
                        tvCellNumber.setText(persian.E2P(tnxDetail.getCellNumber()));
                        tvChargeType.setText(transactionDetailResponseMessage.getService().getTransactionDetail().getDescription());
                    }

                    if (transaction.getPaymentType() != TransactionDTO.PaymentType.TOP_UP) {
                        llCellNumber.setVisibility(View.GONE);
                        llChrageType.setVisibility(View.GONE);
                        indicatorCellNumber.setVisibility(View.GONE);
                        indicatorChargeType.setVisibility(View.GONE);
                    }

                    if (transaction.getTransactionType() == TransactionDTO.TransactionType.CREDIT) {
                        total_amount_value.setText(persian.E2P(formatter.format(tnxDetail.getAmount())));
                        amount_value.setText(persian.E2P(formatter.format(tnxDetail.getAmount())));

                        fee_charge_value.setVisibility(View.INVISIBLE);
                        fee_charge_text.setVisibility(View.INVISIBLE);
                        fee_charge_value.setText(persian.E2P(formatter.format(tnxDetail.getFeeCharge())));
                    } else if (transaction.getTransactionType() == TransactionDTO.TransactionType.DEBIT) {
                        total_amount_value.setText(persian.E2P(formatter.format(tnxDetail.getAmount() + tnxDetail.getFeeCharge())));
                        amount_value.setText(persian.E2P(formatter.format(tnxDetail.getAmount())));
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