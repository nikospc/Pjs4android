package grppjs4.sug;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;


import java.io.File;


public class Accueilactivite extends AppCompatActivity implements edt.OnFragmentInteractionListener,messagerie.OnFragmentInteractionListener,parametre.OnFragmentInteractionListener,accueil.OnFragmentInteractionListener {

    private TextView mTextMessage;
    private SharedPreferences preflog;
    private SharedPreferences.Editor editor;
    public static final String PREFS_NAME = "save";
    private String s="ii.ics";
    private AHBottomNavigation.OnTabSelectedListener mOnNavigationItemSelectedListener
            = new  AHBottomNavigation.OnTabSelectedListener() {
        @Override
        public boolean onTabSelected(int position, boolean wasSelected) {
            Fragment fragment = null;
            Class fragmentClass = null;
            switch (position) {
                case 0:
                    fragmentClass=accueil.class;
                    //mTextMessage.setText(R.string.title_home);
                    break;
                case 1:
                    fragmentClass=edt.class;
                    //mTextMessage.setText(R.string.title_edt);
                    //return true;
                    break;

                case 2:
                    fragmentClass=messagerie.class;
                    //mTextMessage.setText(R.string.title_messagerie);
                    //return true;
                    break;

                case 3:
                    fragmentClass=parametre.class;
                    //mTextMessage.setText(R.string.title_home);
                    //return true;
                    break;

            }
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
            Log.d("dddd", "onTabSelected: ddd");
            return true;
        }

    };



    private  AHBottomNavigation.OnNavigationPositionListener mOnNPositionListener=new AHBottomNavigation.OnNavigationPositionListener(){
            @Override public void onPositionChange(int y) {
            // Manage the new y position
        }
    };


        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //ajoute les entrées de menu_test à l'ActionBar
        getMenuInflater().inflate(R.menu.actionbarmenu, menu);
        return true;
    }

    //gère le click sur une action de l'ActionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.deco:
               deco();
                return true;
            case R.id.refresh:
                edtrequete edtrequete =new edtrequete(getApplicationContext ());
                edtrequete.execute();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueilactivite);


        Toolbar toolbaract = (Toolbar) findViewById(R.id.toolbaraction);
        setSupportActionBar(toolbaract);
        ActionBar a=getSupportActionBar();
        getSupportActionBar().setDisplayOptions(a.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);

        preflog=getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        editor=preflog.edit();

        mTextMessage = (TextView) findViewById(R.id.message);

        AHBottomNavigation bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        AHBottomNavigationAdapter navigationAdapter = new AHBottomNavigationAdapter(this, R.menu.navigation);
        navigationAdapter.setupWithBottomNavigation(bottomNavigation);
        bottomNavigation.setOnTabSelectedListener(mOnNavigationItemSelectedListener);
        bottomNavigation.setOnNavigationPositionListener(mOnNPositionListener);

        if(savedInstanceState==null){
            Fragment fragment = null;
            Class fragmentClass = null;
            fragmentClass = accueil.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
            File file = new File(getApplicationContext().getFilesDir(),s);
            if(!file.exists()){
                edtrequete edt =new edtrequete(getApplicationContext ());
                edt.execute();
            }
        }

    }

    public void deco(){
        Toast.makeText(this,"A bientôt",Toast.LENGTH_LONG).show();
        editor.remove("id");
        editor.putBoolean("islogin",false);
        editor.apply();
        editor.commit();
        this.finish();
        Intent i=new Intent(this,LoginActivity.class);
        startActivity(i);
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
