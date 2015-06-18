package com.hampay.mobile.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.response.BusinessListResponse;
import com.hampay.common.core.model.response.ContactsHampayEnabledResponse;
import com.hampay.mobile.android.Helper.DatabaseHelper;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.activity.PayBusinessActivity;
import com.hampay.mobile.android.activity.PayOneActivity;
import com.hampay.mobile.android.adapter.HamPayBusinessAdapter;
import com.hampay.mobile.android.adapter.HamPayContactAdapter;
import com.hampay.mobile.android.adapter.RecentPayOneAdapter;
import com.hampay.mobile.android.model.RecentPay;
import com.hampay.mobile.android.webservice.WebServices;

import java.util.List;

/**
 * Created by amir on 6/5/15.
 */
public class PayToBusinessFragment extends Fragment {


    private ListView businessListView;
    private HamPayBusinessAdapter hamPayBusinessAdapter;

    public PayToBusinessFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pay_to_business, container, false);

        new HttpHamPay‌Business().execute();

        businessListView = (ListView)rootView.findViewById(R.id.businessListView);


        businessListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), PayBusinessActivity.class);
                intent.putExtra("business_name", businessListResponse.getService().getBusinesses().get(position).getTitle());
                intent.putExtra("business_code", businessListResponse.getService().getBusinesses().get(position).getCode());
                startActivity(intent);

            }
        });

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


    private ResponseMessage<BusinessListResponse> businessListResponse;

    public class HttpHamPay‌Business extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... params) {

            WebServices webServices = new WebServices(getActivity());
            businessListResponse = webServices.getHamPayBusiness();

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (businessListResponse != null) {

                hamPayBusinessAdapter = new HamPayBusinessAdapter(getActivity(), businessListResponse.getService().getBusinesses());

                businessListView.setAdapter(hamPayBusinessAdapter);

            }


        }
    }

}
