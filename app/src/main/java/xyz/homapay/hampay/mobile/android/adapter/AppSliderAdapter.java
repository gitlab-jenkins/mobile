package xyz.homapay.hampay.mobile.android.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;

import xyz.homapay.hampay.mobile.android.fragment.AppSliderFragmentA;
import xyz.homapay.hampay.mobile.android.fragment.AppSliderFragmentB;
import xyz.homapay.hampay.mobile.android.fragment.AppSliderFragmentC;
import xyz.homapay.hampay.mobile.android.fragment.AppSliderFragmentD;
import xyz.homapay.hampay.mobile.android.fragment.AppSliderFragmentE;

/**
 * Created by amir on 8/10/15.
 */
public class AppSliderAdapter extends FragmentStatePagerAdapter {

    public AppSliderAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment=null;
        if(i == 0)
        {
            fragment=new AppSliderFragmentA();
        }
        if(i==1)
        {
            fragment=new AppSliderFragmentB();
        }
        if(i==2)
        {
            fragment=new AppSliderFragmentC();
        }
        if(i==3)
        {
            fragment=new AppSliderFragmentD();
        }
        if(i==4)
        {
            fragment=new AppSliderFragmentE();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        if(object instanceof AppSliderFragmentE){
            view.setTag(4);
        }
        if(object instanceof AppSliderFragmentD){
            view.setTag(3);
        }
        if(object instanceof AppSliderFragmentC){
            view.setTag(2);
        }
        if(object instanceof AppSliderFragmentB){
            view.setTag(1);
        }
        if(object instanceof AppSliderFragmentA){
            view.setTag(0);
        }
        return super.isViewFromObject(view, object);
    }

}
