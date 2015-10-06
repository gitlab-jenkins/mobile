package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hampay.common.core.model.response.dto.TransactionDTO;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.JalaliConvert;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

public class TransactionDetailActivity extends ActionBarActivity implements View.OnClickListener{

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
    FacedTextView message;
    LinearLayout responseMessage_ll;
    FacedTextView reject_message;
    LinearLayout pay_to_one_ll;
    LinearLayout send_message;
    LinearLayout user_call;
    Context context;
    Activity activity;

    public void backActionBar(View view){
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);

        persianEnglishDigit = new PersianEnglishDigit();

        context = this;
        activity = TransactionDetailActivity.this;

        from_to_text = (FacedTextView)findViewById(R.id.from_to_text);
        status_icon = (ImageView)findViewById(R.id.status_icon);
        status_text = (FacedTextView)findViewById(R.id.status_text);
        user_name = (FacedTextView)findViewById(R.id.user_name);
        user_mobile_no = (FacedTextView)findViewById(R.id.user_mobile_no);
        date_time = (FacedTextView)findViewById(R.id.date_time);
        tracking_code = (FacedTextView)findViewById(R.id.tracking_code);
        price_pay = (FacedTextView)findViewById(R.id.price_pay);
        message = (FacedTextView)findViewById(R.id.message);
        responseMessage_ll = (LinearLayout)findViewById(R.id.responseMessage_ll);
        reject_message = (FacedTextView)findViewById(R.id.reject_message);
        pay_to_one_ll = (LinearLayout)findViewById(R.id.pay_to_one_ll);
        pay_to_one_ll.setOnClickListener(this);

        send_message = (LinearLayout)findViewById(R.id.send_message);
        send_message.setOnClickListener(this);

        user_call = (LinearLayout)findViewById(R.id.user_call);
        user_call.setOnClickListener(this);

        bundle = getIntent().getExtras();

        Intent intent = getIntent();

        transactionDTO = (TransactionDTO)intent.getSerializableExtra(Constants.USER_TRANSACTION_DTO);

        if (transactionDTO.getTransactionStatus() == TransactionDTO.TransactionStatus.SUCCESS){

            if (transactionDTO.getTransactionType() == TransactionDTO.TransactionType.CREDIT){
                from_to_text.setText(getString(R.string.transaction_from));
                status_text.setText(getString(R.string.credit));
                status_text.setTextColor(getResources().getColor(R.color.register_btn_color));
                status_icon.setImageResource(R.drawable.arrow_r);
            }
            else if (transactionDTO.getTransactionType() == TransactionDTO.TransactionType.DEBIT){
                from_to_text.setText(getString(R.string.transaction_to));
                status_text.setText(getString(R.string.debit));
                status_text.setTextColor(getResources().getColor(R.color.user_change_status));
                status_icon.setImageResource(R.drawable.arrow_p);
            }

        }else if (transactionDTO.getTransactionStatus() == TransactionDTO.TransactionStatus.PENDING) {
            from_to_text.setText(getString(R.string.transaction_to));
            status_text.setText(context.getString(R.string.pending));
            status_text.setTextColor(getResources().getColor(R.color.pending_transaction));
            status_icon.setImageResource(R.drawable.pending);
        }
        else {
            from_to_text.setText(getString(R.string.transaction_to));
            status_text.setText(getString(R.string.fail));
            status_text.setTextColor(getResources().getColor(R.color.colorPrimary));
            status_icon.setImageResource(R.drawable.arrow_f);
        }


        user_name.setText(transactionDTO.getPersonName());
        message.setText(transactionDTO.getMessage());
        if (transactionDTO.getRejectReasonMessage() != null){
            responseMessage_ll.setVisibility(View.VISIBLE);
            reject_message.setText(transactionDTO.getRejectReasonMessage());
        }
        price_pay.setText(persianEnglishDigit.E2P(String.format("%,d", transactionDTO.getAmount())));
        user_mobile_no.setText(persianEnglishDigit.E2P(transactionDTO.getPhoneNumber()));
        date_time.setText(persianEnglishDigit.E2P((new JalaliConvert()).GregorianToPersian(transactionDTO.getTransactionDate())));
        tracking_code.setText(persianEnglishDigit.E2P(transactionDTO.getReference()));


    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.pay_to_one_ll:
                Intent intent = new Intent();
                intent.setClass(TransactionDetailActivity.this, PayOneActivity.class);
                intent.putExtra("contact_phone_no", transactionDTO.getPhoneNumber());
                intent.putExtra("contact_name", transactionDTO.getPersonName());
                startActivity(intent);
                break;

            case R.id.send_message:

                if (transactionDTO.getPhoneNumber() != null)
                    new HamPayDialog(activity).showCommunicateDialog(0, transactionDTO.getPhoneNumber());
                break;

            case R.id.user_call:
                if (transactionDTO.getPhoneNumber() != null)
                    new HamPayDialog(activity).showCommunicateDialog(1, transactionDTO.getPhoneNumber());
                break;
        }

    }
}