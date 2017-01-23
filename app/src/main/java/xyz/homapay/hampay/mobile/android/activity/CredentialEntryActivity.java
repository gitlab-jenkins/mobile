package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.dto.ContactDTO;
import xyz.homapay.hampay.common.core.model.request.RegistrationCredentialsRequest;
import xyz.homapay.hampay.common.core.model.response.RegistrationCredentialsResponse;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.Manifest;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.RequestCredentialEntry;
import xyz.homapay.hampay.mobile.android.component.edittext.FacedEditText;
import xyz.homapay.hampay.mobile.android.component.edittext.MemorableTextWatcher;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.dialog.permission.ActionPermission;
import xyz.homapay.hampay.mobile.android.dialog.permission.PermissionContactDialog;
import xyz.homapay.hampay.mobile.android.dialog.permission.PermissionDeviceDialog;
import xyz.homapay.hampay.mobile.android.firebase.LogEvent;
import xyz.homapay.hampay.mobile.android.firebase.service.ServiceEvent;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.p.Credential.CredentialEntry;
import xyz.homapay.hampay.mobile.android.p.Credential.CredentialEntryImpl;
import xyz.homapay.hampay.mobile.android.p.Credential.CredentialEntryView;
import xyz.homapay.hampay.mobile.android.permission.PermissionListener;
import xyz.homapay.hampay.mobile.android.permission.RequestPermissions;
import xyz.homapay.hampay.mobile.android.util.AppManager;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.DeviceInfo;
import xyz.homapay.hampay.mobile.android.util.ModelLayerImpl;

public class CredentialEntryActivity extends AppCompatActivity implements CredentialEntryView, PermissionContactDialog.PermissionContactDialogListener {

    private final Handler handler = new Handler();
    @BindView(R.id.memorable_value)
    FacedEditText memorable_value;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;
    private Activity activity;
    private HamPayDialog hamPayDialog;
    private String Uuid = "";
    private Bundle bundle;
    private String userEntryPassword;
    private RegistrationCredentialsRequest registrationCredentialsRequest;
    private ArrayList<PermissionListener> permissionListeners = new ArrayList<>();
    private CredentialEntry credentialEntry;
    private boolean contactPermission = false;

    public void userManual(View view) {
        Intent intent = new Intent();
        intent.setClass(activity, UserManualActivity.class);
        intent.putExtra(Constants.USER_MANUAL_TEXT, R.string.user_manual_text_memorable_word_entry);
        intent.putExtra(Constants.USER_MANUAL_TITLE, R.string.user_manual_title_memorable_word_entry);
        startActivity(intent);
    }


    @Override
    protected void onPause() {
        super.onPause();
        HamPayApplication.setAppSate(AppState.Paused);
    }

    @Override
    protected void onStop() {
        super.onStop();
        HamPayApplication.setAppSate(AppState.Stoped);
    }

    @Override
    protected void onResume() {
        super.onResume();
        HamPayApplication.setAppSate(AppState.Resumed);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        for (PermissionListener permissionListener : permissionListeners)
            if (permissionListener.onResult(requestCode, permissions, grantResults)) {
                permissionListeners.remove(permissionListener);
            }
    }

    private void requestAndLoadUserContact() {
        String[] permissions = new String[]{Manifest.permission.READ_CONTACTS};

        permissionListeners = new RequestPermissions().request(activity, Constants.READ_CONTACTS, permissions, new PermissionListener() {
            @Override
            public boolean onResult(int requestCode, String[] requestPermissions, int[] grantResults) {
                if (requestCode == Constants.READ_CONTACTS) {
                    if (grantResults.length > 0 && requestPermissions[0].equals(Manifest.permission.READ_CONTACTS) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        registrationCredentialsRequest.setUserIdToken(prefs.getString(Constants.REGISTERED_USER_ID_TOKEN, ""));
                        registrationCredentialsRequest.setDeviceId(new DeviceInfo(activity).getAndroidId());
                        Uuid = UUID.randomUUID().toString();
                        registrationCredentialsRequest.setInstallationToken(Uuid);
                        registrationCredentialsRequest.setMemorableKey(memorable_value.getText().toString());
                        registrationCredentialsRequest.setPassCode(userEntryPassword);
                        contactPermission = true;
                        credentialEntry.credential(registrationCredentialsRequest, AppManager.getAuthToken(context), contactPermission);
                    } else {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS);
                            if (showRationale) {
                                handler.post(new Runnable() {
                                    public void run() {
                                        PermissionContactDialog permissionContactDialog = new PermissionContactDialog();
                                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                                        fragmentTransaction.add(permissionContactDialog, null);
                                        fragmentTransaction.commitAllowingStateLoss();
                                    }
                                });
                            } else {
                                registrationCredentialsRequest.setUserIdToken(prefs.getString(Constants.REGISTERED_USER_ID_TOKEN, ""));
                                registrationCredentialsRequest.setDeviceId(new DeviceInfo(activity).getAndroidId());
                                Uuid = UUID.randomUUID().toString();
                                registrationCredentialsRequest.setInstallationToken(Uuid);
                                registrationCredentialsRequest.setMemorableKey(memorable_value.getText().toString());
                                registrationCredentialsRequest.setPassCode(userEntryPassword);
                                registrationCredentialsRequest.setPassCode(userEntryPassword);
                                credentialEntry.credential(registrationCredentialsRequest, AppManager.getAuthToken(context), contactPermission);
                            }
                        } else {
                            handler.post(new Runnable() {
                                public void run() {
                                    PermissionDeviceDialog permissionDeviceDialog = new PermissionDeviceDialog();
                                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                                    fragmentTransaction.add(permissionDeviceDialog, null);
                                    fragmentTransaction.commitAllowingStateLoss();
                                }
                            });
                        }
                    }
                    return true;
                }
                return false;
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credentioal_entry);

        ButterKnife.bind(this);

        activity = CredentialEntryActivity.this;
        context = this;

        credentialEntry = new CredentialEntryImpl(new ModelLayerImpl(activity), this);

        bundle = getIntent().getExtras();

        registrationCredentialsRequest = new RegistrationCredentialsRequest();

        userEntryPassword = bundle.getString(Constants.USER_ENTRY_PASSWORD);

        hamPayDialog = new HamPayDialog(activity);

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        memorable_value.addTextChangedListener(new MemorableTextWatcher(memorable_value));

        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.keepOn_button:
                if (memorable_value.getText().toString().trim().length() > 1) {
                    requestAndLoadUserContact();
                } else {
                    Toast.makeText(context, getString(R.string.msg_memorable_incorrect), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        new HamPayDialog(activity).exitRegistrationDialog();
    }

    @Override
    public void onError() {
        Toast.makeText(context, R.string.err_message_registration, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRegisterResponse(boolean state, ResponseMessage<RegistrationCredentialsResponse> data, String message) {
        hamPayDialog.dismisWaitingDialog();
        ServiceEvent serviceName;
        LogEvent logEvent = new LogEvent(context);
        ResultStatus resultStatus;
        if (state && data != null) {
            resultStatus = data.getService().getResultStatus();
            if (resultStatus == ResultStatus.SUCCESS) {
                editor.putString(Constants.MEMORABLE_WORD, memorable_value.getText().toString());
                editor.putString(Constants.LOGIN_API_LEVEL, Constants.API_LEVEL);
                editor.putString(Constants.UUID, Uuid);
                editor.commit();
                serviceName = ServiceEvent.REGISTRATION_CREDENTIALS_SUCCESS;
                Intent intent = new Intent();
                intent.setClass(CredentialEntryActivity.this, CompleteRegistrationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivity(intent);
            } else {
                serviceName = ServiceEvent.REGISTRATION_CREDENTIALS_FAILURE;
                new HamPayDialog(activity).showFailMemorableEntryDialog(credentialEntry, registrationCredentialsRequest,
                        AppManager.getAuthToken(context),
                        data.getService().getResultStatus().getCode(),
                        data.getService().getResultStatus().getDescription(), contactPermission);
            }
        } else {
            serviceName = ServiceEvent.REGISTRATION_CREDENTIALS_FAILURE;
            new HamPayDialog(activity).showFailMemorableEntryDialog(credentialEntry, registrationCredentialsRequest,
                    AppManager.getAuthToken(context),
                    Constants.LOCAL_ERROR_CODE,
                    getString(R.string.msg_fail_memorable_entry), contactPermission);
        }
        logEvent.log(serviceName);
    }

    @Override
    public void showProgressDialog() {
        hamPayDialog.showWaitingDialog("");
    }

    @Override
    public void dismissProgressDialog() {
        hamPayDialog.dismisWaitingDialog();
    }

    @Override
    public void keyExchangeProblem() {
        Toast.makeText(activity, getString(R.string.system_connectivity), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFinishEditDialog(ActionPermission actionPermission) {
        switch (actionPermission) {
            case GRANT:
                requestAndLoadUserContact();
                break;
            case DENY:
                registrationCredentialsRequest.setUserIdToken(prefs.getString(Constants.REGISTERED_USER_ID_TOKEN, ""));
                registrationCredentialsRequest.setDeviceId(new DeviceInfo(activity).getAndroidId());
                Uuid = UUID.randomUUID().toString();
                registrationCredentialsRequest.setInstallationToken(Uuid);
                registrationCredentialsRequest.setMemorableKey(memorable_value.getText().toString());
                registrationCredentialsRequest.setPassCode(userEntryPassword);
                registrationCredentialsRequest.setPassCode(userEntryPassword);
                credentialEntry.credential(registrationCredentialsRequest, AppManager.getAuthToken(context), contactPermission);

                break;
        }
    }
}
