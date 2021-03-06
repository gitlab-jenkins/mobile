package xyz.homapay.hampay.mobile.android.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import xyz.homapay.hampay.common.common.encrypt.EncryptionException;
import xyz.homapay.hampay.common.common.request.LogoutRequest;
import xyz.homapay.hampay.common.common.response.LogoutResponse;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.enums.BizSortFactor;
import xyz.homapay.hampay.common.core.model.request.ChangePassCodeRequest;
import xyz.homapay.hampay.common.core.model.request.ContactUsRequest;
import xyz.homapay.hampay.common.core.model.request.ContactsHampayEnabledRequest;
import xyz.homapay.hampay.common.core.model.request.LatestPaymentRequest;
import xyz.homapay.hampay.common.core.model.request.LatestPurchaseRequest;
import xyz.homapay.hampay.common.core.model.request.RegistrationCredentialsRequest;
import xyz.homapay.hampay.common.core.model.request.RegistrationEntryRequest;
import xyz.homapay.hampay.common.core.model.request.TACRequest;
import xyz.homapay.hampay.common.core.model.request.TransactionListRequest;
import xyz.homapay.hampay.common.core.model.request.UploadImageRequest;
import xyz.homapay.hampay.common.core.model.request.UserProfileRequest;
import xyz.homapay.hampay.common.core.model.response.ContactUsResponse;
import xyz.homapay.hampay.common.core.model.response.RegistrationSendSmsTokenResponse;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.activity.ChangeEmailPassActivity;
import xyz.homapay.hampay.mobile.android.activity.ChangeMemorableActivity;
import xyz.homapay.hampay.mobile.android.activity.HamPayLoginActivity;
import xyz.homapay.hampay.mobile.android.activity.ProfileEntryActivity;
import xyz.homapay.hampay.mobile.android.activity.SMSVerificationActivity;
import xyz.homapay.hampay.mobile.android.activity.UnlinkPassActivity;
import xyz.homapay.hampay.mobile.android.adapter.charge.ChargeAdapter;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestChangePassCode;
import xyz.homapay.hampay.mobile.android.async.RequestContactHampayEnabled;
import xyz.homapay.hampay.mobile.android.async.RequestLatestPayment;
import xyz.homapay.hampay.mobile.android.async.RequestLatestPurchase;
import xyz.homapay.hampay.mobile.android.async.RequestNewLogout;
import xyz.homapay.hampay.mobile.android.async.RequestTAC;
import xyz.homapay.hampay.mobile.android.async.RequestUploadImage;
import xyz.homapay.hampay.mobile.android.async.RequestUserProfile;
import xyz.homapay.hampay.mobile.android.async.RequestUserTransaction;
import xyz.homapay.hampay.mobile.android.common.charge.ChargeAdapterModel;
import xyz.homapay.hampay.mobile.android.common.charge.RecyclerItemClickListener;
import xyz.homapay.hampay.mobile.android.common.messages.MessageSelectChargeAmount;
import xyz.homapay.hampay.mobile.android.common.messages.MessageSelectChargeType;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.edittext.EmailTextWatcher;
import xyz.homapay.hampay.mobile.android.component.edittext.FacedEditText;
import xyz.homapay.hampay.mobile.android.firebase.LogEvent;
import xyz.homapay.hampay.mobile.android.firebase.app.AppEvent;
import xyz.homapay.hampay.mobile.android.p.auth.RegisterEntry;
import xyz.homapay.hampay.mobile.android.p.auth.SMSSender;
import xyz.homapay.hampay.mobile.android.p.auth.SMSSenderImpl;
import xyz.homapay.hampay.mobile.android.p.auth.SMSSenderView;
import xyz.homapay.hampay.mobile.android.p.business.BusinessList;
import xyz.homapay.hampay.mobile.android.p.business.BusinessSearch;
import xyz.homapay.hampay.mobile.android.p.credential.CredentialEntry;
import xyz.homapay.hampay.mobile.android.util.AppManager;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.CurrencyFormatter;
import xyz.homapay.hampay.mobile.android.util.EmailVerification;
import xyz.homapay.hampay.mobile.android.util.ModelLayerImpl;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;
import xyz.homapay.hampay.mobile.android.util.font.FontFace;
import xyz.homapay.hampay.mobile.android.webservice.SecuredWebServices;

/**
 * Created by amir on 7/8/15.
 */
public class HamPayDialog {

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Activity activity;
    HamPayCustomDialog dialog;
    Rect rect = new Rect();
    String contactUsMail = "";
    String contactUsPhone = "";
    ResponseMessage<ContactUsResponse> contactUsResponseResponseMessage = null;
    private CurrencyFormatter currencyFormatter;
    private SMSSender smsSender = null;

    public HamPayDialog(Activity activity) {

        this.activity = activity;
        this.windowDisplayFrame();
        prefs = activity.getSharedPreferences(Constants.APP_PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = activity.getSharedPreferences(Constants.APP_PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
        currencyFormatter = new CurrencyFormatter();
    }

    private void windowDisplayFrame() {
        Activity parent = activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rect);
    }

    public void exitRegistrationDialog() {

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_exit_registeration, null);

        FacedTextView continuation_registration = (FacedTextView) view.findViewById(R.id.continuation_registration);
        FacedTextView confirm_exit = (FacedTextView) view.findViewById(R.id.confirm_exit);

        continuation_registration.setOnClickListener(v -> dialog.dismiss());

        confirm_exit.setOnClickListener(v -> {
            dialog.dismiss();
            activity.finish();
        });

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showWaitingDialog(String hampayUser) {

        try {
            dismisWaitingDialog();

            View view = activity.getLayoutInflater().inflate(R.layout.dialog_waiting, null);
            FacedTextView waiting_text = (FacedTextView) view.findViewById(R.id.waiting_text);
            FacedTextView userName = (FacedTextView) view.findViewById(R.id.userName);
            if (hampayUser.length() != 0) {
                userName.setText(hampayUser);
                waiting_text.setText(activity.getString(R.string.dialog_hampay_user_waiting));
            }
            view.setMinimumWidth((int) (rect.width() * 0.85f));
            if (!activity.isFinishing()) {
                dialog = new HamPayCustomDialog(view, activity, 0);
                dialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showHamPayCommunication() {

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_communication, null);
        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void dismisWaitingDialog() {
        if (dialog != null && !activity.isFinishing()) {
            if (dialog.isShowing())
                dialog.dismiss();
        }
    }

    public void showDisMatchPasswordDialog() {
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_dismatch_password, null);
        FacedTextView retry_password = (FacedTextView) view.findViewById(R.id.retry_password);
        retry_password.setOnClickListener(v -> dialog.dismiss());
        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showDisMatchMemorableDialog() {

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_dismatch_memorable, null);
        FacedTextView retry_memorable = (FacedTextView) view.findViewById(R.id.retry_memorable);
        retry_memorable.setOnClickListener(v -> {
            dialog.dismiss();
            activity.finish();
        });

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void fetchContactUsInfo() {
        ContactUsRequest contactUsRequest = new ContactUsRequest();
        contactUsRequest.setRequestUUID(UUID.randomUUID().toString());
        new HttpContactUs().execute(contactUsRequest);
    }

    public void showContactUsDialog() {

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_contact_us, null);

        FacedTextView send_message = (FacedTextView) view.findViewById(R.id.send_message);
        FacedTextView call_message = (FacedTextView) view.findViewById(R.id.call_message);

        send_message.setOnClickListener(v -> {
            dialog.dismiss();

            if (contactUsMail.length() != 0) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", contactUsMail, null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.app_name));
                emailIntent.putExtra(Intent.EXTRA_TEXT, activity.getString(R.string.insert_message));
                activity.startActivity(Intent.createChooser(emailIntent, activity.getString(R.string.hampay_contact)));
            }
        });

        call_message.setOnClickListener(v -> {
            dialog.dismiss();

            if (contactUsPhone.length() != 0) {
                Intent callIntent = new Intent(Intent.ACTION_VIEW);
                callIntent.setData(Uri.parse("tel:" + contactUsPhone));
                activity.startActivity(callIntent);
            }
        });

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showLogoutDialog() {

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_exit_app, null);

        FacedTextView exit_app_yes = (FacedTextView) view.findViewById(R.id.exit_app_yes);
        FacedTextView exit_app_no = (FacedTextView) view.findViewById(R.id.exit_app_no);

        exit_app_yes.setOnClickListener(v -> {
            LogoutRequest logoutRequest = new LogoutRequest();
            RequestNewLogout requestNewLogout = new RequestNewLogout(activity, new RequestLoginTaskCompleteListener());
            requestNewLogout.execute(logoutRequest);
            dialog.dismiss();
            activity.finish();
        });

        exit_app_no.setOnClickListener(v -> dialog.dismiss());

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showRemovePasswordDialog() {

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_remove_password, null);

        FacedTextView re_registeration = (FacedTextView) view.findViewById(R.id.re_registeration);
        FacedTextView cancel_remove_password = (FacedTextView) view.findViewById(R.id.cancel_remove_password);

        re_registeration.setOnClickListener(v -> {

            editor.clear().commit();
            editor.commit();

            dialog.dismiss();
            Intent intent = new Intent(activity, ProfileEntryActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            activity.finish();
            activity.startActivity(intent);
        });

        cancel_remove_password.setOnClickListener(v -> dialog.dismiss());

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showLoginFailDialog(Integer remainRetryCount) {

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_login_fail, null);

        FacedTextView failedLoginText = (FacedTextView) view.findViewById(R.id.failedLoginText);


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

        login_retry.setOnClickListener(v -> dialog.dismiss());

        remove_password.setOnClickListener(v -> {
            dialog.dismiss();
            showRemovePasswordDialog();
        });

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }


    public void showIncorrectSMSVerification() {

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_incorrect_sms_verication, null);

        FacedTextView retry_sms_verification = (FacedTextView) view.findViewById(R.id.retry_sms_verification);

        retry_sms_verification.setOnClickListener(v -> dialog.dismiss());

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }


    public void showPreventRootDeviceDialog() {

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_prevent_root_device, null);

        FacedTextView prevent_rooted_device = (FacedTextView) view.findViewById(R.id.prevent_rooted_device);

        prevent_rooted_device.setOnClickListener(v -> {
            dialog.dismiss();
            activity.finish();
        });

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showFailRegistrationEntryDialog(final RegisterEntry registerEntry,
                                                final RegistrationEntryRequest registrationEntryRequest,
                                                final String authToken,
                                                final String code,
                                                final String message) {

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_registration_entry, null);
        FacedTextView responseMessage = (FacedTextView) view.findViewById(R.id.responseMessage);
        responseMessage.setText(activity.getString(R.string.error_code, code) + "\n" + message);

        FacedTextView retry_registration_entry = (FacedTextView) view.findViewById(R.id.retry_registration_entry);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);


        retry_registration_entry.setOnClickListener(v -> {
            dialog.dismiss();
            registerEntry.register(registrationEntryRequest, authToken);
        });

        cancel_request.setOnClickListener(v -> dialog.dismiss());

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showFailRegistrationSendSmsTokenDialog(final SMSSender smsSender,
                                                       final String code,
                                                       final String message) {

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_registration_send_sms_token, null);

        FacedTextView responseMessage = (FacedTextView) view.findViewById(R.id.responseMessage);

        responseMessage.setText(activity.getString(R.string.error_code, code) + "\n" + message);

        FacedTextView retry_registration_sms_token = (FacedTextView) view.findViewById(R.id.retry_registration_sms_token);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);

        retry_registration_sms_token.setOnClickListener(v -> {
            dialog.dismiss();
            smsSender.send(AppManager.getRegisterIdToken(activity));
        });

        cancel_request.setOnClickListener(v -> dialog.dismiss());

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }


    public void showFailRegistrationVerifyMobileDialog(final String code,
                                                       final String message) {
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_registration_verify_mobile_request, null);
        FacedTextView responseMessage = (FacedTextView) view.findViewById(R.id.responseMessage);
        responseMessage.setText(activity.getString(R.string.error_code, code) + "\n" + message);
        FacedTextView retry_registration_verify_mobile = (FacedTextView) view.findViewById(R.id.retry_registration_verify_mobile);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);
        retry_registration_verify_mobile.setOnClickListener(v -> {
            dialog.dismiss();
//                requestVerifyMobile.execute(registrationVerifyMobileRequest);
        });
        cancel_request.setOnClickListener(v -> dialog.dismiss());
        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showFailUserProfileDialog(final RequestUserProfile requestUserProfile,
                                          final UserProfileRequest userProfileRequest,
                                          final String code,
                                          final String message) {

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_user_profile, null);

        FacedTextView responseMessage = (FacedTextView) view.findViewById(R.id.responseMessage);
        responseMessage.setText(activity.getString(R.string.error_code, code) + "\n" + message);

        FacedTextView retry_user_profile = (FacedTextView) view.findViewById(R.id.retry_user_profile);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);

        retry_user_profile.setOnClickListener(v -> {
            dialog.dismiss();
            requestUserProfile.execute(userProfileRequest);
        });

        cancel_request.setOnClickListener(v -> dialog.dismiss());

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }


    public void showFailMemorableEntryDialog(final CredentialEntry credentialEntry,
                                             final RegistrationCredentialsRequest registrationMemorableWordEntryRequest,
                                             final String authToken,
                                             final String code,
                                             final String message,
                                             final boolean permission) {

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_memorable_entry, null);

        FacedTextView responseMessage = (FacedTextView) view.findViewById(R.id.responseMessage);
        responseMessage.setText(activity.getString(R.string.error_code, code) + "\n" + message);

        FacedTextView retry_memorable_entry = (FacedTextView) view.findViewById(R.id.retry_memorable_entry);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);

        retry_memorable_entry.setOnClickListener(v -> {
            dialog.dismiss();
            credentialEntry.credential(registrationMemorableWordEntryRequest, authToken, permission);
        });

        cancel_request.setOnClickListener(v -> dialog.dismiss());

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showFailTCRequestDialog(final RequestTAC requestTAC,
                                        final TACRequest tacRequest,
                                        final String code,
                                        final String message) {

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_tac_request, null);
        FacedTextView responseMessage = (FacedTextView) view.findViewById(R.id.responseMessage);

        responseMessage.setText(activity.getString(R.string.error_code, code) + "\n" + message);

        FacedTextView retry_tac_request = (FacedTextView) view.findViewById(R.id.retry_tac_request);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);


        retry_tac_request.setOnClickListener(v -> {
            dialog.dismiss();
//                if (requestTAC.getStatus() == AsyncTask.Status.FINISHED)
//                    requestTAC.execute(tacRequest);
        });
        cancel_request.setOnClickListener(v -> {
            dialog.dismiss();
            activity.finish();
        });

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }


    public void showSuccessChangeSettingDialog(final String message, final boolean forceChange) {
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_success_change_setting, null);
        FacedTextView responseMessage = (FacedTextView) view.findViewById(R.id.responseMessage);
        responseMessage.setText(message);
        FacedTextView success_change_setting = (FacedTextView) view.findViewById(R.id.success_change_setting);
        success_change_setting.setOnClickListener(v -> {
            if (forceChange) {
                Intent intent = new Intent();
                intent.setClass(activity, HamPayLoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                activity.finish();
                activity.startActivity(intent);
            } else {
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

    public void showSuccessFriendsInvitation(Runnable job) {
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_success_friends_invitation, null);
        FacedTextView btnOk = (FacedTextView) view.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(v -> job.run());
        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showFailChangePassCodeDialog(final RequestChangePassCode requestChangePassCode,
                                             final ChangePassCodeRequest changePassCodeRequest,
                                             final String code,
                                             final String message) {

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_change_pass_code, null);

        FacedTextView responseMessage = (FacedTextView) view.findViewById(R.id.responseMessage);
        responseMessage.setText(activity.getString(R.string.error_code, code) + "\n" + message);
        FacedTextView retry_change_pass_code = (FacedTextView) view.findViewById(R.id.retry_change_pass_code);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);

//        if ((code.compareTo("1005") == 0) || (code.compareTo("۱۰۰۵") == 0) ){
//            retry_change_pass_code.setVisibility(View.INVISIBLE);
//        }

        retry_change_pass_code.setOnClickListener(v -> dialog.dismiss());
        cancel_request.setOnClickListener(v -> {
            dialog.dismiss();
            activity.finish();
        });

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }


    public void showFailChangeMemorableWordDialog(final String code, final String message) {

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_change_memorable_word, null);
        FacedTextView responseMessage = (FacedTextView) view.findViewById(R.id.responseMessage);
        responseMessage.setText(activity.getString(R.string.error_code, code) + "\n" + message);
        FacedTextView retry_change_memorable_word = (FacedTextView) view.findViewById(R.id.retry_change_memorable_word);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);

        retry_change_memorable_word.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent();
            intent.setClass(activity, ChangeMemorableActivity.class);
            activity.startActivity(intent);
            activity.finish();
        });
        cancel_request.setOnClickListener(v -> {
            dialog.dismiss();
            activity.finish();
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
                                              final String message) {
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_user_transaction, null);
        FacedTextView responseMessage = (FacedTextView) view.findViewById(R.id.responseMessage);
        responseMessage.setText(activity.getString(R.string.error_code, code) + "\n" + message);
        FacedTextView retry_user_transation = (FacedTextView) view.findViewById(R.id.retry_user_transation);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);
        retry_user_transation.setOnClickListener(v -> {
            dialog.dismiss();
            if (requestUserTransaction.getStatus() == AsyncTask.Status.FINISHED)
                requestUserTransaction.execute(transactionListRequest);
        });
        cancel_request.setOnClickListener(v -> dialog.dismiss());
        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showFailBusinessListDialog(final BusinessList businessList,
                                           final BizSortFactor sortFactor,
                                           final String code,
                                           final String message) {
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_business_list, null);
        FacedTextView responseMessage = (FacedTextView) view.findViewById(R.id.responseMessage);
        responseMessage.setText(activity.getString(R.string.error_code, code) + "\n" + message);

        FacedTextView retry_business_list = (FacedTextView) view.findViewById(R.id.retry_business_list);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);


        retry_business_list.setOnClickListener(v -> {
            dialog.dismiss();
            businessList.load(sortFactor);
        });
        cancel_request.setOnClickListener(v -> dialog.dismiss());

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showFailBusinessSearchListDialog(final BusinessSearch businessSearch,
                                                 final String searchTerm,
                                                 final BizSortFactor sortFactor,
                                                 final String code,
                                                 final String message) {

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_business_search_list, null);
        FacedTextView responseMessage = (FacedTextView) view.findViewById(R.id.responseMessage);
        responseMessage.setText(activity.getString(R.string.error_code, code) + "\n" + message);
        FacedTextView retry_business_search_list = (FacedTextView) view.findViewById(R.id.retry_business_search_list);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);
        retry_business_search_list.setOnClickListener(v -> {
            dialog.dismiss();
            businessSearch.search(searchTerm, sortFactor, true);
        });
        cancel_request.setOnClickListener(v -> dialog.dismiss());

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showFailContactsHamPayEnabledDialog(final RequestContactHampayEnabled requestContactHampayEnabled,
                                                    final ContactsHampayEnabledRequest contactsHampayEnabledRequest,
                                                    final String code,
                                                    final String message) {

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_contacts_enabled, null);
        FacedTextView responseMessage = (FacedTextView) view.findViewById(R.id.responseMessage);
        responseMessage.setText(activity.getString(R.string.error_code, code) + "\n" + message);

        FacedTextView retry_contacts_enabled = (FacedTextView) view.findViewById(R.id.retry_contacts_enabled);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);


        retry_contacts_enabled.setOnClickListener(v -> {
            dialog.dismiss();
            requestContactHampayEnabled.execute(contactsHampayEnabledRequest);
        });
        cancel_request.setOnClickListener(v -> dialog.dismiss());

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showFailPaymentDialog(final String code,
                                      final String message) {

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_payment, null);
        FacedTextView responseMessage = (FacedTextView) view.findViewById(R.id.responseMessage);
        responseMessage.setText(activity.getString(R.string.error_code, (new PersianEnglishDigit()).E2P(code)) + "\n" + (new PersianEnglishDigit()).E2P(message));

        FacedTextView retry_payment = (FacedTextView) view.findViewById(R.id.retry_payment);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);

        retry_payment.setOnClickListener(v -> dialog.dismiss());
        cancel_request.setOnClickListener(v -> {
            dialog.dismiss();
            activity.finish();
        });

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showIncorrectAmountDialog(Long MaxXferAmount, Long MinXferAmount) {

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_violate_amount, null);

        FacedTextView responseMessage = (FacedTextView) view.findViewById(R.id.responseMessage);

        responseMessage.setText(activity.getString(R.string.msg_incorrect_amount, new PersianEnglishDigit().E2P(currencyFormatter.format(MaxXferAmount)) + ""
                , new PersianEnglishDigit().E2P(currencyFormatter.format(MinXferAmount))));

        FacedTextView payment_permission = (FacedTextView) view.findViewById(R.id.payment_permission);

        payment_permission.setOnClickListener(v -> dialog.dismiss());

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }


    public void showUnlinkDialog() {

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_unlink, null);

        FacedTextView unlink_confirm = (FacedTextView) view.findViewById(R.id.unlink_confirm);
        FacedTextView unlink_disconfirm = (FacedTextView) view.findViewById(R.id.unlink_disconfirm);

        unlink_confirm.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent();
            intent.setClass(activity, UnlinkPassActivity.class);
            activity.startActivity(intent);
        });

        unlink_disconfirm.setOnClickListener(v -> dialog.dismiss());

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showFailUnlinkDialog(final String code, final String message) {

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_unlink_user, null);
        FacedTextView responseMessage = (FacedTextView) view.findViewById(R.id.responseMessage);
        responseMessage.setText(activity.getString(R.string.error_code, code) + "\n" + message);

        FacedTextView retry_unlink_user = (FacedTextView) view.findViewById(R.id.retry_unlink_user);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);


        retry_unlink_user.setOnClickListener(v -> dialog.dismiss());
        cancel_request.setOnClickListener(v -> {
            dialog.dismiss();
            activity.finish();
        });

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showFailChangeEmail(final String code,
                                    final String message) {

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_unlink_user, null);
        FacedTextView responseMessage = (FacedTextView) view.findViewById(R.id.responseMessage);
        responseMessage.setText(activity.getString(R.string.error_code, code) + "\n" + message);

        FacedTextView retry_unlink_user = (FacedTextView) view.findViewById(R.id.retry_unlink_user);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);

        retry_unlink_user.setOnClickListener(v -> dialog.dismiss());
        cancel_request.setOnClickListener(v -> {
            dialog.dismiss();
            activity.finish();
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
                                    final String message) {

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_upload_image, null);
        FacedTextView responseMessage = (FacedTextView) view.findViewById(R.id.responseMessage);
        responseMessage.setText(activity.getString(R.string.error_code, code) + "\n" + message);

        FacedTextView retry_upload_image = (FacedTextView) view.findViewById(R.id.retry_upload_image);
        FacedTextView cancel_upload_image = (FacedTextView) view.findViewById(R.id.cancel_upload_image);


        retry_upload_image.setOnClickListener(v -> {
            dialog.dismiss();
            if (requestUploadImage.getStatus() == AsyncTask.Status.FINISHED)
                requestUploadImage.execute(uploadImageRequest);
        });
        cancel_upload_image.setOnClickListener(v -> dialog.dismiss());

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }


    public void showChangeEmail() {

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_change_email, null);

        ImageView emailIcon = (ImageView) view.findViewById(R.id.emailIcon);
        final FacedEditText emailValue = (FacedEditText) view.findViewById(R.id.emailValue);
        FacedTextView change_email = (FacedTextView) view.findViewById(R.id.change_email);
        FacedTextView cancel_change_email = (FacedTextView) view.findViewById(R.id.cancel_change_email);
        emailValue.addTextChangedListener(new EmailTextWatcher(emailValue, emailIcon));
        emailValue.setText(prefs.getString(Constants.REGISTERED_USER_EMAIL, ""));

        change_email.setOnClickListener(v -> {
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
        });

        cancel_change_email.setOnClickListener(v -> dialog.dismiss());


        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void smsConfirmDialog(final String cellNumber) {

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_sms_confirm, null);
        FacedTextView sms_user_notify = (FacedTextView) view.findViewById(R.id.sms_user_notify);
        sms_user_notify.setText(activity.getString(R.string.sms_verification_text, new PersianEnglishDigit().E2P(cellNumber)));
        FacedTextView confirmation = (FacedTextView) view.findViewById(R.id.confirmation);
        FacedTextView dis_confirmation = (FacedTextView) view.findViewById(R.id.dis_confirmation);

        dis_confirmation.setOnClickListener(v -> dialog.dismiss());

        view.setMinimumWidth((int) (rect.width() * 0.85f));

        final HamPayDialog dlg = new HamPayDialog(activity);
        confirmation.setOnClickListener(v -> {
            dialog.dismiss();
            smsSender = new SMSSenderImpl(new ModelLayerImpl(activity), new SMSSenderView() {
                @Override
                public void onSMSSent(boolean state, ResponseMessage<RegistrationSendSmsTokenResponse> data, String message) {
                    if (state) {
                        if (data != null) {
                            if (data.getService().getResultStatus() == ResultStatus.SUCCESS) {
                                Intent intent = new Intent();
                                intent.setClass(activity, SMSVerificationActivity.class);
                                intent.putExtra(Constants.REGISTERED_CELL_NUMBER, cellNumber);
                                activity.startActivity(intent);
                            } else {
                                new HamPayDialog(activity).showFailRegistrationSendSmsTokenDialog(smsSender, data.getService().getResultStatus().getCode(), data.getService().getResultStatus().getDescription());
                            }

                        }
                    } else {
                        dlg.showFailRegistrationSendSmsTokenDialog(smsSender, activity.getString(R.string.err_general), activity.getString(R.string.err_general_sms_text));
                    }
                }

                @Override
                public void showProgress() {
                    dlg.showWaitingDialog("");
                }

                @Override
                public void cancelProgress() {
                    dlg.dismisWaitingDialog();
                }

                @Override
                public void onError() {
                    try {
                        dlg.dismisWaitingDialog();
                        Toast.makeText(activity, activity.getString(R.string.err_general_sms_text), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            smsSender.send(AppManager.getRegisterIdToken(activity));
        });

        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void pspFailResultDialog(String responseCode, String description) {

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_ipg_failure, null);

        FacedTextView message = (FacedTextView) view.findViewById(R.id.message);
        FacedTextView confirmation = (FacedTextView) view.findViewById(R.id.confirmation);

        if (responseCode.length() > 0) {
            message.setText("کد خطا: " + new PersianEnglishDigit().E2P(responseCode) + "\n" + description);
        }

        confirmation.setOnClickListener(v -> dialog.dismiss());

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }


    public void ipgFailDialog() {

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_ipg_failure, null);

        FacedTextView confirmation = (FacedTextView) view.findViewById(R.id.confirmation);

        confirmation.setOnClickListener(v -> {
            dialog.dismiss();
            activity.finish();
        });

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }

        dialog.setOnDismissListener(dialog1 -> {
            activity.finish();
        });

    }

    public void showFailPendingPurchaseDialog(final RequestLatestPurchase requestLatestPurchase,
                                              final LatestPurchaseRequest latestPurchaseRequest,
                                              final String code,
                                              final String message) {

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_contacts_enabled, null);
        FacedTextView responseMessage = (FacedTextView) view.findViewById(R.id.responseMessage);
        responseMessage.setText(activity.getString(R.string.error_code, code) + "\n" + message);

        FacedTextView retry_contacts_enabled = (FacedTextView) view.findViewById(R.id.retry_contacts_enabled);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);

        retry_contacts_enabled.setOnClickListener(v -> {
            dialog.dismiss();
            requestLatestPurchase.execute(latestPurchaseRequest);
        });
        cancel_request.setOnClickListener(v -> {
            dialog.dismiss();
            activity.finish();
        });

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showFailPurchaseInfoDialog(final String code, final String message) {

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_contacts_enabled, null);
        FacedTextView responseMessage = (FacedTextView) view.findViewById(R.id.responseMessage);
        responseMessage.setText(activity.getString(R.string.error_code, code) + "\n" + message);

        FacedTextView retry_contacts_enabled = (FacedTextView) view.findViewById(R.id.retry_contacts_enabled);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);


        retry_contacts_enabled.setOnClickListener(v -> dialog.dismiss());
        cancel_request.setOnClickListener(v -> {
            dialog.dismiss();
            activity.finish();
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
                                             final String message) {

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_contacts_enabled, null);
        FacedTextView responseMessage = (FacedTextView) view.findViewById(R.id.responseMessage);
        responseMessage.setText(activity.getString(R.string.error_code, code) + "\n" + message);

        FacedTextView retry_contacts_enabled = (FacedTextView) view.findViewById(R.id.retry_contacts_enabled);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);


        retry_contacts_enabled.setOnClickListener(v -> {
            dialog.dismiss();
        });
        cancel_request.setOnClickListener(v -> {
            dialog.dismiss();
            activity.finish();
        });

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showFailPendingPaymentDialog(final String code,
                                             final String message) {

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_contacts_enabled, null);
        FacedTextView responseMessage = (FacedTextView) view.findViewById(R.id.responseMessage);
        responseMessage.setText(activity.getString(R.string.error_code, code) + "\n" + message);

        FacedTextView retry_contacts_enabled = (FacedTextView) view.findViewById(R.id.retry_contacts_enabled);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);


        retry_contacts_enabled.setOnClickListener(v -> {
            dialog.dismiss();
//                requestLatestPayment.execute(latestPaymentRequest);
        });
        cancel_request.setOnClickListener(v -> {
            dialog.dismiss();
            activity.finish();
        });

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void successPaymentRequestDialog(String requestCode) {


        View view = activity.getLayoutInflater().inflate(R.layout.dialog_payment_request_success, null);

        FacedTextView request_payment_message = (FacedTextView) view.findViewById(R.id.request_payment_message);
        request_payment_message.setText(activity.getString(R.string.msg_success_payment_request, new PersianEnglishDigit().E2P(requestCode)));
        FacedTextView confirmation = (FacedTextView) view.findViewById(R.id.confirmation);

        confirmation.setOnClickListener(v -> {
            dialog.dismiss();
            Intent returnIntent = new Intent();
            returnIntent.putExtra(Constants.ACTIVITY_RESULT, ResultStatus.SUCCESS.ordinal());
            activity.setResult(Activity.RESULT_OK, returnIntent);
            activity.finish();
        });

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void failurePaymentRequestDialog(final String code, final String message) {
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_payment_request_failure, null);

        FacedTextView request_payment_message = (FacedTextView) view.findViewById(R.id.request_payment_message);
        request_payment_message.setText(code + "\n" + message);

        FacedTextView confirmation = (FacedTextView) view.findViewById(R.id.confirmation);

        confirmation.setOnClickListener(v -> {
            dialog.dismiss();
            activity.finish();
        });

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showFailIBANChangeDialog(final String code, final String message) {

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_request_iban_confirm, null);
        FacedTextView responseMessage = (FacedTextView) view.findViewById(R.id.responseMessage);
        responseMessage.setText(activity.getString(R.string.error_code, code) + "\n" + message);

        FacedTextView retry_iban_request = (FacedTextView) view.findViewById(R.id.retry_iban_request);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);

        retry_iban_request.setOnClickListener(v -> dialog.dismiss());

        cancel_request.setOnClickListener(v -> {
            dialog.dismiss();
            activity.finish();
        });

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showFailBillInfoDialog(final String code, final String message) {

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_bill, null);
        FacedTextView responseMessage = (FacedTextView) view.findViewById(R.id.responseMessage);
        responseMessage.setText(activity.getString(R.string.error_code, code) + "\n" + message);

        FacedTextView retry = (FacedTextView) view.findViewById(R.id.retry);
        FacedTextView cancel = (FacedTextView) view.findViewById(R.id.cancel);

        retry.setOnClickListener(v -> dialog.dismiss());

        cancel.setOnClickListener(v -> {
            dialog.dismiss();
            activity.finish();
        });

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void removeImageFailDialog() {

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_remove_image_failure, null);

        FacedTextView confirmation = (FacedTextView) view.findViewById(R.id.confirmation);

        confirmation.setOnClickListener(v -> dialog.dismiss());

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showNoNetwork() {

        new Handler(Looper.getMainLooper()).post(() -> {
            View view = activity.getLayoutInflater().inflate(R.layout.dialog_no_network, null);

            FacedTextView networkSetting = (FacedTextView) view.findViewById(R.id.network_setting);
            FacedTextView networkCancel = (FacedTextView) view.findViewById(R.id.network_cancel);

            networkSetting.setOnClickListener(v -> {
                try {
                    dialog.dismiss();
                    activity.startActivity(new Intent(Settings.ACTION_SETTINGS));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            networkCancel.setOnClickListener(v -> {
                dialog.dismiss();
                activity.finish();
            });

            view.setMinimumWidth((int) (rect.width() * 0.85f));
            if (!activity.isFinishing()) {
                dialog = new HamPayCustomDialog(view, activity, 0);
                dialog.show();
            }
        });
    }

    public void showUnknownIban() {
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_known_iban, null);
        FacedTextView unknown_iban_confirm = (FacedTextView) view.findViewById(R.id.unknown_iban_confirm);
        unknown_iban_confirm.setOnClickListener(v -> dialog.dismiss());
        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showFirstIpg(String userName) {
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_first_ipg, null);
        FacedTextView message = (FacedTextView) view.findViewById(R.id.message);
        message.setText(userName + " " + "عزیز" + "\n" + activity.getString(R.string.first_ipg_loading));
        FacedTextView ipgConfirm = (FacedTextView) view.findViewById(R.id.ipg_confirm);
        ipgConfirm.setOnClickListener(v -> dialog.dismiss());
        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void preventPaymentRequest() {
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_pevent_payment_request, null);
        FacedTextView message = (FacedTextView) view.findViewById(R.id.message);


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
        preventPaymentRequest.setOnClickListener(v -> dialog.dismiss());
        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void appUpdateDialog(final String storeUrl) {

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_app_update, null);

        FacedTextView confirmation = (FacedTextView) view.findViewById(R.id.confirmation);

        confirmation.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(storeUrl));
            activity.startActivity(intent);
            dialog.dismiss();
        });

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showChargeTypeChooser(Context ctx, ArrayList<ChargeAdapterModel> itemsAdapter) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((AppCompatActivity) ctx).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        LinearLayoutManager manager = new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false);
        ChargeAdapter adapter = new ChargeAdapter(ctx, itemsAdapter);
        View view = activity.getLayoutInflater().inflate(R.layout.dlg_charge_chooser, null);
        view.setMinimumWidth((int) (metrics.widthPixels * 0.85f));
        dialog = new HamPayCustomDialog(view, activity, 0);
        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.main_view);
        FacedTextView title = (FacedTextView) dialog.findViewById(R.id.tvTitle);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(ctx, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                dialog.cancel();
                EventBus.getDefault().post(new MessageSelectChargeType(position, itemsAdapter.get(position).getType(), itemsAdapter.get(position).getDesc()));
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
        title.setText(activity.getString(R.string.type_charge));
        dialog.show();
    }

    public void showChargeAmountChooser(Context ctx, List<String> items) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((AppCompatActivity) ctx).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        LinearLayoutManager manager = new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false);
        ArrayList<ChargeAdapterModel> itemsAdapter = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            ChargeAdapterModel model = new ChargeAdapterModel(i, "", items.get(i));
            itemsAdapter.add(model);
        }
        ChargeAdapter adapter = new ChargeAdapter(ctx, itemsAdapter, FontFace.getInstance(ctx).getVAZIR());
        View view = activity.getLayoutInflater().inflate(R.layout.dlg_charge_chooser, null);
        view.setMinimumWidth((int) (metrics.widthPixels * 0.85f));
        dialog = new HamPayCustomDialog(view, activity, 0);
        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.main_view);
        FacedTextView title = (FacedTextView) dialog.findViewById(R.id.tvTitle);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(ctx, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                dialog.cancel();
                EventBus.getDefault().post(new MessageSelectChargeAmount(adapter.getAmount(position) + "", position));
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
        title.setText(activity.getString(R.string.amount_charge));
        dialog.show();
    }

    public void showErrorGeneral() {
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_fail_contacts_enabled, null);

        FacedTextView retry_contacts_enabled = (FacedTextView) view.findViewById(R.id.retry_contacts_enabled);
        FacedTextView cancel_request = (FacedTextView) view.findViewById(R.id.cancel_request);

        retry_contacts_enabled.setOnClickListener(v -> {
            dialog.dismiss();
        });
        cancel_request.setOnClickListener(v -> {
            dialog.dismiss();
            activity.finish();
        });

        view.setMinimumWidth((int) (rect.width() * 0.85f));
        if (!activity.isFinishing()) {
            dialog = new HamPayCustomDialog(view, activity, 0);
            dialog.show();
        }
    }

    public void showNoServerConnection() {
        new Handler(Looper.getMainLooper()).post(() -> {
            View view = activity.getLayoutInflater().inflate(R.layout.dialog_no_server_connection, null);

            FacedTextView btnOk = (FacedTextView) view.findViewById(R.id.btnOk);

            btnOk.setOnClickListener(v -> {
                try {
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            view.setMinimumWidth((int) (rect.width() * 0.85f));
            if (!activity.isFinishing()) {
                dialog = new HamPayCustomDialog(view, activity, 0);
                dialog.show();
            }
        });
    }

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
            if (contactUsResponseResponseMessage != null) {
                contactUsMail = contactUsResponseResponseMessage.getService().getEmailAddress();
                contactUsPhone = contactUsResponseResponseMessage.getService().getPhoneNumber();
                showContactUsDialog();
            } else {
                Toast.makeText(activity, activity.getString(R.string.hampay_contact_failed), Toast.LENGTH_LONG).show();
            }


        }
    }

    public class RequestLoginTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<LogoutResponse>> {
        public RequestLoginTaskCompleteListener() {
        }

        @Override
        public void onTaskComplete(ResponseMessage<LogoutResponse> logoutResponseResponseMessage) {
            if (logoutResponseResponseMessage != null) {
                AppEvent appEvent = AppEvent.LOGOUT;
                LogEvent logEvent = new LogEvent(activity);
                logEvent.log(appEvent);
                editor.remove(Constants.LOGIN_TOKEN_ID);
                editor.commit();
            }
        }

        @Override
        public void onTaskPreRun() {
        }
    }
}
