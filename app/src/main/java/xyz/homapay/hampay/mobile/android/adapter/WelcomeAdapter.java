package xyz.homapay.hampay.mobile.android.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import xyz.homapay.hampay.mobile.android.R;

/**
 * Created by amir on 10/18/16.
 */
public class WelcomeAdapter extends PagerAdapter {

    private LayoutInflater layoutInflater;
    private int[] layouts;
    private Context context;

    public WelcomeAdapter(Context context) {
        this.context = context;
        layouts = new int[]{
                R.layout.welcome_slider0,
                R.layout.welcome_slider1,
                R.layout.welcome_slider2,
                R.layout.welcome_slider3,
                R.layout.welcome_slider4,
                R.layout.welcome_slider5
        };
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(layouts[position], container, false);


        if (position == 0){
            WebView webView = (WebView)view.findViewById(R.id.webview);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadUrl("file:///android_asset/certification.html");
        }

        container.addView(view);

        return view;
    }

    @Override
    public int getCount() {
        return layouts.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }
}
