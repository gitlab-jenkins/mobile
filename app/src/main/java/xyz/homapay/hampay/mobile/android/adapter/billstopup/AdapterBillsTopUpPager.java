package xyz.homapay.hampay.mobile.android.adapter.billstopup;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.fragment.billtopup.FrgBills;
import xyz.homapay.hampay.mobile.android.fragment.billtopup.FrgTopUp;

/**
 * Created by mohammad on 1/23/2017 AD.
 */

public class AdapterBillsTopUpPager extends FragmentStatePagerAdapter {

    private Context ctx;

    public AdapterBillsTopUpPager(Context ctx, FragmentManager fm) {
        super(fm);
        this.ctx = ctx;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frg = null;
        switch (position) {
            case 1:
                frg = FrgBills.newInstance();
                break;
            case 0:
                frg = FrgTopUp.newInstance();
                break;
        }
        return frg;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        switch (position) {
            case 1:
                title = ctx.getString(R.string.bills_header_title);
                break;
            case 0:
                title = ctx.getString(R.string.top_up_header_title);
                break;
        }
        return title;
    }
}
