package com.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hampay.mobile.android.R;
import com.hampay.mobile.android.adapter.AppSliderAdapter;
import com.hampay.mobile.android.component.material.ButtonRectangle;
import com.hampay.mobile.android.dialog.HamPayDialog;
import com.hampay.mobile.android.fragment.AppSliderFragmentA;
import com.hampay.mobile.android.fragment.AppSliderFragmentB;
import com.hampay.mobile.android.fragment.AppSliderFragmentC;
import com.hampay.mobile.android.fragment.AppSliderFragmentD;
import com.hampay.mobile.android.fragment.AppSliderFragmentE;
import com.hampay.mobile.android.util.Constants;
import com.hampay.mobile.android.util.RootUtil;


public class AppSliderActivity extends ActionBarActivity {

    private ViewPager sliding_into_app;
    private ButtonRectangle register_button;
    private ImageView image_splash;
    private Animation fadeInAnimation;
    private Animation splashLogoAnimation;
    private ImageView intro_icon;
    private ImageView indicator_1;
    private ImageView indicator_2;
    private ImageView indicator_3;
    private ImageView indicator_4;
    private ImageView indicator_5;

    private SharedPreferences prefs;

    private Activity activity;
    private Activity destinationActivity;
    private String registeredActivityData = "";

    private Intent intent;


//    Handler handler;
//    Runnable runnable;
//    int slideIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_slider);

        activity = AppSliderActivity.this;

//        if (new RootUtil().checkRootedDevice()){
//            new HamPayDialog(this).showPreventRootDeviceDialog();
//            return;
//        }


        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);

        intent = new Intent();




        register_button = (ButtonRectangle)findViewById(R.id.register_button);
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(AppSliderActivity.this, StartActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivity(intent);
            }
        });

        indicator_1 = (ImageView)findViewById(R.id.indicator_1);
        indicator_2 = (ImageView)findViewById(R.id.indicator_2);
        indicator_3 = (ImageView)findViewById(R.id.indicator_3);
        indicator_4 = (ImageView)findViewById(R.id.indicator_4);
        indicator_5 = (ImageView)findViewById(R.id.indicator_5);

        sliding_into_app = (ViewPager)findViewById(R.id.sliding_into_app);
        sliding_into_app.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent motionEvent) {
                return true;
            }
        });

        sliding_into_app.setAdapter(new AppSliderAdapter(getSupportFragmentManager()));
        sliding_into_app.setOffscreenPageLimit(5);

//        handler = new Handler(Looper.getMainLooper());
//        runnable = new Runnable() {
//            public void run() {
//                slideIndex = (++slideIndex) % 5;
//                sliding_into_app.setCurrentItem(slideIndex, true);
//                handler.postDelayed(runnable, 5000);
//            }
//        };
//        handler.postDelayed(runnable, 5000);

        final LayerDrawable background = (LayerDrawable) sliding_into_app.getBackground();

        background.getDrawable(0).setAlpha(255);
        background.getDrawable(1).setAlpha(0);
        background.getDrawable(2).setAlpha(0);
        background.getDrawable(3).setAlpha(0);
        background.getDrawable(4).setAlpha(0);

        sliding_into_app.setPageTransformer(true, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View view, float position) {

                int index = (Integer) view.getTag();
                Drawable currentDrawableInLayerDrawable;
                currentDrawableInLayerDrawable = background.getDrawable(index);

                if (position <= -1 || position >= 1) {
                    currentDrawableInLayerDrawable.setAlpha(0);
                } else if (position == 0) {
                    currentDrawableInLayerDrawable.setAlpha(255);
                } else {
                    currentDrawableInLayerDrawable.setAlpha((int) (255 - Math.abs(position * 255)));
                }

            }
        });

        sliding_into_app.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        indicator_1.setImageResource(R.drawable.circle_indicator_transparency);
                        indicator_2.setImageResource(R.drawable.circle_indicator);
                        indicator_3.setImageResource(R.drawable.circle_indicator);
                        indicator_4.setImageResource(R.drawable.circle_indicator);
                        indicator_5.setImageResource(R.drawable.circle_indicator);
                        break;
                    case 1:
                        indicator_1.setImageResource(R.drawable.circle_indicator);
                        indicator_2.setImageResource(R.drawable.circle_indicator_transparency);
                        indicator_3.setImageResource(R.drawable.circle_indicator);
                        indicator_4.setImageResource(R.drawable.circle_indicator);
                        indicator_5.setImageResource(R.drawable.circle_indicator);
                        break;
                    case 2:
                        indicator_1.setImageResource(R.drawable.circle_indicator);
                        indicator_2.setImageResource(R.drawable.circle_indicator);
                        indicator_3.setImageResource(R.drawable.circle_indicator_transparency);
                        indicator_4.setImageResource(R.drawable.circle_indicator);
                        indicator_5.setImageResource(R.drawable.circle_indicator);
                        break;
                    case 3:
                        indicator_1.setImageResource(R.drawable.circle_indicator);
                        indicator_2.setImageResource(R.drawable.circle_indicator);
                        indicator_3.setImageResource(R.drawable.circle_indicator);
                        indicator_4.setImageResource(R.drawable.circle_indicator_transparency);
                        indicator_5.setImageResource(R.drawable.circle_indicator);
                        break;
                    case 4:
                        indicator_1.setImageResource(R.drawable.circle_indicator);
                        indicator_2.setImageResource(R.drawable.circle_indicator);
                        indicator_3.setImageResource(R.drawable.circle_indicator);
                        indicator_4.setImageResource(R.drawable.circle_indicator);
                        indicator_5.setImageResource(R.drawable.circle_indicator_transparency);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        registeredActivityData = prefs.getString(Constants.REGISTERED_ACTIVITY_DATA, "");

        if (registeredActivityData.length() != 0){
//            if (registeredActivityData.equalsIgnoreCase(StartActivity.class.getName())){
//                destinationActivity = new StartActivity();
//            }else if (registeredActivityData.equalsIgnoreCase(PostStartActivity.class.getName())){
//                destinationActivity = new PostStartActivity();
//            }
            if (registeredActivityData.equalsIgnoreCase(ProfileEntryActivity.class.getName())){
                destinationActivity = new ProfileEntryActivity();
            }
            else if (registeredActivityData.equalsIgnoreCase(VerificationActivity.class.getName())){
                destinationActivity = new VerificationActivity();
            }
//            else if (registeredActivityData.equalsIgnoreCase(SMSVerificationActivity.class.getName())){
//                destinationActivity = new SMSVerificationActivity();
//            }
            else if (registeredActivityData.equalsIgnoreCase(ConfirmAccountNoActivity.class.getName())){
                destinationActivity = new ConfirmAccountNoActivity();
            }
            else if (registeredActivityData.equalsIgnoreCase(ConfirmInfoActivity.class.getName())){
                destinationActivity = new ConfirmInfoActivity();
            }
            else if (registeredActivityData.equalsIgnoreCase(RegVerifyAccountNoActivity.class.getName())){
                destinationActivity = new RegVerifyAccountNoActivity();
            }
            else if (registeredActivityData.equalsIgnoreCase(PostRegVerifyAccountNoActivity.class.getName())){
                destinationActivity = new PostRegVerifyAccountNoActivity();
            }
            else if (registeredActivityData.equalsIgnoreCase(PasswordEntryActivity.class.getName())){
                destinationActivity = new PasswordEntryActivity();
            }
            else if (registeredActivityData.equalsIgnoreCase(MemorableWordEntryActivity.class.getName())){
                destinationActivity = new MemorableWordEntryActivity();
            }

            new HamPayDialog(activity).showResumeRegisterationDialog(destinationActivity);


        }

//        if (registeredActivityData.equalsIgnoreCase(ProfileEntryActivity.class.getName())){
//            intent.setClass(AppSliderActivity.this, ProfileEntryActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            finish();
//            startActivity(intent);
//        }

        else {
            if (prefs.getBoolean(Constants.REGISTERED_USER, false)) {

                RelativeLayout hampay_login_splash = (RelativeLayout)findViewById(R.id.hampay_login_splash);
                hampay_login_splash.setVisibility(View.VISIBLE);

                Thread thread = new Thread() {
                    public void run() {

                        try {
                            sleep(3 * 1000);

                            intent.setClass(AppSliderActivity.this, HamPayLoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            finish();
                            startActivity(intent);


                        } catch (Exception e) {

                        }
                    }
                };
                thread.start();

            } else {

                image_splash = (ImageView) findViewById(R.id.image_splash);
                intro_icon = (ImageView) findViewById(R.id.intro_icon);

                fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_fadein);
                splashLogoAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_logo);
                fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        intro_icon.setVisibility(View.GONE);
                        sliding_into_app.setOnTouchListener(null);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                fadeInAnimation.setFillAfter(false);
                fadeInAnimation.setRepeatMode(0);
                fadeInAnimation.setFillAfter(true);
                image_splash.startAnimation(fadeInAnimation);

                splashLogoAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        intro_icon.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                intro_icon.startAnimation(splashLogoAnimation);
            }
        }
    }




//    class MyAdapter extends FragmentStatePagerAdapter
//    {
//
//        public MyAdapter(FragmentManager fm) {
//            super(fm);
//        }
//
//        @Override
//        public Fragment getItem(int i) {
//            Fragment fragment=null;
//            if(i == 0)
//            {
//                fragment=new AppSliderFragmentA();
//            }
//            if(i==1)
//            {
//                fragment=new AppSliderFragmentB();
//            }
//            if(i==2)
//            {
//                fragment=new AppSliderFragmentC();
//            }
//            if(i==3)
//            {
//                fragment=new AppSliderFragmentD();
//            }
//            if(i==4)
//            {
//                fragment=new AppSliderFragmentE();
//            }
//            return fragment;
//        }
//
//        @Override
//        public int getCount() {
//            return 5;
//        }
//
//        @Override
//        public boolean isViewFromObject(View view, Object object) {
//            if(object instanceof AppSliderFragmentE){
//                view.setTag(4);
//            }
//            if(object instanceof AppSliderFragmentD){
//                view.setTag(3);
//            }
//            if(object instanceof AppSliderFragmentC){
//                view.setTag(2);
//            }
//            if(object instanceof AppSliderFragmentB){
//                view.setTag(1);
//            }
//            if(object instanceof AppSliderFragmentA){
//                view.setTag(0);
//            }
//            return super.isViewFromObject(view, object);
//        }
//    }


}
