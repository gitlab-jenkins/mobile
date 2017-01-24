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
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.PendingPOListRequest;
import xyz.homapay.hampay.common.core.model.response.PendingPOListResponse;
import xyz.homapay.hampay.common.core.model.response.dto.PaymentInfoDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.adapter.PendingPOAdapter;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.edittext.FacedEditText;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.p.payment.PendingPOList;
import xyz.homapay.hampay.mobile.android.p.payment.PendingPOListImpl;
import xyz.homapay.hampay.mobile.android.p.payment.PendingPOListView;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.ModelLayerImpl;

public class PaymentRequestListActivity extends AppCompatActivity implements PendingPOListView {

    @BindView(R.id.paymentRequestList)
    ListView paymentRequestList;
    @BindView(R.id.hampay_contacts)
    ImageView hampay_contacts;
    @BindView(R.id.search_text)
    FacedEditText search_text;
    @BindView(R.id.pullToRefresh)
    SwipeRefreshLayout pullToRefresh;
    @BindView(R.id.nullPendingText)
    FacedTextView nullPendingText;
    @BindView(R.id.search_bar)
    RelativeLayout search_bar;
    private Context context;
    private Activity activity;
    private List<PaymentInfoDTO> paymentInfoList;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private String authToken = "";
    private PendingPOAdapter pendingPOAdapter;
    private PendingPOListRequest pendingPOListRequest;
    private HamPayDialog hamPayDialog;
    private InputMethodManager inputMethodManager;
    private PendingPOList pendingPOList;

    public void backActionBar(View view) {
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
        ButterKnife.bind(this);

        context = this;
        activity = PaymentRequestListActivity.this;
        hamPayDialog = new HamPayDialog(activity);
        pendingPOList = new PendingPOListImpl(new ModelLayerImpl(context), this);
        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
        authToken = prefs.getString(Constants.LOGIN_TOKEN_ID, "");

        inputMethodManager = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(getString(R.string.payment_request_null_message));
        ImageSpan is = new ImageSpan(context, R.drawable.add_payment_note);
        spannableStringBuilder.setSpan(is, 39, 42, 0);
        nullPendingText.setText(spannableStringBuilder);

        pendingPOList.getList();
        pullToRefresh.setOnRefreshListener(() -> pendingPOList.getList());
        search_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<PaymentInfoDTO> searchPaymentInfo = new ArrayList<>();
                if (paymentInfoList != null) {
                    for (PaymentInfoDTO paymentInfo : paymentInfoList) {
                        if (paymentInfo.getCalleeName().toLowerCase().contains(search_text.getText().toString().toLowerCase())) {
                            searchPaymentInfo.add(paymentInfo);
                        }
                    }
                    pendingPOAdapter = new PendingPOAdapter(activity, searchPaymentInfo, authToken);
                    paymentRequestList.setAdapter(pendingPOAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        search_text.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                inputMethodManager.hideSoftInputFromWindow(search_text.getWindowToken(), 0);
                return true;
            }
            return false;
        });

        paymentRequestList.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent();
            intent.setClass(activity, PendingPODetailActivity.class);
            intent.putExtra(Constants.PAYMENT_INFO, paymentInfoList.get(position));
            startActivityForResult(intent, 1024);
        });

        hampay_contacts.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClass(activity, HamPayContactsActivity.class);
            startActivity(intent);
        });
    }

    private void forceLogout() {
        editor.remove(Constants.LOGIN_TOKEN_ID);
        editor.commit();
        Intent intent = new Intent();
        intent.setClass(context, HamPayLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (activity != null) {
            finish();
            startActivity(intent);
        }
    }

    @Override
    public void showProgress() {
        hamPayDialog.showWaitingDialog("");
    }

    @Override
    public void cancelProgress() {
        hamPayDialog.dismisWaitingDialog();
    }

    @Override
    public void onError() {
        hamPayDialog.showErrorGeneral();
    }

    @Override
    public void onListLoaded(boolean state, ResponseMessage<PendingPOListResponse> data, String message) {
        paymentRequestList.setAdapter(null);
        pullToRefresh.setRefreshing(false);
        if (data != null) {
            if (data.getService().getResultStatus() == ResultStatus.SUCCESS) {
                paymentInfoList = data.getService().getPendingList();
                if (paymentInfoList.size() > 0) {
                    pendingPOAdapter = new PendingPOAdapter(activity, paymentInfoList, authToken);
                    paymentRequestList.setAdapter(pendingPOAdapter);
                    nullPendingText.setVisibility(View.GONE);
                    search_bar.setVisibility(View.VISIBLE);
                } else {
                    nullPendingText.setVisibility(View.VISIBLE);
                    search_bar.setVisibility(View.INVISIBLE);
                }
            } else if (data.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                forceLogout();
            } else {
                nullPendingText.setVisibility(View.VISIBLE);
            }
        } else {
            nullPendingText.setVisibility(View.VISIBLE);
        }
    }
}



