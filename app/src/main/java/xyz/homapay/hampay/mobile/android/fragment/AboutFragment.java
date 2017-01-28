package xyz.homapay.hampay.mobile.android.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.homapay.hampay.mobile.android.BuildConfig;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;

/**
 * Created by amir on 6/5/15.
 */
public class AboutFragment extends Fragment {

    @BindView(R.id.version)
    FacedTextView version;

    @BindView(R.id.webview)
    WebView webview;

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        ButterKnife.bind(this, rootView);

        version.setText(getString(R.string.about_text_7, BuildConfig.VERSION_NAME + " - " + BuildConfig.VERSION_CODE));
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl("file:///android_asset/certification.html");

        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}

