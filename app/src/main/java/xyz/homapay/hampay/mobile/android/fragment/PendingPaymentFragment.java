package xyz.homapay.hampay.mobile.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.PendingPurchaseListRequest;
import xyz.homapay.hampay.common.core.model.response.PendingPurchaseListResponse;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestPendingPurchase;

/**
 * Created by amir on 6/5/15.
 */
public class PendingPaymentFragment extends Fragment {


    RequestPendingPurchase requestPendingPurchase;
    PendingPurchaseListRequest pendingPurchaseListRequest;

    ListView pendigPaymentListView;

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
        requestPendingPurchase.execute(pendingPurchaseListRequest);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_pending_payment, container, false);

        pendigPaymentListView = (ListView)rootView.findViewById(R.id.pendigPaymentListView);



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

            if (pendingPurchaseListResponseMessage != null) {
                if (pendingPurchaseListResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {

//                    pendingPurchaseListResponseMessage.getService().getPendingList()

                }
            }
        }

        @Override
        public void onTaskPreRun() {

        }
    }
}
