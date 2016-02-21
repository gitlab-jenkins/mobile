package xyz.homapay.hampay.mobile.android.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.util.List;

import xyz.homapay.hampay.common.core.model.dto.ContactDTO;
import xyz.homapay.hampay.common.core.model.response.dto.UserProfileDTO;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.activity.BusinessPurchaseActivity;
import xyz.homapay.hampay.mobile.android.activity.PayOneActivity;
import xyz.homapay.hampay.mobile.android.activity.PaymentRequestActivity;
import xyz.homapay.hampay.mobile.android.activity.PendingPurchasePaymentActivity;
import xyz.homapay.hampay.mobile.android.activity.TransactionsHistoryActivity;
import xyz.homapay.hampay.mobile.android.adapter.GuideAdapter;
import xyz.homapay.hampay.mobile.android.animation.Collapse;
import xyz.homapay.hampay.mobile.android.animation.Expand;
import xyz.homapay.hampay.mobile.android.async.RequestImageDownloader;
import xyz.homapay.hampay.mobile.android.async.listener.RequestImageDownloaderTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.circleimageview.CircleImageView;
import xyz.homapay.hampay.mobile.android.util.Constants;

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
    UserProfileDTO userProfileDTO;
    Bundle bundle;
    private String user_image_url = "";
    LinearLayout hampay_1_ll;
    LinearLayout hampay_2_ll;
    LinearLayout hampay_3_ll;
    LinearLayout hampay_4_ll;
    CircleImageView hampay_image_1;
    CircleImageView hampay_image_2;
    CircleImageView hampay_image_3;
    CircleImageView hampay_image_4;
    FacedTextView hampay_1;
    FacedTextView hampay_2;
    FacedTextView hampay_3;
    FacedTextView hampay_4;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    private Context context;

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        context = getActivity();

        prefs = getActivity().getSharedPreferences(Constants.APP_PREFERENCE_NAME, context.MODE_PRIVATE);
        editor = getActivity().getSharedPreferences(Constants.APP_PREFERENCE_NAME, context.MODE_PRIVATE).edit();


        bundle = getArguments();

        if (bundle != null){
            this.userProfileDTO = (UserProfileDTO) bundle.getSerializable(Constants.USER_PROFILE_DTO);
        }

        hampay_1_ll = (LinearLayout)rootView.findViewById(R.id.hampay_1_ll);
        hampay_2_ll = (LinearLayout)rootView.findViewById(R.id.hampay_2_ll);
        hampay_3_ll = (LinearLayout)rootView.findViewById(R.id.hampay_3_ll);
        hampay_4_ll = (LinearLayout)rootView.findViewById(R.id.hampay_4_ll);
        hampay_1_ll.setOnClickListener(this);
        hampay_2_ll.setOnClickListener(this);
        hampay_3_ll.setOnClickListener(this);
        hampay_4_ll.setOnClickListener(this);
        hampay_1 = (FacedTextView)rootView.findViewById(R.id.hampay_1);
        hampay_2 = (FacedTextView)rootView.findViewById(R.id.hampay_2);
        hampay_3 = (FacedTextView)rootView.findViewById(R.id.hampay_3);
        hampay_4 = (FacedTextView)rootView.findViewById(R.id.hampay_4);
        hampay_image_1 = (CircleImageView)rootView.findViewById(R.id.hampay_image_1);
        hampay_image_2 = (CircleImageView)rootView.findViewById(R.id.hampay_image_2);
        hampay_image_3 = (CircleImageView)rootView.findViewById(R.id.hampay_image_3);
        hampay_image_4 = (CircleImageView)rootView.findViewById(R.id.hampay_image_4);

        List<ContactDTO> contacts = userProfileDTO.getSelectedContacts();
        for (int contact = 0; contact < contacts.size(); contact++){
            switch (contact){
                case 0:
                    hampay_1_ll.setVisibility(View.VISIBLE);
                    hampay_1.setText(contacts.get(0).getDisplayName());
                    if (contacts.get(0).getContactImageId() != null) {
                        user_image_url = Constants.HTTPS_SERVER_IP + "/users/" + prefs.getString(Constants.LOGIN_TOKEN_ID, "") + "/" + contacts.get(0).getContactImageId();
                        new RequestImageDownloader(context, new RequestImageDownloaderTaskCompleteListener(hampay_image_1)).execute(user_image_url);
                    }
                    break;
                case 1:
                    hampay_2_ll.setVisibility(View.VISIBLE);
                    hampay_2.setText(contacts.get(1).getDisplayName());
                    if (contacts.get(1).getContactImageId() != null) {
                        user_image_url = Constants.HTTPS_SERVER_IP + "/users/" + prefs.getString(Constants.LOGIN_TOKEN_ID, "") + "/" + contacts.get(1).getContactImageId();
                        new RequestImageDownloader(context, new RequestImageDownloaderTaskCompleteListener(hampay_image_2)).execute(user_image_url);
                    }
                    break;
                case 2:
                    hampay_3_ll.setVisibility(View.VISIBLE);
                    hampay_3.setText(contacts.get(2).getDisplayName());
                    if (contacts.get(2).getContactImageId() != null) {
                        user_image_url = Constants.HTTPS_SERVER_IP + "/users/" + prefs.getString(Constants.LOGIN_TOKEN_ID, "") + "/" + contacts.get(2).getContactImageId();
                        new RequestImageDownloader(context, new RequestImageDownloaderTaskCompleteListener(hampay_image_3)).execute(user_image_url);
                    }
                    break;
                case 3:
                    hampay_4_ll.setVisibility(View.VISIBLE);
                    hampay_4.setText(contacts.get(3).getDisplayName());
                    if (contacts.get(3).getContactImageId() != null) {
                        user_image_url = Constants.HTTPS_SERVER_IP + "/users/" + prefs.getString(Constants.LOGIN_TOKEN_ID, "") + "/" + contacts.get(3).getContactImageId();
                        new RequestImageDownloader(context, new RequestImageDownloaderTaskCompleteListener(hampay_image_4)).execute(user_image_url);
                    }
                    break;

            }
        }

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

            case R.id.hampay_1_ll:
                intent = new Intent(getActivity(), PayOneActivity.class);
                intent.putExtra(Constants.CONTACT_PHONE_NO, userProfileDTO.getSelectedContacts().get(0).getCellNumber());
                intent.putExtra(Constants.CONTACT_NAME, userProfileDTO.getSelectedContacts().get(0).getDisplayName());
                startActivity(intent);
                break;
            case R.id.hampay_2_ll:
                intent = new Intent(getActivity(), PayOneActivity.class);
                intent.putExtra(Constants.CONTACT_PHONE_NO, userProfileDTO.getSelectedContacts().get(1).getCellNumber());
                intent.putExtra(Constants.CONTACT_NAME, userProfileDTO.getSelectedContacts().get(1).getDisplayName());
                startActivity(intent);
                break;
            case R.id.hampay_3_ll:
                intent = new Intent(getActivity(), PayOneActivity.class);
                intent.putExtra(Constants.CONTACT_PHONE_NO, userProfileDTO.getSelectedContacts().get(2).getCellNumber());
                intent.putExtra(Constants.CONTACT_NAME, userProfileDTO.getSelectedContacts().get(2).getDisplayName());
                startActivity(intent);
                break;
            case R.id.hampay_4_ll:
                intent = new Intent(getActivity(), PayOneActivity.class);
                intent.putExtra(Constants.CONTACT_PHONE_NO, userProfileDTO.getSelectedContacts().get(3).getCellNumber());
                intent.putExtra(Constants.CONTACT_NAME, userProfileDTO.getSelectedContacts().get(3).getDisplayName());
                startActivity(intent);
                break;
        }

    }
}
