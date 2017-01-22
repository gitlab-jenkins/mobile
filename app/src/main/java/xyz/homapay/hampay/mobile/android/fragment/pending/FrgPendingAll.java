package xyz.homapay.hampay.mobile.android.fragment.pending;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import xyz.homapay.hampay.common.core.model.response.TopUpResponse;
import xyz.homapay.hampay.common.core.model.response.dto.FundDTO;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.activity.InvoicePendingConfirmationActivity;
import xyz.homapay.hampay.mobile.android.activity.RequestBusinessPayDetailActivity;
import xyz.homapay.hampay.mobile.android.activity.ServiceBillsDetailActivity;
import xyz.homapay.hampay.mobile.android.activity.ServiceTopUpDetailActivity;
import xyz.homapay.hampay.mobile.android.adapter.PendingFundListAdapter;
import xyz.homapay.hampay.mobile.android.common.charge.ChargeType;
import xyz.homapay.hampay.mobile.android.common.messages.MessageSetOperator;
import xyz.homapay.hampay.mobile.android.component.CustomTextView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.dialog.cancelPending.CancelPendingDialog;
import xyz.homapay.hampay.mobile.android.p.pending.PendingPayment;
import xyz.homapay.hampay.mobile.android.p.pending.PendingPaymentImpl;
import xyz.homapay.hampay.mobile.android.p.pending.PendingPaymentView;
import xyz.homapay.hampay.mobile.android.p.topup.TopUpDetailImpl;
import xyz.homapay.hampay.mobile.android.p.topup.TopUpDetailView;
import xyz.homapay.hampay.mobile.android.util.AppManager;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.ModelLayerImpl;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;
import xyz.homapay.hampay.mobile.android.util.TelephonyUtils;

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
        lst.setOnItemClickListener((adapterView, view, position, l) -> {
            Intent intent1 = new Intent();
            lst.setEnabled(false);
            if (adapter.getItem(position).getPaymentType() == FundDTO.PaymentType.PURCHASE) {
                intent1.setClass(getActivity(), RequestBusinessPayDetailActivity.class);
                intent1.putExtra(Constants.PROVIDER_ID, adapter.getItem(position).getProviderId());
                startActivityForResult(intent1, 45);
                lst.setEnabled(true);
            } else if (adapter.getItem(position).getPaymentType() == FundDTO.PaymentType.PAYMENT) {
                intent1.setClass(getActivity(), InvoicePendingConfirmationActivity.class);
                intent1.putExtra(Constants.PROVIDER_ID, adapter.getItem(position).getProviderId());
                startActivityForResult(intent1, 46);
                lst.setEnabled(true);
            } else if (adapter.getItem(position).getPaymentType() == FundDTO.PaymentType.UTILITY_BILL) {
                intent1.setClass(getActivity(), ServiceBillsDetailActivity.class);
                intent1.putExtra(Constants.PROVIDER_ID, adapter.getItem(position).getProviderId());
                startActivityForResult(intent1, 47);
                lst.setEnabled(true);
            } else {
                new TopUpDetailImpl(new ModelLayerImpl(getActivity()), new TopUpDetailView() {
                    @Override
                    public void showProgress() {
                        dlg.showWaitingDialog("");
                    }

                    @Override
                    public void dissmisProgress() {
                        dlg.dismisWaitingDialog();
                    }

                    @Override
                    public void onError() {
                        dlg.showFailPendingPaymentDialog(getString(R.string.err_general), getString(R.string.err_general_text));
                    }

                    @Override
                    public void onDetailLoaded(boolean status, ResponseMessage<TopUpResponse> data, String message) {
                        if (status) {
                            Intent intent = new Intent(getActivity(), ServiceTopUpDetailActivity.class);
                            MessageSetOperator messageSetOperator = new MessageSetOperator(new TelephonyUtils().getNumberOperator(data.getService().getTopUpInfoDTO().getCellNumber()));
                            data.getService().getTopUpInfoDTO().setImageId(messageSetOperator.getOperatorName());
                            intent.putExtra(Constants.TOP_UP_INFO, data.getService().getTopUpInfoDTO());
                            intent.putExtra(Constants.CHARGE_TYPE, ChargeType.DIRECT.ordinal());
                            intent.putExtra(Constants.FUND_DTO, adapter.getItem(position));
                            startActivityForResult(intent, 48);
                        } else {
                            dlg.showFailPendingPaymentDialog(data.getService().getResultStatus().getCode(), data.getService().getResultStatus().getDescription());
                        }
                    }
                }).getDetail(adapter.getItem(position).getProviderId());
            }
        });
        lst.setOnItemLongClickListener((adapterView, view, position, l) -> {
            String productCode = "";
            if (adapter != null) {
                productCode = adapter.getItem(position).getCode();
            }
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            CancelPendingDialog cancelPendingDialog = new CancelPendingDialog();
            Bundle bundle = new Bundle();
            bundle.putString(Constants.PENDING_CODE, new PersianEnglishDigit().E2P(productCode));
            cancelPendingDialog.setArguments(bundle);
            cancelPendingDialog.show(fragmentManager, "fragment_edit_name");
            return true;
        });
        pendingPayment.getList(FundType.ALL);
        pullToRefresh.setOnRefreshListener(() -> pendingPayment.getList(FundType.ALL));
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        lst.setEnabled(true);
    }

    @Override
    public void showProgress() {
        tvNodata.setVisibility(View.INVISIBLE);
        dlg.showWaitingDialog("");
    }

    @Override
    public void cancelProgress() {
        dlg.dismisWaitingDialog();
        pullToRefresh.setRefreshing(false);
    }

    @Override
    public void onError() {
        dlg.dismisWaitingDialog();
        pullToRefresh.setRefreshing(false);
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
