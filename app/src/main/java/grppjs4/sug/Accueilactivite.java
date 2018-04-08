package grppjs4.sug;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.File;
import java.util.concurrent.ExecutionException;

import grppjs4.sug.Entities.User;


public class Accueilactivite extends AppCompatActivity implements edt.OnFragmentInteractionListener,messagerie.OnFragmentInteractionListener,parametre.OnFragmentInteractionListener,accueil.OnFragmentInteractionListener {

    private TextView mTextMessage;
    private SharedPreferences preflog;
    private SharedPreferences.Editor editor;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef; // mref.child('user').push blabla pr ajouter user;
    public static final String PREFS_NAME = "save";
    private String s="ii.ics";
    String id="";
    private String TAG="tets";

    private AHBottomNavigation.OnTabSelectedListener mOnNavigationItemSelectedListener
            = new  AHBottomNavigation.OnTabSelectedListener() {
        @Override
        public boolean onTabSelected(int position, boolean wasSelected) {
            Fragment fragment = null;
            Class fragmentClass = null;
            switch (position) {
                /*case 0:
                    fragmentClass=accueil.class;
                    //mTextMessage.setText(R.string.title_home);
                    break;*/
                case 0:
                    fragmentClass=edt.class;
                    //mTextMessage.setText(R.string.title_edt);
                    //return true;
                    break;

                case 1:
                    fragmentClass=messagerie.class;
                    //mTextMessage.setText(R.string.title_messagerie);
                    //return true;
                    break;

                case 2:
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
                edtrequete edtrequete =new edtrequete(getApplicationContext (),id);
                edtrequete.execute();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueilactivite);

        //Toolbar toolbaract = (Toolbar) findViewById(R.id.toolbaraction);
        //setSupportActionBar(toolbaract);
        //ActionBar a=getSupportActionBar();
        //a.hide();
        //getSupportActionBar().setDisplayOptions(a.DISPLAY_SHOW_CUSTOM);
        //getSupportActionBar().setCustomView(R.layout.abs_layout);*/

        preflog=getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        editor=preflog.edit();
        mTextMessage = (TextView) findViewById(R.id.message);
        id=preflog.getString("id","");

        AHBottomNavigation bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        AHBottomNavigationAdapter navigationAdapter = new AHBottomNavigationAdapter(this, R.menu.navigation);
        navigationAdapter.setupWithBottomNavigation(bottomNavigation);
        bottomNavigation.setOnTabSelectedListener(mOnNavigationItemSelectedListener);
        bottomNavigation.setOnNavigationPositionListener(mOnNPositionListener);
        bottomNavigation.setAccentColor(Color.parseColor("#447DA2"));
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.SHOW_WHEN_ACTIVE);


        if(savedInstanceState==null){

            File file = new File(getApplicationContext().getFilesDir(),s);
            if(!file.exists()||verifcoint()){
                edtrequete edt =new edtrequete(getApplicationContext (),id);
                try {
                    edt.execute().get();
                    Toast.makeText(this, "Emploi du temps à jour !", Toast.LENGTH_SHORT).show();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }

            Fragment fragment = null;
            Class fragmentClass = null;
            fragmentClass = edt.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();


            mAuth = FirebaseAuth.getInstance();
            mDatabase = FirebaseDatabase.getInstance();
            mRef = mDatabase.getReference();

            if(mAuth.getCurrentUser()!=null && preflog.getString("PSEUDO",null) !=null){
                ///startActivity(new Intent(getApplicationContext(),TchatActivity.class));
                //finish();
            }
            else{
                String use=preflog.getString("PSEUDO",null);
                registerUser(use);
            }
        }





    }

    public void deco(){
        Toast.makeText(this,"A bientôt",Toast.LENGTH_LONG).show();
        editor.remove("id");
        editor.putBoolean("islogin",false);
        editor.apply();
        editor.commit();
        File file = new File(getApplicationContext().getFilesDir(),s);
        if(file.exists()){
            file.delete();
        }
        this.finish();
        Intent i=new Intent(this,LoginActivity.class);
        startActivity(i);
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    private void checkUsername(final String username, final CheckUsernameCallback callback){


        mRef.child(Constants.USERNAME_DB).child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){ //user deja pris
                    callback.isTaken();
                }else{
                    callback.isValid(username);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


                Log.w(TAG, "onCancelled: "+databaseError.getMessage() );


                //loader.setVisibility(View.INVISIBLE);

            }
        });

    }


    private void registerUser(final String username){
        mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Connexion impossible, Veuillez Réessayer", Toast.LENGTH_SHORT).show();

                }
               // loader.setVisibility(View.INVISIBLE);
                final String userId=task.getResult().getUser().getUid();
                checkUsername(username, new CheckUsernameCallback() {
                    @Override
                    public void isValid(final String username) {

                        User newUser = new User(username,userId);
                        mRef.child(Constants.USER_DB).child(userId).setValue(newUser).addOnCompleteListener(Accueilactivite.this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    mRef.child(Constants.USERNAME_DB).child(username).setValue(userId);
                                    preflog.edit().putString("PSEUDO",username).apply();
                                    //startActivity(new Intent(getApplicationContext(),TchatActivity.class));
                                    //finish();
                                }
                            }
                        });

                    }

                    @Override
                    public void isTaken() {
                        Toast.makeText(getApplicationContext(),"Veuillez choisir un autre pseudo celui-ci est déja pris",Toast.LENGTH_SHORT).show();
                       // loader.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });
    }


    interface CheckUsernameCallback{

        void isValid(String username);
        void isTaken();
    }

    public boolean verifcoint(){
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else {
            connected = false;
        }
        return connected;
    }


}
