package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.IntentCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.dto.ContactDTO;
import xyz.homapay.hampay.common.core.model.request.ContactsHampayEnabledRequest;
import xyz.homapay.hampay.common.core.model.response.ContactsHampayEnabledResponse;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.Manifest;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.adapter.HamPayContactsAdapter;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestContactHampayEnabled;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.edittext.FacedEditText;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.dialog.permission.ActionPermission;
import xyz.homapay.hampay.mobile.android.dialog.permission.PermissionContactDialog;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.permission.PermissionListener;
import xyz.homapay.hampay.mobile.android.permission.RequestPermissions;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.UserContacts;

public class HamPayContactsActivity extends AppCompatActivity implements PermissionContactDialog.PermissionContactDialogListener{

    private ArrayList<PermissionListener> permissionListeners = new ArrayList<>();
    private String authToken;
    private Context context;
    private Activity activity;
    private ListView paymentRequestList;
    private SwipeRefreshLayout pullToRefresh;
    private List<ContactDTO> contacts;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private HamPayContactsAdapter hamPayContactsAdapter;
    private ContactsHampayEnabledRequest contactsHampayEnabledRequest;
    private RequestContactHampayEnabled requestContactHampayEnabled;
    private InputMethodManager inputMethodManager;
    private FacedEditText search_text;
    private HamPayDialog hamPayDialog;
    private String searchPhrase = "";
    private FacedTextView nullHampayContactsText;
    private final Handler handler = new Handler();

    public void backActionBar(View view){
        finish();
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
    public void onFinishEditDialog(ActionPermission actionPermission) {
        switch (actionPermission) {
            case GRANT:
                requestAndLoadUserContact();
                break;
            case DENY:
                contacts = new ArrayList<ContactDTO>();
                contactsHampayEnabledRequest.setContacts(contacts);
                requestContactHampayEnabled = new RequestContactHampayEnabled(activity, new RequestContactHampayEnabledTaskCompleteListener());
                requestContactHampayEnabled.execute(contactsHampayEnabledRequest);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        for (PermissionListener permissionListener : permissionListeners)
            if (permissionListener.onResult(requestCode, permissions, grantResults)) {
                permissionListeners.remove(permissionListener);
            }
    }


    private void requestAndLoadUserContact() {
        String[] permissions = new String[]{Manifest.permission.READ_CONTACTS};

        permissionListeners = new RequestPermissions().request(activity, Constants.READ_CONTACTS, permissions, new PermissionListener() {
            @Override
            public boolean onResult(int requestCode, String[] requestPermissions, int[] grantResults) {
                if (requestCode == Constants.READ_CONTACTS) {
                    // Check if the permission is correct and is granted
                    if (requestPermissions[0].equals(Manifest.permission.READ_CONTACTS) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        UserContacts userContacts = new UserContacts(context);
                        contacts = userContacts.read();
                        contactsHampayEnabledRequest.setContacts(contacts);
                        requestContactHampayEnabled = new RequestContactHampayEnabled(activity, new RequestContactHampayEnabledTaskCompleteListener());
                        requestContactHampayEnabled.execute(contactsHampayEnabledRequest);
                    } else {

                        handler.post(new Runnable() {
                            public void run() {
                                PermissionContactDialog permissionContactDialog = new PermissionContactDialog();
                                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                                fragmentTransaction.add(permissionContactDialog, null);
                                fragmentTransaction.commitAllowingStateLoss();
                            }
                        });
                    }

                    return true;
                }

                return false;
            }
        });
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
        nullHampayContactsText = (FacedTextView)findViewById(R.id.nullHampayContactsText);
        pullToRefresh = (SwipeRefreshLayout)findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                contactsHampayEnabledRequest = new ContactsHampayEnabledRequest();
                requestAndLoadUserContact();
            }
        });
        paymentRequestList = (ListView)findViewById(R.id.paymentRequestList);
        inputMethodManager = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        search_text = (FacedEditText)findViewById(R.id.search_text);
        search_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchPhrase = search_text.getText().toString();
                List<ContactDTO> searchContacts = new ArrayList<>();
                if (contacts != null) {
                    for (ContactDTO contact : contacts) {
                        if (searchPhrase.length() == 0 || searchPhrase.length() == 1) {
                            if (contact.getDisplayName().startsWith(searchPhrase)) {
                                searchContacts.add(contact);
                            }
                        } else if (searchPhrase.length() > 1) {
                            if (contact.getDisplayName().contains(searchPhrase)) {
                                searchContacts.add(contact);
                            }
                        }
                    }
                    hamPayContactsAdapter = new HamPayContactsAdapter(activity, searchContacts, authToken);
                    paymentRequestList.setAdapter(hamPayContactsAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        search_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    inputMethodManager.hideSoftInputFromWindow(search_text.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
        editor.commit();


        paymentRequestList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(activity, PaymentRequestDetailActivity.class);
                intent.putExtra(Constants.HAMPAY_CONTACT, contacts.get(position));
                startActivityForResult(intent, 1024);
            }
        });

        contactsHampayEnabledRequest = new ContactsHampayEnabledRequest();
        requestAndLoadUserContact();
    }


    public class RequestContactHampayEnabledTaskCompleteListener implements
            AsyncTaskCompleteListener<ResponseMessage<ContactsHampayEnabledResponse>> {
        @Override
        public void onTaskComplete(ResponseMessage<ContactsHampayEnabledResponse> contactsHampayEnabledResponseMessage) {

            hamPayDialog.dismisWaitingDialog();
            pullToRefresh.setRefreshing(false);

            if (contactsHampayEnabledResponseMessage != null){
                if (contactsHampayEnabledResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS){
                    contacts = contactsHampayEnabledResponseMessage.getService().getContacts();
                    if (contacts.size() == 0){
                        nullHampayContactsText.setVisibility(View.VISIBLE);
                        paymentRequestList.setVisibility(View.GONE);
                    }else {
                        nullHampayContactsText.setVisibility(View.GONE);
                        hamPayContactsAdapter = new HamPayContactsAdapter(activity, contacts, authToken);
                        paymentRequestList.setAdapter(hamPayContactsAdapter);
                    }
                }else if (contactsHampayEnabledResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE){
                    forceLogout();
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
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
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

}



