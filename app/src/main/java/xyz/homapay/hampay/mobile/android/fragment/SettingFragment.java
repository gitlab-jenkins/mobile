package xyz.homapay.hampay.mobile.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.activity.ChangeMemorableActivity;
import xyz.homapay.hampay.mobile.android.activity.ChangePasswordActivity;
import xyz.homapay.hampay.mobile.android.adapter.SettingAdapter;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;

/**
 * Created by amir on 6/5/15.
 */
public class SettingFragment extends Fragment {


    ListView settingListView;
    SettingAdapter settingAdapter;

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);

        settingListView = (ListView)rootView.findViewById(R.id.settingListView);

        settingAdapter = new SettingAdapter(getActivity());

        settingListView.setAdapter(settingAdapter);

        settingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent;

                switch (position){
                    case 0:
                        intent = new Intent();
                        intent.setClass(getActivity(), ChangePasswordActivity.class);
                        startActivity(intent);
                        break;

                    case 1:
                        intent = new Intent();
                        intent.setClass(getActivity(), ChangeMemorableActivity.class);
                        startActivity(intent);
                        break;

                    case 2:
                        new HamPayDialog(getActivity()).showUnlinkDialog();
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

