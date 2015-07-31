package com.hampay.mobile.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.common.response.ResultStatus;
import com.hampay.common.core.model.request.TransactionListRequest;
import com.hampay.common.core.model.response.TransactionListResponse;
import com.hampay.common.core.model.response.dto.TransactionDTO;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.activity.TransactionDetailActivity;
import com.hampay.mobile.android.adapter.UserTransactionAdapter;
import com.hampay.mobile.android.async.AsyncTaskCompleteListener;
import com.hampay.mobile.android.async.RequestUserTransaction;
import com.hampay.mobile.android.component.doblist.DobList;
import com.hampay.mobile.android.component.doblist.events.OnLoadMoreListener;
import com.hampay.mobile.android.component.doblist.exceptions.NoEmptyViewException;
import com.hampay.mobile.android.component.doblist.exceptions.NoListviewException;
import com.hampay.mobile.android.dialog.HamPayDialog;
import com.hampay.mobile.android.util.Constant;
import com.hampay.mobile.android.util.Constants;

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

    private boolean onLoadMore = false;

    private List<TransactionDTO> transactionDTOs;

    RequestUserTransaction requestUserTransaction;
    TransactionListRequest transactionListRequest;
    int requestPageNumber = 0;

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
                intent.putExtra(Constants.USER_TRANSACTION_DTO, transactionDTOs.get(position));
                startActivity(intent);
            }
        });

        requestPageNumber = 0;

        transactionListRequest = new TransactionListRequest();
        transactionListRequest.setPageNumber(requestPageNumber);
        transactionListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);

        requestUserTransaction = new RequestUserTransaction(getActivity(), new RequestUserTransactionsTaskCompleteListener());
        requestUserTransaction.execute(transactionListRequest);

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


    public class RequestUserTransactionsTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<TransactionListResponse>> {

        List<TransactionDTO> newTransactionDTOs;

        @Override
        public void onTaskComplete(ResponseMessage<TransactionListResponse> transactionListResponseMessage) {

            loading_rl.setVisibility(View.GONE);

            if (transactionListResponseMessage != null) {

                if (transactionListResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {

                    newTransactionDTOs = transactionListResponseMessage.getService().getTransactions();
                    transactionDTOs.addAll(newTransactionDTOs);

                    if (transactionDTOs != null) {

                        if (newTransactionDTOs.size() == 0 || newTransactionDTOs.size() < Constants.DEFAULT_PAGE_SIZE){
                            FINISHED_SCROLLING = true;
                        }

                        if (transactionDTOs.size() > 0) {

                            requestPageNumber++;

                            if (onLoadMore) {
                                if (newTransactionDTOs != null)
                                    addDummyData(newTransactionDTOs.size());
                            } else {
                                initDobList(rootView, transationListView);
                                transationListView.setAdapter(userTransactionAdapter);
                            }
                        }

                    }

                } else {
                    transactionListRequest.setPageNumber(requestPageNumber);
                    requestUserTransaction = new RequestUserTransaction(getActivity(), new RequestUserTransactionsTaskCompleteListener());
                    new HamPayDialog(getActivity()).showFailUserTransactionDialog(requestUserTransaction, transactionListRequest);
                }
            } else {
                transactionListRequest.setPageNumber(requestPageNumber);
                requestUserTransaction = new RequestUserTransaction(getActivity(), new RequestUserTransactionsTaskCompleteListener());
                new HamPayDialog(getActivity()).showFailUserTransactionDialog(requestUserTransaction, transactionListRequest);
            }
        }


        @Override
        public void onTaskPreRun() {
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

                    if (!FINISHED_SCROLLING) {
                        transactionListRequest.setPageNumber(requestPageNumber);
                        requestUserTransaction = new RequestUserTransaction(getActivity(), new RequestUserTransactionsTaskCompleteListener());
                        requestUserTransaction.execute(transactionListRequest);

                    } else
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
