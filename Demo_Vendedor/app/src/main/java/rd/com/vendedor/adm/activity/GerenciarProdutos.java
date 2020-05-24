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
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import rd.com.vendedor.R;
import rd.com.vendedor.adm.adapter.AdapterProdutos;
import rd.com.vendedor.adm.item.Amostras;
import rd.com.vendedor.adm.item.Produto;
import rd.com.vendedor.adm.utils.Constants;

public class GerenciarProdutos extends AppCompatActivity {
    RecyclerView recyclerView;
    List<Produto> produtos;
    public static String tipoEstabelecimento, idEstabelecimento, nomeEstabelecimento, cidadecode;
    TextView msg;
    ProgressBar progressBar;
    AdapterProdutos adapter_produtos;
    public static Amostras amostra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerenciar_produtos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Adicionar_produtos.class);
                intent.setAction("add_from_amostra");
                intent.putExtra(Constants.nomeEstabelecimento, nomeEstabelecimento);
                intent.putExtra(Constants.idEstabelecimento, idEstabelecimento);
                intent.putExtra(Constants.cidadeCode, cidadecode);

                intent.putExtra("amostracode", amostra.getCaminho());
                intent.putExtra("amostranome", amostra.getTitulo());
                intent.putExtra("quantidade", amostra.getQuantidade());
                intent.putExtra("categoria", amostra.getCategoria());
                intent.putExtra("url", amostra.getUrl());

                startActivity(intent);
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences("Detalhes", Context.MODE_PRIVATE);
        nomeEstabelecimento = sharedPreferences.getString(Constants.nomeEstabelecimento, "");
        idEstabelecimento = sharedPreferences.getString(Constants.idEstabelecimento, "");
        tipoEstabelecimento = sharedPreferences.getString(Constants.tipoEstabelecimento, "");
        cidadecode = sharedPreferences.getString(Constants.cidadeCode, "84623000");
        amostra = (Amostras) getIntent().getSerializableExtra("amostra");

        progressBar = findViewById(R.id.progressBar3);
        msg = findViewById(R.id.textView6);
        recyclerView = findViewById(R.id.recyclerView);
        produtos = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter_produtos = new AdapterProdutos(produtos);
        recyclerView.setAdapter(adapter_produtos);

        buscarDados();

    }

    private void buscarDados() {
        FirebaseFirestore.getInstance()
                .collection("estabelecimentos")
                .document(Constants.cidade)
                .collection(cidadecode)
                .document(tipoEstabelecimento)
                .collection(idEstabelecimento)
                .document("produtos")
                .collection("produtos")
                .whereEqualTo("amostra", amostra.getCaminho())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                progressBar.setVisibility(View.GONE);
                if (!documentSnapshots.isEmpty()){
                    for (DocumentChange dc : documentSnapshots.getDocumentChanges()) {
                        produtos.add(dc.getDocument().toObject(Produto.class));
                        adapter_produtos.notifyItemInserted(produtos.size() - 1);
                    }
                } else {
                    msg.setVisibility(View.VISIBLE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                Snackbar.make(recyclerView, "Ocorreu uma falha ao carregar os produtos", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

}
