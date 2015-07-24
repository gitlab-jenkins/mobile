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
import com.hampay.mobile.android.model.AppSlideInfo;

import java.util.List;

/**
 * Created by amir on 7/23/15.
 */
public class AppSliderAdapter extends PagerAdapter {

    private Context context;
    private List<AppSlideInfo> appSlideInfos;

    public AppSliderAdapter(Context context, List<AppSlideInfo> appSlideInfos){

        this.context = context;
        this.appSlideInfos = appSlideInfos;

    }


    @Override
    public int getCount() {
        return appSlideInfos.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        LayoutInflater inflater = (LayoutInflater) container.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.fragment_app_slider_a, null);

        ImageView intro_icon = (ImageView)view.findViewById(R.id.intro_icon);
        FacedTextView intro_text = (FacedTextView) view.findViewById(R.id.intro_text);
//        ImageView wireframe = (ImageView)view.findViewById(R.id.wireframe);

        intro_icon.setImageResource(appSlideInfos.get(position).getImageRes());
        intro_text.setText(appSlideInfos.get(position).getImageDescription());
//        wireframe.setImageResource(appSlideInfos.get(position).getWallImageRes());

        RelativeLayout layout = new RelativeLayout(context);
        //layout.setOrientation(LinearLayout.VERTICAL);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);

        layout.setLayoutParams(layoutParams);

        layout.addView(view);

        container.addView(layout);
        return layout;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout)object);
    }



}
