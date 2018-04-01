package rd.com.vendedor.adm.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import rd.com.vendedor.R;
import rd.com.vendedor.adm.adapter.Adapter_Funcionarios;
import rd.com.vendedor.adm.item.Funcionario;
import rd.com.vendedor.adm.utils.Constants;

public class Gerenciar_funcionarios extends AppCompatActivity {
    private static final String TAG = "Gerir Funcionarios";
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    List<Funcionario> funcionarios;
    String estabelecimento, estabelecimento_id, tipoEstabelecimento, cidade, cidadecode;//essas  serão as identificacoes de cada estabelecimento,
    //que serao obtidas no login e salvas na memoria do dispositivo
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerir_funcionarios);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Gerenciar Funcionários");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        funcionarios = new ArrayList<>();
        progressBar = findViewById(R.id.progressBar2);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Adc_funcionario.class));
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences("Detalhes", Context.MODE_PRIVATE);
        estabelecimento = sharedPreferences.getString(Constants.nomeEstabelecimento, "");
        estabelecimento_id = sharedPreferences.getString(Constants.idEstabelecimento, "");
        tipoEstabelecimento = sharedPreferences.getString(Constants.tipoEstabelecimento, "");
        cidadecode = sharedPreferences.getString(Constants.cidadeCode, "");
        cidade = sharedPreferences.getString(Constants.cidade, "");

        recyclerView = findViewById(R.id.recyclerView);
        adapter = new Adapter_Funcionarios(funcionarios);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
        buscar_dados();

    }

    private void buscar_dados() {
        FirebaseFirestore.getInstance()
                .collection("Adm")
                .document(estabelecimento_id)
                .collection("funcionarios")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                if (!documentSnapshots.isEmpty()) {
                    progressBar.setVisibility(View.GONE);
                    List<DocumentSnapshot> snapshotList = documentSnapshots.getDocuments();
                    Log.v(TAG, snapshotList.toString());
                    for (int i = 0; i < snapshotList.size(); i++) {
                        funcionarios.add(snapshotList.get(i).toObject(Funcionario.class));
                        adapter.notifyItemInserted(funcionarios.size() - 1);
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(recyclerView, "Adicione seu primeiro funcionario clicando no botão '+'", Snackbar.LENGTH_INDEFINITE).show();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

}
