package xyz.homapay.hampay.mobile.android.fragment;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.adapter.GuideAdapter;
import xyz.homapay.hampay.mobile.android.animation.Collapse;
import xyz.homapay.hampay.mobile.android.animation.Expand;

/**
 * Created by amir on 6/5/15.
 */
public class MainFragment extends Fragment implements View.OnClickListener{


    private ImageView main_banner;
    private LinearLayout show_hampay_friend;
    private LinearLayout hampay_friend;
    private ImageView indicator_icon;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        main_banner = (ImageView)rootView.findViewById(R.id.main_banner);
        main_banner.getLayoutParams().height = size.x / 3;

        show_hampay_friend = (LinearLayout)rootView.findViewById(R.id.show_hampay_friend);
        hampay_friend = (LinearLayout)rootView.findViewById(R.id.hampay_friend);
        show_hampay_friend.setOnClickListener(this);

        indicator_icon = (ImageView)rootView.findViewById(R.id.indicator_icon);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.show_hampay_friend:
                if (hampay_friend.getVisibility() == View.GONE){
                    new Expand(hampay_friend).animate();
                    indicator_icon.setImageResource(R.drawable.ic_friend_collaps);
                }else {
                    new Collapse(hampay_friend).animate();
                    indicator_icon.setImageResource(R.drawable.ic_friend_expand);
                }
                break;
        }

    }
}

