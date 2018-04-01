package rd.com.demo.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import rd.com.demo.R;
import rd.com.demo.auxiliares.Constants;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Localizacao extends AppCompatActivity {
    private static final int MY_LOCATION_REQUEST_CODE = 0;
    private static final String TAG = "Location";
    protected Location mLastLocation;
    Spinner spinner;
    CardView cardView;
    Button bt;
    ProgressBar progressBar;
    TextView textView;
    boolean modoManual = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localizacao);

        progressBar = findViewById(R.id.progressBar4);
        textView = findViewById(R.id.textView10);
        cardView = findViewById(R.id.card);
        spinner = findViewById(R.id.spinner);
        bt = findViewById(R.id.button9);
        ArrayAdapter<String> adapter;

        final List<String> arrayList = new ArrayList<String>();
        arrayList.add("Cruz Machado");
        arrayList.add("Campinas");

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, arrayList);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //if (ContextCompat.checkSelfPermission(Localizacao.this, Manifest.permission.ACCESS_FINE_LOCATION)
          //      == PackageManager.PERMISSION_GRANTED) {
            //getLastLocation();
        //} else {
            chamarDialogo();
        //}/

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modoManual) {
                    SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    switch (spinner.getSelectedItemPosition()) {
                        case 0:
                            editor.putString(Constants.cidadecode, "84623000").apply();
                            editor.putString(Constants.cidade, "Cruz machado").apply();
                            break;
                        case 1:
                            editor.putString(Constants.cidadecode, "13010060").apply();
                            editor.putString(Constants.cidade, "Campinas").apply();
                    }
                    Toast.makeText(getApplicationContext(), "Localização definida", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
            }
        });

    }
    private void chamarDialogo(){
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
            } else {
                modoManual();
            }
        }
    }
    private void o(Location location){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String errorMessage = "";

        // Get the location passed to this service through an extra.
       // Location location = intent.getParcelableExtra(
         //       Constants.LOCATION_DATA_EXTRA);
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
        if (addresses == null || addresses.size()  == 0) {
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
            for(int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            Log.i(TAG, "Endereco");
            deliverResultToReceiver(
                    addresses);
        }

    }
    private void modoManual(){
        modoManual = true;
        cardView.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.VISIBLE);
        textView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    private void deliverResultToReceiver(List<Address> join) {
        SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(Constants.cidadecode, join.get(0).getPostalCode().replace("-", "")).apply();
        editor.putString(Constants.cidade, join.get(0).getSubAdminArea()).apply();
        //setResult(RESULT_OK);

        Toast.makeText(getApplicationContext(), "Localização definida", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
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
                            o(mLastLocation);

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