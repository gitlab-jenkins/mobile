package xyz.homapay.hampay.mobile.android.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.adapter.business.AdapterBusiness;
import xyz.homapay.hampay.mobile.android.common.messages.MessageOnBackPressed;
import xyz.homapay.hampay.mobile.android.component.CustomTab;

/**
 * Created by amir on 1/22/17.
 */

public class ActivityBusiness extends ActivityParent implements View.OnClickListener {

    @BindView(R.id.tab)
    CustomTab tab;

    @BindView(R.id.pager)
    ViewPager pager;

    private Context ctx = this;
    private AdapterBusiness adapterBusiness;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business);
        try {
            ButterKnife.bind(this);
            adapterBusiness = new AdapterBusiness(ctx, getSupportFragmentManager());
            pager.setAdapter(adapterBusiness);
            tab.init(pager);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBack:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        EventBus.getDefault().post(new MessageOnBackPressed());
    }
}
