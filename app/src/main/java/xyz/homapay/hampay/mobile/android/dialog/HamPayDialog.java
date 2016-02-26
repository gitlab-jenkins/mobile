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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.text.NumberFormat;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.BankListRequest;
import xyz.homapay.hampay.common.core.model.request.BusinessListRequest;
import xyz.homapay.hampay.common.core.model.request.BusinessPaymentRequest;
import xyz.homapay.hampay.common.core.model.request.BusinessSearchRequest;
import xyz.homapay.hampay.common.core.model.request.CardProfileRequest;
import xyz.homapay.hampay.common.core.model.request.ChangeEmailRequest;
import xyz.homapay.hampay.common.core.model.request.ChangeMemorableWordRequest;
import xyz.homapay.hampay.common.core.model.request.ChangePassCodeRequest;
import xyz.homapay.hampay.common.core.model.request.ContactUsRequest;
import xyz.homapay.hampay.common.core.model.request.ContactsHampayEnabledRequest;
import xyz.homapay.hampay.common.core.model.request.GetUserIdTokenRequest;
import xyz.homapay.hampay.common.core.model.request.IBANChangeRequest;
import xyz.homapay.hampay.common.core.model.request.IBANConfirmationRequest;
import xyz.homapay.hampay.common.core.model.request.IllegalAppListRequest;
import xyz.homapay.hampay.common.core.model.request.IndividualPaymentRequest;
import xyz.homapay.hampay.common.core.model.request.LatestPurchaseRequest;
import xyz.homapay.hampay.common.core.model.request.MobileRegistrationIdEntryRequest;
import xyz.homapay.hampay.common.core.model.request.RegistrationConfirmUserDataRequest;
import xyz.homapay.hampay.common.core.model.request.RegistrationCredentialsRequest;
import xyz.homapay.hampay.common.core.model.request.RegistrationEntryRequest;
import xyz.homapay.hampay.common.core.model.request.RegistrationFetchUserDataRequest;
import xyz.homapay.hampay.common.core.model.request.RegistrationSendSmsTokenRequest;
import xyz.homapay.hampay.common.core.model.request.RegistrationVerifyAccountRequest;
import xyz.homapay.hampay.common.core.model.request.RegistrationVerifyMobileRequest;
import xyz.homapay.hampay.common.core.model.request.RegistrationVerifyTransferMoneyRequest;
import xyz.homapay.hampay.common.core.model.request.TACAcceptRequest;
import xyz.homapay.hampay.common.core.model.request.TACRequest;
import xyz.homapay.hampay.common.core.model.request.TransactionListRequest;
import xyz.homapay.hampay.common.core.model.request.UnlinkUserRequest;
import xyz.homapay.hampay.common.core.model.request.UploadImageRequest;
import xyz.homapay.hampay.common.core.model.request.UserProfileRequest;
import xyz.homapay.hampay.common.core.model.request.VerifyAccountRequest;
import xyz.homapay.hampay.common.core.model.request.VerifyTransferMoneyRequest;
import xyz.homapay.hampay.common.core.model.response.BusinessPaymentConfirmResponse;
import xyz.homapay.hampay.common.core.model.response.BusinessPaymentResponse;
import xyz.homapay.hampay.common.core.model.response.ChangeEmailResponse;
import xyz.homapay.hampay.common.core.model.response.ContactUsResponse;
import xyz.homapay.hampay.common.core.model.response.IBANChangeResponse;
import xyz.homapay.hampay.common.core.model.response.IBANConfirmationResponse;
import xyz.homapay.hampay.common.core.model.response.IndividualPaymentConfirmResponse;
import xyz.homapay.hampay.common.core.model.response.IndividualPaymentResponse;
import xyz.homapay.hampay.common.core.model.response.RegistrationSendSmsTokenResponse;
import xyz.homapay.hampay.common.core.model.response.TACAcceptResponse;
import xyz.homapay.hampay.common.core.model.response.dto.UserProfileDTO;
import xyz.homapay.hampay.common.psp.model.request.RegisterCardRequest;
import xyz.homapay.hampay.common.psp.model.request.UnregisterCardRequest;
import xyz.homapay.hampay.common.psp.model.response.PurchaseResponse;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.Helper.DatabaseHelper;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.activity.AppSliderActivity;
import xyz.homapay.hampay.mobile.android.activity.ChangeMemorableActivity;
import xyz.homapay.hampay.mobile.android.activity.ChangeUserImageActivity;
import xyz.homapay.hampay.mobile.android.activity.GuideDetailActivity;
import xyz.homapay.hampay.mobile.android.activity.HamPayLoginActivity;
import xyz.homapay.hampay.mobile.android.activity.MainActivity;
import xyz.homapay.hampay.mobile.android.activity.PostStartActivity;
import xyz.homapay.hampay.mobile.android.activity.ProfileEntryActivity;
import xyz.homapay.hampay.mobile.android.activity.SMSVerificationActivity;
import xyz.homapay.hampay.mobile.android.activity.StartActivity;
import xyz.homapay.hampay.mobile.android.activity.UnlinkPassActivity;
import xyz.homapay.hampay.mobile.android.analytics.GaAnalyticsEvent;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestBankList;
import xyz.homapay.hampay.mobile.android.async.RequestBusinessPayment;
import xyz.homapay.hampay.mobile.android.async.RequestCardProfile;
import xyz.homapay.hampay.mobile.android.async.RequestChangeEmail;
import xyz.homapay.hampay.mobile.android.async.RequestChangeMemorableWord;
import xyz.homapay.hampay.mobile.android.async.RequestChangePassCode;
import xyz.homapay.hampay.mobile.android.async.RequestConfirmUserData;
import xyz.homapay.hampay.mobile.android.async.RequestContactHampayEnabled;
import xyz.homapay.hampay.mobile.android.async.RequestCredentialEntry;
import xyz.homapay.hampay.mobile.android.async.RequestFetchUserData;
import xyz.homapay.hampay.mobile.android.async.RequestHamPayBusiness;
import xyz.homapay.hampay.mobile.android.async.RequestIBANChange;
import xyz.homapay.hampay.mobile.android.async.RequestIBANConfirmation;
import xyz.homapay.hampay.mobile.android.async.RequestIllegalAppList;
import xyz.homapay.hampay.mobile.android.async.RequestIndividualPayment;
import xyz.homapay.hampay.mobile.android.async.RequestLatestPurchase;
import xyz.homapay.hampay.mobile.android.async.RequestLogout;
import xyz.homapay.hampay.mobile.android.async.RequestMobileRegistrationIdEntry;
import xyz.homapay.hampay.mobile.android.async.RequestRegisterCard;
import xyz.homapay.hampay.mobile.android.async.RequestRegisterVerifyAccount;
import xyz.homapay.hampay.mobile.android.async.RequestRegistrationEntry;
import xyz.homapay.hampay.mobile.android.async.RequestRegistrationSendSmsToken;
import xyz.homapay.hampay.mobile.android.async.RequestRegistrationVerifyTransferMoney;
import xyz.homapay.hampay.mobile.android.async.RequestSearchHamPayBusiness;
import xyz.homapay.hampay.mobile.android.async.RequestTAC;
import xyz.homapay.hampay.mobile.android.async.RequestTACAccept;
import xyz.homapay.hampay.mobile.android.async.RequestUnlinkUser;
import xyz.homapay.hampay.mobile.android.async.RequestUploadImage;
import xyz.homapay.hampay.mobile.android.async.RequestUserIdToken;
import xyz.homapay.hampay.mobile.android.async.RequestUserProfile;
import xyz.homapay.hampay.mobile.android.async.RequestUserTransaction;
import xyz.homapay.hampay.mobile.android.async.RequestVerifyAccount;
import xyz.homapay.hampay.mobile.android.async.RequestVerifyMobile;
import xyz.homapay.hampay.mobile.android.async.RequestVerifyTransferMoney;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.edittext.EmailTextWatcher;
import xyz.homapay.hampay.mobile.android.component.edittext.FacedEditText;
import xyz.homapay.hampay.mobile.android.model.FailedLoginResponse;
import xyz.homapay.hampay.mobile.android.model.LogoutData;
import xyz.homapay.hampay.mobile.android.model.LogoutResponse;
import xyz.homapay.hampay.mobile.android.model.RecentPay;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.EmailVerification;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;

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

    public void showTcPrivacyDialog(){

        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_tc_privacy, null);

        FacedTextView tc_privacy_text = (FacedTextView) view.findViewById(R.id.tc_privacy_text);

        Spannable tcPrivacySpannable = new SpannableString(activity.getString(R.string.tc_privacy_text));
        ClickableSpan tcClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent intent = new Intent();
                intent.setClass(activity, GuideDetailActivity.class);
                intent.putExtra(Constants.WEB_PAGE_ADDRESS, Constants.HTTPS_SERVER_IP + "/help/privacy.html");
                activity.startActivity(intent);
            }
        };
        tcPrivacySpannable.setSpan(tcClickableSpan, 3, 35, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tc_privacy_text.setText(tcPrivacySpannable);
        tc_privacy_text.setMovementMethod(LinkMovementMethod.getInstance());
        ClickableSpan privacySpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent intent = new Intent();
                intent.setClass(activity, GuideDetailActivity.class);
                intent.putExtra(Constants.WEB_PAGE_ADDRESS, Constants.HTTPS_SERVER_IP + "/help/tc.html");
                activity.startActivity(intent);
            }
        };

        tcPrivacySpannable.setSpan(privacySpan, 38, 65, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tc_privacy_text.setText(tcPrivacySpannable);
        tc_privacy_text.setMovementMethod(LinkMovementMethod.getInstance());


        FacedTextView tc_privacy_confirm = (FacedTextView) view.findViewById(R.id.tc_privacy_confirm);
        tc_privacy_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
//                intent.setClass(activity, PostStartActivity.class);
                intent.setClass(activity, ProfileEntryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.finish();
                activity.startActivity(intent);
            }
        });


        FacedTextView tc_privacy_disconfirm = (FacedTextView) view.findViewById(R.id.tc_privacy_disconfirm);

        tc_privacy_disconfirm.setOnClickListener(new View.OnClickListener() {
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

        Pattern pattern = Pattern.compile(Constants.WEB_URL_REGEX);
        Matcher matcher = pattern.matcher(accept_term);
        while (matcher.find()) {
            final String urlStr = matcher.group();

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
                    intent.putExtra(Constants.PENDING_PURCHASE_PAYMENT_ID, tacAcceptResponseMessage.getService().getProductCode());
                    intent.putExtra(Constants.PENDING_PURCHASE_PAYMENT_COUNT, tacAcceptResponseMessage.getService().getPendingPurchasesCount());
                    intent.putExtra(Constants.USER_PROFILE_DTO, userProfileDTO);
                    editor.putBoolean(Constants.FORCE_USER_PROFILE, false);
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
                individualPaymentConfirmResponse.getFullName(),
                (new PersianEnglishDigit().E2P(String.format("%,d", individualPaymentConfirmResponse.getFeeCharge()).replace(".", ","))),
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


//    public void businessPaymentConfirmDialog(final BusinessPaymentConfirmResponse businessPaymentConfirmResponse,
//                                             final Long amountValue,
//                                             final String userMessage){
//        Rect displayRectangle = new Rect();
//        Activity parent = (Activity) activity;
//        Window window = parent.getWindow();
//        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
//        View view = activity.getLayoutInflater().inflate(R.layout.dialog_pay_one, null);
//        FacedTextView pay_one_confirm = (FacedTextView) view.findViewById(R.id.pay_one_confirm);
//        FacedTextView confirmation = (FacedTextView) view.findViewById(R.id.confirmation);
//        FacedTextView dis_confirmation = (FacedTextView) view.findViewById(R.id.dis_confirmation);
//
//        confirmation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//                showWaitingdDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
//                businessPaymentRequest = new BusinessPaymentRequest();
//                businessPaymentRequest.setAmount(amountValue);
//                businessPaymentRequest.setBusinessCode(businessPaymentConfirmResponse.getBusinessCode());
//                businessPaymentRequest.setMessage(userMessage);
//                requestBusinessPayment = new RequestBusinessPayment(activity,
//                        new RequestBusinessPaymentTaskCompleteListener(businessPaymentRequest, businessPaymentConfirmResponse.getFullName()));
//                requestBusinessPayment.execute(businessPaymentRequest);
//            }
//        });
//
//
//        dis_confirmation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//        pay_one_confirm.setText(activity.getString(R.string.pay_one_confirm,
//                (new PersianEnglishDigit()).E2P(String.format("%,d", amountValue).replace(".", ",")),
//                businessPaymentConfirmResponse.getFullName(),
//                "۰",
//                businessPaymentConfirmResponse.getBankName()));
//
//        view.setMinimumWidth((int) (displayRectangle.width() * 0.8f));
//        dialog = new Dialog(activity);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.setContentView(view);
//        dialog.setTitle(null);
//        dialog.setCanceledOnTouchOutside(true);
//
//        dialog.show();
//    }


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

    public void showFailCardProfileDialog(final RequestCardProfile requestCardProfile,
                                       final CardProfileRequest cardProfileRequest,
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
                requestCardProfile.execute(cardProfileRequest);
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


    public void showFailIllegalAppListDialog(final RequestIllegalAppList requestIllegalAppList,
                                             final IllegalAppListRequest illegalAppListRequest,
                                             final String code,
                                             final String message){

        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_illegal_app_list, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_fetch_illegal_app_list = (FacedTextView) view.findViewById(R.id.retry_fetch_illegal_app_list);
        FacedTextView cancel_request = (FacedTextView)view.findViewById(R.id.cancel_request);

        retry_fetch_illegal_app_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestIllegalAppList.execute(illegalAppListRequest);
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

    public void showFailRegisterCardDialog(final RequestRegisterCard requestRegisterCard,
                                           final RegisterCardRequest registerCardRequest,
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
                requestRegisterCard.execute(registerCardRequest);
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
                if (requestUserTransaction.getStatus() == AsyncTask.Status.FINISHED)
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

        responseCode.setText(activity.getString(R.string.error_code, (new PersianEnglishDigit()).E2P(code)));
        responseMessage.setText((new PersianEnglishDigit()).E2P(message));

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
                , new PersianEnglishDigit(nf.format(MinXferAmount) + "").E2P()));

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

    public void showFailChnageEmail(final RequestChangeEmail requestChangeEmail,
                                    final ChangeEmailRequest changeEmailRequest,
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

                if (requestChangeEmail.getStatus() == AsyncTask.Status.FINISHED) {
                    requestChangeEmail.execute(changeEmailRequest);
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


    public void showUserProfileImage(){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_user_image_profile, null);

        FacedTextView camera_choose = (FacedTextView)view.findViewById(R.id.camera_choose);
        camera_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(activity, ChangeUserImageActivity.class);
                intent.putExtra(Constants.IMAGE_PROFILE_SOURCE, Constants.CAMERA_SELECT);
                activity.startActivityForResult(intent, 5000);
                dialog.dismiss();
            }
        });

        FacedTextView gallary_choose = (FacedTextView)view.findViewById(R.id.gallary_choose);
        gallary_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(activity, ChangeUserImageActivity.class);
                intent.putExtra(Constants.IMAGE_PROFILE_SOURCE, Constants.CONTENT_SELECT);
                activity.startActivityForResult(intent, 5000);
                dialog.dismiss();
            }
        });


        FacedTextView remove_choose = (FacedTextView)view.findViewById(R.id.remove_choose);
        FacedTextView cancel_choose = (FacedTextView)view.findViewById(R.id.cancel_choose);

        cancel_choose.setOnClickListener(new View.OnClickListener() {
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


    public void showFailUploadImage(final RequestUploadImage requestUploadImage,
                                    final UploadImageRequest uploadImageRequest,
                                    final String code,
                                    final String message){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_upload_image, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_upload_image = (FacedTextView) view.findViewById(R.id.retry_upload_image);
        FacedTextView cancel_upload_image = (FacedTextView) view.findViewById(R.id.cancel_upload_image);


        retry_upload_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (requestUploadImage.getStatus() == AsyncTask.Status.FINISHED)
                    requestUploadImage.execute(uploadImageRequest);
            }
        });
        cancel_upload_image.setOnClickListener(new View.OnClickListener() {
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


    public void showChangeEmail(final String password, final String memorableWord){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_change_email, null);

        ImageView emailIcon = (ImageView)view.findViewById(R.id.emailIcon);
        final CheckBox email_confirm_check = (CheckBox)view.findViewById(R.id.email_confirm_check);
        final FacedEditText emailValue = (FacedEditText)view.findViewById(R.id.emailValue);
        FacedTextView change_email = (FacedTextView)view.findViewById(R.id.change_email);
        FacedTextView cancel_change_email = (FacedTextView)view.findViewById(R.id.cancel_change_email);

        email_confirm_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!new EmailVerification().isValid(emailValue.getText().toString())) {
                        email_confirm_check.setChecked(false);
                    }
                }
            }
        });
        emailValue.addTextChangedListener(new EmailTextWatcher(emailValue, emailIcon));
        emailValue.setText(prefs.getString(Constants.REGISTERED_USER_EMAIL, ""));

        change_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (new EmailVerification().isValid(emailValue.getText().toString())) {
                    dialog.dismiss();
                    ChangeEmailRequest changeEmailRequest = new ChangeEmailRequest();
                    changeEmailRequest.setEmail(emailValue.getText().toString());
                    changeEmailRequest.setPassCode(password);
                    changeEmailRequest.setMemorableWord(memorableWord);
                    RequestChangeEmail requestChangeEmail = new RequestChangeEmail(activity, new RequestChangeEmailTaskCompleteListener(changeEmailRequest));
                    requestChangeEmail.execute(changeEmailRequest);
                }else {
                    Toast.makeText(activity, activity.getString(R.string.msg_email_invalid), Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancel_change_email.setOnClickListener(new View.OnClickListener() {
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

    public class RequestChangeEmailTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<ChangeEmailResponse>> {

        private RequestChangeEmail requestChangeEmail = null;
        private ChangeEmailRequest changeEmailRequest;

        public RequestChangeEmailTaskCompleteListener(ChangeEmailRequest changeEmailRequest){
            this.changeEmailRequest = changeEmailRequest;

        }

        @Override
        public void onTaskComplete(ResponseMessage<ChangeEmailResponse> changeEmailResponseResponseMessage)
        {

            dismisWaitingDialog();

            if (changeEmailResponseResponseMessage != null) {
                if (changeEmailResponseResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {

                    dialog.dismiss();
                    activity.finish();

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Change Email User")
                            .setAction("Change")
                            .setLabel("Success")
                            .build());
                }
                else {
                    requestChangeEmail = new RequestChangeEmail(activity, new RequestChangeEmailTaskCompleteListener(changeEmailRequest));
                    new HamPayDialog(activity).showFailChnageEmail(requestChangeEmail, changeEmailRequest,
                            changeEmailResponseResponseMessage.getService().getResultStatus().getCode(),
                            changeEmailResponseResponseMessage.getService().getResultStatus().getDescription());

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Change Email User")
                            .setAction("Change")
                            .setLabel("Fail(Server)")
                            .build());
                }
            }else {
                RequestChangeEmail requestChangeEmail = new RequestChangeEmail(activity, new RequestChangeEmailTaskCompleteListener(changeEmailRequest));
                new HamPayDialog(activity).showFailChnageEmail(requestChangeEmail, changeEmailRequest,
                        Constants.LOCAL_ERROR_CODE,
                        activity.getString(R.string.msg_gail_change_email));

                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Change Email User")
                        .setAction("Change")
                        .setLabel("Fail(Mobile)")
                        .build());
            }
        }

        @Override
        public void onTaskPreRun() {
            showWaitingdDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }


    RequestRegistrationSendSmsToken requestRegistrationSendSmsToken;
    RegistrationSendSmsTokenRequest registrationSendSmsTokenRequest;

    public void smsConfirmDialog(final String cellNumber, final String cardNumber){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_sms_confirm, null);
        FacedTextView sms_user_notify = (FacedTextView) view.findViewById(R.id.sms_user_notify);
        sms_user_notify.setText(activity.getString(R.string.sms_verification_text, new PersianEnglishDigit().E2P(prefs.getString(Constants.REGISTERED_CELL_NUMBER, ""))));
        FacedTextView confirmation = (FacedTextView) view.findViewById(R.id.confirmation);
        FacedTextView dis_confirmation = (FacedTextView) view.findViewById(R.id.dis_confirmation);

        confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                registrationSendSmsTokenRequest = new RegistrationSendSmsTokenRequest();
                registrationSendSmsTokenRequest.setUserIdToken(prefs.getString(Constants.REGISTERED_USER_ID_TOKEN, ""));
                requestRegistrationSendSmsToken = new RequestRegistrationSendSmsToken(activity, new RequestRegistrationSendSmsTokenTaskCompleteListener(cellNumber, cardNumber));
                requestRegistrationSendSmsToken.execute(registrationSendSmsTokenRequest);
            }
        });


        dis_confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.8f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);

        dialog.show();
    }

    public class RequestRegistrationSendSmsTokenTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<RegistrationSendSmsTokenResponse>> {


        private String cellNumber;
        private String cardNumber;

        public RequestRegistrationSendSmsTokenTaskCompleteListener(String cellNumber, String cardNumber){
            this.cellNumber = cellNumber;
            this.cardNumber = cardNumber;
        }

        @Override
        public void onTaskComplete(ResponseMessage<RegistrationSendSmsTokenResponse> registrationSendSmsTokenResponse)
        {

            dismisWaitingDialog();

            if (registrationSendSmsTokenResponse != null) {
                if (registrationSendSmsTokenResponse.getService().getResultStatus() == ResultStatus.SUCCESS) {

                    Intent intent = new Intent();
                    intent.setClass(activity, SMSVerificationActivity.class);
                    intent.putExtra(Constants.REGISTERED_CELL_NUMBER, cellNumber);
                    intent.putExtra(Constants.REGISTERED_ACCOUNT_NO, cardNumber);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    activity.finish();
                    activity.startActivity(intent);


                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Send Sms Token")
                            .setAction("Send")
                            .setLabel("Success")
                            .build());
                }else if (registrationSendSmsTokenResponse.getService().getResultStatus() == ResultStatus.REGISTRATION_INVALID_STEP){
                    new HamPayDialog(activity).showInvalidStepDialog();

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Send Sms Token")
                            .setAction("Send")
                            .setLabel("Success(Invalid)")
                            .build());
                }
                else {
                    requestRegistrationSendSmsToken = new RequestRegistrationSendSmsToken(activity, new RequestRegistrationSendSmsTokenTaskCompleteListener(cellNumber, cardNumber));
                    new HamPayDialog(activity).showFailRegistrationSendSmsTokenDialog(requestRegistrationSendSmsToken, registrationSendSmsTokenRequest,
                            registrationSendSmsTokenResponse.getService().getResultStatus().getCode(),
                            registrationSendSmsTokenResponse.getService().getResultStatus().getDescription());

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Send Sms Token")
                            .setAction("Send")
                            .setLabel("Fail(Server)")
                            .build());
                }

            }else {
                requestRegistrationSendSmsToken = new RequestRegistrationSendSmsToken(activity, new RequestRegistrationSendSmsTokenTaskCompleteListener(cellNumber, cardNumber));
                new HamPayDialog(activity).showFailRegistrationSendSmsTokenDialog(requestRegistrationSendSmsToken, registrationSendSmsTokenRequest,
                        Constants.LOCAL_ERROR_CODE,
                        activity.getString(R.string.mgs_fail_registration_send_sms_token));

                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Send Sms Token")
                        .setAction("Send")
                        .setLabel("Fail(Mobile)")
                        .build());
            }
        }

        @Override
        public void onTaskPreRun() {
            showWaitingdSMSDialog(requestRegistrationSendSmsToken, "");
        }
    }


    public void pspResultDialog(String purchaseCode){

        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_request_pay, null);

        FacedTextView request_payment_message = (FacedTextView) view.findViewById(R.id.request_payment_message);
//        request_payment_message.setText(activity.getString(R.string.msg_success_pending_payment, new PersianEnglishDigit().E2P(purchaseCode) + ""));
        request_payment_message.setText(activity.getString(R.string.msg_success_psp_result));
        FacedTextView confirmation = (FacedTextView) view.findViewById(R.id.confirmation);

        confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.finish();
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.8f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);

        dialog.show();
    }


    public void showFailPendingPaymentDialog(final RequestLatestPurchase requestLatestPurchase,
                                             final LatestPurchaseRequest latestPurchaseRequest,
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
                requestLatestPurchase.execute(latestPurchaseRequest);
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


    public void creditRequestDialog(){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_credit_request, null);

        FacedTextView pay_one_confirm_ref = (FacedTextView) view.findViewById(R.id.pay_one_confirm_ref);
        FacedTextView confirmation = (FacedTextView) view.findViewById(R.id.confirmation);


        confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", 1024);
                activity.setResult(1024);
                activity.finish();
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.8f));
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);

        dialog.show();
    }

    public void showIBANConfirmationDialog(final String iban, final IBANConfirmationResponse ibanConfirmationResponse){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_request_iban_confirm, null);

        FacedTextView ibanNumber = (FacedTextView)view.findViewById(R.id.ibanNumber);
        FacedTextView ibanOwnInfo = (FacedTextView)view.findViewById(R.id.ibanOwnInfo);

        ibanNumber.setText("IR" + new PersianEnglishDigit().E2P(iban));
        ibanOwnInfo.setText(activity.getString(R.string.sheba_question_part2_text, ibanConfirmationResponse.getName(), ibanConfirmationResponse.getBankName()));

        FacedTextView iban_request_confirm = (FacedTextView) view.findViewById(R.id.iban_request_confirm);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);

        iban_request_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IBANChangeRequest ibanChangeRequest = new IBANChangeRequest();
                ibanChangeRequest.setIban(iban);
                RequestIBANChange requestIBANChange = new RequestIBANChange(activity, new RequestIBANChangeTaskCompleteListener(ibanChangeRequest));
                requestIBANChange.execute(ibanChangeRequest);
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


    public class RequestIBANChangeTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<IBANChangeResponse>> {

        IBANChangeRequest ibanChangeRequest;
        RequestIBANChange requestIBANChange;

        public RequestIBANChangeTaskCompleteListener(IBANChangeRequest ibanChangeRequest) {
            this.ibanChangeRequest = ibanChangeRequest;
        }

        @Override
        public void onTaskComplete(ResponseMessage<IBANChangeResponse> ibanChangeResponseMessage) {
            dismisWaitingDialog();
            if (ibanChangeResponseMessage != null) {
                if (ibanChangeResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", 1023);
                    activity.setResult(1023);
                    activity.finish();
                } else {
                    requestIBANChange = new RequestIBANChange(activity, new RequestIBANChangeTaskCompleteListener(ibanChangeRequest));
                    showFailIBANChangeDialog(requestIBANChange, ibanChangeRequest,
                            ibanChangeResponseMessage.getService().getResultStatus().getCode(),
                            ibanChangeResponseMessage.getService().getResultStatus().getDescription());
                }
            } else {
                requestIBANChange = new RequestIBANChange(activity, new RequestIBANChangeTaskCompleteListener(ibanChangeRequest));
                showFailIBANChangeDialog(requestIBANChange, ibanChangeRequest,
                        Constants.LOCAL_ERROR_CODE,
                        activity.getString(R.string.msg_fail_iban_change));
            }
        }

        @Override
        public void onTaskPreRun() {
            showWaitingdDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }

    public void showFailIBANChangeDialog(final RequestIBANChange requestIBANChange,
                                         final IBANChangeRequest ibanChangeRequest,
                                         final String code,
                                         final String message){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_request_iban_confirm, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_iban_request = (FacedTextView) view.findViewById(R.id.retry_iban_request);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);

        retry_iban_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestIBANChange.execute(ibanChangeRequest);
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

    public void showFailIBANConfirmationDialog(final RequestIBANConfirmation requestIBANConfirmation,
                                               final IBANConfirmationRequest ibanConfirmationRequest,
                                               final String code,
                                               final String message){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_request_iban_confirm, null);

        FacedTextView responseCode = (FacedTextView)view.findViewById(R.id.responseCode);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseCode.setText(activity.getString(R.string.error_code, code));
        responseMessage.setText(message);

        FacedTextView retry_iban_request = (FacedTextView) view.findViewById(R.id.retry_iban_request);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);

        retry_iban_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestIBANConfirmation.execute(ibanConfirmationRequest);
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
