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

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.UserProfileRequest;
import xyz.homapay.hampay.common.core.model.response.UserProfileResponse;
import xyz.homapay.hampay.common.core.model.response.dto.UserProfileDTO;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.activity.HamPayLoginActivity;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestUserProfile;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.firebase.LogEvent;
import xyz.homapay.hampay.mobile.android.firebase.service.ServiceEvent;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.HamPayUtils;
import xyz.homapay.hampay.mobile.android.util.ImageManager;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

/**
 * Created by amir on 6/5/15.
 */
public class AccountDetailFragment extends Fragment {

    private View hide_bg;
    private PersianEnglishDigit persianEnglishDigit;
    private FacedTextView user_name_text;
    private ImageView image_profile;
    private LinearLayout iban_ll;
    private FacedTextView user_cell_number;
    private FacedTextView user_iban_value;
    private FacedTextView user_iban_bank;
    private FacedTextView user_national_code;
    private LinearLayout emailLayout;
    private FacedTextView user_email;
    private HamPayDialog hamPayDialog;
    private UserProfileDTO userProfileDTO;
    private Bundle bundle;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private RequestUserProfile requestUserProfile;
    private UserProfileRequest userProfileRequest;
    private Context context;
    private ImageManager imageManager;


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.IBAN_CHANGE_RESULT_CODE) {
            if(resultCode == getActivity().RESULT_OK){
                iban_ll.setVisibility(View.VISIBLE);
                user_iban_value.setText("IR" + persianEnglishDigit.E2P(new HamPayUtils().splitStringEvery(data.getStringExtra(Constants.RETURN_IBAN_CONFIRMED), 4)));
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
        bundle = getArguments();

        if (bundle != null){
            this.userProfileDTO = (UserProfileDTO) bundle.getSerializable(Constants.USER_PROFILE);
        }

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

        image_profile = (ImageView)rootView.findViewById(R.id.image_profile);
        user_name_text = (FacedTextView)rootView.findViewById(R.id.user_name_text);
        iban_ll = (LinearLayout)rootView.findViewById(R.id.iban_ll);
        user_cell_number = (FacedTextView)rootView.findViewById(R.id.user_cell_number);
        user_iban_value = (FacedTextView)rootView.findViewById(R.id.user_iban_value);
        user_iban_bank = (FacedTextView)rootView.findViewById(R.id.user_iban_bank);
        user_national_code = (FacedTextView)rootView.findViewById(R.id.user_national_code);
        emailLayout = (LinearLayout)rootView.findViewById(R.id.email_layout);
        user_email = (FacedTextView) rootView.findViewById(R.id.user_email);


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
        ServiceEvent serviceName;
        LogEvent logEvent = new LogEvent(context);

        public RequestUserProfileTaskCompleteListener(){
        }

        @Override
        public void onTaskComplete(ResponseMessage<UserProfileResponse> userProfileResponseMessage)
        {
            hamPayDialog.dismisWaitingDialog();
            if (userProfileResponseMessage != null) {


                if (userProfileResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS){
                    serviceName = ServiceEvent.USER_PROFILE_SUCCESS;
                    userProfileDTO = userProfileResponseMessage.getService().getUserProfile();
                    fillUserProfile(userProfileDTO);
                    hide_bg.setVisibility(View.GONE);
                } else if (userProfileResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    serviceName = ServiceEvent.USER_PROFILE_FAILURE;
                    forceLogout();
                }
                else{
                    serviceName = ServiceEvent.USER_PROFILE_FAILURE;
                    requestUserProfile = new RequestUserProfile(getActivity(), new RequestUserProfileTaskCompleteListener());
                    new HamPayDialog(getActivity()).showFailUserProfileDialog(requestUserProfile, userProfileRequest,
                            userProfileResponseMessage.getService().getResultStatus().getCode(),
                            userProfileResponseMessage.getService().getResultStatus().getDescription());
                    hide_bg.setVisibility(View.VISIBLE);
                }
            }
            else {
                serviceName = ServiceEvent.USER_PROFILE_FAILURE;
                requestUserProfile = new RequestUserProfile(getActivity(), new RequestUserProfileTaskCompleteListener());
                new HamPayDialog(getActivity()).showFailUserProfileDialog(requestUserProfile, userProfileRequest,
                        Constants.LOCAL_ERROR_CODE,
                        getString(R.string.msg_fail_user_profile));

                hide_bg.setVisibility(View.VISIBLE);
            }
            logEvent.log(serviceName);
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
            hide_bg.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (prefs.getString(Constants.REGISTERED_USER_EMAIL, "").length() != 0) {
            user_email.setText(prefs.getString(Constants.REGISTERED_USER_EMAIL, ""));
            emailLayout.setVisibility(View.VISIBLE);
        }else {
            emailLayout.setVisibility(View.GONE);
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
            image_profile.setTag(userProfileDTO.getUserImageId());
            imageManager.displayImage(userProfileDTO.getUserImageId(), image_profile, R.drawable.user_placeholder);
        }else {
            image_profile.setImageResource(R.drawable.user_placeholder);
        }

        user_cell_number.setText(persianEnglishDigit.E2P(userProfileDTO.getCellNumber()));
        if (userProfileDTO.getIbanDTO() != null) {
            user_iban_value.setText("IR" + persianEnglishDigit.E2P(new HamPayUtils().splitStringEvery(userProfileDTO.getIbanDTO().getIban(), 4)));
            user_iban_bank.setText(userProfileDTO.getIbanDTO().getBankName());
            iban_ll.setVisibility(View.VISIBLE);
            editor.putBoolean(Constants.SETTING_CHANGE_IBAN_STATUS, true);
            editor.commit();
        }
        user_national_code.setText(persianEnglishDigit.E2P(userProfileDTO.getNationalCode()));
    }

    private void forceLogout() {
        editor.remove(Constants.LOGIN_TOKEN_ID);
        editor.commit();
        Intent intent = new Intent();
        intent.setClass(context, HamPayLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (getActivity() != null) {
            getActivity().finish();
            getActivity().startActivity(intent);
        }
    }
}
