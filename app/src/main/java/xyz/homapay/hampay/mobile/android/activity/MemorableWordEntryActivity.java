package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
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
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.permission.PermissionListener;
import xyz.homapay.hampay.mobile.android.permission.RequestPermissions;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.DeviceInfo;
import xyz.homapay.hampay.mobile.android.util.SecurityUtils;
import xyz.homapay.hampay.mobile.android.util.UserContacts;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MemorableWordEntryActivity extends AppCompatActivity {

    private FacedTextView keepOn_button;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private FacedEditText memorable_value;

    Context context;

    Activity activity;

    HamPayDialog hamPayDialog;

    String Uuid = "";

    Bundle bundle;

    String userEntryPassword;

    Tracker hamPayGaTracker;

    RequestCredentialEntry requestCredentialEntry;
    RegistrationCredentialsRequest registrationCredentialsRequest;


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
                    // Check if the permission is correct and is granted
                    if (requestPermissions[0].equals(Manifest.permission.READ_CONTACTS) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // Permission granted

                        UserContacts userContacts = new UserContacts(context);
                        List<ContactDTO> contact = userContacts.read();

//                        Toast.makeText(activity, "Access allowed!", Toast.LENGTH_SHORT).show();

                    } else {
                        // Permission not granted
//                        Toast.makeText(activity, "Access denied!", Toast.LENGTH_SHORT).show();
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


        requestAndLoadUserContact();

        userEntryPassword = bundle.getString(Constants.USER_ENTRY_PASSWORD);

        hamPayDialog = new HamPayDialog(activity);

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);

        hamPayGaTracker = ((HamPayApplication) getApplication())
                .getTracker(HamPayApplication.TrackerName.APP_TRACKER);

        memorable_value = (FacedEditText)findViewById(R.id.memorable_value);
        memorable_value.addTextChangedListener(new MemorableTextWatcher(memorable_value));

        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
        editor.putString(Constants.REGISTERED_ACTIVITY_DATA, PasswordEntryActivity.class.getName());
        editor.commit();

        keepOn_button = (FacedTextView) findViewById(R.id.keepOn_button);
        keepOn_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (memorable_value.getText().toString().trim().length() > 1 ) {
                    registrationCredentialsRequest = new RegistrationCredentialsRequest();
                    registrationCredentialsRequest.setUserIdToken(prefs.getString(Constants.REGISTERED_USER_ID_TOKEN, ""));
                    registrationCredentialsRequest.setDeviceId(new DeviceInfo(activity).getAndroidId());
                    Uuid = UUID.randomUUID().toString();
                    registrationCredentialsRequest.setInstallationToken(Uuid);
                    registrationCredentialsRequest.setMemorableKey(memorable_value.getText().toString());
                    registrationCredentialsRequest.setPassCode(userEntryPassword);
                    requestCredentialEntry = new RequestCredentialEntry(context, new RequestMemorableWordEntryResponseTaskCompleteListener());
                    requestCredentialEntry.execute(registrationCredentialsRequest);
                }else {
                    Toast.makeText(context, getString(R.string.msg_memorable_incorrect), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }




    public class RequestMemorableWordEntryResponseTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<RegistrationCredentialsResponse>>
    {
        public RequestMemorableWordEntryResponseTaskCompleteListener(){
        }

        @Override
        public void onTaskComplete(ResponseMessage<RegistrationCredentialsResponse> registrationMemorableWordEntryResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            ResultStatus resultStatus;

            if (registrationMemorableWordEntryResponseMessage != null) {

                resultStatus = registrationMemorableWordEntryResponseMessage.getService().getResultStatus();

                if (resultStatus == ResultStatus.SUCCESS) {
                    editor.putString(Constants.MEMORABLE_WORD, memorable_value.getText().toString());
                    editor.putString(Constants.UUID, Uuid);
                    editor.commit();
                    Intent intent = new Intent();
                    intent.setClass(MemorableWordEntryActivity.this, CompleteRegistrationActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(intent);

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Registration Memorable Word Entry")
                            .setAction("Registration")
                            .setLabel("Success")
                            .build());
                }
                else {
                    requestCredentialEntry = new RequestCredentialEntry(context, new RequestMemorableWordEntryResponseTaskCompleteListener());
                    new HamPayDialog(activity).showFailMemorableEntryDialog(requestCredentialEntry, registrationCredentialsRequest,
                            registrationMemorableWordEntryResponseMessage.getService().getResultStatus().getCode(),
                            registrationMemorableWordEntryResponseMessage.getService().getResultStatus().getDescription());

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Registration Memorable Word Entry")
                            .setAction("Registration")
                            .setLabel("Fail(Server)")
                            .build());
                }
            }else {
                requestCredentialEntry = new RequestCredentialEntry(context, new RequestMemorableWordEntryResponseTaskCompleteListener());
                new HamPayDialog(activity).showFailMemorableEntryDialog(requestCredentialEntry, registrationCredentialsRequest,
                        Constants.LOCAL_ERROR_CODE,
                        getString(R.string.msg_fail_memorable_entry));

                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Registration Memorable Word Entry")
                        .setAction("Registration")
                        .setLabel("Fail(Mobile)")
                        .build());
            }

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
