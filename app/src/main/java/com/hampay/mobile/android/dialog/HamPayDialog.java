package com.hampay.mobile.android.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.ContactUsRequest;
import com.hampay.common.core.model.request.TACAcceptRequest;
import com.hampay.common.core.model.response.ContactUsResponse;
import com.hampay.common.core.model.response.TACAcceptResponse;
import com.hampay.common.core.model.response.TACResponse;
import com.hampay.common.core.model.response.dto.UserProfileDTO;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.activity.HamPayLoginActivity;
import com.hampay.mobile.android.activity.MainActivity;
import com.hampay.mobile.android.async.AsyncTaskCompleteListener;
import com.hampay.mobile.android.async.RequestTAC;
import com.hampay.mobile.android.async.RequestTACAccept;
import com.hampay.mobile.android.component.FacedTextView;
import com.hampay.mobile.android.serialize.UserProfile;
import com.hampay.mobile.android.util.Constants;
import com.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/8/15.
 */
public class HamPayDialog {

    Activity activity;
    Dialog contactUsDialog;

    public HamPayDialog(Activity activity){

        this.activity = activity;

    }


    public void showDisMatchPasswordDialog(){

        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_dismatch_password, null);

        FacedTextView retry_password = (FacedTextView) view.findViewById(R.id.retry_password);

        retry_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactUsDialog.dismiss();
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        contactUsDialog = new Dialog(activity);
        contactUsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        contactUsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        contactUsDialog.setContentView(view);
        contactUsDialog.setTitle(null);
        contactUsDialog.setCanceledOnTouchOutside(false);
        contactUsDialog.show();
    }

    public void showNoResultSearchDialog(){

        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_no_result, null);

        FacedTextView retry_search = (FacedTextView) view.findViewById(R.id.retry_search);

        retry_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactUsDialog.dismiss();
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        contactUsDialog = new Dialog(activity);
        contactUsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        contactUsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        contactUsDialog.setContentView(view);
        contactUsDialog.setTitle(null);
        contactUsDialog.setCanceledOnTouchOutside(false);
        contactUsDialog.show();
    }


    public void showIncorrectPrice(){

        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_incorrect, null);

        FacedTextView retry_price = (FacedTextView) view.findViewById(R.id.retry_price);

        retry_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactUsDialog.dismiss();
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        contactUsDialog = new Dialog(activity);
        contactUsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        contactUsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        contactUsDialog.setContentView(view);
        contactUsDialog.setTitle(null);
        contactUsDialog.setCanceledOnTouchOutside(false);
        contactUsDialog.show();
    }



    public void showContactUsDialog(){

        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_contact_us, null);

        FacedTextView send_message = (FacedTextView) view.findViewById(R.id.send_message);
        FacedTextView call_message = (FacedTextView) view.findViewById(R.id.call_message);

        ContactUsRequest contactUsRequest = new ContactUsRequest();
        contactUsRequest.setRequestUUID("");
        new HttpContactUs().execute(contactUsRequest);

        send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactUsDialog.dismiss();

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", contactUsMail, null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.app_name));
                emailIntent.putExtra(Intent.EXTRA_TEXT, activity.getString(R.string.insert_message));
                activity.startActivity(Intent.createChooser(emailIntent, activity.getString(R.string.hampay_contact)));
            }
        });

        call_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactUsDialog.dismiss();

                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contactUsPhone));
                activity.startActivity(intent);
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        contactUsDialog = new Dialog(activity);
        contactUsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        contactUsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        contactUsDialog.setContentView(view);
        contactUsDialog.setTitle(null);
        contactUsDialog.setCanceledOnTouchOutside(false);

        contactUsDialog.show();
    }


    String contactUsMail = "";
    String contactUsPhone = "";
    ResponseMessage<ContactUsResponse> contactUsResponseResponseMessage = null;

    public class HttpContactUs extends AsyncTask<ContactUsRequest, Void, String> {

        @Override
        protected String doInBackground(ContactUsRequest... params) {

            WebServices webServices = new WebServices();
            contactUsResponseResponseMessage = webServices.contactUsResponse(params[0]);

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (contactUsResponseResponseMessage.getService() != null){
                contactUsMail = contactUsResponseResponseMessage.getService().getEmailAddress();
                contactUsPhone = contactUsResponseResponseMessage.getService().getPhoneNumber();
            }


        }
    }

    TACAcceptRequest tacAcceptRequest;

    public void showTACAcceptDialog(String accept_term){

        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_tac_accept, null);

        FacedTextView tac_term = (FacedTextView)view.findViewById(R.id.tac_term);
        FacedTextView tac_accept = (FacedTextView) view.findViewById(R.id.tac_accept);
        FacedTextView tac_reject = (FacedTextView) view.findViewById(R.id.tac_reject);

        tac_term.setText(accept_term);

        tacAcceptRequest = new TACAcceptRequest();

        tac_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactUsDialog.dismiss();
                activity.finish();
            }
        });

        tac_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RequestTACAccept(activity, new RequestTACAcceptResponseTaskCompleteListener()).execute(tacAcceptRequest);
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        contactUsDialog = new Dialog(activity);
        contactUsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        contactUsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        contactUsDialog.setContentView(view);
        contactUsDialog.setTitle(null);
        contactUsDialog.setCanceledOnTouchOutside(false);

        contactUsDialog.show();
    }

    private ResponseMessage<TACAcceptResponse> tACAcceptResponse;
    public static UserProfileDTO userProfileDTO;

    public class RequestTACAcceptResponseTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<TACAcceptResponse>>
    {
        public RequestTACAcceptResponseTaskCompleteListener(){
        }

        @Override
        public void onTaskComplete(ResponseMessage<TACAcceptResponse> tacAcceptResponseMessage)
        {
            if (tacAcceptResponseMessage.getService().getResultStatus() != null) {

                userProfileDTO = tacAcceptResponseMessage.getService().getUserProfile();

                Intent intent = new Intent();
                intent.setClass(activity, MainActivity.class);
                activity.startActivity(intent);

                activity.finish();

                if (contactUsDialog != null && contactUsDialog.isShowing()){
                    contactUsDialog.dismiss();
                }

//                Intent intent = new Intent(activity, MainActivity.class);
//                intent.putExtra("userProfileDTO", userProfileDTO);
//                activity.startActivity(intent);

//                if (tacAcceptResponseMessage.getService().getShouldAcceptTAC()){
//
//                    (new HamPayDialog(activity)).showTACAcceptDialog(tacAcceptResponseMessage.getService().getTac());
//
//                }

            }
            else {
                Toast.makeText(activity, activity.getString(R.string.no_network), Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void onTaskPreRun() {
//            loading_rl.setVisibility(View.VISIBLE);
        }
    }


}
