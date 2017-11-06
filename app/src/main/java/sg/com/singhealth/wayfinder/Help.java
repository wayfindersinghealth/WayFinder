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

        String help = getString(R.string.help);
        String Simplified = getString(R.string.Simplified);
        String Learn = getString(R.string.help_learn);
        String learn_help = getString(R.string.learn_help);
        String Find = getString(R.string.help_find);
        String find_help = getString(R.string.find_help);
        String OK = getString(R.string.help_ok);
        String begin = getString(R.string.begin);


        //-- Add Slide --
        addSlide(AppIntroFragment.newInstance(help, Simplified, R.drawable.icon_help, Color.parseColor("#ed9107")));
        addSlide(AppIntroFragment.newInstance(Learn, learn_help, R.drawable.icon_location, Color.parseColor("#ed9107")));
        addSlide(AppIntroFragment.newInstance(Find, find_help, R.drawable.icon_find, Color.parseColor("#ed9107")));
        addSlide(AppIntroFragment.newInstance(OK, begin, R.drawable.icon_ok, Color.parseColor("#ed9107")));

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