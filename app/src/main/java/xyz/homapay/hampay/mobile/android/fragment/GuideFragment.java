package xyz.homapay.hampay.mobile.android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.activity.UserManualActivity;
import xyz.homapay.hampay.mobile.android.adapter.GuideAdapter;
import xyz.homapay.hampay.mobile.android.util.Constants;

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
                intent.setClass(getActivity(), UserManualActivity.class);

                switch (position){

                    case 0:
                        intent.putExtra(Constants.USER_MANUAL_TEXT, R.string.user_manual_merchant_payment);
                        break;

                    case 1:
                        intent.putExtra(Constants.USER_MANUAL_TEXT, R.string.user_manual_online_payment);
                        break;

                    case 2:
                        intent.putExtra(Constants.USER_MANUAL_TEXT, R.string.user_manual_charity_payment);
                        break;

                    case 3:
                        intent.putExtra(Constants.USER_MANUAL_TEXT, R.string.user_manual_payment_request);
                        break;

                    case 4:
                        intent.putExtra(Constants.USER_MANUAL_TEXT, R.string.user_manual_pending_payment);
                        break;

                    case 5:
                        intent.putExtra(Constants.USER_MANUAL_TEXT, R.string.user_manual_iban_intro);
                        break;

                    case 6:
                        intent.putExtra(Constants.USER_MANUAL_TEXT, R.string.user_manual_transaction);
                        break;

                    case 7:
                        intent.putExtra(Constants.USER_MANUAL_TEXT, R.string.user_manual_change_password);
                        break;

                    case 8:
                        intent.putExtra(Constants.USER_MANUAL_TEXT, R.string.user_manual_change_memorable_word);
                        break;

                    case 9:
                        intent.putExtra(Constants.USER_MANUAL_TEXT, R.string.user_manual_iban_change);
                        break;

                    case 10:
                        intent.putExtra(Constants.USER_MANUAL_TEXT, R.string.user_manual_unlink);
                        break;

                    case 11:
                        intent.putExtra(Constants.USER_MANUAL_TEXT, R.string.user_manual_contact_us);
                        break;

                }

                startActivity(intent);
            }
        });

        return rootView;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }
}

