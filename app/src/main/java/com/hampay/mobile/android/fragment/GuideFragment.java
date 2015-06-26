package com.hampay.mobile.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.hampay.mobile.android.R;
import com.hampay.mobile.android.activity.ChangePasswordActivity;
import com.hampay.mobile.android.adapter.GuideAdapter;
import com.hampay.mobile.android.adapter.SettingAdapter;

/**
 * Created by amir on 6/5/15.
 */
public class GuideFragment extends Fragment {


    ListView guideListView;
    GuideAdapter guideAdapter;

    public GuideFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_guide, container, false);

        guideListView = (ListView)rootView.findViewById(R.id.guideListView);

        guideAdapter = new GuideAdapter(getActivity());

        guideListView.setAdapter(guideAdapter);

        guideListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent();

                switch (position){
                    case 0:
                        break;

                    case 1:
                        break;

                    case 2:
                        break;

                    case 3:
                        break;

                    case 4:
                        break;

                    case 5:
                        break;

                    case 6:
                        break;
                }
            }
        });

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
}

