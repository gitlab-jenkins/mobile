package com.hampay.mobile.android.activity;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.hampay.common.core.model.response.dto.UserProfileDTO;
import com.hampay.mobile.android.R;
import com.hampay.mobile.android.component.FacedTextView;
import com.hampay.mobile.android.dialog.HamPayDialog;
import com.hampay.mobile.android.fragment.AccountDetailFragment;
import com.hampay.mobile.android.fragment.FragmentDrawer;
import com.hampay.mobile.android.fragment.GuideFragment;
import com.hampay.mobile.android.fragment.PayToBusinessFragment;
import com.hampay.mobile.android.fragment.PayToOneFragment;
import com.hampay.mobile.android.fragment.SettingFragment;
import com.hampay.mobile.android.fragment.UserTransactionFragment;
import com.hampay.mobile.android.model.LogoutData;
import com.hampay.mobile.android.util.Constants;


public class MainActivity extends ActionBarActivity implements FragmentDrawer.FragmentDrawerListener, View.OnClickListener {

    private static String TAG = MainActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle mDrawerToggle;

    ImageView nav_icon;

    FacedTextView fragment_title;

    public static  FacedTextView user_account_name;

    int currentFragmet = 0;

    ImageView first_shortcut;
    ImageView second_shortcut;

    UserProfileDTO userProfileDTO;

    Activity activity;

    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bundle = new Bundle();

        activity = MainActivity.this;

        Intent intent = getIntent();

        userProfileDTO = (UserProfileDTO) intent.getSerializableExtra(Constants.USER_PROFILE_DTO);

        fragment_title = (FacedTextView)findViewById(R.id.fragment_title);

        user_account_name = (FacedTextView)findViewById(R.id.user_account_name);

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

        displayView(currentFragmet);
    }


    @Override
    public void onDrawerItemSelected(View view, int position) {
        if (currentFragmet != position || position == 1 || position == 7) {
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
                new HamPayDialog(activity).showContactUsDialog();
                break;
            case 6:
                fragment = new GuideFragment();
                title = getString(R.string.title_guide);
                break;
            case 7:

                SharedPreferences prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
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
    public void onBackPressed() {
        SharedPreferences prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);

        LogoutData logoutData = new LogoutData();
        logoutData.setIplanetDirectoryPro(prefs.getString(Constants.TOKEN_ID, null));
        new HamPayDialog(activity).showExitDialog(logoutData);
    }
}