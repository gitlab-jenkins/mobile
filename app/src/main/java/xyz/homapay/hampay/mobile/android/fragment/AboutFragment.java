package xyz.homapay.hampay.mobile.android.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xyz.homapay.hampay.mobile.android.R;

/**
 * Created by amir on 6/5/15.
 */
public class AboutFragment extends Fragment {

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);


        return rootView;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }
}

