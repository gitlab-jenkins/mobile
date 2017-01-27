package xyz.homapay.hampay.mobile.android.fragment.pending;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.response.CancelFundResponse;
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
import xyz.homapay.hampay.mobile.android.common.messages.MessageOnBackPressed;
import xyz.homapay.hampay.mobile.android.common.messages.MessageSetOperator;
import xyz.homapay.hampay.mobile.android.common.messages.MessageSheetStateChanged;
import xyz.homapay.hampay.mobile.android.component.CustomTextView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.dialog.cancelPending.ActionPending;
import xyz.homapay.hampay.mobile.android.dialog.cancelPending.CancelPendingDialog;
import xyz.homapay.hampay.mobile.android.m.common.ModelLayer;
import xyz.homapay.hampay.mobile.android.p.pending.CancelFund;
import xyz.homapay.hampay.mobile.android.p.pending.CancelFundImpl;
import xyz.homapay.hampay.mobile.android.p.pending.CancelFundView;
import xyz.homapay.hampay.mobile.android.p.pending.PendingPayment;
import xyz.homapay.hampay.mobile.android.p.pending.PendingPaymentImpl;
import xyz.homapay.hampay.mobile.android.p.pending.PendingPaymentView;
import xyz.homapay.hampay.mobile.android.p.topup.TopUpDetail;
import xyz.homapay.hampay.mobile.android.p.topup.TopUpDetailImpl;
import xyz.homapay.hampay.mobile.android.p.topup.TopUpDetailView;
import xyz.homapay.hampay.mobile.android.util.AppManager;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.ModelLayerImpl;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;
import xyz.homapay.hampay.mobile.android.util.TelephonyUtils;
import xyz.homapay.hampay.mobile.android.util.font.FontFace;

/**
 * Created by mohammad on 1/22/17.
 */

public class FrgPendingRequests extends Fragment implements PendingPaymentView, CancelPendingDialog.CancelPendingDialogListener, CancelFundView, TopUpDetailView {

    @BindView(R.id.lst)
    ListView lst;

    @BindView(R.id.tvNoData)
    CustomTextView tvNodata;

    @BindView(R.id.pullToRefresh)
    SwipeRefreshLayout pullToRefresh;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.fab_sheet)
    View fab_sheet;

    @BindView(R.id.overlay)
    View overlay;

    @BindView(R.id.fab_sheet_item_all)
    CustomTextView fabAll;

    @BindView(R.id.fab_sheet_item_commercial)
    CustomTextView fabCommercial;

    @BindView(R.id.fab_sheet_item_personal)
    CustomTextView fabPersonal;

    private FilterState filterState = FilterState.ALL;

    private View rootView;
    private PendingPayment pendingPayment;
    private CancelFund cancelFund;
    private TopUpDetail topUpDetail;
    private ModelLayer modelLayer;
    private HamPayDialog dlg;
    private PendingFundListAdapter adapter;
    private int position;
    private MaterialSheetFab materialSheetFab;
    private Handler countDownHandler;

    public static FrgPendingRequests newInstance() {
        FrgPendingRequests fragment = new FrgPendingRequests();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        modelLayer = new ModelLayerImpl(getActivity());
        dlg = new HamPayDialog(getActivity());
        pendingPayment = new PendingPaymentImpl(modelLayer, this);
        cancelFund = new CancelFundImpl(modelLayer, this);
        topUpDetail = new TopUpDetailImpl(modelLayer, this);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frg_pending_request, null);
        ButterKnife.bind(this, rootView);

        int sheetColor = ContextCompat.getColor(getActivity(), R.color.app_origin);
        int fabColor = ContextCompat.getColor(getActivity(), R.color.white);
        materialSheetFab = new MaterialSheetFab(fab, fab_sheet, overlay, sheetColor, fabColor);
        materialSheetFab.setEventListener(new MaterialSheetFabEventListener() {
            @Override
            public void onShowSheet() {
                super.onShowSheet();
                EventBus.getDefault().post(new MessageSheetStateChanged(true));
                switch (filterState) {
                    case ALL:
                        fabAll.setTypeface(FontFace.getInstance(getActivity()).getVAZIR_BOLD());
                        fabCommercial.setTypeface(FontFace.getInstance(getActivity()).getVAZIR());
                        fabPersonal.setTypeface(FontFace.getInstance(getActivity()).getVAZIR());
                        //
                        fabAll.setTextColor(ContextCompat.getColor(getActivity(), R.color.app_origin));
                        fabCommercial.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                        fabPersonal.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                        break;
                    case COMMERCIAL:
                        fabAll.setTypeface(FontFace.getInstance(getActivity()).getVAZIR());
                        fabCommercial.setTypeface(FontFace.getInstance(getActivity()).getVAZIR_BOLD());
                        fabPersonal.setTypeface(FontFace.getInstance(getActivity()).getVAZIR());
                        //
                        fabAll.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                        fabCommercial.setTextColor(ContextCompat.getColor(getActivity(), R.color.app_origin));
                        fabPersonal.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                        break;
                    case PERSONAL:
                        fabAll.setTypeface(FontFace.getInstance(getActivity()).getVAZIR());
                        fabCommercial.setTypeface(FontFace.getInstance(getActivity()).getVAZIR());
                        fabPersonal.setTypeface(FontFace.getInstance(getActivity()).getVAZIR_BOLD());
                        //
                        fabAll.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                        fabCommercial.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                        fabPersonal.setTextColor(ContextCompat.getColor(getActivity(), R.color.app_origin));
                        break;
                }
            }

            @Override
            public void onHideSheet() {
                super.onHideSheet();
                EventBus.getDefault().post(new MessageSheetStateChanged(false));
            }
        });

        fabAll.setOnClickListener(view -> {
            filterState = FilterState.ALL;
            materialSheetFab.hideSheet();
            pendingPayment.getList(AppManager.getFundType(filterState));
        });
        fabCommercial.setOnClickListener(view -> {
            filterState = FilterState.COMMERCIAL;
            materialSheetFab.hideSheet();
            pendingPayment.getList(AppManager.getFundType(filterState));
        });
        fabPersonal.setOnClickListener(view -> {
            filterState = FilterState.PERSONAL;
            materialSheetFab.hideSheet();
            pendingPayment.getList(AppManager.getFundType(filterState));
        });

        lst.setOnItemClickListener((adapterView, view, position, l) -> {
            Intent intent1 = new Intent();
            lst.setEnabled(false);
            this.position = position;
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
                topUpDetail.getDetail(adapter.getItem(position).getProviderId());
            }
        });
        lst.setOnItemLongClickListener((adapterView, view, position, l) -> {
            this.position = position;
            String productCode = "";
            if (adapter != null) {
                productCode = adapter.getItem(position).getCode();
            }
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            CancelPendingDialog cancelPendingDialog = CancelPendingDialog.newInstance(this);
            Bundle bundle = new Bundle();
            bundle.putString(Constants.PENDING_CODE, new PersianEnglishDigit().E2P(productCode));
            cancelPendingDialog.setArguments(bundle);
            cancelPendingDialog.show(fragmentManager, "fragment_edit_name");
            return true;
        });
        pendingPayment.getList(AppManager.getFundType(filterState));
        pullToRefresh.setOnRefreshListener(() -> pendingPayment.getList(AppManager.getFundType(filterState)));
        countDownHandler = new Handler(Looper.getMainLooper());
        countDownHandler.postDelayed(() -> {
            if (adapter != null)
                adapter.notifyDataSetChanged();
        }, 1000);

        countDownHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (adapter != null && adapter.getCount() > 0)
                    adapter.notifyDataSetChanged();
                countDownHandler.postDelayed(this, 1000);
            }
        }, 1000);
        return rootView;
    }

    @Subscribe
    public void onBackPressed(MessageOnBackPressed onBackPressedOnPendingAct) {
        if (materialSheetFab.isSheetVisible()) {
            materialSheetFab.hideSheet();
        }
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
            } else {
                if (adapter != null)
                    adapter.clear();
                tvNodata.setVisibility(View.VISIBLE);
                dlg.dismisWaitingDialog();
            }
        } else {
            dlg.dismisWaitingDialog();
        }
    }

    @Override
    public void onFinishEditDialog(ActionPending actionPending) {
        if (actionPending.equals(ActionPending.REMOVE)) {
            cancelFund.cancel(AppManager.extractFundTypeFromPaymentType(adapter.getItem(position).getPaymentType()), adapter.getItem(position).getProviderId());
        }
    }

    @Override
    public void onCancelDone(boolean state, ResponseMessage<CancelFundResponse> data, String message) {
        if (state && data != null && data.getService() != null) {
            pendingPayment.getList(AppManager.getFundType(filterState));
        } else {
            dlg.dismisWaitingDialog();
        }
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
            dlg.dismisWaitingDialog();
            dlg.showFailPendingPaymentDialog(data.getService().getResultStatus().getCode(), data.getService().getResultStatus().getDescription());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 45 || requestCode == 46 || requestCode == 47 || requestCode == 48) {
            if (resultCode == Activity.RESULT_OK) {
                int result = data.getIntExtra(Constants.ACTIVITY_RESULT, -1);
                if (result == 0) {
                    pendingPayment.getList(AppManager.getFundType(filterState));
                }
            }
        }
    }

    public enum FilterState {
        ALL, COMMERCIAL, PERSONAL
    }
}
