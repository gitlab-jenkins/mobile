package com.hampay.mobile.android.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hampay.mobile.android.R;
import com.hampay.mobile.android.component.FacedTextView;

/**
 * Created by amir on 6/3/15.
 */
public class IntroAdapter extends PagerAdapter{


    int[] m‌Background = {
            R.drawable.wireframe_1,
            R.drawable.wireframe_2,
            R.drawable.wireframe_3,
            R.drawable.wireframe_4,
            R.drawable.wireframe_5,
    };

    int[] m‌Icons = {
            R.drawable.logo_hampay,
            R.drawable.logo_hampay,
            R.drawable.logo_hampay,
            R.drawable.logo_hampay,
            R.drawable.logo_hampay
    };

    int[] m‌Intor = {
            R.string.intro_1,
            R.string.intro_2,
            R.string.intro_3,
            R.string.intro_4,
            R.string.intro_5
    };


    Context mContext;
    LayoutInflater mLayoutInflater;

    public IntroAdapter(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return m‌Background.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.fragment_intro_item, container, false);

        ImageView intro_background = (ImageView) itemView.findViewById(R.id.intro_background);
        ImageView intro_icon = (ImageView) itemView.findViewById(R.id.intro_icon);
        FacedTextView intro_text = (FacedTextView)itemView.findViewById(R.id.intro_text);

        intro_background.setImageResource(m‌Background[position]);
        intro_icon.setImageResource(m‌Icons[position]);
        intro_text.setText(m‌Intor[position]);

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }

}
