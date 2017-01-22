package xyz.homapay.hampay.mobile.android.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.adapter.business.AdapterBusiness;
import xyz.homapay.hampay.mobile.android.adapter.pending.AdapterPendingList;
import xyz.homapay.hampay.mobile.android.common.messages.MessageOnBackPressedOnPendingAct;
import xyz.homapay.hampay.mobile.android.component.CustomTab;

/**
 * Created by amir on 1/22/17.
 */

public class ActivityBusiness extends AppCompatActivity implements View.OnClickListener {

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
            tab.setTabGravity(TabLayout.GRAVITY_FILL);
            tab.init(pager);
            tab.setSelectedTab(1);
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
        EventBus.getDefault().post(new MessageOnBackPressedOnPendingAct());
    }
}
