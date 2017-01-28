package xyz.homapay.hampay.mobile.android.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.adapter.billstopup.AdapterBillsTopUpPager;
import xyz.homapay.hampay.mobile.android.common.messages.MessageKeyboardStateChanged;
import xyz.homapay.hampay.mobile.android.common.messages.MessageOnBackPressed;
import xyz.homapay.hampay.mobile.android.component.CustomTab;
import xyz.homapay.hampay.mobile.android.permission.PermissionListener;

/**
 * Created by mohammad on 1/23/2017 AD.
 */

public class ActivityBillsTopUp extends ActivityParent implements View.OnClickListener {

    public static ArrayList<PermissionListener> permissionListeners = new ArrayList<>();
    @BindView(R.id.tab)
    CustomTab tab;
    @BindView(R.id.pager)
    ViewPager pager;
    private AdapterBillsTopUpPager adapter;
    private boolean isKeyBoardOpen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bills_top_up);
        ButterKnife.bind(this);
        adapter = new AdapterBillsTopUpPager(ctx, getSupportFragmentManager());
        pager.setAdapter(adapter);
        tab.init(pager);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.imgBack)
            onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        for (PermissionListener item : permissionListeners) {
            if (item.onResult(requestCode, permissions, grantResults)) {
                permissionListeners.remove(item);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (isKeyBoardOpen)
            EventBus.getDefault().post(new MessageOnBackPressed());
        else
            super.onBackPressed();
    }

    @Subscribe
    public void onKeyBoardStateChanged(MessageKeyboardStateChanged messageKeyboardStateChanged) {
        isKeyBoardOpen = messageKeyboardStateChanged.isOpen();
    }
}
