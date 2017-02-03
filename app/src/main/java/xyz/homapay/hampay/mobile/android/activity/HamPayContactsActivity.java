package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
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
import xyz.homapay.hampay.mobile.android.component.edittext.FacedEditText;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.dialog.permission.ActionPermission;
import xyz.homapay.hampay.mobile.android.dialog.permission.PermissionContactDialog;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.permission.PermissionListener;
import xyz.homapay.hampay.mobile.android.permission.RequestPermissions;
import xyz.homapay.hampay.mobile.android.util.AppManager;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.UserContacts;

public class HamPayContactsActivity extends AppCompatActivity implements PermissionContactDialog.PermissionContactDialogListener {

    private final Handler handler = new Handler();
    @BindView(R.id.paymentRequestList)
    ListView paymentRequestList;
    @BindView(R.id.pullToRefresh)
    SwipeRefreshLayout pullToRefresh;
    @BindView(R.id.search_text)
    FacedEditText search_text;
    @BindView(R.id.hampayContactShare)
    LinearLayout hampayContactShare;
    private ArrayList<PermissionListener> permissionListeners = new ArrayList<>();
    private String authToken;
    private Context context;
    private Activity activity;
    private List<ContactDTO> contacts;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private HamPayContactsAdapter hamPayContactsAdapter;
    private ContactsHampayEnabledRequest contactsHampayEnabledRequest;
    private RequestContactHampayEnabled requestContactHampayEnabled;
    private InputMethodManager inputMethodManager;
    private HamPayDialog hamPayDialog;
    private String searchPhrase = "";
    List<ContactDTO> searchContacts = new ArrayList<>();

    public void backActionBar(View view) {
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
                finish();
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

        permissionListeners = new RequestPermissions().request(activity, Constants.READ_CONTACTS, permissions, (requestCode, requestPermissions, grantResults) -> {
            if (requestCode == Constants.READ_CONTACTS) {
                if (grantResults.length > 0 && requestPermissions[0].equals(Manifest.permission.READ_CONTACTS) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    UserContacts userContacts = new UserContacts(context);
                    contacts = userContacts.read();
                    contactsHampayEnabledRequest.setContacts(contacts);
                    requestContactHampayEnabled = new RequestContactHampayEnabled(activity, new RequestContactHampayEnabledTaskCompleteListener());
                    requestContactHampayEnabled.execute(contactsHampayEnabledRequest);
                } else {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS);
                        if (showRationale) {
                            handler.post(() -> {
                                PermissionContactDialog permissionContactDialog = new PermissionContactDialog();
                                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                                fragmentTransaction.add(permissionContactDialog, null);
                                fragmentTransaction.commitAllowingStateLoss();
                            });
                        } else {
                            contacts = new ArrayList<>();
                            contactsHampayEnabledRequest.setContacts(contacts);
                            requestContactHampayEnabled = new RequestContactHampayEnabled(activity, new RequestContactHampayEnabledTaskCompleteListener());
                            requestContactHampayEnabled.execute(contactsHampayEnabledRequest);
                        }
                    } else {
                        handler.post(() -> {
                            PermissionContactDialog permissionContactDialog = new PermissionContactDialog();
                            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.add(permissionContactDialog, null);
                            fragmentTransaction.commitAllowingStateLoss();
                        });
                    }
                }
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hampay_contacts);
        ButterKnife.bind(this);

        context = this;
        activity = HamPayContactsActivity.this;
        hamPayDialog = new HamPayDialog(activity);
        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
        authToken = prefs.getString(Constants.LOGIN_TOKEN_ID, "");
        hampayContactShare.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.hampay_share_text) + "https://play.google.com/store/apps/details?id=" + getPackageName());
            startActivity(Intent.createChooser(intent, getString(R.string.app_name)));
        });
        pullToRefresh.setOnRefreshListener(() -> {
            contactsHampayEnabledRequest = new ContactsHampayEnabledRequest();
            requestAndLoadUserContact();
        });
        View footerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.hampay_contact_footer, null, false);
        paymentRequestList.removeFooterView(footerView);
        paymentRequestList.addFooterView(footerView, null, false);
        inputMethodManager = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        search_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchPhrase = s.toString();
                searchContacts = new ArrayList<>();
                if (contacts != null) {
                    for (ContactDTO contact : contacts) {
                        if (searchPhrase.length() == 0 || searchPhrase.length() == 1) {
                            if (contact.getDisplayName().toLowerCase().startsWith(searchPhrase.toLowerCase())) {
                                searchContacts.add(contact);
                            }
                        } else if (searchPhrase.length() > 1) {
                            if (contact.getDisplayName().toLowerCase().contains(searchPhrase.toLowerCase())) {
                                searchContacts.add(contact);
                            }
                        }
                    }
                    hamPayContactsAdapter = new HamPayContactsAdapter(activity, searchContacts, authToken);
                    paymentRequestList.setAdapter(hamPayContactsAdapter);
                }
            }
        });

        search_text.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                inputMethodManager.hideSoftInputFromWindow(search_text.getWindowToken(), 0);
                return true;
            }
            return false;
        });

        AppManager.setMobileTimeout(context);
        editor.commit();


        paymentRequestList.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent();
            intent.setClass(activity, PaymentRequestDetailActivity.class);
            if (search_text.getText().toString().length() > 0){
                intent.putExtra(Constants.HAMPAY_CONTACT, searchContacts.get(position));
            }else {
                intent.putExtra(Constants.HAMPAY_CONTACT, contacts.get(position));
            }
            startActivityForResult(intent, 1024);
        });

        contactsHampayEnabledRequest = new ContactsHampayEnabledRequest();
        requestAndLoadUserContact();
    }

    @Override
    protected void onPause() {
        super.onPause();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(search_text.getWindowToken(), 0);
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

    public class RequestContactHampayEnabledTaskCompleteListener implements
            AsyncTaskCompleteListener<ResponseMessage<ContactsHampayEnabledResponse>> {
        @Override
        public void onTaskComplete(ResponseMessage<ContactsHampayEnabledResponse> contactsHampayEnabledResponseMessage) {

            hamPayDialog.dismisWaitingDialog();
            pullToRefresh.setRefreshing(false);

            if (contactsHampayEnabledResponseMessage != null) {
                if (contactsHampayEnabledResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {

                    contacts = contactsHampayEnabledResponseMessage.getService().getContacts();
                    if (contacts.size() == 0) {
                        paymentRequestList.setVisibility(View.GONE);
                    } else {
                        hamPayContactsAdapter = new HamPayContactsAdapter(activity, contacts, authToken);
                        paymentRequestList.setAdapter(hamPayContactsAdapter);
                    }
                } else if (contactsHampayEnabledResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    forceLogout();
                } else {
                    requestContactHampayEnabled = new RequestContactHampayEnabled(context, new RequestContactHampayEnabledTaskCompleteListener());
                    new HamPayDialog(activity).showFailContactsHamPayEnabledDialog(requestContactHampayEnabled, contactsHampayEnabledRequest,
                            contactsHampayEnabledResponseMessage.getService().getResultStatus().getCode(),
                            contactsHampayEnabledResponseMessage.getService().getResultStatus().getDescription());

                }
            } else {
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

}



