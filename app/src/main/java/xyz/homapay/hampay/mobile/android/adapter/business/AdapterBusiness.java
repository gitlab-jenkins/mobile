package xyz.homapay.hampay.mobile.android.adapter.business;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.fragment.business.BusinessCodeFragment;
import xyz.homapay.hampay.mobile.android.fragment.business.BusinessNameFragment;
import xyz.homapay.hampay.mobile.android.fragment.pending.FrgPendingRecieved;
import xyz.homapay.hampay.mobile.android.fragment.pending.FrgPendingRequests;

/**
 * Created by amir on 1/22/17.
 */

public class AdapterBusiness extends FragmentStatePagerAdapter {

    private Context ctx;

    public AdapterBusiness(Context ctx, FragmentManager fm) {
        super(fm);
        this.ctx = ctx;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frg = null;
        switch (position) {
            case 0:
                frg = BusinessNameFragment.newInstance();
                break;
            case 1:
                frg = BusinessCodeFragment.newInstance();
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
        CharSequence title = "";
        switch (position) {
            case 0:
                title = ctx.getString(R.string.business_payment_per_name);
                break;
            case 1:
                title = ctx.getString(R.string.business_payment_per_code);
                break;
        }
        return title;
    }
}
