package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.PendingPOListRequest;
import xyz.homapay.hampay.common.core.model.response.PendingPOListResponse;
import xyz.homapay.hampay.common.core.model.response.dto.PaymentInfoDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.adapter.PendingPOAdapter;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestPendingPOList;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.edittext.FacedEditText;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;

public class PaymentRequestListActivity extends AppCompatActivity{

    private Context context;
    private Activity activity;
    private ListView paymentRequestList;
    private List<PaymentInfoDTO> paymentInfoList;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private String authToken = "";
    private PendingPOAdapter pendingPOAdapter;
    private RequestPendingPOList requestPendingPOList;
    private PendingPOListRequest pendingPOListRequest;
    private HamPayDialog hamPayDialog;
    private InputMethodManager inputMethodManager;
    private ImageView hampay_contacts;
    private FacedEditText search_text;
    private SwipeRefreshLayout pullToRefresh;
    private FacedTextView nullPendingText;
    private RelativeLayout search_bar;

    public void backActionBar(View view){
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        HamPayApplication.setAppSate(AppState.Resumed);
        if ((System.currentTimeMillis() - prefs.getLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis()) > Constants.MOBILE_TIME_OUT_INTERVAL)) {
            Intent intent = new Intent();
            intent.setClass(context, HamPayLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        if ((System.currentTimeMillis() - prefs.getLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis()) > Constants.MOBILE_TIME_OUT_INTERVAL)) {
            Intent intent = new Intent();
            intent.setClass(context, HamPayLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_request_list);

        context = this;
        activity = PaymentRequestListActivity.this;
        hamPayDialog = new HamPayDialog(activity);

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
        authToken =  prefs.getString(Constants.LOGIN_TOKEN_ID, "");

        inputMethodManager = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);

        nullPendingText = (FacedTextView)findViewById(R.id.nullPendingText);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(getString(R.string.payment_request_null_message));
        ImageSpan is = new ImageSpan(context, R.drawable.add_payment_note);
        spannableStringBuilder.setSpan(is, 39, 42, 0);
        nullPendingText.setText(spannableStringBuilder);

        search_bar = (RelativeLayout)findViewById(R.id.search_bar);


        pullToRefresh = (SwipeRefreshLayout)findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pendingPOListRequest = new PendingPOListRequest();
                requestPendingPOList = new RequestPendingPOList(activity, new RequestPendingPOListTaskCompleteListener());
                requestPendingPOList.execute(pendingPOListRequest);
            }
        });
        paymentRequestList = (ListView)findViewById(R.id.paymentRequestList);
        hampay_contacts = (ImageView)findViewById(R.id.hampay_contacts);
        search_text = (FacedEditText)findViewById(R.id.search_text);
        search_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<PaymentInfoDTO> searchPaymentInfo = new ArrayList<>();
                if (paymentInfoList != null) {
                    for (PaymentInfoDTO paymentInfo : paymentInfoList) {
                        if (paymentInfo.getCalleeName().contains(search_text.getText().toString())) {
                            searchPaymentInfo.add(paymentInfo);
                        }
                    }
                    pendingPOAdapter = new PendingPOAdapter(activity, searchPaymentInfo, authToken);
                    paymentRequestList.setAdapter(pendingPOAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        search_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    inputMethodManager.hideSoftInputFromWindow(search_text.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
        editor.commit();
        pendingPOListRequest = new PendingPOListRequest();
        requestPendingPOList = new RequestPendingPOList(activity, new RequestPendingPOListTaskCompleteListener());
        requestPendingPOList.execute(pendingPOListRequest);

        paymentRequestList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(activity, PendingPODetailActivity.class);
                intent.putExtra(Constants.PAYMENT_INFO, paymentInfoList.get(position));
                startActivityForResult(intent, 1024);
            }
        });

        hampay_contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(activity, HamPayContactsActivity.class);
                startActivity(intent);
            }
        });
    }


    public class RequestPendingPOListTaskCompleteListener implements
            AsyncTaskCompleteListener<ResponseMessage<PendingPOListResponse>> {
        @Override
        public void onTaskComplete(ResponseMessage<PendingPOListResponse> pendingPOListResponseResponseMessage) {
            hamPayDialog.dismisWaitingDialog();
            paymentRequestList.setAdapter(null);
            pullToRefresh.setRefreshing(false);
            if (pendingPOListResponseResponseMessage != null){
                if (pendingPOListResponseResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS){
                    paymentInfoList = pendingPOListResponseResponseMessage.getService().getPendingList();
                    if (paymentInfoList.size() > 0) {
                        pendingPOAdapter = new PendingPOAdapter(activity, paymentInfoList, authToken);
                        paymentRequestList.setAdapter(pendingPOAdapter);
                        nullPendingText.setVisibility(View.GONE);
                        search_bar.setVisibility(View.VISIBLE);
                    }else {
                        nullPendingText.setVisibility(View.VISIBLE);
                        search_bar.setVisibility(View.INVISIBLE);
                    }
                }
                else {
                    nullPendingText.setVisibility(View.VISIBLE);
                }
            }else {
                nullPendingText.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }
}



