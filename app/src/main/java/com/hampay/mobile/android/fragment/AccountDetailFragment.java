package com.hampay.mobile.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.dto.ContactDTO;
import com.hampay.common.core.model.dto.UserVerificationStatus;
import com.hampay.common.core.model.request.VerifyAccountRequest;
import com.hampay.common.core.model.response.VerifyAccountResponse;
import com.hampay.common.core.model.response.dto.UserProfileDTO;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.activity.MainActivity;
import com.hampay.mobile.android.activity.PayOneActivity;
import com.hampay.mobile.android.activity.VerifyAccountActivity;
import com.hampay.mobile.android.component.FacedTextView;
import com.hampay.mobile.android.component.material.ButtonRectangle;
import com.hampay.mobile.android.util.Constants;
import com.hampay.mobile.android.util.JalaliConvert;
import com.hampay.mobile.android.webservice.WebServices;

import java.util.List;

/**
 * Created by amir on 6/5/15.
 */
public class AccountDetailFragment extends Fragment implements View.OnClickListener {

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
    ImageView hampay_image_2;
    ImageView hampay_image_3;
    ImageView hampay_image_4;
    LinearLayout hampay_1_ll;
    LinearLayout hampay_2_ll;
    LinearLayout hampay_3_ll;
    LinearLayout hampay_4_ll;

    RelativeLayout loading_rl;

    ButtonRectangle verify_account_button;

    UserProfileDTO userProfileDTO;

    Bundle bundle;

//    public AccountDetailFragment() {
//    }

//    public AccountDetailFragment(UserProfileDTO userProfileDTO) {
//        // Required empty public constructor
//        this.userProfileDTO = userProfileDTO;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bundle = getArguments();

        this.userProfileDTO = (UserProfileDTO)bundle.getSerializable(Constants.USER_PROFILE_DTO);

        new HttpUserProfile().execute();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account_detail, container, false);

        verification_status_ll = (LinearLayout)rootView.findViewById(R.id.verification_status_ll);

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

                VerifyAccountRequest verifyAccountRequest = new VerifyAccountRequest();

                loading_rl.setVisibility(View.VISIBLE);
                new HttpVerifyAccountResponse().execute(verifyAccountRequest);
            }
        });


        loading_rl = (RelativeLayout)rootView.findViewById(R.id.loading_rl);


        user_image = (ImageView)rootView.findViewById(R.id.user_image);
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
        hampay_image_2 = (ImageView)rootView.findViewById(R.id.hampay_image_2);
        hampay_image_3 = (ImageView)rootView.findViewById(R.id.hampay_image_3);
        hampay_image_4 = (ImageView)rootView.findViewById(R.id.hampay_image_4);

        // Inflate the layout for this fragment
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

        Intent intent;

        switch (v.getId()){
            case R.id.hampay_1_ll:
                intent = new Intent(getActivity(), PayOneActivity.class);
                intent.putExtra(Constants.CONTACT_PHONE_NO, userProfileDTO.getAccountNumber());
                intent.putExtra(Constants.CONTACT_NAME, userProfileDTO.getFullName());
                startActivity(intent);
                break;
            case R.id.hampay_2_ll:
                intent = new Intent(getActivity(), PayOneActivity.class);
                intent.putExtra(Constants.CONTACT_PHONE_NO, userProfileDTO.getAccountNumber());
                intent.putExtra(Constants.CONTACT_NAME, userProfileDTO.getFullName());
                startActivity(intent);
                break;
            case R.id.hampay_3_ll:
                intent = new Intent(getActivity(), PayOneActivity.class);
                intent.putExtra(Constants.CONTACT_PHONE_NO, userProfileDTO.getAccountNumber());
                intent.putExtra(Constants.CONTACT_NAME, userProfileDTO.getFullName());
                startActivity(intent);
                break;
            case R.id.hampay_4_ll:
                intent = new Intent(getActivity(), PayOneActivity.class);
                intent.putExtra(Constants.CONTACT_PHONE_NO, userProfileDTO.getAccountNumber());
                intent.putExtra(Constants.CONTACT_NAME, userProfileDTO.getFullName());
                startActivity(intent);
                break;
        }
    }


    public class HttpUserProfile extends AsyncTask<Void, Void, String> {

//        ResponseMessage<UserProfileResponse> userProfileResponse = null;

//        UserProfileDTO userProfileDTO;

        JalaliConvert jalaliConvert;

        @Override
        protected String doInBackground(Void... params) {

//            WebServices webServices = new WebServices();
            //webServices.testBankList1();
//            userProfileResponse = webServices.getUserProfile();

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (userProfileDTO != null) {


                if (userProfileDTO.getVerificationStatus() == UserVerificationStatus.UNVERIFIED) {
                    verification_status_ll.setVisibility(View.VISIBLE);
                }else {
                    verification_status_ll.setVisibility(View.GONE);
                }
                jalaliConvert = new JalaliConvert();

//                userProfileDTO = userProfileResponse.getService().getUserProfile();

                if (userProfileDTO.getVerificationStatus().ordinal() == 0){
                    user_image.setImageResource(R.drawable.user_icon_blak);
                }else {
                    user_image.setImageResource(R.drawable.user_icon_blue);
                }
                user_name_text.setText(userProfileDTO.getFullName());
                MainActivity.user_account_name.setText(userProfileDTO.getFullName());
                user_account_no_text.setText(getString(R.string.account_no) + ": " + userProfileDTO.getAccountNumber());
                user_bank_name.setText(userProfileDTO.getBankName());
                user_mobile_no.setText(getString(R.string.mobile_no) + ": " + userProfileDTO.getCellNumber());

                user_account_title.setText(getString(R.string.account_type));

                if (userProfileDTO.getVerificationStatus() == UserVerificationStatus.DELEGATED) {
                    user_account_type.setText(": " + "عادی");
                }else {
                    user_account_type.setText(": " + "محدود");
                }

                if (userProfileDTO.getLastLoginDate() != null) {
                    user_last_login.setText(getString(R.string.last_login) + ": "
                            + jalaliConvert.GregorianToPersian(userProfileDTO.getLastLoginDate()));
                }else {
                    user_last_login.setText("");
                }


                List<ContactDTO> contactDTOs = userProfileDTO.getSelectedContacts();


                for (int contact = 0; contact < contactDTOs.size(); contact++){
                    switch (contact){
                        case 0:
                            hampay_1_ll.setVisibility(View.VISIBLE);
                            hampay_1.setText(contactDTOs.get(0).getDisplayName());
//                            if (contactDTOs.get(0).getUserVerificationStatus() == UserVerificationStatus.VERIFIED){
                                hampay_image_1.setImageResource(R.drawable.user_icon_blue_s);
//                            }else {
//                                hampay_image_1.setImageResource(R.drawable.user_icon_blak_s);
//                            }
                            break;
                        case 1:
                            hampay_2_ll.setVisibility(View.VISIBLE);
                            hampay_2.setText(contactDTOs.get(1).getDisplayName());
//                            if (contactDTOs.get(1).getUserVerificationStatus() == UserVerificationStatus.VERIFIED){
                                hampay_image_2.setImageResource(R.drawable.user_icon_blue_s);
//                            }else {
//                                hampay_image_2.setImageResource(R.drawable.user_icon_blak_s);
//                            }
                            break;
                        case 2:
                            hampay_3_ll.setVisibility(View.VISIBLE);
                            hampay_3.setText(contactDTOs.get(2).getDisplayName());
//                            if (contactDTOs.get(2).getUserVerificationStatus() == UserVerificationStatus.VERIFIED){
                                hampay_image_3.setImageResource(R.drawable.user_icon_blue_s);
//                            }else {
//                                hampay_image_3.setImageResource(R.drawable.user_icon_blak_s);
//                            }
                            break;
                        case 3:
                            hampay_4_ll.setVisibility(View.VISIBLE);
                            hampay_4.setText(contactDTOs.get(3).getDisplayName());
//                            if (contactDTOs.get(3).getUserVerificationStatus() == UserVerificationStatus.VERIFIED){
                                hampay_image_4.setImageResource(R.drawable.user_icon_blue_s);
//                            }else {
//                                hampay_image_4.setImageResource(R.drawable.user_icon_blak_s);
//                            }
                            break;

                    }
                }

                loading_rl.setVisibility(View.GONE);

            }
        }
    }


    private ResponseMessage<VerifyAccountResponse> verifyAccountResponse;

    public class HttpVerifyAccountResponse extends AsyncTask<VerifyAccountRequest, Void, String> {

        @Override
        protected String doInBackground(VerifyAccountRequest... params) {

            WebServices webServices = new WebServices(getActivity());
            verifyAccountResponse = webServices.verifyAccountResponse(params[0]);

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            loading_rl.setVisibility(View.GONE);

            if (isAdded()) {

                if (verifyAccountResponse != null) {

                    Intent intent = new Intent();
                    intent.setClass(getActivity(), VerifyAccountActivity.class);
                    intent.putExtra(Constants.TRANSFER_MONEY_COMMENT, verifyAccountResponse.getService().getTransferMoneyComment());
                    startActivity(intent);

//                verification_response_text.setText(
//                        verifyAccountResponse.getService().getTransferMoneyComment());
                }
            }
        }
    }
}
