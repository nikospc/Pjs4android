package grppjs4.sug;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import okhttp3.HttpUrl;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

import static android.provider.Telephony.Mms.Part.FILENAME;

public class EDT extends AsyncTask<Void, Void, Boolean> {


    Response response;
    String url = "http://ent-ng.parisdescartes.fr/export/ics.php";
    String id = "ii03208";
    Context ctx;
    String s="ii.ics";
    File file;

    EDT(Context c) {
        ctx = c;
         file=new File(ctx.getFilesDir(),s);


    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        OkHttpClient client = new OkHttpClient();

        //SI ON VEUT FAIRE DU GET
        HttpUrl.Builder httpBuider = HttpUrl.parse(url).newBuilder();
        httpBuider.addQueryParameter("login", id);
        Request request = new Request.Builder().url(httpBuider.build()).build();

        try { //REPONSE DU SERVEUR

            response = client.newCall(request).execute();
            BufferedSink sink = Okio.buffer(Okio.sink(file));
            sink.writeAll(response.body().source());
            sink.close();
            Log.d("copie", "doInBackground: tou a ete copie");
            return true;


        } catch (IOException e) {
            return false;
        }
    }

    @Override
    protected void onPostExecute(final Boolean success) {

        if (success) {


        }
        else{

            }
    }

}