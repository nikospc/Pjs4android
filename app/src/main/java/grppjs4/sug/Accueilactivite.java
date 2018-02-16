package grppjs4.sug;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.alamkanak.weekview.WeekViewEvent;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class Accueilactivite extends AppCompatActivity implements edt.OnFragmentInteractionListener,messagerie.OnFragmentInteractionListener,parametre.OnFragmentInteractionListener,accueil.OnFragmentInteractionListener {

    private TextView mTextMessage;
    private SharedPreferences preflog;
    private SharedPreferences.Editor editor;
    public static final String PREFS_NAME = "save";

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
                EDT edt=new EDT(getApplicationContext ());
                edt.execute();
                ParcoursICS();
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

        }


        /*CalendarBuilder builder = new CalendarBuilder();
       FileInputStream fin = null;
        try {
            File file=new File(getApplicationContext().getFilesDir(),"ii.ics");
            fin = new FileInputStream(file);



            try {
                Calendar calendar = builder.build(fin);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParserException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/


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

    public void ParcoursICS() {
// Parcours le fichier ics et récupère les rendez-vous
        String ligne;
        String s="ii.ics";
        List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
        Calendar cal = Calendar.getInstance();
        WeekViewEvent w = new WeekViewEvent();
        try {
            // ouverture du fichier texte (.ics)
            File file = new File(getApplicationContext().getFilesDir(),s);
            BufferedReader planning = new BufferedReader(new FileReader(file));
            ligne = planning.readLine();
            while(!ligne.equalsIgnoreCase("BEGIN:VEVENT")){
                ligne=planning.readLine();
                Log.i("ics", ligne);
            }

            while (!ligne.equalsIgnoreCase("END:VCALENDAR") && ligne != null) {

                if (ligne.equalsIgnoreCase("BEGIN:VEVENT")) {
                    // Rendez-vous à prendre en compte
                    ligne = planning.readLine();
                    Log.d("parc", ligne);
                    ligne = planning.readLine();
                    Log.i("b1", ligne);
                    while (!ligne.equalsIgnoreCase("END:VEVENT")) {

                        // récupération des informations utiles
                        if (ligne.contains("DTSTART:")) {
                            // date et heure de début du rendez-vous
                            cal.set(Calendar.HOUR, Integer.parseInt(ligne.substring(18,19)));
                            cal.set(Calendar.MINUTE, Integer.parseInt(ligne.substring(20,21)));
                            cal.set(Calendar.SECOND, Integer.parseInt(ligne.substring(22,23)));
                            w.setStartTime(cal);
                            Log.i("date debut", w.getStartTime().toString());

                        }
                        else if (ligne.contains("DTEND:")) {
                            cal.set(Calendar.HOUR, Integer.parseInt(ligne.substring(16,17)));
                            cal.set(Calendar.MINUTE, Integer.parseInt(ligne.substring(18,19)));
                            cal.set(Calendar.SECOND, Integer.parseInt(ligne.substring(20,21)));
                            w.setEndTime(cal);
                            Log.i("date debut", w.getEndTime().toString());
                            // date et heure de fin du rendez-vous

                        }
                        else if (ligne.contains("LOCATION:")) {
                            w.setLocation(ligne.substring(9));
                            Log.i("lieu", w.getLocation());
                            // lieu du début du rendez-vous

                        }
                        else if (ligne.equalsIgnoreCase("DTSTAMP")) {
                            // date et heure de création du planning (utile pour savoir s'il a été modifié)

                        }
                        else if (ligne.equalsIgnoreCase("DESCRIPTION")) {
                            // description du rendez-vous (avec coordonnées PEQ)

                        }
                        else if (ligne.contains("SUMMARY:")) {
                            w.setName(ligne.substring(8));
                            Log.i("resume", w.getName());
                            // numéro de vol ou type de rendez-vous (hotel par exemple)

                        }
                        ligne = planning.readLine();

                    }
                    // traitement du rendez-vous : 1. mise en forme en fct des options, 2. création du rendez-vous dans l'agenda

                }
                ligne = planning.readLine();
            }

        }
        catch (FileNotFoundException e1) {
            // Erreur : le fichier n'existe pas
            Toast.makeText(getApplicationContext(), "Erreur : fichier ics inexistant", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            // Erreur : problème de lecture fichier
            Toast.makeText(getApplicationContext(), "Erreur : problème de lecture du fichier ics", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
