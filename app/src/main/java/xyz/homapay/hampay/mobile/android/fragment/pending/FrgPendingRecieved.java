package xyz.homapay.hampay.mobile.android.fragment.pending;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xyz.homapay.hampay.mobile.android.R;

/**
 * Created by mohammad on 1/22/17.
 */

public class FrgPendingRecieved extends Fragment {

    private View rootView;

    public static FrgPendingRecieved newInstance() {
        FrgPendingRecieved fragment = new FrgPendingRecieved();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frg_pending_commercial, null);
        return rootView;
    }
}
