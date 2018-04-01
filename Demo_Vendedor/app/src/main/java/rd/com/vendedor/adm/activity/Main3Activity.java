package rd.com.vendedor.adm.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import rd.com.vendedor.R;
import rd.com.vendedor.adm.adapter.Adapter_Ammostras;
import rd.com.vendedor.adm.item.Amostras;
import rd.com.vendedor.adm.item.UserAdm;
import rd.com.vendedor.adm.utils.Constants;

import static com.google.firebase.firestore.DocumentChange.Type.ADDED;
import static com.google.firebase.firestore.DocumentChange.Type.MODIFIED;
import static com.google.firebase.firestore.DocumentChange.Type.REMOVED;

public class Main3Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    public static Context context;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    LinearLayoutManager layoutManager;
    List<Amostras> produtos;
    ProgressBar progressBar;
    String estabelecimento, estabelecimento_id, tipoEstabelecimento, cidadecode, cidade;//essas  serão as identificacoes de cada estabelecimento,
    //que serao obtidas no login e salvas na memoria do dispositivo
    int tipo;//tipo de estabelecimento
    String nome;
    Snackbar snackbar = null;
    List<String> categorias;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (FirebaseAuth.getInstance().getCurrentUser() == null){
            startActivity(new Intent(getApplicationContext(), Login.class));
        }else {
            categorias = new ArrayList<>();
            context = this;
            produtos = new ArrayList<>();
            setTitle("Amostras");

            SharedPreferences sharedPreferences = getSharedPreferences("Detalhes", Context.MODE_PRIVATE);
            estabelecimento = sharedPreferences.getString(Constants.nomeEstabelecimento, "");
            estabelecimento_id = sharedPreferences.getString(Constants.idEstabelecimento, "");
            tipoEstabelecimento = sharedPreferences.getString(Constants.tipoEstabelecimento, "");
            cidadecode = sharedPreferences.getString(Constants.cidadeCode, "");
            cidade = sharedPreferences.getString(Constants.cidade, "");


            nome = sharedPreferences.getString(Constants.nome, "");

            verif_adm();
            switch (tipoEstabelecimento) {
                case Constants.restaurantes:
                    tipo = 0;
                    break;
                case Constants.lanchonetes:
                    tipo = 1;
                    break;
                case Constants.sorveterias:
                    tipo = 2;
                    break;
            }


            FloatingActionButton fab = findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (snackbar !=  null){
                        snackbar.dismiss();
                    }
                    Intent intent = new Intent(getApplicationContext(), Adicionar_amostras.class);
                    intent.putExtra(Constants.nomeEstabelecimento, estabelecimento);
                    intent.putExtra(Constants.idEstabelecimento, estabelecimento_id);
                    intent.putExtra(Constants.tipoEstabelecimento, tipoEstabelecimento);
                    startActivity(intent);
                }
            });
            progressBar = findViewById(R.id.progressBar);
            recyclerView = findViewById(R.id.recyclerView);
            adapter = new Adapter_Ammostras(produtos);
            layoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
            buscarDados("");

            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
            definirMenu();
            definirNavigationHeader();
        }
    }
    private void verif_adm(){
        FirebaseFirestore.getInstance()
                .collection("Adm")
                .document(estabelecimento_id)
                .collection("info")
                .document("Adm")
                .collection("ids")
                .document(FirebaseAuth.getInstance().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.v("MainActivity", "Login Ok");
                atualizar_dados(documentSnapshot);
            }

            private void atualizar_dados(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    UserAdm userAdm = documentSnapshot.toObject(UserAdm.class);
                    SharedPreferences sharedPreferences =
                            getSharedPreferences("Detalhes", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    String estabelecimentoId = documentSnapshot.get("estabelecimentoid").toString();
                    String estabelecimentoTipo = documentSnapshot.get("estabelecimentotipo").toString();
                    editor.putString(Constants.idEstabelecimento, estabelecimentoId).apply();
                    editor.putString(Constants.nomeEstabelecimento,
                            documentSnapshot.get("estabelecimentonome").toString()).apply();
                    editor.putString(Constants.tipoEstabelecimento, estabelecimentoTipo).apply();


                    editor.putString(Constants.nome, userAdm.getNome()).apply();
                    editor.putString(Constants.sobrenome, userAdm.getSobrenome()).apply();
                    editor.putString(Constants.id, FirebaseAuth.getInstance().getUid()).apply();
                    editor.putString(Constants.cargo, userAdm.getCargo()).apply();
                } else {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getApplicationContext(), Login.class));
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                FirebaseAuth.getInstance().signOut();
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Ouve uma falha na autenticação, por favor faça o Login novamente",
                        Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });
    }
    private void definirNavigationHeader() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TextView nomeUsuario = header.findViewById(R.id.nome);
        nomeUsuario.setText(nome);

    }
    private void definirMenu() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        if (tipo == 0) {//restaurantes
            categorias.add("Pratos");
            categorias.add("Porções e Entradas");
            categorias.add("Petiscos");
            categorias.add("Carnes");
            categorias.add("Massas");
            categorias.add("Saladas");
            categorias.add("Sobremessas");
            categorias.add("Lanches");
            categorias.add("Bebidas");

            menu.findItem(R.id.item_1).setTitle("Pratos");
            menu.findItem(R.id.item_2).setTitle("Porções e Entradas");
            menu.findItem(R.id.item_3).setTitle("Petiscos");
            menu.findItem(R.id.item_4).setTitle("Carnes");
            menu.findItem(R.id.item_5).setTitle("Massas");
            menu.findItem(R.id.item_6).setTitle("Saladas");
            menu.findItem(R.id.item_7).setTitle("Sobremessas");
            menu.findItem(R.id.item_8).setTitle("Lanches");
            menu.findItem(R.id.item_9).setTitle("Bebidas");

            menu.findItem(R.id.item_10).setVisible(false);
        } else if (tipo == 1){//lanchonetes
            categorias.add("Aperitivos");
            categorias.add("Sanduiches");
            categorias.add("Salgados");
            categorias.add("Doces");
            categorias.add("Bebidas");
            categorias.add("Bebidas Alcoolicas");
            categorias.add("Frios");
            categorias.add("Fritos");
            categorias.add("Pratos Prontos");
            categorias.add("Outros");

            menu.findItem(R.id.item_1).setTitle("Aperitivos");
            menu.findItem(R.id.item_2).setTitle("Sanduiches");
            menu.findItem(R.id.item_3).setTitle("Salgados");
            menu.findItem(R.id.item_4).setTitle("Doces");
            menu.findItem(R.id.item_5).setTitle("Refrigerantes");
            menu.findItem(R.id.item_6).setTitle("Bebidas Alcoolicas");
            menu.findItem(R.id.item_7).setTitle("Frios");
            menu.findItem(R.id.item_8).setTitle("Fritos");
            menu.findItem(R.id.item_9).setTitle("Pratos Prontos");
            menu.findItem(R.id.item_10).setTitle("Outros");
            //menu.findItem(R.id.nav_pkg_manage).setVisible(false);//In case you want to remove menu item

        }


        navigationView.setNavigationItemSelectedListener(this);
    }
    private void buscarDados(String categoria){
        progressBar.setVisibility(View.VISIBLE);
        if (categoria.isEmpty()) {
            setTitle("Amostras");

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("estabelecimentos")
                    .document("cidade")
                    .collection(cidadecode)
                    .document(tipoEstabelecimento)
                    .collection("amostras")
                    .whereEqualTo("id_estabelecimento", estabelecimento_id)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w(TAG, "listen:error", e);
                                return;
                            }
                            if (!documentSnapshots.isEmpty()) {
                                progressBar.setVisibility(View.GONE);
                                for (DocumentChange dc : documentSnapshots.getDocumentChanges()) {
                                    switch (dc.getType()) {
                                        case ADDED:
                                            Amostras p = dc.getDocument().toObject(Amostras.class);
                                            p.setCaminho(dc.getDocument().getId());
                                            produtos.add(p);
                                            adapter.notifyItemInserted(produtos.size() - 1);
                                            break;
                                        case MODIFIED:
                                            Amostras l = dc.getDocument().toObject(Amostras.class);
                                            for(int i = 0; i < produtos.size(); i++){
                                                if (produtos.get(i).getCaminho().equals(l.getCaminho())){
                                                    produtos.set(i, l);//atualiza o objeto
                                                    adapter.notifyItemChanged(i);//notifica o adapter da atualizacao
                                                    break;
                                                }
                                            }
                                            break;
                                        case REMOVED:
                                            Amostras o = dc.getDocument().toObject(Amostras.class);
                                            for(int i = 0; i < produtos.size(); i++){
                                                if (produtos.get(i).getCaminho().equals(o.getCaminho())){
                                                    produtos.remove(i);///remove o produto atraves do indice
                                                    adapter.notifyItemRemoved(i);//notifica o adapter
                                                    break;//finaliza o looop
                                                }
                                            }
                                            break;
                                    }
                                }

                            } else {
                                progressBar.setVisibility(View.GONE);
                                Snackbar.make(recyclerView, "Adicione sua primeira amostracode clicando no botão +", Snackbar.LENGTH_INDEFINITE).show();
                            }
                        }
                    });
        } else {
            setTitle(categoria);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("estabelecimentos")
                    .document(tipoEstabelecimento)
                    .collection("amostras")
                    .whereEqualTo("id_estabelecimento", estabelecimento_id)
                    .whereEqualTo("categoria", categoria)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w(TAG, "listen:error", e);
                                return;
                            }
                            if (!documentSnapshots.isEmpty()) {
                                progressBar.setVisibility(View.GONE);
                                for (DocumentChange dc : documentSnapshots.getDocumentChanges()) {
                                    switch (dc.getType()) {
                                        case ADDED:
                                            Amostras p = dc.getDocument().toObject(Amostras.class);
                                            p.setCaminho(dc.getDocument().getId());
                                            produtos.add(p);
                                            adapter.notifyItemInserted(produtos.size() - 1);
                                            break;
                                        case MODIFIED:
                                            Amostras l = dc.getDocument().toObject(Amostras.class);
                                            for(int i = 0; i < produtos.size(); i++){
                                                if (produtos.get(i).getCaminho().equals(l.getCaminho())){
                                                    produtos.set(i, l);//atualiza o objeto
                                                    adapter.notifyItemChanged(i);//notifica o adapter da atualizacao
                                                    break;
                                                }
                                            }
                                            break;
                                        case REMOVED:
                                            Amostras o = dc.getDocument().toObject(Amostras.class);
                                            for(int i = 0; i < produtos.size(); i++){
                                                if (produtos.get(i).getCaminho().equals(o.getCaminho())){
                                                    produtos.remove(i);///remove o produto atraves do indice
                                                    adapter.notifyItemRemoved(i);//notifica o adapter
                                                    break;//finaliza o looop
                                                }
                                            }
                                            break;
                                    }
                                }

                            } else {
                                progressBar.setVisibility(View.GONE);
                                Snackbar.make(recyclerView, "Adicione sua primeira amostracode clicando no botão +", Snackbar.LENGTH_INDEFINITE).show();
                            }
                        }
                    });
        }
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
        getMenuInflater().inflate(R.menu.main3, menu);

        /*
        MenuItem item = menu.findItem(R.id.spinner);
        Spinner spinner = (Spinner) item.getActionView();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_list_item_array_lachonetes, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
        switch (id){
            case R.id.item_all:
                adapter.notifyItemRangeRemoved(0, produtos.size());
                produtos.clear();
                buscarDados("");
                break;
            case R.id.item_1:
                adapter.notifyItemRangeRemoved(0, produtos.size());
                produtos.clear();
                buscarDados(categorias.get(0));
                break;
            case R.id.item_2:
                adapter.notifyItemRangeRemoved(0, produtos.size());
                produtos.clear();
                buscarDados(categorias.get(1));
                break;
            case R.id.item_3:
                adapter.notifyItemRangeRemoved(0, produtos.size());
                produtos.clear();
                buscarDados(categorias.get(2));
                break;
            case R.id.item_4:
                adapter.notifyItemRangeRemoved(0, produtos.size());
                produtos.clear();
                buscarDados(categorias.get(3));
                break;
            case R.id.item_5:
                adapter.notifyItemRangeRemoved(0, produtos.size());
                produtos.clear();
                buscarDados(categorias.get(4));
                break;
            case R.id.item_6:
                adapter.notifyItemRangeRemoved(0, produtos.size());
                produtos.clear();
                buscarDados(categorias.get(5));
                break;
            case R.id.item_7:
                adapter.notifyItemRangeRemoved(0, produtos.size());
                produtos.clear();
                buscarDados(categorias.get(6));
                break;
            case R.id.item_8:
                adapter.notifyItemRangeRemoved(0, produtos.size());
                produtos.clear();
                buscarDados(categorias.get(7));
                break;
            case R.id.item_9:
                adapter.notifyItemRangeRemoved(0, produtos.size());
                produtos.clear();
                buscarDados(categorias.get(8));
                break;
            case R.id.item_10:
                adapter.notifyItemRangeRemoved(0, produtos.size());
                produtos.clear();
                buscarDados(categorias.get(9));
                break;


            case R.id.funcionarios:
                Intent intent = new Intent(getApplicationContext(), Gerenciar_funcionarios.class);
                intent.putExtra(Constants.cidadeCode, cidadecode);
                intent.putExtra(Constants.cidade, cidade);
                startActivity(intent);
                break;
            case R.id.mudar_conta:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
                break;

        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
