package xyz.homapay.hampay.mobile.android.fragment.pending;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.response.PendingPOListResponse;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.activity.PendingPODetailActivity;
import xyz.homapay.hampay.mobile.android.adapter.PendingPOAdapter;
import xyz.homapay.hampay.mobile.android.component.CustomTextView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.p.payment.PendingPOList;
import xyz.homapay.hampay.mobile.android.p.payment.PendingPOListImpl;
import xyz.homapay.hampay.mobile.android.p.payment.PendingPOListView;
import xyz.homapay.hampay.mobile.android.util.AppManager;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.ModelLayerImpl;

/**
 * Created by mohammad on 1/22/17.
 */

public class FrgPendingRecieved extends Fragment implements PendingPOListView {

    @BindView(R.id.pullToRefresh)
    SwipeRefreshLayout pullToRefresh;
    @BindView(R.id.lst)
    ListView lst;
    @BindView(R.id.tvNoData)
    CustomTextView tvNoData;
    private View rootView;
    private PendingPOList pendingPOList;
    private HamPayDialog dlg;
    private PendingPOAdapter pendingPOAdapter;

    public static FrgPendingRecieved newInstance() {
        FrgPendingRecieved fragment = new FrgPendingRecieved();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pendingPOList = new PendingPOListImpl(new ModelLayerImpl(getActivity()), this);
        dlg = new HamPayDialog(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frg_pending_recieved, null);
        ButterKnife.bind(this, rootView);

        lst.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent();
            intent.setClass(getActivity(), PendingPODetailActivity.class);
            intent.putExtra(Constants.PAYMENT_INFO, pendingPOAdapter.getItem(position));
            startActivityForResult(intent, 1024);
        });
        pullToRefresh.setOnRefreshListener(() -> pendingPOList.getList());
        pendingPOList.getList();

        return rootView;
    }

    @Override
    public void showProgress() {
        tvNoData.setVisibility(View.INVISIBLE);
        dlg.showWaitingDialog("");
    }

    @Override
    public void cancelProgress() {
        if (pendingPOAdapter != null && pendingPOAdapter.getCount() > 0)
            tvNoData.setVisibility(View.INVISIBLE);
        else
            tvNoData.setVisibility(View.VISIBLE);
        dlg.dismisWaitingDialog();
        pullToRefresh.setRefreshing(false);
    }

    @Override
    public void onError() {
        dlg.dismisWaitingDialog();
        pullToRefresh.setRefreshing(false);
    }

    @Override
    public void onListLoaded(boolean state, ResponseMessage<PendingPOListResponse> data, String message) {
        if (state && data != null && data.getService() != null) {
            if (data.getService().getResultStatus() == ResultStatus.SUCCESS) {
                pendingPOAdapter = new PendingPOAdapter(getActivity(), data.getService().getPendingList(), AppManager.getAuthToken(getActivity()));
                lst.setAdapter(pendingPOAdapter);
            } else if (data.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                AppManager.logOut(getActivity());
            }
        }
    }
}
