package com.hampay.mobile.android.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hampay.mobile.android.R;
import com.hampay.mobile.android.adapter.IntroAdapter;
import com.hampay.mobile.android.component.FacedTextView;

public class IntroActivity extends ActionBarActivity {

    ViewPager introViewPager;
    IntroAdapter introAdapter;
    ImageView circle_1;
    ImageView circle_2;
    ImageView circle_3;
    ImageView circle_4;
    ImageView circle_5;
    CardView register_CardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        introViewPager = (ViewPager)findViewById(R.id.introViewPager);

        WallPaperAdapter wallPaperAdapter = new WallPaperAdapter();

        introViewPager.setAdapter(wallPaperAdapter);

        introViewPager.setOffscreenPageLimit(5);


        circle_1 = (ImageView)findViewById(R.id.circle_1);
        circle_2 = (ImageView)findViewById(R.id.circle_2);
        circle_3 = (ImageView)findViewById(R.id.circle_3);
        circle_4 = (ImageView)findViewById(R.id.circle_4);
        circle_5 = (ImageView)findViewById(R.id.circle_5);

        register_CardView = (CardView)findViewById(R.id.register_CardView);
        register_CardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(IntroActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        introViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                switch (position){
                    case 0:
                        circle_1.setImageResource(R.drawable.circle);
                        circle_2.setImageResource(R.drawable.circle_transparency);
                        circle_3.setImageResource(R.drawable.circle_transparency);
                        circle_4.setImageResource(R.drawable.circle_transparency);
                        circle_5.setImageResource(R.drawable.circle_transparency);
                        break;

                    case 1:
                        circle_1.setImageResource(R.drawable.circle_transparency);
                        circle_2.setImageResource(R.drawable.circle);
                        circle_3.setImageResource(R.drawable.circle_transparency);
                        circle_4.setImageResource(R.drawable.circle_transparency);
                        circle_5.setImageResource(R.drawable.circle_transparency);
                        break;

                    case 2:
                        circle_1.setImageResource(R.drawable.circle_transparency);
                        circle_2.setImageResource(R.drawable.circle_transparency);
                        circle_3.setImageResource(R.drawable.circle);
                        circle_4.setImageResource(R.drawable.circle_transparency);
                        circle_5.setImageResource(R.drawable.circle_transparency);
                        break;

                    case 3:
                        circle_1.setImageResource(R.drawable.circle_transparency);
                        circle_2.setImageResource(R.drawable.circle_transparency);
                        circle_3.setImageResource(R.drawable.circle_transparency);
                        circle_4.setImageResource(R.drawable.circle);
                        circle_5.setImageResource(R.drawable.circle_transparency);
                        break;

                    case 4:
                        circle_1.setImageResource(R.drawable.circle_transparency);
                        circle_2.setImageResource(R.drawable.circle_transparency);
                        circle_3.setImageResource(R.drawable.circle_transparency);
                        circle_4.setImageResource(R.drawable.circle_transparency);
                        circle_5.setImageResource(R.drawable.circle);
                        break;
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_intro, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    int[] m‌Background = {
            R.drawable.wireframe_1,
            R.drawable.wireframe_2,
            R.drawable.wireframe_3,
            R.drawable.wireframe_4,
            R.drawable.wireframe_5,
    };

    int[] m‌Icons = {
            R.drawable.logo_hampay,
            R.drawable.logo_hampay,
            R.drawable.pay_icon,
            R.drawable.bussines_icon,
            R.drawable.account_icon
    };

    int[] m‌Intor = {
            R.string.intro_1,
            R.string.intro_2,
            R.string.intro_3,
            R.string.intro_4,
            R.string.intro_5
    };


    private class WallPaperAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return m‌Background.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            LayoutInflater inflater = (LayoutInflater) container.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = inflater.inflate(R.layout.fragment_intro_item, null);

            ImageView intro_background = (ImageView) view.findViewById(R.id.intro_background);
            ImageView intro_icon = (ImageView) view.findViewById(R.id.intro_icon);
            FacedTextView intro_text = (FacedTextView)view.findViewById(R.id.intro_text);

            intro_background.setImageResource(m‌Background[position]);
            intro_icon.setImageResource(m‌Icons[position]);
            intro_text.setText(m‌Intor[position]);


            RelativeLayout layout = new RelativeLayout(IntroActivity.this);

            layout.addView(view);


            container.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((RelativeLayout)object);
        }

    }



}
