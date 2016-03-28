package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.Collections;
import java.util.List;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.PendingPaymentListRequest;
import xyz.homapay.hampay.common.core.model.request.PendingPurchaseListRequest;
import xyz.homapay.hampay.common.core.model.response.PendingPaymentListResponse;
import xyz.homapay.hampay.common.core.model.response.PendingPurchaseListResponse;
import xyz.homapay.hampay.common.core.model.response.dto.PaymentInfoDTO;
import xyz.homapay.hampay.common.core.model.response.dto.PspInfoDTO;
import xyz.homapay.hampay.common.core.model.response.dto.PurchaseInfoDTO;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.adapter.PendingPaymentAdapter;
import xyz.homapay.hampay.mobile.android.adapter.PendingPurchaseAdapter;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestPendingPayment;
import xyz.homapay.hampay.mobile.android.async.RequestPendingPurchase;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.impl.comparator.PaymentAmountComparator;
import xyz.homapay.hampay.mobile.android.impl.comparator.PaymentDateComparator;
import xyz.homapay.hampay.mobile.android.impl.comparator.PaymentExpireComparator;
import xyz.homapay.hampay.mobile.android.impl.comparator.PurchaseAmountComparator;
import xyz.homapay.hampay.mobile.android.impl.comparator.PurchaseDateComparator;
import xyz.homapay.hampay.mobile.android.impl.comparator.PurchaseExpireComparator;
import xyz.homapay.hampay.mobile.android.util.Constants;

public class PendingPurchasePaymentActivity extends AppCompatActivity implements View.OnClickListener {

    private Activity activity;

    private RelativeLayout purchase_rl;
    private FacedTextView purchase_title;
    private View purchase_sep;
    private RelativeLayout payment_rl;
    private FacedTextView payment_title;
    private View payment_sep;
    private FacedTextView nullPendingText;


    CoordinatorLayout coordinatorLayout;

    RequestPendingPurchase requestPendingPurchase;
    PendingPurchaseListRequest pendingPurchaseListRequest;

    RequestPendingPayment requestPendingPayment;
    PendingPaymentListRequest pendingPaymentListRequest;

    PendingPurchaseAdapter pendingPurchaseAdapter;
    PendingPaymentAdapter pendingPaymentAdapter;

    ListView pendingListView;

    HamPayDialog hamPayDialog;

    private List<PurchaseInfoDTO> purchaseInfoDTOs;
    List<PaymentInfoDTO> paymentInfoDTOs;
    PspInfoDTO pspInfoDTOs;


    private Dialog dialog;

    SharedPreferences prefs;

    private int itemPosition;

    private String authToken;

    public void backActionBar(View view){
        finish();
    }

    public void typeView(View v){

        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_pending_type, null);

        final FacedTextView pending_purchase = (FacedTextView) view.findViewById(R.id.pending_purchase);
        final FacedTextView pending_payment = (FacedTextView) view.findViewById(R.id.pending_payment);

        pending_purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestPendingPurchase = new RequestPendingPurchase(activity, new RequestPendingPurchaseTaskCompleteListener());
                pendingPurchaseListRequest = new PendingPurchaseListRequest();
                requestPendingPurchase.execute(pendingPurchaseListRequest);
            }
        });

        pending_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestPendingPayment = new RequestPendingPayment(activity, new RequestPendingPaymentTaskCompleteListener());
                pendingPaymentListRequest = new PendingPaymentListRequest();
                requestPendingPayment.execute(pendingPaymentListRequest);
            }
        });

        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setDimAmount(0);
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        layoutParams.x = 25;
        layoutParams.y = 20;

        dialog.show();
    }

    public void sortView(View v){

        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_pending_sort, null);

        final FacedTextView sort_date = (FacedTextView) view.findViewById(R.id.sort_date);
        final FacedTextView sort_expire = (FacedTextView) view.findViewById(R.id.sort_expire);
        final FacedTextView sort_amount = (FacedTextView) view.findViewById(R.id.sort_amount);

        sort_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (purchaseInfoDTOs != null){
                    Collections.sort(purchaseInfoDTOs, new PurchaseDateComparator());
                    pendingPurchaseAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }else if (paymentInfoDTOs != null){
                    Collections.sort(paymentInfoDTOs, new PaymentDateComparator());
                    pendingPaymentAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            }
        });

        sort_expire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (purchaseInfoDTOs != null){
                    Collections.sort(purchaseInfoDTOs, new PurchaseExpireComparator());
                    pendingPurchaseAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }else if (paymentInfoDTOs != null){
                    Collections.sort(paymentInfoDTOs, new PaymentExpireComparator());
                    pendingPaymentAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            }
        });

        sort_amount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (purchaseInfoDTOs != null){
                    Collections.sort(purchaseInfoDTOs, new PurchaseAmountComparator());
                    pendingPurchaseAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }else if (paymentInfoDTOs != null){
                    Collections.sort(paymentInfoDTOs, new PaymentAmountComparator());
                    pendingPaymentAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            }
        });


        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setDimAmount(0);
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        layoutParams.x = 25;
        layoutParams.y = 20;

        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_purchase_payment);

        activity = PendingPurchasePaymentActivity.this;

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);

        authToken = prefs.getString(Constants.LOGIN_TOKEN_ID, "");

        Intent intent = getIntent();

        nullPendingText = (FacedTextView)findViewById(R.id.nullPendingText);
        purchase_rl = (RelativeLayout)findViewById(R.id.purchase_rl);
        purchase_rl.setOnClickListener(this);
        purchase_title = (FacedTextView)findViewById(R.id.purchase_title);
        purchase_sep = (View)findViewById(R.id.purchase_sep);
        payment_rl = (RelativeLayout)findViewById(R.id.payment_rl);
        payment_rl.setOnClickListener(this);
        payment_title = (FacedTextView)findViewById(R.id.payment_title);
        payment_sep = (View)findViewById(R.id.payment_sep);

        hamPayDialog = new HamPayDialog(activity);

        pendingListView = (ListView)findViewById(R.id.pendingListView);

        requestPendingPurchase = new RequestPendingPurchase(activity, new RequestPendingPurchaseTaskCompleteListener());
        pendingPurchaseListRequest = new PendingPurchaseListRequest();

        requestPendingPayment = new RequestPendingPayment(activity, new RequestPendingPaymentTaskCompleteListener());
        pendingPaymentListRequest = new PendingPaymentListRequest();

        if (intent.getStringExtra(Constants.CONTACT_NAME) != null){
            requestPendingPayment.execute(pendingPaymentListRequest);
        }else {
            requestPendingPurchase.execute(pendingPurchaseListRequest);
        }


        coordinatorLayout = (CoordinatorLayout)findViewById(R.id
                .coordinatorLayout);


        pendingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                if (purchaseInfoDTOs != null) {
                    intent.setClass(activity, RequestBusinessPayDetailActivity.class);
                    intent.putExtra(Constants.PENDING_PAYMENT_REQUEST_LIST, purchaseInfoDTOs.get(position));
                    intent.putExtra(Constants.PSP_INFO, purchaseInfoDTOs.get(position).getPspInfo());
                    itemPosition = position;
                    startActivityForResult(intent, 45);
                } else if (paymentInfoDTOs != null) {
                    intent.setClass(activity, InvoicePaymentPendingActivity.class);
                    intent.putExtra(Constants.PAYMENT_INFO, paymentInfoDTOs.get(position));
                    intent.putExtra(Constants.PSP_INFO, pspInfoDTOs);
                    itemPosition = position;
                    startActivityForResult(intent, 46);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 46) {
            if(resultCode == Activity.RESULT_OK){
                int result = data.getIntExtra(Constants.ACTIVITY_RESULT, 0);
                if (result == 1){
                    paymentInfoDTOs.remove(itemPosition);
                    pendingPaymentAdapter.notifyDataSetChanged();
                    if (paymentInfoDTOs.size() == 0) {
                        nullPendingText.setVisibility(View.VISIBLE);
                    }
                }

            }
            if (resultCode == Activity.RESULT_CANCELED) {
            }
        }

        if (requestCode == 45) {
            if(resultCode == Activity.RESULT_OK){
                int result = data.getIntExtra(Constants.ACTIVITY_RESULT, 0);
                if (result == 1){
                    purchaseInfoDTOs.remove(itemPosition);
                    pendingPurchaseAdapter.notifyDataSetChanged();
                    if (purchaseInfoDTOs.size() == 0){
                        nullPendingText.setVisibility(View.VISIBLE);
                    }
                }

            }
            if (resultCode == Activity.RESULT_CANCELED) {
            }
        }
    }

    public class RequestPendingPurchaseTaskCompleteListener implements
            AsyncTaskCompleteListener<ResponseMessage<PendingPurchaseListResponse>> {
        @Override
        public void onTaskComplete(ResponseMessage<PendingPurchaseListResponse> pendingPurchaseListResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            if (pendingPurchaseListResponseMessage != null) {
                if (pendingPurchaseListResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    purchaseInfoDTOs = pendingPurchaseListResponseMessage.getService().getPendingList();
                    if (purchaseInfoDTOs.size() == 0){
                        nullPendingText.setVisibility(View.VISIBLE);
                    }else {
                        Collections.sort(purchaseInfoDTOs, new PurchaseDateComparator());
                        pendingPurchaseAdapter = new PendingPurchaseAdapter(activity, purchaseInfoDTOs, authToken);
                        pendingListView.setAdapter(pendingPurchaseAdapter);
                        paymentInfoDTOs = null;
                        nullPendingText.setVisibility(View.GONE);
                    }
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
                    if (paymentInfoDTOs.size() == 0){
                        nullPendingText.setVisibility(View.VISIBLE);
                    }else {
                        pspInfoDTOs = pendingPaymentListResponseMessage.getService().getPspInfo();
                        Collections.sort(paymentInfoDTOs, new PaymentDateComparator());
                        pendingPaymentAdapter = new PendingPaymentAdapter(activity, paymentInfoDTOs, authToken);
                        pendingListView.setAdapter(pendingPaymentAdapter);
                        purchaseInfoDTOs = null;
                        nullPendingText.setVisibility(View.GONE);
                    }

                }
            }
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingdDialog("");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.purchase_rl:
                purchase_title.setTextColor(getResources().getColor(R.color.user_change_status));
                purchase_sep.setBackgroundColor(getResources().getColor(R.color.user_change_status));
                payment_title.setTextColor(getResources().getColor(R.color.normal_text));
                payment_sep.setBackgroundColor(getResources().getColor(R.color.normal_text));

                requestPendingPurchase = new RequestPendingPurchase(activity, new RequestPendingPurchaseTaskCompleteListener());
                pendingPurchaseListRequest = new PendingPurchaseListRequest();
                requestPendingPurchase.execute(pendingPurchaseListRequest);

                break;

            case R.id.payment_rl:
                payment_title.setTextColor(getResources().getColor(R.color.user_change_status));
                payment_sep.setBackgroundColor(getResources().getColor(R.color.user_change_status));
                purchase_title.setTextColor(getResources().getColor(R.color.normal_text));
                purchase_sep.setBackgroundColor(getResources().getColor(R.color.normal_text));

                requestPendingPayment = new RequestPendingPayment(activity, new RequestPendingPaymentTaskCompleteListener());
                pendingPaymentListRequest = new PendingPaymentListRequest();
                requestPendingPayment.execute(pendingPaymentListRequest);
                break;
        }
    }
}
