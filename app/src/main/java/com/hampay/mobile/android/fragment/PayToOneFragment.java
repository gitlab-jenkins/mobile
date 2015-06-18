package com.hampay.mobile.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.dto.ContactDTO;
import com.hampay.common.core.model.response.ContactsHampayEnabledResponse;
import com.hampay.common.core.model.response.UserProfileResponse;
import com.hampay.mobile.android.Helper.DatabaseHelper;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.activity.PayOneActivity;
import com.hampay.mobile.android.adapter.HamPayContactAdapter;
import com.hampay.mobile.android.adapter.RecentPayOneAdapter;
import com.hampay.mobile.android.model.RecentPay;
import com.hampay.mobile.android.webservice.WebServices;

import java.util.List;

/**
 * Created by amir on 6/5/15.
 */
public class PayToOneFragment extends Fragment {

    DatabaseHelper dbHelper;
    List<RecentPay> recentPays;
    ListView recent_payListView;
    RecentPayOneAdapter recentPayOneAdapter;
    HamPayContactAdapter hamPayContactAdapter;

    ResponseMessage<ContactsHampayEnabledResponse> contactsHampayEnabledResponse;

    public PayToOneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new DatabaseHelper(getActivity());
        RecentPay recentPay = new RecentPay();
        recentPay.setName("امیر");
        recentPay.setPhone("۰۹۱۲۶۱۵۷۹۰۵");
        recentPay.setMessage("مرامی پول ریختم");
        recentPay.setStatus("واریز");

        dbHelper.createRecentPAy(recentPay);

        recentPays = dbHelper.getAllRecentPays();

//        for (RecentPay pay : recentPays){
//            Log.e("PAY", pay.getId() + ": " + pay.getName());
//        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pay_to_one, container, false);

        new HttpHamPayContact().execute();

        recent_payListView = (ListView)rootView.findViewById(R.id.recent_payListView);

        recent_payListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), PayOneActivity.class);
                intent.putExtra("contact_name", contactsHampayEnabledResponse.getService().getContacts().get(position).getDisplayName());
                intent.putExtra("contact_phone_no", contactsHampayEnabledResponse.getService().getContacts().get(position).getCellNumber());
                startActivity(intent);
            }
        });



//
//        recentPayOneAdapter = new RecentPayOneAdapter(getActivity(), recentPays);
//
//        recent_payListView.setAdapter(recentPayOneAdapter);

        // Inflate the layout for this fragment
        return rootView;
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

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (contactsHampayEnabledResponse != null) {

                hamPayContactAdapter = new HamPayContactAdapter(getActivity(), contactsHampayEnabledResponse.getService().getContacts());

                recent_payListView.setAdapter(hamPayContactAdapter);

            }


        }
    }

}
