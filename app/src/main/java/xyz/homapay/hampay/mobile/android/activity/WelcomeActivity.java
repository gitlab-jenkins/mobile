package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.adapter.WelcomeAdapter;
import xyz.homapay.hampay.mobile.android.firebase.LogEvent;
import xyz.homapay.hampay.mobile.android.firebase.app.AppEvent;
import xyz.homapay.hampay.mobile.android.model.NotificationMessageType;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.PreferencesManager;
import xyz.homapay.hampay.mobile.android.util.RootUtil;

public class WelcomeActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private WelcomeAdapter welcomeAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private Bundle bundle;
    private Intent intent;
    private Activity activity;
    private int[] layouts = new int[]{
            R.layout.welcome_slider0,
            R.layout.welcome_slider1,
            R.layout.welcome_slider2,
            R.layout.welcome_slider3,
            R.layout.welcome_slider4,
            R.layout.welcome_slider5
    };
    private Button btnSkip, btnNext;
    private PreferencesManager preferencesManager;
    private AppEvent appEvent = AppEvent.LAUNCH;
    private RootUtil rootUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogEvent logEvent = new LogEvent(this);
        logEvent.log(appEvent);

        activity = WelcomeActivity.this;

        rootUtil = new RootUtil(activity);
//        if (rootUtil.checkRootedDevice()){
//            new HamPayDialog(activity).showPreventRootDeviceDialog();
//            return;
//        }

        bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getBoolean(Constants.HAS_NOTIFICATION)) {

                if(HamPayLoginActivity.instance != null) {
                    try {
                        HamPayLoginActivity.instance.finish();
                    } catch (Exception e) {}
                }

                NotificationMessageType notificationMessageType;
                notificationMessageType = NotificationMessageType.valueOf(bundle.getString(Constants.NOTIFICATION_TYPE));

                switch (notificationMessageType){
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
        if (preferencesManager.isRegistered()){
            launchLoginScreen();
        }
        else if (!preferencesManager.isFirstTimeLaunch()) {
            launchRegisterScreen();
            finish();
        }

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_welcome);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnSkip = (Button) findViewById(R.id.btn_skip);
        btnSkip.setVisibility(View.GONE);
        btnNext = (Button) findViewById(R.id.btn_next);

        changeStatusBarColor();

        welcomeAdapter = new WelcomeAdapter(this);
        viewPager.setAdapter(welcomeAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchRegisterScreen();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = getItem(+1);
                if (current < layouts.length) {
                    viewPager.setCurrentItem(current);
                } else {
                    launchRegisterScreen();
                }
            }
        });

    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length - 1];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        if (currentPage > 0) {
            for (int i = 0; i < dots.length; i++) {
                dots[i] = new TextView(this);
                dots[i].setText(Html.fromHtml("&#8226;"));
                dots[i].setTextSize(35);
                dots[i].setTextColor(colorsInactive[0]);
                dotsLayout.addView(dots[i]);
            }
            if (dots.length > 0)
                dots[currentPage - 1].setTextColor(colorsActive[0]);
        }
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchRegisterScreen() {
        preferencesManager.setFirstTimeLaunch(false);
        startActivity(new Intent(WelcomeActivity.this, ProfileEntryActivity.class));
        finish();
    }

    private void launchLoginScreen(){
        startActivity(new Intent(WelcomeActivity.this, HamPayLoginActivity.class));
        finish();
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
            if (position == 0){
                btnSkip.setVisibility(View.GONE);
            }
            // changing the next button text 'NEXT' / 'GOT IT'
            else if (position == layouts.length - 1) {
                // last page. make button text to GOT IT
                btnNext.setText(getString(R.string.register));
                btnSkip.setVisibility(View.GONE);
            } else {
                // still pages are left
                btnNext.setText(getString(R.string.next_slide));
                btnSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }
}
