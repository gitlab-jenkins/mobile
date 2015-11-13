package xyz.homapay.hampay.mobile.android.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.dto.ContactDTO;
import xyz.homapay.hampay.common.core.model.dto.UserVerificationStatus;
import xyz.homapay.hampay.common.core.model.request.UserProfileRequest;
import xyz.homapay.hampay.common.core.model.request.VerifyAccountRequest;
import xyz.homapay.hampay.common.core.model.response.UserProfileResponse;
import xyz.homapay.hampay.common.core.model.response.VerifyAccountResponse;
import xyz.homapay.hampay.common.core.model.response.dto.UserProfileDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.activity.MainActivity;
import xyz.homapay.hampay.mobile.android.activity.PayOneActivity;
import xyz.homapay.hampay.mobile.android.activity.VerifyAccountActivity;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestImageDownloader;
import xyz.homapay.hampay.mobile.android.async.RequestUserProfile;
import xyz.homapay.hampay.mobile.android.async.RequestVerifyAccount;
import xyz.homapay.hampay.mobile.android.async.listener.RequestImageDownloaderTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.circleimageview.CircleImageView;
import xyz.homapay.hampay.mobile.android.component.material.ButtonRectangle;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.util.AESHelper;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.DeviceInfo;
import xyz.homapay.hampay.mobile.android.util.JalaliConvert;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;
import xyz.homapay.hampay.mobile.android.util.SecurityUtils;

import java.io.File;
import java.util.List;

/**
 * Created by amir on 6/5/15.
 */
public class AccountDetailFragment extends Fragment implements View.OnClickListener {


    View hide_bg;

    String user_image_url = "";

    PersianEnglishDigit persianEnglishDigit;

    LinearLayout verification_status_ll;

    ImageView user_image;
    FacedTextView user_name_text;
    FacedTextView user_account_no_text;
    FacedTextView user_bank_name;
    FacedTextView user_mobile_no;
    FacedTextView user_account_type;
    FacedTextView user_account_title;
    FacedTextView user_last_login;
    FacedTextView hampay_1;
    FacedTextView hampay_2;
    FacedTextView hampay_3;
    FacedTextView hampay_4;
    ImageView hampay_image_1;
    CircleImageView hampay_image_2;
    ImageView hampay_image_3;
    ImageView hampay_image_4;
    LinearLayout hampay_1_ll;
    LinearLayout hampay_2_ll;
    LinearLayout hampay_3_ll;
    LinearLayout hampay_4_ll;

    RequestImageDownloader requestImageDownloader;
    CircleImageView image_profile;

    HamPayDialog hamPayDialog;

    ButtonRectangle verify_account_button;

    UserProfileDTO userProfileDTO;

    Bundle bundle;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    RequestVerifyAccount requestVerifyAccount;
    VerifyAccountRequest verifyAccountRequest;

    RequestUserProfile requestUserProfile;
    UserProfileRequest userProfileRequest;

    Tracker hamPayGaTracker;

    byte[] mobileKey;
    String serverKey;

    String encryptedData;

    DeviceInfo deviceInfo;

    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();

        persianEnglishDigit = new PersianEnglishDigit();

        prefs = getActivity().getSharedPreferences(Constants.APP_PREFERENCE_NAME, getActivity().MODE_PRIVATE);
        editor = getActivity().getSharedPreferences(Constants.APP_PREFERENCE_NAME, getActivity().MODE_PRIVATE).edit();

        hamPayGaTracker = ((HamPayApplication) getActivity().getApplicationContext())
                .getTracker(HamPayApplication.TrackerName.APP_TRACKER);


        bundle = getArguments();

        if (bundle != null){
            this.userProfileDTO = (UserProfileDTO) bundle.getSerializable(Constants.USER_PROFILE_DTO);
        }

        deviceInfo = new DeviceInfo(context);

        try {

            mobileKey = SecurityUtils.getInstance(context).generateSHA_256(
                    deviceInfo.getMacAddress(),
                    deviceInfo.getIMEI(),
                    deviceInfo.getAndroidId());

            serverKey = prefs.getString(Constants.USER_ID_TOKEN, "");

            if (userProfileDTO.getMaxXferAmount() != null && userProfileDTO.getMinXferAmount() != null) {
                editor.putLong(Constants.MAX_XFER_Amount, this.userProfileDTO.getMaxXferAmount());
                editor.putLong(Constants.MIN_XFER_Amount, this.userProfileDTO.getMinXferAmount());
                editor.commit();
            }

        }
        catch (Exception ex){
            Log.e("Error", ex.getStackTrace().toString());
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account_detail, container, false);

        hide_bg = (View)rootView.findViewById(R.id.hide_bg);

        verification_status_ll = (LinearLayout)rootView.findViewById(R.id.verification_status_ll);

        image_profile = (CircleImageView)rootView.findViewById(R.id.image_profile);

        String URL = Constants.HTTPS_SERVER_IP + "/users/" + prefs.getString(Constants.LOGIN_TOKEN_ID, "") + "/" + userProfileDTO.getUserImageId();

        requestImageDownloader = new RequestImageDownloader(context, new RequestImageDownloaderTaskCompleteListener(image_profile));
        requestImageDownloader.execute(URL);

        String filePath = context.getFilesDir().getPath().toString() + "/" + "userImage.png";
        File file = new File(filePath);
        if (file.exists()){
            Picasso.with(context).invalidate(file);
            Picasso.with(context).load(file).into(image_profile);
        }


        hamPayDialog = new HamPayDialog(getActivity());

        hampay_1_ll = (LinearLayout)rootView.findViewById(R.id.hampay_1_ll);
        hampay_2_ll = (LinearLayout)rootView.findViewById(R.id.hampay_2_ll);
        hampay_3_ll = (LinearLayout)rootView.findViewById(R.id.hampay_3_ll);
        hampay_4_ll = (LinearLayout)rootView.findViewById(R.id.hampay_4_ll);
        hampay_1_ll.setOnClickListener(this);
        hampay_2_ll.setOnClickListener(this);
        hampay_3_ll.setOnClickListener(this);
        hampay_4_ll.setOnClickListener(this);

        verify_account_button = (ButtonRectangle)rootView.findViewById(R.id.verify_account_button);
        verify_account_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyAccountRequest = new VerifyAccountRequest();
                requestVerifyAccount = new RequestVerifyAccount(getActivity(), new RequestVerifyAccountTaskCompleteListener());
                requestVerifyAccount.execute(verifyAccountRequest);
            }
        });



//        user_image = (ImageView)rootView.findViewById(R.id.user_image);
        user_name_text = (FacedTextView)rootView.findViewById(R.id.user_name_text);
        user_account_no_text = (FacedTextView)rootView.findViewById(R.id.user_account_no_text);
        user_bank_name = (FacedTextView)rootView.findViewById(R.id.user_bank_name);
        user_mobile_no = (FacedTextView)rootView.findViewById(R.id.user_mobile_no);
        user_account_type = (FacedTextView)rootView.findViewById(R.id.user_account_type);
        user_account_title = (FacedTextView)rootView.findViewById(R.id.user_account_title);
        user_last_login = (FacedTextView)rootView.findViewById(R.id.user_last_login);

        hampay_1 = (FacedTextView)rootView.findViewById(R.id.hampay_1);
        hampay_2 = (FacedTextView)rootView.findViewById(R.id.hampay_2);
        hampay_3 = (FacedTextView)rootView.findViewById(R.id.hampay_3);
        hampay_4 = (FacedTextView)rootView.findViewById(R.id.hampay_4);
        hampay_image_1 = (ImageView)rootView.findViewById(R.id.hampay_image_1);
        hampay_image_2 = (CircleImageView)rootView.findViewById(R.id.hampay_image_2);
        hampay_image_3 = (ImageView)rootView.findViewById(R.id.hampay_image_3);
        hampay_image_4 = (ImageView)rootView.findViewById(R.id.hampay_image_4);


        if (prefs.getBoolean(Constants.FORCE_USER_PROFILE, false)){
            userProfileRequest = new UserProfileRequest();
            requestUserProfile = new RequestUserProfile(getActivity(), new RequestUserProfileTaskCompleteListener());
            requestUserProfile.execute(userProfileRequest);
        }else {
            fillUserProfile(userProfileDTO);
            editor.putBoolean(Constants.FORCE_USER_PROFILE, true);
            editor.commit();
        }

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();

        if (requestUserProfile != null) {
            if (!requestUserProfile.isCancelled())
                requestUserProfile.cancel(true);
        }
        if (requestVerifyAccount != null){
            if (!requestVerifyAccount.isCancelled()){
                requestVerifyAccount.cancel(true);
            }
        }

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

        Intent intent;

        switch (v.getId()){
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


    public class RequestUserProfileTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<UserProfileResponse>>
    {
        public RequestUserProfileTaskCompleteListener(){
        }

        @Override
        public void onTaskComplete(ResponseMessage<UserProfileResponse> userProfileResponseMessage)
        {

            Log.e("FINISH", "FINISH");

            hamPayDialog.dismisWaitingDialog();


            if (userProfileResponseMessage != null) {


                if (userProfileResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS){

                    userProfileDTO = userProfileResponseMessage.getService().getUserProfile();

                    fillUserProfile(userProfileDTO);

                    hide_bg.setVisibility(View.GONE);

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("User Profile")
                            .setAction("Fetch")
                            .setLabel("Success")
                            .build());

                }
                else{
                    requestUserProfile = new RequestUserProfile(getActivity(), new RequestUserProfileTaskCompleteListener());
                    new HamPayDialog(getActivity()).showFailUserProfileDialog(requestUserProfile, userProfileRequest,
                            userProfileResponseMessage.getService().getResultStatus().getCode(),
                            userProfileResponseMessage.getService().getResultStatus().getDescription());

                    hide_bg.setVisibility(View.VISIBLE);

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("User Profile")
                            .setAction("Fetch")
                            .setLabel("Fail(Server)")
                            .build());
                }
            }
            else {
                requestUserProfile = new RequestUserProfile(getActivity(), new RequestUserProfileTaskCompleteListener());
                new HamPayDialog(getActivity()).showFailUserProfileDialog(requestUserProfile, userProfileRequest,
                        Constants.LOCAL_ERROR_CODE,
                        getString(R.string.msg_fail_user_profile));

                hide_bg.setVisibility(View.VISIBLE);

                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("User Profile")
                        .setAction("Fetch")
                        .setLabel("Fail(Mobile)")
                        .build());
            }
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingdDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
            hide_bg.setVisibility(View.VISIBLE);
        }
    }


    public class RequestVerifyAccountTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<VerifyAccountResponse>>
    {
        public RequestVerifyAccountTaskCompleteListener(){
        }

        @Override
        public void onTaskComplete(ResponseMessage<VerifyAccountResponse> verifyAccountResponseMessage)
        {
            hamPayDialog.dismisWaitingDialog();
            if (verifyAccountResponseMessage != null) {

                if (verifyAccountResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS){

                    Intent intent = new Intent();
                    intent.setClass(getActivity(), VerifyAccountActivity.class);
                    intent.putExtra(Constants.TRANSFER_MONEY_COMMENT, verifyAccountResponseMessage.getService().getTransferMoneyComment());
                    startActivityForResult(intent, 1023);
                    startActivity(intent);


                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Verify Account")
                            .setAction("Verify")
                            .setLabel("Success")
                            .build());

                }
                else{
                    hamPayDialog.dismisWaitingDialog();
                    requestVerifyAccount = new RequestVerifyAccount(getActivity(), new RequestVerifyAccountTaskCompleteListener());
                    new HamPayDialog(getActivity()).showFailVerifyAccountDialog(requestVerifyAccount, verifyAccountRequest,
                            verifyAccountResponseMessage.getService().getResultStatus().getCode(),
                            verifyAccountResponseMessage.getService().getResultStatus().getDescription());

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Verify Account")
                            .setAction("Verify")
                            .setLabel("Fail(Server)")
                            .build());
                }
            }
            else {
                hamPayDialog.dismisWaitingDialog();
                requestVerifyAccount = new RequestVerifyAccount(getActivity(), new RequestVerifyAccountTaskCompleteListener());
                new HamPayDialog(getActivity()).showFailVerifyAccountDialog(requestVerifyAccount, verifyAccountRequest,
                        Constants.LOCAL_ERROR_CODE,
                        getString(R.string.msg_fail_verify_account));

                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Verify Account")
                        .setAction("Verify")
                        .setLabel("Fail(Mobile)")
                        .build());
            }
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingdDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }


    JalaliConvert jalaliConvert;

    private void fillUserProfile(UserProfileDTO userProfileDTO){

        if (userProfileDTO.getMaxXferAmount() != null && userProfileDTO.getMinXferAmount() != null) {
            editor.putLong(Constants.MAX_XFER_Amount, this.userProfileDTO.getMaxXferAmount());
            editor.putLong(Constants.MIN_XFER_Amount, this.userProfileDTO.getMinXferAmount());
            editor.commit();
        }

        if (userProfileDTO.getVerificationStatus() == UserVerificationStatus.UNVERIFIED) {
            verification_status_ll.setVisibility(View.VISIBLE);
        }else {
            verification_status_ll.setVisibility(View.GONE);
        }
        jalaliConvert = new JalaliConvert();


        if (userProfileDTO.getVerificationStatus() == UserVerificationStatus.UNVERIFIED){
//            user_image.setImageResource(R.drawable.user_icon_blak);
        }else {
//            user_image.setImageResource(R.drawable.user_icon_blue);
        }
        user_name_text.setText(userProfileDTO.getFullName());
        MainActivity.user_account_name.setText(userProfileDTO.getFullName());

        user_account_no_text.setText(persianEnglishDigit.E2P(userProfileDTO.getAccountNumber()));
        user_bank_name.setText(" " + userProfileDTO.getBankName());
        user_mobile_no.setText(persianEnglishDigit.E2P(userProfileDTO.getCellNumber()));

        editor.putInt(Constants.USER_VERIFICATION_STATUS, userProfileDTO.getVerificationStatus().ordinal());
        editor.commit();

        switch (userProfileDTO.getVerificationStatus()){

            case UNVERIFIED:
                user_account_type.setText(getString(R.string.unverified_account));
                break;

            case PENDING_REVIEW:
                user_account_type.setText(getString(R.string.pending_review_account));
                break;

            case VERIFIED:
                user_account_type.setText(getString(R.string.verified_account));
                break;

            case DELEGATED:
                user_account_type.setText(getString(R.string.delegate_account));
                break;
        }


        if (userProfileDTO.getLastLoginDate() != null) {
            user_last_login.setText(getString(R.string.last_login) + ": "
                    + persianEnglishDigit.E2P(jalaliConvert.GregorianToPersian(userProfileDTO.getLastLoginDate())));
        }else {
            user_last_login.setText("");
        }

        hide_bg.setVisibility(View.GONE);

        List<ContactDTO> contactDTOs = userProfileDTO.getSelectedContacts();


        for (int contact = 0; contact < contactDTOs.size(); contact++){
            switch (contact){
                case 0:
                    hampay_1_ll.setVisibility(View.VISIBLE);
                    hampay_1.setText(contactDTOs.get(0).getDisplayName());
                    if (contactDTOs.get(0).getContactImageId() != null) {
                        user_image_url = Constants.HTTPS_SERVER_IP + "/users/" + prefs.getString(Constants.LOGIN_TOKEN_ID, "") + "/" + contactDTOs.get(0).getContactImageId();
                        new RequestImageDownloader(context, new RequestImageDownloaderTaskCompleteListener(hampay_image_1)).execute(user_image_url);
                    }
//                    if (contactDTOs.get(0).getUserVerificationStatus() == UserVerificationStatus.DELEGATED){
//                        hampay_image_1.setImageResource(R.drawable.user_icon_blue_s);
//                    }else {
//                        hampay_image_1.setImageResource(R.drawable.user_icon_blak_s);
//                    }
                    break;
                case 1:
                    hampay_2_ll.setVisibility(View.VISIBLE);
                    hampay_2.setText(contactDTOs.get(1).getDisplayName());
                    if (contactDTOs.get(1).getContactImageId() != null) {
                        user_image_url = Constants.HTTPS_SERVER_IP + "/users/" + prefs.getString(Constants.LOGIN_TOKEN_ID, "") + "/" + contactDTOs.get(1).getContactImageId();
                        new RequestImageDownloader(context, new RequestImageDownloaderTaskCompleteListener(hampay_image_2)).execute(user_image_url);
                    }
//                    if (contactDTOs.get(1).getUserVerificationStatus() == UserVerificationStatus.DELEGATED){
//                        hampay_image_2.setImageResource(R.drawable.user_icon_blue_s);
//                    }else {
//                        hampay_image_2.setImageResource(R.drawable.user_icon_blak_s);
//                    }
                    break;
                case 2:
                    hampay_3_ll.setVisibility(View.VISIBLE);
                    hampay_3.setText(contactDTOs.get(2).getDisplayName());
                    if (contactDTOs.get(2).getContactImageId() != null) {
                        user_image_url = Constants.HTTPS_SERVER_IP + "/users/" + prefs.getString(Constants.LOGIN_TOKEN_ID, "") + "/" + contactDTOs.get(2).getContactImageId();
                        new RequestImageDownloader(context, new RequestImageDownloaderTaskCompleteListener(hampay_image_3)).execute(user_image_url);
                    }

//                    if (contactDTOs.get(2).getUserVerificationStatus() == UserVerificationStatus.DELEGATED){
//                        hampay_image_3.setImageResource(R.drawable.user_icon_blue_s);
//                    }else {
//                        hampay_image_3.setImageResource(R.drawable.user_icon_blak_s);
//                    }
                    break;
                case 3:
                    hampay_4_ll.setVisibility(View.VISIBLE);
                    hampay_4.setText(contactDTOs.get(3).getDisplayName());
                    if (contactDTOs.get(3).getContactImageId() != null) {
                        user_image_url = Constants.HTTPS_SERVER_IP + "/users/" + prefs.getString(Constants.LOGIN_TOKEN_ID, "") + "/" + contactDTOs.get(3).getContactImageId();
                        new RequestImageDownloader(context, new RequestImageDownloaderTaskCompleteListener(hampay_image_4)).execute(user_image_url);
                    }

//                    if (contactDTOs.get(3).getUserVerificationStatus() == UserVerificationStatus.DELEGATED){
//                        hampay_image_4.setImageResource(R.drawable.user_icon_blue_s);
//                    }else {
//                        hampay_image_4.setImageResource(R.drawable.user_icon_blak_s);
//                    }
                    break;

            }
        }

    }
}
