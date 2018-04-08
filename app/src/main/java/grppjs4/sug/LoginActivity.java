package grppjs4.sug;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity  {


    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "ii03208:testt", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Button btnsignup;
    private SharedPreferences preflog;
    private SharedPreferences.Editor editor;
    private Response response;
    OkHttpClient client = new OkHttpClient();//creation d'une instance okhttp
    public String url= "http://sug.nikolaspaci.fr/index.php?controle=coSUG&action=identmob";//url où l'on va acceder
    //http://ent-ng.parisdescartes.fr/export/ics.php




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        preflog = this.getSharedPreferences(
                "save", this.MODE_PRIVATE);

        editor = preflog.edit();

        //verifie si dans preference.xml on des id de connexion
        if(preflog.getBoolean("islogin",false)) {
            Log.i("pas de id","aucun id");
            Intent i=new Intent(LoginActivity.this,Accueilactivite.class);
            startActivity(i);
            finish();
        }
        else {

            // Set up the login form.
            mEmailView = (AutoCompleteTextView) findViewById(R.id.identifiantcompte);
            //populateAutoComplete();

            mPasswordView = (EditText) findViewById(R.id.password);
            mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                        attemptLogin();
                        return true;
                    }
                    return false;
                }
            });

            Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
            mEmailSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin();
                }
            });

            Button btnsignup=(Button)findViewById(R.id.btn_signup);
            btnsignup.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(LoginActivity.this, "Nos équipes travaillent dessus, créez un compte sur le site !", Toast.LENGTH_SHORT).show();
                    //Intent i=new Intent(LoginActivity.this,Signup.class);
                    //startActivity(i);

                }
            });
            mLoginFormView = findViewById(R.id.login_form);
            mProgressView = findViewById(R.id.login_progress);
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String identifiant = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid identifiant address.
        if (TextUtils.isEmpty(identifiant)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }
        else if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        else if (!isEmailValid(identifiant)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);//
            mAuthTask = new UserLoginTask(identifiant, password);
            mAuthTask.execute();
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return (email.contains("@")||(email.contains("ii")));
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_longAnimTime);


            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }






    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private  String mdpcrypt;
        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            OkHttpClient client = new OkHttpClient();
            String verifco = "false";
            //SI ON VEUT FAIRE DU GET
            /*HttpUrl.Builder httpBuider = HttpUrl.parse(url).newBuilder();
            httpBuider.addQueryParameter("id", mEmail);
            Request request = new Request.Builder().url(httpBuider.build()).build();*/
            mdpcrypt= new String(Hex.encodeHex(DigestUtils.sha1(mPassword)));
            // ICI ON FAIT DU POST
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("mailconnect", mEmail)
                    .addFormDataPart("mdpconnect",mPassword)
                    .build();

            Request.Builder builder = new Request.Builder();
            builder.url(url);
            builder.post(requestBody);
            Request request = builder.build();

            try { //REPONSE DU SERVEUR

                 response = client.newCall(request).execute();


                JSONObject myJson = null;
                try {
                    myJson = new JSONObject(response.body().string());
                    verifco = myJson.optString("verifco");


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // use myJson as needed, for example
                Log.i("hu", verifco);

                if(verifco.equals("true")){ //on regarde si il retourne true
                    String idetu = myJson.optString("name");
                    String pseudo = myJson.optString("pseudo");
                    editor.putBoolean("islogin",true); //alors on sauvegarde l'id dans un fichier de sauvegarde
                    editor.putString("id",idetu);
                    editor.putString("PSEUDO",pseudo);
                    editor.commit();
                    return true;
                }
                else if(verifco.equals("false")){
                    return false; //sinon retour et indique mdp faux
                }
            } catch (IOException e) {
                return false;
            }

            /*for (String credential : DUMMY_CREDENTIALS) { ///verification factice pour le moment
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    editor.putBoolean("islogin",true);
                    editor.putString("id",mEmail);
                    editor.commit();
                    return pieces[1].equals(mPassword);
                }
            }
*/
            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Intent i=new Intent(LoginActivity.this,Accueilactivite.class);
                startActivity(i);
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

