package xyz.homapay.hampay.mobile.android.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.activity.ChangeEmailPassActivity;
import xyz.homapay.hampay.mobile.android.activity.ChangeMemorableActivity;
import xyz.homapay.hampay.mobile.android.activity.ChangePassCodeActivity;
import xyz.homapay.hampay.mobile.android.activity.IntroIBANActivity;
import xyz.homapay.hampay.mobile.android.adapter.SettingAdapter;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.model.setting.HamPaySetting;
import xyz.homapay.hampay.mobile.android.model.setting.SettingStatus;
import xyz.homapay.hampay.mobile.android.util.Constants;

/**
 * Created by amir on 6/5/15.
 */
public class SettingFragment extends Fragment {

    ListView settingListView;
    SettingAdapter settingAdapter;
    List<HamPaySetting> hamPaySettings = new ArrayList<>();
    String[] settingValueList;
    String[] settingKeyList;
    private SharedPreferences prefs;

    public SettingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getActivity().getSharedPreferences(Constants.APP_PREFERENCE_NAME, getActivity().MODE_PRIVATE);

        settingKeyList = getResources().getStringArray(R.array.setting_key_list);
        settingValueList = getResources().getStringArray(R.array.setting_value_list);
        for (int i = 0; i < settingKeyList.length; i++){
            HamPaySetting hamPaySetting = new HamPaySetting();
            hamPaySetting.setTitlel(settingValueList[i]);
            if (prefs.getBoolean(settingKeyList[i], true)) {
                hamPaySetting.setSettingStatus(SettingStatus.Active);
            }else {
                hamPaySetting.setSettingStatus(SettingStatus.Inactive);
            }
            hamPaySettings.add(hamPaySetting);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);

        settingListView = (ListView)rootView.findViewById(R.id.settingListView);

        settingAdapter = new SettingAdapter(getActivity(), hamPaySettings);

        settingListView.setAdapter(settingAdapter);

        settingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (hamPaySettings.get(position).getSettingStatus() == SettingStatus.Inactive){
                    new HamPayDialog(getActivity()).showUnknownIban();
                    return;
                }

                Intent intent;

                switch (position){
                    case 0:
                        intent = new Intent();
                        intent.setClass(getActivity(), ChangePassCodeActivity.class);
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

                    case 3:
                        new HamPayDialog(getActivity()).showChangeEmail();
                        break;

                    case 4:
                        intent = new Intent();
                        intent.setClass(getActivity(), IntroIBANActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });

        return rootView;
    }
    
    @Override
    public void onDetach() {
        super.onDetach();
    }
}

