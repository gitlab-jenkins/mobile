package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.dto.ContactDTO;
import xyz.homapay.hampay.common.core.model.request.ContactsHampayEnabledRequest;
import xyz.homapay.hampay.common.core.model.request.GetUserIdTokenRequest;
import xyz.homapay.hampay.common.core.model.response.ContactsHampayEnabledResponse;
import xyz.homapay.hampay.common.core.model.response.GetUserIdTokenResponse;
import xyz.homapay.hampay.mobile.android.Helper.DatabaseHelper;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.adapter.HamPayEnabledContactAdapter;
import xyz.homapay.hampay.mobile.android.adapter.RecentPaymentRequestAdapter;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestContactHampayEnabled;
import xyz.homapay.hampay.mobile.android.async.RequestUserIdToken;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.model.RecentPay;
import xyz.homapay.hampay.mobile.android.util.Constants;

public class PaymentRequestActivity extends AppCompatActivity implements View.OnClickListener{


    private Context context;
    private Activity activity;
    private ListView paymentRequestList;
    DatabaseHelper dbHelper;
    List<RecentPay> recentPays;
    List<ContactDTO> hamPayContact;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    GetUserIdTokenRequest getUserIdTokenRequest;
    RequestUserIdToken requestUserIdToken;

    private RecentPaymentRequestAdapter recentPaymentRequestAdapter;
    private HamPayEnabledContactAdapter hamPayEnabledContactAdapter;

    String serverKey = "";

    private RelativeLayout recent_rl;
    private FacedTextView recent_title;
    private View recent_sep;
    private RelativeLayout hampay_rl;
    private FacedTextView hampay_title;
    private View hampay_sep;

    private int selectedType = 1;

    ContactsHampayEnabledRequest contactsHampayEnabledRequest;
    RequestContactHampayEnabled requestContactHampayEnabled;

    public void backActionBar(View view){
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_request);

        context = this;
        activity = PaymentRequestActivity.this;

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();

        recentPays = new ArrayList<>();
        paymentRequestList = (ListView)findViewById(R.id.paymentRequestList);

        recent_rl = (RelativeLayout)findViewById(R.id.recent_rl);
        recent_rl.setOnClickListener(this);
        recent_title = (FacedTextView)findViewById(R.id.recent_title);
        recent_sep = (View)findViewById(R.id.recent_sep);
        hampay_rl = (RelativeLayout)findViewById(R.id.hampay_rl);
        hampay_rl.setOnClickListener(this);
        hampay_title = (FacedTextView)findViewById(R.id.hampay_title);
        hampay_sep = (View)findViewById(R.id.hampay_sep);

        recentPaymentRequestAdapter = new RecentPaymentRequestAdapter(activity, recentPays, prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
        paymentRequestList.setAdapter(recentPaymentRequestAdapter);

        paymentRequestList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(activity, PaymentRequestDetailActivity.class);
                intent.putExtra(Constants.LOGIN_TOKEN_ID, prefs.getString(Constants.LOGIN_TOKEN_ID, ""));

                if (selectedType == 1) {
                    intent.putExtra("contact_name", recentPays.get(position).getName());
                    intent.putExtra("contact_phone_no", recentPays.get(position).getPhone());
                    intent.putExtra(Constants.USER_IMAGE_PROFILE_ID, recentPays.get(position).getId());
                }else {
                    intent.putExtra("contact_name", /*hamPayContact.get(position).getDisplayName()*/"علی امیری آخوندیان");
                    intent.putExtra("contact_phone_no", hamPayContact.get(position).getCellNumber());
                    intent.putExtra(Constants.USER_IMAGE_PROFILE_ID, hamPayContact.get(position).getContactImageId());
                }
                startActivityForResult(intent, 1024);
            }
        });

        if ((prefs.getString(Constants.USER_ID_TOKEN, "") != null && prefs.getString(Constants.USER_ID_TOKEN, "").length() == 16)){
            serverKey = prefs.getString(Constants.USER_ID_TOKEN, "");
            dbHelper = new DatabaseHelper(activity, serverKey);
            recentPays = dbHelper.getAllRecentPays();
            recentPaymentRequestAdapter = new RecentPaymentRequestAdapter(activity, recentPays, prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
            paymentRequestList.setAdapter(recentPaymentRequestAdapter);
        }else {
            recentPays = new ArrayList<RecentPay>();
            getUserIdTokenRequest = new GetUserIdTokenRequest();
            requestUserIdToken = new RequestUserIdToken(context, new RequestGetUserIdTokenResponseTaskCompleteListener());
            requestUserIdToken.execute(getUserIdTokenRequest);
        }


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.recent_rl:
                selectedType = 1;
                recent_title.setTextColor(getResources().getColor(R.color.user_change_status));
                recent_sep.setBackgroundColor(getResources().getColor(R.color.user_change_status));
                hampay_title.setTextColor(getResources().getColor(R.color.normal_text));
                hampay_sep.setBackgroundColor(getResources().getColor(R.color.normal_text));
                break;

            case R.id.hampay_rl:
                selectedType = 2;
                hampay_title.setTextColor(getResources().getColor(R.color.user_change_status));
                hampay_sep.setBackgroundColor(getResources().getColor(R.color.user_change_status));
                recent_title.setTextColor(getResources().getColor(R.color.normal_text));
                recent_sep.setBackgroundColor(getResources().getColor(R.color.normal_text));
                contactsHampayEnabledRequest = new ContactsHampayEnabledRequest();
                requestContactHampayEnabled = new RequestContactHampayEnabled(context, new RequestContactHampayEnabledTaskCompleteListener());
                requestContactHampayEnabled.execute(contactsHampayEnabledRequest);
                break;
        }

    }

    public class RequestContactHampayEnabledTaskCompleteListener implements
            AsyncTaskCompleteListener<ResponseMessage<ContactsHampayEnabledResponse>> {
        @Override
        public void onTaskComplete(ResponseMessage<ContactsHampayEnabledResponse> contactsHampayEnabledResponseMessage) {

            if (contactsHampayEnabledResponseMessage != null){
                if (contactsHampayEnabledResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS){

                    hamPayContact = contactsHampayEnabledResponseMessage.getService().getContacts();

                    hamPayEnabledContactAdapter = new HamPayEnabledContactAdapter(context, hamPayContact,
                            prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
                    paymentRequestList.setAdapter(hamPayEnabledContactAdapter);
                }
                else {
                    requestContactHampayEnabled = new RequestContactHampayEnabled(context, new RequestContactHampayEnabledTaskCompleteListener());
                    new HamPayDialog(activity).showFailContactsHamPayEnabledDialog(requestContactHampayEnabled, contactsHampayEnabledRequest,
                            contactsHampayEnabledResponseMessage.getService().getResultStatus().getCode(),
                            contactsHampayEnabledResponseMessage.getService().getResultStatus().getDescription());

                }
            }else {
                requestContactHampayEnabled = new RequestContactHampayEnabled(context, new RequestContactHampayEnabledTaskCompleteListener());
                new HamPayDialog(activity).showFailContactsHamPayEnabledDialog(requestContactHampayEnabled, contactsHampayEnabledRequest,
                        Constants.LOCAL_ERROR_CODE,
                        getString(R.string.msg_fail_contacts_enabled));

            }
        }

        @Override
        public void onTaskPreRun() { }
    }




    public class RequestGetUserIdTokenResponseTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<GetUserIdTokenResponse>> {
        public RequestGetUserIdTokenResponseTaskCompleteListener(){
        }

        @Override
        public void onTaskComplete(ResponseMessage<GetUserIdTokenResponse> registrationGetUserIdTokenResponseMessage) {

            ResultStatus resultStatus;
            if (registrationGetUserIdTokenResponseMessage != null) {
                resultStatus = registrationGetUserIdTokenResponseMessage.getService().getResultStatus();
                if (resultStatus == ResultStatus.SUCCESS) {
                    serverKey = registrationGetUserIdTokenResponseMessage.getService().getUserIdToken();
                    editor.putString(Constants.USER_ID_TOKEN, serverKey);
                    editor.commit();

                    dbHelper = new DatabaseHelper(activity, serverKey);
                    recentPays = dbHelper.getAllRecentPays();
                    recentPaymentRequestAdapter = new RecentPaymentRequestAdapter(activity, recentPays, prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
                    paymentRequestList.setAdapter(recentPaymentRequestAdapter);

                }else {
                    requestUserIdToken = new RequestUserIdToken(context, new RequestGetUserIdTokenResponseTaskCompleteListener());
                    new HamPayDialog(activity).showFailGetUserIdTokenDialog(requestUserIdToken, getUserIdTokenRequest,
                            registrationGetUserIdTokenResponseMessage.getService().getResultStatus().getCode(),
                            registrationGetUserIdTokenResponseMessage.getService().getResultStatus().getDescription());
                }
            }else {
                requestUserIdToken = new RequestUserIdToken(context, new RequestGetUserIdTokenResponseTaskCompleteListener());
                new HamPayDialog(activity).showFailGetUserIdTokenDialog(requestUserIdToken, getUserIdTokenRequest,
                        Constants.LOCAL_ERROR_CODE,
                        getString(R.string.msg_fail_server_key));
            }
        }

        @Override
        public void onTaskPreRun() {   }
    }
}



