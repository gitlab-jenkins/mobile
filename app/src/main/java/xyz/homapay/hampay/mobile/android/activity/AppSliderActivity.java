package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.IllegalAppListRequest;
import xyz.homapay.hampay.common.core.model.response.IllegalAppListResponse;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.adapter.AppSliderAdapter;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestIllegalAppList;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.material.ButtonRectangle;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.model.NotificationMessageType;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.DeviceInfo;


public class AppSliderActivity extends AppCompatActivity {

    private ViewPager sliding_into_app;
    private FacedTextView register_button;
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
    SharedPreferences.Editor editor;

    Tracker hamPayGaTracker;

    private Activity activity;

    private Intent intent;

    HamPayDialog hamPayDialog;

    GoogleCloudMessaging gcm;
    String regid;
    String PROJECT_NUMBER = "936219454834";

    IllegalAppListRequest illegalAppListRequest;
    RequestIllegalAppList requestIllegalAppList;
    long launchAppCount = 0;


    Bundle bundle;

    @Override
    protected void onPause() {
        super.onPause();
//        HamPayApplication.setAppSate(AppState.Paused);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        HamPayApplication.setAppSate(AppState.Stoped);
        if (requestIllegalAppList != null){
            if (!requestIllegalAppList.isCancelled())
                requestIllegalAppList.cancel(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        HamPayApplication.setAppSate(AppState.Resumed);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_slider);

        activity = AppSliderActivity.this;

        bundle = getIntent().getExtras();

//        try {
//            soapTest();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

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



        hamPayDialog = new HamPayDialog(activity);

        hamPayGaTracker = ((HamPayApplication) getApplication())
                .getTracker(HamPayApplication.TrackerName.APP_TRACKER);



//        try {
//            key = SecurityUtils.getInstance(this).generateSHA_256(
//                    deviceInfo.getMacAddress(),
//                    deviceInfo.getIMEI(),
//                    deviceInfo.getAndroidId()
//            );
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }

//        getRegId();

//        if (new RootUtil(this).checkRootedDevice()){
//            new HamPayDialog(this).showPreventRootDeviceDialog();
//            return;
//        }


        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();

//        editor.clear().commit();

//        launchAppCount = prefs.getLong(Constants.LAUNCH_APP_COUNT, 0);
//        editor.putLong(Constants.LAUNCH_APP_COUNT, launchAppCount + 1).commit();
//        launchAppCount = 0;
//        if ((launchAppCount % 10) == 0) {
//            illegalAppListRequest = new IllegalAppListRequest();
//            requestIllegalAppList = new RequestIllegalAppList(activity, new RequestIllegalAppListTaskCompleteListener());
//            requestIllegalAppList.execute(illegalAppListRequest);
//        }


        intent = new Intent();


        register_button = (FacedTextView) findViewById(R.id.register_button);
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

        indicator_1 = (ImageView) findViewById(R.id.indicator_1);
        indicator_2 = (ImageView) findViewById(R.id.indicator_2);
        indicator_3 = (ImageView) findViewById(R.id.indicator_3);
        indicator_4 = (ImageView) findViewById(R.id.indicator_4);
        indicator_5 = (ImageView) findViewById(R.id.indicator_5);

        sliding_into_app = (ViewPager) findViewById(R.id.sliding_into_app);
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

        sliding_into_app.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        indicator_1.setImageResource(R.drawable.slider_indicator_selected);
                        indicator_2.setImageResource(R.drawable.slider_indicator_unselected);
                        indicator_3.setImageResource(R.drawable.slider_indicator_unselected);
                        indicator_4.setImageResource(R.drawable.slider_indicator_unselected);
                        indicator_5.setImageResource(R.drawable.slider_indicator_unselected);
                        break;
                    case 1:
                        indicator_1.setImageResource(R.drawable.slider_indicator_unselected);
                        indicator_2.setImageResource(R.drawable.slider_indicator_selected);
                        indicator_3.setImageResource(R.drawable.slider_indicator_unselected);
                        indicator_4.setImageResource(R.drawable.slider_indicator_unselected);
                        indicator_5.setImageResource(R.drawable.slider_indicator_unselected);
                        break;
                    case 2:
                        indicator_1.setImageResource(R.drawable.slider_indicator_unselected);
                        indicator_2.setImageResource(R.drawable.slider_indicator_unselected);
                        indicator_3.setImageResource(R.drawable.slider_indicator_selected);
                        indicator_4.setImageResource(R.drawable.slider_indicator_unselected);
                        indicator_5.setImageResource(R.drawable.slider_indicator_unselected);
                        break;
                    case 3:
                        indicator_1.setImageResource(R.drawable.slider_indicator_unselected);
                        indicator_2.setImageResource(R.drawable.slider_indicator_unselected);
                        indicator_3.setImageResource(R.drawable.slider_indicator_unselected);
                        indicator_4.setImageResource(R.drawable.slider_indicator_selected);
                        indicator_5.setImageResource(R.drawable.slider_indicator_unselected);
                        break;
                    case 4:
                        indicator_1.setImageResource(R.drawable.slider_indicator_unselected);
                        indicator_2.setImageResource(R.drawable.slider_indicator_unselected);
                        indicator_3.setImageResource(R.drawable.slider_indicator_unselected);
                        indicator_4.setImageResource(R.drawable.slider_indicator_unselected);
                        indicator_5.setImageResource(R.drawable.slider_indicator_selected);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



        if (prefs.getBoolean(Constants.REGISTERED_USER, false)) {

            RelativeLayout hampay_login_splash = (RelativeLayout) findViewById(R.id.hampay_login_splash);
            hampay_login_splash.setVisibility(View.VISIBLE);

            Thread thread = new Thread() {
                public void run() {

                    try {
                        launchAppCount = prefs.getLong(Constants.LAUNCH_APP_COUNT, 0);
                        editor.putLong(Constants.LAUNCH_APP_COUNT, launchAppCount + 1).commit();
                        if ((launchAppCount % 10) == 0 || prefs.getBoolean(Constants.FORCE_FETCH_ILLEGAL_APPS, false)) {
                            illegalAppListRequest = new IllegalAppListRequest();
                            requestIllegalAppList = new RequestIllegalAppList(activity, new RequestIllegalAppListTaskCompleteListener());
                            requestIllegalAppList.execute(illegalAppListRequest);
                        }else {
                            sleep(2 * 1000);
                            intent.setClass(AppSliderActivity.this, HamPayLoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            finish();
                            startActivity(intent);
                        }

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


    public class RequestIllegalAppListTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<IllegalAppListResponse>> {


        @Override
        public void onTaskComplete(ResponseMessage<IllegalAppListResponse> illegalAppListResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            if (illegalAppListResponseMessage != null) {

                if (illegalAppListResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Illegal App List")
                            .setAction("Fetch")
                            .setLabel("Success")
                            .build());

                    String downloadedAppNames = new DeviceInfo(activity).getDownloadedAppNames();
                    for (String illegalAppName : illegalAppListResponseMessage.getService().getIllegalAppList()){
                        if (downloadedAppNames.equalsIgnoreCase(illegalAppName)){
                            requestIllegalAppList = new RequestIllegalAppList(activity, new RequestIllegalAppListTaskCompleteListener());
                            new HamPayDialog(activity).showFailIllegalAppListDialog(requestIllegalAppList, illegalAppListRequest,
                                    illegalAppName,
                                    getString(R.string.msg_found_illegal_app));

                            editor.putBoolean(Constants.FORCE_FETCH_ILLEGAL_APPS, true).commit();

                            return;
                        }
                    }

                    editor.putBoolean(Constants.FORCE_FETCH_ILLEGAL_APPS, false).commit();

                    intent.setClass(AppSliderActivity.this, HamPayLoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(intent);

                } else {
                    illegalAppListResponseMessage.getService().getResultStatus().getDescription();
                    requestIllegalAppList = new RequestIllegalAppList(activity, new RequestIllegalAppListTaskCompleteListener());
                    new HamPayDialog(activity).showFailIllegalAppListDialog(requestIllegalAppList, illegalAppListRequest,
                            illegalAppListResponseMessage.getService().getResultStatus().getCode(),
                            illegalAppListResponseMessage.getService().getResultStatus().getDescription());

                    hamPayGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Illegal App List")
                            .setAction("Fetch")
                            .setLabel("Fail(Server)")
                            .build());
                }
            } else {
                requestIllegalAppList = new RequestIllegalAppList(activity, new RequestIllegalAppListTaskCompleteListener());
                new HamPayDialog(activity).showFailIllegalAppListDialog(requestIllegalAppList, illegalAppListRequest,
                        Constants.LOCAL_ERROR_CODE,
                        getString(R.string.msg_fail_illegal_app_list));

                hamPayGaTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Illegal App List")
                        .setAction("Fetch")
                        .setLabel("Fail(Mobile)")
                        .build());
            }
        }

        @Override
        public void onTaskPreRun() {
        }


    }
}
