package xyz.homapay.hampay.mobile.android.activity;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.UUID;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.MobileRegistrationIdEntryRequest;
import xyz.homapay.hampay.common.core.model.response.MobileRegistrationIdEntryResponse;
import xyz.homapay.hampay.common.core.model.response.dto.UserProfileDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestImageDownloader;
import xyz.homapay.hampay.mobile.android.async.RequestMobileRegistrationIdEntry;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.circleimageview.CircleImageView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.fragment.AboutFragment;
import xyz.homapay.hampay.mobile.android.fragment.AccountDetailFragment;
import xyz.homapay.hampay.mobile.android.fragment.FragmentDrawer;
import xyz.homapay.hampay.mobile.android.fragment.GuideFragment;
import xyz.homapay.hampay.mobile.android.fragment.PayToBusinessFragment;
import xyz.homapay.hampay.mobile.android.fragment.PayToOneFragment;
import xyz.homapay.hampay.mobile.android.fragment.SettingFragment;
import xyz.homapay.hampay.mobile.android.fragment.TCFragment;
import xyz.homapay.hampay.mobile.android.fragment.UserTransactionFragment;
import xyz.homapay.hampay.mobile.android.model.LogoutData;
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

    ImageView first_shortcut;
    ImageView second_shortcut;

    UserProfileDTO userProfileDTO;

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


    RequestImageDownloader requestImageDownloader;


    byte[] mobileKey;
    String serverKey;

    String encryptedData;
    String decryptedData;

    DeviceInfo deviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bundle = getIntent().getExtras();


        hamPayGaTracker = ((HamPayApplication) getApplication())
                .getTracker(HamPayApplication.TrackerName.APP_TRACKER);

        if (bundle != null) {
            fromNotification = bundle.getBoolean(Constants.NOTIFICATION);
        }

        activity = MainActivity.this;
        context = this;

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = activity.getSharedPreferences(Constants.APP_PREFERENCE_NAME, activity.MODE_PRIVATE).edit();



//        deviceInfo = new DeviceInfo(context);
//
//        try {
//
//            mobileKey = SecurityUtils.getInstance(this).generateSHA_256(
//                    deviceInfo.getMacAddress(),
//                    deviceInfo.getIMEI(),
//                    deviceInfo.getAndroidId());
//
//            serverKey = prefs.getString(Constants.USER_ID_TOKEN, "");
//
//            encryptedData = AESHelper.encrypt(mobileKey, serverKey, "ThisIsASecretKetThisIsASecretKetThisIsASecretKetThisIsASecretKetThisIsASecretKetThisIsASecretKetThisIsASecretKetThisIsASecretKet");
//            decryptedData = AESHelper.decrypt(mobileKey, serverKey, encryptedData.trim());
//        }
//        catch (Exception ex){
//            Log.e("Error", ex.getStackTrace().toString());
//        }


        Intent intent = getIntent();

        userProfileDTO = (UserProfileDTO) intent.getSerializableExtra(Constants.USER_PROFILE_DTO);

        fragment_title = (FacedTextView)findViewById(R.id.fragment_title);

        user_account_name = (FacedTextView)findViewById(R.id.user_account_name);
        image_profile = (CircleImageView)findViewById(R.id.image_profile);
        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HamPayDialog(activity).showUserProfileImage();
            }
        });



        first_shortcut = (ImageView)findViewById(R.id.first_shortcut);
        second_shortcut = (ImageView)findViewById(R.id.second_shortcut);

        first_shortcut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragment = null;
                String title = getString(R.string.app_name);

                switch (currentFragmet){
                    case 0:
                        fragment = new PayToOneFragment();
                        title = getString(R.string.title_pay_to_one);
                        currentFragmet = 2;

                        break;
                    case 1:
                        fragment = new PayToOneFragment();
                        title = getString(R.string.title_pay_to_one);
                        currentFragmet = 2;
                        break;
                    case 2:
                        fragment = new AccountDetailFragment();
                        bundle.putSerializable(Constants.USER_PROFILE_DTO, userProfileDTO);
                        fragment.setArguments(bundle);
                        title = getString(R.string.title_account_detail);
                        currentFragmet = 0;
                        break;
                    case 3:
                        fragment = new AccountDetailFragment();
                        bundle.putSerializable(Constants.USER_PROFILE_DTO, userProfileDTO);
                        fragment.setArguments(bundle);
                        title = getString(R.string.title_account_detail);
                        currentFragmet = 0;
                        break;
                }

                setupActionnBar(currentFragmet);

                if (fragment != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fragment);
                    fragmentTransaction.commit();

                    fragment_title.setText(title);

                }
            }
        });

        second_shortcut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragment = null;
                String title = getString(R.string.app_name);

                switch (currentFragmet){
                    case 0:
                        fragment = new UserTransactionFragment();
                        title = getString(R.string.title_transactions);
                        currentFragmet = 1;

                        break;
                    case 1:
                        fragment = new AccountDetailFragment();
                        bundle.putSerializable(Constants.USER_PROFILE_DTO, userProfileDTO);
                        fragment.setArguments(bundle);
                        title = getString(R.string.title_account_detail);
                        currentFragmet = 0;
                        break;
                    case 2:
                        fragment = new PayToBusinessFragment();
                        title = getString(R.string.title_pay_to_business);
                        currentFragmet = 3;
                        break;
                    case 3:
                        fragment = new PayToOneFragment();
                        title = getString(R.string.title_pay_to_one);
                        currentFragmet = 2;
                        break;

                }

                setupActionnBar(currentFragmet);

                if (fragment != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fragment);
                    fragmentTransaction.commit();

                    fragment_title.setText(title);

                }
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

        if (fromNotification){
            displayView(1);
        }else {
            displayView(currentFragmet);
        }


        if (!prefs.getBoolean(Constants.SEND_MOBILE_REGISTER_ID, false)) {
            getRegId();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        String filePath = getFilesDir().getPath().toString() + "/" + "userImage.png";
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
    public void onUserInteraction() {
        super.onUserInteraction();
        Log.e("EXIT", "onUserInteraction");
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
        if (currentFragmet != position || position == 1 || position == 5 || position == 7 || position == 8 || position == 9 || position == 10) {
            currentFragmet = position;
            displayView(position);
        }
    }

    private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                fragment = new AccountDetailFragment();
                if (userProfileDTO != null)
                    bundle.putSerializable(Constants.USER_PROFILE_DTO, userProfileDTO);
                fragment.setArguments(bundle);
                title = getString(R.string.title_account_detail);

                break;
            case 1:
                fragment = new UserTransactionFragment();
                title = getString(R.string.title_transactions);
                break;
            case 2:
                fragment = new PayToOneFragment();
                title = getString(R.string.title_pay_to_one);
                break;
            case 3:
                fragment = new PayToBusinessFragment();
                title = getString(R.string.title_pay_to_business);
                break;
            case 4:
                fragment = new SettingFragment();
                title = getString(R.string.title_settings);
                break;
            case 5:
                new HamPayDialog(activity).fetchContactUsInfo();
                break;
            case 6:
                fragment = new GuideFragment();
                title = getString(R.string.title_guide);
                break;
            case 7:
                fragment = new AboutFragment();
                title = getString(R.string.title_about);
                break;
            case 8:
                Uri uri = Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=بب" + context.getPackageName())));
                }
                break;
            case 9:
                fragment = new TCFragment();
                title = getString(R.string.title_alredy_tc);
                break;
            case 10:
                LogoutData logoutData = new LogoutData();
                logoutData.setIplanetDirectoryPro(prefs.getString(Constants.TOKEN_ID, null));
                new HamPayDialog(activity).showExitDialog(logoutData);
                break;

            default:
                break;
        }

        setupActionnBar(currentFragmet);

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            fragment_title.setText(title);

        }
    }

    private void setupActionnBar(int currentFragment){

        first_shortcut.setVisibility(View.VISIBLE);
        second_shortcut.setVisibility(View.VISIBLE);

        switch (currentFragment){
            case 0:
                first_shortcut.setImageResource(R.drawable.pay_ic_wit);
                second_shortcut.setImageResource(R.drawable.businnes_ic_wit);
                break;

            case 1:
                first_shortcut.setImageResource(R.drawable.pay_ic_wit);
                second_shortcut.setImageResource(R.drawable.account_ic_wit);
                break;

            case 2:
                first_shortcut.setImageResource(R.drawable.account_ic_wit);
                second_shortcut.setImageResource(R.drawable.transactions_ic_wit);
                break;

            case 3:
                first_shortcut.setImageResource(R.drawable.account_ic_wit);
                second_shortcut.setImageResource(R.drawable.pay_ic_wit);
                break;

            default:
                first_shortcut.setVisibility(View.GONE);
                second_shortcut.setVisibility(View.GONE);
                break;
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
                fragment = new PayToOneFragment();
                title = getString(R.string.title_pay_to_one);
                currentFragmet = 2;
                setupActionnBar(currentFragmet);
                if (fragment != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fragment);
                    fragmentTransaction.commit();
                    fragment_title.setText(title);
                }

            }
        }else if (requestCode == 1023){
            if(resultCode == 1023){
                fragment = new AccountDetailFragment();
                bundle.putSerializable(Constants.USER_PROFILE_DTO, userProfileDTO);
                fragment.setArguments(bundle);
                title = getString(R.string.title_account_detail);
                currentFragmet = 0;
                setupActionnBar(currentFragmet);
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
                setupActionnBar(currentFragmet);
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