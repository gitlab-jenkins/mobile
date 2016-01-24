package xyz.homapay.hampay.mobile.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.CancelPurchasePaymentRequest;
import xyz.homapay.hampay.common.core.model.request.PendingPurchaseListRequest;
import xyz.homapay.hampay.common.core.model.response.CancelPurchasePaymentResponse;
import xyz.homapay.hampay.common.core.model.response.PendingPurchaseListResponse;
import xyz.homapay.hampay.common.core.model.response.dto.PurchaseInfoDTO;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.activity.RequestBusinessPayDetailActivity;
import xyz.homapay.hampay.mobile.android.activity.TransactionDetailActivity;
import xyz.homapay.hampay.mobile.android.adapter.PendingPaymentAdapter;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestCancelPurchase;
import xyz.homapay.hampay.mobile.android.async.RequestPendingPurchase;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.util.Constants;

/**
 * Created by amir on 6/5/15.
 */
public class PendingPaymentFragment extends Fragment {


    CoordinatorLayout coordinatorLayout;

    RequestPendingPurchase requestPendingPurchase;
    PendingPurchaseListRequest pendingPurchaseListRequest;

    PendingPaymentAdapter pendingPaymentAdapter;

    ListView pendigPaymentListView;

    HamPayDialog hamPayDialog;

    List<PurchaseInfoDTO> purchaseInfoDTOs;


    RequestCancelPurchase requestCancelPurchase;
    CancelPurchasePaymentRequest cancelPurchasePaymentRequest;

    public PendingPaymentFragment() {
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


        requestPendingPurchase = new RequestPendingPurchase(getActivity(), new RequestPendingPurchaseTaskCompleteListener());
        pendingPurchaseListRequest = new PendingPurchaseListRequest();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_pending_payment, container, false);

        hamPayDialog = new HamPayDialog(getActivity());
        pendigPaymentListView = (ListView)rootView.findViewById(R.id.pendigPaymentListView);

        coordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id
                .coordinatorLayout);

        requestPendingPurchase.execute(pendingPurchaseListRequest);

        pendigPaymentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), RequestBusinessPayDetailActivity.class);
                intent.putExtra(Constants.PENDING_PAYMENT_REQUEST, purchaseInfoDTOs.get(position));
                startActivity(intent);
            }
        });

        pendigPaymentListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "", Snackbar.LENGTH_LONG)
                        .setAction("از پرداخت منصرف شدم", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                requestCancelPurchase = new RequestCancelPurchase(getActivity(), new RequestCancelPurchasePaymentTaskCompleteListener(position));
                                cancelPurchasePaymentRequest = new CancelPurchasePaymentRequest();
                                cancelPurchasePaymentRequest.setPurchaseCode(purchaseInfoDTOs.get(position).getPurchaseCode());
                                requestCancelPurchase.execute(cancelPurchasePaymentRequest);
                            }
                        });

                snackbar.setActionTextColor(getResources().getColor(R.color.colorPrimary));

                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.YELLOW);

                snackbar.show();

                return true;
            }
        });



        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();

    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }



    public class RequestPendingPurchaseTaskCompleteListener implements
            AsyncTaskCompleteListener<ResponseMessage<PendingPurchaseListResponse>> {
        @Override
        public void onTaskComplete(ResponseMessage<PendingPurchaseListResponse> pendingPurchaseListResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            if (pendingPurchaseListResponseMessage != null) {
                if (pendingPurchaseListResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {

                    purchaseInfoDTOs =  pendingPurchaseListResponseMessage.getService().getPendingList();

                    pendingPaymentAdapter = new PendingPaymentAdapter(getActivity(),purchaseInfoDTOs);
                    pendigPaymentListView.setAdapter(pendingPaymentAdapter);

                }
            }
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingdDialog("");
        }
    }


    public class RequestCancelPurchasePaymentTaskCompleteListener implements
            AsyncTaskCompleteListener<ResponseMessage<CancelPurchasePaymentResponse>> {

        int position;

        RequestCancelPurchasePaymentTaskCompleteListener(int position){
            this.position = position;
        }


        @Override
        public void onTaskComplete(ResponseMessage<CancelPurchasePaymentResponse> cancelPurchasePaymentResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            if (cancelPurchasePaymentResponseMessage != null) {
                if (cancelPurchasePaymentResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    cancelPurchasePaymentResponseMessage.getService().getRequestUUID();
                    purchaseInfoDTOs.remove(position);
                    pendingPaymentAdapter.notifyDataSetChanged();
                }
            }
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingdDialog("");
        }
    }
}
