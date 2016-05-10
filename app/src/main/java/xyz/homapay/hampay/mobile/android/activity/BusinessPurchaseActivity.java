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
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.List;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.BusinessListRequest;
import xyz.homapay.hampay.common.core.model.request.BusinessSearchRequest;
import xyz.homapay.hampay.common.core.model.response.BusinessListResponse;
import xyz.homapay.hampay.common.core.model.response.dto.BusinessDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.adapter.HamPayBusinessesAdapter;
import xyz.homapay.hampay.mobile.android.animation.Collapse;
import xyz.homapay.hampay.mobile.android.animation.Expand;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestHamPayBusiness;
import xyz.homapay.hampay.mobile.android.async.RequestSearchHamPayBusiness;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.doblist.DobList;
import xyz.homapay.hampay.mobile.android.component.doblist.events.OnLoadMoreListener;
import xyz.homapay.hampay.mobile.android.component.doblist.exceptions.NoEmptyViewException;
import xyz.homapay.hampay.mobile.android.component.doblist.exceptions.NoListviewException;
import xyz.homapay.hampay.mobile.android.component.edittext.FacedEditText;
import xyz.homapay.hampay.mobile.android.component.material.ButtonRectangle;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

public class BusinessPurchaseActivity extends AppCompatActivity implements View.OnClickListener {



    private Context context;
    private Activity activity;

//    private ListView businessListView;
    private LinearLayout find_business_purchase;

    private PersianEnglishDigit persianEnglishDigit;

    ImageView payment_button;
    LinearLayout displayKeyboard;
    LinearLayout keyboard;
    String inputPurchaseCode = "";
    FacedTextView input_digit_1;
    FacedTextView input_digit_2;
    FacedTextView input_digit_3;
    FacedTextView input_digit_4;
    FacedTextView input_digit_5;
    FacedTextView input_digit_6;

    FacedTextView digit_1;
    FacedTextView digit_2;
    FacedTextView digit_3;
    FacedTextView digit_4;
    FacedTextView digit_5;
    FacedTextView digit_6;
    FacedTextView digit_7;
    FacedTextView digit_8;
    FacedTextView digit_9;
    FacedTextView digit_0;
    FacedTextView keyboard_dismiss;
    RelativeLayout backspace;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    HamPayDialog hamPayDialog;

    RelativeLayout businesses_list;

    private boolean onLoadMore = false;
    DobList dobList;


    private List<BusinessDTO> businessDTOs;

    private HamPayBusinessesAdapter hamPayBusinessesAdapter;

    private boolean FINISHED_SCROLLING = false;


    ImageView searchImage;
    FacedEditText searchPhraseText;

    InputMethodManager inputMethodManager;


    BusinessListRequest businessListRequest;
    BusinessSearchRequest businessSearchRequest;

    RequestSearchHamPayBusiness requestSearchHamPayBusiness;
    RequestHamPayBusiness requestHamPayBusiness;

    int requestPageNumber = 0;

    boolean searchEnabled = false;

    Tracker hamPayGaTracker;

    private Dialog dialog;

    private RelativeLayout search_layout;

    public void backActionBar(View view){
        finish();
    }


    @Override
    public void onBackPressed() {

        if (keyboard.getVisibility() == View.VISIBLE){
            new Collapse(keyboard).animate();
        }
        else {
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        HamPayApplication.setAppSate(AppState.Stoped);
        if (requestHamPayBusiness != null){
            if (!requestHamPayBusiness.isCancelled())
                requestHamPayBusiness.cancel(true);
        }

        if (requestSearchHamPayBusiness != null){
            if (!requestSearchHamPayBusiness.isCancelled())
                requestSearchHamPayBusiness.cancel(true);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_purchase);

        context = this;
        activity = BusinessPurchaseActivity.this;

        persianEnglishDigit = new PersianEnglishDigit();

//        businessListView = (ListView)findViewById(R.id.businessListView);
//        businessListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent();
//                intent.setClass(context, BusinessPaymentInfoActivity.class);
//                intent.putExtra(Constants.BUSINESS_INFO, businessDTOs.get(position));
//                context.startActivity(intent);
//            }
//        });

        find_business_purchase = (LinearLayout)findViewById(R.id.find_business_purchase);

        payment_button = (ImageView)findViewById(R.id.payment_button);
        payment_button.setOnClickListener(this);

        keyboard = (LinearLayout)findViewById(R.id.keyboard);
        displayKeyboard = (LinearLayout)findViewById(R.id.displayKeyboard);
        displayKeyboard.setOnClickListener(this);
        input_digit_1 = (FacedTextView)findViewById(R.id.input_digit_1);
        input_digit_2 = (FacedTextView) findViewById(R.id.input_digit_2);
        input_digit_3 = (FacedTextView) findViewById(R.id.input_digit_3);
        input_digit_4 = (FacedTextView) findViewById(R.id.input_digit_4);
        input_digit_5 = (FacedTextView) findViewById(R.id.input_digit_5);
        input_digit_6 = (FacedTextView) findViewById(R.id.input_digit_6);

        digit_1 = (FacedTextView) findViewById(R.id.digit_1);
        digit_1.setOnClickListener(this);
        digit_2 = (FacedTextView) findViewById(R.id.digit_2);
        digit_2.setOnClickListener(this);
        digit_3 = (FacedTextView) findViewById(R.id.digit_3);
        digit_3.setOnClickListener(this);
        digit_4 = (FacedTextView) findViewById(R.id.digit_4);
        digit_4.setOnClickListener(this);
        digit_5 = (FacedTextView) findViewById(R.id.digit_5);
        digit_5.setOnClickListener(this);
        digit_6 = (FacedTextView) findViewById(R.id.digit_6);
        digit_6.setOnClickListener(this);
        digit_7 = (FacedTextView) findViewById(R.id.digit_7);
        digit_7.setOnClickListener(this);
        digit_8 = (FacedTextView) findViewById(R.id.digit_8);
        digit_8.setOnClickListener(this);
        digit_9 = (FacedTextView) findViewById(R.id.digit_9);
        digit_9.setOnClickListener(this);
        digit_0 = (FacedTextView) findViewById(R.id.digit_0);
        digit_0.setOnClickListener(this);
        keyboard_dismiss = (FacedTextView) findViewById(R.id.keyboard_dismiss);
        keyboard_dismiss.setOnClickListener(this);
        backspace = (RelativeLayout) findViewById(R.id.backspace);
        backspace.setOnClickListener(this);

        businesses_list = (RelativeLayout)findViewById(R.id.businesses_list);
        businesses_list.setOnClickListener(this);


        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();

        hamPayBusinessesAdapter = new HamPayBusinessesAdapter(activity, prefs.getString(Constants.LOGIN_TOKEN_ID, ""));

        hamPayGaTracker = ((HamPayApplication) getApplication())
                .getTracker(HamPayApplication.TrackerName.APP_TRACKER);


        inputMethodManager = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);

//        searchImage = (ImageView) findViewById(R.id.searchImage);
//        searchImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (searchPhraseText.getText().toString().length() > 0) {
//                    performBusinessSearch(searchPhraseText.getText().toString());
//                }
//            }
//        });

        searchPhraseText = (FacedEditText) findViewById(R.id.searchPhraseText);

//        searchPhraseText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                if (s.toString().length() == 0) {
//                    requestPageNumber = 0;
//                    searchEnabled = false;
//                    FINISHED_SCROLLING = false;
//                    onLoadMore = false;
//                    businessListRequest.setPageNumber(requestPageNumber);
//                    businessListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
//                    requestHamPayBusiness = new RequestHamPayBusiness(context, new RequestBusinessListTaskCompleteListener(searchEnabled));
//                    requestHamPayBusiness.execute(businessListRequest);
//
//                    inputMethodManager.hideSoftInputFromWindow(searchPhraseText.getWindowToken(), 0);
//
//                    hamPayBusinessesAdapter.clear();
//                    businessDTOs.clear();
//
//                    hamPayDialog.showWaitingdDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
//
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });

//        searchPhraseText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    performBusinessSearch(searchPhraseText.getText().toString());
//                    return true;
//                }
//                return false;
//            }
//        });

        businessDTOs = new ArrayList<BusinessDTO>();

        hamPayDialog = new HamPayDialog(activity);
//        hamPayDialog.showWaitingdDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
//
//        if ((System.currentTimeMillis() - prefs.getLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis()) > Constants.MOBILE_TIME_OUT_INTERVAL)) {
//            Intent intent = new Intent();
//            intent.setClass(activity, HamPayLoginActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            finish();
//            startActivity(intent);
//        }else {
//            editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
//            editor.commit();
//            businessListRequest = new BusinessListRequest();
//            businessListRequest.setPageNumber(requestPageNumber);
//            businessListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
//
//            requestHamPayBusiness = new RequestHamPayBusiness(this, new RequestBusinessListTaskCompleteListener(searchEnabled));
//            requestHamPayBusiness.execute(businessListRequest);
//        }


    }

    @Override
    public void onClick(View v) {

        Intent intent;

        switch (v.getId()){

            case R.id.businesses_list:
                intent = new Intent();
                intent.setClass(activity, BusinessesListActivity.class);
                startActivity(intent);
                break;

            case R.id.payment_button:

                new Collapse(keyboard).animate();

                if (inputPurchaseCode.length() == 6) {
                    intent = new Intent();
                    intent.putExtra(Constants.BUSINESS_PURCHASE_CODE, inputPurchaseCode);
                    intent.setClass(context, RequestBusinessPayDetailActivity.class);
                    startActivity(intent);
                    input_digit_1.setText("");
                    input_digit_2.setText("");
                    input_digit_3.setText("");
                    input_digit_4.setText("");
                    input_digit_5.setText("");
                    input_digit_6.setText("");
                    inputPurchaseCode = "";
                }else {
                    Toast.makeText(context, getString(R.string.msg_incorrect_pending_payment_code), Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.displayKeyboard:
                if (keyboard.getVisibility() != View.VISIBLE)
                    new Expand(keyboard).animate();
                break;

            case R.id.keyboard_dismiss:
                if (keyboard.getVisibility() == View.VISIBLE)
                    new Collapse(keyboard).animate();
                break;

            case R.id.digit_1:
                inputDigit("1");
                break;

            case R.id.rect:
                inputDigit("1");
                break;

            case R.id.digit_2:
                inputDigit("2");
                break;

            case R.id.digit_3:
                inputDigit("3");
                break;

            case R.id.digit_4:
                inputDigit("4");
                break;

            case R.id.digit_5:
                inputDigit("5");
                break;

            case R.id.digit_6:
                inputDigit("6");
                break;

            case R.id.digit_7:
                inputDigit("7");
                break;

            case R.id.digit_8:
                inputDigit("8");
                break;

            case R.id.digit_9:
                inputDigit("9");
                break;

            case R.id.digit_0:
                inputDigit("0");
                break;

            case R.id.backspace:
                inputDigit("d");
                break;

        }
    }

    private void performBusinessSearch(String searchTerm) {
        requestPageNumber = 0;
        searchEnabled = true;
        FINISHED_SCROLLING = false;
        onLoadMore = false;

//        if ((System.currentTimeMillis() - prefs.getLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis()) > Constants.MOBILE_TIME_OUT_INTERVAL)) {
//            Intent intent = new Intent();
//            intent.setClass(activity, HamPayLoginActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            finish();
//            startActivity(intent);
//        }else {
//            editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
//            editor.commit();
//            businessSearchRequest = new BusinessSearchRequest();
//            businessSearchRequest.setPageNumber(requestPageNumber);
//            businessSearchRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
//            businessSearchRequest.setTerm(searchTerm);
//            requestSearchHamPayBusiness = new RequestSearchHamPayBusiness(activity, new RequestBusinessListTaskCompleteListener(searchEnabled));
//            requestSearchHamPayBusiness.execute(businessSearchRequest);
//        }


        inputMethodManager.hideSoftInputFromWindow(searchPhraseText.getWindowToken(), 0);

        hamPayBusinessesAdapter.clear();
        businessDTOs.clear();

        hamPayDialog.showWaitingdDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));

    }


//    public class RequestBusinessListTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<BusinessListResponse>> {
//
//        List<BusinessDTO> newBusinessDTOs;
//        boolean searchEnabled;
//
//        public RequestBusinessListTaskCompleteListener(boolean searchEnabled){
//            this.searchEnabled = searchEnabled;
//        }
//
//        @Override
//        public void onTaskComplete(ResponseMessage<BusinessListResponse> businessListResponseMessage) {
//
//            hamPayDialog.dismisWaitingDialog();
//
//            if (businessListResponseMessage != null) {
//                if (businessListResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
//                    newBusinessDTOs = businessListResponseMessage.getService().getBusinesses();
//                    businessDTOs.addAll(newBusinessDTOs);
//                    if (businessDTOs != null) {
//                        if (newBusinessDTOs.size() == 0 || newBusinessDTOs.size() < Constants.DEFAULT_PAGE_SIZE) {
//                            FINISHED_SCROLLING = true;
//                        }
//                        if (businessDTOs.size() > 0) {
//                            requestPageNumber++;
//                            if (onLoadMore) {
//                                if (newBusinessDTOs != null)
//                                    addDummyData(newBusinessDTOs.size());
//                            } else {
//                                initDobList(getWindow().getDecorView().getRootView(), businessListView);
//                                businessListView.setAdapter(hamPayBusinessesAdapter);
//                            }
//                        }
//                    }
//
//                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
//                            .setCategory("Business List")
//                            .setAction("Fetch")
//                            .setLabel("Success")
//                            .build());
//
//                }else {
//                    if (!searchEnabled) {
//                        businessListRequest.setPageNumber(requestPageNumber);
//                        businessListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
//                        requestHamPayBusiness = new RequestHamPayBusiness(activity, new RequestBusinessListTaskCompleteListener(false));
//                        new HamPayDialog(activity).showFailBusinessListDialog(requestHamPayBusiness, businessListRequest,
//                                businessListResponseMessage.getService().getResultStatus().getCode(),
//                                businessListResponseMessage.getService().getResultStatus().getDescription());
//                    }else {
//                        businessSearchRequest.setPageNumber(requestPageNumber);
//                        businessSearchRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
//                        requestSearchHamPayBusiness = new RequestSearchHamPayBusiness(activity, new RequestBusinessListTaskCompleteListener(true));
//                        new HamPayDialog(activity).showFailBusinessSearchListDialog(requestSearchHamPayBusiness, businessSearchRequest,
//                                businessListResponseMessage.getService().getResultStatus().getCode(),
//                                businessListResponseMessage.getService().getResultStatus().getDescription());
////                        requestSearchHamPayBusiness.execute(businessSearchRequest);
//                    }
//
//                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
//                            .setCategory("Business List")
//                            .setAction("Fetch")
//                            .setLabel("Fail(Server)")
//                            .build());
//                }
//            }else {
//                if (!searchEnabled) {
//                    businessListRequest.setPageNumber(requestPageNumber);
//                    businessListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
//                    requestHamPayBusiness = new RequestHamPayBusiness(activity, new RequestBusinessListTaskCompleteListener(false));
//                    new HamPayDialog(activity).showFailBusinessListDialog(requestHamPayBusiness, businessListRequest,
//                            Constants.LOCAL_ERROR_CODE,
//                            getString(R.string.msg_fail_business_list));
//                }else {
//                    businessSearchRequest.setPageNumber(requestPageNumber);
//                    businessSearchRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
//                    requestSearchHamPayBusiness = new RequestSearchHamPayBusiness(activity, new RequestBusinessListTaskCompleteListener(true));
//                    new HamPayDialog(activity).showFailBusinessSearchListDialog(requestSearchHamPayBusiness, businessSearchRequest,
//                            Constants.LOCAL_ERROR_CODE,
//                            getString(R.string.msg_fail_business_search_list));
////                    requestSearchHamPayBusiness.execute(businessSearchRequest);
//                }
//
//                hamPayGaTracker.send(new HitBuilders.EventBuilder()
//                        .setCategory("Business List")
//                        .setAction("Fetch")
//                        .setLabel("Fail(Mobile)")
//                        .build());
//            }
//        }
//
//        @Override
//        public void onTaskPreRun() {
//        }
//
//        private void initDobList(View rootView, ListView listView) {
//
//            dobList = new DobList();
//            try {
//
//                dobList.register(listView);
//
//                dobList.addDefaultLoadingFooterView();
//
//                View noItems = rootView.findViewById(R.id.noItems);
//                dobList.setEmptyView(noItems);
//
//                dobList.setOnLoadMoreListener(new OnLoadMoreListener() {
//
//                    @Override
//                    public void onLoadMore(final int totalItemCount) {
//
//                        onLoadMore = true;
//
//                        if (!FINISHED_SCROLLING) {
//                            if (!searchEnabled) {
//                                businessListRequest.setPageNumber(requestPageNumber);
//                                businessListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
//                                requestHamPayBusiness = new RequestHamPayBusiness(activity, new RequestBusinessListTaskCompleteListener(false));
//                                requestHamPayBusiness.execute(businessListRequest);
//                            } else {
//                                businessSearchRequest.setPageNumber(requestPageNumber);
//                                businessSearchRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
//                                requestSearchHamPayBusiness = new RequestSearchHamPayBusiness(activity, new RequestBusinessListTaskCompleteListener(searchEnabled));
//                                requestSearchHamPayBusiness.execute(businessSearchRequest);
//                            }
//                        } else
//                            dobList.finishLoading();
//
//                    }
//                });
//
//            } catch (NoListviewException e) {
//                e.printStackTrace();
//            }
//
//            try {
//
//                dobList.startCentralLoading();
//
//            } catch (NoEmptyViewException e) {
//                e.printStackTrace();
//            }
//
//            addDummyData(businessDTOs.size());
//        }
//
//        protected void addDummyData(int itemsCount) {
//            addItems(hamPayBusinessesAdapter.getCount(), hamPayBusinessesAdapter.getCount() + itemsCount);
//            dobList.finishLoading();
//        }
//
//        protected void addItems(int from, int to) {
//            for (int i = from; i < to; i++) {
//                hamPayBusinessesAdapter.addItem(businessDTOs.get(i));
//            }
//        }
//    }


    private void inputDigit(String digit){
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        if (inputPurchaseCode.length() <= 5) {

            switch (inputPurchaseCode.length()) {
                case 0:
                    if (digit.equalsIgnoreCase("d")) {
                        input_digit_1.setText("");
                    } else {
                        input_digit_1.setText(persianEnglishDigit.E2P(digit));
                    }
                    input_digit_2.setText("");
                    input_digit_3.setText("");
                    input_digit_4.setText("");
                    input_digit_5.setText("");
                    input_digit_6.setText("");
                    vibrator.vibrate(20);
                    break;

                case 1:
                    if (digit.equalsIgnoreCase("d")) {
                        input_digit_2.setText("");
                    } else {
                        input_digit_2.setText(persianEnglishDigit.E2P(digit));
                    }
                    input_digit_3.setText("");
                    input_digit_4.setText("");
                    input_digit_5.setText("");
                    input_digit_6.setText("");
                    vibrator.vibrate(20);

                    break;
                case 2:
                    if (digit.equalsIgnoreCase("d")) {
                        input_digit_3.setText("");
                    } else {
                        input_digit_3.setText(persianEnglishDigit.E2P(digit));
                    }
                    input_digit_4.setText("");
                    input_digit_5.setText("");
                    input_digit_6.setText("");
                    vibrator.vibrate(20);
                    break;
                case 3:
                    if (digit.equalsIgnoreCase("d")) {
                        input_digit_4.setText("");
                    } else {
                        input_digit_4.setText(persianEnglishDigit.E2P(digit));
                    }
                    input_digit_5.setText("");
                    input_digit_6.setText("");
                    vibrator.vibrate(20);
                    break;
                case 4:
                    if (digit.equalsIgnoreCase("d")) {
                        input_digit_5.setText("");
                    } else {
                        input_digit_5.setText(persianEnglishDigit.E2P(digit));
                    }
                    input_digit_6.setText("");
                    vibrator.vibrate(20);
                    break;
                case 5:
                    if (digit.equalsIgnoreCase("d")) {
                        input_digit_6.setText("");
                    } else {
                        input_digit_6.setText(persianEnglishDigit.E2P(digit));
                    }
                    vibrator.vibrate(20);
                    break;
            }

        }

        if (digit.contains("d")){
            if (inputPurchaseCode.length() > 0) {
                inputPurchaseCode = inputPurchaseCode.substring(0, inputPurchaseCode.length() - 1);
                if (inputPurchaseCode.length() == 5){
                    input_digit_6.setText("");
                }
                if (inputPurchaseCode.length() == 4){
                    input_digit_5.setText("");
                }
                else if (inputPurchaseCode.length() == 3){
                    input_digit_4.setText("");
                }
                else if (inputPurchaseCode.length() == 2){
                    input_digit_3.setText("");
                }
                else if (inputPurchaseCode.length() == 1){
                    input_digit_2.setText("");
                }
                else if (inputPurchaseCode.length() == 0){
                    input_digit_1.setText("");
                }
            }
        }
        else {
            if (inputPurchaseCode.length() <= 5) {
                inputPurchaseCode += digit;
            }
        }
    }

}
