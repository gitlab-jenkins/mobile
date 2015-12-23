package xyz.homapay.hampay.mobile.android.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.util.Constants;

/**
 * Created by amir on 7/23/15.
 */
public class TCFragment extends Fragment {

    HamPayDialog hamPayDialog;
    SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tc,container,false);


        prefs = getActivity().getSharedPreferences(Constants.APP_PREFERENCE_NAME, getActivity().MODE_PRIVATE);

        WebView webView = (WebView)view.findViewById(R.id.webview);

        WebSettings settings = webView.getSettings();
        hamPayDialog = new HamPayDialog(getActivity());
        hamPayDialog.showWaitingdDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));

        settings.setJavaScriptEnabled(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);


        webView.loadUrl(Constants.HTTPS_SERVER_IP + "/help/tc.html");
        webView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                hamPayDialog.dismisWaitingDialog();
            }
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
                handler.proceed();
            }
        });

//        Pattern pattern = Pattern.compile(Constants.WEB_URL_REGEX);
//        Matcher matcher = pattern.matcher(accept_term);
//        while (matcher.find()) {
//            final String urlStr = matcher.group();
//
//            Spannable WordtoSpan = new SpannableString(accept_term);
//
//            ClickableSpan privacySpan = new ClickableSpan() {
//                @Override
//                public void onClick(View textView) {
//                    Intent i = new Intent(Intent.ACTION_VIEW);
//                    if(urlStr.toLowerCase().contains("http://")) {
//                        i.setData(Uri.parse(urlStr));
//                    }else {
//                        i.setData(Uri.parse("http://" + urlStr));
//                    }
//                    startActivity(i);
//                }
//            };
//
//            WordtoSpan.setSpan(privacySpan, accept_term.indexOf(urlStr), accept_term.indexOf(urlStr) + urlStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//            tac_term.setText(WordtoSpan);
//
//            tac_term.setMovementMethod(LinkMovementMethod.getInstance());
//
//        }


        return view;
    }
}
