package sg.com.singhealth.wayfinder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * File Name: SplashScreen.java
 * Created By: AY17 P3 FYPJ NYP SIT
 * Description: -
 */

public class SplashScreen extends Activity {

    private static final int SPLASH_SHOW_TIME = 2500;
    Animation myAnimation;
    ImageView iv;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        setContentView(R.layout.splash_screen);

        iv = (ImageView) findViewById(R.id.imageViewSingHealth);
        tv = (TextView) findViewById(R.id.textViewLoading);
        myAnimation = AnimationUtils.loadAnimation(this, R.anim.animation);
        iv.startAnimation(myAnimation);
        iv.startAnimation(myAnimation);

        new BackgroundSplashTask().execute();
    }

    private class BackgroundSplashTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                Thread.sleep(SPLASH_SHOW_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            //-- Shared Preferences --
            SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

            Intent i;
            if (isFirstStart) {
                //-- Introduction Tutorial Slide --
                i = new Intent(SplashScreen.this, IntroductionTutorial.class);
                //-- Preferences Editor --
                SharedPreferences.Editor e = getPrefs.edit();
                e.putBoolean("firstStart", false);
                e.apply();
            } else {
                //-- Application Layout --
                i = new Intent(SplashScreen.this, MainActivity.class);
            }

            startActivity(i);
            finish();
        }
    }
}
