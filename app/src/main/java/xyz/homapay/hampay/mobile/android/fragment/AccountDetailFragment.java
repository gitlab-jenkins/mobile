package xyz.homapay.hampay.mobile.android.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.List;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.dto.ContactDTO;
import xyz.homapay.hampay.common.core.model.request.UserProfileRequest;
import xyz.homapay.hampay.common.core.model.response.UserProfileResponse;
import xyz.homapay.hampay.common.core.model.response.dto.UserProfileDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.activity.IntroIBANActivity;
import xyz.homapay.hampay.mobile.android.activity.MainActivity;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestUserProfile;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.circleimageview.CircleImageView;
import xyz.homapay.hampay.mobile.android.component.material.ButtonRectangle;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.DeviceInfo;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

/**
 * Created by amir on 6/5/15.
 */
public class AccountDetailFragment extends Fragment {


    View hide_bg;

    PersianEnglishDigit persianEnglishDigit;

    FacedTextView user_name_text;
    FacedTextView user_account_no_text;
    FacedTextView user_bank_name;
    LinearLayout iban_ll;
    FacedTextView user_mobile_no;
    FacedTextView user_iban_value;
    FacedTextView user_iban_bank;
    FacedTextView user_national_code;
    LinearLayout selectedHampay;
    FacedTextView hampay_1;
    FacedTextView hampay_2;
    FacedTextView hampay_3;
    FacedTextView hampay_4;
    CircleImageView hampay_image_1;
    CircleImageView hampay_image_2;
    CircleImageView hampay_image_3;
    CircleImageView hampay_image_4;

    HamPayDialog hamPayDialog;

    ButtonRectangle intro_account_button;

    UserProfileDTO userProfileDTO;

    Bundle bundle;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    RequestUserProfile requestUserProfile;
    UserProfileRequest userProfileRequest;

    Tracker hamPayGaTracker;

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

        deviceInfo = new DeviceInfo(getActivity());

        try {
            editor.putLong(Constants.MAX_BUSINESS_XFER_AMOUNT, this.userProfileDTO.getMaxBusinessXferAmount());
            editor.putLong(Constants.MIN_BUSINESS_XFER_AMOUNT, this.userProfileDTO.getMinBusinessXferAmount());
            editor.putLong(Constants.MAX_INDIVIDUAL_XFER_AMOUNT, this.userProfileDTO.getMaxIndividualXferAmount());
            editor.putLong(Constants.MIN_INDIVIDUAL_XFER_AMOUNT, this.userProfileDTO.getMinIndividualXferAmount());
            editor.commit();
        }
        catch (Exception ex) {
            Log.e("Error", ex.getStackTrace().toString());
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account_detail, container, false);

        hide_bg = (View)rootView.findViewById(R.id.hide_bg);


//        image_profile = (CircleImageView)rootView.findViewById(R.id.image_profile);

        String URL = Constants.HTTPS_SERVER_IP + Constants.IMAGE_PREFIX + prefs.getString(Constants.LOGIN_TOKEN_ID, "") + "/" + userProfileDTO.getUserImageId();

//        requestImageDownloader = new RequestImageDownloader(context, new RequestImageDownloaderTaskCompleteListener(image_profile));
//        requestImageDownloader.execute(URL);

//        String filePath = context.getFilesDir().getPath().toString() + "/" + "userImage.png";
//        File file = new File(filePath);
//        if (file.exists()){
//            Picasso.with(context).invalidate(file);
//            Picasso.with(context).load(file).into(image_profile);
//        }


        hamPayDialog = new HamPayDialog(getActivity());

        selectedHampay = (LinearLayout)rootView.findViewById(R.id.selectedHampay);

        intro_account_button = (ButtonRectangle)rootView.findViewById(R.id.intro_account_button);
        intro_account_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context, IntroIBANActivity.class);
                startActivityForResult(intent, 1023);
            }
        });


        user_name_text = (FacedTextView)rootView.findViewById(R.id.user_name_text);
        user_account_no_text = (FacedTextView)rootView.findViewById(R.id.user_account_no_text);
        user_bank_name = (FacedTextView)rootView.findViewById(R.id.user_bank_name);
        iban_ll = (LinearLayout)rootView.findViewById(R.id.iban_ll);
        user_mobile_no = (FacedTextView)rootView.findViewById(R.id.user_mobile_no);
        user_iban_value = (FacedTextView)rootView.findViewById(R.id.user_iban_value);
        user_iban_bank = (FacedTextView)rootView.findViewById(R.id.user_iban_bank);
        user_national_code = (FacedTextView)rootView.findViewById(R.id.user_national_code);

        hampay_1 = (FacedTextView)rootView.findViewById(R.id.hampay_1);
        hampay_2 = (FacedTextView)rootView.findViewById(R.id.hampay_2);
        hampay_3 = (FacedTextView)rootView.findViewById(R.id.hampay_3);
        hampay_4 = (FacedTextView)rootView.findViewById(R.id.hampay_4);
        hampay_image_1 = (CircleImageView)rootView.findViewById(R.id.hampay_image_1);
        hampay_image_2 = (CircleImageView)rootView.findViewById(R.id.hampay_image_2);
        hampay_image_3 = (CircleImageView)rootView.findViewById(R.id.hampay_image_3);
        hampay_image_4 = (CircleImageView)rootView.findViewById(R.id.hampay_image_4);


        if (prefs.getBoolean(Constants.FORCE_USER_PROFILE, false)){
            userProfileRequest = new UserProfileRequest();
            requestUserProfile = new RequestUserProfile(getActivity(), new RequestUserProfileTaskCompleteListener());
            requestUserProfile.execute(userProfileRequest);
        }else {
            fillUserProfile(userProfileDTO);
            editor.putBoolean(Constants.FORCE_USER_PROFILE, true);
            editor.commit();
        }

        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();

        if (requestUserProfile != null) {
            if (!requestUserProfile.isCancelled())
                requestUserProfile.cancel(true);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
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

    private void fillUserProfile(UserProfileDTO userProfileDTO){

        editor.putLong(Constants.MAX_BUSINESS_XFER_AMOUNT, this.userProfileDTO.getMaxBusinessXferAmount());
        editor.putLong(Constants.MIN_BUSINESS_XFER_AMOUNT, this.userProfileDTO.getMinBusinessXferAmount());
        editor.putLong(Constants.MAX_INDIVIDUAL_XFER_AMOUNT, this.userProfileDTO.getMaxIndividualXferAmount());
        editor.putLong(Constants.MIN_INDIVIDUAL_XFER_AMOUNT, this.userProfileDTO.getMinIndividualXferAmount());
        editor.commit();

        user_name_text.setText(userProfileDTO.getFullName());
        MainActivity.user_account_name.setText(userProfileDTO.getFullName());

        user_account_no_text.setText(persianEnglishDigit.E2P(userProfileDTO.getCardDTO().getMaskedCardNumber()));
        user_bank_name.setText(" " + userProfileDTO.getCardDTO().getBankName());
        user_mobile_no.setText(persianEnglishDigit.E2P(userProfileDTO.getCellNumber()));
        if (userProfileDTO.getIbanDTO() != null) {
            user_iban_value.setText("IR" + persianEnglishDigit.E2P(userProfileDTO.getIbanDTO().getIban()));
            user_iban_bank.setText(userProfileDTO.getIbanDTO().getBankName());
            iban_ll.setVisibility(View.VISIBLE);
        }
        user_national_code.setText(persianEnglishDigit.E2P(prefs.getString(Constants.REGISTERED_NATIONAL_CODE, "")));

        hide_bg.setVisibility(View.GONE);

        List<ContactDTO> contactDTOs = userProfileDTO.getSelectedContacts();

        if (contactDTOs.size() > 0){
            selectedHampay.setVisibility(View.VISIBLE);
        }
    }
}
