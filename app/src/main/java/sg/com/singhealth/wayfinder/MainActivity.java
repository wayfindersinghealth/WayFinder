package sg.com.singhealth.wayfinder;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

import layout.AboutFragment;
import layout.BlankFragment;
import layout.FindYourWayFragment;
import layout.LearnFragment;
import layout.LoginFragment;
import layout.MainFragment;
import layout.RegisterFragment;
import layout.StartFragment;

/**
 * File Name: MainActivity.java
 * Created By: AY17 P3 FYPJ NYP SIT
 * Description: -
 */

//Add [_Fragment.OnFragmentInteractionListener] if applicable
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        MainFragment.OnFragmentInteractionListener,
        LearnFragment.OnFragmentInteractionListener,
        FindYourWayFragment.OnFragmentInteractionListener,
        AboutFragment.OnFragmentInteractionListener,
        LoginFragment.OnFragmentInteractionListener,
        StartFragment.OnFragmentInteractionListener,
        RegisterFragment.OnFragmentInteractionListener,
        BlankFragment.OnFragmentInteractionListener{
    SharedPreferences spf;
    SharedPreferences.Editor editor;
    NavigationView navigationView;
    Menu nav_Menu;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spf = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        editor = spf.edit();
        String getlanguagesetting = "";

        getlanguagesetting = spf.getString("languagesetting", null);
        if (getlanguagesetting != null && !getlanguagesetting.equals("")) {
            if (getlanguagesetting.equals("English")) {
                Resources res = getResources();
                Configuration config = res.getConfiguration();
                config.locale = Locale.ENGLISH;
                DisplayMetrics dm = res.getDisplayMetrics();
                res.updateConfiguration(config, dm);
            } else {
                Resources res = getResources();
                Configuration config = res.getConfiguration();
                config.locale = Locale.CHINA;
                DisplayMetrics dm = res.getDisplayMetrics();
                res.updateConfiguration(config, dm);
            }

        } else {
            Resources res = getResources();
            Configuration config = res.getConfiguration();
            config.locale = Locale.ENGLISH;
            DisplayMetrics dm = res.getDisplayMetrics();
            res.updateConfiguration(config, dm);
        }
        setContentView(R.layout.activity_main);

        //-------- DO NOT TOUCH START --------
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);

        //-- Hide Keyboard in DrawerLayout --
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.app_name, R.string.app_name) {


            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                hideKeyboard(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                hideKeyboard(drawerView);
            }
        };



        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //Calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();

        //-- End Keyboard in DrawerLayout --

        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Set MainFragment as Default
        Fragment fragment = new MainFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();

        //-------- DO NOT TOUCH END --------



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (id == R.id.nav_learn) {
            navigationLearn();
        } else if (id == R.id.nav_preferences) {
            navigationLanguagePreference();
        } else if (id == R.id.nav_findYourWay) {
            navigationFindYourWay();

        } /*else if (id == R.id.nav_help) {

        } */ else if (id == R.id.nav_about) {
            navigationAbout();
        } else if (id == R.id.nav_Appt) {
            navigationBlack();
        } else if (id == R.id.nav_logout) {
            navigationLogout();
        }
        else {
            //Else Home Fragment
            navigationHome();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //-------- START OF METHODS --------

    //---- Set Action Bar Title ----
    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    //---- Hide Keyboard Method used in app_bar_main----
    public void hideKeyboard(View drawerView) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(drawerView.getWindowToken(), 0);
    }

    //---- Home Button ----
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            onPause();
        }
        return true;
    }

    //---- Access Home Page ----
    public void navigationHome() {
        Fragment fragment;
        fragment = new MainFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
    }




    //---- Access Learn Page ----
    public void navigationLearn() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            Fragment fragment;
            fragment = new LearnFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
        }
        else {
            Fragment fragment;
            fragment = new StartFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
        }
    }

    //---- Access Find Your Way Page ----
    public void navigationFindYourWay() {
        Fragment fragment;
        fragment = new FindYourWayFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
    }

    //---- Access Login Page ----
    public void navigationLogin() {
        Fragment fragment;
        fragment = new LoginFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
    }


    //---- Access Help Page ----

    //---- Access Language Preference Page ----
    public void navigationLanguagePreference() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.language_spinner, null);
        mBuilder.setTitle("Select Your Preferred Language");

        //-- Start of Language Spinner --
        final Spinner mSpinner = (Spinner) mView.findViewById(R.id.spinnerLanguage);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.languageList));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        //-- End of Language Spinner --

        mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!mSpinner.getSelectedItem().toString().equalsIgnoreCase("Select Your Preferred Language")) {
                    if (mSpinner.getSelectedItem().toString().equalsIgnoreCase("English") || mSpinner.getSelectedItem().toString().equals("英文")) {

                        editor.putString("languagesetting", "English");
                        editor.commit();
                        Toast.makeText(MainActivity.this, "Restarting the WayFinder!", Toast.LENGTH_SHORT).show();
                        Intent intent = getBaseContext().getPackageManager()
                                .getLaunchIntentForPackage(getBaseContext().getPackageName());
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } else if (mSpinner.getSelectedItem().toString().equalsIgnoreCase("Chinese") || mSpinner.getSelectedItem().toString().equals("中文")) {
                        editor.putString("languagesetting", "Chinese");
                        editor.commit();
                        Toast.makeText(MainActivity.this, "正在重新启动您的应用程序！", Toast.LENGTH_SHORT).show();
                        Intent intent = getBaseContext().getPackageManager()
                                .getLaunchIntentForPackage(getBaseContext().getPackageName());
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }

                    Toast.makeText(MainActivity.this, "Changing Language....", Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }
            }
        });

        mBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        mBuilder.setView(mView);
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

    //---- Access About Page ----
    public void navigationAbout() {
        Fragment fragment;
        fragment = new AboutFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
    }


    //---- Access Logout Function ----
    public void navigationLogout() {
        if(user!= null){
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(MainActivity.this, "Logout...Success", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(MainActivity.this, "The user is not logged in", Toast.LENGTH_SHORT).show();
        }
        Fragment fragment;
        fragment = new MainFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
    }

    //---- Access Black Page ----
    public void navigationBlack(){
        Fragment fragment;
        fragment = new BlankFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
    }

    //-------- END OF METHODS --------
}

