package grppjs4.sug;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Signup extends AppCompatActivity {
    private EditText inputEmail, inputPassword;
    private Button  btnSignUp,btnretour;
    private ConstraintLayout cl;
    private  ConstraintLayout cD;
    private SignupTask mSignupTask;
    private String email,password;
    private String nom,prenom,code,group,pseudo;
    private EditText inputCode,inputGroup,inputnom,inputprenom,inputpseudo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        inputEmail = (EditText) findViewById(R.id.txtemail);
        inputPassword = (EditText) findViewById(R.id.password);
        inputCode = (EditText) findViewById(R.id.txtCodeEtu);
        inputGroup = (EditText) findViewById(R.id.txtgroupetd);
        inputnom = (EditText) findViewById(R.id.txtnomD);
        inputprenom = (EditText) findViewById(R.id.txtprenomD);
        inputpseudo = (EditText) findViewById(R.id.txtPseudo);
        btnSignUp=(Button)findViewById(R.id.sign_up_button2);
         cl = findViewById(R.id.inscr1);
         cD = findViewById(R.id.incr_dem);
   btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptsignup();
            }
        });

        btnretour=(Button)findViewById(R.id.btnretour);
        btnretour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cD.setVisibility(View.GONE);
                cl.setVisibility(View.VISIBLE);
            }
        });
    }

    private void attemptsignup() {
        nom=inputnom.getText().toString().trim();
        prenom=inputprenom.getText().toString().trim();
        code=inputCode.getText().toString().trim();
        group=inputGroup.getText().toString().trim();
        pseudo=inputpseudo.getText().toString().trim();
        mSignupTask = new SignupTask(email, password,nom,prenom,code,group,pseudo);
        mSignupTask.execute();
    }

    public void suiteClicked(View view) {


         email = inputEmail.getText().toString().trim();
         password = inputPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            return;
        }

        cl.setVisibility(View.GONE);
        cD.setVisibility(View.VISIBLE);


    }

    public class SignupTask extends AsyncTask<Void, Integer, Boolean> {
        private String url= "http://www.nikolaspaci.fr/index.php?controle=coSUG&action=suscribemob";//url où l'on va acceder
        String email,  password,  nom,  prenom,  code,  group,  pseudo;
        private Response response;
        JSONObject myJson = null;
        String msgerror;
        public SignupTask(String email, String password, String nom, String prenom, String code, String group, String pseudo) {
            this.email=email;
            this.password=password;
            this.nom=nom;
            this.prenom=prenom;
            this.code=code;
            this.group=group;
            this.pseudo=pseudo;

        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("mail", email)
                    .addFormDataPart("mdp",password)
                    .addFormDataPart("pseudo", pseudo)
                    .addFormDataPart("group","5")
                    .addFormDataPart("prenom", prenom)
                    .addFormDataPart("nom",nom)
                    .addFormDataPart("code",code)
                    .build();

            Request.Builder builder = new Request.Builder();
            builder.url(url);
            builder.post(requestBody);
            Request request = builder.build();
            try { //REPONSE DU SERVEUR

                response = client.newCall(request).execute();
                try {
                    myJson = new JSONObject(response.body().string());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String verifinsc = myJson.optString("verifinsc");
                Log.i("hu", verifinsc);
                if(verifinsc.equals("true")){ //on regarde si il retourne true
                    return true;
                }
                else{
                     msgerror=myJson.optString("erreur");
                    return false; //sinon retour
                }
            } catch (IOException e) {
                return false;
            }


        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mSignupTask = null;
            //showProgress(false);

            if (success) {
                Intent i=new Intent(Signup.this,LoginActivity.class);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(getApplicationContext(),msgerror, Toast.LENGTH_SHORT).show();            }
        }

        @Override
        protected void onCancelled() {
            mSignupTask = null;
            //showProgress(false);
        }
    }

}

