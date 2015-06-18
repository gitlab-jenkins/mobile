package com.hampay.mobile.android.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.response.BankListResponse;
import com.hampay.common.core.model.response.BusinessListResponse;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.adapter.BankListAdapter;
import com.hampay.mobile.android.adapter.HamPayBusinessAdapter;
import com.hampay.mobile.android.component.FacedTextView;
import com.hampay.mobile.android.webservice.WebServices;

public class ProfileEntryActivity extends ActionBarActivity {


    CardView keepOn_CardView;
    RelativeLayout bankSelection;
    Dialog bankSelectionDialog;
    FacedTextView selected_bank_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_entry);


        selected_bank_title = (FacedTextView)findViewById(R.id.selected_bank_title);

        bankSelection = (RelativeLayout)findViewById(R.id.bankSelection);
        bankSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Rect displayRectangle = new Rect();
                Activity parent = (Activity) ProfileEntryActivity.this;
                Window window = parent.getWindow();
                window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

                View view = getLayoutInflater().inflate(R.layout.dialog_bank_select, null);

                ListView bankListView = (ListView) view.findViewById(R.id.bankListView);

                BankListAdapter bankListAdapter = new BankListAdapter(getApplicationContext(), bankListResponse.getService().getBanks());

                bankListView.setAdapter(bankListAdapter);
                bankListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selected_bank_title.setText(bankListResponse.getService().getBanks().get(position).getTitle());
                        bankSelectionDialog.dismiss();
                    }
                });


                view.setMinimumWidth((int) (displayRectangle.width() * 0.8f));
                bankSelectionDialog = new Dialog(ProfileEntryActivity.this);
                bankSelectionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                bankSelectionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                bankSelectionDialog.setContentView(view);
                bankSelectionDialog.setTitle(null);
                bankSelectionDialog.setCanceledOnTouchOutside(true);

                bankSelectionDialog.show();
            }
        });

        new HttpBankList().execute();

        keepOn_CardView = (CardView) findViewById(R.id.keepOn_CardView);
        keepOn_CardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ProfileEntryActivity.this, VerificationActivity.class);
                startActivity(intent);
            }
        });
    }


    private ResponseMessage<BankListResponse> bankListResponse;

    public class HttpBankList extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... params) {

            WebServices webServices = new WebServices(getApplicationContext());
            bankListResponse = webServices.getBankList();

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (bankListResponse != null) {

            }


        }
    }
}
