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
import android.widget.Toast;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.dto.ContactDTO;
import com.hampay.common.core.model.response.TransactionListResponse;
import com.hampay.common.core.model.response.UserProfileResponse;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.activity.TransactionDetailActivity;
import com.hampay.mobile.android.adapter.TransactionAdapter;
import com.hampay.mobile.android.webservice.WebServices;

import java.util.List;

/**
 * Created by amir on 6/5/15.
 */
public class TransactionFragment extends Fragment {

    ListView transationListView;
    TransactionAdapter transactionAdapter;

    public static ResponseMessage<TransactionListResponse> transactionListResponse;

    public TransactionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_transaction, container, false);


        transationListView = (ListView)rootView.findViewById(R.id.transationListView);


        transationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), TransactionDetailActivity.class);
                intent.putExtra("index", position);
                startActivity(intent);
            }
        });

        new HttpUserTransaction().execute();

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


    public class HttpUserTransaction extends AsyncTask<Void, Void, String> {



        @Override
        protected String doInBackground(Void... params) {

            WebServices webServices = new WebServices();
            //webServices.testBankList1();
            transactionListResponse = webServices.getUserTransaction();

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            transactionAdapter = new TransactionAdapter(getActivity(), transactionListResponse);

            transationListView.setAdapter(transactionAdapter);

        }
    }
}
