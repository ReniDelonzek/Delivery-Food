package rd.com.demo.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import rd.com.demo.activity.ComecarCadastro;

import static rd.com.demo.auxiliares.LibraryClass.PREF;


public class BaixarFotoPerfilUsuario extends AsyncTask<Context, Void, String> {
    private Bitmap bitmap;
    private final String TAG = "BaixarFoto";

    @Override
    protected String doInBackground(Context... params) {
        Context context = params[0];
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        String userId = sp.getString("id", null);
        String token = sp.getString("token", null);
        URL url;
        switch (ComecarCadastro.mododeLogin){
            case 1:
                try {
                url = new URL("http://" + userId + token + "?sz=76");
                InputStream is = (InputStream) getObjeto(url);
                bitmap = BitmapFactory.decodeStream(is);
                saveBitmap(bitmap, "profile");
        } catch(Exception ex){
            Log.e(TAG, ex.getMessage());
        }
                break;
            case 2:
                try{
                    url = new URL("https://graph.facebook.com/" + userId +
                            "/picture?type=large&height=400&width=400");
                    InputStream is = (InputStream) getObjeto(url);
                    bitmap = BitmapFactory.decodeStream(is);
                    saveBitmap(bitmap, "profile");
                    capa(userId, token);
                }
                catch(Exception ex){
                    Log.e(TAG, ex.getMessage());
                }
                break;
        }


        return null;
    }
    private Object getObjeto(URL url) throws IOException {
        return url.getContent();
    }

    private void capa(String THE_USER_ID, String token){
        String URL = "https://graph.facebook.com/" + THE_USER_ID + "?fields=cover&access_token="
                + token;

        String finalCoverPhoto;
        HttpURLConnection urlConnection;
        BufferedReader reader;
        String resposta;
        try {
            URL url = new URL(URL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            StringBuilder buffer = new StringBuilder();
            while ((line = reader.readLine()) != null){
                buffer.append(line);
            }
            resposta = buffer.toString();

                JSONObject JODetails = new JSONObject(resposta);

                if (JODetails.has("cover")) {
                    String getInitialCover = null;

                        getInitialCover = JODetails.getString("cover");
                    if (getInitialCover.equals("null")) {
                        finalCoverPhoto = null;
                    } else {
                        JSONObject JOCover = JODetails.optJSONObject("cover");

                        if (JOCover.has("source"))  {
                            finalCoverPhoto = JOCover.getString("source");
                        } else {
                            finalCoverPhoto = null;
                        }
                    }
                } else {
                    finalCoverPhoto = null;
                }
                if (finalCoverPhoto != null){
                    url = new URL(finalCoverPhoto);
                    InputStream is = (InputStream) getObjeto(url);
                    bitmap = BitmapFactory.decodeStream(is);
                    saveBitmap(bitmap, "photocover");
                }
                Log.v(TAG, finalCoverPhoto);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    private void saveBitmap(Bitmap pBitmap, String nomeArquivo){
        try{
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            pBitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);

            File f = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "Demo" +
                    "/Imagens/");
            f.mkdirs();
            File a = new File(f, "." + nomeArquivo + ".png");
            a.createNewFile();
            FileOutputStream fo = new FileOutputStream(a);
            fo.write(bytes.toByteArray());
            fo.close();
            Log.v(TAG, "sucesso");
        }
        catch(Exception e){
            Log.e("Could not save", e.toString());
        }
    }
}
