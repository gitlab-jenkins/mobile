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
import xyz.homapay.hampay.mobile.android.activity.GuideDetailActivity;
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
                intent.setClass(getActivity(), GuideDetailActivity.class);

                switch (position){

                    case 0:
                        intent.putExtra(Constants.WEB_PAGE_ADDRESS, Constants.HTTPS_SERVER_IP + "/help/reg-intro.html");
                        break;

                    case 1:
                        intent.putExtra(Constants.WEB_PAGE_ADDRESS, Constants.HTTPS_SERVER_IP + "/help/reg-userInfo.html");
                        break;

                    case 2:
                        intent.putExtra(Constants.WEB_PAGE_ADDRESS, Constants.HTTPS_SERVER_IP + "/help/reg-smsToken.html");
                        break;

                    case 3:
                        intent.putExtra(Constants.WEB_PAGE_ADDRESS, Constants.HTTPS_SERVER_IP + "/help/userInfoCheck.html");
                        break;

                    case 4:
                        intent.putExtra(Constants.WEB_PAGE_ADDRESS, Constants.HTTPS_SERVER_IP + "/help/accountVerification.html");
                        break;

                    case 5:
                        intent.putExtra(Constants.WEB_PAGE_ADDRESS, Constants.HTTPS_SERVER_IP + "/help/pass-a.html");
                        break;

                    case 6:
                        intent.putExtra(Constants.WEB_PAGE_ADDRESS, Constants.HTTPS_SERVER_IP + "/help/memorableKey.html");
                        break;

                    case 7:
                        intent.putExtra(Constants.WEB_PAGE_ADDRESS, Constants.HTTPS_SERVER_IP + "/help/accountDetail.html");
                        break;

                    case 8:
                        intent.putExtra(Constants.WEB_PAGE_ADDRESS, Constants.HTTPS_SERVER_IP + "/help/limitedAccount.html");
                        break;

                    case 9:
                        intent.putExtra(Constants.WEB_PAGE_ADDRESS, Constants.HTTPS_SERVER_IP + "/help/login.html");
                        break;

                    case 10:
                        intent.putExtra(Constants.WEB_PAGE_ADDRESS, Constants.HTTPS_SERVER_IP + "/help/transaction.html");
                        break;

                    case 11:
                        intent.putExtra(Constants.WEB_PAGE_ADDRESS, Constants.HTTPS_SERVER_IP + "/help/user-unlink.html");
                        break;

                    case 12:
                        intent.putExtra(Constants.WEB_PAGE_ADDRESS, Constants.HTTPS_SERVER_IP + "/help/payment.html");
                        break;

                    case 13:
                        intent.putExtra(Constants.WEB_PAGE_ADDRESS, Constants.HTTPS_SERVER_IP + "/help/businessPayment.html");
                        break;

                    case 14:
                        intent.putExtra(Constants.WEB_PAGE_ADDRESS, Constants.HTTPS_SERVER_IP + "/help/contactUs.html");
                        break;

                    case 15:
                        intent.putExtra(Constants.WEB_PAGE_ADDRESS, Constants.HTTPS_SERVER_IP + "/help/logout.html");
                        break;

                    case 16:
                        intent.putExtra(Constants.WEB_PAGE_ADDRESS, Constants.HTTPS_SERVER_IP + "/help/setting.html");
                        break;

                    case 17:
                        intent.putExtra(Constants.WEB_PAGE_ADDRESS, Constants.HTTPS_SERVER_IP + "/help/changeMemorableWord.html");
                        break;

                    case 18:
                        intent.putExtra(Constants.WEB_PAGE_ADDRESS, Constants.HTTPS_SERVER_IP + "/help/changePassword.html");
                        break;

                }

                startActivity(intent);
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

