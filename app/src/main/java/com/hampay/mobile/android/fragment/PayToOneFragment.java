package com.hampay.mobile.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.hampay.mobile.android.Helper.DatabaseHelper;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.adapter.RecentPayOneAdapter;
import com.hampay.mobile.android.model.RecentPay;

import java.util.List;

/**
 * Created by amir on 6/5/15.
 */
public class PayToOneFragment extends Fragment {

    DatabaseHelper dbHelper;
    List<RecentPay> recentPays;
    ListView recent_payListView;
    RecentPayOneAdapter recentPayOneAdapter;

    public PayToOneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new DatabaseHelper(getActivity());
        RecentPay recentPay = new RecentPay();
        recentPay.setName("امیر");
        recentPay.setPhone("۰۹۱۲۶۱۵۷۹۰۵");
        recentPay.setMessage("مرامی پول ریختم");
        recentPay.setStatus("واریز");

        dbHelper.createRecentPAy(recentPay);

        recentPays = dbHelper.getAllRecentPays();

//        for (RecentPay pay : recentPays){
//            Log.e("PAY", pay.getId() + ": " + pay.getName());
//        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pay_to_one, container, false);

        recent_payListView = (ListView)rootView.findViewById(R.id.recent_payListView);

        recentPayOneAdapter = new RecentPayOneAdapter(getActivity(), recentPays);

        recent_payListView.setAdapter(recentPayOneAdapter);

        // Inflate the layout for this fragment
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
}
