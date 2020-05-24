package rd.com.demo.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import rd.com.demo.R;
import rd.com.demo.auxiliares.Constants;

public class Localizacao extends AppCompatActivity {
    private static final int MY_LOCATION_REQUEST_CODE = 0;
    private static final String TAG = "Location";
    protected Location mLastLocation;
    Spinner spinner;
    CardView cardView;
    Button bt;
    ProgressBar progressBar;
    TextView textView;
    boolean modoManual = true;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localizacao);


        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(Localizacao.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    getLastLocation();
                    spinner.setVisibility(View.GONE);
                    textView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    cardView.setVisibility(View.GONE);
                    modoManual = false;
                } else {
                    chamarDialogo();
                }
            }
        });
        progressBar = findViewById(R.id.progressBar4);
        textView = findViewById(R.id.textView10);
        cardView = findViewById(R.id.card);
        spinner = findViewById(R.id.spinner);
        bt = findViewById(R.id.button9);
        ArrayAdapter<String> adapter;

        final List<String> arrayList = new ArrayList<String>();
        arrayList.add("Cruz Machado - PR");
        arrayList.add("Campinas - SP");
        arrayList.add("São Paulo - SP");

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, arrayList);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modoManual) {
                    SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    String id = obterId(spinner.getSelectedItem().toString());
                    editor.putString(Constants.cidadecode, id).apply();
                    editor.putString(Constants.cidade, spinner.getSelectedItem().toString()).apply();
                    progressDialog = ProgressDialog.show(Localizacao.this,
                            "", "Obtendo informações do Local", true);
                    verificar(id);
                }
            }
        });

    }

    private void verificar(String id) {
        FirebaseDatabase.getInstance().getReference()
                .child("Cidades")
                .child(id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                            Toast.makeText(getApplicationContext(), "Localização definida com sucesso", Toast.LENGTH_SHORT).show();
                        } else {
                            startActivity(new Intent(getApplicationContext(), Indisponivel.class));
                            finish();
                            SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.remove(Constants.cidadecode).apply();
                            editor.remove(Constants.cidade).apply();
                        }
                        if (progressDialog != null)
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        if (progressDialog != null)
                            progressDialog.dismiss();
                    }
                });

    }

    public String obterId(String cidade){
        AssetManager gestor_arquivos_asset = getResources().getAssets();
        InputStream canal_de_transmicao;
        String s = "";
        try {
            canal_de_transmicao = gestor_arquivos_asset.open("cidades_estado_com_id_sem_acento.txt");
            InputStreamReader gestor_para_leitura_em_caracteres = new InputStreamReader(canal_de_transmicao);
            BufferedReader classe_de_leitura = new BufferedReader(gestor_para_leitura_em_caracteres);

            String recebe_string;
            String estado_selecionado = cidade.substring(
                    cidade.indexOf("-") + 1
            ).replace(" ", "");
            while((recebe_string = classe_de_leitura.readLine()) != null){
                String ci = recebe_string.split(",")[3];
                String estado_atual = recebe_string.split(",")[2];
                if (estado_atual.equals(estado_selecionado)
                        && ci.toLowerCase().equals(removerAcentos(cidade.substring(0, cidade.indexOf("-") - 1)).toLowerCase())){
                    s = recebe_string.split(",")[0];
                    break;
                }
            }


            canal_de_transmicao.close();//fecha obterDados canal de transmicao
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }
    public String removerAcentos(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }
    private void chamarDialogo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setNegativeButton("Definir manualmente", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                modoManual();
            }
        });
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.requestPermissions(Localizacao.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_LOCATION_REQUEST_CODE);
            }
        });
        AlertDialog alerta;
        alerta = builder.create();
        alerta.setTitle("Permissão necessária");
        alerta.setMessage("Precisamos saber sua localização para que com base " +
                "nela possamos exibir os estabelecimentos mais próximos");
        alerta.show();
    }
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (Objects.equals(permissions[0], Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
                spinner.setVisibility(View.GONE);
                textView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                cardView.setVisibility(View.GONE);
                modoManual = false;
            } else {
                modoManual();
            }
        }
    }
    private void obterDados(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String errorMessage = "";
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    // In this sample, get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = "Não acessivel";
            Log.e(TAG, errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = "Latitude ou Longitude invalidas";
            Log.e(TAG, errorMessage + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " +
                    location.getLongitude(), illegalArgumentException);
            modoManual();
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size() == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = "Sem endereco definido";
                Log.e(TAG, errorMessage);
            }
            //deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
            Toast.makeText(getApplicationContext(), "Erro ao obter os dados", Toast.LENGTH_SHORT).show();
            modoManual();
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            Log.i(TAG, "Endereco");
            deliverResultToReceiver(
                    addresses, addressFragments);
        }

    }
    private void modoManual() {
        modoManual = true;
        cardView.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.VISIBLE);
        textView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }
    private void deliverResultToReceiver(List<Address> join, ArrayList<String> addressFragments) {
        SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String sig = join.get(0).getAddressLine(0).substring(
                join.get(0).getAddressLine(0).indexOf(join.get(0).getSubAdminArea()),
                join.get(0).getAddressLine(0).indexOf(join.get(0).getPostalCode()) - 2
        );

        progressDialog = ProgressDialog.show(Localizacao.this,
                "", "Obtendo informações do Local", true);
        String id =  obterId(sig);
        editor.putString(Constants.cidadecode, id).apply();
        editor.putString(Constants.cidade, join.get(0).getSubAdminArea()).apply();
        //setResult(RESULT_OK);
        verificar(id);

    }
    private void getLastLocation() {
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
        } else {
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {

                                //obtém a última localização conhecida
                                mLastLocation = task.getResult();
                                //Toast.makeText(getApplicationContext(), "Latitude: " + String.valueOf(mLastLocation.getLatitude()) +
                                //      /"Longitude: " + String.valueOf(mLastLocation.getLongitude()), Toast.LENGTH_SHORT).show();

                                //startIntentService();
                                obterDados(mLastLocation);

                            } else {
                                modoManual();
                                Toast.makeText(getApplicationContext(), "Falha ao recuperar as info", Toast.LENGTH_SHORT).show();
                                Log.e("Erro", task.getException().getMessage());
                                spinner.setVisibility(View.VISIBLE);

                                //Não há localização conhecida ou houve uma excepção
                                //A excepção pode ser obtida com task.getException()
                            }
                        }
                    });
        }
    }



}