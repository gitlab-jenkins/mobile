package xyz.homapay.hampay.mobile.android.activity;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.app.Notification;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import br.com.goncalves.pugnotification.notification.PugNotification;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.MobileRegistrationIdEntryRequest;
import xyz.homapay.hampay.common.core.model.response.MobileRegistrationIdEntryResponse;
import xyz.homapay.hampay.common.core.model.response.dto.UserProfileDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.Helper.DatabaseHelper;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestMobileRegistrationIdEntry;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.circleimageview.CircleImageView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.fragment.AboutFragment;
import xyz.homapay.hampay.mobile.android.fragment.AccountDetailFragment;
import xyz.homapay.hampay.mobile.android.fragment.FragmentDrawer;
import xyz.homapay.hampay.mobile.android.fragment.GuideFragment;
import xyz.homapay.hampay.mobile.android.fragment.MainFragment;
import xyz.homapay.hampay.mobile.android.fragment.PayToBusinessFragment;
import xyz.homapay.hampay.mobile.android.fragment.CreditRequestFragment;
import xyz.homapay.hampay.mobile.android.fragment.PaymentRequestFragment;
import xyz.homapay.hampay.mobile.android.fragment.PendingPaymentFragment;
import xyz.homapay.hampay.mobile.android.fragment.PrivacyFragment;
import xyz.homapay.hampay.mobile.android.fragment.SettingFragment;
import xyz.homapay.hampay.mobile.android.fragment.TCFragment;
import xyz.homapay.hampay.mobile.android.fragment.UserTransactionFragment;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.model.LatestPurchase;
import xyz.homapay.hampay.mobile.android.model.LogoutData;
import xyz.homapay.hampay.mobile.android.model.NotificationMessageType;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.DeviceInfo;


public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener, View.OnClickListener {

    private static String TAG = MainActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle mDrawerToggle;

    ImageView nav_icon;

    FacedTextView fragment_title;

    public static  FacedTextView user_account_name;

    CircleImageView image_profile;

    int currentFragmet = 0;

    UserProfileDTO userProfileDTO;

    String pendingPurchasePaymentId = "";
    int pendingPurchasePaymentCount = 0;

    Activity activity;

    Bundle bundle;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    boolean fromNotification = false;

    HamPayDialog hamPayDialog;

    Tracker hamPayGaTracker;

    Context context;

    RequestMobileRegistrationIdEntry requestMobileRegistrationIdEntry;
    MobileRegistrationIdEntryRequest mobileRegistrationIdEntryRequest;

    Intent intent;

    DatabaseHelper databaseHelper;

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

        String filePath = getFilesDir().getPath().toString() + "/" + "userImage.jpeg";
        File file = new File(filePath);
        if (file.exists()){
            Picasso.with(this).invalidate(file);
            Picasso.with(this).load(file).into(image_profile);

        }

//        String URL = Constants.HTTPS_SERVER_IP + "/users/" + prefs.getString(Constants.LOGIN_TOKEN_ID, "") + "/" + userProfileDTO.getUserImageId();

//        requestImageDownloader = new RequestImageDownloader(context, new RequestImageDownloaderTaskCompleteListener(image_profile));
//        requestImageDownloader.execute(URL);

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

        bundle = getIntent().getExtras();

        Intent intent = getIntent();

        pendingPurchasePaymentId = bundle.getString(Constants.PENDING_PURCHASE_PAYMENT_ID, "");
        pendingPurchasePaymentCount = bundle.getInt(Constants.PENDING_PURCHASE_PAYMENT_COUNT, 0);
        userProfileDTO = (UserProfileDTO) intent.getSerializableExtra(Constants.USER_PROFILE_DTO);


//        PugNotification.with(context)
//                .load()
//                .identifier(1020)
//                .title("TEST")
//                .message("WelCome")
//                .bigTextStyle(":))))))))))))))")
//                .smallIcon(R.mipmap.ic_launcher)
////                .largeIcon(largeIcon)
//                .flags(Notification.DEFAULT_ALL)
////                .button(icon, title, pendingIntent)
//                .click(UnlinkPassActivity.class, bundle)
//                .dismiss(MainActivity.class, bundle)
//                .color(R.color.colorPrimary)
//                .ticker("ticker")
////                .when(when)
////                .vibrate(100)
////                .lights(color, ledOnMs, ledOfMs)
////                .sound(sound)
//                .autoCancel(true)
//                .simple()
//                .build();


        hamPayGaTracker = ((HamPayApplication) getApplication())
                .getTracker(HamPayApplication.TrackerName.APP_TRACKER);

        if (bundle != null) {
            fromNotification = bundle.getBoolean(Constants.NOTIFICATION);
        }



        databaseHelper = new DatabaseHelper(context);

        List<LatestPurchase> latestPurchaseList = databaseHelper.getAllLatestPurchases();

        if (pendingPurchasePaymentId.length() != 0) {
            if (databaseHelper.getIsExistPurchaseRequest(pendingPurchasePaymentId)) {
                LatestPurchase latestPurchase = databaseHelper.getPurchaseRequest(pendingPurchasePaymentId);
                if (latestPurchase.getIsCanceled().equalsIgnoreCase("0")) {
                    intent.setClass(context, RequestBusinessPayDetailActivity.class);
                    startActivity(intent);
                }
            }else {
                databaseHelper.createPurchaseRequest(pendingPurchasePaymentId);
                intent.setClass(context, RequestBusinessPayDetailActivity.class);
                startActivity(intent);
            }
        }

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = activity.getSharedPreferences(Constants.APP_PREFERENCE_NAME, activity.MODE_PRIVATE).edit();

        editor.putLong(Constants.MAX_BUSINESS_XFER_AMOUNT, this.userProfileDTO.getMaxBusinessXferAmount());
        editor.putLong(Constants.MIN_BUSINESS_XFER_AMOUNT, this.userProfileDTO.getMinBusinessXferAmount());
        editor.putLong(Constants.MAX_INDIVIDUAL_XFER_AMOUNT, this.userProfileDTO.getMaxIndividualXferAmount());
        editor.putLong(Constants.MIN_INDIVIDUAL_XFER_AMOUNT, this.userProfileDTO.getMinIndividualXferAmount());
        editor.commit();

        fragment_title = (FacedTextView)findViewById(R.id.fragment_title);

        user_account_name = (FacedTextView)findViewById(R.id.user_account_name);
        user_account_name.setText(userProfileDTO.getFullName());
        image_profile = (CircleImageView)findViewById(R.id.image_profile);
        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HamPayDialog(activity).showUserProfileImage();
            }
        });


//        addNewAccount(AccountGeneral.ACCOUNT_TYPE, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS);
//        ContactsManager.addContact(MainActivity.this, new HamPayContact("", "Sharafkar", "", "09122020200"));
//        ContactsManager.addContact(MainActivity.this, new HamPayContact("", "Sharafkar", "", "09122020200"));


        nav_icon = (ImageView)findViewById(R.id.nav_icon);
        nav_icon.setOnClickListener(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        //setSupportActionBar(mToolbar);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
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
                //getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                //getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }

        };

        displayView(currentFragmet);

//        if (fromNotification){
//            displayView(1);
//        }else {
//
//        }


        if (!prefs.getBoolean(Constants.SEND_MOBILE_REGISTER_ID, false)) {
            getRegId();
        }


        if (bundle != null) {
            if (bundle.getBoolean(Constants.HAS_NOTIFICATION)) {
                NotificationMessageType notificationMessageType;
                notificationMessageType = NotificationMessageType.valueOf(bundle.getString(Constants.NOTIFICATION_TYPE));

                Intent notificationIntent;

                switch (notificationMessageType){
                    case PAYMENT:

                        displayView(2);

                        break;

                    case CREDIT_REQUEST:
                        notificationIntent = getIntent();
                        notificationIntent.setClass(activity, IndividualPaymentPendingActivity.class);
                        startActivity(notificationIntent);
                        break;
                }

            }
        }

    }






    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
//        Log.e("EXIT", "onUserInteraction");
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        Log.e("EXIT", "onUserLeaveHint");
        editor.putString(Constants.USER_ID_TOKEN, "");
        editor.commit();
    }


    @Override
    public void onDrawerItemSelected(View view, int position) {
        if (currentFragmet != position || position == 1 || position == 5 || position == 7 || position == 8 || position == 9 || position == 10 || position == 12) {
            currentFragmet = position;
            displayView(position);
        }
    }

    private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {

            case 0:
                fragment = new MainFragment();
                if (userProfileDTO != null) {
                    bundle.putSerializable(Constants.USER_PROFILE_DTO, userProfileDTO);
                    fragment.setArguments(bundle);
                }
                title = getString(R.string.title_main_fragment);
                break;
            case 1:
                fragment = new AccountDetailFragment();
                if (userProfileDTO != null) {
                    bundle.putSerializable(Constants.USER_PROFILE_DTO, userProfileDTO);
                    fragment.setArguments(bundle);
                }
                title = getString(R.string.title_account_detail);
                break;
            case 2:
                fragment = new SettingFragment();
                title = getString(R.string.title_settings);
                break;
//                fragment = new UserTransactionFragment();
//                title = getString(R.string.title_transactions);
//                break;
            case 3:
                new HamPayDialog(activity).fetchContactUsInfo();
                break;
//                fragment = new PendingPaymentFragment();
//                title = getString(R.string.title_pending_payment);
//                break;
            case 4:
                fragment = new GuideFragment();
                title = getString(R.string.title_guide);
                break;
//                fragment = new CreditRequestFragment();
//                title = getString(R.string.title_credit_request);
//                break;
            case 5:
                LogoutData logoutData = new LogoutData();
                logoutData.setIplanetDirectoryPro(prefs.getString(Constants.LOGIN_TOKEN_ID, null));
                new HamPayDialog(activity).showExitDialog(logoutData);
                break;
//                fragment = new SettingFragment();
//                title = getString(R.string.title_settings);
//                break;
            case 6:
                new HamPayDialog(activity).fetchContactUsInfo();
                break;
            case 7:
                fragment = new GuideFragment();
                title = getString(R.string.title_guide);
                break;
            case 8:
                fragment = new AboutFragment();
                title = getString(R.string.title_about);
                break;
            case 9:
                Uri uri = Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + context.getPackageName())));
                }
                break;
            case 10:
                fragment = new TCFragment();
                title = getString(R.string.title_already_tc);
                break;
            case 11:
                fragment = new PrivacyFragment();
                title = getString(R.string.title_already_privacy);
                break;
            case 12:
//                LogoutData logoutData = new LogoutData();
//                logoutData.setIplanetDirectoryPro(prefs.getString(Constants.LOGIN_TOKEN_ID, null));
//                new HamPayDialog(activity).showExitDialog(logoutData);
                break;

            default:
                break;
        }


        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            fragment_title.setText(title);

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

        Fragment fragment;
        String title;

        if (requestCode == 1024) {
            if(resultCode == 1024){
//                String result = data.getStringExtra("result");
                fragment = new CreditRequestFragment();
                title = getString(R.string.title_credit_request);
                currentFragmet = 2;
                if (fragment != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fragment);
                    fragmentTransaction.commit();
                    fragment_title.setText(title);
                }

            }
        }else if (resultCode == 1023){
            if(resultCode == 1023){
                fragment = new AccountDetailFragment();
                bundle.putSerializable(Constants.USER_PROFILE_DTO, userProfileDTO);
                fragment.setArguments(bundle);
                title = getString(R.string.title_account_detail);
                currentFragmet = 0;
                if (fragment != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fragment);
                    fragmentTransaction.commit();
                    fragment_title.setText(title);
                }
            }
        }else if (requestCode == 5000){
            if (resultCode == 5000){
                fragment = new AccountDetailFragment();
                bundle.putSerializable(Constants.USER_PROFILE_DTO, userProfileDTO);
                fragment.setArguments(bundle);
                title = getString(R.string.title_account_detail);
                currentFragmet = 0;
                if (fragment != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fragment);
                    fragmentTransaction.commit();
                    fragment_title.setText(title);
                }
            }
        }

    }

    @Override
    public void onBackPressed() {
        LogoutData logoutData = new LogoutData();
        logoutData.setIplanetDirectoryPro(prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
        new HamPayDialog(activity).showExitDialog(logoutData);
    }



    GoogleCloudMessaging gcm;
    String regid;
    String PROJECT_NUMBER = "936219454834";

    public void getRegId(){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    regid = gcm.register(PROJECT_NUMBER);
                    msg = "Device registered, registration ID=" + regid;
                    Log.e("GCM", msg);

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();

                }catch (NullPointerException ex){
                    msg = "Error :" + ex.getMessage();
                }
                return regid;
            }

            @Override
            protected void onPostExecute(String registerId) {
                if (registerId != null) {
                    mobileRegistrationIdEntryRequest = new MobileRegistrationIdEntryRequest();
                    mobileRegistrationIdEntryRequest.setRegistrationId(registerId);
                    mobileRegistrationIdEntryRequest.setDeviceId(new DeviceInfo(context).getAndroidId());
                    mobileRegistrationIdEntryRequest.setRequestUUID(UUID.randomUUID().toString());
                    requestMobileRegistrationIdEntry = new RequestMobileRegistrationIdEntry(context, new RequestMobileRegistrationIdEntryTaskCompleteListener());
                    requestMobileRegistrationIdEntry.execute(mobileRegistrationIdEntryRequest);
                }
            }
        }.execute(null, null, null);
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



}