package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.List;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.dto.ContactDTO;
import xyz.homapay.hampay.common.core.model.request.ContactsHampayEnabledRequest;
import xyz.homapay.hampay.common.core.model.request.GetUserIdTokenRequest;
import xyz.homapay.hampay.common.core.model.response.ContactsHampayEnabledResponse;
import xyz.homapay.hampay.common.core.model.response.GetUserIdTokenResponse;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.Helper.DatabaseHelper;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.adapter.PayOneAdapter;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestContactHampayEnabled;
import xyz.homapay.hampay.mobile.android.async.RequestUserIdToken;
import xyz.homapay.hampay.mobile.android.component.edittext.FacedEditText;
import xyz.homapay.hampay.mobile.android.component.sectionlist.PinnedHeaderListView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.model.EnabledHamPay;
import xyz.homapay.hampay.mobile.android.model.RecentPay;
import xyz.homapay.hampay.mobile.android.util.Constants;

/**
 * Created by amir on 6/5/15.
 */
public class PayToOneActivity extends AppCompatActivity {

    static DatabaseHelper dbHelper;
    static List<RecentPay> recentPays;
    static List<EnabledHamPay> enabledHamPays;
    List<RecentPay> searchRecentPays;
    List<EnabledHamPay> searchEnabledHamPay;

    HamPayDialog hamPayDialog;
    List<ContactDTO> contactDTOs;

    static PinnedHeaderListView pinnedHeaderListView;

    FacedEditText searchPhraseText;

    ImageView searchImage;

    InputMethodManager inputMethodManager;

    boolean searchEnabled = false;

    static Activity context;

    static PayOneAdapter payOneAdapter;

    ContactsHampayEnabledRequest contactsHampayEnabledRequest;
    RequestContactHampayEnabled requestContactHampayEnabled;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    Tracker hamPayGaTracker;

    GetUserIdTokenRequest getUserIdTokenRequest;
    RequestUserIdToken requestUserIdToken;

    String serverKey = "";

    public PayToOneActivity() {
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void backActionBar(View view){
        finish();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pay_to_one);

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();


        searchRecentPays = new ArrayList<RecentPay>();
        searchEnabledHamPay = new ArrayList<EnabledHamPay>();

        hamPayGaTracker = ((HamPayApplication) getApplication())
                .getTracker(HamPayApplication.TrackerName.APP_TRACKER);

        inputMethodManager = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);

        context = PayToOneActivity.this;

        hamPayDialog = new HamPayDialog(context);
        hamPayDialog.showWaitingdDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));

        searchPhraseText = (FacedEditText)findViewById(R.id.searchPhraseText);

        searchPhraseText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (count == 0) {
                    searchEnabled = false;
                    performPayToOneSearch("", searchEnabled);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchPhraseText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchEnabled = true;
                    performPayToOneSearch(searchPhraseText.getText().toString(), searchEnabled);
                    return true;
                }
                return false;
            }
        });

        searchImage = (ImageView)findViewById(R.id.searchImage);
        searchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchPhraseText.getText().toString().length() > 0) {
                    searchEnabled = true;
                    performPayToOneSearch(searchPhraseText.getText().toString(), searchEnabled);
                }
            }
        });

        pinnedHeaderListView = (PinnedHeaderListView)findViewById(R.id.pinnedListView);


        if ((prefs.getString(Constants.USER_ID_TOKEN, "") != null && prefs.getString(Constants.USER_ID_TOKEN, "").length() == 16)){

            serverKey = prefs.getString(Constants.USER_ID_TOKEN, "");
            dbHelper = new DatabaseHelper(context, serverKey);

            recentPays = dbHelper.getAllRecentPays();
            enabledHamPays = dbHelper.getAllEnabledHamPay();

            if (enabledHamPays.size() > 0) {
                if (enabledHamPays.size() > 0) {
                    payOneAdapter = new PayOneAdapter(context,
                            recentPays,
                            enabledHamPays,
                            prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
                    pinnedHeaderListView.setAdapter(payOneAdapter);
                }
                hamPayDialog.dismisWaitingDialog();
            }

            if ((System.currentTimeMillis() - prefs.getLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis()) > Constants.MOBILE_TIME_OUT_INTERVAL)) {
                Intent intent = new Intent();
                intent.setClass(context, HamPayLoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivity(intent);
            }else {
                editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                editor.commit();

                if (!prefs.getBoolean(Constants.FETCHED_HAMPAY_ENABLED, false)) {
                    contactsHampayEnabledRequest = new ContactsHampayEnabledRequest();
                    requestContactHampayEnabled = new RequestContactHampayEnabled(context, new RequestContactHampayEnabledTaskCompleteListener());
                    requestContactHampayEnabled.execute(contactsHampayEnabledRequest);
                }
            }

        }else {
            recentPays = new ArrayList<RecentPay>();
            enabledHamPays = new ArrayList<EnabledHamPay>();
            getUserIdTokenRequest = new GetUserIdTokenRequest();
            requestUserIdToken = new RequestUserIdToken(context, new RequestGetUserIdTokenResponseTaskCompleteListener());
            requestUserIdToken.execute(getUserIdTokenRequest);
        }
    }



    @Override
    public void onStop() {
        super.onStop();
        if (requestContactHampayEnabled != null){
            if (!requestContactHampayEnabled.isCancelled())
                requestContactHampayEnabled.cancel(true);
        }
    }

    private void performPayToOneSearch(String searchPhrase, boolean searchEnabled){

//        inputMethodManager.hideSoftInputFromWindow(searchPhraseText.getWindowToken(), 0);


        if (searchEnabled) {

            searchRecentPays.clear();
            searchEnabledHamPay.clear();

            for (RecentPay recentPay : recentPays) {
                if (recentPay.getName().toLowerCase().contains(searchPhrase.toLowerCase())
                        || recentPay.getPhone().toLowerCase().contains(searchPhrase.toLowerCase())) {
                    searchRecentPays.add(recentPay);
                }
            }



            for (EnabledHamPay enabledHamPay : enabledHamPays) {
                if (enabledHamPay.getDisplayName().toLowerCase().contains(searchPhrase.toLowerCase())
                        || enabledHamPay.getCellNumber().toLowerCase().contains(searchPhrase.toLowerCase())) {
                    searchEnabledHamPay.add(enabledHamPay);
                }
            }

            if (searchRecentPays.size() == 0 && searchEnabledHamPay.size() == 0){
                (new HamPayDialog(context)).showNoResultSearchDialog();
            }else {
                payOneAdapter = new PayOneAdapter(context,
                        searchRecentPays,
                        searchEnabledHamPay,
                        prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
                pinnedHeaderListView.setAdapter(payOneAdapter);
            }

        }else {

            payOneAdapter = new PayOneAdapter(context,
                    recentPays,
                    enabledHamPays,
                    prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
            pinnedHeaderListView.setAdapter(payOneAdapter);

        }

    }


    public class RequestContactHampayEnabledTaskCompleteListener implements
            AsyncTaskCompleteListener<ResponseMessage<ContactsHampayEnabledResponse>> {
        @Override
        public void onTaskComplete(ResponseMessage<ContactsHampayEnabledResponse> contactsHampayEnabledResponseMessage) {

            if (contactsHampayEnabledResponseMessage != null){
                if (contactsHampayEnabledResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS){
                    contactDTOs = contactsHampayEnabledResponseMessage.getService().getContacts();

                    dbHelper = new DatabaseHelper(context, serverKey);

                    if (contactDTOs.size() > 0) {

                        recentPays = dbHelper.getAllRecentPays();
                        enabledHamPays = dbHelper.getAllEnabledHamPay();

                        dbHelper.deleteEnabledHamPays();

                        for (ContactDTO contactDTO : contactDTOs) {

                            EnabledHamPay enabledHamPay = new EnabledHamPay();
                            enabledHamPay.setCellNumber(contactDTO.getCellNumber());
                            enabledHamPay.setDisplayName(contactDTO.getDisplayName());
                            enabledHamPay.setPhotoId(contactDTO.getContactImageId());
                            dbHelper.createEnabledHamPay(enabledHamPay);

                        }

                        editor.putBoolean(Constants.FETCHED_HAMPAY_ENABLED, true);
                        editor.commit();
                    }

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Contacts Hampay Enabled")
                            .setAction("Fetch")
                            .setLabel("Success")
                            .build());
                }
                else {
                    requestContactHampayEnabled = new RequestContactHampayEnabled(context, new RequestContactHampayEnabledTaskCompleteListener());
                    new HamPayDialog(context).showFailContactsHamPayEnabledDialog(requestContactHampayEnabled, contactsHampayEnabledRequest,
                            contactsHampayEnabledResponseMessage.getService().getResultStatus().getCode(),
                            contactsHampayEnabledResponseMessage.getService().getResultStatus().getDescription());

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Contacts Hampay Enabled")
                            .setAction("Fetch")
                            .setLabel("Fail(Server)")
                            .build());
                }
            }else {
                requestContactHampayEnabled = new RequestContactHampayEnabled(context, new RequestContactHampayEnabledTaskCompleteListener());
                new HamPayDialog(context).showFailContactsHamPayEnabledDialog(requestContactHampayEnabled, contactsHampayEnabledRequest,
                        Constants.LOCAL_ERROR_CODE,
                        getString(R.string.msg_fail_contacts_enabled));

                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Contacts Hampay Enabled")
                        .setAction("Fetch")
                        .setLabel("Fail(Mobile)")
                        .build());
            }

//                if (payOneAdapter.getCount() == 0) {
            enabledHamPays = dbHelper.getAllEnabledHamPay();
            payOneAdapter = new PayOneAdapter(context,
                    recentPays,
                    enabledHamPays,
                    prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
            pinnedHeaderListView.setAdapter(payOneAdapter);
//                }

            hamPayDialog.dismisWaitingDialog();
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

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Get User Id Token")
                            .setAction("Get")
                            .setLabel("Success")
                            .build());

                    if ((System.currentTimeMillis() - prefs.getLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis()) > Constants.MOBILE_TIME_OUT_INTERVAL)) {
                        Intent intent = new Intent();
                        intent.setClass(context, HamPayLoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        startActivity(intent);
                    }else {
                        editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                        editor.commit();

//                        if (!prefs.getBoolean(Constants.FETCHED_HAMPAY_ENABLED, false)) {
                        contactsHampayEnabledRequest = new ContactsHampayEnabledRequest();
                        requestContactHampayEnabled = new RequestContactHampayEnabled(context, new RequestContactHampayEnabledTaskCompleteListener());
                        requestContactHampayEnabled.execute(contactsHampayEnabledRequest);
//                        }
                    }

                }else {
                    requestUserIdToken = new RequestUserIdToken(context, new RequestGetUserIdTokenResponseTaskCompleteListener());
                    new HamPayDialog(context).showFailGetUserIdTokenDialog(requestUserIdToken, getUserIdTokenRequest,
                            registrationGetUserIdTokenResponseMessage.getService().getResultStatus().getCode(),
                            registrationGetUserIdTokenResponseMessage.getService().getResultStatus().getDescription());

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Get User Id Token")
                            .setAction("Get")
                            .setLabel("Fail(Server)")
                            .build());
                }
            }else {
                requestUserIdToken = new RequestUserIdToken(context, new RequestGetUserIdTokenResponseTaskCompleteListener());
                new HamPayDialog(context).showFailGetUserIdTokenDialog(requestUserIdToken, getUserIdTokenRequest,
                        Constants.LOCAL_ERROR_CODE,
                        getString(R.string.msg_fail_server_key));

                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Get User Id Token")
                        .setAction("Get")
                        .setLabel("Fail(Mobile)")
                        .build());
            }
        }

        @Override
        public void onTaskPreRun() {   }
    }
}
