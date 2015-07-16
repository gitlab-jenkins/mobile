package com.hampay.mobile.android.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hampay.common.core.model.response.dto.TransactionDTO;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.component.FacedTextView;
import com.hampay.mobile.android.fragment.UserTransactionFragment;
import com.hampay.mobile.android.util.JalaliConvert;

public class TransactionDetailActivity extends ActionBarActivity implements View.OnClickListener{

    Bundle bundle;
    int index = 0;

    TransactionDTO transaction;

    ImageView status_icon;
    FacedTextView status_text;
    FacedTextView user_name;
    FacedTextView user_mobile_no;
    FacedTextView date_time;
    FacedTextView tracking_code;
    FacedTextView price_pay;
    FacedTextView message;
    LinearLayout pay_to_one_ll;
    LinearLayout send_message;
    LinearLayout user_call;

    public void backActionBar(View view){
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);


        status_icon = (ImageView)findViewById(R.id.status_icon);
        status_text = (FacedTextView)findViewById(R.id.status_text);
        user_name = (FacedTextView)findViewById(R.id.user_name);
        user_mobile_no = (FacedTextView)findViewById(R.id.user_mobile_no);
        date_time = (FacedTextView)findViewById(R.id.date_time);
        tracking_code = (FacedTextView)findViewById(R.id.tracking_code);
        price_pay = (FacedTextView)findViewById(R.id.price_pay);
        message = (FacedTextView)findViewById(R.id.message);
        pay_to_one_ll = (LinearLayout)findViewById(R.id.pay_to_one_ll);
        pay_to_one_ll.setOnClickListener(this);

        send_message = (LinearLayout)findViewById(R.id.send_message);
        send_message.setOnClickListener(this);

        user_call = (LinearLayout)findViewById(R.id.user_call);
        user_call.setOnClickListener(this);

        bundle = getIntent().getExtras();

        index = bundle.getInt("index", 0);

        transaction = UserTransactionFragment.transactionDTOs.get(index);

        if (transaction.getTransactionStatus().ordinal() == 0){

            if (transaction.getTransactionType().ordinal() == 0){
                status_text.setText(getString(R.string.credit));
                status_text.setTextColor(getResources().getColor(R.color.register_btn_color));
                status_icon.setImageResource(R.drawable.arrow_r);
            }
            else if (transaction.getTransactionType().ordinal() == 1){
                status_text.setText(getString(R.string.debit));
                status_text.setTextColor(getResources().getColor(R.color.user_change_status));
                status_icon.setImageResource(R.drawable.arrow_p);
            }

        }else {
            status_text.setText(getString(R.string.fail));
            status_text.setTextColor(getResources().getColor(R.color.colorPrimary));
            status_icon.setImageResource(R.drawable.arrow_f);
        }


        user_name.setText(transaction.getPersonName());
        date_time.setText((new JalaliConvert()).GregorianToPersian(transaction.getTransactionDate()));
        message.setText(transaction.getMessage());
        price_pay.setText(getString(R.string.price) + ": "+ String.format("%,d", transaction.getAmount()).replace(",", ".") + " "+ getString(R.string.currency_rials));
        user_mobile_no.setText(transaction.getMobileNumber());
        date_time.setText(getString(R.string.transaction_date) + " " + (new JalaliConvert()).GregorianToPersian(transaction.getTransactionDate()));
        tracking_code.setText(transaction.getReference());


//        status_text.setText(transaction.getTransactionType().name());
//        user_name.setText(transaction.getPersonName());


//        message.setText(transaction.getMessage());
//        price_pay.setText(transaction.getAmount() + "");


    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.pay_to_one_ll:
                Intent intent = new Intent();
                intent.setClass(TransactionDetailActivity.this, PayOneActivity.class);
                intent.putExtra("contact_phone_no", transaction.getMobileNumber());
                intent.putExtra("contact_name", transaction.getPersonName());
                startActivity(intent);
                break;

            case R.id.send_message:
                if (transaction.getMobileNumber() != null)
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", transaction.getMobileNumber(), null)));
                break;

            case R.id.user_call:
                if (transaction.getMobileNumber() != null)
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("tel", transaction.getMobileNumber(), null)));
                break;
        }

    }
}
