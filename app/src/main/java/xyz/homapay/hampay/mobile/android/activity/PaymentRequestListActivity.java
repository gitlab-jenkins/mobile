package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.List;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.dto.ContactDTO;
import xyz.homapay.hampay.common.core.model.request.ContactsHampayEnabledRequest;
import xyz.homapay.hampay.common.core.model.request.LatestInvoiceContactsRequest;
import xyz.homapay.hampay.common.core.model.request.PendingPOListRequest;
import xyz.homapay.hampay.common.core.model.response.ContactsHampayEnabledResponse;
import xyz.homapay.hampay.common.core.model.response.LatestInvoiceContactsResponse;
import xyz.homapay.hampay.common.core.model.response.PendingPOListResponse;
import xyz.homapay.hampay.common.core.model.response.dto.PaymentInfoDTO;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.adapter.LatestInvoiceAdapter;
import xyz.homapay.hampay.mobile.android.adapter.PendingPOAdapter;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestContactHampayEnabled;
import xyz.homapay.hampay.mobile.android.async.RequestLatestInvoiceContacts;
import xyz.homapay.hampay.mobile.android.async.RequestPendingPOList;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.util.Constants;

public class PaymentRequestListActivity extends AppCompatActivity{


    private Context context;
    private Activity activity;
    private ListView paymentRequestList;
    List<ContactDTO> contacts;
    List<PaymentInfoDTO> paymentInfoList;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    private String authToken = "";

    private LatestInvoiceAdapter latestInvoiceAdapter;
    private PendingPOAdapter pendingPOAdapter;

    private RequestLatestInvoiceContacts requestLatestInvoiceContacts;
    private LatestInvoiceContactsRequest latestInvoiceContactsRequest;


    private RequestPendingPOList requestPendingPOList;
    private PendingPOListRequest pendingPOListRequest;

    private Dialog dialog;

    ContactsHampayEnabledRequest contactsHampayEnabledRequest;
    RequestContactHampayEnabled requestContactHampayEnabled;

    private HamPayDialog hamPayDialog;

    private RelativeLayout search_layout;

    private ImageView hampay_contacts;

    public void backActionBar(View view){
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_request_list);

        context = this;
        activity = PaymentRequestListActivity.this;
        hamPayDialog = new HamPayDialog(activity);

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
        authToken =  prefs.getString(Constants.LOGIN_TOKEN_ID, "");

        paymentRequestList = (ListView)findViewById(R.id.paymentRequestList);
        hampay_contacts = (ImageView)findViewById(R.id.hampay_contacts);



        pendingPOListRequest = new PendingPOListRequest();
        requestPendingPOList = new RequestPendingPOList(activity, new RequestPendingPOListTaskCompleteListener());
        requestPendingPOList.execute(pendingPOListRequest);

//        latestInvoiceContactsRequest = new LatestInvoiceContactsRequest();
//        requestLatestInvoiceContacts = new RequestLatestInvoiceContacts(activity, new RequestLatestInvoiceContactsTaskCompleteListener());
//        requestLatestInvoiceContacts.execute(latestInvoiceContactsRequest);

        paymentRequestList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(activity, PaymentRequestDetailActivity.class);
                intent.putExtra(Constants.HAMPAY_CONTACT, contacts.get(position));
                startActivityForResult(intent, 1024);
            }
        });

        hampay_contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(activity, HamPayContactsActivity.class);
                startActivity(intent);
            }
        });
    }


    public class RequestContactHampayEnabledTaskCompleteListener implements
            AsyncTaskCompleteListener<ResponseMessage<ContactsHampayEnabledResponse>> {
        @Override
        public void onTaskComplete(ResponseMessage<ContactsHampayEnabledResponse> contactsHampayEnabledResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            if (contactsHampayEnabledResponseMessage != null){
                if (contactsHampayEnabledResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS){

                    contacts = contactsHampayEnabledResponseMessage.getService().getContacts();

                    if (contacts.size() > 0){
                        search_layout.setVisibility(View.VISIBLE);
                    }else {
                        search_layout.setVisibility(View.GONE);
                    }

                    latestInvoiceAdapter = new LatestInvoiceAdapter(context, contacts,
                            prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
                    paymentRequestList.setAdapter(latestInvoiceAdapter);
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
        public void onTaskPreRun() {
            hamPayDialog.showWaitingdDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }

    public class RequestLatestInvoiceContactsTaskCompleteListener implements
            AsyncTaskCompleteListener<ResponseMessage<LatestInvoiceContactsResponse>> {
        @Override
        public void onTaskComplete(ResponseMessage<LatestInvoiceContactsResponse> latestInvoiceContactsResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            if (latestInvoiceContactsResponseMessage != null){
                if (latestInvoiceContactsResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS){

                    contacts = latestInvoiceContactsResponseMessage.getService().getContacts();

                    if (contacts.size() > 0){
//                        search_layout.setVisibility(View.VISIBLE);
                    }else {
//                        search_layout.setVisibility(View.GONE);
                    }

                    latestInvoiceAdapter = new LatestInvoiceAdapter(context, contacts,
                            prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
                    paymentRequestList.setAdapter(latestInvoiceAdapter);
                }
                else {
                    requestLatestInvoiceContacts = new RequestLatestInvoiceContacts(context, new RequestLatestInvoiceContactsTaskCompleteListener());
                    new HamPayDialog(activity).showFailLatestInvoiceDialog(requestLatestInvoiceContacts, latestInvoiceContactsRequest,
                            latestInvoiceContactsResponseMessage.getService().getResultStatus().getCode(),
                            latestInvoiceContactsResponseMessage.getService().getResultStatus().getDescription());

                }
            }else {
                requestLatestInvoiceContacts = new RequestLatestInvoiceContacts(context, new RequestLatestInvoiceContactsTaskCompleteListener());
                new HamPayDialog(activity).showFailLatestInvoiceDialog(requestLatestInvoiceContacts, latestInvoiceContactsRequest,
                        Constants.LOCAL_ERROR_CODE,
                        getString(R.string.msg_fail_contacts_enabled));
            }
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingdDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }


    public class RequestPendingPOListTaskCompleteListener implements
            AsyncTaskCompleteListener<ResponseMessage<PendingPOListResponse>> {
        @Override
        public void onTaskComplete(ResponseMessage<PendingPOListResponse> pendingPOListResponseResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            if (pendingPOListResponseResponseMessage != null){
                if (pendingPOListResponseResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS){

                    paymentInfoList = pendingPOListResponseResponseMessage.getService().getPendingList();
                    pendingPOAdapter = new PendingPOAdapter(activity, paymentInfoList, authToken);
                    paymentRequestList.setAdapter(pendingPOAdapter);

                }
                else {
//                    requestPendingPOList = new RequestPendingPOList(context, new RequestPendingPOListTaskCompleteListener());
//                    new HamPayDialog(activity).showFailLatestInvoiceDialog(requestPendingPOList, latestInvoiceContactsRequest,
//                            latestInvoiceContactsResponseMessage.getService().getResultStatus().getCode(),
//                            latestInvoiceContactsResponseMessage.getService().getResultStatus().getDescription());

                }
            }else {
//                requestLatestInvoiceContacts = new RequestLatestInvoiceContacts(context, new RequestLatestInvoiceContactsTaskCompleteListener());
//                new HamPayDialog(activity).showFailLatestInvoiceDialog(requestLatestInvoiceContacts, latestInvoiceContactsRequest,
//                        Constants.LOCAL_ERROR_CODE,
//                        getString(R.string.msg_fail_contacts_enabled));
            }
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingdDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }
}



