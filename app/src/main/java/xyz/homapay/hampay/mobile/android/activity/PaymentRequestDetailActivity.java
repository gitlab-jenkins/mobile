package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.dto.ContactDTO;
import xyz.homapay.hampay.common.core.model.request.CalculateVatRequest;
import xyz.homapay.hampay.common.core.model.response.CalculateVatResponse;
import xyz.homapay.hampay.common.core.model.response.dto.PaymentInfoDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestCalculateVat;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.edittext.CurrencyFormatterTextWatcher;
import xyz.homapay.hampay.mobile.android.component.edittext.FacedEditText;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.firebase.LogEvent;
import xyz.homapay.hampay.mobile.android.firebase.service.ServiceEvent;
import xyz.homapay.hampay.mobile.android.img.ImageHelper;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.CurrencyFormatter;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

public class PaymentRequestDetailActivity extends AppCompatActivity {

    @BindView(R.id.payment_request_button)
    FacedTextView payment_request_button;
    PersianEnglishDigit persianEnglishDigit;
    @BindView(R.id.contact_name)
    FacedTextView contact_name;
    @BindView(R.id.cell_number)
    FacedTextView cell_number;
    @BindView(R.id.contact_message)
    FacedEditText contact_message;
    String contactMssage = "";
    @BindView(R.id.amount_value)
    FacedEditText amount_value;
    @BindView(R.id.vat_value)
    FacedTextView vat_value;
    boolean creditValueValidation = false;
    boolean intentContact = false;
    Context context;
    Activity activity;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    HamPayDialog hamPayDialog;
    @BindView(R.id.user_image)
    ImageView user_image;
    @BindView(R.id.add_vat)
    LinearLayout add_vat;
    @BindView(R.id.vat_icon)
    ImageView vat_icon;
    @BindView(R.id.amount_total)
    FacedTextView amount_total;
    private ContactDTO hamPayContact;
    private PaymentInfoDTO paymentInfo;
    private String displayName;
    private String cellNumber;
    private String imageId;
    private long amountValue = 0;
    private long MaxXferAmount = 0;
    private long MinXferAmount = 0;
    private long calculatedVat = 0;
    private CurrencyFormatter formatter;

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
        setContentView(R.layout.activity_payment_request_detail);
        ButterKnife.bind(this);

        context = this;
        activity = PaymentRequestDetailActivity.this;

        persianEnglishDigit = new PersianEnglishDigit();
        formatter = new CurrencyFormatter();
        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();

        try {
            MaxXferAmount = prefs.getLong(Constants.MAX_INDIVIDUAL_XFER_AMOUNT, 0);
            MinXferAmount = prefs.getLong(Constants.MIN_INDIVIDUAL_XFER_AMOUNT, 0);

        } catch (Exception ex) {
            Log.e("Error", ex.getStackTrace().toString());
        }

        hamPayDialog = new HamPayDialog(activity);

        amount_value = (FacedEditText) findViewById(R.id.amount_value);
        amount_value.addTextChangedListener(new CurrencyFormatterTextWatcher(amount_value));
        amount_value.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                vat_icon.setImageResource(R.drawable.add_vat);
                vat_value.setText("۰");
                calculatedVat = 0;
                amount_total.setText(amount_value.getText().toString());
            }
        });
        amount_value.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                creditValueValidation = amount_value.getText().toString().length() != 0;
            }
        });

        add_vat.setOnClickListener(v -> {
            if (amount_value.getText().toString().length() > 0) {
                if (amount_value.getText().toString().indexOf("٬") != -1) {
                    amountValue = Long.parseLong(persianEnglishDigit.P2E(amount_value.getText().toString().replace("٬", "")));
                } else if (amount_value.getText().toString().indexOf(",") != -1) {
                    amountValue = Long.parseLong(persianEnglishDigit.P2E(amount_value.getText().toString().replace(",", "")));
                } else {
                    amountValue = Long.parseLong(persianEnglishDigit.P2E(amount_value.getText().toString()));
                }
                if (calculatedVat == 0) {
                    CalculateVatRequest calculateVatRequest = new CalculateVatRequest();
                    calculateVatRequest.setAmount(amountValue);
                    RequestCalculateVat requestCalculateVat = new RequestCalculateVat(activity, new RequestCalculateVatTaskCompleteListener());
                    requestCalculateVat.execute(calculateVatRequest);
                } else {
                    vat_icon.setImageResource(R.drawable.add_vat);
                    vat_value.setText("۰");
                    calculatedVat = 0;
                    amount_total.setText(persianEnglishDigit.E2P(formatter.format(amountValue)));
                }
            }
        });

        Intent intent = getIntent();


        hamPayContact = (ContactDTO) intent.getSerializableExtra(Constants.HAMPAY_CONTACT);
        paymentInfo = (PaymentInfoDTO) intent.getSerializableExtra(Constants.PAYMENT_INFO);
        displayName = intent.getStringExtra(Constants.CONTACT_NAME);
        cellNumber = intent.getStringExtra(Constants.CONTACT_PHONE_NO);
        imageId = intent.getStringExtra(Constants.IMAGE_ID);

        if (hamPayContact != null) {
            displayName = hamPayContact.getDisplayName();
            cellNumber = hamPayContact.getCellNumber();
            imageId = hamPayContact.getContactImageId();
        } else if (paymentInfo != null) {
            displayName = paymentInfo.getCalleeName();
            cellNumber = paymentInfo.getCalleePhoneNumber();
            imageId = paymentInfo.getImageId();
        }


        if (hamPayContact != null || paymentInfo != null || displayName != null) {
            contact_name.setText(displayName);
            cell_number.setText(persianEnglishDigit.E2P(cellNumber));

            if (hamPayContact != null) {
                if (hamPayContact.getContactImageId() != null) {
                    imageId = hamPayContact.getContactImageId();
                }
            }
            if (paymentInfo != null) {
                if (paymentInfo.getImageId() != null) {
                    imageId = paymentInfo.getImageId();
                }
            }

            if (imageId != null) {
                editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                editor.commit();
                user_image.setTag(imageId);
                ImageHelper.getInstance(activity).imageLoader(imageId, user_image, R.drawable.user_placeholder);
            } else {
                user_image.setImageResource(R.drawable.user_placeholder);
            }
        } else {
        }


        payment_request_button = (FacedTextView) findViewById(R.id.payment_request_button);
        payment_request_button.setOnClickListener(v -> {
            amount_value.clearFocus();
            if (amount_value.getText().toString().length() == 0) {
                Toast.makeText(activity, getString(R.string.msg_null_amount), Toast.LENGTH_SHORT).show();
                return;
            }
            if (creditValueValidation) {
                contactMssage = contact_message.getText().toString();
                contactMssage = contactMssage.replaceAll(Constants.ENTER_CHARACTERS_REGEX, " ");
                editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                editor.commit();
                if (amount_value.getText().toString().indexOf("٬") != -1) {
                    amountValue = Long.parseLong(persianEnglishDigit.P2E(amount_value.getText().toString().replace("٬", "")));
                } else if (amount_value.getText().toString().indexOf(",") != -1) {
                    amountValue = Long.parseLong(persianEnglishDigit.P2E(amount_value.getText().toString().replace(",", "")));
                } else {
                    amountValue = Long.parseLong(persianEnglishDigit.P2E(amount_value.getText().toString()));
                }
                if (amountValue + calculatedVat >= MinXferAmount && amountValue + calculatedVat <= MaxXferAmount) {
                    Intent intent1 = new Intent(PaymentRequestDetailActivity.this, PaymentRequestConfirmActivity.class);
                    intent1.putExtra(Constants.CONTACT_NAME, displayName);
                    intent1.putExtra(Constants.CONTACT_PHONE_NO, cellNumber);
                    intent1.putExtra(Constants.IMAGE_ID, imageId);
                    intent1.putExtra(Constants.CONTACT_AMOUNT, amountValue);
                    intent1.putExtra(Constants.CONTACT_VAT, calculatedVat);
                    intent1.putExtra(Constants.CONTACT_MESSAGE, contact_message.getText().toString());
                    startActivityForResult(intent1, 10);
                } else {
                    new HamPayDialog(activity).showIncorrectAmountDialog(MinXferAmount, MaxXferAmount);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

        if (intentContact) {
            Intent i = new Intent();
            i.setClass(this, MainActivity.class);
            startActivity(i);
        }
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", 1024);
        setResult(1024);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 10) {
            if (resultCode == Activity.RESULT_OK) {
                int result = data.getIntExtra(Constants.ACTIVITY_RESULT, -1);
                if (result == 0) {
                    finish();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
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

    public class RequestCalculateVatTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<CalculateVatResponse>> {

        ServiceEvent serviceName;
        LogEvent logEvent = new LogEvent(context);

        public RequestCalculateVatTaskCompleteListener() {
        }

        @Override
        public void onTaskComplete(ResponseMessage<CalculateVatResponse> calculateVatResponseMessage) {

            hamPayDialog.dismisWaitingDialog();
            ResultStatus resultStatus;
            if (calculateVatResponseMessage != null) {
                resultStatus = calculateVatResponseMessage.getService().getResultStatus();
                if (resultStatus == ResultStatus.SUCCESS) {
                    serviceName = ServiceEvent.CALCULATE_VAT_SUCCESS;
                    vat_value.setText(persianEnglishDigit.E2P(formatter.format(calculateVatResponseMessage.getService().getAmount())));
                    calculatedVat = calculateVatResponseMessage.getService().getAmount();
                    amount_total.setText(persianEnglishDigit.E2P(formatter.format(calculatedVat + amountValue)));
                    vat_icon.setImageResource(R.drawable.remove_vat);
                } else if (calculateVatResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    serviceName = ServiceEvent.CALCULATE_VAT_FAILURE;
                    forceLogout();
                } else {
                    serviceName = ServiceEvent.CALCULATE_VAT_FAILURE;
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
