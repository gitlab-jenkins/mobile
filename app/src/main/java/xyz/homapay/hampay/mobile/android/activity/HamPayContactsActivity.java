package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.dto.ContactDTO;
import xyz.homapay.hampay.common.core.model.request.ContactsHampayEnabledRequest;
import xyz.homapay.hampay.common.core.model.response.ContactsHampayEnabledResponse;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.adapter.HamPayContactsAdapter;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestContactHampayEnabled;
import xyz.homapay.hampay.mobile.android.component.edittext.FacedEditText;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.util.Constants;

public class HamPayContactsActivity extends AppCompatActivity{


    private String authToken;
    private Context context;
    private Activity activity;
    private ListView paymentRequestList;
    List<ContactDTO> contacts;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    private HamPayContactsAdapter hamPayContactsAdapter;

    private Dialog dialog;

    ContactsHampayEnabledRequest contactsHampayEnabledRequest;
    RequestContactHampayEnabled requestContactHampayEnabled;

    private FacedEditText search_text;

    private HamPayDialog hamPayDialog;
    public void backActionBar(View view){
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hampay_contacts);

        context = this;
        activity = HamPayContactsActivity.this;
        hamPayDialog = new HamPayDialog(activity);

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
        authToken = prefs.getString(Constants.LOGIN_TOKEN_ID, "");
        paymentRequestList = (ListView)findViewById(R.id.paymentRequestList);
        search_text = (FacedEditText)findViewById(R.id.search_text);
        search_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                List<ContactDTO> searchContacts = new ArrayList<>();

                for (ContactDTO contact: contacts){
                    if (contact.getDisplayName().contains(search_text.getText().toString())){
                        searchContacts.add(contact);
                    }
                }
                hamPayContactsAdapter = new HamPayContactsAdapter(activity, searchContacts, authToken);
                paymentRequestList.setAdapter(hamPayContactsAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        contactsHampayEnabledRequest = new ContactsHampayEnabledRequest();
        requestContactHampayEnabled = new RequestContactHampayEnabled(activity, new RequestContactHampayEnabledTaskCompleteListener());
        requestContactHampayEnabled.execute(contactsHampayEnabledRequest);

        paymentRequestList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(activity, PaymentRequestDetailActivity.class);
                intent.putExtra(Constants.HAMPAY_CONTACT, contacts.get(position));
                startActivityForResult(intent, 1024);
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
//                        search_layout.setVisibility(View.VISIBLE);
                    }else {
//                        search_layout.setVisibility(View.GONE);
                    }

                    hamPayContactsAdapter = new HamPayContactsAdapter(context, contacts, authToken);
                    paymentRequestList.setAdapter(hamPayContactsAdapter);
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

}



