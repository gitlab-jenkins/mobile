package xyz.homapay.hampay.mobile.android.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.adapter.pending.AdapterPendingList;
import xyz.homapay.hampay.mobile.android.component.CustomTab;

/**
 * Created by mohammad on 1/22/17.
 */

public class ActivityPendingRequestList extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.tab)
    CustomTab tab;

    @BindView(R.id.pager)
    ViewPager pager;

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
            tab.setSelectedTab(2);
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
}
