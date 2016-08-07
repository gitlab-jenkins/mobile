package xyz.homapay.hampay.mobile.android.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import xyz.homapay.hampay.mobile.android.BuildConfig;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;

/**
 * Created by amir on 6/5/15.
 */
public class AboutFragment extends Fragment {

    FacedTextView version;

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

        version = (FacedTextView)rootView.findViewById(R.id.version);

        version.setText(getString(R.string.about_text_7, BuildConfig.VERSION_NAME + " - "+ BuildConfig.VERSION_CODE));

        WebView webView = (WebView)rootView.findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/certification.html");

        return rootView;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }
}

