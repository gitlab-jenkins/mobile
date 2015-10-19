package xyz.homapay.hampay.mobile.android.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.dto.ContactDTO;
import xyz.homapay.hampay.common.core.model.request.ContactsHampayEnabledRequest;
import xyz.homapay.hampay.common.core.model.request.GetUserIdTokenRequest;
import xyz.homapay.hampay.common.core.model.response.ContactsHampayEnabledResponse;
import xyz.homapay.hampay.common.core.model.response.GetUserIdTokenResponse;
import xyz.homapay.hampay.common.core.model.response.RegistrationEntryResponse;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.Helper.DatabaseHelper;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.activity.HamPayLoginActivity;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amir on 6/5/15.
 */
public class PayToOneFragment extends Fragment {

    static DatabaseHelper dbHelper;
    static List<RecentPay> recentPays;
    static List<EnabledHamPay> enabledHamPays;
    List<RecentPay> searchRecentPays;
    List<EnabledHamPay> searchEnabledHamPay;

    HamPayDialog hamPayDialog;

    //    ResponseMessage<ContactsHampayEnabledResponse> contactsHampayEnabledResponse;
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

    public PayToOneFragment() {
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        prefs = getActivity().getSharedPreferences(Constants.APP_PREFERENCE_NAME, getActivity().MODE_PRIVATE);
        editor = getActivity().getSharedPreferences(Constants.APP_PREFERENCE_NAME, getActivity().MODE_PRIVATE).edit();


        searchRecentPays = new ArrayList<RecentPay>();
        searchEnabledHamPay = new ArrayList<EnabledHamPay>();

        hamPayGaTracker = ((HamPayApplication) getActivity().getApplication())
                .getTracker(HamPayApplication.TrackerName.APP_TRACKER);

//        for (RecentPay pay : recentPays){
//            Log.e("PAY", pay.getId() + ": " + pay.getName());
//        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pay_to_one, container, false);

        inputMethodManager = (InputMethodManager)getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);

        context = getActivity();

        hamPayDialog = new HamPayDialog(getActivity());
        hamPayDialog.showWaitingdDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));

        searchPhraseText = (FacedEditText)rootView.findViewById(R.id.searchPhraseText);

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

        searchImage = (ImageView)rootView.findViewById(R.id.searchImage);
        searchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchPhraseText.getText().toString().length() > 0) {
                    searchEnabled = true;
                    performPayToOneSearch(searchPhraseText.getText().toString(), searchEnabled);
                }
            }
        });

        pinnedHeaderListView = (PinnedHeaderListView)rootView.findViewById(R.id.pinnedListView);
//        pinnedHeaderListView.setOnItemClickListener(new PinnedHeaderListView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int section, int position, long id) {
//
//                if (searchEnabled){
//
//                    if (section == 0) {
//                        Intent intent = new Intent();
//                        intent.setClass(getActivity(), PayOneActivity.class);
//                        intent.putExtra("contact_name", searchRecentPays.get(position).getName());
//                        intent.putExtra("contact_phone_no", searchRecentPays.get(position).getPhone());
//                        startActivity(intent);
//                    } else if (section == 1) {
//                        Intent intent = new Intent();
//                        intent.setClass(getActivity(), PayOneActivity.class);
//                        intent.putExtra("contact_name", searchContactDTOs.get(position).getDisplayName());
//                        intent.putExtra("contact_phone_no", searchContactDTOs.get(position).getCellNumber());
//                        startActivity(intent);
//                    }
//
//                }else {
//
//                    if (section == 0) {
//                        Intent intent = new Intent();
//                        intent.setClass(getActivity(), PayOneActivity.class);
//                        intent.putExtra("contact_name", recentPays.get(position).getName());
//                        intent.putExtra("contact_phone_no", recentPays.get(position).getPhone());
//                        startActivity(intent);
//                    } else if (section == 1) {
//                        Intent intent = new Intent();
//                        intent.setClass(getActivity(), PayOneActivity.class);
//                        intent.putExtra("contact_name", contactsHampayEnabledResponse.getService().getContacts().get(position).getDisplayName());
//                        intent.putExtra("contact_phone_no", contactsHampayEnabledResponse.getService().getContacts().get(position).getCellNumber());
//                        startActivity(intent);
//                    }
//                }
//
//            }
//
//            @Override
//            public void onSectionClick(AdapterView<?> adapterView, View view, int section, long id) {
//
//            }
//        });

        if ((prefs.getString(Constants.USER_ID_TOKEN, "") != null && prefs.getString(Constants.USER_ID_TOKEN, "").length() == 16)){

            serverKey = prefs.getString(Constants.USER_ID_TOKEN, "");
            dbHelper = new DatabaseHelper(getActivity(), serverKey);

            recentPays = dbHelper.getAllRecentPays();
            enabledHamPays = dbHelper.getAllEnabledHamPay();

            if (enabledHamPays.size() > 0) {
                if (enabledHamPays.size() > 0) {
                    payOneAdapter = new PayOneAdapter(getActivity(),
                            recentPays,
                            enabledHamPays,
                            prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
                    pinnedHeaderListView.setAdapter(payOneAdapter);
                }
                hamPayDialog.dismisWaitingDialog();
            }

            if ((System.currentTimeMillis() - prefs.getLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis()) > Constants.MOBILE_TIME_OUT_INTERVAL)) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), HamPayLoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getActivity().finish();
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




        return rootView;
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

        inputMethodManager.hideSoftInputFromWindow(searchPhraseText.getWindowToken(), 0);


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
                (new HamPayDialog(getActivity())).showNoResultSearchDialog();
            }else {
                payOneAdapter = new PayOneAdapter(getActivity(),
                        searchRecentPays,
                        searchEnabledHamPay,
                        prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
                pinnedHeaderListView.setAdapter(payOneAdapter);
            }

        }else {

            payOneAdapter = new PayOneAdapter(getActivity(),
                    recentPays,
                    enabledHamPays,
                    prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
            pinnedHeaderListView.setAdapter(payOneAdapter);

        }

    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


//    ResponseMessage<ContactsHampayEnabledResponse> contactsHampayEnabledResponse;
//
//    public class HttpHamPayContact extends AsyncTask<Void, Void, String> {
//
//        @Override
//        protected String doInBackground(Void... params) {
//
//            WebServices webServices = new WebServices(getActivity());
//            contactsHampayEnabledResponse = webServices.getEnabledHamPayContacts();
//
//            if (contactsHampayEnabledResponse != null){
//
//                if (contactsHampayEnabledResponse.getService().getResultStatus() == ResultStatus.SUCCESS) {
//
//                    contactDTOs = webServices.getEnabledHamPayContacts().getService().getContacts();
//
//                    if (contactDTOs.size() > 0) {
//
//                        ContentResolver resolver = getActivity().getContentResolver();
//                        resolver.delete(ContactsContract.RawContacts.CONTENT_URI, ContactsContract.RawContacts.ACCOUNT_TYPE + " = ?", new String[]{AccountGeneral.ACCOUNT_TYPE});
//
//                        dbHelper.deleteEnabledHamPays();
//
//                        for (ContactDTO contactDTO : contactDTOs) {
//
//                            EnabledHamPay enabledHamPay = new EnabledHamPay();
//                            enabledHamPay.setCellNumber(contactDTO.getCellNumber());
//                            enabledHamPay.setDisplayName(contactDTO.getDisplayName());
//
//                            dbHelper.createEnabledHamPay(enabledHamPay);
//
////                addNewAccount(AccountGeneral.ACCOUNT_TYPE, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS);
////                ContactsManager.addContact(getActivity(), new HamPayContact("",
////                        contactDTO.getDisplayName(),
////                        "",
////                        contactDTO.getCellNumber()));
////
////                Log.e("Create", contactDTO.getDisplayName());
//
//                        }
//
////                new RequestUpdateEnabledHamPay(context, new RequestUpdateEnableHamPayTaskCompleteListener()).execute(enabledHamPays);
//
//                    }
//                }
//            }
//
//
//            return null;
//
//
//        }
//
//
//
//
//        private void addNewAccount(String accountType, String authTokenType) {
//            final AccountManagerFuture<Bundle> future = AccountManager.get(getActivity())
//                    .addAccount(accountType, authTokenType, null, null, getActivity(), new AccountManagerCallback<Bundle>() {
//                        @Override
//                        public void run(AccountManagerFuture<Bundle> future) {
//                            try {
//                                Bundle bnd = future.getResult();
//                                Log.i("", "Account was created");
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }, null);
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//
//            if (isAdded()){
//
//
//                if (payOneAdapter == null){
//                    enabledHamPays = dbHelper.getAllEnabledHamPay();
//                    payOneAdapter = new PayOneAdapter(getActivity(),
//                            recentPays,
//                            enabledHamPays);
//                    pinnedHeaderListView.setAdapter(payOneAdapter);
//                }
//
////                enabledHamPays = dbHelper.getAllEnabledHamPay();
////
////                Log.e("Enabled Length:", enabledHamPays.size() + "");
////
////                for (EnabledHamPay enabledHamPay : enabledHamPays){
////                    Log.e("Display Name", enabledHamPay.getDisplayName());
////                }
//
////                if (enabledHamPays.size() > 0){
////                    PayOneAdapter sectionedAdapter = new PayOneAdapter(getActivity(),
////                            recentPays,
////                            enabledHamPays);
////                    pinnedHeaderListView.setAdapter(sectionedAdapter);
////                }
//
////                onResume = true;
//
////                new RequestUpdateEnabledHamPay(context, new RequestUpdateEnableHamPayTaskCompleteListener()).execute(enabledHamPays);
//
////                if (contactsHampayEnabledResponse != null) {
////
////                    PayOneAdapter sectionedAdapter = new PayOneAdapter(getActivity(),
////                            recentPays,
////                            contactsHampayEnabledResponse.getService().getContacts());
////                    pinnedHeaderListView.setAdapter(sectionedAdapter);
////
////                }
//
//
//            }
//        }
//    }
//



    public class RequestContactHampayEnabledTaskCompleteListener implements
            AsyncTaskCompleteListener<ResponseMessage<ContactsHampayEnabledResponse>> {
        @Override
        public void onTaskComplete(ResponseMessage<ContactsHampayEnabledResponse> contactsHampayEnabledResponseMessage) {

            if (contactsHampayEnabledResponseMessage != null){
                if (contactsHampayEnabledResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS){
                    contactDTOs = contactsHampayEnabledResponseMessage.getService().getContacts();

                    if (contactDTOs.size() > 0) {

                        dbHelper = new DatabaseHelper(context, serverKey);

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

            if (isAdded()) {

//                if (payOneAdapter == null) {
//                    enabledHamPays = dbHelper.getAllEnabledHamPay();
//                    payOneAdapter = new PayOneAdapter(getActivity(),
//                            recentPays,
//                            enabledHamPays,
//                            prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
//                    pinnedHeaderListView.setAdapter(payOneAdapter);
//                }
            }
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
                        intent.setClass(getActivity(), HamPayLoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        getActivity().finish();
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
                    new HamPayDialog(getActivity()).showFailGetUserIdTokenDialog(requestUserIdToken, getUserIdTokenRequest,
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
                new HamPayDialog(getActivity()).showFailGetUserIdTokenDialog(requestUserIdToken, getUserIdTokenRequest,
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
