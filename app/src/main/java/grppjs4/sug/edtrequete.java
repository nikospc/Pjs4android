package grppjs4.sug;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

public class edtrequete extends AsyncTask<Void, Void, Boolean> {


    Response response;
    String url = "http://ent-ng.parisdescartes.fr/export/ics.php";
    String id = "";
    Context ctx;
    String s="ii.ics";
    File file;

    edtrequete(Context c, String id) {
        ctx = c;
         file=new File(ctx.getFilesDir(),s);
         this.id=id;

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