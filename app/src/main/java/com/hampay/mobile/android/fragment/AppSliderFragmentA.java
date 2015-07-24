package com.hampay.mobile.android.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hampay.mobile.android.R;

/**
 * Created by amir on 7/23/15.
 */
public class AppSliderFragmentA extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_app_slider_a,container,false);

        return v;
    }
}
