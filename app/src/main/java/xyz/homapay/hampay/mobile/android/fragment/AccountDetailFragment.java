package xyz.homapay.hampay.mobile.android.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
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
import xyz.homapay.hampay.mobile.android.activity.IntroAccountActivity;
import xyz.homapay.hampay.mobile.android.activity.MainActivity;
import xyz.homapay.hampay.mobile.android.activity.PayOneActivity;
import xyz.homapay.hampay.mobile.android.activity.RequestPayBusinessListActivity;
import xyz.homapay.hampay.mobile.android.activity.RequestBusinessPayDetailActivity;
import xyz.homapay.hampay.mobile.android.activity.VerifyAccountActivity;
import xyz.homapay.hampay.mobile.android.animation.Expand;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestImageDownloader;
import xyz.homapay.hampay.mobile.android.async.RequestUserProfile;
import xyz.homapay.hampay.mobile.android.async.RequestVerifyAccount;
import xyz.homapay.hampay.mobile.android.async.listener.RequestImageDownloaderTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.circleimageview.CircleImageView;
import xyz.homapay.hampay.mobile.android.component.material.ButtonRectangle;
import xyz.homapay.hampay.mobile.android.component.material.RippleView;
import xyz.homapay.hampay.mobile.android.component.progressbar.ColorsShape;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
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
    FacedTextView user_iban_value;
    FacedTextView user_iban_bank;
    FacedTextView user_national_code;
    FacedTextView user_account_type;
    FacedTextView user_account_title;
    FacedTextView user_last_login;
    FacedTextView hampay_1;
    FacedTextView hampay_2;
    FacedTextView hampay_3;
    FacedTextView hampay_4;
    CircleImageView hampay_image_1;
    CircleImageView hampay_image_2;
    CircleImageView hampay_image_3;
    CircleImageView hampay_image_4;
    LinearLayout hampay_1_ll;
    LinearLayout hampay_2_ll;
    LinearLayout hampay_3_ll;
    LinearLayout hampay_4_ll;

    RippleView digit_1;
    RippleView digit_2;
    RippleView digit_3;
    RippleView digit_4;
    RippleView digit_5;
    RippleView digit_6;
    RippleView digit_7;
    RippleView digit_8;
    RippleView digit_9;
    RippleView digit_0;
    RippleView keyboard_help;
    RippleView backspace;

    FacedTextView input_digit_1;
    FacedTextView input_digit_2;
    FacedTextView input_digit_3;
    FacedTextView input_digit_4;
    FacedTextView input_digit_5;
    FacedTextView input_digit_6;

    ButtonRectangle business_request_pay_button;

    RequestImageDownloader requestImageDownloader;
    CircleImageView image_profile;

    HamPayDialog hamPayDialog;

    ButtonRectangle verify_account_button;
    ButtonRectangle intro_account_button;

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

    LinearLayout keyboard;
    LinearLayout activation_holder;

    String inputPasswordValue = "";

    FacedTextView request_business_name;
    FacedTextView request_business_code;

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

        request_business_name = (FacedTextView)rootView.findViewById(R.id.request_business_name);
        request_business_name.setOnClickListener(this);
        request_business_code = (FacedTextView)rootView.findViewById(R.id.request_business_code);
        request_business_code.setOnClickListener(this);

        keyboard = (LinearLayout)rootView.findViewById(R.id.keyboard);
        activation_holder = (LinearLayout)rootView.findViewById(R.id.activation_holder);
        activation_holder.setOnClickListener(this);

        business_request_pay_button = (ButtonRectangle)rootView.findViewById(R.id.business_request_pay_button);
        business_request_pay_button.setOnClickListener(this);

        input_digit_1 = (FacedTextView)rootView.findViewById(R.id.input_digit_1);
        input_digit_2 = (FacedTextView)rootView.findViewById(R.id.input_digit_2);
        input_digit_3 = (FacedTextView)rootView.findViewById(R.id.input_digit_3);
        input_digit_4 = (FacedTextView)rootView.findViewById(R.id.input_digit_4);
        input_digit_5 = (FacedTextView)rootView.findViewById(R.id.input_digit_5);
        input_digit_6 = (FacedTextView)rootView.findViewById(R.id.input_digit_6);

        digit_1 = (RippleView)rootView.findViewById(R.id.digit_1);
        digit_1.setOnClickListener(this);
        digit_2 = (RippleView)rootView.findViewById(R.id.digit_2);
        digit_2.setOnClickListener(this);
        digit_3 = (RippleView)rootView.findViewById(R.id.digit_3);
        digit_3.setOnClickListener(this);
        digit_4 = (RippleView)rootView.findViewById(R.id.digit_4);
        digit_4.setOnClickListener(this);
        digit_5 = (RippleView)rootView.findViewById(R.id.digit_5);
        digit_5.setOnClickListener(this);
        digit_6 = (RippleView)rootView.findViewById(R.id.digit_6);
        digit_6.setOnClickListener(this);
        digit_7 = (RippleView)rootView.findViewById(R.id.digit_7);
        digit_7.setOnClickListener(this);
        digit_8 = (RippleView)rootView.findViewById(R.id.digit_8);
        digit_8.setOnClickListener(this);
        digit_9 = (RippleView)rootView.findViewById(R.id.digit_9);
        digit_9.setOnClickListener(this);
        digit_0 = (RippleView)rootView.findViewById(R.id.digit_0);
        digit_0.setOnClickListener(this);
        keyboard_help = (RippleView)rootView.findViewById(R.id.keyboard_help);
        keyboard_help.setOnClickListener(this);
        backspace = (RippleView)rootView.findViewById(R.id.backspace);
        backspace.setOnClickListener(this);

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


        intro_account_button = (ButtonRectangle)rootView.findViewById(R.id.intro_account_button);
        intro_account_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context, IntroAccountActivity.class);
                startActivityForResult(intent, 1023);
            }
        });

        verify_account_button = (ButtonRectangle)rootView.findViewById(R.id.verify_account_button);
        verify_account_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyAccountRequest = new VerifyAccountRequest();
                requestVerifyAccount = new RequestVerifyAccount(getActivity(), new RequestVerifyAccountTaskCompleteListener());
                requestVerifyAccount.execute(verifyAccountRequest);
            }
        });


        user_name_text = (FacedTextView)rootView.findViewById(R.id.user_name_text);
        user_account_no_text = (FacedTextView)rootView.findViewById(R.id.user_account_no_text);
        user_bank_name = (FacedTextView)rootView.findViewById(R.id.user_bank_name);
        user_mobile_no = (FacedTextView)rootView.findViewById(R.id.user_mobile_no);
        user_iban_value = (FacedTextView)rootView.findViewById(R.id.user_iban_value);
        user_iban_bank = (FacedTextView)rootView.findViewById(R.id.user_iban_bank);
        user_national_code = (FacedTextView)rootView.findViewById(R.id.user_national_code);
        user_account_type = (FacedTextView)rootView.findViewById(R.id.user_account_type);
        user_account_title = (FacedTextView)rootView.findViewById(R.id.user_account_title);
        user_last_login = (FacedTextView)rootView.findViewById(R.id.user_last_login);

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

        Intent intent = new Intent();

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

            case R.id.activation_holder:
                if (keyboard.getVisibility() != View.VISIBLE)
                    new Expand(keyboard).animate();
                break;

            case R.id.digit_1:
                inputDigit("1");
                break;

            case R.id.rect:
                inputDigit("1");
                break;

            case R.id.digit_2:
                inputDigit("2");
                break;

            case R.id.digit_3:
                inputDigit("3");
                break;

            case R.id.digit_4:
                inputDigit("4");
                break;

            case R.id.digit_5:
                inputDigit("5");
                break;

            case R.id.digit_6:
                inputDigit("6");
                break;

            case R.id.digit_7:
                inputDigit("7");
                break;

            case R.id.digit_8:
                inputDigit("8");
                break;

            case R.id.digit_9:
                inputDigit("9");
                break;

            case R.id.digit_0:
                inputDigit("0");
                break;

            case R.id.backspace:
                inputDigit("d");
                break;

            case  R.id.business_request_pay_button:
                intent.setClass(context, RequestBusinessPayDetailActivity.class);
                startActivity(intent);
                input_digit_1.setText("");
                input_digit_1.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                input_digit_2.setText("");
                input_digit_2.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                input_digit_3.setText("");
                input_digit_3.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                input_digit_4.setText("");
                input_digit_4.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                input_digit_5.setText("");
                input_digit_5.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                input_digit_6.setText("");
                input_digit_6.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                break;

            case R.id.request_business_name:
//                request_business_name.setTextColor(Color.WHITE);
//                request_business_code.setTextColor(Color.BLACK);

                intent.setClass(context, RequestPayBusinessListActivity.class);
                startActivity(intent);
                break;

            case R.id.request_business_code:
                request_business_name.setTextColor(Color.BLACK);
                request_business_code.setTextColor(Color.WHITE);
                break;
        }
    }

    private void inputDigit(String digit){
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        if (inputPasswordValue.length() <= 5) {

            switch (inputPasswordValue.length()) {
                case 0:
                    if (digit.equalsIgnoreCase("d")) {
                        input_digit_1.setText("");
                        input_digit_1.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    } else {
                        input_digit_1.setText(persianEnglishDigit.E2P(digit));
                        input_digit_1.setBackgroundColor(Color.TRANSPARENT);
                    }
                    input_digit_2.setText("");
                    input_digit_2.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    input_digit_3.setText("");
                    input_digit_3.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    input_digit_4.setText("");
                    input_digit_4.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    input_digit_5.setText("");
                    input_digit_5.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    input_digit_6.setText("");
                    input_digit_6.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    vibrator.vibrate(20);
                    break;

                case 1:
                    if (digit.equalsIgnoreCase("d")) {
                        input_digit_2.setText("");
                        input_digit_2.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    } else {
                        input_digit_2.setText(persianEnglishDigit.E2P(digit));
                        input_digit_2.setBackgroundColor(Color.TRANSPARENT);
                    }
                    input_digit_3.setText("");
                    input_digit_3.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    input_digit_4.setText("");
                    input_digit_4.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    input_digit_5.setText("");
                    input_digit_5.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    input_digit_6.setText("");
                    input_digit_6.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    vibrator.vibrate(20);

                    break;
                case 2:
                    if (digit.equalsIgnoreCase("d")) {
                        input_digit_3.setText("");
                        input_digit_3.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    } else {
                        input_digit_3.setText(persianEnglishDigit.E2P(digit));
                        input_digit_3.setBackgroundColor(Color.TRANSPARENT);
                    }
                    input_digit_4.setText("");
                    input_digit_4.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    input_digit_5.setText("");
                    input_digit_5.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    input_digit_6.setText("");
                    input_digit_6.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    vibrator.vibrate(20);
                    break;
                case 3:
                    if (digit.equalsIgnoreCase("d")) {
                        input_digit_4.setText("");
                        input_digit_4.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    } else {
                        input_digit_4.setText(persianEnglishDigit.E2P(digit));
                        input_digit_4.setBackgroundColor(Color.TRANSPARENT);
                    }
                    input_digit_5.setText("");
                    input_digit_5.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    input_digit_6.setText("");
                    input_digit_6.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    vibrator.vibrate(20);
                    break;
                case 4:
                    if (digit.equalsIgnoreCase("d")) {
                        input_digit_5.setText("");
                        input_digit_5.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    } else {
                        input_digit_5.setText(persianEnglishDigit.E2P(digit));
                        input_digit_5.setBackgroundColor(Color.TRANSPARENT);
                    }
                    input_digit_6.setText("");
                    input_digit_6.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    vibrator.vibrate(20);
                    break;
                case 5:
                    if (digit.equalsIgnoreCase("d")) {
                        input_digit_6.setText("");
                        input_digit_6.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                    } else {
                        input_digit_6.setText(persianEnglishDigit.E2P(digit));
                        input_digit_6.setBackgroundColor(Color.TRANSPARENT);
                    }
                    vibrator.vibrate(20);
                    break;
//                case 6:
//                    if (digit.equalsIgnoreCase("d")) {
//                        input_digit_6.setText("");
//                        input_digit_6.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
//                    } else {
//                        input_digit_6.setText(persianEnglishDigit.E2P(digit));
//                        input_digit_6.setBackgroundColor(Color.TRANSPARENT);
//                    }
//                    vibrator.vibrate(20);
//                    break;
            }

        }

        if (digit.contains("d")){
            if (inputPasswordValue.length() > 0) {
                inputPasswordValue = inputPasswordValue.substring(0, inputPasswordValue.length() - 1);
                if (inputPasswordValue.length() == 5){
                    input_digit_6.setText("");
                    input_digit_6.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                }
                if (inputPasswordValue.length() == 4){
                    input_digit_5.setText("");
                    input_digit_5.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                }
                else if (inputPasswordValue.length() == 3){
                    input_digit_4.setText("");
                    input_digit_4.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                }
                else if (inputPasswordValue.length() == 2){
                    input_digit_3.setText("");
                    input_digit_3.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                }
                else if (inputPasswordValue.length() == 1){
                    input_digit_2.setText("");
                    input_digit_2.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                }
                else if (inputPasswordValue.length() == 0){
                    input_digit_1.setText("");
                    input_digit_1.setBackgroundDrawable(getResources().getDrawable(R.drawable.remember_edittext_bg));
                }
            }
        }
        else {
            if (inputPasswordValue.length() <= 5) {
                inputPasswordValue += digit;
            }
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

        editor.putLong(Constants.MAX_BUSINESS_XFER_AMOUNT, this.userProfileDTO.getMaxBusinessXferAmount());
        editor.putLong(Constants.MIN_BUSINESS_XFER_AMOUNT, this.userProfileDTO.getMinBusinessXferAmount());
        editor.putLong(Constants.MAX_INDIVIDUAL_XFER_AMOUNT, this.userProfileDTO.getMaxIndividualXferAmount());
        editor.putLong(Constants.MIN_INDIVIDUAL_XFER_AMOUNT, this.userProfileDTO.getMinIndividualXferAmount());
        editor.commit();

        jalaliConvert = new JalaliConvert();

        user_name_text.setText(userProfileDTO.getFullName());
        MainActivity.user_account_name.setText(userProfileDTO.getFullName());

        user_account_no_text.setText(persianEnglishDigit.E2P(userProfileDTO.getCardDTO().getMaskedCardNumber()));
        user_bank_name.setText(" " + userProfileDTO.getCardDTO().getBankName());
        user_mobile_no.setText(persianEnglishDigit.E2P(userProfileDTO.getCellNumber()));
        if (userProfileDTO.getIbanDTO() != null) {
            user_iban_value.setText("IR" + persianEnglishDigit.E2P(userProfileDTO.getIbanDTO().getIban()));
            user_iban_bank.setText(userProfileDTO.getIbanDTO().getBankName());
        }
        user_national_code.setText(persianEnglishDigit.E2P(userProfileDTO.getCellNumber()));
        editor.commit();

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
                    break;
                case 1:
                    hampay_2_ll.setVisibility(View.VISIBLE);
                    hampay_2.setText(contactDTOs.get(1).getDisplayName());
                    if (contactDTOs.get(1).getContactImageId() != null) {
                        user_image_url = Constants.HTTPS_SERVER_IP + "/users/" + prefs.getString(Constants.LOGIN_TOKEN_ID, "") + "/" + contactDTOs.get(1).getContactImageId();
                        new RequestImageDownloader(context, new RequestImageDownloaderTaskCompleteListener(hampay_image_2)).execute(user_image_url);
                    }
                    break;
                case 2:
                    hampay_3_ll.setVisibility(View.VISIBLE);
                    hampay_3.setText(contactDTOs.get(2).getDisplayName());
                    if (contactDTOs.get(2).getContactImageId() != null) {
                        user_image_url = Constants.HTTPS_SERVER_IP + "/users/" + prefs.getString(Constants.LOGIN_TOKEN_ID, "") + "/" + contactDTOs.get(2).getContactImageId();
                        new RequestImageDownloader(context, new RequestImageDownloaderTaskCompleteListener(hampay_image_3)).execute(user_image_url);
                    }
                    break;
                case 3:
                    hampay_4_ll.setVisibility(View.VISIBLE);
                    hampay_4.setText(contactDTOs.get(3).getDisplayName());
                    if (contactDTOs.get(3).getContactImageId() != null) {
                        user_image_url = Constants.HTTPS_SERVER_IP + "/users/" + prefs.getString(Constants.LOGIN_TOKEN_ID, "") + "/" + contactDTOs.get(3).getContactImageId();
                        new RequestImageDownloader(context, new RequestImageDownloaderTaskCompleteListener(hampay_image_4)).execute(user_image_url);
                    }
                    break;

            }
        }

    }
}
