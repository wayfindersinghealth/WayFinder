package sg.com.singhealth.wayfinder;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * File Name: IntroductionTutorial.java
 * Created By: AY17 P3 FYPJ NYP SIT
 * Description: -
 */

public class Help extends AppIntro2 {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //-- Add Slide --
        addSlide(AppIntroFragment.newInstance("HELP", "Journey Simplified.", R.drawable.icon_help, Color.parseColor("#ed9107")));
        addSlide(AppIntroFragment.newInstance("Learn Location", "Find where you are with a touch", R.drawable.icon_location, Color.parseColor("#ed9107")));
        addSlide(AppIntroFragment.newInstance("Find Your Way", "Make your journey simple with pinpoint accuracy direction to your destination", R.drawable.icon_find, Color.parseColor("#ed9107")));
        addSlide(AppIntroFragment.newInstance("You are all set. Enjoy WayFinder", "GET STARTED", R.drawable.icon_ok, Color.parseColor("#ed9107")));

        //-- Override bar/separator color --
        setBarColor(Color.parseColor("#ed9107"));

        //-- StatusBar --
        showStatusBar(true);

        //-- Skip/Done Button --
        showSkipButton(false);
        showDoneButton(true);

        //-- Animations --
        setSlideOverAnimation();
    }

    @Override
    public void onDonePressed() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

}