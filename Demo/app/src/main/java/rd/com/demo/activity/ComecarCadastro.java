package rd.com.demo.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rd.com.demo.R;
import rd.com.demo.services.BaixarFotoPerfilUsuario;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static rd.com.demo.auxiliares.LibraryClass.PREF;


public class ComecarCadastro extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {
    final String TAG = "FazerCadastro";
    CardView bt_LoginEmail;
    CardView bt_loginFacebook, bt_LoginGoogle;
    GoogleApiClient googleApiClient;
    CallbackManager mCallbackManager;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE
    };
    EditText editText;
    boolean aberto = false;
    public static boolean finish = false;
    public static int mododeLogin;
    TextInputLayout textInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            finish();
        }//se estiver logado em alguma conta finaliza a atividade e volta para principal
        setContentView(R.layout.activity_criar_conta);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        registrarViews();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        if (googleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(googleApiClient);
        }
        bt_LoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mododeLogin = 1;
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(signInIntent, 1);
            }
        });

        mCallbackManager = CallbackManager.Factory.create();
        bt_loginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mododeLogin = 2;
                LoginManager.getInstance().logInWithReadPermissions(
                        ComecarCadastro.this, Arrays.asList("public_profile", "email"));
            }
        });
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Profile.fetchProfileForCurrentAccessToken();

                String token = loginResult.getAccessToken().getToken();
                String id = loginResult.getAccessToken().getUserId();
                SharedPreferences sp = getApplicationContext().getSharedPreferences(PREF, Context.MODE_PRIVATE);
                sp.edit().putString("id", id).apply();
                sp.edit().putString("token", token).apply();///salva dados do login

                BaixarFotoPerfilUsuario fotoPerfilUsuario = new BaixarFotoPerfilUsuario();
                fotoPerfilUsuario.execute(getApplicationContext());
                Log.v(TAG, "user_id = " + id);

                String email;
                String nome = "";
                String URL = "https://graph.facebook.com/" + id + "?fields=email,name&access_token="
                        + token;

                HttpURLConnection urlConnection = null;
                BufferedReader reader;
                String resposta;
                try {
                    java.net.URL url = new URL(URL);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.connect();


                    InputStream in =
                            new BufferedInputStream(urlConnection.getInputStream());
                    reader = new BufferedReader(new InputStreamReader(in));
                    String line = "";
                    StringBuilder buffer = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }
                    resposta = buffer.toString();
                    JSONObject JODetails = new JSONObject(resposta);
                    if (JODetails.has("email")) {
                        email = JODetails.getString("email");
                    } else {
                        email = null;
                    }
                    if (JODetails.has("name")) {
                        nome = JODetails.getString("name");
                    } else {
                        if (Profile.getCurrentProfile() != null) {
                            nome = Profile.getCurrentProfile().getName();
                        }
                    }
                    chamar_activity(nome, email);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancel() {
                Toast.makeText(ComecarCadastro.this, "Cancelado", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.v(TAG, "ErroLoginFb: " + error.getMessage());
            }
        });
        bt_LoginEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ConcluirCadastro.class);
                intent.setAction("Email");
                startActivity(intent);
                finish();
            }
        });
        //verifyStoragePermissions(this);

        permissoes(this);
    }

    private void permissoes(final Activity activity) {
        // Se não possui permissão
        if (ContextCompat.checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Verifica se já mostramos o alerta e o usuário negou na 1ª vez.
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, WRITE_EXTERNAL_STORAGE)) {
                // Caso o usuário tenha negado a permissão anteriormente, e não tenha marcado o check "nunca mais mostre este alerta"
                // Podemos mostrar um alerta explicando para o usuário porque a permissão é importante.
            } else {
                // Solicita a permissão
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, 0);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Usuário aceitou a permissão!
                } else {
                    // Usuário negou a permissão.
                    // Não podemos utilizar esta funcionalidade.
                }
            }
        }
    }

    private void chamar_activity(String nome, String email) {
        Intent intent = new Intent(getApplicationContext(), ConcluirCadastro.class);
        intent.setAction("Social");
        intent.putExtra("nome", nome);
        intent.putExtra("email", email);
        startActivity(intent);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                try {
                    String photourl = result.getSignInAccount().getPhotoUrl().getAuthority();
                    String patch = result.getSignInAccount().getPhotoUrl().getPath();
                    SharedPreferences sp = getApplicationContext().getSharedPreferences(PREF, Context.MODE_PRIVATE);
                    sp.edit().putString("id", photourl).apply();
                    sp.edit().putString("token", patch).apply();
                    BaixarFotoPerfilUsuario fotoPerfilUsuario = new BaixarFotoPerfilUsuario();
                    fotoPerfilUsuario.execute(getApplicationContext());
                    String email = result.getSignInAccount().getEmail();
                    String nome = result.getSignInAccount().getDisplayName();
                    if (email != null && nome != null) {
                        chamar_activity(nome, email);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Log.v(TAG, "erroCriarContaGoogle: " + result.getStatus().toString());
            }
        }
    }

    private void registrarViews() {
        bt_LoginEmail = findViewById(R.id.login_email);
        bt_loginFacebook = findViewById(R.id.login_fb);
        bt_LoginGoogle = findViewById(R.id.login_gg);
        editText = findViewById(R.id.email);
        textInputLayout = findViewById(R.id.TILemail);
    }

    public boolean validateEmail(String email) {
        final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher;
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void t(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        t("Ocorreu um erro de conexão");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (finish) {
            finish();
        }
    }

}

