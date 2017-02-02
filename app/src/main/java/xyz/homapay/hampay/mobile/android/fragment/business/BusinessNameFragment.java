package xyz.homapay.hampay.mobile.android.fragment.business;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.enums.BizSortFactor;
import xyz.homapay.hampay.common.core.model.response.BusinessListResponse;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.activity.BusinessPaymentInfoActivity;
import xyz.homapay.hampay.mobile.android.adapter.HamPayBusinessesAdapter;
import xyz.homapay.hampay.mobile.android.common.messages.MessageOnBackPressed;
import xyz.homapay.hampay.mobile.android.common.messages.MessageSheetStateChanged;
import xyz.homapay.hampay.mobile.android.common.messages.MessageTabChanged;
import xyz.homapay.hampay.mobile.android.component.CustomTextView;
import xyz.homapay.hampay.mobile.android.component.EndlessScrollListener;
import xyz.homapay.hampay.mobile.android.component.MyTextWatcher;
import xyz.homapay.hampay.mobile.android.component.edittext.FacedEditText;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.firebase.EventLogger;
import xyz.homapay.hampay.mobile.android.firebase.service.ServiceEvent;
import xyz.homapay.hampay.mobile.android.p.business.BusinessList;
import xyz.homapay.hampay.mobile.android.p.business.BusinessListImpl;
import xyz.homapay.hampay.mobile.android.p.business.BusinessListView;
import xyz.homapay.hampay.mobile.android.p.business.BusinessSearch;
import xyz.homapay.hampay.mobile.android.p.business.BusinessSearchImpl;
import xyz.homapay.hampay.mobile.android.util.AppManager;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.ModelLayerImpl;
import xyz.homapay.hampay.mobile.android.util.font.FontFace;

/**
 * Created by mohammad on 1/22/17.
 */

public class BusinessNameFragment extends Fragment implements BusinessListView {

    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.fab_sheet)
    View fab_sheet;
    @BindView(R.id.overlay)
    View overlay;
    @BindView(R.id.fab_sheet_item_all)
    CustomTextView fabAll;
    //    @BindView(R.id.fab_sheet_item_popular)
//    CustomTextView fabPoular;
    @BindView(R.id.fab_sheet_item_recent)
    CustomTextView fabRecent;
    @BindView(R.id.businessListView)
    ListView businessListView;
    @BindView(R.id.pullToRefresh)
    SwipeRefreshLayout pullToRefresh;
    @BindView(R.id.etSearchPhraseText)
    FacedEditText etSearchPhraseText;
    @BindView(R.id.imgSearchImage)
    ImageView imgSearchImage;
    @BindView(R.id.prg)
    ProgressBar prg;
    private View rootView;
    private HamPayDialog hamPayDialog;
    private HamPayBusinessesAdapter hamPayBusinessesAdapter;
    private BizSortFactor bizSortFactor = BizSortFactor.NAME;
    private InputMethodManager inputMethodManager;
    private MaterialSheetFab materialSheetFab;
    private BusinessList businessList;
    private BusinessSearch businessSearch;

    public static BusinessNameFragment newInstance() {
        BusinessNameFragment fragment = new BusinessNameFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        businessList = new BusinessListImpl(new ModelLayerImpl(getActivity()), this);
        businessSearch = new BusinessSearchImpl(new ModelLayerImpl(getActivity()), this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_business_name, null);
        ButterKnife.bind(this, rootView);
        businessListView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                if (page > 2)
                    businessList.loadMore();
                return true;
            }
        });

        int sheetColor = ContextCompat.getColor(getActivity(), R.color.app_origin);
        int fabColor = ContextCompat.getColor(getActivity(), R.color.white);

        materialSheetFab = new MaterialSheetFab(fab, fab_sheet, overlay, sheetColor, fabColor);
        materialSheetFab.setEventListener(new MaterialSheetFabEventListener() {
            @Override
            public void onShowSheet() {
                super.onShowSheet();
                EventBus.getDefault().post(new MessageSheetStateChanged(true));
                switch (bizSortFactor) {
                    case DATE:
                        fabAll.setTypeface(FontFace.getInstance(getActivity()).getVAZIR());
//                        fabPoular.setTypeface(FontFace.getInstance(getActivity()).getVAZIR());
                        fabRecent.setTypeface(FontFace.getInstance(getActivity()).getVAZIR_BOLD());
                        //
                        fabAll.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
//                        fabPoular.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                        fabRecent.setTextColor(ContextCompat.getColor(getActivity(), R.color.app_origin));
                        break;
                    case NAME:
                        fabAll.setTypeface(FontFace.getInstance(getActivity()).getVAZIR_BOLD());
//                        fabPoular.setTypeface(FontFace.getInstance(getActivity()).getVAZIR_BOLD());
                        fabRecent.setTypeface(FontFace.getInstance(getActivity()).getVAZIR());
                        //
                        fabAll.setTextColor(ContextCompat.getColor(getActivity(), R.color.app_origin));
//                        fabPoular.setTextColor(ContextCompat.getColor(getActivity(), R.color.app_origin));
                        fabRecent.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                        break;
                    default:
                        fabAll.setTypeface(FontFace.getInstance(getActivity()).getVAZIR_BOLD());
//                        fabPoular.setTypeface(FontFace.getInstance(getActivity()).getVAZIR_BOLD());
                        fabRecent.setTypeface(FontFace.getInstance(getActivity()).getVAZIR());
                        //
                        fabAll.setTextColor(ContextCompat.getColor(getActivity(), R.color.app_origin));
//                        fabPoular.setTextColor(ContextCompat.getColor(getActivity(), R.color.app_origin));
                        fabRecent.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
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
            materialSheetFab.hideSheet();
            bizSortFactor = BizSortFactor.NAME;
            businessList.load(bizSortFactor);
        });
//        fabPoular.setOnClickListener(view -> {
//            materialSheetFab.hideSheet();
//            businessList.load(BizSortFactor.NAME);
//        });
        fabRecent.setOnClickListener(view -> {
            materialSheetFab.hideSheet();
            bizSortFactor = BizSortFactor.DATE;
            businessList.load(bizSortFactor);
        });

        imgSearchImage.setOnClickListener(v -> {
            if (etSearchPhraseText.getText().toString().trim().length() > 0) {
                performBusinessSearch(etSearchPhraseText.getText().toString());
            }
        });

        businessListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent();
            intent.setClass(getActivity(), BusinessPaymentInfoActivity.class);
            intent.putExtra(Constants.BUSINESS_INFO, hamPayBusinessesAdapter.getItem(position));
            getActivity().startActivity(intent);
        });

        hamPayDialog = new HamPayDialog(getActivity());
        hamPayDialog.showWaitingDialog("");

        AppManager.setMobileTimeout(getActivity());

        pullToRefresh.setOnRefreshListener(() -> {
            hamPayBusinessesAdapter.clear();
            businessList.load(bizSortFactor);
        });
        businessList.load(bizSortFactor);
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        etSearchPhraseText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                performBusinessSearch(etSearchPhraseText.getText().toString());
                return true;
            }
            return false;
        });

        etSearchPhraseText.addTextChangedListener(new MyTextWatcher(text -> {
            if (text.length() == 0) {
                businessList.load(bizSortFactor);
            }
        }));

        return rootView;
    }

    private void performBusinessSearch(String searchTerm) {
        AppManager.setMobileTimeout(getActivity());
        inputMethodManager.hideSoftInputFromWindow(etSearchPhraseText.getWindowToken(), 0);
        businessSearch.search(searchTerm, bizSortFactor, true);
    }

    @Subscribe
    public void onBackPressed(MessageOnBackPressed onBackPressedOnPendingAct) {
        if (materialSheetFab.isSheetVisible()) {
            materialSheetFab.hideSheet();
        }
    }

    @Override
    public void showProgress() {
        if (hamPayBusinessesAdapter != null)
            hamPayBusinessesAdapter.clear();
        prg.setVisibility(View.VISIBLE);
        hamPayDialog.showWaitingDialog("");
    }

    @Override
    public void cancelProgress() {
        hamPayDialog.dismisWaitingDialog();
        pullToRefresh.setRefreshing(false);
        prg.setVisibility(View.GONE);
    }

    @Override
    public void onError() {
        cancelProgress();
        Toast.makeText(getActivity(), getString(R.string.msg_fail_business_list), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onListLoaded(boolean state, ResponseMessage<BusinessListResponse> data, String message) {
        if (state && data != null &&
                data.getService() != null &&
                data.getService().getBusinesses() != null &&
                data.getService().getBusinesses().size() > 0 &&
                data.getService().getResultStatus() == ResultStatus.SUCCESS) {
            if (hamPayBusinessesAdapter == null) {
                hamPayBusinessesAdapter = new HamPayBusinessesAdapter(getActivity(), AppManager.getAuthToken(getActivity()));
                businessListView.setAdapter(hamPayBusinessesAdapter);
            }
            hamPayBusinessesAdapter.addItems(data.getService().getBusinesses());
            EventLogger.getInstance(getActivity()).log(ServiceEvent.BUSINESS_LIST_SUCCESS);
        } else {
            EventLogger.getInstance(getActivity()).log(ServiceEvent.BUSINESS_LIST_FAILURE);
            hamPayBusinessesAdapter.clear();
        }

        if (state && data != null &&
                data.getService() != null &&
                data.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
            AppManager.logOut(getActivity());
        }
    }

    @Subscribe
    public void tabChanged(MessageTabChanged messageTabChanged) {
        if (messageTabChanged.getSelectedPosition() == 0) {
            if (materialSheetFab.isSheetVisible())
                materialSheetFab.hideSheet();
        }
    }

}
