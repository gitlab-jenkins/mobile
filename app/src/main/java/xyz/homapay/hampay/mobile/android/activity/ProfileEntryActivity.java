package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.stephentuso.welcome.WelcomeActivity;
import com.stephentuso.welcome.WelcomeHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.RegistrationEntryRequest;
import xyz.homapay.hampay.common.core.model.response.RegistrationEntryResponse;
import xyz.homapay.hampay.mobile.android.Manifest;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.edittext.EmailTextWatcher;
import xyz.homapay.hampay.mobile.android.component.edittext.FacedEditText;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.dialog.permission.ActionPermission;
import xyz.homapay.hampay.mobile.android.dialog.permission.PermissionDeviceDialog;
import xyz.homapay.hampay.mobile.android.firebase.LogEvent;
import xyz.homapay.hampay.mobile.android.firebase.app.AppEvent;
import xyz.homapay.hampay.mobile.android.firebase.service.ServiceEvent;
import xyz.homapay.hampay.mobile.android.model.NotificationMessageType;
import xyz.homapay.hampay.mobile.android.p.auth.RegisterEntry;
import xyz.homapay.hampay.mobile.android.p.auth.RegisterEntryImpl;
import xyz.homapay.hampay.mobile.android.p.auth.RegisterEntryView;
import xyz.homapay.hampay.mobile.android.permission.PermissionListener;
import xyz.homapay.hampay.mobile.android.permission.RequestPermissions;
import xyz.homapay.hampay.mobile.android.util.AppManager;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.ModelLayerImpl;
import xyz.homapay.hampay.mobile.android.util.NationalCodeVerification;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;
import xyz.homapay.hampay.mobile.android.util.PreferencesManager;
import xyz.homapay.hampay.mobile.android.util.RootUtil;

public class ProfileEntryActivity extends ActivityParentBase implements PermissionDeviceDialog.PermissionDeviceDialogListener, RegisterEntryView {

    private static final int REQUEST_WELCOME_SCREEN_RESULT = 13;
    private final Handler handler = new Handler();
    @BindView(R.id.keepOn_button)
    FacedTextView keepOn_button;
    @BindView(R.id.cellNumberValue)
    FacedEditText cellNumberValue;
    @BindView(R.id.cellNumberIcon)
    ImageView cellNumberIcon;
    @BindView(R.id.nationalCodeValue)
    FacedEditText nationalCodeValue;
    @BindView(R.id.nationalCodeIcon)
    ImageView nationalCodeIcon;
    @BindView(R.id.userNameFamilyIcon)
    ImageView userNameFamilyIcon;
    @BindView(R.id.userNameFamily)
    FacedEditText userNameFamily;
    @BindView(R.id.emailValue)
    FacedEditText emailValue;
    @BindView(R.id.emailIcon)
    ImageView emailIcon;
    @BindView(R.id.tac_privacy_text)
    FacedTextView tac_privacy_text;
    @BindView(R.id.confirmTacPrivacy)
    CheckBox confirmTacPrivacy;
    private Activity activity;
    private PersianEnglishDigit persianEnglishDigit;
    private boolean cellNumberIsValid = false;
    private boolean nationalCodeIsValid = false;
    private boolean userNameFamilyIsValid = true;
    private EmailTextWatcher emailTextWatcher;
    private String rawNationalCode = "";
    private int rawNationalCodeLength = 0;
    private int rawNationalCodeLengthOffset = 0;
    private String procNationalCode = "";
    private SharedPreferences.Editor editor;
    private RegistrationEntryRequest registrationEntryRequest;
    private HamPayDialog hamPayDialog;
    private ArrayList<PermissionListener> permissionListeners = new ArrayList<>();
    private String fullUserName = "";
    private RegisterEntry registerer;
    private RootUtil rootUtil;
    private Bundle bundle;
    private Intent intent;
    private PreferencesManager preferencesManager;
    private List<ScreenItem> welcomeScreens = new ArrayList<>();

    public void userManual(View view) {
        Intent intent = new Intent();
        intent.setClass(activity, UserManualActivity.class);
        intent.putExtra(Constants.USER_MANUAL_TEXT, R.string.user_manual_text_profile_entry);
        intent.putExtra(Constants.USER_MANUAL_TITLE, R.string.user_manual_title_profile_entry);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        for (PermissionListener permissionListener : permissionListeners) {
            if (permissionListener.onResult(requestCode, permissions, grantResults)) {
                permissionListeners.remove(permissionListener);
            }
        }
    }

    private void requestAndLoadPhoneState() {
        String[] permissions = new String[]{Manifest.permission.READ_PHONE_STATE};
        permissionListeners = new RequestPermissions().request(activity, Constants.READ_PHONE_STATE, permissions, (requestCode, requestPermissions, grantResults) -> {
            if (requestCode == Constants.READ_PHONE_STATE) {
                if (grantResults.length > 0 && requestPermissions[0].equals(Manifest.permission.READ_PHONE_STATE) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    registrationEntryRequest = new RegistrationEntryRequest();
                    registrationEntryRequest.setCellNumber(persianEnglishDigit.P2E(getString(R.string.iran_prefix_cell_number) + cellNumberValue.getText().toString()));
                    registrationEntryRequest.setFullName(fullUserName);
                    registrationEntryRequest.setEmail(emailValue.getText().toString().trim());
                    registrationEntryRequest.setNationalCode(persianEnglishDigit.P2E(nationalCodeValue.getText().toString().replaceAll("-", "")));
                    registerer.register(registrationEntryRequest, AppManager.getAuthToken(ctx));
                } else {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE);
                        if (showRationale) {
                            handler.post(() -> {
                                keepOn_button.setEnabled(true);
                                PermissionDeviceDialog permissionDeviceDialog = new PermissionDeviceDialog();
                                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                                fragmentTransaction.add(permissionDeviceDialog, null);
                                fragmentTransaction.commitAllowingStateLoss();
                            });
                        } else {
                            keepOn_button.setEnabled(true);
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            intent.setData(Uri.parse("package:" + ctx.getPackageName()));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                            ctx.startActivity(intent);
                        }
                    } else {
                        handler.post(() -> {
                            PermissionDeviceDialog permissionDeviceDialog = new PermissionDeviceDialog();
                            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.add(permissionDeviceDialog, null);
                            fragmentTransaction.commitAllowingStateLoss();
                        });
                    }
                }
                return true;
            }
            return false;
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;

        LogEvent logEvent = new LogEvent(this);
        AppEvent appEvent = AppEvent.LAUNCH;
        logEvent.log(appEvent);

        rootUtil = new RootUtil(activity);
//        if (rootUtil.checkRootedDevice()) {
//            new HamPayDialog(activity).showPreventRootDeviceDialog();
//            return;
//        }

        bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getBoolean(Constants.HAS_NOTIFICATION)) {

                if (HamPayLoginActivity.instance != null) {
                    try {
                        HamPayLoginActivity.instance.finish();
                    } catch (Exception e) {
                    }
                }

                NotificationMessageType notificationMessageType;
                notificationMessageType = NotificationMessageType.valueOf(bundle.getString(Constants.NOTIFICATION_TYPE));

                switch (notificationMessageType) {
                    case PAYMENT:
                        intent = getIntent();
                        intent.setClass(activity, HamPayLoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        startActivity(intent);
                        break;

                    case CREDIT_REQUEST:
                        intent = getIntent();
                        intent.setClass(activity, HamPayLoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        startActivity(intent);
                        break;

                    case PURCHASE:
                        intent = getIntent();
                        intent.setClass(activity, HamPayLoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        startActivity(intent);
                        break;

                    case USER_PAYMENT_CONFIRM:
                        intent = getIntent();
                        intent.setClass(activity, HamPayLoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        startActivity(intent);
                        break;
                }

            }
        }

        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }

        preferencesManager = new PreferencesManager(this);
        if (preferencesManager.isRegistered()) {
            launchLoginScreen();
        } else if (preferencesManager.isFirstTimeLaunch()) {
            launchRegisterScreen();
        }

        setContentView(R.layout.activity_profile_entry);

        ButterKnife.bind(this);

        registerer = new RegisterEntryImpl(new ModelLayerImpl(activity), this);
        persianEnglishDigit = new PersianEnglishDigit();
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
        confirmTacPrivacy = (CheckBox) findViewById(R.id.confirmTacPrivacy);
        confirmTacPrivacy.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                keepOn_button.setBackgroundResource(R.drawable.registration_button_style);
            } else {
                keepOn_button.setBackgroundResource(R.drawable.disable_button_style);
            }
        });

        Spannable tcPrivacySpannable = new SpannableString(activity.getString(R.string.start_tac_privacy));
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
        tcPrivacySpannable.setSpan(tcClickableSpan, 17, 45, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tac_privacy_text.setText(tcPrivacySpannable);
        tac_privacy_text.setMovementMethod(LinkMovementMethod.getInstance());
        ClickableSpan privacySpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent intent = new Intent();
                intent.setClass(activity, GuideDetailActivity.class);
                intent.putExtra(Constants.WEB_PAGE_ADDRESS, Constants.HTTPS_SERVER_IP + "/users/privacy-file");
                intent.putExtra(Constants.TAC_PRIVACY_TITLE, activity.getString(R.string.privacy_title_activity));
                startActivity(intent);
            }
        };

        tcPrivacySpannable.setSpan(privacySpan, 48, 75, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tac_privacy_text.setText(tcPrivacySpannable);
        tac_privacy_text.setMovementMethod(LinkMovementMethod.getInstance());

        hamPayDialog = new HamPayDialog(activity);

        cellNumberValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cellNumberValue.removeTextChangedListener(this);
                cellNumberValue.setText(persianEnglishDigit.E2P(s.toString()));
                cellNumberValue.setSelection(s.toString().length());
                cellNumberValue.addTextChangedListener(this);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        cellNumberValue.setOnFocusChangeListener((v, hasFocus) -> {

            if (!hasFocus) {
                if (cellNumberValue.getText().toString().length() == 9) {
                    cellNumberIcon.setImageResource(R.drawable.right_icon);
                    cellNumberIsValid = true;
                } else {
                    cellNumberIcon.setImageResource(R.drawable.false_icon);
                    cellNumberIsValid = false;
                }
            }
        });

        nationalCodeValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nationalCodeValue.removeTextChangedListener(this);
                if (nationalCodeValue.getText().toString().length() <= 12) {
                    rawNationalCode = s.toString().replace("-", "");
                    rawNationalCodeLength = rawNationalCode.length();
                    rawNationalCodeLengthOffset = 0;
                    procNationalCode = "";
                    if (rawNationalCode.length() > 0) {
                        for (int i = 0; i < rawNationalCodeLength; i++) {
                            if (Constants.NATIONAL_CODE_FORMAT.charAt(i + rawNationalCodeLengthOffset) == '-') {
                                procNationalCode += "-" + rawNationalCode.charAt(i);
                                rawNationalCodeLengthOffset++;
                            } else {
                                procNationalCode += rawNationalCode.charAt(i);
                            }
                        }
                        procNationalCode = persianEnglishDigit.E2P(procNationalCode);
                        nationalCodeValue.setText(procNationalCode);
                        nationalCodeValue.setSelection(nationalCodeValue.getText().toString().length());
                    }
                }

                nationalCodeValue.addTextChangedListener(this);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        nationalCodeValue.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (new NationalCodeVerification(persianEnglishDigit.P2E(nationalCodeValue.getText().toString().replaceAll("-", ""))).isValidCode()) {
                    nationalCodeIcon.setImageResource(R.drawable.right_icon);
                    nationalCodeIsValid = true;
                } else {
                    nationalCodeIcon.setImageResource(R.drawable.false_icon);
                    nationalCodeIsValid = false;
                }
            }
        });

        userNameFamily.setOnFocusChangeListener((v, hasFocus) -> {
            fullUserName = userNameFamily.getText().toString().trim().replaceAll("^ +| +$|( )+", "$1");
            if (!hasFocus) {
                userNameFamilyIsValid = true;
                if (fullUserName.length() <= 1) {
                    userNameFamilyIsValid = false;
                    userNameFamilyIcon.setImageResource(R.drawable.false_icon);
                } else {
                    userNameFamilyIsValid = true;
                    userNameFamilyIcon.setImageResource(R.drawable.right_icon);
                }
            }
        });

        emailTextWatcher = new EmailTextWatcher(emailValue, emailIcon);

        emailValue.addTextChangedListener(emailTextWatcher);
        keepOn_button.setOnClickListener(v -> {

            fullUserName = userNameFamily.getText().toString().trim().replaceAll("^ +| +$|( )+", "$1");

            if (!confirmTacPrivacy.isChecked()) {
                Toast.makeText(activity, getString(R.string.force_tac), Toast.LENGTH_SHORT).show();
                return;
            }
            keepOn_button.requestFocus();
            View view = getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(
                        INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            cellNumberValue.clearFocus();
            userNameFamily.clearFocus();
            nationalCodeValue.clearFocus();
            if (cellNumberIsValid && nationalCodeIsValid
                    && cellNumberValue.getText().toString().trim().length() > 0
                    && nationalCodeValue.getText().toString().trim().length() > 0
                    && fullUserName.length() >= 2
                    && emailTextWatcher.isValid()) {
                keepOn_button.setEnabled(false);
                requestAndLoadPhoneState();

            } else {

                if (cellNumberValue.getText().toString().length() == 0 || !cellNumberIsValid) {
                    Toast.makeText(ctx, getString(R.string.msg_cellNumber_invalid), Toast.LENGTH_SHORT).show();
                    cellNumberIcon.setImageResource(R.drawable.false_icon);
                    cellNumberValue.requestFocus();
                } else if (fullUserName.length() <= 1 || !userNameFamilyIsValid) {
                    Toast.makeText(ctx, getString(R.string.msg_username_invalid), Toast.LENGTH_SHORT).show();
                    userNameFamilyIcon.setImageResource(R.drawable.false_icon);
                    userNameFamily.requestFocus();
                } else if (nationalCodeValue.getText().toString().length() == 0 || !nationalCodeIsValid) {
                    Toast.makeText(ctx, getString(R.string.msg_nationalCode_invalid), Toast.LENGTH_SHORT).show();
                    nationalCodeIcon.setImageResource(R.drawable.false_icon);
                    nationalCodeValue.requestFocus();
                } else if (!emailTextWatcher.isValid()) {
                    Toast.makeText(ctx, getString(R.string.msg_invalid_email), Toast.LENGTH_SHORT).show();
                    emailValue.requestFocus();
                }
            }
        });
    }

    @Override
    public void onFinishEditDialog(ActionPermission actionPermission) {
        switch (actionPermission) {
            case GRANT:
                requestAndLoadPhoneState();
                break;
            case DENY:
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        new HamPayDialog(activity).exitRegistrationDialog();
    }

    @Override
    public void onError() {
        hamPayDialog.dismisWaitingDialog();
        keepOn_button.setEnabled(true);
        Toast.makeText(ctx, R.string.err_message_registration, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRegisterResponse(boolean state, ResponseMessage<RegistrationEntryResponse> data, String message) {
        keepOn_button.setEnabled(true);
        ServiceEvent serviceName;
        if (state && data != null) {
            if (data.getService().getResultStatus() == ResultStatus.SUCCESS) {
                AppManager.setRegisterIdToken(ctx, data.getService().getUserIdToken());
                AppManager.setRegisterUserName(ctx, fullUserName);
                AppManager.setRegisterUserEmail(ctx, emailValue.getText().toString().trim());
                hamPayDialog.smsConfirmDialog(getString(R.string.iran_prefix_cell_number) + cellNumberValue.getText().toString());
                serviceName = ServiceEvent.REGISTRATION_ENTRY_SUCCESS;
            } else {
                serviceName = ServiceEvent.REGISTRATION_ENTRY_FAILURE;
                new HamPayDialog(activity).showFailRegistrationEntryDialog(registerer, registrationEntryRequest, AppManager.getAuthToken(ctx),
                        data.getService().getResultStatus().getCode(),
                        data.getService().getResultStatus().getDescription());
            }
        } else {
            hamPayDialog.dismisWaitingDialog();
            serviceName = ServiceEvent.REGISTRATION_ENTRY_FAILURE;
        }
        new LogEvent(ctx).log(serviceName);
    }

    @Override
    public void showProgress() {
        hamPayDialog.showWaitingDialog("");
    }

    @Override
    public void cancelProgress() {
        hamPayDialog.dismisWaitingDialog();
    }

    @Override
    public void keyExchangeProblem() {
        keepOn_button.setEnabled(true);
        keepOn_button.setBackgroundResource(R.drawable.disable_button_style);
        Toast.makeText(activity, getString(R.string.system_connectivity), Toast.LENGTH_LONG).show();
    }

    private void launchRegisterScreen() {
        welcomeScreens.add(new ScreenItem(R.string.title_included_pages, R.string.description_included_pages, IncludedPagesWelcomeActivity.class, REQUEST_WELCOME_SCREEN_RESULT));
        final ScreenItem item = welcomeScreens.get(0);
        if (item.requestCode != null) {
            item.helper.forceShow(item.requestCode);
        } else {
            item.helper.forceShow();
        }
    }

    private void launchLoginScreen() {
        startActivity(new Intent(ProfileEntryActivity.this, HamPayLoginActivity.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_WELCOME_SCREEN_RESULT) {

            if (resultCode == RESULT_OK) {
                preferencesManager.setFirstTimeLaunch(false);
            } else if (resultCode == RESULT_CANCELED) {
            }

        }

    }

    private class ScreenItem {

        String title;
        String description;
        WelcomeHelper helper;
        Integer requestCode;

        ScreenItem(int titleRes, int descriptionRes, Class<? extends WelcomeActivity> activityClass) {
            this(titleRes, descriptionRes, activityClass, null);
        }

        ScreenItem(int titleRes, int descriptionRes, Class<? extends WelcomeActivity> activityClass, Integer requestCode) {
            this(titleRes, descriptionRes, new WelcomeHelper(ProfileEntryActivity.this, activityClass), requestCode);
        }

        ScreenItem(int titleRes, int descriptionRes, WelcomeHelper helper, Integer requestCode) {
            this.title = getString(titleRes);
            this.description = getString(descriptionRes);
            this.helper = helper;
            this.requestCode = requestCode;
        }

    }

}

