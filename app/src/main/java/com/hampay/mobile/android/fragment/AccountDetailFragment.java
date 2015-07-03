package com.hampay.mobile.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.dto.ContactDTO;
import com.hampay.common.core.model.response.UserProfileResponse;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.activity.MainActivity;
import com.hampay.mobile.android.activity.RegVerifyAccountNoActivity;
import com.hampay.mobile.android.activity.VerifyAccountActivity;
import com.hampay.mobile.android.component.FacedTextView;
import com.hampay.mobile.android.webservice.WebServices;

import java.util.List;

/**
 * Created by amir on 6/5/15.
 */
public class AccountDetailFragment extends Fragment {

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
    RelativeLayout loading_rl;

    CardView verify_account_CardView;

    public AccountDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new HttpUserProfile().execute();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account_detail, container, false);


        verify_account_CardView = (CardView)rootView.findViewById(R.id.verify_account_CardView);
        verify_account_CardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), VerifyAccountActivity.class);
                startActivity(intent);
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


    public class HttpUserProfile extends AsyncTask<Void, Void, String> {

        ResponseMessage<UserProfileResponse> userProfileResponse = null;

        @Override
        protected String doInBackground(Void... params) {

            WebServices webServices = new WebServices();
            //webServices.testBankList1();
            userProfileResponse = webServices.getUserProfile();

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (userProfileResponse != null) {

                if (userProfileResponse.getService().getUserProfile().getVerificationStatus().ordinal() == 0){
                    user_image.setImageResource(R.drawable.user_icon_blak);
                }else {
                    user_image.setImageResource(R.drawable.user_icon_blue);
                }
                user_name_text.setText(userProfileResponse.getService().getUserProfile().getFullName());
                MainActivity.user_account_name.setText(userProfileResponse.getService().getUserProfile().getFullName());
                user_account_no_text.setText(getString(R.string.account_no) + ": " + userProfileResponse.getService().getUserProfile().getAccountNumber());
                user_bank_name.setText(userProfileResponse.getService().getUserProfile().getBankName());
                user_mobile_no.setText(getString(R.string.mobile_no) + ": " + userProfileResponse.getService().getUserProfile().getCellNumber());

                user_account_title.setText(getString(R.string.account_type));

                if (userProfileResponse.getService().getUserProfile().getVerificationStatus().name().equalsIgnoreCase("UNVERIFIED")) {
                    user_account_type.setText(": " + "محدود");
                }else {
                    user_account_type.setText(": " + "عادی");
                }

                user_last_login.setText(getString(R.string.last_login) + userProfileResponse.getService().getUserProfile().getLastLoginDate());



                List<ContactDTO> contactDTOs = userProfileResponse.getService().getUserProfile().getSelectedContacts();

                hampay_1.setText(contactDTOs.get(0).getDisplayName());
                hampay_2.setText(contactDTOs.get(1).getDisplayName());
                hampay_3.setText(contactDTOs.get(2).getDisplayName());
                hampay_4.setText(contactDTOs.get(3).getDisplayName());

                if (contactDTOs.get(0).getUserVerificationStatus().ordinal() == 0){
                    hampay_image_1.setImageResource(R.drawable.user_icon_blak_s);
                }else {
                    hampay_image_1.setImageResource(R.drawable.user_icon_blue_s);
                }

                if (contactDTOs.get(1).getUserVerificationStatus().ordinal() == 0){
                    hampay_image_2.setImageResource(R.drawable.user_icon_blak_s);
                }else {
                    hampay_image_2.setImageResource(R.drawable.user_icon_blue_s);
                }

                if (contactDTOs.get(2).getUserVerificationStatus().ordinal() == 0){
                    hampay_image_3.setImageResource(R.drawable.user_icon_blak_s);
                }else {
                    hampay_image_3.setImageResource(R.drawable.user_icon_blue_s);
                }

                if (contactDTOs.get(3).getUserVerificationStatus().ordinal() == 0){
                    hampay_image_4.setImageResource(R.drawable.user_icon_blak_s);
                }else {
                    hampay_image_4.setImageResource(R.drawable.user_icon_blue_s);
                }



                loading_rl.setVisibility(View.GONE);

            }
        }
    }
}
