package xyz.homapay.hampay.mobile.android.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.adapter.pending.AdapterPendingList;
import xyz.homapay.hampay.mobile.android.common.messages.MessageOnBackPressedOnPendingAct;
import xyz.homapay.hampay.mobile.android.common.messages.MessageSheetStateChanged;
import xyz.homapay.hampay.mobile.android.component.CustomTab;

/**
 * Created by mohammad on 1/22/17.
 */

public class ActivityPendingRequestList extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.tab)
    CustomTab tab;

    @BindView(R.id.pager)
    ViewPager pager;

    private boolean isSheetOpen;

    private Context ctx = this;
    private AdapterPendingList adapterPendingList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_request_list);
        try {
            ButterKnife.bind(this);
            adapterPendingList = new AdapterPendingList(ctx, getSupportFragmentManager());
            pager.setAdapter(adapterPendingList);
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
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onSheetStateChanged(MessageSheetStateChanged changed) {
        isSheetOpen = changed.isOpen();
    }

    @Override
    public void onBackPressed() {
        if (isSheetOpen)
            EventBus.getDefault().post(new MessageOnBackPressedOnPendingAct());
        else
            super.onBackPressed();
    }
}
