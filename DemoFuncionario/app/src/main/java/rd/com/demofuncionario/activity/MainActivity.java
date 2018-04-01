package rd.com.demofuncionario.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import rd.com.demofuncionario.R;
import rd.com.demofuncionario.adapter.MainActivityadapter;
import rd.com.demofuncionario.auxiliar.Constants;
import rd.com.demofuncionario.item.firebase.Pedidos;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "Funcionario.class";
    RecyclerView recyclerView;
    MainActivityadapter adapter;
    List<Pedidos> list;
    String estabelecimentoid, estabelecimentotipo, cidadecode;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_funcionario);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Suporte.class));
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        estabelecimentoid = sharedPreferences.getString(Constants.idEstabelecimento, "");
        estabelecimentotipo = sharedPreferences.getString(Constants.tipoEstabelecimento, "");
        cidadecode = sharedPreferences.getString(Constants.cidadeCode, "");

            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            progressBar = findViewById(R.id.progressBar);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            list = new ArrayList<>();
            recyclerView = findViewById(R.id.recyclerView);
            adapter = new MainActivityadapter(list);
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            recyclerView.setAdapter(adapter);

            carregar_pedidos(Constants.enviado_code, Constants.andamento_code);
            definirHeader();
    }

    private void carregar_pedidos(int start, int end) {
        FirebaseFirestore.getInstance()
                .collection("estabelecimentos")
                .document(Constants.cidade)
                .collection(cidadecode)
                .document(estabelecimentotipo)
                .collection(estabelecimentoid)
                .document("pedidos")
                .collection("novos")
                .whereGreaterThan("status_code", start - 1)//o -1 se faz necessario pois ele so faz busca do tal numero pra cima
                .whereLessThan("status_code", end + 1)//o +1 é usado pros resultados abragerem os pedidos prontos
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        progressBar.setVisibility(View.GONE);
                        if (documentSnapshots != null) {
                            if (!documentSnapshots.isEmpty()) {
                                for (DocumentChange dc : documentSnapshots.getDocumentChanges()) {
                                    if (dc.getType() == DocumentChange.Type.ADDED) {
                                        Pedidos pedidos = dc.getDocument().toObject(Pedidos.class);
                                        pedidos.setData(dc.getDocument().getString("data"));
                                        pedidos.setHora(dc.getDocument().getString("hora"));
                                        pedidos.setId(dc.getDocument().getId());
                                        list.add(pedidos);
                                        adapter.notifyItemInserted(list.size() - 1);
                                    } else if (dc.getType() == DocumentChange.Type.MODIFIED){
                                        Pedidos pedidos = dc.getDocument().toObject(Pedidos.class);
                                        pedidos.setData(dc.getDocument().getString("data"));
                                        pedidos.setHora(dc.getDocument().getString("hora"));
                                        pedidos.setId(dc.getDocument().getId());
                                            for(int i = 0; i < list.size(); i++){
                                                if (list.get(i).getId().equals(dc.getDocument().getId())) {
                                                    list.set(i, pedidos);
                                                    adapter.notifyItemChanged(i);
                                                    break;
                                                }
                                            }
                                        }
                                    }
                            } else {//documento vazio
                                Snackbar.make(recyclerView, "Nenhum pedido a ser processado \uD83D\uDE04", Snackbar.LENGTH_SHORT).show();
                            }
                        } else {
                            e.printStackTrace();
                            if (e.getMessage().contains("PERMISSION_DENIED: Missing or insufficient permissions.")){
                                Snackbar.make(recyclerView, "Parece que você não tem permissão de acesso," +
                                        " tem certeza que está na conta de funcionario?", Snackbar.LENGTH_LONG).show();
                            } else {
                                Snackbar.make(recyclerView, "Houve uma falha na recuperação dos dados," +
                                        " por favor tente novamente", Snackbar.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.funcionario, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.andamento) {
            list.clear();
            adapter.notifyDataSetChanged();
            carregar_pedidos(Constants.recebido_code, Constants.andamento_code);
        } else if (id == R.id.prontos) {
            list.clear();
            adapter.notifyDataSetChanged();
            carregar_pedidos(Constants.pronto_code, Constants.aguardandoRetirada_code);
        } else if (id == R.id.finalizados) {
            list.clear();
            adapter.notifyDataSetChanged();
            carregar_pedidos(Constants.concluido_code, Constants.cancelado_code);
        } else if (id == R.id.suporte) {
            startActivity(new Intent(getApplicationContext(), Suporte.class));
        } else if (id == R.id.sair) {
            sair();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void definirHeader() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TextView nomeUsuario = header.findViewById(R.id.nome);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
            String nome = sharedPreferences.getString(Constants.nome, "");
            for(int i = 0; i < nome.length() - 1; i++){
                if (nome.substring(i, i + 1).equals(" ")){
                    nome = nome.substring(0, i);
                    i = nome.length();//finaliza o loop
                }
            }
            nomeUsuario.setText(nome);
            setTitle(String.format("Olá %s ☺", nome));

        } else {

        }
    }
    private void sair(){
        SharedPreferences sharedPreferences =
                getSharedPreferences("Login", Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }
}
