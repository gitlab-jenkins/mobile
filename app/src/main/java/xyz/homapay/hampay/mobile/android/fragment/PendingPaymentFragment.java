package xyz.homapay.hampay.mobile.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.CancelPurchasePaymentRequest;
import xyz.homapay.hampay.common.core.model.request.PendingPaymentListRequest;
import xyz.homapay.hampay.common.core.model.request.PendingPurchaseListRequest;
import xyz.homapay.hampay.common.core.model.response.CancelPurchasePaymentResponse;
import xyz.homapay.hampay.common.core.model.response.PendingPaymentListResponse;
import xyz.homapay.hampay.common.core.model.response.PendingPurchaseListResponse;
import xyz.homapay.hampay.common.core.model.response.dto.PaymentInfoDTO;
import xyz.homapay.hampay.common.core.model.response.dto.PurchaseInfoDTO;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.activity.IndividualPaymentPendingActivity;
import xyz.homapay.hampay.mobile.android.activity.PayOneActivity;
import xyz.homapay.hampay.mobile.android.activity.RequestBusinessPayDetailActivity;
import xyz.homapay.hampay.mobile.android.adapter.PendingPaymentAdapter;
import xyz.homapay.hampay.mobile.android.adapter.PendingPurchaseAdapter;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestCancelPurchase;
import xyz.homapay.hampay.mobile.android.async.RequestPendingPayment;
import xyz.homapay.hampay.mobile.android.async.RequestPendingPurchase;
import xyz.homapay.hampay.mobile.android.component.material.ButtonRectangle;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.util.Constants;

/**
 * Created by amir on 6/5/15.
 */
public class PendingPaymentFragment extends Fragment {


    CoordinatorLayout coordinatorLayout;

    RequestPendingPurchase requestPendingPurchase;
    PendingPurchaseListRequest pendingPurchaseListRequest;

    RequestPendingPayment requestPendingPayment;
    PendingPaymentListRequest pendingPaymentListRequest;

    PendingPurchaseAdapter pendingPurchaseAdapter;
    PendingPaymentAdapter pendingCreditRequestAdapter;

    ListView pendigPaymentListView;

    HamPayDialog hamPayDialog;

    List<PurchaseInfoDTO> purchaseInfoDTOs;
    List<PaymentInfoDTO> paymentInfoDTOs;


    RequestCancelPurchase requestCancelPurchase;
    CancelPurchasePaymentRequest cancelPurchasePaymentRequest;

    ButtonRectangle request_business_button;
    ButtonRectangle request_individual_button;

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

        requestPendingPayment = new RequestPendingPayment(getActivity(), new RequestPendingPaymentTaskCompleteListener());
        pendingPaymentListRequest = new PendingPaymentListRequest();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_pending_payment, container, false);

        hamPayDialog = new HamPayDialog(getActivity());
        pendigPaymentListView = (ListView)rootView.findViewById(R.id.pendigPaymentListView);

        request_business_button = (ButtonRectangle)rootView.findViewById(R.id.request_business_button);
        request_individual_button = (ButtonRectangle)rootView.findViewById(R.id.request_individual_button);

        request_business_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPendingPurchase = new RequestPendingPurchase(getActivity(), new RequestPendingPurchaseTaskCompleteListener());
                pendingPurchaseListRequest = new PendingPurchaseListRequest();
                requestPendingPurchase.execute(pendingPurchaseListRequest);
            }
        });

        request_individual_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPendingPayment = new RequestPendingPayment(getActivity(), new RequestPendingPaymentTaskCompleteListener());
                pendingPaymentListRequest = new PendingPaymentListRequest();
                requestPendingPayment.execute(pendingPaymentListRequest);
            }
        });


        coordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id
                .coordinatorLayout);

        requestPendingPurchase.execute(pendingPurchaseListRequest);

        pendigPaymentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent();

                if (purchaseInfoDTOs != null) {
                    intent.setClass(getActivity(), RequestBusinessPayDetailActivity.class);
                    intent.putExtra(Constants.PENDING_PAYMENT_REQUEST, purchaseInfoDTOs.get(position));
                    startActivity(intent);
                }else if (paymentInfoDTOs != null){
                    intent.setClass(getActivity(), IndividualPaymentPendingActivity.class);
                    intent.putExtra(Constants.CONTACT_PHONE_NO, paymentInfoDTOs.get(position).getCallerPhoneNumber());
                    intent.putExtra(Constants.CONTACT_NAME, /*paymentInfoDTOs.get(position).getCallerName()*/ "");
                    startActivity(intent);
                }
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
                    pendingPurchaseAdapter = new PendingPurchaseAdapter(getActivity(),purchaseInfoDTOs);
                    pendigPaymentListView.setAdapter(pendingPurchaseAdapter);
                    paymentInfoDTOs = null;
                }
            }
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingdDialog("");
        }
    }


    public class RequestPendingPaymentTaskCompleteListener implements
            AsyncTaskCompleteListener<ResponseMessage<PendingPaymentListResponse>> {
        @Override
        public void onTaskComplete(ResponseMessage<PendingPaymentListResponse> pendingPaymentListResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            if (pendingPaymentListResponseMessage != null) {
                if (pendingPaymentListResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    paymentInfoDTOs = pendingPaymentListResponseMessage.getService().getPendingList();
                    pendingCreditRequestAdapter = new PendingPaymentAdapter(getActivity(),paymentInfoDTOs);
                    pendigPaymentListView.setAdapter(pendingCreditRequestAdapter);
                    purchaseInfoDTOs = null;
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
                    pendingPurchaseAdapter.notifyDataSetChanged();
                }
            }
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingdDialog("");
        }
    }
}
