package xyz.homapay.hampay.mobile.android.activity;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.MobileRegistrationIdEntryRequest;
import xyz.homapay.hampay.common.core.model.request.PSPResultRequest;
import xyz.homapay.hampay.common.core.model.request.UserProfileRequest;
import xyz.homapay.hampay.common.core.model.response.MobileRegistrationIdEntryResponse;
import xyz.homapay.hampay.common.core.model.response.PSPResultResponse;
import xyz.homapay.hampay.common.core.model.response.UserProfileResponse;
import xyz.homapay.hampay.common.core.model.response.dto.UserProfileDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.Helper.DatabaseHelper;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestMobileRegistrationIdEntry;
import xyz.homapay.hampay.mobile.android.async.RequestPSPResult;
import xyz.homapay.hampay.mobile.android.async.RequestUserProfile;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.dialog.ImageProfile.ActionImage;
import xyz.homapay.hampay.mobile.android.dialog.ImageProfile.EditImageDialog;
import xyz.homapay.hampay.mobile.android.fragment.AboutFragment;
import xyz.homapay.hampay.mobile.android.fragment.AccountDetailFragment;
import xyz.homapay.hampay.mobile.android.fragment.FragmentDrawer;
import xyz.homapay.hampay.mobile.android.fragment.GuideFragment;
import xyz.homapay.hampay.mobile.android.fragment.MainFragment;
import xyz.homapay.hampay.mobile.android.fragment.SettingFragment;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.model.LogoutData;
import xyz.homapay.hampay.mobile.android.model.NotificationMessageType;
import xyz.homapay.hampay.mobile.android.model.SyncPspResult;
import xyz.homapay.hampay.mobile.android.receiver.GcmBroadcastReceiver;
import xyz.homapay.hampay.mobile.android.service.GcmMessageHandler;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.DeviceInfo;
import xyz.homapay.hampay.mobile.android.util.ImageManager;


public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener, View.OnClickListener, EditImageDialog.EditImageDialogListener {

    private GoogleCloudMessaging googleCloudMessaging;
    private String registrationId;
    private FragmentDrawer drawerFragment;

    private Fragment fragment = null;
    private RequestPSPResult requestPSPResult;
    private PSPResultRequest pspResultRequest;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle mDrawerToggle;

    ImageView nav_icon;

    FacedTextView fragment_title;

    ImageView image_profile;
    LinearLayout user_image_layout;

    int currentFragmet = 0;

    UserProfileDTO userProfileDTO;

    String pendingPurchaseCode = null;
    String pendingPaymentCode = null;
    int pendingPurchaseCount = 0;
    int pendingPaymentCount = 0;

    Activity activity;

    Bundle bundle;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    boolean hasNotification = false;

    HamPayDialog hamPayDialog;

    Tracker hamPayGaTracker;

    Context context;

    RequestMobileRegistrationIdEntry requestMobileRegistrationIdEntry;
    MobileRegistrationIdEntryRequest mobileRegistrationIdEntryRequest;

    Intent intent;

    DatabaseHelper dbHelper;

    private String authToken = "";
    private ImageManager imageManager;
    private ImageView user_manual;
    private String fragmentTitle = "";

    public void userManual(View view){
        Intent intent = new Intent();
        intent.setClass(activity, UserManualActivity.class);
        if (currentFragmet == 0) {
            intent.putExtra(Constants.USER_MANUAL_TEXT, R.string.user_manual_text_main);
            intent.putExtra(Constants.USER_MANUAL_TITLE, R.string.user_manual_title_main);
            startActivity(intent);
        }else if (currentFragmet == 1){
            intent.putExtra(Constants.USER_MANUAL_TEXT, R.string.user_manual_text_account_details);
            intent.putExtra(Constants.USER_MANUAL_TITLE, R.string.user_manual_title_account_details);
            startActivity(intent);
        }
        else if (currentFragmet == 2){
            intent.putExtra(Constants.USER_MANUAL_TEXT, R.string.user_manual_text_setting);
            intent.putExtra(Constants.USER_MANUAL_TITLE, R.string.user_manual_title_setting);
            startActivity(intent);
        }

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
        if ((System.currentTimeMillis() - prefs.getLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis()) > Constants.MOBILE_TIME_OUT_INTERVAL)){
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, HamPayLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
        }else {
            editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
            editor.commit();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = MainActivity.this;
        context = this;

        imageManager = new ImageManager(activity, 200000, false);

        bundle = getIntent().getExtras();

        Intent intent = getIntent();

        pendingPurchaseCode = bundle.getString(Constants.PENDING_PURCHASE_CODE);
        pendingPaymentCode = bundle.getString(Constants.PENDING_PAYMENT_CODE);
        pendingPurchaseCount = bundle.getInt(Constants.PENDING_PURCHASE_COUNT, 0);
        pendingPaymentCount = bundle.getInt(Constants.PENDING_PAYMENT_COUNT, 0);

        userProfileDTO = (UserProfileDTO) intent.getSerializableExtra(Constants.USER_PROFILE_DTO);
        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = activity.getSharedPreferences(Constants.APP_PREFERENCE_NAME, activity.MODE_PRIVATE).edit();
        if (!prefs.contains(Constants.SETTING_CHANGE_IBAN_STATUS)){
            editor.putBoolean(Constants.SETTING_CHANGE_IBAN_STATUS, false);
            editor.commit();
        }
        authToken = prefs.getString(Constants.LOGIN_TOKEN_ID, "");

        editor.putLong(Constants.MAX_BUSINESS_XFER_AMOUNT, this.userProfileDTO.getMaxBusinessXferAmount());
        editor.putLong(Constants.MIN_BUSINESS_XFER_AMOUNT, this.userProfileDTO.getMinBusinessXferAmount());
        editor.putLong(Constants.MAX_INDIVIDUAL_XFER_AMOUNT, this.userProfileDTO.getMaxIndividualXferAmount());
        editor.putLong(Constants.MIN_INDIVIDUAL_XFER_AMOUNT, this.userProfileDTO.getMinIndividualXferAmount());
        editor.commit();

        hamPayGaTracker = ((HamPayApplication) getApplication())
                .getTracker(HamPayApplication.TrackerName.APP_TRACKER);

        if (bundle != null) {
            hasNotification = bundle.getBoolean(Constants.HAS_NOTIFICATION);
        }

        dbHelper = new DatabaseHelper(context);

        List<SyncPspResult> syncPspResults = dbHelper.allSyncPspResult();
        if (syncPspResults.size() > 0) {
            SyncPspResult syncPspResult = syncPspResults.get(0);
            pspResultRequest = new PSPResultRequest();
            pspResultRequest.setPspResponseCode(syncPspResult.getResponseCode());
            pspResultRequest.setProductCode(syncPspResult.getProductCode());
            pspResultRequest.setTrackingCode(syncPspResult.getSwTrace());
            if (syncPspResult.getType().equalsIgnoreCase("PURCHASE")) {
                requestPSPResult = new RequestPSPResult(context, new RequestPSPResultTaskCompleteListener(syncPspResult.getSwTrace()), 1);
                requestPSPResult.execute(pspResultRequest);
            }else if (syncPspResult.getType().equalsIgnoreCase("PAYMENT")){
                requestPSPResult = new RequestPSPResult(context, new RequestPSPResultTaskCompleteListener(syncPspResult.getSwTrace()), 2);
                requestPSPResult.execute(pspResultRequest);
            }
        }



        if (hasNotification) {
            NotificationMessageType notificationMessageType;
            notificationMessageType = NotificationMessageType.valueOf(bundle.getString(Constants.NOTIFICATION_TYPE));

            Intent notificationIntent;

            switch (notificationMessageType){
                case PAYMENT:
                    displayView(2);
                    break;

                case CREDIT_REQUEST:
                    notificationIntent = getIntent();
                    notificationIntent.setClass(activity, InvoicePendingConfirmationActivity.class);
                    startActivity(notificationIntent);
                    break;

                case PURCHASE:
                    notificationIntent = getIntent();
                    notificationIntent.setClass(activity, RequestBusinessPayDetailActivity.class);
                    startActivity(notificationIntent);
                    break;
                case USER_PAYMENT_CONFIRM:
                    notificationIntent = getIntent();
                    notificationIntent.setClass(activity, TransactionsListActivity.class);
                    startActivity(notificationIntent);
                    break;
            }
        }else {
            if (pendingPurchaseCode != null){
                if (!dbHelper.checkPurchaseRequest(pendingPurchaseCode)) {
                    intent.setClass(context, RequestBusinessPayDetailActivity.class);
                    startActivity(intent);
                }
            }else if (pendingPaymentCode != null){
                if (!dbHelper.checkPaymentRequest(pendingPaymentCode)) {
                    intent.setClass(context, InvoicePendingConfirmationActivity.class);
                    startActivity(intent);
                }
            }
        }


        user_manual = (ImageView)findViewById(R.id.user_manual);
        fragment_title = (FacedTextView)findViewById(R.id.fragment_title);
        image_profile = (ImageView)findViewById(R.id.image_profile);

        user_image_layout = (LinearLayout)findViewById(R.id.user_image_layout);
        user_image_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                EditImageDialog userEditPhotoDialog = new EditImageDialog();
                userEditPhotoDialog.show(fm, "fragment_edit_name");
            }
        });


//        addNewAccount(AccountGeneral.ACCOUNT_TYPE, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS);
//        ContactsManager.addContact(MainActivity.this, new HamPayContact("", "Sharafkar", "", "09122020200"));
//        ContactsManager.addContact(MainActivity.this, new HamPayContact("", "Sharafkar", "", "09122020200"));


        nav_icon = (ImageView)findViewById(R.id.nav_icon);
        nav_icon.setOnClickListener(this);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), null);
        drawerFragment.setDrawerListener(this);


        mDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout,
                R.string.drawer_open,
                R.string.drawer_close){

            @Override
            public boolean onOptionsItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        if(!drawerLayout.isDrawerOpen(Gravity.RIGHT))
                            drawerLayout.openDrawer(Gravity.RIGHT);
                        else
                            drawerLayout.closeDrawer(Gravity.RIGHT);

                        return true;

                    default:
                        break;
                }

                return super.onOptionsItemSelected(item);
            }

            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }

        };


        if (userProfileDTO.getUserImageId() != null) {
            String userImageUrl = Constants.HTTPS_SERVER_IP + Constants.IMAGE_PREFIX + authToken + "/" + userProfileDTO.getUserImageId();
            image_profile.setTag(userImageUrl.split("/")[6]);
            imageManager.displayImage(userImageUrl, image_profile, R.drawable.user_placeholder);
        }else {
            image_profile.setImageResource(R.drawable.user_placeholder);
        }
        displayView(currentFragmet);

        if (!prefs.getBoolean(Constants.SEND_MOBILE_REGISTER_ID, false)) {
            getRegId();
        }
    }






    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        editor.putString(Constants.USER_ID_TOKEN, "");
        editor.commit();
    }


    @Override
    public void onDrawerItemSelected(View view, int position) {
        if (currentFragmet != position || position == 5|| position == 6 || position == 7|| position == 8) {
//            currentFragmet = position;
            displayView(position);
        }
    }

    private void displayView(int position) {
        Intent intent = new Intent();
        switch (position) {
            case 0:
                currentFragmet = 0;
                user_manual.setVisibility(View.VISIBLE);
                fragment = new MainFragment();
                if (userProfileDTO != null) {
                    bundle.putSerializable(Constants.USER_PROFILE_DTO, userProfileDTO);
                    bundle.putInt(Constants.PENDING_PAYMENT_COUNT, pendingPaymentCount);
                    bundle.putInt(Constants.PENDING_PURCHASE_COUNT, pendingPurchaseCount);
                    fragment.setArguments(bundle);
                }
                fragmentTitle = getString(R.string.title_main_fragment);
                break;
            case 1:
                currentFragmet = 1;
                user_manual.setVisibility(View.VISIBLE);
                fragment = new AccountDetailFragment();
                if (userProfileDTO != null) {
                    bundle.putSerializable(Constants.USER_PROFILE_DTO, userProfileDTO);
                    fragment.setArguments(bundle);
                }
                fragmentTitle = getString(R.string.title_account_detail);
                break;
            case 2:
                currentFragmet = 2;
                user_manual.setVisibility(View.VISIBLE);
                fragment = new SettingFragment();
                fragmentTitle = getString(R.string.title_settings);
                break;

            case 3:
                currentFragmet = 3;
                user_manual.setVisibility(View.GONE);
                fragment = new GuideFragment();
                fragmentTitle = getString(R.string.title_guide);
                break;

            case 4:
                currentFragmet = 4;
                user_manual.setVisibility(View.GONE);
                fragment = new AboutFragment();
                fragmentTitle = getString(R.string.title_hampay_about);
                break;

            case 5:
                new HamPayDialog(activity).fetchContactUsInfo();
                break;

            case 6:
                intent.setClass(activity, GuideDetailActivity.class);
                intent.putExtra(Constants.WEB_PAGE_ADDRESS, Constants.HTTPS_SERVER_IP + "/users/tac-file");
                intent.putExtra(Constants.TAC_PRIVACY_TITLE, activity.getString(R.string.tac_title_activity));
                activity.startActivity(intent);
                break;
            case 7:
                intent.setClass(activity, GuideDetailActivity.class);
                intent.putExtra(Constants.WEB_PAGE_ADDRESS, Constants.HTTPS_SERVER_IP + "/users/privacy-file");
                intent.putExtra(Constants.TAC_PRIVACY_TITLE, activity.getString(R.string.privacy_title_activity));
                activity.startActivity(intent);
                break;

            case 8:
                LogoutData logoutData = new LogoutData();
                logoutData.setIplanetDirectoryPro(prefs.getString(Constants.LOGIN_TOKEN_ID, null));
                new HamPayDialog(activity).showExitDialog(logoutData);
                break;
        }


        if (fragment != null) {
            setFragment(fragment, fragmentTitle);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.nav_icon:
                if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    drawerLayout.closeDrawer(Gravity.RIGHT);
                }
                else {
                    drawerLayout.openDrawer(Gravity.RIGHT);
                }
                break;
        }
    }


    private void addNewAccount(String accountType, String authTokenType) {
        final AccountManagerFuture<Bundle> future = AccountManager.get(this).addAccount(accountType, authTokenType, null, null, this, new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> future) {
                try {
                    Bundle bnd = future.getResult();
                    Log.i("", "Account was created");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 5000) {
            UserProfileRequest userProfileRequest = new UserProfileRequest();
            RequestUserProfile requestUserProfile = new RequestUserProfile(activity, new RequestUserProfileTaskCompleteListener());
            requestUserProfile.execute(userProfileRequest);
        }

    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            drawerLayout.closeDrawer(Gravity.RIGHT);
        }else if (currentFragmet != 0){
            fragment = new MainFragment();
            if (userProfileDTO != null) {
                currentFragmet = 0;
                fragmentTitle = getString(R.string.title_main_fragment);
                user_manual.setVisibility(View.VISIBLE);
                bundle.putSerializable(Constants.USER_PROFILE_DTO, userProfileDTO);
                bundle.putInt(Constants.PENDING_PAYMENT_COUNT, pendingPaymentCount);
                bundle.putInt(Constants.PENDING_PURCHASE_COUNT, pendingPurchaseCount);
                fragment.setArguments(bundle);
            }
            setFragment(fragment, getString(R.string.title_main_fragment));
        }else if(false){

        }
        else{
            LogoutData logoutData = new LogoutData();
            logoutData.setIplanetDirectoryPro(prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
            new HamPayDialog(activity).showExitDialog(logoutData);
        }
    }

    public void getRegId(){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (googleCloudMessaging == null) {
                        googleCloudMessaging = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    registrationId = googleCloudMessaging.register(Constants.PROJECT_NUMBER);
                    msg = "Device registered, registration ID=" + registrationId;
                    Log.e("GCM", msg);

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();

                }catch (NullPointerException ex){
                    msg = "Error :" + ex.getMessage();
                }
                return registrationId;
            }

            @Override
            protected void onPostExecute(String registerId) {
                if (registerId != null) {
                    mobileRegistrationIdEntryRequest = new MobileRegistrationIdEntryRequest();
                    mobileRegistrationIdEntryRequest.setRegistrationId(registerId);
                    mobileRegistrationIdEntryRequest.setDeviceId(new DeviceInfo(activity).getAndroidId());
                    mobileRegistrationIdEntryRequest.setRequestUUID(UUID.randomUUID().toString());
                    requestMobileRegistrationIdEntry = new RequestMobileRegistrationIdEntry(context, new RequestMobileRegistrationIdEntryTaskCompleteListener());
                    requestMobileRegistrationIdEntry.execute(mobileRegistrationIdEntryRequest);
                }
            }
        }.execute(null, null, null);
    }

    @Override
    public void onFinishEditDialog(ActionImage actionImage) {
        switch (actionImage){
            case NOPE:
                break;

            case REMOVE_SUCCESS:
                image_profile.setImageResource(R.drawable.user_placeholder);
                break;

            case REMOVE_FAIL:
                new HamPayDialog(activity).removeImageFailDialog();
                if (userProfileDTO.getUserImageId() != null) {
                    String userImageUrl = Constants.HTTPS_SERVER_IP + Constants.IMAGE_PREFIX + authToken + "/" + userProfileDTO.getUserImageId();
                    image_profile.setTag(userImageUrl.split("/")[6]);
                    imageManager.displayImage(userImageUrl, image_profile, R.drawable.user_placeholder);
                }else {
                    image_profile.setImageResource(R.drawable.user_placeholder);
                }
                break;
        }
    }

    public class RequestMobileRegistrationIdEntryTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<MobileRegistrationIdEntryResponse>> {
        @Override
        public void onTaskComplete(ResponseMessage<MobileRegistrationIdEntryResponse> mobileRegistrationIdEntryResponseMessage)
        {
            if (mobileRegistrationIdEntryResponseMessage != null) {
                if (mobileRegistrationIdEntryResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {

                    editor.putBoolean(Constants.SEND_MOBILE_REGISTER_ID, true);
                    editor.commit();

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Mobile Registration Id Entry")
                            .setAction("Registration")
                            .setLabel("Success")
                            .build());
                }else if (mobileRegistrationIdEntryResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    forceLogout();
                }
                else {
                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Mobile Registration Id Entry")
                            .setAction("Registration")
                            .setLabel("Fail(Server)")
                            .build());
                }

            }else {
                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Mobile Registration Id Entry")
                        .setAction("Registration")
                        .setLabel("Fail(Mobile)")
                        .build());
            }
        }

        @Override
        public void onTaskPreRun() {   }
    }


    public class RequestPSPResultTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<PSPResultResponse>> {

        private String SWTrace;

        public RequestPSPResultTaskCompleteListener(String SWTrace){
            this.SWTrace = SWTrace;
        }

        @Override
        public void onTaskComplete(ResponseMessage<PSPResultResponse> pspResultResponseMessage) {

            if (pspResultResponseMessage != null){
                if (pspResultResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS){
                    if (SWTrace != null) {
                        dbHelper.syncPspResult(SWTrace);
                    }
                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Pending Payment Request")
                            .setAction("Payment")
                            .setLabel("Success")
                            .build());
                }else if (pspResultResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    forceLogout();
                }
            }
        }

        @Override
        public void onTaskPreRun() {
        }
    }

    public class RequestUserProfileTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<UserProfileResponse>>
    {
        public RequestUserProfileTaskCompleteListener(){
        }

        @Override
        public void onTaskComplete(ResponseMessage<UserProfileResponse> userProfileResponseMessage)
        {
            if (userProfileResponseMessage != null) {
                if (userProfileResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS){
                    userProfileDTO = userProfileResponseMessage.getService().getUserProfile();
                    fragment = new MainFragment();
                    if (userProfileDTO != null) {
                        bundle.putSerializable(Constants.USER_PROFILE_DTO, userProfileDTO);
                        bundle.putInt(Constants.PENDING_PAYMENT_COUNT, pendingPaymentCount);
                        bundle.putInt(Constants.PENDING_PURCHASE_COUNT, pendingPurchaseCount);
                        fragment.setArguments(bundle);
                        if (userProfileDTO.getUserImageId() != null) {
                            String userImageUrl = Constants.HTTPS_SERVER_IP + Constants.IMAGE_PREFIX + authToken + "/" + userProfileDTO.getUserImageId();
                            image_profile.setTag(userImageUrl.split("/")[6]);
                            imageManager = new ImageManager(activity, 200000, true);
                            imageManager.displayImage(userImageUrl, image_profile, R.drawable.user_placeholder);
                        }else {
                            image_profile.setImageResource(R.drawable.user_placeholder);
                        }
                        if (fragment != null) {
                            setFragment(fragment, getString(R.string.title_main_fragment));
                        }
                    }
                }else if (userProfileResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    forceLogout();
                }
                else{
                }
            }
            else {
            }
        }

        @Override
        public void onTaskPreRun() {
        }
    }

    private void setFragment(Fragment fragment, String title){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.replace(R.id.container_body, fragment).addToBackStack("tag").commit();
//        fragmentTransaction.commit();
        fragment_title.setText(title);
    }

    private void forceLogout() {
        editor.remove(Constants.LOGIN_TOKEN_ID);
        editor.commit();
        Intent intent = new Intent();
        intent.setClass(context, HamPayLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (activity != null) {
            finish();
            startActivity(intent);
        }
    }

}