package xyz.homapay.hampay.mobile.android.fragment.billtopup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.activity.ServiceBillsActivity;

/**
 * Created by mohammad on 1/23/2017 AD.
 */

public class FrgBills extends Fragment implements View.OnClickListener {

    @BindView(R.id.serviceBills)
    LinearLayout serviceBills;
    private View rootView;

    public static FrgBills newInstance() {
        FrgBills fragment = new FrgBills();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frg_bills, null);
        ButterKnife.bind(this, rootView);
        serviceBills.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.serviceBills) {
            startActivity(new Intent(getActivity(), ServiceBillsActivity.class));
        }
    }
}
