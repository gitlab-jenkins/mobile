package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
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
import xyz.homapay.hampay.common.core.model.request.ContactsHampayEnabledRequest;
import xyz.homapay.hampay.common.core.model.request.TransactionListRequest;
import xyz.homapay.hampay.common.core.model.response.BusinessListResponse;
import xyz.homapay.hampay.common.core.model.response.TransactionListResponse;
import xyz.homapay.hampay.common.core.model.response.dto.BusinessDTO;
import xyz.homapay.hampay.common.core.model.response.dto.TransactionDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.adapter.HamPayBusinessesAdapter;
import xyz.homapay.hampay.mobile.android.adapter.UserTransactionAdapter;
import xyz.homapay.hampay.mobile.android.animation.Collapse;
import xyz.homapay.hampay.mobile.android.animation.Expand;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestContactHampayEnabled;
import xyz.homapay.hampay.mobile.android.async.RequestHamPayBusiness;
import xyz.homapay.hampay.mobile.android.async.RequestSearchHamPayBusiness;
import xyz.homapay.hampay.mobile.android.async.RequestUserTransaction;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.doblist.DobList;
import xyz.homapay.hampay.mobile.android.component.doblist.events.OnLoadMoreListener;
import xyz.homapay.hampay.mobile.android.component.doblist.exceptions.NoEmptyViewException;
import xyz.homapay.hampay.mobile.android.component.doblist.exceptions.NoListviewException;
import xyz.homapay.hampay.mobile.android.component.edittext.FacedEditText;
import xyz.homapay.hampay.mobile.android.component.material.ButtonRectangle;
import xyz.homapay.hampay.mobile.android.component.material.RippleView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

public class BusinessPurchaseActivity extends AppCompatActivity implements View.OnClickListener {



    private Context context;
    private Activity activity;

    private RelativeLayout name_rl;
    private FacedTextView name_title;
    private View name_sep;
    private RelativeLayout code_rl;
    private FacedTextView code_title;
    private View code_sep;

    private int selectedType = 1;

    private ListView businessListView;
    private LinearLayout find_business_purchase;

    private PersianEnglishDigit persianEnglishDigit;

    ButtonRectangle find_business_purchase_button;
    LinearLayout displayKeyboard;
    LinearLayout keyboard;
    String inputPurchaseCode = "";
    FacedTextView input_digit_1;
    FacedTextView input_digit_2;
    FacedTextView input_digit_3;
    FacedTextView input_digit_4;
    FacedTextView input_digit_5;
    FacedTextView input_digit_6;

    RippleView digit_1;
    RippleView digit_2;
    RippleView digit_3;
    RippleView digit_4;
    RippleView digit_5;
    RippleView digit_6;
    RippleView digit_7;
    RippleView digit_8;
    RippleView digit_9;
    RippleView digit_0;
    RippleView keyboard_help;
    RippleView backspace;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    HamPayDialog hamPayDialog;

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

        name_rl = (RelativeLayout)findViewById(R.id.name_rl);
        name_rl.setOnClickListener(this);
        name_title = (FacedTextView)findViewById(R.id.name_title);
        name_sep = (View)findViewById(R.id.name_sep);
        code_rl = (RelativeLayout)findViewById(R.id.code_rl);
        code_rl.setOnClickListener(this);
        code_title = (FacedTextView)findViewById(R.id.code_title);
        code_sep = (View)findViewById(R.id.code_sep);

        businessListView = (ListView)findViewById(R.id.businessListView);
        businessListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(context, PayBusinessActivity.class);
                intent.putExtra("business_name", businessDTOs.get(position).getTitle());
                intent.putExtra("business_code", businessDTOs.get(position).getCode());
                context.startActivity(intent);
            }
        });

        find_business_purchase = (LinearLayout)findViewById(R.id.find_business_purchase);

        find_business_purchase_button = (ButtonRectangle)findViewById(R.id.find_business_purchase_button);
        find_business_purchase_button.setOnClickListener(this);

        keyboard = (LinearLayout)findViewById(R.id.keyboard);
        displayKeyboard = (LinearLayout)findViewById(R.id.displayKeyboard);
        displayKeyboard.setOnClickListener(this);
        input_digit_1 = (FacedTextView)findViewById(R.id.input_digit_1);
        input_digit_2 = (FacedTextView) findViewById(R.id.input_digit_2);
        input_digit_3 = (FacedTextView) findViewById(R.id.input_digit_3);
        input_digit_4 = (FacedTextView) findViewById(R.id.input_digit_4);
        input_digit_5 = (FacedTextView) findViewById(R.id.input_digit_5);
        input_digit_6 = (FacedTextView) findViewById(R.id.input_digit_6);

        digit_1 = (RippleView) findViewById(R.id.digit_1);
        digit_1.setOnClickListener(this);
        digit_2 = (RippleView) findViewById(R.id.digit_2);
        digit_2.setOnClickListener(this);
        digit_3 = (RippleView) findViewById(R.id.digit_3);
        digit_3.setOnClickListener(this);
        digit_4 = (RippleView) findViewById(R.id.digit_4);
        digit_4.setOnClickListener(this);
        digit_5 = (RippleView) findViewById(R.id.digit_5);
        digit_5.setOnClickListener(this);
        digit_6 = (RippleView) findViewById(R.id.digit_6);
        digit_6.setOnClickListener(this);
        digit_7 = (RippleView) findViewById(R.id.digit_7);
        digit_7.setOnClickListener(this);
        digit_8 = (RippleView) findViewById(R.id.digit_8);
        digit_8.setOnClickListener(this);
        digit_9 = (RippleView) findViewById(R.id.digit_9);
        digit_9.setOnClickListener(this);
        digit_0 = (RippleView) findViewById(R.id.digit_0);
        digit_0.setOnClickListener(this);
        keyboard_help = (RippleView) findViewById(R.id.keyboard_help);
        keyboard_help.setOnClickListener(this);
        backspace = (RippleView)findViewById(R.id.backspace);
        backspace.setOnClickListener(this);


        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();

        hamPayBusinessesAdapter = new HamPayBusinessesAdapter(activity);

        hamPayGaTracker = ((HamPayApplication) getApplication())
                .getTracker(HamPayApplication.TrackerName.APP_TRACKER);


        inputMethodManager = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);

        searchImage = (ImageView) findViewById(R.id.searchImage);
        searchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchPhraseText.getText().toString().length() > 0) {
                    performBusinessSearch(searchPhraseText.getText().toString());
                }
            }
        });

        searchPhraseText = (FacedEditText) findViewById(R.id.searchPhraseText);

        searchPhraseText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().length() == 0) {
                    requestPageNumber = 0;
                    searchEnabled = false;
                    FINISHED_SCROLLING = false;
                    onLoadMore = false;
                    businessListRequest.setPageNumber(requestPageNumber);
                    businessListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
                    requestHamPayBusiness = new RequestHamPayBusiness(context, new RequestBusinessListTaskCompleteListener(searchEnabled));
                    requestHamPayBusiness.execute(businessListRequest);

                    inputMethodManager.hideSoftInputFromWindow(searchPhraseText.getWindowToken(), 0);

                    hamPayBusinessesAdapter.clear();
                    businessDTOs.clear();

                    hamPayDialog.showWaitingdDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchPhraseText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performBusinessSearch(searchPhraseText.getText().toString());
                    return true;
                }
                return false;
            }
        });

        businessDTOs = new ArrayList<BusinessDTO>();

        hamPayDialog = new HamPayDialog(activity);
        hamPayDialog.showWaitingdDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));

        if ((System.currentTimeMillis() - prefs.getLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis()) > Constants.MOBILE_TIME_OUT_INTERVAL)) {
            Intent intent = new Intent();
            intent.setClass(activity, HamPayLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
        }else {
            editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
            editor.commit();
            businessListRequest = new BusinessListRequest();
            businessListRequest.setPageNumber(requestPageNumber);
            businessListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);

            requestHamPayBusiness = new RequestHamPayBusiness(this, new RequestBusinessListTaskCompleteListener(searchEnabled));
            requestHamPayBusiness.execute(businessListRequest);
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.name_rl:
                if (keyboard.getVisibility() == View.VISIBLE){
                    new Collapse(keyboard).animate();
                }
                businessListView.setVisibility(View.VISIBLE);
                find_business_purchase.setVisibility(View.GONE);
                selectedType = 1;
                name_title.setTextColor(getResources().getColor(R.color.user_change_status));
                name_sep.setBackgroundColor(getResources().getColor(R.color.user_change_status));
                code_title.setTextColor(getResources().getColor(R.color.normal_text));
                code_sep.setBackgroundColor(getResources().getColor(R.color.normal_text));



                break;

            case R.id.code_rl:
                businessListView.setVisibility(View.GONE);
                find_business_purchase.setVisibility(View.VISIBLE);
                selectedType = 2;
                code_title.setTextColor(getResources().getColor(R.color.user_change_status));
                code_sep.setBackgroundColor(getResources().getColor(R.color.user_change_status));
                name_title.setTextColor(getResources().getColor(R.color.normal_text));
                name_sep.setBackgroundColor(getResources().getColor(R.color.normal_text));
                break;


            case R.id.find_business_purchase_button:

                    new Collapse(keyboard).animate();

                if (inputPurchaseCode.length() == 6) {
                    inputPurchaseCode = "";
                    Intent intent = new Intent();
                    intent.setClass(context, RequestBusinessPayDetailActivity.class);
                    startActivity(intent);
                    input_digit_1.setText("");
                    input_digit_2.setText("");
                    input_digit_3.setText("");
                    input_digit_4.setText("");
                    input_digit_5.setText("");
                    input_digit_6.setText("");
                    }else {
                    Toast.makeText(context, getString(R.string.msg_incorrect_pending_payment_code), Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.displayKeyboard:
                if (keyboard.getVisibility() != View.VISIBLE)
                    new Expand(keyboard).animate();
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

        if ((System.currentTimeMillis() - prefs.getLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis()) > Constants.MOBILE_TIME_OUT_INTERVAL)) {
            Intent intent = new Intent();
            intent.setClass(activity, HamPayLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
        }else {
            editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
            editor.commit();
            businessSearchRequest = new BusinessSearchRequest();
            businessSearchRequest.setPageNumber(requestPageNumber);
            businessSearchRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
            businessSearchRequest.setTerm(searchTerm);
            requestSearchHamPayBusiness = new RequestSearchHamPayBusiness(activity, new RequestBusinessListTaskCompleteListener(searchEnabled));
            requestSearchHamPayBusiness.execute(businessSearchRequest);
        }


        inputMethodManager.hideSoftInputFromWindow(searchPhraseText.getWindowToken(), 0);

        hamPayBusinessesAdapter.clear();
        businessDTOs.clear();

        hamPayDialog.showWaitingdDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));

    }


    public class RequestBusinessListTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<BusinessListResponse>> {

        List<BusinessDTO> newBusinessDTOs;
        boolean searchEnabled;

        public RequestBusinessListTaskCompleteListener(boolean searchEnabled){
            this.searchEnabled = searchEnabled;
        }

        @Override
        public void onTaskComplete(ResponseMessage<BusinessListResponse> businessListResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            if (businessListResponseMessage != null) {
                if (businessListResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    newBusinessDTOs = businessListResponseMessage.getService().getBusinesses();
                    businessDTOs.addAll(newBusinessDTOs);
                    if (businessDTOs != null) {
                        if (newBusinessDTOs.size() == 0 || newBusinessDTOs.size() < Constants.DEFAULT_PAGE_SIZE) {
                            FINISHED_SCROLLING = true;
                        }
                        if (businessDTOs.size() > 0) {
                            requestPageNumber++;
                            if (onLoadMore) {
                                if (newBusinessDTOs != null)
                                    addDummyData(newBusinessDTOs.size());
                            } else {
                                initDobList(getWindow().getDecorView().getRootView(), businessListView);
                                businessListView.setAdapter(hamPayBusinessesAdapter);
                            }
                        }
                    }

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Business List")
                            .setAction("Fetch")
                            .setLabel("Success")
                            .build());

                }else {
                    if (!searchEnabled) {
                        businessListRequest.setPageNumber(requestPageNumber);
                        businessListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
                        requestHamPayBusiness = new RequestHamPayBusiness(activity, new RequestBusinessListTaskCompleteListener(false));
                        new HamPayDialog(activity).showFailBusinessListDialog(requestHamPayBusiness, businessListRequest,
                                businessListResponseMessage.getService().getResultStatus().getCode(),
                                businessListResponseMessage.getService().getResultStatus().getDescription());
                    }else {
                        businessSearchRequest.setPageNumber(requestPageNumber);
                        businessSearchRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
                        requestSearchHamPayBusiness = new RequestSearchHamPayBusiness(activity, new RequestBusinessListTaskCompleteListener(true));
                        new HamPayDialog(activity).showFailBusinessSearchListDialog(requestSearchHamPayBusiness, businessSearchRequest,
                                businessListResponseMessage.getService().getResultStatus().getCode(),
                                businessListResponseMessage.getService().getResultStatus().getDescription());
//                        requestSearchHamPayBusiness.execute(businessSearchRequest);
                    }

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Business List")
                            .setAction("Fetch")
                            .setLabel("Fail(Server)")
                            .build());
                }
            }else {
                if (!searchEnabled) {
                    businessListRequest.setPageNumber(requestPageNumber);
                    businessListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
                    requestHamPayBusiness = new RequestHamPayBusiness(activity, new RequestBusinessListTaskCompleteListener(false));
                    new HamPayDialog(activity).showFailBusinessListDialog(requestHamPayBusiness, businessListRequest,
                            Constants.LOCAL_ERROR_CODE,
                            getString(R.string.msg_fail_business_list));
                }else {
                    businessSearchRequest.setPageNumber(requestPageNumber);
                    businessSearchRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
                    requestSearchHamPayBusiness = new RequestSearchHamPayBusiness(activity, new RequestBusinessListTaskCompleteListener(true));
                    new HamPayDialog(activity).showFailBusinessSearchListDialog(requestSearchHamPayBusiness, businessSearchRequest,
                            Constants.LOCAL_ERROR_CODE,
                            getString(R.string.msg_fail_business_search_list));
//                    requestSearchHamPayBusiness.execute(businessSearchRequest);
                }

                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Business List")
                        .setAction("Fetch")
                        .setLabel("Fail(Mobile)")
                        .build());
            }
        }

        @Override
        public void onTaskPreRun() {
        }

        private void initDobList(View rootView, ListView listView) {

            dobList = new DobList();
            try {

                dobList.register(listView);

                dobList.addDefaultLoadingFooterView();

                View noItems = rootView.findViewById(R.id.noItems);
                dobList.setEmptyView(noItems);

                dobList.setOnLoadMoreListener(new OnLoadMoreListener() {

                    @Override
                    public void onLoadMore(final int totalItemCount) {

                        onLoadMore = true;

                        if (!FINISHED_SCROLLING) {
                            if (!searchEnabled) {
                                businessListRequest.setPageNumber(requestPageNumber);
                                businessListRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
                                requestHamPayBusiness = new RequestHamPayBusiness(activity, new RequestBusinessListTaskCompleteListener(false));
                                requestHamPayBusiness.execute(businessListRequest);
                            } else {
                                businessSearchRequest.setPageNumber(requestPageNumber);
                                businessSearchRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
                                requestSearchHamPayBusiness = new RequestSearchHamPayBusiness(activity, new RequestBusinessListTaskCompleteListener(searchEnabled));
                                requestSearchHamPayBusiness.execute(businessSearchRequest);
                            }
                        } else
                            dobList.finishLoading();

                    }
                });

            } catch (NoListviewException e) {
                e.printStackTrace();
            }

            try {

                dobList.startCentralLoading();

            } catch (NoEmptyViewException e) {
                e.printStackTrace();
            }

            addDummyData(businessDTOs.size());
        }

        protected void addDummyData(int itemsCount) {
            addItems(hamPayBusinessesAdapter.getCount(), hamPayBusinessesAdapter.getCount() + itemsCount);
            dobList.finishLoading();
        }

        protected void addItems(int from, int to) {
            for (int i = from; i < to; i++) {
                hamPayBusinessesAdapter.addItem(businessDTOs.get(i));
            }
        }
    }


    private void inputDigit(String digit){
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        if (inputPurchaseCode.length() <= 5) {

            switch (inputPurchaseCode.length()) {
                case 0:
                    if (digit.equalsIgnoreCase("d")) {
                        input_digit_1.setText("");
//                        input_digit_1.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    } else {
                        input_digit_1.setText(persianEnglishDigit.E2P(digit));
//                        input_digit_1.setBackgroundColor(Color.TRANSPARENT);
                    }
                    input_digit_2.setText("");
//                    input_digit_2.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    input_digit_3.setText("");
//                    input_digit_3.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    input_digit_4.setText("");
//                    input_digit_4.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    input_digit_5.setText("");
//                    input_digit_5.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    input_digit_6.setText("");
//                    input_digit_6.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    vibrator.vibrate(20);
                    break;

                case 1:
                    if (digit.equalsIgnoreCase("d")) {
                        input_digit_2.setText("");
//                        input_digit_2.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    } else {
                        input_digit_2.setText(persianEnglishDigit.E2P(digit));
//                        input_digit_2.setBackgroundColor(Color.TRANSPARENT);
                    }
                    input_digit_3.setText("");
//                    input_digit_3.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    input_digit_4.setText("");
//                    input_digit_4.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    input_digit_5.setText("");
//                    input_digit_5.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    input_digit_6.setText("");
//                    input_digit_6.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    vibrator.vibrate(20);

                    break;
                case 2:
                    if (digit.equalsIgnoreCase("d")) {
                        input_digit_3.setText("");
//                        input_digit_3.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    } else {
                        input_digit_3.setText(persianEnglishDigit.E2P(digit));
//                        input_digit_3.setBackgroundColor(Color.TRANSPARENT);
                    }
                    input_digit_4.setText("");
//                    input_digit_4.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    input_digit_5.setText("");
//                    input_digit_5.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    input_digit_6.setText("");
//                    input_digit_6.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    vibrator.vibrate(20);
                    break;
                case 3:
                    if (digit.equalsIgnoreCase("d")) {
                        input_digit_4.setText("");
//                        input_digit_4.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    } else {
                        input_digit_4.setText(persianEnglishDigit.E2P(digit));
//                        input_digit_4.setBackgroundColor(Color.TRANSPARENT);
                    }
                    input_digit_5.setText("");
//                    input_digit_5.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    input_digit_6.setText("");
//                    input_digit_6.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    vibrator.vibrate(20);
                    break;
                case 4:
                    if (digit.equalsIgnoreCase("d")) {
                        input_digit_5.setText("");
//                        input_digit_5.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    } else {
                        input_digit_5.setText(persianEnglishDigit.E2P(digit));
//                        input_digit_5.setBackgroundColor(Color.TRANSPARENT);
                    }
                    input_digit_6.setText("");
//                    input_digit_6.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    vibrator.vibrate(20);
                    break;
                case 5:
                    if (digit.equalsIgnoreCase("d")) {
                        input_digit_6.setText("");
//                        input_digit_6.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    } else {
                        input_digit_6.setText(persianEnglishDigit.E2P(digit));
//                        input_digit_6.setBackgroundColor(Color.TRANSPARENT);
                    }
                    vibrator.vibrate(20);
                    break;
//                case 6:
//                    if (digit.equalsIgnoreCase("d")) {
//                        input_digit_6.setText("");
//                        input_digit_6.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
//                    } else {
//                        input_digit_6.setText(persianEnglishDigit.E2P(digit));
//                        input_digit_6.setBackgroundColor(Color.TRANSPARENT);
//                    }
//                    vibrator.vibrate(20);
//                    break;
            }

        }

        if (digit.contains("d")){
            if (inputPurchaseCode.length() > 0) {
                inputPurchaseCode = inputPurchaseCode.substring(0, inputPurchaseCode.length() - 1);
                if (inputPurchaseCode.length() == 5){
                    input_digit_6.setText("");
//                    input_digit_6.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                }
                if (inputPurchaseCode.length() == 4){
                    input_digit_5.setText("");
//                    input_digit_5.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                }
                else if (inputPurchaseCode.length() == 3){
                    input_digit_4.setText("");
//                    input_digit_4.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                }
                else if (inputPurchaseCode.length() == 2){
                    input_digit_3.setText("");
//                    input_digit_3.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                }
                else if (inputPurchaseCode.length() == 1){
                    input_digit_2.setText("");
//                    input_digit_2.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                }
                else if (inputPurchaseCode.length() == 0){
                    input_digit_1.setText("");
//                    input_digit_1.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
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
