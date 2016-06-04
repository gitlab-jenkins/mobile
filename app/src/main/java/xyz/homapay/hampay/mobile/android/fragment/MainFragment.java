package xyz.homapay.hampay.mobile.android.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.Date;
import java.util.List;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.dto.ContactDTO;
import xyz.homapay.hampay.common.core.model.request.PendingCountRequest;
import xyz.homapay.hampay.common.core.model.response.PendingCountResponse;
import xyz.homapay.hampay.common.core.model.response.dto.UserProfileDTO;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.activity.BusinessPurchaseActivity;
import xyz.homapay.hampay.mobile.android.activity.HamPayLoginActivity;
import xyz.homapay.hampay.mobile.android.activity.PaymentRequestDetailActivity;
import xyz.homapay.hampay.mobile.android.activity.PaymentRequestListActivity;
import xyz.homapay.hampay.mobile.android.activity.PendingPurchasePaymentListActivity;
import xyz.homapay.hampay.mobile.android.activity.TransactionsListActivity;
import xyz.homapay.hampay.mobile.android.animation.Collapse;
import xyz.homapay.hampay.mobile.android.animation.Expand;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestPendingCount;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.slidinguppanel.SlidingUpPanelLayout;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.ImageManager;
import xyz.homapay.hampay.mobile.android.util.JalaliConvert;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

/**
 * Created by amir on 6/5/15.
 */
public class MainFragment extends Fragment implements View.OnClickListener{


    private SlidingUpPanelLayout slidingUpPanelLayout;
    private ImageView main_banner;
    private LinearLayout hampay_friend;
    LinearLayout user_transaction_history;
    LinearLayout user_payment_request;
    LinearLayout businessPurchase;
    LinearLayout pendingPurchasePayment;
    UserProfileDTO userProfileDTO;
    Bundle bundle;
    private String userImageUrl = "";
    LinearLayout hampay_1_ll;
    LinearLayout hampay_2_ll;
    LinearLayout hampay_3_ll;
    LinearLayout hampay_4_ll;
    ImageView hampay_image_1;
    ImageView hampay_image_2;
    ImageView hampay_image_3;
    ImageView hampay_image_4;
    FacedTextView hampay_1;
    FacedTextView hampay_2;
    FacedTextView hampay_3;
    FacedTextView hampay_4;
    private LinearLayout bottom_panel;

    FacedTextView user_last_login;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    private Context context;

    private FacedTextView message_text;
    private FacedTextView date_text;
    private FacedTextView pending_badge;

    private PersianEnglishDigit persianEnglishDigit;
    private ImageManager imageManager;
    private String authToken;

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
        persianEnglishDigit = new PersianEnglishDigit();

        imageManager = new ImageManager(getActivity(), 200000, false);

        Date currentDate = new Date();

        slidingUpPanelLayout = (SlidingUpPanelLayout)rootView.findViewById(R.id.sliding_layout);


        message_text = (FacedTextView)rootView.findViewById(R.id.message_text);
        date_text = (FacedTextView)rootView.findViewById(R.id.date_text);

        JalaliConvert jalaliConvert = new JalaliConvert(currentDate);
        message_text.setText(jalaliConvert.homeMessage());
        date_text.setText(persianEnglishDigit.E2P(jalaliConvert.homeDate()));
        bottom_panel = (LinearLayout)rootView.findViewById(R.id.bottom_panel);

        prefs = getActivity().getSharedPreferences(Constants.APP_PREFERENCE_NAME, context.MODE_PRIVATE);
        editor = getActivity().getSharedPreferences(Constants.APP_PREFERENCE_NAME, context.MODE_PRIVATE).edit();
        authToken = prefs.getString(Constants.LOGIN_TOKEN_ID, "");


        bundle = getArguments();

        if (bundle != null){
            userProfileDTO = (UserProfileDTO) bundle.getSerializable(Constants.USER_PROFILE_DTO);
        }


        pending_badge = (FacedTextView)rootView.findViewById(R.id.pending_badge);
        int totalPendingCount = bundle.getInt(Constants.PENDING_PURCHASE_COUNT) + bundle.getInt(Constants.PENDING_PAYMENT_COUNT);
        if (totalPendingCount == 0){
            pending_badge.setVisibility(View.GONE);
        }else {
            pending_badge.setText(persianEnglishDigit.E2P(String.valueOf(totalPendingCount)));
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
        hampay_image_1 = (ImageView)rootView.findViewById(R.id.hampay_image_1);
        hampay_image_2 = (ImageView)rootView.findViewById(R.id.hampay_image_2);
        hampay_image_3 = (ImageView)rootView.findViewById(R.id.hampay_image_3);
        hampay_image_4 = (ImageView)rootView.findViewById(R.id.hampay_image_4);
        user_last_login = (FacedTextView)rootView.findViewById(R.id.user_last_login);
        jalaliConvert = new JalaliConvert(userProfileDTO.getLastLoginDate());
        if (userProfileDTO.getLastLoginDate() != null) {
            user_last_login.setText(getString(R.string.last_login) + " "
                    + persianEnglishDigit.E2P(jalaliConvert.homeDate() + " " + getString(R.string.time) + " " + jalaliConvert.getTimeDay()));
        }else {
            user_last_login.setText("");
        }

        List<ContactDTO> contacts = userProfileDTO.getSelectedContacts();
        if (contacts.size() == 0){
            bottom_panel.setVisibility(View.GONE);
        }
        for (int contact = 0; contact < contacts.size(); contact++){
            switch (contact) {
                case 0:
                    hampay_1_ll.setVisibility(View.VISIBLE);
                    hampay_1.setText(contacts.get(0).getDisplayName());
                    if (contacts.get(0).getContactImageId() != null) {
                        userImageUrl = Constants.HTTPS_SERVER_IP + Constants.IMAGE_PREFIX + authToken + "/" + contacts.get(0).getContactImageId();
                        hampay_image_1.setTag(userImageUrl.split("/")[6]);
                        imageManager.displayImage(userImageUrl, hampay_image_1, R.drawable.user_placeholder);
                    } else {
                        hampay_image_1.setImageResource(R.drawable.user_placeholder);
                    }
                    break;
                case 1:
                    hampay_2_ll.setVisibility(View.VISIBLE);
                    hampay_2.setText(contacts.get(1).getDisplayName());
                    if (contacts.get(1).getContactImageId() != null) {
                        userImageUrl = Constants.HTTPS_SERVER_IP + Constants.IMAGE_PREFIX + authToken + "/" + contacts.get(1).getContactImageId();
                        hampay_image_2.setTag(userImageUrl.split("/")[6]);
                        imageManager.displayImage(userImageUrl, hampay_image_2, R.drawable.user_placeholder);
                    } else {
                        hampay_image_2.setImageResource(R.drawable.user_placeholder);
                    }
                    break;
                case 2:
                    hampay_3_ll.setVisibility(View.VISIBLE);
                    hampay_3.setText(contacts.get(2).getDisplayName());
                    if (contacts.get(2).getContactImageId() != null) {
                        userImageUrl = Constants.HTTPS_SERVER_IP + Constants.IMAGE_PREFIX + authToken + "/" + contacts.get(2).getContactImageId();
                        hampay_image_3.setTag(userImageUrl.split("/")[6]);
                        imageManager.displayImage(userImageUrl, hampay_image_3, R.drawable.user_placeholder);
                    } else {
                        hampay_image_3.setImageResource(R.drawable.user_placeholder);
                    }
                    break;
                case 3:
                    hampay_4_ll.setVisibility(View.VISIBLE);
                    hampay_4.setText(contacts.get(3).getDisplayName());
                    if (contacts.get(3).getContactImageId() != null) {
                        userImageUrl = Constants.HTTPS_SERVER_IP + Constants.IMAGE_PREFIX + authToken + "/" + contacts.get(3).getContactImageId();
                        hampay_image_4.setTag(userImageUrl.split("/")[6]);
                        imageManager.displayImage(userImageUrl, hampay_image_4, R.drawable.user_placeholder);
                    } else {
                        hampay_image_4.setImageResource(R.drawable.user_placeholder);
                    }
                    break;

            }
        }

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        main_banner = (ImageView)rootView.findViewById(R.id.main_banner);
        main_banner.getLayoutParams().height =  (int)(size.x / 2.5);
        hampay_friend = (LinearLayout)rootView.findViewById(R.id.hampay_friend);

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
    public void onResume() {
        super.onResume();
        PendingCountRequest pendingCountRequest = new PendingCountRequest();
        RequestPendingCount requestPendingCount = new RequestPendingCount(getActivity(), new RequestPendingCountTaskCompleteListener());
        requestPendingCount.execute(pendingCountRequest);
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
                }else {
                    new Collapse(hampay_friend).animate();
                }
                break;

            case R.id.user_transaction_history:
                if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED){
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    return;
                }
                intent.setClass(getActivity(), TransactionsListActivity.class);
                startActivity(intent);
                break;

            case R.id.user_payment_request:
                if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED){
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    return;
                }
                intent.setClass(getActivity(), PaymentRequestListActivity.class);
                startActivity(intent);
                break;

            case R.id.businessPurchase:
                if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED){
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    return;
                }
                intent.setClass(getActivity(), BusinessPurchaseActivity.class);
                startActivity(intent);
                break;

            case R.id.pendingPurchasePayment:
                if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED){
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    return;
                }
                intent.setClass(getActivity(), PendingPurchasePaymentListActivity.class);
                startActivity(intent);
                break;

            case R.id.hampay_1_ll:
                intent = new Intent(getActivity(), PaymentRequestDetailActivity.class);
                intent.putExtra(Constants.HAMPAY_CONTACT, userProfileDTO.getSelectedContacts().get(0));
                startActivity(intent);
                break;
            case R.id.hampay_2_ll:
                intent = new Intent(getActivity(), PaymentRequestDetailActivity.class);
                intent.putExtra(Constants.HAMPAY_CONTACT, userProfileDTO.getSelectedContacts().get(1));
                startActivity(intent);
                break;
            case R.id.hampay_3_ll:
                intent = new Intent(getActivity(), PaymentRequestDetailActivity.class);
                intent.putExtra(Constants.HAMPAY_CONTACT, userProfileDTO.getSelectedContacts().get(2));
                startActivity(intent);
                break;
            case R.id.hampay_4_ll:
                intent = new Intent(getActivity(), PaymentRequestDetailActivity.class);
                intent.putExtra(Constants.HAMPAY_CONTACT, userProfileDTO.getSelectedContacts().get(3));
                startActivity(intent);
                break;
        }

    }

    public class RequestPendingCountTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<PendingCountResponse>>
    {

        @Override
        public void onTaskComplete(ResponseMessage<PendingCountResponse> pendingCountResponseMessage)
        {
            if (pendingCountResponseMessage != null) {
                if (pendingCountResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS){
                    int pendingCount = pendingCountResponseMessage.getService().getPendingCount();
                    if (pendingCount == 0){
                        pending_badge.setVisibility(View.GONE);
                    }else if (pendingCount > 0) {
                        pending_badge.setVisibility(View.VISIBLE);
                        pending_badge.setText(persianEnglishDigit.E2P(String.valueOf(pendingCount)));
                    }
                }else if (pendingCountResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    forceLogout();
                }
            }
        }

        @Override
        public void onTaskPreRun() {
        }
    }

    private void forceLogout() {
        editor.remove(Constants.LOGIN_TOKEN_ID);
        editor.commit();
        Intent intent = new Intent();
        intent.setClass(context, HamPayLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getActivity().finish();
        getActivity().startActivity(intent);
    }
}

