package xyz.homapay.hampay.mobile.android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.activity.UserManualActivity;
import xyz.homapay.hampay.mobile.android.adapter.GuideAdapter;
import xyz.homapay.hampay.mobile.android.util.Constants;

/**
 * Created by amir on 6/5/15.
 */
public class GuideFragment extends Fragment {

    @BindView(R.id.guideListView)
    ListView guideListView;
    GuideAdapter guideAdapter;

    public static GuideFragment newInstance() {
        GuideFragment fragment = new GuideFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_guide, container, false);
        ButterKnife.bind(this, rootView);

        guideAdapter = new GuideAdapter(getActivity());
        guideListView.setAdapter(guideAdapter);
        guideListView.setOnItemClickListener((parent, view, position, id) -> {

            Intent intent = new Intent();
            intent.setClass(getActivity(), UserManualActivity.class);

            switch (position) {

                case 0:
                    intent.putExtra(Constants.USER_MANUAL_TEXT, R.string.user_manual_text_merchant_payment);
                    intent.putExtra(Constants.USER_MANUAL_TITLE, R.string.user_manual_title_merchant_payment);
                    break;

                case 1:
                    intent.putExtra(Constants.USER_MANUAL_TEXT, R.string.user_manual_text_online_payment);
                    intent.putExtra(Constants.USER_MANUAL_TITLE, R.string.user_manual_title_online_payment);
                    break;

                case 2:
                    intent.putExtra(Constants.USER_MANUAL_TEXT, R.string.user_manual_title_bill);
                    intent.putExtra(Constants.USER_MANUAL_TITLE, R.string.user_manual_text_bill);
                    break;

                case 3:
                    intent.putExtra(Constants.USER_MANUAL_TEXT, R.string.user_manual_text_charge);
                    intent.putExtra(Constants.USER_MANUAL_TITLE, R.string.user_manual_title_charge);
                    break;

                case 4:
                    intent.putExtra(Constants.USER_MANUAL_TEXT, R.string.user_manual_text_charity_payment);
                    intent.putExtra(Constants.USER_MANUAL_TITLE, R.string.user_manual_title_charity_payment);
                    break;

                case 5:
                    intent.putExtra(Constants.USER_MANUAL_TEXT, R.string.user_manual_text_payment_request);
                    intent.putExtra(Constants.USER_MANUAL_TITLE, R.string.user_manual_title_payment_request);
                    break;

                case 6:
                    intent.putExtra(Constants.USER_MANUAL_TEXT, R.string.user_manual_text_pending_payment);
                    intent.putExtra(Constants.USER_MANUAL_TITLE, R.string.user_manual_title_pending_payment);
                    break;

                case 7:
                    intent.putExtra(Constants.USER_MANUAL_TEXT, R.string.user_manual_text_iban_intro);
                    intent.putExtra(Constants.USER_MANUAL_TITLE, R.string.user_manual_title_iban_intro);
                    break;

                case 8:
                    intent.putExtra(Constants.USER_MANUAL_TEXT, R.string.user_manual_text_transaction);
                    intent.putExtra(Constants.USER_MANUAL_TITLE, R.string.user_manual_title_transaction);
                    break;

                case 9:
                    intent.putExtra(Constants.USER_MANUAL_TEXT, R.string.user_manual_text_change_password);
                    intent.putExtra(Constants.USER_MANUAL_TITLE, R.string.user_manual_title_change_password);
                    break;

                case 10:
                    intent.putExtra(Constants.USER_MANUAL_TEXT, R.string.user_manual_text_change_memorable_word);
                    intent.putExtra(Constants.USER_MANUAL_TITLE, R.string.user_manual_title_change_memorable_word);
                    break;

                case 11:
                    intent.putExtra(Constants.USER_MANUAL_TEXT, R.string.user_manual_text_iban_change);
                    intent.putExtra(Constants.USER_MANUAL_TITLE, R.string.user_manual_title_iban_change);
                    break;

                case 12:
                    intent.putExtra(Constants.USER_MANUAL_TEXT, R.string.user_manual_text_friends_invitation);
                    intent.putExtra(Constants.USER_MANUAL_TITLE, R.string.user_manual_title_friends_invitation);
                    break;

                case 13:
                    intent.putExtra(Constants.USER_MANUAL_TEXT, R.string.user_manual_text_unlink);
                    intent.putExtra(Constants.USER_MANUAL_TITLE, R.string.user_manual_title_unlink);
                    break;

                case 14:
                    intent.putExtra(Constants.USER_MANUAL_TEXT, R.string.user_manual_text_contact_us);
                    intent.putExtra(Constants.USER_MANUAL_TITLE, R.string.user_manual_title_contact_us);
                    break;

            }

            startActivity(intent);
        });

        return rootView;
    }
}

