package xyz.homapay.hampay.mobile.android.activity;

import com.stephentuso.welcome.ParallaxPage;
import com.stephentuso.welcome.WelcomeActivity;
import com.stephentuso.welcome.WelcomeConfiguration;

import xyz.homapay.hampay.mobile.android.R;

/**
 * Created by Amir on 10/10/16.
 */

public class IncludedPagesWelcomeActivity extends WelcomeActivity {

    @Override
    protected WelcomeConfiguration configuration() {
        return new WelcomeConfiguration.Builder(this)
                .defaultBackgroundColor(R.color.colorPrimary)
                .swipeToDismiss(true)
                .canSkip(false)
                .defaultTitleTypefacePath("fonts/vazir_regular_bold.ttf")
                .defaultHeaderTypefacePath("fonts/vazir_regular_bold.ttf")
                .defaultDescriptionTypefacePath("fonts/vazir_regular.ttf")
                .bottomLayout(WelcomeConfiguration.BottomLayout.STANDARD_DONE_IMAGE)
                .page(new ParallaxPage(R.layout.parallax_intro_page_1, getString(R.string.app_name_slider), getString(R.string.intro_0_text)).background(R.color.app_intro_bg))
                .page(new ParallaxPage(R.layout.parallax_intro_page_2, getString(R.string.intro_1_title), getString(R.string.intro_1_text)).background(R.color.app_intro_bg))
                .page(new ParallaxPage(R.layout.parallax_intro_page_3, getString(R.string.intro_2_title), getString(R.string.intro_2_text)).background(R.color.app_intro_bg))
                .page(new ParallaxPage(R.layout.parallax_intro_page_4, getString(R.string.intro_3_title), getString(R.string.intro_3_text)).background(R.color.app_intro_bg))
                .page(new ParallaxPage(R.layout.parallax_intro_page_5, getString(R.string.intro_4_title), getString(R.string.intro_4_text)).background(R.color.app_intro_bg))
                .animateButtons(true)
                .build();
    }

}
