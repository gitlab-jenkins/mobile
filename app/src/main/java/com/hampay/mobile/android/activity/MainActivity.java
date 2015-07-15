package com.hampay.mobile.android.activity;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.ContactUsRequest;
import com.hampay.common.core.model.response.ContactUsResponse;
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
import com.hampay.mobile.android.fragment.TransactionFragment;
import com.hampay.mobile.android.serialize.UserProfile;
import com.hampay.mobile.android.util.Constants;
import com.hampay.mobile.android.webservice.WebServices;


public class MainActivity extends ActionBarActivity implements FragmentDrawer.FragmentDrawerListener, View.OnClickListener {

    private static String TAG = MainActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle mDrawerToggle;

    ImageView nav_icon;

    FacedTextView fragment_title;

    public static  FacedTextView user_account_name;

    Dialog dialogExitApp;

    int currentFragmet = 0;

    ImageView first_shortcut;
    ImageView second_shortcut;

    UserProfileDTO userProfileDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Intent intent = getIntent();;

//        if (HamPayDialog.userProfileDTO != null){
            userProfileDTO = (UserProfileDTO) intent.getSerializableExtra(Constants.USER_PROFILE_DTO);
//        }

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
                        fragment = new AccountDetailFragment(userProfileDTO);
                        title = getString(R.string.title_account_detail);
                        currentFragmet = 0;
                        break;
                    case 3:
                        fragment = new AccountDetailFragment(userProfileDTO);
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
                        fragment = new TransactionFragment();
                        title = getString(R.string.title_transactions);
                        currentFragmet = 1;

                        break;
                    case 1:
                        fragment = new AccountDetailFragment(userProfileDTO);
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

        //new HttpBanks().execute();

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if(id == R.id.action_search){
            Toast.makeText(getApplicationContext(), "Search action is selected!", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        if (currentFragmet != position || position == 1) {
            currentFragmet = position;
            displayView(position);
        }
    }

    private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                fragment = new AccountDetailFragment(userProfileDTO);
                title = getString(R.string.title_account_detail);

                break;
            case 1:
                fragment = new TransactionFragment();
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
                showContactUs();
                break;
            case 6:
                fragment = new GuideFragment();
                title = getString(R.string.title_guide);
                break;
            case 7:
                showExitDialog();
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

    private void showExitDialog(){
        Rect displayRectangle = new Rect();
        Activity parent = (Activity) MainActivity.this;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = getLayoutInflater().inflate(R.layout.dialog_exit_app, null);

        FacedTextView exit_app_yes = (FacedTextView) view.findViewById(R.id.exit_app_yes);
        FacedTextView exit_app_no = (FacedTextView) view.findViewById(R.id.exit_app_no);

        exit_app_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogExitApp.dismiss();
                finish();
            }
        });

        exit_app_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogExitApp.dismiss();
            }
        });

        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
        dialogExitApp = new Dialog(MainActivity.this);
        dialogExitApp.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogExitApp.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogExitApp.setContentView(view);
        dialogExitApp.setTitle(null);
        dialogExitApp.setCanceledOnTouchOutside(false);

        dialogExitApp.show();
    }


    private void showContactUs(){

//        Rect displayRectangle = new Rect();
//        Activity parent = (Activity) MainActivity.this;
//        Window window = parent.getWindow();
//        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
//
//        View view = getLayoutInflater().inflate(R.layout.dialog_contact_us, null);
//
//        FacedTextView send_message = (FacedTextView) view.findViewById(R.id.send_message);
//        FacedTextView call_message = (FacedTextView) view.findViewById(R.id.call_message);
//
//        ContactUsRequest contactUsRequest = new ContactUsRequest();
//        contactUsRequest.setRequestUUID("");
//        new HttpContactUs().execute(contactUsRequest);
//
//        send_message.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialogExitApp.dismiss();
//
//                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
//                        "mailto", contactUsMail, null));
//                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
//                emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.insert_message));
//                startActivity(Intent.createChooser(emailIntent, getString(R.string.hampay_contact)));
//            }
//        });
//
//        call_message.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialogExitApp.dismiss();
//
//                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contactUsPhone));
//                startActivity(intent);
//            }
//        });
//
//        view.setMinimumWidth((int) (displayRectangle.width() * 0.85f));
//        dialogExitApp = new Dialog(MainActivity.this);
//        dialogExitApp.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogExitApp.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogExitApp.setContentView(view);
//        dialogExitApp.setTitle(null);
//        dialogExitApp.setCanceledOnTouchOutside(false);
//
//        dialogExitApp.show();

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



        showExitDialog();

    }
}