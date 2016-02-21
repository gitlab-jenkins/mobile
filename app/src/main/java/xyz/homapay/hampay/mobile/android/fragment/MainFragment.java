package xyz.homapay.hampay.mobile.android.fragment;

import android.app.Activity;
import android.content.Intent;
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
import xyz.homapay.hampay.mobile.android.activity.BusinessPurchaseActivity;
import xyz.homapay.hampay.mobile.android.activity.PaymentRequestActivity;
import xyz.homapay.hampay.mobile.android.activity.PendingPurchasePaymentActivity;
import xyz.homapay.hampay.mobile.android.activity.TransactionsHistoryActivity;
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
    LinearLayout user_transaction_history;
    LinearLayout user_payment_request;
    LinearLayout businessPurchase;
    LinearLayout pendingPurchasePayment;

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

        user_transaction_history = (LinearLayout)rootView.findViewById(R.id.user_transaction_history);
        user_transaction_history.setOnClickListener(this);

        user_payment_request = (LinearLayout)rootView.findViewById(R.id.user_payment_request);
        user_payment_request.setOnClickListener(this);

        businessPurchase = (LinearLayout)rootView.findViewById(R.id.businessPurchase);
        businessPurchase.setOnClickListener(this);

        pendingPurchasePayment = (LinearLayout)rootView.findViewById(R.id.pendingPurchasePayment);
        pendingPurchasePayment.setOnClickListener(this);

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

        Intent intent = new Intent();

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

            case R.id.user_transaction_history:
                intent.setClass(getActivity(), TransactionsHistoryActivity.class);
                startActivity(intent);
                break;

            case R.id.user_payment_request:
                intent.setClass(getActivity(), PaymentRequestActivity.class);
                startActivity(intent);
                break;

            case R.id.businessPurchase:
                intent.setClass(getActivity(), BusinessPurchaseActivity.class);
                startActivity(intent);
                break;

            case R.id.pendingPurchasePayment:
                intent.setClass(getActivity(), PendingPurchasePaymentActivity.class);
                startActivity(intent);
                break;
        }

    }
}

