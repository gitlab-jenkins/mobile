package com.hampay.mobile.android.fragment;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.dto.ContactDTO;
import com.hampay.common.core.model.response.ContactsHampayEnabledResponse;
import com.hampay.mobile.android.Helper.DatabaseHelper;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.account.AccountGeneral;
import com.hampay.mobile.android.account.ContactsManager;
import com.hampay.mobile.android.account.HamPayContact;
import com.hampay.mobile.android.activity.PayOneActivity;
import com.hampay.mobile.android.adapter.PayOneAdapter;
import com.hampay.mobile.android.component.edittext.FacedEditText;
import com.hampay.mobile.android.component.sectionlist.PinnedHeaderListView;
import com.hampay.mobile.android.model.RecentPay;
import com.hampay.mobile.android.webservice.WebServices;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amir on 6/5/15.
 */
public class PayToOneFragment extends Fragment {

    DatabaseHelper dbHelper;
    List<RecentPay> recentPays;
    List<RecentPay> searchRecentPays;
    List<ContactDTO> searchContactDTOs;
    RelativeLayout loading_rl;

    ResponseMessage<ContactsHampayEnabledResponse> contactsHampayEnabledResponse;

    PinnedHeaderListView pinnedHeaderListView;

    FacedEditText searchPhraseText;

    ImageView searchImage;

    InputMethodManager inputMethodManager;

    boolean searchEnabled = false;

    public PayToOneFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new DatabaseHelper(getActivity());

        recentPays = dbHelper.getAllRecentPays();

        searchRecentPays = new ArrayList<RecentPay>();
        searchContactDTOs = new ArrayList<ContactDTO>();

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

        new HttpHamPayContact().execute();

        loading_rl = (RelativeLayout)rootView.findViewById(R.id.loading_rl);

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
        pinnedHeaderListView.setOnItemClickListener(new PinnedHeaderListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int section, int position, long id) {

                if (searchEnabled){

                    if (section == 0) {
//                        Intent intent = new Intent();
//                        intent.setClass(getActivity(), PayOneActivity.class);
//                        intent.putExtra("contact_name", searchRecentPays.get(position).getName());
//                        intent.putExtra("contact_phone_no", searchRecentPays.get(position).getPhone());
//                        startActivity(intent);
                    } else if (section == 1) {
//                        Intent intent = new Intent();
//                        intent.setClass(getActivity(), PayOneActivity.class);
//                        intent.putExtra("contact_name", searchContactDTOs.get(position).getDisplayName());
//                        intent.putExtra("contact_phone_no", searchContactDTOs.get(position).getCellNumber());
//                        startActivity(intent);
                    }

                }else {

                    if (section == 0) {
//                        Intent intent = new Intent();
//                        intent.setClass(getActivity(), PayOneActivity.class);
//                        intent.putExtra("contact_name", recentPays.get(position).getName());
//                        intent.putExtra("contact_phone_no", recentPays.get(position).getPhone());
//                        startActivity(intent);
                    } else if (section == 1) {
//                        Intent intent = new Intent();
//                        intent.setClass(getActivity(), PayOneActivity.class);
//                        intent.putExtra("contact_name", contactsHampayEnabledResponse.getService().getContacts().get(position).getDisplayName());
//                        intent.putExtra("contact_phone_no", contactsHampayEnabledResponse.getService().getContacts().get(position).getCellNumber());
//                        startActivity(intent);
                    }
                }

            }

            @Override
            public void onSectionClick(AdapterView<?> adapterView, View view, int section, long id) {

            }
        });


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

//        recentPays = dbHelper.getAllRecentPays();

//        if (contactsHampayEnabledResponse != null && contactsHampayEnabledResponse.getService().getContacts().size() > 0){
//
//            PayOneAdapter sectionedAdapter = new PayOneAdapter(getActivity(),
//                    recentPays,
//                    contactsHampayEnabledResponse.getService().getContacts());
//            pinnedHeaderListView.setAdapter(sectionedAdapter);

//            loading_rl.setVisibility(View.GONE);
//        }
//        else {
//            new HttpHamPayContact().execute();
//        }

    }

    private void performPayToOneSearch(String searchPhrase, boolean searchEnabled){

        inputMethodManager.hideSoftInputFromWindow(searchPhraseText.getWindowToken(), 0);


        if (searchEnabled) {

            searchRecentPays.clear();
            searchContactDTOs.clear();

            for (RecentPay recentPay : recentPays) {
                if (recentPay.getName().contains(searchPhrase) || recentPay.getPhone().contains(searchPhrase)) {
                    searchRecentPays.add(recentPay);
                }
            }

            for (ContactDTO contactDTO : contactsHampayEnabledResponse.getService().getContacts()) {
                if (contactDTO.getDisplayName().contains(searchPhrase) || contactDTO.getCellNumber().contains(searchPhrase)) {
                    searchContactDTOs.add(contactDTO);
                }
            }

            PayOneAdapter sectionedAdapter = new PayOneAdapter(getActivity(),
                    searchRecentPays,
                    searchContactDTOs);
            pinnedHeaderListView.setAdapter(sectionedAdapter);

        }else {

            PayOneAdapter sectionedAdapter = new PayOneAdapter(getActivity(),
                    recentPays,
                    contactsHampayEnabledResponse.getService().getContacts());
            pinnedHeaderListView.setAdapter(sectionedAdapter);

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


    public class HttpHamPayContact extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            WebServices webServices = new WebServices(getActivity());
            contactsHampayEnabledResponse = webServices.getHamPayContacts();

            for (ContactDTO contactDTO : contactsHampayEnabledResponse.getService().getContacts()){

                addNewAccount(AccountGeneral.ACCOUNT_TYPE, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS);
                ContactsManager.addContact(getActivity(), new HamPayContact("",
                        contactDTO.getDisplayName(),
                        "",
                        contactDTO.getCellNumber()));

                Log.e("Create", contactDTO.getDisplayName());

            }

            return null;
        }

        private void addNewAccount(String accountType, String authTokenType) {
            final AccountManagerFuture<Bundle> future = AccountManager.get(getActivity())
                    .addAccount(accountType, authTokenType, null, null, getActivity(), new AccountManagerCallback<Bundle>() {
                @Override
                public void run(AccountManagerFuture<Bundle> future) {
                    try {
                        Bundle bnd = future.getResult();
                        Log.i("", "Account was created");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, null);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (contactsHampayEnabledResponse != null) {

                PayOneAdapter sectionedAdapter = new PayOneAdapter(getActivity(),
                        recentPays,
                        contactsHampayEnabledResponse.getService().getContacts());
                pinnedHeaderListView.setAdapter(sectionedAdapter);


                loading_rl.setVisibility(View.GONE);

            }


        }
    }

}
