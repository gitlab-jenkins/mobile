package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

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
import xyz.homapay.hampay.mobile.android.firebase.LogEvent;
import xyz.homapay.hampay.mobile.android.firebase.app.AppEvent;
import xyz.homapay.hampay.mobile.android.firebase.service.ServiceEvent;
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
    private RelativeLayout wtContainer;
    private int walkThroughStep = 0;
    private RelativeLayout wtFirstLayout;
    private RelativeLayout wtSecondLayout;
    private RelativeLayout wtThirdLayout;
    private RelativeLayout wtFourthLayout;
    private RelativeLayout wtFifthLayout;
    private RelativeLayout wtSixthLayout;
    private FacedTextView transactionNote;
    private DrawerLayout drawerLayout;
    private ImageView nav_icon;
    private FacedTextView fragment_title;
    private ImageView image_profile;
    private LinearLayout user_image_layout;
    private int currentFragment = 0;
    private UserProfileDTO userProfileDTO;
    private String pendingPurchaseCode = null;
    private String pendingPaymentCode = null;
    private int pendingPurchaseCount = 0;
    private int pendingPaymentCount = 0;
    private boolean showCreateInvoice = true;
    private Activity activity;
    private Bundle bundle = new Bundle();
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private boolean hasNotification = false;
    private Context context;
    private RequestMobileRegistrationIdEntry requestMobileRegistrationIdEntry;
    private MobileRegistrationIdEntryRequest mobileRegistrationIdEntryRequest;
    private DatabaseHelper dbHelper;
    private ImageManager imageManager;
    private ImageView user_manual;
    private String fragmentTitle = "";
    private AppEvent appEvent = AppEvent.LOGIN;

    public void userManual(View view){
        Intent intent = new Intent();
        intent.setClass(activity, UserManualActivity.class);
        if (currentFragment == 0) {
            intent.putExtra(Constants.USER_MANUAL_TEXT, R.string.user_manual_text_main);
            intent.putExtra(Constants.USER_MANUAL_TITLE, R.string.user_manual_title_main);
            startActivity(intent);
        }else if (currentFragment == 1){
            intent.putExtra(Constants.USER_MANUAL_TEXT, R.string.user_manual_text_account_details);
            intent.putExtra(Constants.USER_MANUAL_TITLE, R.string.user_manual_title_account_details);
            startActivity(intent);
        }
        else if (currentFragment == 2){
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
        LogEvent logEvent = new LogEvent(this);
        logEvent.log(appEvent);

        bundle = getIntent().getExtras();

        Intent intent = getIntent();

        pendingPurchaseCode = bundle.getString(Constants.PENDING_PURCHASE_CODE);
        pendingPaymentCode = bundle.getString(Constants.PENDING_PAYMENT_CODE);
        pendingPurchaseCount = bundle.getInt(Constants.PENDING_PURCHASE_COUNT, 0);
        pendingPaymentCount = bundle.getInt(Constants.PENDING_PAYMENT_COUNT, 0);
        showCreateInvoice = bundle.getBoolean(Constants.SHOW_CREATE_INVOICE, true);

        userProfileDTO = (UserProfileDTO) intent.getSerializableExtra(Constants.USER_PROFILE_DTO);
        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = activity.getSharedPreferences(Constants.APP_PREFERENCE_NAME, activity.MODE_PRIVATE).edit();
        if (!prefs.contains(Constants.SETTING_CHANGE_IBAN_STATUS)){
            editor.putBoolean(Constants.SETTING_CHANGE_IBAN_STATUS, false);
            editor.commit();
        }

        editor.putLong(Constants.MAX_BUSINESS_XFER_AMOUNT, this.userProfileDTO.getMaxBusinessXferAmount());
        editor.putLong(Constants.MIN_BUSINESS_XFER_AMOUNT, this.userProfileDTO.getMinBusinessXferAmount());
        editor.putLong(Constants.MAX_INDIVIDUAL_XFER_AMOUNT, this.userProfileDTO.getMaxIndividualXferAmount());
        editor.putLong(Constants.MIN_INDIVIDUAL_XFER_AMOUNT, this.userProfileDTO.getMinIndividualXferAmount());
        editor.commit();
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
                }
            }
        }

        wtContainer = (RelativeLayout)findViewById(R.id.wt_container);
        wtFirstLayout = (RelativeLayout)findViewById(R.id.wt_first_layout);
        wtSecondLayout = (RelativeLayout)findViewById(R.id.wt_second_layout);
        wtThirdLayout = (RelativeLayout)findViewById(R.id.wt_third_layout);
        wtFourthLayout = (RelativeLayout)findViewById(R.id.wt_fourth_layout);
        wtFifthLayout = (RelativeLayout)findViewById(R.id.wt_fifth_layout);
        wtSixthLayout = (RelativeLayout)findViewById(R.id.wt_sixth_layout);
        transactionNote = (FacedTextView)findViewById(R.id.transaction_note);

        if (prefs.getBoolean(Constants.SHOW_WALK_THROUGH, true)){
            wtContainer.setVisibility(View.VISIBLE);
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
        nav_icon = (ImageView)findViewById(R.id.nav_icon);
        nav_icon.setOnClickListener(this);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), null);
        drawerFragment.setDrawerListener(this);

        if (userProfileDTO.getUserImageId() != null) {
            image_profile.setTag(userProfileDTO.getUserImageId());
            imageManager.displayImage(userProfileDTO.getUserImageId(), image_profile, R.drawable.user_placeholder);
        }else {
            image_profile.setImageResource(R.drawable.user_placeholder);
        }
        displayView(currentFragment);

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
    }


    @Override
    public void onDrawerItemSelected(View view, int position) {
        if (currentFragment != position || position == 4|| position == 6 || position == 7 || position == 8 || position == 9) {
            displayView(position);
        }
    }

    private void displayView(int position) {
        Intent intent = new Intent();
        switch (position) {
            case 0:
                currentFragment = 0;
                user_manual.setVisibility(View.VISIBLE);
                fragment = new MainFragment();
                if (userProfileDTO != null) {
                    bundle.putSerializable(Constants.USER_PROFILE_DTO, userProfileDTO);
                    bundle.putInt(Constants.PENDING_PAYMENT_COUNT, pendingPaymentCount);
                    bundle.putInt(Constants.PENDING_PURCHASE_COUNT, pendingPurchaseCount);
                    bundle.putBoolean(Constants.SHOW_CREATE_INVOICE, showCreateInvoice);
                    fragment.setArguments(bundle);
                }
                fragmentTitle = getString(R.string.title_main_fragment);
                break;
            case 1:
                currentFragment = 1;
                user_manual.setVisibility(View.VISIBLE);
                fragment = new AccountDetailFragment();
                if (userProfileDTO != null) {
                    bundle.putSerializable(Constants.USER_PROFILE_DTO, userProfileDTO);
                    fragment.setArguments(bundle);
                }
                fragmentTitle = getString(R.string.title_account_detail);
                break;
            case 2:
                currentFragment = 2;
                user_manual.setVisibility(View.VISIBLE);
                fragment = new SettingFragment();
                fragmentTitle = getString(R.string.title_settings);
                break;

            case 3:
                currentFragment = 3;
                user_manual.setVisibility(View.GONE);
                fragment = new GuideFragment();
                fragmentTitle = getString(R.string.title_guide);
                break;

            case 4:
                currentFragment = 0;
                user_manual.setVisibility(View.VISIBLE);
                fragment = new MainFragment();
                if (userProfileDTO != null) {
                    bundle.putSerializable(Constants.USER_PROFILE_DTO, userProfileDTO);
                    bundle.putInt(Constants.PENDING_PAYMENT_COUNT, pendingPaymentCount);
                    bundle.putInt(Constants.PENDING_PURCHASE_COUNT, pendingPurchaseCount);
                    bundle.putBoolean(Constants.SHOW_CREATE_INVOICE, showCreateInvoice);
                    fragment.setArguments(bundle);
                }
                fragmentTitle = getString(R.string.title_main_fragment);
                wtFirstLayout.setVisibility(View.VISIBLE);
                wtContainer.setVisibility(View.VISIBLE);
                break;

            case 5:
                currentFragment = 4;
                user_manual.setVisibility(View.GONE);
                fragment = new AboutFragment();
                fragmentTitle = getString(R.string.title_hampay_about);
                break;

            case 6:
                new HamPayDialog(activity).fetchContactUsInfo();
                break;

            case 7:
                intent.setClass(activity, GuideDetailActivity.class);
                intent.putExtra(Constants.WEB_PAGE_ADDRESS, Constants.HTTPS_SERVER_IP + "/users/tac-file");
                intent.putExtra(Constants.TAC_PRIVACY_TITLE, activity.getString(R.string.tac_title_activity));
                activity.startActivity(intent);
                break;
            case 8:
                intent.setClass(activity, GuideDetailActivity.class);
                intent.putExtra(Constants.WEB_PAGE_ADDRESS, Constants.HTTPS_SERVER_IP + "/users/privacy-file");
                Log.e("URL", Constants.HTTPS_SERVER_IP + "/users/privacy-file");
                intent.putExtra(Constants.TAC_PRIVACY_TITLE, activity.getString(R.string.privacy_title_activity));
                activity.startActivity(intent);
                break;

            case 9:
                LogoutData logoutData = new LogoutData();
                logoutData.setIplanetDirectoryPro(prefs.getString(Constants.LOGIN_TOKEN_ID, null));
                new HamPayDialog(activity).showLogoutDialog();
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


    public void wt_click(View view){
        switch (view.getId()){

            case R.id.next_wt:
                walkThroughStep = (walkThroughStep + 1) % 7;
                switch (walkThroughStep){
                    case 1:
                        wtFirstLayout.setVisibility(View.GONE);
                        wtSecondLayout.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        wtSecondLayout.setVisibility(View.GONE);
                        wtThirdLayout.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        wtThirdLayout.setVisibility(View.GONE);
                        wtFourthLayout.setVisibility(View.VISIBLE);
                        break;
                    case 4:
                        wtFourthLayout.setVisibility(View.GONE);
                        wtFifthLayout.setVisibility(View.VISIBLE);
                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(getString(R.string.wt_note_6));
                        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.rgb(117, 198, 219));
                        spannableStringBuilder.setSpan(foregroundColorSpan, 8, 14, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                        foregroundColorSpan = new ForegroundColorSpan(Color.rgb(114, 189, 119));
                        spannableStringBuilder.setSpan(foregroundColorSpan, 20, 26, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                        foregroundColorSpan = new ForegroundColorSpan(Color.rgb(213, 61, 66));
                        spannableStringBuilder.setSpan(foregroundColorSpan, 300, 307, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                        transactionNote.setText(spannableStringBuilder);
                        break;
                    case 5:
                        wtFifthLayout.setVisibility(View.GONE);
                        wtSixthLayout.setVisibility(View.VISIBLE);
                        editor.putBoolean(Constants.SHOW_WALK_THROUGH, false).commit();
                        break;
                    case 6:
                        walkThroughStep = 0;
                        wtSixthLayout.setVisibility(View.GONE);
                        wtSecondLayout.setVisibility(View.GONE);
                        wtThirdLayout.setVisibility(View.GONE);
                        wtFourthLayout.setVisibility(View.GONE);
                        wtFifthLayout.setVisibility(View.GONE);
                        wtSixthLayout.setVisibility(View.GONE);
                        wtContainer.setVisibility(View.GONE);
                        break;
                }

                break;

            case R.id.pre_wt:

                if (walkThroughStep > 0)    walkThroughStep--;

                switch (walkThroughStep) {

                    case 0:
                        wtFirstLayout.setVisibility(View.VISIBLE);
                        wtSecondLayout.setVisibility(View.GONE);
                        break;
                    case 1:
                        wtSecondLayout.setVisibility(View.VISIBLE);
                        wtThirdLayout.setVisibility(View.GONE);
                        break;
                    case 2:
                        wtThirdLayout.setVisibility(View.VISIBLE);
                        wtFourthLayout.setVisibility(View.GONE);
                        break;
                    case 3:
                        wtFourthLayout.setVisibility(View.VISIBLE);
                        wtFifthLayout.setVisibility(View.GONE);
                        break;
                    case 4:
                        wtFifthLayout.setVisibility(View.VISIBLE);
                        wtSixthLayout.setVisibility(View.GONE);
                        break;
                    case 5:
                        wtSixthLayout.setVisibility(View.VISIBLE);
                        break;
                }
                break;

            case R.id.wt_dismiss:
                walkThroughStep = 0;
                wtFirstLayout.setVisibility(View.GONE);
                wtSecondLayout.setVisibility(View.GONE);
                wtThirdLayout.setVisibility(View.GONE);
                wtFourthLayout.setVisibility(View.GONE);
                wtFifthLayout.setVisibility(View.GONE);
                wtSixthLayout.setVisibility(View.GONE);
                wtContainer.setVisibility(View.GONE);
                editor.putBoolean(Constants.SHOW_WALK_THROUGH, false).commit();
                break;

        }
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

        if (wtContainer.getVisibility() == View.VISIBLE){
            wtContainer.setVisibility(View.GONE);
            return;
        }


        if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            drawerLayout.closeDrawer(Gravity.RIGHT);
        }else if (currentFragment != 0){
            fragment = new MainFragment();
            if (userProfileDTO != null) {
                currentFragment = 0;
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
            new HamPayDialog(activity).showLogoutDialog();
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
                currentFragment = 0;
                user_manual.setVisibility(View.VISIBLE);
                fragment = new MainFragment();
                if (userProfileDTO != null) {
                    bundle.putSerializable(Constants.USER_PROFILE_DTO, userProfileDTO);
                    bundle.putInt(Constants.PENDING_PAYMENT_COUNT, pendingPaymentCount);
                    bundle.putInt(Constants.PENDING_PURCHASE_COUNT, pendingPurchaseCount);
                    bundle.putBoolean(Constants.SHOW_CREATE_INVOICE, showCreateInvoice);
                    fragment.setArguments(bundle);
                }
                fragmentTitle = getString(R.string.title_main_fragment);
                if (fragment != null) {
                    setFragment(fragment, fragmentTitle);
                }
                break;

            case REMOVE_FAIL:
                new HamPayDialog(activity).removeImageFailDialog();
                if (userProfileDTO.getUserImageId() != null) {
                    image_profile.setTag( userProfileDTO.getUserImageId());
                    imageManager.displayImage( userProfileDTO.getUserImageId(), image_profile, R.drawable.user_placeholder);
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
            ServiceEvent serviceName;
            LogEvent logEvent = new LogEvent(context);

            if (mobileRegistrationIdEntryResponseMessage != null) {
                if (mobileRegistrationIdEntryResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    serviceName = ServiceEvent.APP_REGISTRATION_ID_ENTRY_SUCCESS;
                    editor.putBoolean(Constants.SEND_MOBILE_REGISTER_ID, true);
                    editor.commit();
                }else if (mobileRegistrationIdEntryResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    serviceName = ServiceEvent.APP_REGISTRATION_ID_ENTRY_FAILURE;
                    forceLogout();
                }
                else {
                    serviceName = ServiceEvent.APP_REGISTRATION_ID_ENTRY_FAILURE;
                }
                logEvent.log(serviceName);
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

                ResultStatus resultStatus = pspResultResponseMessage.getService().getResultStatus();

                if (resultStatus == ResultStatus.SUCCESS || resultStatus == ResultStatus.PAYMENT_NOT_FOUND || resultStatus == ResultStatus.PURCHASE_NOT_FOUND || resultStatus == ResultStatus.INVALID_FUND_STATUS_EXCEPTION){
                    if (SWTrace != null) {
                        dbHelper.syncPspResult(SWTrace);
                    }
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
        ServiceEvent serviceName;
        LogEvent logEvent = new LogEvent(context);

        public RequestUserProfileTaskCompleteListener(){
        }

        @Override
        public void onTaskComplete(ResponseMessage<UserProfileResponse> userProfileResponseMessage)
        {
            if (userProfileResponseMessage != null) {
                if (userProfileResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS){
                    serviceName = ServiceEvent.USER_PROFILE_SUCCESS;
                    userProfileDTO = userProfileResponseMessage.getService().getUserProfile();
                    currentFragment = 0;
                    fragment = new MainFragment();
                    if (userProfileDTO != null) {
                        bundle.putSerializable(Constants.USER_PROFILE_DTO, userProfileDTO);
                        bundle.putInt(Constants.PENDING_PAYMENT_COUNT, pendingPaymentCount);
                        bundle.putInt(Constants.PENDING_PURCHASE_COUNT, pendingPurchaseCount);
                        fragment.setArguments(bundle);
                        if (userProfileDTO.getUserImageId() != null) {
                            image_profile.setTag(userProfileDTO.getUserImageId());
                            imageManager = new ImageManager(activity, 200000, true);
                            imageManager.displayImage(userProfileDTO.getUserImageId(), image_profile, R.drawable.user_placeholder);
                        }else {
                            image_profile.setImageResource(R.drawable.user_placeholder);
                        }
                        if (fragment != null) {
                            setFragment(fragment, getString(R.string.title_main_fragment));
                        }
                    }
                }else if (userProfileResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    serviceName = ServiceEvent.USER_PROFILE_FAILURE;
                    forceLogout();
                }
                else{
                    serviceName = ServiceEvent.USER_PROFILE_FAILURE;
                }
            }
            else {
                serviceName = ServiceEvent.USER_PROFILE_FAILURE;
            }
            logEvent.log(serviceName);
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