package xyz.homapay.hampay.mobile.android.dialog;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.content.IntentCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

import xyz.homapay.hampay.common.common.encrypt.EncryptionException;
import xyz.homapay.hampay.common.common.request.LogoutRequest;
import xyz.homapay.hampay.common.common.response.LogoutResponse;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.BusinessListRequest;
import xyz.homapay.hampay.common.core.model.request.BusinessSearchRequest;
import xyz.homapay.hampay.common.core.model.request.CardProfileRequest;
import xyz.homapay.hampay.common.core.model.request.ChangeEmailRequest;
import xyz.homapay.hampay.common.core.model.request.ChangeMemorableWordRequest;
import xyz.homapay.hampay.common.core.model.request.ChangePassCodeRequest;
import xyz.homapay.hampay.common.core.model.request.ContactUsRequest;
import xyz.homapay.hampay.common.core.model.request.ContactsHampayEnabledRequest;
import xyz.homapay.hampay.common.core.model.request.IBANChangeRequest;
import xyz.homapay.hampay.common.core.model.request.IBANConfirmationRequest;
import xyz.homapay.hampay.common.core.model.request.IllegalAppListRequest;
import xyz.homapay.hampay.common.core.model.request.LatestPaymentRequest;
import xyz.homapay.hampay.common.core.model.request.LatestPurchaseRequest;
import xyz.homapay.hampay.common.core.model.request.RegistrationCredentialsRequest;
import xyz.homapay.hampay.common.core.model.request.RegistrationEntryRequest;
import xyz.homapay.hampay.common.core.model.request.RegistrationSendSmsTokenRequest;
import xyz.homapay.hampay.common.core.model.request.TACAcceptRequest;
import xyz.homapay.hampay.common.core.model.request.TACRequest;
import xyz.homapay.hampay.common.core.model.request.TransactionListRequest;
import xyz.homapay.hampay.common.core.model.request.UnlinkUserRequest;
import xyz.homapay.hampay.common.core.model.request.UploadImageRequest;
import xyz.homapay.hampay.common.core.model.request.UserProfileRequest;
import xyz.homapay.hampay.common.core.model.response.ContactUsResponse;
import xyz.homapay.hampay.common.core.model.response.IBANConfirmationResponse;
import xyz.homapay.hampay.common.core.model.response.RegistrationSendSmsTokenResponse;
import xyz.homapay.hampay.common.core.model.response.TACAcceptResponse;
import xyz.homapay.hampay.common.core.model.response.dto.UserProfileDTO;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.activity.ChangeEmailPassActivity;
import xyz.homapay.hampay.mobile.android.activity.ChangeIbanPassActivity;
import xyz.homapay.hampay.mobile.android.activity.ChangeMemorableActivity;
import xyz.homapay.hampay.mobile.android.activity.ChangePassCodeActivity;
import xyz.homapay.hampay.mobile.android.activity.GuideDetailActivity;
import xyz.homapay.hampay.mobile.android.activity.HamPayLoginActivity;
import xyz.homapay.hampay.mobile.android.activity.MainActivity;
import xyz.homapay.hampay.mobile.android.activity.ProfileEntryActivity;
import xyz.homapay.hampay.mobile.android.activity.SMSVerificationActivity;
import xyz.homapay.hampay.mobile.android.activity.UnlinkPassActivity;
import xyz.homapay.hampay.mobile.android.activity.WelcomeActivity;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestCardProfile;
import xyz.homapay.hampay.mobile.android.async.RequestChangeEmail;
import xyz.homapay.hampay.mobile.android.async.RequestChangeMemorableWord;
import xyz.homapay.hampay.mobile.android.async.RequestChangePassCode;
import xyz.homapay.hampay.mobile.android.async.RequestContactHampayEnabled;
import xyz.homapay.hampay.mobile.android.async.RequestCredentialEntry;
import xyz.homapay.hampay.mobile.android.async.RequestHamPayBusiness;
import xyz.homapay.hampay.mobile.android.async.RequestIBANChange;
import xyz.homapay.hampay.mobile.android.async.RequestIBANConfirmation;
import xyz.homapay.hampay.mobile.android.async.RequestIllegalAppList;
import xyz.homapay.hampay.mobile.android.async.RequestLatestPayment;
import xyz.homapay.hampay.mobile.android.async.RequestLatestPurchase;
import xyz.homapay.hampay.mobile.android.async.RequestNewLogout;
import xyz.homapay.hampay.mobile.android.async.RequestRegistrationEntry;
import xyz.homapay.hampay.mobile.android.async.RequestRegistrationSendSmsToken;
import xyz.homapay.hampay.mobile.android.async.RequestSearchHamPayBusiness;
import xyz.homapay.hampay.mobile.android.async.RequestTAC;
import xyz.homapay.hampay.mobile.android.async.RequestTACAccept;
import xyz.homapay.hampay.mobile.android.async.RequestUnlinkUser;
import xyz.homapay.hampay.mobile.android.async.RequestUploadImage;
import xyz.homapay.hampay.mobile.android.async.RequestUserProfile;
import xyz.homapay.hampay.mobile.android.async.RequestUserTransaction;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.edittext.EmailTextWatcher;
import xyz.homapay.hampay.mobile.android.component.edittext.FacedEditText;
import xyz.homapay.hampay.mobile.android.firebase.LogEvent;
import xyz.homapay.hampay.mobile.android.firebase.app.AppEvent;
import xyz.homapay.hampay.mobile.android.firebase.service.ServiceEvent;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.CurrencyFormatter;
import xyz.homapay.hampay.mobile.android.util.EmailVerification;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;
import xyz.homapay.hampay.mobile.android.webservice.SecuredWebServices;

/**
 * Created by amir on 7/8/15.
 */
public class HamPayDialog {

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Activity activity;
    HamPayCustomDialog dialog;
    String serverKey = "";
    Rect rect = new Rect();
    private CurrencyFormatter currencyFormatter;

    public HamPayDialog(Activity activity){

        this.activity = activity;
        this.windowDisplayFrame();
        prefs = activity.getSharedPreferences(Constants.APP_PREFERENCE_NAME, activity.MODE_PRIVATE);
        editor = activity.getSharedPreferences(Constants.APP_PREFERENCE_NAME, activity.MODE_PRIVATE).edit();
        currencyFormatter = new CurrencyFormatter();
    }

    private void windowDisplayFrame(){
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rect);
    }


    public HamPayDialog(Activity activity, String serverKey){

        this.activity = activity;
        this.serverKey = serverKey;
        prefs = activity.getSharedPreferences(Constants.APP_PREFERENCE_NAME, activity.MODE_PRIVATE);
        editor = activity.getSharedPreferences(Constants.APP_PREFERENCE_NAME, activity.MODE_PRIVATE).edit();
    }

    public void exitRegistrationDialog(){

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_exit_registeration, null);

        FacedTextView continuation_registration = (FacedTextView) view.findViewById(R.id.continuation_registration);
        FacedTextView confirm_exit = (FacedTextView) view.findViewById(R.id.confirm_exit);

        continuation_registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        confirm_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.finish();
            }
        });

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showWaitingDialog(String hampayUser){

        dismisWaitingDialog();

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_waiting, null);
        FacedTextView waiting_text = (FacedTextView)view.findViewById(R.id.waiting_text);
        FacedTextView userName = (FacedTextView)view.findViewById(R.id.userName);
        if (hampayUser.length() != 0){
            userName.setText(hampayUser);
            waiting_text.setText(activity.getString(R.string.dialog_hampay_user_waiting));
        }
        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showHamPayCommunication(){

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_communication, null);
        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showTcPrivacyDialog(){

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_tc_privacy, null);
        FacedTextView tc_privacy_text = (FacedTextView) view.findViewById(R.id.tc_privacy_text);
        Spannable tcPrivacySpannable = new SpannableString(activity.getString(R.string.tc_privacy_text));
        ClickableSpan tcClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent intent = new Intent();
                intent.setClass(activity, GuideDetailActivity.class);
                intent.putExtra(Constants.WEB_PAGE_ADDRESS, Constants.HTTPS_SERVER_IP + "/users/tac-file");
                intent.putExtra(Constants.TAC_PRIVACY_TITLE, activity.getString(R.string.tac_title_activity));
                activity.startActivity(intent);
            }
        };
        tcPrivacySpannable.setSpan(tcClickableSpan, 3, 18, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tc_privacy_text.setText(tcPrivacySpannable);
        tc_privacy_text.setMovementMethod(LinkMovementMethod.getInstance());
        ClickableSpan privacySpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent intent = new Intent();
                intent.setClass(activity, GuideDetailActivity.class);
                intent.putExtra(Constants.WEB_PAGE_ADDRESS, Constants.HTTPS_SERVER_IP + "/users/privacy-file");
                intent.putExtra(Constants.TAC_PRIVACY_TITLE, activity.getString(R.string.privacy_title_activity));
                activity.startActivity(intent);
            }
        };

        tcPrivacySpannable.setSpan(privacySpan, 60, 92, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tc_privacy_text.setText(tcPrivacySpannable);
        tc_privacy_text.setMovementMethod(LinkMovementMethod.getInstance());


        FacedTextView tc_privacy_confirm = (FacedTextView) view.findViewById(R.id.tc_privacy_confirm);
        tc_privacy_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
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
        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }



    public void dismisWaitingDialog(){
        if (dialog != null && !activity.isFinishing()){
            if (dialog.isShowing())
                dialog.dismiss();
        }
    }

    public void showDisMatchPasswordDialog(){

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_dismatch_password, null);
        FacedTextView retry_password = (FacedTextView) view.findViewById(R.id.retry_password);
        retry_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showDisMatchMemorableDialog(){

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_dismatch_memorable, null);
        FacedTextView retry_memorable = (FacedTextView) view.findViewById(R.id.retry_memorable);
        retry_memorable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.finish();
            }
        });

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void fetchContactUsInfo(){
        ContactUsRequest contactUsRequest = new ContactUsRequest();
        contactUsRequest.setRequestUUID(UUID.randomUUID().toString());
        new HttpContactUs().execute(contactUsRequest);
    }

    public void showContactUsDialog(){

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
                    Intent callIntent = new Intent(Intent.ACTION_VIEW);
                    callIntent.setData(Uri.parse("tel:" + contactUsPhone));
                    activity.startActivity(callIntent);
                }
            }
        });

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    String contactUsMail = "";
    String contactUsPhone = "";
    ResponseMessage<ContactUsResponse> contactUsResponseResponseMessage = null;

    public class HttpContactUs extends AsyncTask<ContactUsRequest, Void, String> {

        @Override
        protected String doInBackground(ContactUsRequest... params) {

            SecuredWebServices webServices = new SecuredWebServices(activity, Constants.CONNECTION_TYPE);
            try {
                contactUsResponseResponseMessage = webServices.contactUsResponse(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (EncryptionException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
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

    private UserProfileDTO userProfileDTO;

    public class RequestTACAcceptResponseTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<TACAcceptResponse>>
    {

        @Override
        public void onTaskComplete(ResponseMessage<TACAcceptResponse> tacAcceptResponseMessage)
        {

            dismisWaitingDialog();
            ServiceEvent serviceName;
            LogEvent logEvent = new LogEvent(activity);

            if (tacAcceptResponseMessage != null) {

                if (tacAcceptResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    serviceName = ServiceEvent.TAC_ACCEPT_SUCCESS;
                    editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                    editor.commit();

                    userProfileDTO = tacAcceptResponseMessage.getService().getTacDTO().getUserProfile();

                    Intent intent = new Intent();
                    intent.setClass(activity, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(Constants.PENDING_PURCHASE_CODE, tacAcceptResponseMessage.getService().getTacDTO().getPurchaseProductCode());
                    intent.putExtra(Constants.PENDING_PAYMENT_CODE, tacAcceptResponseMessage.getService().getTacDTO().getPaymentProductCode());
                    intent.putExtra(Constants.PENDING_PURCHASE_COUNT, tacAcceptResponseMessage.getService().getTacDTO().getPendingPurchasesCount());
                    intent.putExtra(Constants.PENDING_PAYMENT_COUNT, tacAcceptResponseMessage.getService().getTacDTO().getPendingPaymentCount());
                    intent.putExtra(Constants.USER_PROFILE_DTO, userProfileDTO);
                    editor.putBoolean(Constants.FORCE_USER_PROFILE, false);
                    editor.commit();
                    activity.finish();
                    activity.startActivity(intent);

                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }

                }else if (tacAcceptResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    serviceName = ServiceEvent.TAC_ACCEPT_FAILURE;
                    forceLogout();
                }
                else {
                    serviceName = ServiceEvent.TAC_ACCEPT_FAILURE;
                    requestTACAccept = new RequestTACAccept(activity, new RequestTACAcceptResponseTaskCompleteListener());
                    showFailTACAcceeptRequestDialog(requestTACAccept, tacAcceptRequest,
                            tacAcceptResponseMessage.getService().getResultStatus().getCode(),
                            tacAcceptResponseMessage.getService().getResultStatus().getDescription());
                }
            }
            else {
                serviceName = ServiceEvent.TAC_ACCEPT_FAILURE;
                showFailTACAcceeptRequestDialog(requestTACAccept, tacAcceptRequest,
                        Constants.LOCAL_ERROR_CODE,
                        activity.getString(R.string.msg_fail_tac_accept_request));
            }
            logEvent.log(serviceName);

        }

        @Override
        public void onTaskPreRun() { }
    }

    public void showLogoutDialog(){

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_exit_app, null);

        FacedTextView exit_app_yes = (FacedTextView) view.findViewById(R.id.exit_app_yes);
        FacedTextView exit_app_no = (FacedTextView) view.findViewById(R.id.exit_app_no);

        exit_app_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogoutRequest logoutRequest = new LogoutRequest();
                RequestNewLogout requestNewLogout = new RequestNewLogout(activity, new RequestLoginTaskCompleteListener());
                requestNewLogout.execute(logoutRequest);
                dialog.dismiss();
                activity.finish();
            }
        });

        exit_app_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public class RequestLoginTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<LogoutResponse>>
    {
        public RequestLoginTaskCompleteListener(){
        }

        @Override
        public void onTaskComplete(ResponseMessage<LogoutResponse> logoutResponseResponseMessage)
        {
            if (logoutResponseResponseMessage != null) {
                AppEvent appEvent = AppEvent.LOGOUT;
                LogEvent logEvent = new LogEvent(activity);
                logEvent.log(appEvent);
                editor.remove(Constants.LOGIN_TOKEN_ID);
                editor.commit();
            }
        }

        @Override
        public void onTaskPreRun() {}
    }

    public void showRemovePasswordDialog(){

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_remove_password, null);

        FacedTextView re_registeration = (FacedTextView) view.findViewById(R.id.re_registeration);
        FacedTextView cancel_remove_password = (FacedTextView) view.findViewById(R.id.cancel_remove_password);

        re_registeration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor.clear().commit();
                editor.commit();

                dialog.dismiss();
                Intent intent = new Intent(activity, WelcomeActivity.class);
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

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showLoginFailDialog(Integer remainRetryCount){

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_login_fail, null);

        FacedTextView failedLoginText = (FacedTextView)view.findViewById(R.id.failedLoginText);



        FacedTextView login_retry = (FacedTextView) view.findViewById(R.id.login_retry);

        if (remainRetryCount != null) {
            if (remainRetryCount == 0) {
                login_retry.setVisibility(View.GONE);
                failedLoginText.setText(activity.getString(R.string.msg_locked_hampay_login));
            } else {
                failedLoginText.setText(activity.getString(R.string.msg_login_failure, new PersianEnglishDigit().E2P(remainRetryCount.toString())));
            }
        }


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

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }


    public void showIncorrectSMSVerification(){

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_incorrect_sms_verication, null);

        FacedTextView retry_sms_verification = (FacedTextView) view.findViewById(R.id.retry_sms_verification);

        retry_sms_verification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }



    public void showPreventRootDeviceDialog(){

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_prevent_root_device, null);

        FacedTextView prevent_rooted_device = (FacedTextView) view.findViewById(R.id.prevent_rooted_device);

        prevent_rooted_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.finish();
            }
        });

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showFailCardProfileDialog(final RequestCardProfile requestCardProfile,
                                          final CardProfileRequest cardProfileRequest,
                                          final String code,
                                          final String message){

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_card_info, null);

        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseMessage.setText((activity.getString(R.string.error_code, code) + "\n" + message));

        FacedTextView cancel_request = (FacedTextView)view.findViewById(R.id.cancel_request);

        cancel_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }


    public void showFailIllegalAppListDialog(final RequestIllegalAppList requestIllegalAppList,
                                             final IllegalAppListRequest illegalAppListRequest,
                                             final String code,
                                             final String message){

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_illegal_app_list, null);

        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseMessage.setText(activity.getString(R.string.error_code, code) + "\n" + message);

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

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showFailRegistrationEntryDialog(final RequestRegistrationEntry requestRegistrationEntry,
                                                final RegistrationEntryRequest registrationEntryRequest,
                                                final String code,
                                                final String message){

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_registration_entry, null);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);
        responseMessage.setText(activity.getString(R.string.error_code, code) + "\n" + message);

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

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showFailRegistrationSendSmsTokenDialog(final RequestRegistrationSendSmsToken requestRegistrationSendSmsToken,
                                                       final RegistrationSendSmsTokenRequest registrationEntryRequest,
                                                       final String code,
                                                       final String message){

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_registration_send_sms_token, null);

        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseMessage.setText(activity.getString(R.string.error_code, code) + "\n" + message);

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

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }


    public void showFailRegistrationVerifyMobileDialog(final String code,
                                                       final String message){
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_registration_verify_mobile_request, null);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);
        responseMessage.setText(activity.getString(R.string.error_code, code) + "\n" + message);
        FacedTextView retry_registration_verify_mobile = (FacedTextView) view.findViewById(R.id.retry_registration_verify_mobile);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);
        retry_registration_verify_mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
//                requestVerifyMobile.execute(registrationVerifyMobileRequest);
            }
        });
        cancel_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showFailUserProfileDialog(final RequestUserProfile requestUserProfile,
                                          final UserProfileRequest userProfileRequest,
                                          final String code,
                                          final String message){

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_user_profile, null);

        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);
        responseMessage.setText(activity.getString(R.string.error_code, code) + "\n" + message);

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

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }



    public void showFailMemorableEntryDialog(final RequestCredentialEntry requestMemorableWordEntry,
                                             final RegistrationCredentialsRequest registrationMemorableWordEntryRequest,
                                             final String code,
                                             final String message){

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_memorable_entry, null);

        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);
        responseMessage.setText(activity.getString(R.string.error_code, code) + "\n" + message);

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

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showFailTCRequestDialog(final RequestTAC requestTAC,
                                        final TACRequest tacRequest,
                                        final String code,
                                        final String message){

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_tac_request, null);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseMessage.setText(activity.getString(R.string.error_code, code) + "\n" + message);

        FacedTextView retry_tac_request = (FacedTextView) view.findViewById(R.id.retry_tac_request);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);


        retry_tac_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
//                if (requestTAC.getStatus() == AsyncTask.Status.FINISHED)
//                    requestTAC.execute(tacRequest);
            }
        });
        cancel_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.finish();
            }
        });

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }


    public void showFailTACAcceeptRequestDialog(final RequestTACAccept requestTACAccept,
                                                final TACAcceptRequest tacAcceptRequest,
                                                final String code,
                                                final String message){
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_tac_accept_request, null);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);
        responseMessage.setText(activity.getString(R.string.error_code, code) + "\n" + message);
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
                activity.finish();
            }
        });

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showSuccessChangeSettingDialog(final String message, final boolean forceChange){
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_success_change_setting, null);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);
        responseMessage.setText(message);
        FacedTextView success_change_setting = (FacedTextView) view.findViewById(R.id.success_change_setting);
        success_change_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (forceChange){
                    Intent intent = new Intent();
                    intent.setClass(activity, HamPayLoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                    activity.finish();
                    activity.startActivity(intent);
                }else {
                    dialog.dismiss();
                    activity.finish();
                }
            }
        });
        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showFailChangePassCodeDialog(final RequestChangePassCode requestChangePassCode,
                                             final ChangePassCodeRequest changePassCodeRequest,
                                             final String code,
                                             final String message){

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_change_pass_code, null);

        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);
        responseMessage.setText(activity.getString(R.string.error_code, code) + "\n" + message);
        FacedTextView retry_change_pass_code = (FacedTextView) view.findViewById(R.id.retry_change_pass_code);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);

//        if ((code.compareTo("1005") == 0) || (code.compareTo("۱۰۰۵") == 0) ){
//            retry_change_pass_code.setVisibility(View.INVISIBLE);
//        }

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

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }



    public void showFailChangeMemorableWordDialog(final String code, final String message){

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_change_memorable_word, null);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);
        responseMessage.setText(activity.getString(R.string.error_code, code) + "\n" + message);
        FacedTextView retry_change_memorable_word = (FacedTextView) view.findViewById(R.id.retry_change_memorable_word);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);

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

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }


    public void showFailUserTransactionDialog(final RequestUserTransaction requestUserTransaction,
                                              final TransactionListRequest transactionListRequest,
                                              final String code,
                                              final String message){
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_user_transaction, null);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);
        responseMessage.setText(activity.getString(R.string.error_code, code) + "\n" + message);
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
        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showFailBusinessListDialog(final RequestHamPayBusiness requestHamPayBusiness,
                                           final BusinessListRequest businessListRequest,
                                           final String code,
                                           final String message){
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_business_list, null);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);
        responseMessage.setText(activity.getString(R.string.error_code, code) + "\n" + message);

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

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showFailBusinessSearchListDialog(final RequestSearchHamPayBusiness requestSearchHamPayBusiness,
                                                 final BusinessSearchRequest businessSearchRequest,
                                                 final String code,
                                                 final String message){

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_business_search_list, null);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);
        responseMessage.setText(activity.getString(R.string.error_code, code) + "\n" + message);
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

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showFailContactsHamPayEnabledDialog(final RequestContactHampayEnabled requestContactHampayEnabled,
                                                    final ContactsHampayEnabledRequest contactsHampayEnabledRequest,
                                                    final String code,
                                                    final String message){

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_contacts_enabled, null);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);
        responseMessage.setText(activity.getString(R.string.error_code, code) + "\n" + message);

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

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showFailPaymentDialog(final String code,
                                      final String message){

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_payment, null);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);
        responseMessage.setText(activity.getString(R.string.error_code, (new PersianEnglishDigit()).E2P(code)) + "\n" + (new PersianEnglishDigit()).E2P(message));

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

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showIncorrectAmountDialog(Long MaxXferAmount, Long MinXferAmount){

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_violate_amount, null);

        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        responseMessage.setText(activity.getString(R.string.msg_incorrect_amount, new PersianEnglishDigit(currencyFormatter.format(MaxXferAmount)).E2P() + ""
                , new PersianEnglishDigit(currencyFormatter.format(MinXferAmount)).E2P()));

        FacedTextView payment_permission = (FacedTextView) view.findViewById(R.id.payment_permission);

        payment_permission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }


    public void showUnlinkDialog(){

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

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showFailUnlinkDialog(final String code, final String message){

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_unlink_user, null);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);
        responseMessage.setText(activity.getString(R.string.error_code, code) + "\n" + message);

        FacedTextView retry_unlink_user = (FacedTextView) view.findViewById(R.id.retry_unlink_user);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);


        retry_unlink_user.setOnClickListener(new View.OnClickListener() {
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

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showFailChangeEmail(final String code,
                                    final String message){

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_unlink_user, null);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);
        responseMessage.setText(activity.getString(R.string.error_code, code) + "\n" + message);

        FacedTextView retry_unlink_user = (FacedTextView) view.findViewById(R.id.retry_unlink_user);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);

        retry_unlink_user.setOnClickListener(new View.OnClickListener() {
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

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }


    public void showFailUploadImage(final RequestUploadImage requestUploadImage,
                                    final UploadImageRequest uploadImageRequest,
                                    final String code,
                                    final String message){

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_upload_image, null);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);
        responseMessage.setText(activity.getString(R.string.error_code, code) + "\n" + message);

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

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }


    public void showChangeEmail(){

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_change_email, null);

        ImageView emailIcon = (ImageView)view.findViewById(R.id.emailIcon);
        final FacedEditText emailValue = (FacedEditText)view.findViewById(R.id.emailValue);
        FacedTextView change_email = (FacedTextView)view.findViewById(R.id.change_email);
        FacedTextView cancel_change_email = (FacedTextView)view.findViewById(R.id.cancel_change_email);
        emailValue.addTextChangedListener(new EmailTextWatcher(emailValue, emailIcon));
        emailValue.setText(prefs.getString(Constants.REGISTERED_USER_EMAIL, ""));

        change_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = emailValue.getText().toString();
                if (new EmailVerification().isValid(userEmail.trim())) {
                    dialog.dismiss();
                    Intent intent = new Intent();
                    intent.setClass(activity, ChangeEmailPassActivity.class);
                    intent.putExtra(Constants.REGISTERED_USER_EMAIL, userEmail.trim());
                    activity.startActivity(intent);
                } else {
                    Toast.makeText(activity, activity.getString(R.string.msg_email_invalid), Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancel_change_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }


    RequestRegistrationSendSmsToken requestRegistrationSendSmsToken;
    RegistrationSendSmsTokenRequest registrationSendSmsTokenRequest;

    public void smsConfirmDialog(final String cellNumber, final String cardNumber){

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_sms_confirm, null);
        FacedTextView sms_user_notify = (FacedTextView) view.findViewById(R.id.sms_user_notify);
        sms_user_notify.setText(activity.getString(R.string.sms_verification_text, new PersianEnglishDigit().E2P(cellNumber)));
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

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
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
                    activity.startActivity(intent);
                }
                else {
                    requestRegistrationSendSmsToken = new RequestRegistrationSendSmsToken(activity, new RequestRegistrationSendSmsTokenTaskCompleteListener(cellNumber, cardNumber));
                    new HamPayDialog(activity).showFailRegistrationSendSmsTokenDialog(requestRegistrationSendSmsToken, registrationSendSmsTokenRequest,
                            registrationSendSmsTokenResponse.getService().getResultStatus().getCode(),
                            registrationSendSmsTokenResponse.getService().getResultStatus().getDescription());
                }

            }else {
                requestRegistrationSendSmsToken = new RequestRegistrationSendSmsToken(activity, new RequestRegistrationSendSmsTokenTaskCompleteListener(cellNumber, cardNumber));
                new HamPayDialog(activity).showFailRegistrationSendSmsTokenDialog(requestRegistrationSendSmsToken, registrationSendSmsTokenRequest,
                        Constants.LOCAL_ERROR_CODE,
                        activity.getString(R.string.mgs_fail_registration_send_sms_token));
            }
        }

        @Override
        public void onTaskPreRun() {
            showWaitingDialog("");
        }
    }

    public void pspSuccessResultDialog(String purchaseCode){

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_psp_success, null);

        FacedTextView request_payment_message = (FacedTextView) view.findViewById(R.id.request_payment_message);
        request_payment_message.setText(activity.getString(R.string.msg_success_pending_payment, new PersianEnglishDigit().E2P(purchaseCode)));
        FacedTextView confirmation = (FacedTextView) view.findViewById(R.id.confirmation);

        confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.finish();
            }
        });

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void pspFailResultDialog(String responseCode, String description){

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_ipg_failure, null);

        FacedTextView message = (FacedTextView)view.findViewById(R.id.message);
        FacedTextView confirmation = (FacedTextView) view.findViewById(R.id.confirmation);

        if (responseCode.length() > 0){
            message.setText("کد خطا: " + new PersianEnglishDigit().E2P(responseCode) + "\n" + description);
        }

        confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }


    public void ipgSuccessDialog(String purchaseCode){

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_ipg_success, null);

        FacedTextView request_payment_message = (FacedTextView) view.findViewById(R.id.request_payment_message);
        request_payment_message.setText(activity.getString(R.string.msg_business_payment_success, new PersianEnglishDigit().E2P(purchaseCode)));
        FacedTextView confirmation = (FacedTextView) view.findViewById(R.id.confirmation);

        confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.finish();
            }
        });

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void ipgFailDialog(){

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_ipg_failure, null);

        FacedTextView confirmation = (FacedTextView) view.findViewById(R.id.confirmation);

        confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.finish();
            }
        });

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }


    public void showFailPendingPurchaseDialog(final RequestLatestPurchase requestLatestPurchase,
                                              final LatestPurchaseRequest latestPurchaseRequest,
                                              final String code,
                                              final String message){

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_contacts_enabled, null);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);
        responseMessage.setText(activity.getString(R.string.error_code, code) + "\n" + message);

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

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showFailPurchaseInfoDialog(final String code, final String message){

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_contacts_enabled, null);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);
        responseMessage.setText(activity.getString(R.string.error_code, code) + "\n" + message);

        FacedTextView retry_contacts_enabled = (FacedTextView) view.findViewById(R.id.retry_contacts_enabled);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);


        retry_contacts_enabled.setOnClickListener(new View.OnClickListener() {
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

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showFailPendingPaymentDialog(final RequestLatestPayment requestLatestPayment,
                                             final LatestPaymentRequest latestPaymentRequest,
                                             final String code,
                                             final String message){

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_contacts_enabled, null);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);
        responseMessage.setText(activity.getString(R.string.error_code, code) + "\n" + message);

        FacedTextView retry_contacts_enabled = (FacedTextView) view.findViewById(R.id.retry_contacts_enabled);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);


        retry_contacts_enabled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
//                requestLatestPayment.execute(latestPaymentRequest);
            }
        });
        cancel_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.finish();
            }
        });

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }


    public void successPaymentRequestDialog(String requestCode){


        View view = activity.getLayoutInflater().inflate(R.layout.dialog_payment_request_success, null);

        FacedTextView request_payment_message = (FacedTextView) view.findViewById(R.id.request_payment_message);
        request_payment_message.setText(activity.getString(R.string.msg_success_payment_request, new PersianEnglishDigit().E2P(requestCode)));
        FacedTextView confirmation = (FacedTextView) view.findViewById(R.id.confirmation);

        confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent returnIntent = new Intent();
                returnIntent.putExtra(Constants.ACTIVITY_RESULT, ResultStatus.SUCCESS.ordinal());
                activity.setResult(Activity.RESULT_OK, returnIntent);
                activity.finish();
            }
        });

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void failurePaymentRequestDialog(final String code, final String message){
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_payment_request_failure, null);

        FacedTextView request_payment_message = (FacedTextView)view.findViewById(R.id.request_payment_message);
        request_payment_message.setText(code + "\n" + message);

        FacedTextView confirmation = (FacedTextView) view.findViewById(R.id.confirmation);

        confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.finish();
            }
        });

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showIBANConfirmationDialog(final String iban, final IBANConfirmationResponse ibanConfirmationResponse){

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
                Intent intent = new Intent(activity, ChangeIbanPassActivity.class);
                intent.putExtra(Constants.USER_IBAN, iban);
                activity.startActivity(intent);
            }
        });

        cancel_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.finish();
            }
        });

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showFailIBANChangeDialog(final String code, final String message){

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_request_iban_confirm, null);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);
        responseMessage.setText(activity.getString(R.string.error_code, code) + "\n" + message);

        FacedTextView retry_iban_request = (FacedTextView) view.findViewById(R.id.retry_iban_request);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);

        retry_iban_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
//                requestIBANChange.execute(ibanChangeRequest);
            }
        });

        cancel_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.finish();
            }
        });

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showFailIBANConfirmationDialog(final RequestIBANConfirmation requestIBANConfirmation,
                                               final IBANConfirmationRequest ibanConfirmationRequest,
                                               final String code,
                                               final String message){

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_request_iban_confirm, null);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);
        responseMessage.setText(activity.getString(R.string.error_code, code) + "\n" + message);

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

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showFailIBANConfirmationDialog(){

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_request_iban_confirm, null);
        FacedTextView responseMessage = (FacedTextView)view.findViewById(R.id.responseMessage);

        FacedTextView retry_iban_request = (FacedTextView) view.findViewById(R.id.retry_iban_request);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);

        retry_iban_request.setOnClickListener(new View.OnClickListener() {
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

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void removeImageFailDialog(){

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_remove_image_failure, null);

        FacedTextView confirmation = (FacedTextView) view.findViewById(R.id.confirmation);

        confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showNoNetwork(){

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_no_network, null);

        FacedTextView networkSetting = (FacedTextView) view.findViewById(R.id.network_setting);
        FacedTextView networkCancel = (FacedTextView) view.findViewById(R.id.network_cancel);

        networkSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.startActivity(new Intent(Settings.ACTION_SETTINGS));
            }
        });

        networkCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.finish();
            }
        });

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showUnknownIban(){
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_known_iban, null);
        FacedTextView unknown_iban_confirm = (FacedTextView) view.findViewById(R.id.unknown_iban_confirm);
        unknown_iban_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showFirstIpg(String userName){
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_first_ipg, null);
        FacedTextView message = (FacedTextView) view.findViewById(R.id.message);
        message.setText(userName + " " + "عزیز" + "\n" + activity.getString(R.string.first_ipg_loading));
        FacedTextView ipgConfirm = (FacedTextView) view.findViewById(R.id.ipg_confirm);
        ipgConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void preventPaymentRequest(){
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_pevent_payment_request, null);
        FacedTextView message = (FacedTextView)view.findViewById(R.id.message);


        Spannable tcPrivacySpannable = new SpannableString(activity.getString(R.string.prevent_payment_request));
        ClickableSpan tcClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent merchantIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.MERCHANT_DOCUMENT_URL));
                activity.startActivity(merchantIntent);
            }
        };
        tcPrivacySpannable.setSpan(tcClickableSpan, 17, 35, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        message.setText(tcPrivacySpannable);
        message.setMovementMethod(LinkMovementMethod.getInstance());
        ClickableSpan privacySpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", activity.getString(R.string.merchent_document_email), null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.merchant_id));
                emailIntent.putExtra(Intent.EXTRA_TEXT, activity.getString(R.string.merchant_id_document));
                activity.startActivity(Intent.createChooser(emailIntent, activity.getString(R.string.hampay_contact)));
            }
        };

        tcPrivacySpannable.setSpan(privacySpan, 122, 139, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        message.setText(tcPrivacySpannable);
        message.setMovementMethod(LinkMovementMethod.getInstance());

        FacedTextView preventPaymentRequest = (FacedTextView) view.findViewById(R.id.prevent_payment_request);
        preventPaymentRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void appUpdateDialog(final String storeUrl){

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_app_update, null);

        FacedTextView confirmation = (FacedTextView) view.findViewById(R.id.confirmation);

        confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(storeUrl));
                activity.startActivity(intent);
                dialog.dismiss();
            }
        });

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void forceChangePassDialog(final String cellNumber){

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_change_pass, null);

        FacedTextView confirmation = (FacedTextView) view.findViewById(R.id.confirmation);

        confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ChangePassCodeActivity.class);
                intent.putExtra(Constants.REGISTERED_CELL_NUMBER, cellNumber);
                activity.startActivity(intent);

            }
        });

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    private void forceLogout() {
        editor.remove(Constants.LOGIN_TOKEN_ID);
        editor.commit();
        Intent intent = new Intent();
        intent.setClass(activity, HamPayLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (activity != null) {
            activity.finish();
            activity.startActivity(intent);
        }
    }

}
