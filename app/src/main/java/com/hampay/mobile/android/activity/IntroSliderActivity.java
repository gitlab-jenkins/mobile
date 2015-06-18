package com.hampay.mobile.android.activity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hampay.mobile.android.R;
import com.hampay.mobile.android.adapter.TransformerAdapter;
import com.hampay.mobile.android.component.FacedTextView;
import com.hampay.mobile.android.component.slider.Animations.DescriptionAnimation;
import com.hampay.mobile.android.component.slider.SliderLayout;
import com.hampay.mobile.android.component.slider.SliderTypes.BaseSliderView;
import com.hampay.mobile.android.component.slider.SliderTypes.TextSliderView;
import com.hampay.mobile.android.component.slider.Tricks.ViewPagerEx;

import java.util.HashMap;

public class IntroSliderActivity extends ActionBarActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener{

    private SliderLayout mDemoSlider;
    private CardView register_CardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_slider);
        mDemoSlider = (SliderLayout)findViewById(R.id.slider);

        intro_icon = (ImageView)findViewById(R.id.intro_icon);
        intro_text = (FacedTextView)findViewById(R.id.intro_text);

        intro_icon.setImageResource(m‌Icons[0]);
        intro_text.setText(m‌Intor[0]);


        register_CardView = (CardView)findViewById(R.id.register_CardView);
        register_CardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(IntroSliderActivity.this, StartActivity.class);
                startActivity(intent);
            }
        });

//        HashMap<String,String> url_maps = new HashMap<String, String>();
//        url_maps.put("Hannibal", "http://static2.hypable.com/wp-content/uploads/2013/12/hannibal-season-2-release-date.jpg");
//        url_maps.put("Big Bang Theory", "http://tvfiles.alphacoders.com/100/hdclearart-10.png");
//        url_maps.put("House of Cards", "http://cdn3.nflximg.net/images/3093/2043093.jpg");
//        url_maps.put("Game of Thrones", "http://images.boomsbeat.com/data/images/full/19640/game-of-thrones-season-4-jpg.jpg");

        HashMap<String,Integer> file_maps = new HashMap<String, Integer>();
        file_maps.put("wireframe_1", R.drawable.wireframe_1);
        file_maps.put("wireframe_2", R.drawable.wireframe_2);
        file_maps.put("wireframe_3", R.drawable.wireframe_3);
        file_maps.put("wireframe_4", R.drawable.wireframe_4);
        file_maps.put("wireframe_5", R.drawable.wireframe_5);

        for(String name : file_maps.keySet()){
            TextSliderView textSliderView = new TextSliderView(this);
            // initialize a SliderLayout
            textSliderView
                    .description(name)
                    .image(file_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", name);

            mDemoSlider.addSlider(textSliderView);
        }
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Default);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater menuInflater = getMenuInflater();
//        menuInflater.inflate(R.menu.main,menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.action_custom_indicator:
//                mDemoSlider.setCustomIndicator((PagerIndicator) findViewById(R.id.custom_indicator));
//                break;
//            case R.id.action_custom_child_animation:
//                mDemoSlider.setCustomAnimation(new ChildAnimationExample());
//                break;
//            case R.id.action_restore_default:
//                mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
//                mDemoSlider.setCustomAnimation(new DescriptionAnimation());
//                break;
//            case R.id.action_github:
//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/daimajia/AndroidImageSlider"));
//                startActivity(browserIntent);
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }


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

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        Log.d("Slider Demo", "Page Changed: " + position);

        intro_icon = (ImageView)findViewById(R.id.intro_icon);
        intro_text = (FacedTextView)findViewById(R.id.intro_text);

        intro_icon.setImageResource(m‌Icons[position]);
        intro_text.setText(m‌Intor[position]);

    }

    @Override
    public void onPageScrollStateChanged(int state) {}
}