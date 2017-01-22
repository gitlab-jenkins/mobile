package xyz.homapay.hampay.mobile.android.fragment.pending;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.enums.FundType;
import xyz.homapay.hampay.common.core.model.response.PendingFundListResponse;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.adapter.PendingFundListAdapter;
import xyz.homapay.hampay.mobile.android.component.CustomTextView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.p.pending.PendingPayment;
import xyz.homapay.hampay.mobile.android.p.pending.PendingPaymentImpl;
import xyz.homapay.hampay.mobile.android.p.pending.PendingPaymentView;
import xyz.homapay.hampay.mobile.android.util.AppManager;
import xyz.homapay.hampay.mobile.android.util.ModelLayerImpl;

/**
 * Created by mohammad on 1/22/17.
 */

public class FrgPendingAll extends Fragment implements PendingPaymentView {

    @BindView(R.id.lst)
    ListView lst;

    @BindView(R.id.tvNoData)
    CustomTextView tvNodata;

    @BindView(R.id.pullToRefresh)
    SwipeRefreshLayout pullToRefresh;

    private View rootView;
    private PendingPayment pendingPayment;
    private HamPayDialog dlg;
    private PendingFundListAdapter adapter;

    public static FrgPendingAll newInstance() {
        FrgPendingAll fragment = new FrgPendingAll();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dlg = new HamPayDialog(getActivity());
        pendingPayment = new PendingPaymentImpl(new ModelLayerImpl(getActivity()), this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frg_pending_all, null);
        ButterKnife.bind(this, rootView);
        pendingPayment.getList(FundType.ALL);
        pullToRefresh.setOnRefreshListener(() -> pendingPayment.getList(FundType.ALL));
        return rootView;
    }

    @Override
    public void showProgress() {
        tvNodata.setVisibility(View.INVISIBLE);
        dlg.showWaitingDialog("");
    }

    @Override
    public void cancelProgress() {
        dlg.dismisWaitingDialog();
    }

    @Override
    public void onError() {
        dlg.dismisWaitingDialog();
        Toast.makeText(getActivity(), getString(R.string.err_general), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onListLoaded(boolean state, ResponseMessage<PendingFundListResponse> data, String message) {
        if (state) {
            if (data != null && data.getService() != null && data.getService().getFundDTOList() != null && data.getService().getFundDTOList().size() > 0) {
                adapter = new PendingFundListAdapter(getActivity(), data.getService().getFundDTOList(), AppManager.getAuthToken(getActivity()));
                lst.setAdapter(adapter);
            } else
                tvNodata.setVisibility(View.VISIBLE);
        }
    }
}
