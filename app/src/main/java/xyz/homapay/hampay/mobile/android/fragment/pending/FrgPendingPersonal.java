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

public class FrgPendingPersonal extends Fragment {

    private View rootView;

    public static FrgPendingPersonal newInstance() {
        FrgPendingPersonal fragment = new FrgPendingPersonal();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frg_pending_personal, null);
        return rootView;
    }
}
