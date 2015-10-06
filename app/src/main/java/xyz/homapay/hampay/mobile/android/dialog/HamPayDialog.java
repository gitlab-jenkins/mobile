package xyz.homapay.hampay.mobile.android.dialog;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.common.response.ResultStatus;
import com.hampay.common.core.model.request.BankListRequest;
import com.hampay.common.core.model.request.BusinessListRequest;
import com.hampay.common.core.model.request.BusinessPaymentRequest;
import com.hampay.common.core.model.request.BusinessSearchRequest;
import com.hampay.common.core.model.request.ChangeMemorableWordRequest;
import com.hampay.common.core.model.request.ChangePassCodeRequest;
import com.hampay.common.core.model.request.ContactUsRequest;
import com.hampay.common.core.model.request.ContactsHampayEnabledRequest;
import com.hampay.common.core.model.request.GetUserIdTokenRequest;
import com.hampay.common.core.model.request.IndividualPaymentRequest;
import com.hampay.common.core.model.request.MobileRegistrationIdEntryRequest;
import com.hampay.common.core.model.request.RegistrationConfirmUserDataRequest;
import com.hampay.common.core.model.request.RegistrationCredentialsRequest;
import com.hampay.common.core.model.request.RegistrationEntryRequest;
import com.hampay.common.core.model.request.RegistrationFetchUserDataRequest;
import com.hampay.common.core.model.request.RegistrationSendSmsTokenRequest;
import com.hampay.common.core.model.request.RegistrationVerifyAccountRequest;
import com.hampay.common.core.model.request.RegistrationVerifyMobileRequest;
import com.hampay.common.core.model.request.RegistrationVerifyTransferMoneyRequest;
import com.hampay.common.core.model.request.TACAcceptRequest;
import com.hampay.common.core.model.request.TACRequest;
import com.hampay.common.core.model.request.TransactionListRequest;
import com.hampay.common.core.model.request.UnlinkUserRequest;
import com.hampay.common.core.model.request.UserProfileRequest;
import com.hampay.common.core.model.request.VerifyAccountRequest;
import com.hampay.common.core.model.request.VerifyTransferMoneyRequest;
import com.hampay.common.core.model.response.BusinessPaymentConfirmResponse;
import com.hampay.common.core.model.response.BusinessPaymentResponse;
import com.hampay.common.core.model.response.ContactUsResponse;
import com.hampay.common.core.model.response.IndividualPaymentConfirmResponse;
import com.hampay.common.core.model.response.IndividualPaymentResponse;
import com.hampay.common.core.model.response.TACAcceptResponse;
import com.hampay.common.core.model.response.dto.UserProfileDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.Helper.DatabaseHelper;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.activity.AppSliderActivity;
import xyz.homapay.hampay.mobile.android.activity.ChangeMemorableActivity;
import xyz.homapay.hampay.mobile.android.activity.GuideDetailActivity;
import xyz.homapay.hampay.mobile.android.activity.HamPayLoginActivity;
import xyz.homapay.hampay.mobile.android.activity.MainActivity;
import xyz.homapay.hampay.mobile.android.activity.StartActivity;
import xyz.homapay.hampay.mobile.android.activity.UnlinkPassActivity;
import xyz.homapay.hampay.mobile.android.analytics.GaAnalyticsEvent;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestBankList;
import xyz.homapay.hampay.mobile.android.async.RequestBusinessPayment;
import xyz.homapay.hampay.mobile.android.async.RequestChangeMemorableWord;
import xyz.homapay.hampay.mobile.android.async.RequestChangePassCode;
import xyz.homapay.hampay.mobile.android.async.RequestConfirmUserData;
import xyz.homapay.hampay.mobile.android.async.RequestContactHampayEnabled;
import xyz.homapay.hampay.mobile.android.async.RequestCredentialEntry;
import xyz.homapay.hampay.mobile.android.async.RequestFetchUserData;
import xyz.homapay.hampay.mobile.android.async.RequestHamPayBusiness;
import xyz.homapay.hampay.mobile.android.async.RequestIndividualPayment;
import xyz.homapay.hampay.mobile.android.async.RequestLogout;
import xyz.homapay.hampay.mobile.android.async.RequestMobileRegistrationIdEntry;
import xyz.homapay.hampay.mobile.android.async.RequestRegisterVerifyAccount;
import xyz.homapay.hampay.mobile.android.async.RequestRegistrationEntry;
import xyz.homapay.hampay.mobile.android.async.RequestRegistrationSendSmsToken;
import xyz.homapay.hampay.mobile.android.async.RequestRegistrationVerifyTransferMoney;
import xyz.homapay.hampay.mobile.android.async.RequestSearchHamPayBusiness;
import xyz.homapay.hampay.mobile.android.async.RequestTAC;
import xyz.homapay.hampay.mobile.android.async.RequestTACAccept;
import xyz.homapay.hampay.mobile.android.async.RequestUnlinkUser;
import xyz.homapay.hampay.mobile.android.async.RequestUserIdToken;
import xyz.homapay.hampay.mobile.android.async.RequestUserProfile;
import xyz.homapay.hampay.mobile.android.async.RequestUserTransaction;
import xyz.homapay.hampay.mobile.android.async.RequestVerifyAccount;
import xyz.homapay.hampay.mobile.android.async.RequestVerifyMobile;
import xyz.homapay.hampay.mobile.android.async.RequestVerifyTransferMoney;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.model.FailedLoginResponse;
import xyz.homapay.hampay.mobile.android.model.LogoutData;
import xyz.homapay.hampay.mobile.android.model.LogoutResponse;
import xyz.homapay.hampay.mobile.android.model.RecentPay;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;

import java.text.NumberFormat;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by amir on 7/8/15.
 */
public class HamPayDialog {

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    Activity activity;
    Dialog dialog;

    DatabaseHelper dbHelper;

    Tracker hamPayGaTracker;

    GaAnalyticsEvent gaAnalyticsEvent;

    String serverKey = "";

    public HamPayDialog(Activity activity){

        this.activity = activity;

        prefs = activity.getSharedPreferences(Constants.APP_PREFERENCE_NAME, activity.MODE_PRIVATE);
        editor = activity.getSharedPreferences(Constants.APP_PREFERENCE_NAME, activity.MODE_PRIVATE).edit();


        gaAnalyticsEvent = new GaAnalyticsEvent(activity);

        hamPayGaTracker = ((HamPayApplication) this.activity.getApplicationContext())
                .getTracker(HamPayApplication.TrackerName.APP_TRACKER);

    }

    public HamPayDialog(Activity activity, String serverKey){

        this.activity = activity;
        this.serverKey = serverKey;

        prefs = activity.getSharedPreferences(Constants.APP_PREFERENCE_NAME, activity.MODE_PRIVATE);
        editor = activity.getSharedPreferences(Constants.APP_PREFERENCE_NAME, activity.MODE_PRIVATE).edit();

        gaAnalyticsEvent = new GaAnalyticsEvent(activity);

        dbHelper = new DatabaseHelper(activity, serverKey);

        hamPayGaTracker = ((HamPayApplication) this.activity.getApplicationContext())
                .getTracker(HamPayApplication.TrackerName.APP_TRACKER);

    }


    public void showWaitingdDialog(String hampayUser){

        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_waiting, null);

        FacedTextView wating_text = (FacedTextView)view.findViewById(R.id.wating_text);

        if (hampayUser.length() != 0){
            wating_text.setText(activity.getString(R.string.dialog_hampay_user_waiting, hampayUser));
        }


        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void showWaitingdSMSDialog(final RequestRegistrationSendSmsToken requestRegistrationSendSmsToken, String hampayUser){

        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_waiting, null);

        FacedTextView wating_text = (FacedTextView)view.findViewById(R.id.wating_text);

        if (hampayUser.length() != 0){
            wating_text.setText(activity.getString(R.string.dialog_hampay_user_waiting, hampayUser));
        }


        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
//                Toast.makeText(activity, "ksjdks", Toast.LENGTH_LONG).show();
                requestRegistrationSendSmsToken.cancel(true);
            }
        });
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void dismisWaitingDialog(){

        if (dialog != null){
            if (dialog.isShowing())
                dialog.dismiss();
        }
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
        dialog.setCancelable(false);
        dialog.show();
    }

    public void showDisMatchMemorableDialog(){

        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_dismatch_memorable, null);

        FacedTextView retry_memorable = (FacedTextView) view.findViewById(R.id.retry_memorable);

        retry_memorable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.finish();
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    public void showCommunicateDialog(final int type, final String cellNumber){

        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_communicate_confirm, null);

        FacedTextView communicate_confirm = (FacedTextView) view.findViewById(R.id.communicate_confirm);
        FacedTextView communicate_disconfirm = (FacedTextView) view.findViewById(R.id.communicate_disconfirm);

        communicate_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if (type == 0){
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", cellNumber, null)));
                }else if (type == 1){
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("tel", cellNumber, null)));
                }
            }
        });

        communicate_disconfirm.setOnClickListener(new View.OnClickListener() {
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
        dialog.setCanceledOnTouchOutside(true);

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
        dialog.setCanceledOnTouchOutside(true);
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
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


    public void fetchContactUsInfo(){
        ContactUsRequest contactUsRequest = new ContactUsRequest();
        contactUsRequest.setRequestUUID(UUID.randomUUID().toString());
        new HttpContactUs().execute(contactUsRequest);
    }

    public void showContactUsDialog(){

        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_contact_us, null);

        FacedTextView send_message = (FacedTextView) view.findViewById(R.id.send_message);
        FacedTextView call_message = (FacedTextView) view.findViewById(R.id.call_message);

        send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if (contactUsMail.length() != 0) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", contactUsMail, null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.app_name));
                    emailIntent.putExtra(Intent.EXTRA_TEXT, activity.getString(R.string.insert_message));
                    activity.startActivity(Intent.createChooser(emailIntent, activity.getString(R.string.hampay_contact)));
                }
            }
        });

        call_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if (contactUsPhone.length() != 0) {

                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contactUsPhone));
                    activity.startActivity(intent);
                }
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);

        dialog.show();
    }


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void showHelpDialog(final String help_url){

        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_help, null);

        final FacedTextView contact_us = (FacedTextView) view.findViewById(R.id.contact_us);
        FacedTextView user_help = (FacedTextView) view.findViewById(R.id.user_help);

        contact_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ContactUsRequest contactUsRequest = new ContactUsRequest();
                contactUsRequest.setRequestUUID(UUID.randomUUID().toString());
                new HttpContactUs().execute(contactUsRequest);
            }
        });

        user_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.setClass(activity, GuideDetailActivity.class);
                intent.putExtra(Constants.WEB_PAGE_ADDRESS, help_url);
                activity.startActivity(intent);
            }
        });

        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setDimAmount(0);
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.gravity = Gravity.TOP | Gravity.RIGHT;
        layoutParams.x = 25;
        layoutParams.y = 20;

        dialog.show();
    }


    String contactUsMail = "";
    String contactUsPhone = "";
    ResponseMessage<ContactUsResponse> contactUsResponseResponseMessage = null;

    public class HttpContactUs extends AsyncTask<ContactUsRequest, Void, String> {

        @Override
        protected String doInBackground(ContactUsRequest... params) {

            WebServices webServices = new WebServices(activity);
            contactUsResponseResponseMessage = webServices.newContactUsResponse(params[0]);

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showWaitingdDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            dismisWaitingDialog();
            if (contactUsResponseResponseMessage != null){
                contactUsMail = contactUsResponseResponseMessage.getService().getEmailAddress();
                contactUsPhone = contactUsResponseResponseMessage.getService().getPhoneNumber();
                showContactUsDialog();
            }else {
                Toast.makeText(activity, activity.getString(R.string.hampay_contact_failed), Toast.LENGTH_LONG).show();
            }


        }
    }

    TACAcceptRequest tacAcceptRequest;
    RequestTACAccept requestTACAccept;

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
                dialog.dismiss();
                showWaitingdDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
                requestTACAccept = new RequestTACAccept(activity, new RequestTACAcceptResponseTaskCompleteListener());
                requestTACAccept.execute(tacAcceptRequest);
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        view.setMinimumHeight((int) (displayRectangle.height() * 0.5f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);

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

            dismisWaitingDialog();

            if (tacAcceptResponseMessage != null) {

                if (tacAcceptResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {

                    editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                    editor.commit();

                    userProfileDTO = tacAcceptResponseMessage.getService().getUserProfile();

                    Intent intent = new Intent();
                    intent.setClass(activity, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(Constants.USER_PROFILE_DTO, userProfileDTO);
                    editor.putBoolean(Constants.FORCE_USER_PROFILE, true);
                    editor.commit();
                    activity.finish();
                    activity.startActivity(intent);

                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("TAC Accept")
                            .setAction("Accept")
                            .setLabel("Success")
                            .build());

                }else {
                    requestTACAccept = new RequestTACAccept(activity, new RequestTACAcceptResponseTaskCompleteListener());
                    showFailTACAcceeptRequestDialog(requestTACAccept, tacAcceptRequest,
                            tacAcceptResponseMessage.getService().getResultStatus().getCode(),
                            tacAcceptResponseMessage.getService().getResultStatus().getDescription());

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("TAC Accept")
                            .setAction("Accept")
                            .setLabel("Fail(Server)")
                            .build());
                }
            }
            else {
                showFailTACAcceeptRequestDialog(requestTACAccept, tacAcceptRequest,
                        Constants.LOCAL_ERROR_CODE,
                        activity.getString(R.string.msg_fail_tac_accept_request));

                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("TAC Accept")
                        .setAction("Accept")
                        .setLabel("Fail(Mobile)")
                        .build());
            }

        }

        @Override
        public void onTaskPreRun() { }
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

                Intent intent = new Intent();
                intent.setClass(activity, HamPayLoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(intent);

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
        dialog.setCanceledOnTouchOutside(true);

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

                editor.clear().commit();
                editor.commit();

                dialog.dismiss();
                Intent intent = new Intent(activity, AppSliderActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.finish();
                activity.startActivity(intent);
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
        dialog.setCanceledOnTouchOutside(true);

        dialog.show();
    }

    public void showBackStartRegisterDialog(){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_back_start, null);

        FacedTextView re_registeration = (FacedTextView) view.findViewById(R.id.re_registeration);
        FacedTextView cancel_re_registeration = (FacedTextView) view.findViewById(R.id.cancel_re_registeration);

        re_registeration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor.clear().commit();
                editor.commit();

                dialog.dismiss();
                Intent intent = new Intent(activity, AppSliderActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.finish();
                activity.startActivity(intent);
            }
        });

        cancel_re_registeration.setOnClickListener(new View.OnClickListener() {
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
        dialog.setCanceledOnTouchOutside(true);

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
                activity.finish();

                gaAnalyticsEvent.GaTrackMobileEvent("User Exit HamPay", "Exit", activity.getLocalClassName());
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
        dialog.setCanceledOnTouchOutside(true);

        dialog.show();
    }

    public void showExitLoginDialog(){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_exit_registration, null);

        FacedTextView exit_text = (FacedTextView)view.findViewById(R.id.exit_text);
        FacedTextView exit_registration_yes = (FacedTextView) view.findViewById(R.id.exit_registration_yes);
        FacedTextView exit_registration_no = (FacedTextView) view.findViewById(R.id.exit_registration_no);

        exit_text.setText(activity.getString(R.string.exit_app_text));

        exit_registration_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.finish();

                gaAnalyticsEvent.GaTrackMobileEvent("User Exit HamPay", "Exit", activity.getLocalClassName());
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
        dialog.setCanceledOnTouchOutside(true);

        dialog.show();
    }

    public void showLoginFailDialog(FailedLoginResponse failedLoginResponse){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_login_fail, null);

        FacedTextView failedLoginText = (FacedTextView)view.findViewById(R.id.failedLoginText);

        failedLoginText.setText("کد خطای: " + new PersianEnglishDigit(failedLoginResponse.getCode()).E2P()
                        + "\n"
                        + failedLoginResponse.getMessage()
        );


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
        dialog.setCanceledOnTouchOutside(true);

        dialog.show();
    }


    public void showResumeRegisterationDialog(final Activity destinationActivity){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_resume_registeration, null);

        FacedTextView confrim_resume_register = (FacedTextView)view.findViewById(R.id.confrim_resume_register);
        FacedTextView disconfrim_resume_register = (FacedTextView)view.findViewById(R.id.disconfrim_resume_register);


        confrim_resume_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.setClass(activity, destinationActivity.getClass());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.finish();
                activity.startActivity(intent);
            }
        });

        disconfrim_resume_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.setClass(activity, StartActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.finish();
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
        dialog.setCancelable(false);

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
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }



    RequestIndividualPayment requestIndividualPayment;
    IndividualPaymentRequest individualPaymentRequest;

    public void individualPaymentConfirmDialog(final IndividualPaymentConfirmResponse individualPaymentConfirmResponse,
                                               final Long amountValue,
                                               final String userMessage){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_pay_one, null);
        FacedTextView pay_one_confirm = (FacedTextView) view.findViewById(R.id.pay_one_confirm);
        FacedTextView confirmation = (FacedTextView) view.findViewById(R.id.confirmation);
        FacedTextView dis_confirmation = (FacedTextView) view.findViewById(R.id.dis_confirmation);

        confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showWaitingdDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
                individualPaymentRequest = new IndividualPaymentRequest();
                individualPaymentRequest.setAmount(amountValue);
                individualPaymentRequest.setCellNumber(individualPaymentConfirmResponse.getCellNumber());
                individualPaymentRequest.setMessage(userMessage);
                requestIndividualPayment = new RequestIndividualPayment(activity,
                        new RequestIndividualPaymentTaskCompleteListener(individualPaymentRequest, individualPaymentConfirmResponse.getFullName()));
                requestIndividualPayment.execute(individualPaymentRequest);
            }
        });


        dis_confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        pay_one_confirm.setText(activity.getString(R.string.pay_one_confirm,
                (new PersianEnglishDigit()).E2P(String.format("%,d", amountValue).replace(".", ",")),
//                amountValue.toString(),
                individualPaymentConfirmResponse.getFullName(),
                individualPaymentConfirmResponse.getBankName()));

        view.setMinimumWidth((int) (displayRectangle.width() * 0.8f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);

        dialog.show();
    }

    public void individualPaymentDialog(final IndividualPaymentResponse individualPaymentResponse,
                                        final IndividualPaymentRequest individualPaymentRequest,
                                        final String contactName){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_pay_one_ref, null);

        FacedTextView pay_one_confirm_ref = (FacedTextView) view.findViewById(R.id.pay_one_confirm_ref);
        FacedTextView confirmation = (FacedTextView) view.findViewById(R.id.confirmation);


        confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.setResult(1024);
                activity.finish();
                activity.onBackPressed();
            }
        });

        pay_one_confirm_ref.setText((new PersianEnglishDigit(activity.getString(R.string.pay_one_ref, individualPaymentResponse.getRefCode()))).E2P());

        RecentPay recentPay = new RecentPay();

        if (!dbHelper.getExistRecentPay(individualPaymentRequest.getCellNumber())) {

            recentPay = new RecentPay();
            recentPay.setName(contactName);
            recentPay.setPhone(individualPaymentRequest.getCellNumber());
            recentPay.setMessage(individualPaymentRequest.getMessage());
            dbHelper.createRecentPay(recentPay);

        }else {
            recentPay = new RecentPay();
            recentPay = dbHelper.getRecentPay(individualPaymentRequest.getCellNumber());

            recentPay.setMessage(individualPaymentRequest.getMessage());
            dbHelper.updateRecentPay(recentPay);
        }

        view.setMinimumWidth((int) (displayRectangle.width() * 0.8f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);

        dialog.show();
    }


    public class RequestIndividualPaymentTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<IndividualPaymentResponse>> {

        String contactName;
        IndividualPaymentRequest individualPaymentRequest;

        public RequestIndividualPaymentTaskCompleteListener(IndividualPaymentRequest individualPaymentRequest, String contactName){
            this.contactName = contactName;
            this.individualPaymentRequest = individualPaymentRequest;
        }

        @Override
        public void onTaskComplete(ResponseMessage<IndividualPaymentResponse> individualPaymentResponseMessage) {

            dismisWaitingDialog();
            if (individualPaymentResponseMessage != null){
                if (individualPaymentResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS){

                    individualPaymentDialog(individualPaymentResponseMessage.getService(),
                            individualPaymentRequest,
                            contactName);

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Individual Payment")
                            .setAction("Payment")
                            .setLabel("Success")
                            .build());

                }else {
                    showFailPaymentDialog(individualPaymentResponseMessage.getService().getResultStatus().getCode(),
                            individualPaymentResponseMessage.getService().getResultStatus().getDescription());

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Individual Payment")
                            .setAction("Payment")
                            .setLabel("Fail(Server)")
                            .build());
                }
            }else {
                new HamPayDialog(activity).showFailPaymentDialog(Constants.LOCAL_ERROR_CODE,
                        activity.getString(R.string.msg_fail_payment));

                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Individual Payment")
                        .setAction("Payment")
                        .setLabel("Fail(Mobile)")
                        .build());
            }
        }

        @Override
        public void onTaskPreRun() { }
    }



    RequestBusinessPayment requestBusinessPayment;
    BusinessPaymentRequest businessPaymentRequest;

    public void businessPaymentConfirmDialog(final BusinessPaymentConfirmResponse businessPaymentConfirmResponse,
                                             final Long amountValue,
                                             final String userMessage){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_pay_one, null);
        FacedTextView pay_one_confirm = (FacedTextView) view.findViewById(R.id.pay_one_confirm);
        FacedTextView confirmation = (FacedTextView) view.findViewById(R.id.confirmation);
        FacedTextView dis_confirmation = (FacedTextView) view.findViewById(R.id.dis_confirmation);

        confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showWaitingdDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
                businessPaymentRequest = new BusinessPaymentRequest();
                businessPaymentRequest.setAmount(amountValue);
                businessPaymentRequest.setBusinessCode(businessPaymentConfirmResponse.getBusinessCode());
                businessPaymentRequest.setMessage(userMessage);
                requestBusinessPayment = new RequestBusinessPayment(activity,
                        new RequestBusinessPaymentTaskCompleteListener(businessPaymentRequest, businessPaymentConfirmResponse.getFullName()));
                requestBusinessPayment.execute(businessPaymentRequest);
            }
        });


        dis_confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        pay_one_confirm.setText(activity.getString(R.string.pay_one_confirm,
                (new PersianEnglishDigit()).E2P(String.format("%,d", amountValue).replace(".", ",")),
//                amountValue.toString(),
                businessPaymentConfirmResponse.getFullName(),
                businessPaymentConfirmResponse.getBankName()));

        view.setMinimumWidth((int) (displayRectangle.width() * 0.8f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);

        dialog.show();
    }


    public class RequestBusinessPaymentTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<BusinessPaymentResponse>> {

        String businessName;
        BusinessPaymentRequest businessPaymentRequest;

        public RequestBusinessPaymentTaskCompleteListener(BusinessPaymentRequest businessPaymentRequest, String businessName){
            this.businessName = businessName;
            this.businessPaymentRequest = businessPaymentRequest;
        }

        @Override
        public void onTaskComplete(ResponseMessage<BusinessPaymentResponse> businessPaymentResponseMessage) {

            dismisWaitingDialog();

            if (businessPaymentResponseMessage != null){
                if (businessPaymentResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS){
                    new HamPayDialog(activity).businessPaymentDialog(businessPaymentResponseMessage.getService(),
                            businessPaymentRequest,
                            businessName);

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Business Payment")
                            .setAction("Payment")
                            .setLabel("Success")
                            .build());

                }else {
                    showFailPaymentDialog(businessPaymentResponseMessage.getService().getResultStatus().getCode(),
                            businessPaymentResponseMessage.getService().getResultStatus().getDescription());

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Business Payment")
                            .setAction("Payment")
                            .setLabel("Fail(Server)")
                            .build());
                }
            }else {
                new HamPayDialog(activity).showFailPaymentDialog(Constants.LOCAL_ERROR_CODE,
                        activity.getString(R.string.msg_fail_payment));

                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Business Payment")
                        .setAction("Payment")
                        .setLabel("Fail(Mobile)")
                        .build());
            }
        }

        @Override
        public void onTaskPreRun() { }
    }


    public void businessPaymentDialog(final BusinessPaymentResponse businessPaymentResponse,
                                      final BusinessPaymentRequest businessPaymentRequest,
                                      final String contactName){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_pay_one_ref, null);

        FacedTextView pay_one_confirm_ref = (FacedTextView) view.findViewById(R.id.pay_one_confirm_ref);
        FacedTextView confirmation = (FacedTextView) view.findViewById(R.id.confirmation);


        confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.finish();
                activity.onBackPressed();
            }
        });

        pay_one_confirm_ref.setText((new PersianEnglishDigit(activity.getString(R.string.pay_one_ref, businessPaymentResponse.getRefCode()))).E2P());


        view.setMinimumWidth((int) (displayRectangle.width() * 0.8f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);

        dialog.show();
    }


    public void showPreventRootDeviceDialog(){

        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_prevent_root_device, null);

        FacedTextView prevent_rooted_device = (FacedTextView) view.findViewById(R.id.prevent_rooted_device);

        prevent_rooted_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.finish();
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


    public void showInvalidStepDialog(){

        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_invalid_step, null);

        FacedTextView invalid_step_confrim = (FacedTextView) view.findViewById(R.id.invalid_step_confrim);
        FacedTextView invalid_step_disconfrim = (FacedTextView)view.findViewById(R.id.invalid_step_disconfrim);

        invalid_step_confrim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.clear().commit();
                editor.commit();

                dialog.dismiss();
                Intent intent = new Intent(activity, AppSliderActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.finish();
                activity.startActivity(intent);
            }
        });

        invalid_step_disconfrim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.finish();
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


    public void showFailBankListDialog(final RequestBankList requestBankList,
                                       final BankListRequest bankListRequest,
                                       final String code,
                                       final String message){

        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_bank_list, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_fetch_bank_list = (FacedTextView) view.findViewById(R.id.retry_fetch_bank_list);
        FacedTextView cancel_request = (FacedTextView)view.findViewById(R.id.cancel_request);

        retry_fetch_bank_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestBankList.execute(bankListRequest);
            }
        });

        cancel_request.setOnClickListener(new View.OnClickListener() {
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
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void showFailRegistrationEntryDialog(final RequestRegistrationEntry requestRegistrationEntry,
                                                final RegistrationEntryRequest registrationEntryRequest,
                                                final String code,
                                                final String message){

        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_registration_entry, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_registration_entry = (FacedTextView) view.findViewById(R.id.retry_registration_entry);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);


        retry_registration_entry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestRegistrationEntry.execute(registrationEntryRequest);
            }
        });

        cancel_request.setOnClickListener(new View.OnClickListener() {
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
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void showFailRegistrationSendSmsTokenDialog(final RequestRegistrationSendSmsToken requestRegistrationSendSmsToken,
                                                       final RegistrationSendSmsTokenRequest registrationEntryRequest,
                                                       final String code,
                                                       final String message){

        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_registration_send_sms_token, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_registration_sms_token = (FacedTextView) view.findViewById(R.id.retry_registration_sms_token);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);

        retry_registration_sms_token.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestRegistrationSendSmsToken.execute(registrationEntryRequest);
            }
        });

        cancel_request.setOnClickListener(new View.OnClickListener() {
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
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void showFailRegistrationMobileIdDialog(final RequestMobileRegistrationIdEntry requestMobileRegistrationIdEntry,
                                                       final MobileRegistrationIdEntryRequest mobileRegistrationIdEntryRequest,
                                                       final String code,
                                                       final String message){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_registration_send_sms_token, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_registration_sms_token = (FacedTextView) view.findViewById(R.id.retry_registration_sms_token);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);

        retry_registration_sms_token.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestMobileRegistrationIdEntry.execute(mobileRegistrationIdEntryRequest);
            }
        });

        cancel_request.setOnClickListener(new View.OnClickListener() {
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
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


    public void showFailRegistrationVerifyMobileDialog(final RequestVerifyMobile requestVerifyMobile,
                                                       final RegistrationVerifyMobileRequest registrationVerifyMobileRequest,
                                                       final String code,
                                                       final String message){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_registration_verify_mobile_request, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_registration_verify_mobile = (FacedTextView) view.findViewById(R.id.retry_registration_verify_mobile);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);


        retry_registration_verify_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestVerifyMobile.execute(registrationVerifyMobileRequest);
            }
        });

        cancel_request.setOnClickListener(new View.OnClickListener() {
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
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void showFailConfirmUserDataDialog(final RequestConfirmUserData requestConfirmUserData,
                                              final RegistrationConfirmUserDataRequest registrationConfirmUserDataRequest,
                                              final String code,
                                              final String message){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_confirm_user_data, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_confirm_user_data = (FacedTextView) view.findViewById(R.id.retry_confirm_user_data);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);


        retry_confirm_user_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestConfirmUserData.execute(registrationConfirmUserDataRequest);
            }
        });
        cancel_request.setOnClickListener(new View.OnClickListener() {
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
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


    public void showFailFetchUserDataDialog(final RequestFetchUserData requestFetchUserData,
                                            final RegistrationFetchUserDataRequest registrationFetchUserDataRequest,
                                            final String code,
                                            final String message){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_fetch_user_data, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_fetch_user_data = (FacedTextView) view.findViewById(R.id.retry_fetch_user_data);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);


        retry_fetch_user_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestFetchUserData.execute(registrationFetchUserDataRequest);
            }
        });
        cancel_request.setOnClickListener(new View.OnClickListener() {
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
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }



    public void showFailRegisterVerifyAccountDialog(final RequestRegisterVerifyAccount requestRegisterVerifyAccount,
                                                    final RegistrationVerifyAccountRequest registrationVerifyAccountRequest,
                                                    final String code,
                                                    final String message){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_request_register_verify_account, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_verify_account = (FacedTextView) view.findViewById(R.id.retry_verify_account);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);


        retry_verify_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestRegisterVerifyAccount.execute(registrationVerifyAccountRequest);
            }
        });
        cancel_request.setOnClickListener(new View.OnClickListener() {
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
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void showFailVerifyAccountDialog(final RequestVerifyAccount requestVerifyAccount,
                                            final VerifyAccountRequest verifyAccountRequest,
                                            final String code,
                                            final String message){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_request_register_verify_account, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_verify_account = (FacedTextView) view.findViewById(R.id.retry_verify_account);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);


        retry_verify_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestVerifyAccount.execute(verifyAccountRequest);
            }
        });

        cancel_request.setOnClickListener(new View.OnClickListener() {
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
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void showFailUserProfileDialog(final RequestUserProfile requestUserProfile,
                                          final UserProfileRequest userProfileRequest,
                                          final String code,
                                          final String message){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_user_profile, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_user_profile = (FacedTextView) view.findViewById(R.id.retry_user_profile);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);

        retry_user_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestUserProfile.execute(userProfileRequest);
            }
        });

        cancel_request.setOnClickListener(new View.OnClickListener() {
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
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


    public void showFailRequestVerifyTransferMoneyDialog(final RequestRegistrationVerifyTransferMoney requestRegistrationVerifyTransferMoney,
                                                         final RegistrationVerifyTransferMoneyRequest registrationVerifyTransferMoneyRequest,
                                                         final String code,
                                                         final String message){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_request_verify_transfer_money, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        if (code.length() == 0){
            responseCode.setText("");
            responseCode.setVisibility(View.GONE);
        }else {
            responseCode.setText(activity.getString(R.string.error_code, code));
        }
        responseMessage.setText(message);

        FacedTextView retry_verify_transfer_money = (FacedTextView) view.findViewById(R.id.retry_verify_transfer_money);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);


        retry_verify_transfer_money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestRegistrationVerifyTransferMoney.execute(registrationVerifyTransferMoneyRequest);
            }
        });
        cancel_request.setOnClickListener(new View.OnClickListener() {
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
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


    public void showFailVerifyTransferMoneyDialog(final RequestVerifyTransferMoney requestVerifyTransferMoney,
                                                  final VerifyTransferMoneyRequest verifyTransferMoneyRequest,
                                                  final String code,
                                                  final String message){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_request_verify_transfer_money, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        if (code.length() == 0){
            responseCode.setText("");
            responseCode.setVisibility(View.GONE);
        }else {
            responseCode.setText(activity.getString(R.string.error_code, code));
        }
        responseMessage.setText(message);

        FacedTextView retry_verify_transfer_money = (FacedTextView) view.findViewById(R.id.retry_verify_transfer_money);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);


        retry_verify_transfer_money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showWaitingdDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
                requestVerifyTransferMoney.execute(verifyTransferMoneyRequest);
            }
        });
        cancel_request.setOnClickListener(new View.OnClickListener() {
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
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


//    public void showFailPasswordEntryDialog(final RequestPassCodeEntry requestPassCodeEntry,
//                                            final RegistrationPassCodeEntryRequest registrationPassCodeEntryRequest,
//                                            final String code,
//                                            final String message){
//        Rect displayRectangle = new Rect();
//        Activity parent = (Activity) activity;
//        Window window = parent.getWindow();
//        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
//
//        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_password_entry, null);
//
//        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
//        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);
//
//        responseCode.setText(activity.getString(R.string.error_code, code));
//        responseMessage.setText(message);
//
//        FacedTextView retry_pass_code_entry = (FacedTextView) view.findViewById(R.id.retry_pass_code_entry);
//        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);
//
//        retry_pass_code_entry.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//                requestPassCodeEntry.execute(registrationPassCodeEntryRequest);
//            }
//        });
//        cancel_request.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
//        dialog = new Dialog(activity);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.setContentView(view);
//        dialog.setTitle(null);
//        dialog.setCanceledOnTouchOutside(true);
//        dialog.show();
//    }


    public void showFailMemorableEntryDialog(final RequestCredentialEntry requestMemorableWordEntry,
                                             final RegistrationCredentialsRequest registrationMemorableWordEntryRequest,
                                             final String code,
                                             final String message){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_memorable_entry, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_memorable_entry = (FacedTextView) view.findViewById(R.id.retry_memorable_entry);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);

        retry_memorable_entry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestMemorableWordEntry.execute(registrationMemorableWordEntryRequest);
            }
        });

        cancel_request.setOnClickListener(new View.OnClickListener() {
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
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void showFailTCRequestDialog(final RequestTAC requestTAC,
                                        final TACRequest tacRequest,
                                        final String code,
                                        final String message){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_tac_request, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_tac_request = (FacedTextView) view.findViewById(R.id.retry_tac_request);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);


        retry_tac_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (requestTAC.getStatus() == AsyncTask.Status.FINISHED)
                    requestTAC.execute(tacRequest);
            }
        });
        cancel_request.setOnClickListener(new View.OnClickListener() {
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
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


    public void showFailTACAcceeptRequestDialog(final RequestTACAccept requestTACAccept,
                                                final TACAcceptRequest tacAcceptRequest,
                                                final String code,
                                                final String message){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_tac_accept_request, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_tac_accept_request = (FacedTextView) view.findViewById(R.id.retry_tac_accept_request);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);


        retry_tac_accept_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestTACAccept.execute(tacAcceptRequest);
            }
        });
        cancel_request.setOnClickListener(new View.OnClickListener() {
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
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void showSuccessChangeSettingDialog(final String message){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_success_change_setting, null);

        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseMessage.setText(message);

        FacedTextView success_change_setting = (FacedTextView) view.findViewById(R.id.success_change_setting);

        success_change_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
//                activity.finish();

                Intent intent = new Intent();
                intent.setClass(activity, AppSliderActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
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
        dialog.setCancelable(false);
        dialog.show();
    }

    public void showFailChangePassCodeDialog(final RequestChangePassCode requestChangePassCode,
                                             final ChangePassCodeRequest changePassCodeRequest,
                                             final String code,
                                             final String message){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_change_pass_code, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_change_pass_code = (FacedTextView) view.findViewById(R.id.retry_change_pass_code);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);

        if ((code.compareTo("1005") == 0) || (code.compareTo("۱۰۰۵") == 0) ){
            retry_change_pass_code.setVisibility(View.INVISIBLE);
        }

        retry_change_pass_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
//                activity.finish();
//                requestChangePassCode.execute(changePassCodeRequest);
            }
        });
        cancel_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.finish();
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }



    public void showFailChangeMemorableWordDialog(final RequestChangeMemorableWord requestChangeMemorableWord,
                                                  final ChangeMemorableWordRequest changeMemorableWordRequest,
                                                  final String code,
                                                  final String message){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_change_memorable_word, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_change_memorable_word = (FacedTextView) view.findViewById(R.id.retry_change_memorable_word);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);

        if ((code.compareTo("1005") == 0) || (code.compareTo("۱۰۰۵") == 0) ){
            retry_change_memorable_word.setVisibility(View.INVISIBLE);
        }

        retry_change_memorable_word.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                Intent intent = new Intent();
                intent.setClass(activity, ChangeMemorableActivity.class);
                activity.startActivity(intent);
                activity.finish();
            }
        });
        cancel_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.finish();
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }


    public void showFailUserTransactionDialog(final RequestUserTransaction requestUserTransaction,
                                              final TransactionListRequest transactionListRequest,
                                              final String code,
                                              final String message){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_user_transaction, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_user_transation = (FacedTextView) view.findViewById(R.id.retry_user_transation);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);

        retry_user_transation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestUserTransaction.execute(transactionListRequest);
            }
        });
        cancel_request.setOnClickListener(new View.OnClickListener() {
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
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void showFailBusinessListDialog(final RequestHamPayBusiness requestHamPayBusiness,
                                           final BusinessListRequest businessListRequest,
                                           final String code,
                                           final String message){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_business_list, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_business_list = (FacedTextView) view.findViewById(R.id.retry_business_list);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);


        retry_business_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestHamPayBusiness.execute(businessListRequest);
            }
        });
        cancel_request.setOnClickListener(new View.OnClickListener() {
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
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void showFailBusinessSearchListDialog(final RequestSearchHamPayBusiness requestSearchHamPayBusiness,
                                                 final BusinessSearchRequest businessSearchRequest,
                                                 final String code,
                                                 final String message){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_business_search_list, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_business_search_list = (FacedTextView) view.findViewById(R.id.retry_business_search_list);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);


        retry_business_search_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestSearchHamPayBusiness.execute(businessSearchRequest);
            }
        });
        cancel_request.setOnClickListener(new View.OnClickListener() {
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
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


    public void showFailContactsHamPayEnabledDialog(final RequestContactHampayEnabled requestContactHampayEnabled,
                                                    final ContactsHampayEnabledRequest contactsHampayEnabledRequest,
                                                    final String code,
                                                    final String message){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_contacts_enabled, null);



        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_contacts_enabled = (FacedTextView) view.findViewById(R.id.retry_contacts_enabled);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);


        retry_contacts_enabled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestContactHampayEnabled.execute(contactsHampayEnabledRequest);
            }
        });
        cancel_request.setOnClickListener(new View.OnClickListener() {
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
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void showFailPaymentDialog(final String code,
                                      final String message){

        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_payment, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_payment = (FacedTextView) view.findViewById(R.id.retry_payment);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);


        retry_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        cancel_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.finish();
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void showFailPaymentPermissionDialog(String message){

        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_payment_permission, null);

        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseMessage.setText(activity.getString(R.string.msg_fail_payment_permission, message));


        FacedTextView payment_permission = (FacedTextView) view.findViewById(R.id.payment_permission);

        payment_permission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.finish();
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void showIncorrectAmountDialog(Long MaxXferAmount, Long MinXferAmount){

        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_payment_permission, null);

        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        NumberFormat nf = NumberFormat.getInstance();

        responseMessage.setText(activity.getString(R.string.msg_incorrect_amount, new PersianEnglishDigit(nf.format(MaxXferAmount)).E2P() + ""
                ,  new PersianEnglishDigit(nf.format(MinXferAmount) + "").E2P()));

        FacedTextView payment_permission = (FacedTextView) view.findViewById(R.id.payment_permission);

        payment_permission.setOnClickListener(new View.OnClickListener() {
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
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


    public void showUnlinkDialog(){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_unlink, null);

        FacedTextView unlink_confirm = (FacedTextView) view.findViewById(R.id.unlink_confirm);
        FacedTextView unlink_disconfirm = (FacedTextView) view.findViewById(R.id.unlink_disconfirm);

        unlink_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.setClass(activity, UnlinkPassActivity.class);
                activity.startActivity(intent);
            }
        });

        unlink_disconfirm.setOnClickListener(new View.OnClickListener() {
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
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void showFailUnlinkDialog(final RequestUnlinkUser requestUnlinkUser,
                                     final UnlinkUserRequest unlinkUserRequest,
                                     final String code,
                                     final String message){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_unlink_user, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_unlink_user = (FacedTextView) view.findViewById(R.id.retry_unlink_user);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);

        if ((code.compareTo("1005") == 0) || (code.compareTo("۱۰۰۵") == 0) ){
            retry_unlink_user.setVisibility(View.INVISIBLE);
        }

        retry_unlink_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if (requestUnlinkUser.getStatus() == AsyncTask.Status.FINISHED) {
                    requestUnlinkUser.execute(unlinkUserRequest);
                }
            }
        });
        cancel_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.finish();
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }


    public void showFailGetUserIdTokenDialog(final RequestUserIdToken requestUserIdToken,
                                             final GetUserIdTokenRequest getUserIdTokenRequest,
                                             final String code,
                                             final String message){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_server_key, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_server_key = (FacedTextView) view.findViewById(R.id.retry_server_key);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);

        retry_server_key.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestUserIdToken.execute(getUserIdTokenRequest);
            }
        });

        cancel_request.setOnClickListener(new View.OnClickListener() {
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
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

}