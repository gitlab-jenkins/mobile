package com.hampay.mobile.android.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.BusinessSearchRequest;
import com.hampay.common.core.model.response.BusinessListResponse;
import com.hampay.common.core.model.response.dto.BusinessDTO;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.activity.PayBusinessActivity;
import com.hampay.mobile.android.adapter.HamPayBusinessesAdapter;
import com.hampay.mobile.android.component.FacedEditText;
import com.hampay.mobile.android.component.doblist.DobList;
import com.hampay.mobile.android.component.doblist.events.OnLoadMoreListener;
import com.hampay.mobile.android.component.doblist.exceptions.NoEmptyViewException;
import com.hampay.mobile.android.component.doblist.exceptions.NoListviewException;
import com.hampay.mobile.android.webservice.WebServices;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amir on 6/5/15.
 */
public class PayToBusinessFragment extends Fragment {


    private ListView businessListView;

    RelativeLayout loading_rl;

    private ResponseMessage<BusinessListResponse> businessListResponse;

    private List<BusinessDTO> businessDTOs;

    private HamPayBusinessesAdapter hamPayBusinessesAdapter;

    private boolean FINISHED_SCROLLING = false;

    View rootView;

    ImageView searchImage;
    FacedEditText searchPhraseText;
    BusinessSearchRequest businessSearchRequest;

    InputMethodManager inputMethodManager;

    public PayToBusinessFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hamPayBusinessesAdapter = new HamPayBusinessesAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_pay_to_business, container, false);

        inputMethodManager = (InputMethodManager)getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);

        searchImage = (ImageView)rootView.findViewById(R.id.searchImage);
        searchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchPhraseText.getText().toString().length() > 0){
                    performBusinessSearch(true);
                }
            }
        });

        searchPhraseText = (FacedEditText)rootView.findViewById(R.id.searchPhraseText);

        searchPhraseText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (count == 0){
                    performBusinessSearch(false);
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



                    performBusinessSearch(true);
                    return true;
                }
                return false;
            }
        });

        businessListResponse = new ResponseMessage<BusinessListResponse>();
        businessDTOs = new ArrayList<BusinessDTO>();

        loading_rl = (RelativeLayout)rootView.findViewById(R.id.loading_rl);

        businessListView = (ListView)rootView.findViewById(R.id.businessListView);

        businessListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), PayBusinessActivity.class);
                intent.putExtra("business_name", businessDTOs.get(position).getTitle());
                intent.putExtra("business_code", businessDTOs.get(position).getCode());
                startActivity(intent);

            }
        });

        new HttpHamPay‌Business().execute(false);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    private void performBusinessSearch(boolean search){

        businessSearchRequest = new BusinessSearchRequest();
        businessSearchRequest.setPageNumber(1);
        businessSearchRequest.setPageSize(10);
        businessSearchRequest.setTerm(searchPhraseText.getText().toString());
        inputMethodManager.hideSoftInputFromWindow(searchPhraseText.getWindowToken(), 0);

        hamPayBusinessesAdapter.clear();
        businessDTOs.clear();
        loading_rl.setVisibility(View.VISIBLE);

        new HttpHamPay‌Business().execute(search);

    }

    private boolean onLoadMore = false;
    private boolean searchEnable = false;

    public class HttpHamPay‌Business extends AsyncTask<Boolean, Void, String> {

        List<BusinessDTO> newBusinessDTOs;


        @Override
        protected String doInBackground(Boolean... params) {

            WebServices webServices = new WebServices(getActivity());

            searchEnable = params[0];

            if (params[0]) {

                newBusinessDTOs = webServices.searchBusinessList(businessSearchRequest).getService().getBusinesses();
                businessDTOs.addAll(newBusinessDTOs);

            }else {
                newBusinessDTOs = webServices.getHamPayBusiness().getService().getBusinesses();
                businessDTOs.addAll(newBusinessDTOs);
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            loading_rl.setVisibility(View.GONE);

            if(businessDTOs != null) {

                if(onLoadMore){
                    if (newBusinessDTOs != null)
                        addDummyData(newBusinessDTOs.size());
                }else {
                    initDobList(rootView, businessListView);
                    businessListView.setAdapter(hamPayBusinessesAdapter);
                }
            }
        }
    }

    DobList dobList;

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

                    if(!FINISHED_SCROLLING)
                        new HttpHamPay‌Business().execute(searchEnable);
                    else
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
