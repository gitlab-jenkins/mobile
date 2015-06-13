package com.hampay.mobile.android.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.hampay.common.core.model.response.dto.TransactionDTO;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.component.FacedTextView;
import com.hampay.mobile.android.fragment.TransactionFragment;

public class TransactionDetailActivity extends ActionBarActivity {

    Bundle bundle;
    int index = 0;

    ImageView status_icon;
    FacedTextView status_text;
    FacedTextView user_name;
    FacedTextView user_mobile_no;
    FacedTextView date_time;
    FacedTextView tracking_code;
    FacedTextView price_pay;
    FacedTextView message;



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

        bundle = getIntent().getExtras();

        index = bundle.getInt("index", 0);

        TransactionDTO transaction = TransactionFragment.transactionListResponse.getService().getTransactions().get(index);


        status_text.setText(transaction.getTransactionType().name());
        user_name.setText(transaction.getPersonName());
        date_time.setText(transaction.getTransactionDate().getDate() + " " + transaction.getTransactionDate().getTime());
        tracking_code.setText(transaction.getReference());
        message.setText(transaction.getMessage());
        price_pay.setText(transaction.getAmount() + "");
        user_mobile_no.setText(transaction.getMobileNumber());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_transaction_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
