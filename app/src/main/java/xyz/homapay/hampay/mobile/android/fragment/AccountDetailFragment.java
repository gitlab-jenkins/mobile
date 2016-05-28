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
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.UserProfileRequest;
import xyz.homapay.hampay.common.core.model.response.UserProfileResponse;
import xyz.homapay.hampay.common.core.model.response.dto.UserProfileDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.activity.IntroIBANActivity;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestImageDownloader;
import xyz.homapay.hampay.mobile.android.async.RequestUserProfile;
import xyz.homapay.hampay.mobile.android.async.listener.RequestImageDownloaderTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.DeviceInfo;
import xyz.homapay.hampay.mobile.android.util.HamPayUtils;
import xyz.homapay.hampay.mobile.android.util.ImageManager;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

/**
 * Created by amir on 6/5/15.
 */
public class AccountDetailFragment extends Fragment {


    View hide_bg;

    PersianEnglishDigit persianEnglishDigit;

    FacedTextView user_name_text;
    FacedTextView user_card_number;
    ImageView image_profile;
    FacedTextView user_bank_name;
    LinearLayout iban_ll;
    FacedTextView user_cell_number;
    FacedTextView user_iban_value;
    FacedTextView user_iban_bank;
    FacedTextView user_national_code;

    HamPayDialog hamPayDialog;

    FacedTextView intro_iban_button;

    UserProfileDTO userProfileDTO;

    Bundle bundle;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    RequestUserProfile requestUserProfile;
    UserProfileRequest userProfileRequest;

    Tracker hamPayGaTracker;

    DeviceInfo deviceInfo;

    Context context;

    private String authToken = "";
    private ImageManager imageManager;


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.IBAN_CHANGE_RESULT_CODE) {
            if(resultCode == getActivity().RESULT_OK){
                iban_ll.setVisibility(View.VISIBLE);
                user_iban_value.setText("IR" + persianEnglishDigit.E2P(new HamPayUtils().splitStringEvery(data.getStringExtra(Constants.RETURN_IBAN_CONFIRMED), 4)));
                intro_iban_button.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();

        persianEnglishDigit = new PersianEnglishDigit();

        prefs = getActivity().getSharedPreferences(Constants.APP_PREFERENCE_NAME, getActivity().MODE_PRIVATE);
        editor = getActivity().getSharedPreferences(Constants.APP_PREFERENCE_NAME, getActivity().MODE_PRIVATE).edit();

        imageManager = new ImageManager(getActivity(), 200000, false);
        authToken = prefs.getString(Constants.LOGIN_TOKEN_ID, "");
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

        hamPayDialog = new HamPayDialog(getActivity());

        intro_iban_button = (FacedTextView) rootView.findViewById(R.id.intro_iban_button);
        intro_iban_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context, IntroIBANActivity.class);
                startActivityForResult(intent, Constants.IBAN_CHANGE_RESULT_CODE);
            }
        });

        image_profile = (ImageView)rootView.findViewById(R.id.image_profile);
        user_name_text = (FacedTextView)rootView.findViewById(R.id.user_name_text);
        user_card_number = (FacedTextView)rootView.findViewById(R.id.user_card_number);
        user_bank_name = (FacedTextView)rootView.findViewById(R.id.user_bank_name);
        iban_ll = (LinearLayout)rootView.findViewById(R.id.iban_ll);
        user_cell_number = (FacedTextView)rootView.findViewById(R.id.user_cell_number);
        user_iban_value = (FacedTextView)rootView.findViewById(R.id.user_iban_value);
        user_iban_bank = (FacedTextView)rootView.findViewById(R.id.user_iban_bank);
        user_national_code = (FacedTextView)rootView.findViewById(R.id.user_national_code);


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
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
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

        if (userProfileDTO.getUserImageId() != null) {
            String userImageUrl = Constants.HTTPS_SERVER_IP + Constants.IMAGE_PREFIX + authToken + "/" + userProfileDTO.getUserImageId();
            image_profile.setTag(userImageUrl.split("/")[6]);
            imageManager.displayImage(userImageUrl, image_profile, R.drawable.user_placeholder);
        }else {
            image_profile.setImageResource(R.drawable.user_placeholder);
        }

        user_card_number.setText(persianEnglishDigit.E2P(userProfileDTO.getCardDTO().getMaskedCardNumber()));
        user_bank_name.setText(userProfileDTO.getCardDTO().getBankName());
        user_cell_number.setText(persianEnglishDigit.E2P(userProfileDTO.getCellNumber()));
        if (userProfileDTO.getIbanDTO() != null) {
            user_iban_value.setText("IR" + persianEnglishDigit.E2P(new HamPayUtils().splitStringEvery(userProfileDTO.getIbanDTO().getIban(), 4)));
            user_iban_bank.setText(userProfileDTO.getIbanDTO().getBankName());
            iban_ll.setVisibility(View.VISIBLE);
            intro_iban_button.setVisibility(View.GONE);
            editor.putBoolean(Constants.SETTING_CHANGE_IBAN_STATUS, true);
            editor.commit();
        }
        user_national_code.setText(persianEnglishDigit.E2P(prefs.getString(Constants.REGISTERED_NATIONAL_CODE, "")));
//
//        hide_bg.setVisibility(View.GONE);
//
//        List<ContactDTO> contactDTOs = userProfileDTO.getSelectedContacts();
    }
}
