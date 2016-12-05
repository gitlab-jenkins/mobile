package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.dto.ContactDTO;
import xyz.homapay.hampay.common.core.model.request.RegistrationCredentialsRequest;
import xyz.homapay.hampay.common.core.model.response.RegistrationCredentialsResponse;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.Manifest;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestCredentialEntry;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.edittext.FacedEditText;
import xyz.homapay.hampay.mobile.android.component.edittext.MemorableTextWatcher;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.dialog.permission.ActionPermission;
import xyz.homapay.hampay.mobile.android.dialog.permission.PermissionContactDialog;
import xyz.homapay.hampay.mobile.android.dialog.permission.PermissionDeviceDialog;
import xyz.homapay.hampay.mobile.android.firebase.LogEvent;
import xyz.homapay.hampay.mobile.android.firebase.service.ServiceEvent;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.permission.PermissionListener;
import xyz.homapay.hampay.mobile.android.permission.RequestPermissions;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.DeviceInfo;
import xyz.homapay.hampay.mobile.android.util.UserContacts;

public class MemorableWordEntryActivity extends AppCompatActivity implements PermissionContactDialog.PermissionContactDialogListener {

    private List<ContactDTO> contacts;
    private FacedTextView keepOn_button;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private FacedEditText memorable_value;
    private final Handler handler = new Handler();


    private Context context;
    private Activity activity;
    private HamPayDialog hamPayDialog;
    private String Uuid = "";
    private Bundle bundle;
    private String userEntryPassword;
    private RequestCredentialEntry requestCredentialEntry;
    private RegistrationCredentialsRequest registrationCredentialsRequest;
    private ArrayList<PermissionListener> permissionListeners = new ArrayList<>();

    public void userManual(View view){
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
                    if (requestPermissions[0].equals(Manifest.permission.READ_CONTACTS) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        UserContacts userContacts = new UserContacts(context);
                        contacts = userContacts.read();
                        registrationCredentialsRequest.setContacts(contacts);

                        registrationCredentialsRequest.setUserIdToken(prefs.getString(Constants.REGISTERED_USER_ID_TOKEN, ""));
                        registrationCredentialsRequest.setDeviceId(new DeviceInfo(activity).getAndroidId());
                        Uuid = UUID.randomUUID().toString();
                        registrationCredentialsRequest.setInstallationToken(Uuid);
                        registrationCredentialsRequest.setMemorableKey(memorable_value.getText().toString());
                        registrationCredentialsRequest.setPassCode(userEntryPassword);
                        requestCredentialEntry = new RequestCredentialEntry(context, new RequestMemorableWordEntryResponseTaskCompleteListener());
                        requestCredentialEntry.execute(registrationCredentialsRequest);

                    } else {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS);
                            if (showRationale){
                                handler.post(new Runnable() {
                                    public void run() {
                                        PermissionContactDialog permissionContactDialog = new PermissionContactDialog();
                                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                                        fragmentTransaction.add(permissionContactDialog, null);
                                        fragmentTransaction.commitAllowingStateLoss();
                                    }
                                });
                            }else {
                                contacts = new ArrayList<ContactDTO>();
                                registrationCredentialsRequest.setContacts(contacts);
                                registrationCredentialsRequest.setUserIdToken(prefs.getString(Constants.REGISTERED_USER_ID_TOKEN, ""));
                                registrationCredentialsRequest.setDeviceId(new DeviceInfo(activity).getAndroidId());
                                Uuid = UUID.randomUUID().toString();
                                registrationCredentialsRequest.setInstallationToken(Uuid);
                                registrationCredentialsRequest.setMemorableKey(memorable_value.getText().toString());
                                registrationCredentialsRequest.setPassCode(userEntryPassword);
                                requestCredentialEntry = new RequestCredentialEntry(context, new RequestMemorableWordEntryResponseTaskCompleteListener());
                                requestCredentialEntry.execute(registrationCredentialsRequest);
                            }
                        }else {
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
        setContentView(R.layout.activity_memorable_word_entry);

        activity = MemorableWordEntryActivity.this;
        context = this;

        bundle = getIntent().getExtras();

        registrationCredentialsRequest = new RegistrationCredentialsRequest();

        userEntryPassword = bundle.getString(Constants.USER_ENTRY_PASSWORD);

        hamPayDialog = new HamPayDialog(activity);

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        memorable_value = (FacedEditText)findViewById(R.id.memorable_value);
        memorable_value.addTextChangedListener(new MemorableTextWatcher(memorable_value));

        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();

        keepOn_button = (FacedTextView) findViewById(R.id.keepOn_button);
        keepOn_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (memorable_value.getText().toString().trim().length() > 1 ) {
                    requestAndLoadUserContact();
                }else {
                    Toast.makeText(context, getString(R.string.msg_memorable_incorrect), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onFinishEditDialog(ActionPermission actionPermission) {
        switch (actionPermission){
            case GRANT:
                requestAndLoadUserContact();
                break;
            case DENY:
                contacts = new ArrayList<ContactDTO>();
                registrationCredentialsRequest.setContacts(contacts);
                registrationCredentialsRequest.setUserIdToken(prefs.getString(Constants.REGISTERED_USER_ID_TOKEN, ""));
                registrationCredentialsRequest.setDeviceId(new DeviceInfo(activity).getAndroidId());
                Uuid = UUID.randomUUID().toString();
                registrationCredentialsRequest.setInstallationToken(Uuid);
                registrationCredentialsRequest.setMemorableKey(memorable_value.getText().toString());
                registrationCredentialsRequest.setPassCode(userEntryPassword);
                requestCredentialEntry = new RequestCredentialEntry(context, new RequestMemorableWordEntryResponseTaskCompleteListener());
                requestCredentialEntry.execute(registrationCredentialsRequest);
                break;
        }
    }


    public class RequestMemorableWordEntryResponseTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<RegistrationCredentialsResponse>>
    {
        public RequestMemorableWordEntryResponseTaskCompleteListener(){
        }

        @Override
        public void onTaskComplete(ResponseMessage<RegistrationCredentialsResponse> registrationMemorableWordEntryResponseMessage) {

            hamPayDialog.dismisWaitingDialog();
            ServiceEvent serviceName;
            LogEvent logEvent = new LogEvent(context);
            ResultStatus resultStatus;
            if (registrationMemorableWordEntryResponseMessage != null) {
                resultStatus = registrationMemorableWordEntryResponseMessage.getService().getResultStatus();
                if (resultStatus == ResultStatus.SUCCESS) {
                    editor.putString(Constants.MEMORABLE_WORD, memorable_value.getText().toString());
                    editor.putString(Constants.LOGIN_API_LEVEL, Constants.API_LEVEL);
                    editor.putString(Constants.UUID, Uuid);
                    editor.commit();
                    serviceName = ServiceEvent.REGISTRATION_CREDENTIALS_SUCCESS;
                    Intent intent = new Intent();
                    intent.setClass(MemorableWordEntryActivity.this, CompleteRegistrationActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(intent);
                }
                else {
                    serviceName = ServiceEvent.REGISTRATION_CREDENTIALS_FAILURE;
                    requestCredentialEntry = new RequestCredentialEntry(context, new RequestMemorableWordEntryResponseTaskCompleteListener());
                    new HamPayDialog(activity).showFailMemorableEntryDialog(requestCredentialEntry, registrationCredentialsRequest,
                            registrationMemorableWordEntryResponseMessage.getService().getResultStatus().getCode(),
                            registrationMemorableWordEntryResponseMessage.getService().getResultStatus().getDescription());
                }
            }else {
                serviceName = ServiceEvent.REGISTRATION_CREDENTIALS_FAILURE;
                requestCredentialEntry = new RequestCredentialEntry(context, new RequestMemorableWordEntryResponseTaskCompleteListener());
                new HamPayDialog(activity).showFailMemorableEntryDialog(requestCredentialEntry, registrationCredentialsRequest,
                        Constants.LOCAL_ERROR_CODE,
                        getString(R.string.msg_fail_memorable_entry));
            }
            logEvent.log(serviceName);
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }

    @Override
    public void onBackPressed() {
        new HamPayDialog(activity).exitRegistrationDialog();
    }
}
