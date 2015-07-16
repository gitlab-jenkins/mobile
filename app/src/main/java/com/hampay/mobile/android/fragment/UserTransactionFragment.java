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
import android.widget.RelativeLayout;

import com.hampay.common.core.model.response.dto.TransactionDTO;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.activity.TransactionDetailActivity;
import com.hampay.mobile.android.adapter.UserTransactionAdapter;
import com.hampay.mobile.android.component.doblist.DobList;
import com.hampay.mobile.android.component.doblist.events.OnLoadMoreListener;
import com.hampay.mobile.android.component.doblist.exceptions.NoEmptyViewException;
import com.hampay.mobile.android.component.doblist.exceptions.NoListviewException;
import com.hampay.mobile.android.webservice.WebServices;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amir on 6/5/15.
 */
public class UserTransactionFragment extends Fragment {

    View rootView;

    ListView transationListView;
    UserTransactionAdapter userTransactionAdapter;
    private boolean FINISHED_SCROLLING = false;
    RelativeLayout loading_rl;

    public UserTransactionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        transactionDTOs = new ArrayList<TransactionDTO>();
        userTransactionAdapter = new UserTransactionAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_transaction, container, false);

        loading_rl = (RelativeLayout)rootView.findViewById(R.id.loading_rl);

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


    private boolean onLoadMore = false;
    private boolean searchEnable = false;

    public static List<TransactionDTO> transactionDTOs;

    public class HttpUserTransaction extends AsyncTask<Void, Void, String> {


        List<TransactionDTO> newTransactionDTOs;

        @Override
        protected String doInBackground(Void... params) {

            WebServices webServices = new WebServices();
            newTransactionDTOs = webServices.getUserTransaction().getService().getTransactions();
            transactionDTOs.addAll(newTransactionDTOs);

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            loading_rl.setVisibility(View.GONE);

            if (isAdded()) {

                if (transactionDTOs != null) {

                    if (onLoadMore) {
                        if (newTransactionDTOs != null)
                            addDummyData(newTransactionDTOs.size());
                    } else {
                        initDobList(rootView, transationListView);
                        transationListView.setAdapter(userTransactionAdapter);
                    }
                }

            }

        }
    }


    DobList dobList;

    private void initDobList(View rootView, ListView listView) {

        dobList = new DobList();
        try {

            dobList.register(listView);

            dobList.addDefaultLoadingFooterView();

            View noItems = rootView.findViewById(R.id.noItems);
            dobList.setEmptyView(noItems);

            dobList.setOnLoadMoreListener(new OnLoadMoreListener() {

                @Override
                public void onLoadMore(final int totalItemCount) {

                    onLoadMore = true;

                    if(!FINISHED_SCROLLING)
                        new HttpUserTransaction().execute();
                    else
                        dobList.finishLoading();

                }
            });

        } catch (NoListviewException e) {
            e.printStackTrace();
        }

        try {

            dobList.startCentralLoading();

        } catch (NoEmptyViewException e) {
            e.printStackTrace();
        }

        addDummyData(transactionDTOs.size());
    }

    protected void addDummyData(int itemsCount) {

        addItems(userTransactionAdapter.getCount(), userTransactionAdapter.getCount() + itemsCount);

        dobList.finishLoading();

    }

    protected void addItems(int from, int to) {
        for (int i = from; i < to; i++) {
            userTransactionAdapter.addItem(transactionDTOs.get(i));
        }
    }
}
