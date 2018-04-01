package rd.com.vendedor.adm.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import rd.com.vendedor.R;
import rd.com.vendedor.adm.adapter.Adapter_selecionar_amostra;
import rd.com.vendedor.adm.item.Amostras;
import rd.com.vendedor.adm.utils.Constants;

public class Escolher_amostra extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    List<Amostras> amostras;
    private String categoria;
    String estabelecimento, estabelecimento_id, tipoEstabelecimento, cidadecode;//essas  serão as identificacoes de cada estabelecimento,
    //que serao obtidas no login e salvas na memoria do dispositivo
    int tipo;//tipo de estabelecimento
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escolher_amostra);

        SharedPreferences sharedPreferences = getSharedPreferences("Detalhes", Context.MODE_PRIVATE);
        estabelecimento = sharedPreferences.getString(Constants.nomeEstabelecimento, "");
        estabelecimento_id = sharedPreferences.getString(Constants.idEstabelecimento, "");
        tipoEstabelecimento = sharedPreferences.getString(Constants.tipoEstabelecimento, "");
        cidadecode = sharedPreferences.getString(Constants.tipoEstabelecimento, "");

        recyclerView = findViewById(R.id.recyclerView);
        amostras = new ArrayList<>();
        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        adapter = new  Adapter_selecionar_amostra(amostras, new Adapter_selecionar_amostra.OnItemClickListener() {
            @Override
            public void onItemClick(Amostras item) {
                Toast.makeText(getApplicationContext(), "Amostra selecionada", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("resposta", item.getCaminho());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        categoria = getIntent().getStringExtra("categoria");
        buscar_dados();

    }
    private void buscar_dados(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("estabelecimentos")
                .document(Constants.cidade)
                .collection(cidadecode)
                .document(tipoEstabelecimento)
                .collection("amostras")
                .whereEqualTo("categoria", categoria)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                List<DocumentSnapshot> snapshotList = documentSnapshots.getDocuments();
                for(int i = 0; i < snapshotList.size(); i++){
                    Amostras p = snapshotList.get(i).toObject(Amostras.class);
                    p.setCaminho(snapshotList.get(i).getId());
                    amostras.add(p);
                    adapter.notifyItemInserted(amostras.size() - 1);
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
