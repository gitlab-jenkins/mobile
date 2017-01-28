package xyz.homapay.hampay.mobile.android.component;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import xyz.homapay.hampay.mobile.android.R;

/**
 * Created by mohammad on 1/22/17.
 */

public class CustomTab extends TabLayout {
    public CustomTab(Context context) {
        super(context);
    }

    public CustomTab(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTab(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setSelectedTab(int position) {
        getTabAt(position).select();
    }

    public void init(ViewPager pager) {
        setTabGravity(GRAVITY_FILL);
        pager.setOffscreenPageLimit(pager.getAdapter().getCount());
        setupWithViewPager(pager);
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.app_origin));
        for (int i = 0; i < getTabCount(); i++) {
            getTabAt(i).setCustomView(R.layout.tab_item_header);
            CustomTextView tvHeader = ((CustomTextView) getTabAt(i).getCustomView().findViewById(R.id.header));
            tvHeader.setText(pager.getAdapter().getPageTitle(i));
            tvHeader.setTextColor(ContextCompat.getColor(getContext(), R.color.disable_tab_header));
        }
        addOnTabSelectedListener(new OnTabSelectedListener() {
            @Override
            public void onTabSelected(Tab tab) {
                CustomTextView tvHeader = (CustomTextView) tab.getCustomView().findViewById(R.id.header);
                tvHeader.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            }

            @Override
            public void onTabUnselected(Tab tab) {
                CustomTextView tvHeader = (CustomTextView) tab.getCustomView().findViewById(R.id.header);
                tvHeader.setTextColor(ContextCompat.getColor(getContext(), R.color.disable_tab_header));
            }

            @Override
            public void onTabReselected(Tab tab) {

            }
        });
        setSelectedTabIndicatorColor(Color.WHITE);
        setSelectedTab(pager.getAdapter().getCount() - 1);
    }
}
