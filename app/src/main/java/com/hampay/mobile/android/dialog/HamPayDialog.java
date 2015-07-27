package com.hampay.mobile.android.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.ContactUsRequest;
import com.hampay.common.core.model.request.RegistrationEntryRequest;
import com.hampay.common.core.model.request.TACAcceptRequest;
import com.hampay.common.core.model.request.TACRequest;
import com.hampay.common.core.model.response.ContactUsResponse;
import com.hampay.common.core.model.response.TACAcceptResponse;
import com.hampay.common.core.model.response.dto.UserProfileDTO;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.activity.AppSliderActivity;
import com.hampay.mobile.android.activity.MainActivity;
import com.hampay.mobile.android.async.AsyncTaskCompleteListener;
import com.hampay.mobile.android.async.RequestLogin;
import com.hampay.mobile.android.async.RequestLogout;
import com.hampay.mobile.android.async.RequestRegistrationEntry;
import com.hampay.mobile.android.async.RequestTAC;
import com.hampay.mobile.android.async.RequestTACAccept;
import com.hampay.mobile.android.component.FacedTextView;
import com.hampay.mobile.android.model.LoginResponse;
import com.hampay.mobile.android.model.LogoutData;
import com.hampay.mobile.android.model.LogoutResponse;
import com.hampay.mobile.android.util.Constants;
import com.hampay.mobile.android.webservice.WebServices;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by amir on 7/8/15.
 */
public class HamPayDialog {

    Activity activity;
    Dialog dialog;

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
                dialog.dismiss();
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
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
                dialog.dismiss();
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
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
                dialog.dismiss();
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
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
                dialog.dismiss();

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
                dialog.dismiss();

                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contactUsPhone));
                activity.startActivity(intent);
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(false);

        dialog.show();
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

    public void showTACAcceptDialog(String accept_term) {

        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_tac_accept, null);

        FacedTextView tac_term = (FacedTextView) view.findViewById(R.id.tac_term);
        FacedTextView tac_accept = (FacedTextView) view.findViewById(R.id.tac_accept);
        FacedTextView tac_reject = (FacedTextView) view.findViewById(R.id.tac_reject);

        Pattern p = Pattern.compile(Constants.WEB_URL_REGEX);
        Matcher m = p.matcher(accept_term);
        while (m.find()) {
            final String urlStr = m.group();

            Spannable WordtoSpan = new SpannableString(accept_term);

            ClickableSpan privacySpan = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    if(urlStr.toLowerCase().contains("http://")) {
                        i.setData(Uri.parse(urlStr));
                    }else {
                        i.setData(Uri.parse("http://" + urlStr));
                    }
                    activity.startActivity(i);
                }
            };

            WordtoSpan.setSpan(privacySpan, accept_term.indexOf(urlStr), accept_term.indexOf(urlStr) + urlStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            tac_term.setText(WordtoSpan);

            tac_term.setMovementMethod(LinkMovementMethod.getInstance());

//            char[] stringArray1 = urlStr.toCharArray();
//
//            if (urlStr.startsWith("(") && urlStr.endsWith(")")) {
//
//                char[] stringArray = urlStr.toCharArray();
//
//                char[] newArray = new char[stringArray.length - 2];
//                System.arraycopy(stringArray, 1, newArray, 0, stringArray.length - 2);
//                urlStr = new String(newArray);
//                System.out.println("Finally Url =" + newArray.toString());
//
//            }
        }


        tacAcceptRequest = new TACAcceptRequest();

        tac_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
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
        view.setMinimumHeight((int) (displayRectangle.height() * 0.5f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(false);

        dialog.show();
    }

    private ResponseMessage<TACAcceptResponse> tACAcceptResponse;
    private UserProfileDTO userProfileDTO;

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
                intent.putExtra(Constants.USER_PROFILE_DTO, userProfileDTO);
                activity.startActivity(intent);

                activity.finish();

                if (dialog != null && dialog.isShowing()){
                    dialog.dismiss();
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


    public void showExitDialog(final LogoutData logoutData){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_exit_app, null);

        FacedTextView exit_app_yes = (FacedTextView) view.findViewById(R.id.exit_app_yes);
        FacedTextView exit_app_no = (FacedTextView) view.findViewById(R.id.exit_app_no);

        exit_app_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.finish();

                new RequestLogout(activity, new RequestLogoutResponseTaskCompleteListener()).execute(logoutData);

            }
        });

        exit_app_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(false);

        dialog.show();
    }

    public class RequestLogoutResponseTaskCompleteListener implements AsyncTaskCompleteListener<LogoutResponse>
    {
        public RequestLogoutResponseTaskCompleteListener(){
        }

        @Override
        public void onTaskComplete(LogoutResponse logoutResponse)
        {
            if (logoutResponse != null) {

            }
            else {

            }

        }

        @Override
        public void onTaskPreRun() {
        }
    }

    public void showRemovePasswordDialog(){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_remove_password, null);

        FacedTextView re_registeration = (FacedTextView) view.findViewById(R.id.re_registeration);
        FacedTextView cancel_remove_password = (FacedTextView) view.findViewById(R.id.cancel_remove_password);

        re_registeration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                Intent intent = new Intent(activity, AppSliderActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
                activity.finish();
            }
        });

        cancel_remove_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(false);

        dialog.show();
    }


    public void showExitRegistrationDialog(){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_exit_registration, null);

        FacedTextView exit_registration_yes = (FacedTextView) view.findViewById(R.id.exit_registration_yes);
        FacedTextView exit_registration_no = (FacedTextView) view.findViewById(R.id.exit_registration_no);

        exit_registration_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.setClass(activity, AppSliderActivity.class);
                activity.startActivity(intent);
                activity.finish();
            }
        });

        exit_registration_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(false);

        dialog.show();
    }

    public void showLoginFailDialog(){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_login_fail, null);

        FacedTextView login_retry = (FacedTextView) view.findViewById(R.id.login_retry);
        FacedTextView remove_password = (FacedTextView) view.findViewById(R.id.remove_password);

        login_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        remove_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showRemovePasswordDialog();
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(false);

        dialog.show();
    }


    public void showIncorrectSMSVerification(){

        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_incorrect_sms_verication, null);

        FacedTextView retry_sms_verification = (FacedTextView) view.findViewById(R.id.retry_sms_verification);

        retry_sms_verification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void showFailRegistrationEntryDialog(final RequestRegistrationEntry requestRegistrationEntry
            , final RegistrationEntryRequest registrationEntryRequest){

        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_registration_entry, null);

        FacedTextView retry_registration_entry = (FacedTextView) view.findViewById(R.id.retry_registration_entry);

        retry_registration_entry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                requestRegistrationEntry.execute(registrationEntryRequest);

//                dialog.dismiss();
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


}
