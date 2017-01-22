package xyz.homapay.hampay.mobile.android.adapter.pending;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.fragment.pending.FrgPendingAll;
import xyz.homapay.hampay.mobile.android.fragment.pending.FrgPendingCommercial;
import xyz.homapay.hampay.mobile.android.fragment.pending.FrgPendingPersonal;

/**
 * Created by mohammad on 1/22/17.
 */

public class AdapterPendingList extends FragmentStatePagerAdapter {

    private Context ctx;

    public AdapterPendingList(Context ctx, FragmentManager fm) {
        super(fm);
        this.ctx = ctx;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frg = null;
        switch (position) {
            case 2:
                frg = FrgPendingAll.newInstance();
                break;
            case 1:
                frg = FrgPendingCommercial.newInstance();
                break;
            case 0:
                frg = FrgPendingPersonal.newInstance();
                break;
        }
        return frg;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        CharSequence title = "";
        switch (position) {
            case 2:
                title = ctx.getString(R.string.pending_title_all);
                break;
            case 1:
                title = ctx.getString(R.string.pending_title_commercial);
                break;
            case 0:
                title = ctx.getString(R.string.pending_title_personal);
                break;
        }
        return title;
    }
}
