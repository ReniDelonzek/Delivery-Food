package rd.com.demo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import rd.com.demo.R;
import rd.com.demo.adapter.AdapterMeusFavoritos;
import rd.com.demo.banco.sugarOs.AmostrasFavoritasDB;
import rd.com.demo.banco.sugarOs.ProdutoFavoritosDB;

public class MeusFavoritos extends AppCompatActivity {
    RecyclerView recyclerView;
    AdapterMeusFavoritos adapterMeusPedidos;
    List<Object> list;
    TextView textView;
    Button bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_favoritos);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        list = new ArrayList<>();

        textView = findViewById(R.id.textView8);
        bt = findViewById(R.id.button8);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapterMeusPedidos = new AdapterMeusFavoritos(list);
        recyclerView.setAdapter(adapterMeusPedidos);
        carregarFavoritos();

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void carregarFavoritos() {
        list.addAll(AmostrasFavoritasDB.listAll(AmostrasFavoritasDB.class));
        list.addAll(ProdutoFavoritosDB.listAll(ProdutoFavoritosDB.class));
        if (list.size() == 0){
            bt.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
        } else {
            adapterMeusPedidos.notifyDataSetChanged();
        }
    }

}
