package rd.com.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import rd.com.demo.R;
import rd.com.demo.adapter.AdapterDetalhesAmostra;
import rd.com.demo.auxiliares.Constants;
import rd.com.demo.item.firebase.Amostras;
import rd.com.demo.item.firebase.Produto;

public class DetalhesAmostra extends AppCompatActivity {
    private static final String TAG = "Detalhes";
    ImageView imageView;
    TextView nome, descricao, textView, vazio;
    ProgressBar progressBar;
    Button add;
    RecyclerView recyclerView;
    Amostras amostras;
    List<Produto> list;
    AdapterDetalhesAmostra adapter;
    public static String url;
    String id_estabelecimento, nome_estabelecimento;
    public static String tipo_estabelecimento, cidadecode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_amostra);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Café");
        list = new ArrayList<>();

        imageView = findViewById(R.id.imagem_produto);
        textView = findViewById(R.id.textView7);
        progressBar = findViewById(R.id.progressBar2);
        nome = findViewById(R.id.nome);
        descricao = findViewById(R.id.descricao);
        vazio = findViewById(R.id.textView3);
        recyclerView = findViewById(R.id.recycler_view);


        amostras = (Amostras) getIntent().getSerializableExtra("amostra");
        id_estabelecimento = amostras.getId_estabelecimento();
        nome_estabelecimento = amostras.getNome_estabelecimento();
        tipo_estabelecimento = amostras.getTipo_estabelecimento();
        cidadecode = amostras.getCidadecode();

        nome.setText(amostras.getTitulo());
        String string;
        if (amostras.getQuantidade() == 1){
            string = amostras.getQuantidade() + " opção";
        } else {
            string = amostras.getQuantidade() + " opções";
        }
        descricao.setText(string);
        Picasso.with(getApplicationContext()).
                load(amostras.getUrl()).into(imageView);

        adapter = new AdapterDetalhesAmostra(list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        url = amostras.getUrl();
        buscar_dados();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Imagem.class);
                intent.putExtra("url", amostras.getUrl());
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    private void buscar_dados() {
        FirebaseFirestore.getInstance()
                .collection("estabelecimentos")
                .document(Constants.cidade)
                .collection(cidadecode)
                .document("restaurantes")
                .collection(amostras.getId_estabelecimento())
                .document("produtos")
                .collection("produtos")
                .whereEqualTo("amostra", amostras.getCaminho())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                List<DocumentSnapshot> snapshotList = documentSnapshots.getDocuments();
                progressBar.setVisibility(View.GONE);
                textView.setVisibility(View.GONE);
                if (!snapshotList.isEmpty()) {
                    for (int i = 0; i < snapshotList.size(); i++) {
                        Produto p = snapshotList.get(i).toObject(Produto.class);
                        list.add(p);
                        adapter.notifyItemChanged(list.size() - 1);

                    }
                } else {
                    vazio.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
