package com.hampay.mobile.android.activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hampay.mobile.android.R;
import com.hampay.mobile.android.component.FacedTextView;
import com.hampay.mobile.android.component.material.ButtonRectangle;
import com.hampay.mobile.android.component.slider.Animations.DescriptionAnimation;
import com.hampay.mobile.android.component.slider.SliderLayout;
import com.hampay.mobile.android.component.slider.SliderTypes.BaseSliderView;
import com.hampay.mobile.android.component.slider.SliderTypes.TextSliderView;
import com.hampay.mobile.android.component.slider.Tricks.ViewPagerEx;

import java.util.HashMap;

public class IntroSliderActivity extends ActionBarActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener{

    private SliderLayout mDemoSlider;
    private ButtonRectangle register_button;

    private ImageView image_splash;

    LinearLayout animate_logo;
    LinearLayout animate_logo_1;

    ImageView wireframe_1;
    ImageView wireframe_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_slider);
        mDemoSlider = (SliderLayout)findViewById(R.id.slider);

        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_fadein);
        fadeInAnimation.setFillAfter(false);
        fadeInAnimation.setRepeatMode(0);
        fadeInAnimation.setFillAfter(true);

        fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_fadeout);
        fadeOutAnimation.setFillAfter(false);
        fadeOutAnimation.setRepeatMode(0);
        fadeOutAnimation.setFillAfter(true);

        wireframe_1 = (ImageView)findViewById(R.id.wireframe_1);
        wireframe_2 = (ImageView)findViewById(R.id.wireframe_2);

        animate_logo = (LinearLayout)findViewById(R.id.animate_logo);
        animate_logo_1 = (LinearLayout)findViewById(R.id.animate_logo_1);

        Animation a = AnimationUtils.loadAnimation(this, R.anim.splash_logo);
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animate_logo_1.setVisibility(View.GONE);
                animate_logo.setVisibility(View.VISIBLE);
//                intro_text.startAnimation(fadeOutAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animate_logo_1.startAnimation(a);

        image_splash = (ImageView)findViewById(R.id.image_splash);

        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_fadein);
        fadeInAnimation.setFillAfter(false);
        fadeInAnimation.setRepeatMode(0);
        fadeInAnimation.setFillAfter(true);
        image_splash.startAnimation(fadeInAnimation);

        intro_icon = (ImageView)findViewById(R.id.intro_icon);
        intro_text = (FacedTextView)findViewById(R.id.intro_text);

        intro_icon.setImageResource(m‌Icons[0]);
        intro_text.setText(m‌Intor[0]);


        register_button = (ButtonRectangle)findViewById(R.id.register_button);
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(IntroSliderActivity.this, StartActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

//        HashMap<String,String> url_maps = new HashMap<String, String>();
//        url_maps.put("Hannibal", "http://static2.hypable.com/wp-content/uploads/2013/12/hannibal-season-2-release-date.jpg");
//        url_maps.put("Big Bang Theory", "http://tvfiles.alphacoders.com/100/hdclearart-10.png");
//        url_maps.put("House of Cards", "http://cdn3.nflximg.net/images/3093/2043093.jpg");
//        url_maps.put("Game of Thrones", "http://images.boomsbeat.com/data/images/full/19640/game-of-thrones-season-4-jpg.jpg");

        HashMap<String,Integer> file_maps = new HashMap<String, Integer>();
//        file_maps.put("wireframe_1", R.drawable.wireframe_1);
//        file_maps.put("wireframe_2", R.drawable.wireframe_2);
//        file_maps.put("wireframe_3", R.drawable.wireframe_3);
//        file_maps.put("wireframe_4", R.drawable.wireframe_4);
//        file_maps.put("wireframe_5", R.drawable.wireframe_5);

        for(String name : file_maps.keySet()){
            TextSliderView textSliderView = new TextSliderView(this);
            // initialize a SliderLayout
            textSliderView
                    .description(/*name*/"")
                    .image(file_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            //add your extra information
//            textSliderView.bundle(new Bundle());
//            textSliderView.getBundle()
//                    .putString("extra", name);

            mDemoSlider.addSlider(textSliderView);
        }
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Default);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
//        mDemoSlider.setDuration(100);
        mDemoSlider.addOnPageChangeListener(this);

    }

    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        mDemoSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        //Toast.makeText(this,slider.getBundle().get("extra") + "",Toast.LENGTH_SHORT).show();
    }


    ImageView intro_icon;
    FacedTextView intro_text;

    int[] m‌Icons = {
            R.drawable.logo_hampay,
            R.drawable.pay_icon,
            R.drawable.bussines_icon,
            R.drawable.account_icon,
            R.drawable.transactions_icon
    };

    int[] m‌Intor = {
            R.string.intro_1,
            R.string.intro_2,
            R.string.intro_3,
            R.string.intro_4,
            R.string.intro_5
    };

    Animation fadeInAnimation;
    Animation fadeOutAnimation;

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}


    int selected = 0;

    @Override
    public void onPageSelected(int position) {
        Log.d("Slider Demo", "Page Changed: " + position);

        selected = position;

//        switch (position){
//            case 0:
//                wireframe_1.setImageResource(R.drawable.wireframe_1);
//                wireframe_2.setImageResource(R.drawable.wireframe_2);
//                wireframe_1.startAnimation(fadeInAnimation);
//                wireframe_2.startAnimation(fadeOutAnimation);
//                break;
//            case 1:
//                wireframe_1.setImageResource(R.drawable.wireframe_2);
//                wireframe_2.setImageResource(R.drawable.wireframe_3);
//                wireframe_2.startAnimation(fadeOutAnimation);
//                wireframe_1.startAnimation(fadeInAnimation);
//                break;
//            case 2:
//                wireframe_1.setImageResource(R.drawable.wireframe_3);
//                wireframe_2.setImageResource(R.drawable.wireframe_4);
//                wireframe_1.startAnimation(fadeInAnimation);
//                wireframe_2.startAnimation(fadeOutAnimation);
//                break;
//            case 3:
//                wireframe_1.setImageResource(R.drawable.wireframe_4);
//                wireframe_2.setImageResource(R.drawable.wireframe_5);
//                wireframe_2.startAnimation(fadeOutAnimation);
//                wireframe_1.startAnimation(fadeInAnimation);
//                break;
//            case 4:
//                wireframe_1.setImageResource(R.drawable.wireframe_5);
//                wireframe_2.setImageResource(R.drawable.wireframe_1);
//                wireframe_1.startAnimation(fadeInAnimation);
//                wireframe_2.startAnimation(fadeOutAnimation);
//                break;
//        }

        intro_icon = (ImageView)findViewById(R.id.intro_icon);
        intro_text = (FacedTextView)findViewById(R.id.intro_text);

        intro_icon.setImageResource(m‌Icons[position]);
        intro_text.setText(m‌Intor[position]);

    }

    @Override
    public void onPageScrollStateChanged(int state) {}
}