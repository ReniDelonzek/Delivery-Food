package rd.com.demo.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import rd.com.demo.R;
import rd.com.demo.adapter.SnapAdapterListaEstabelecimentos;
import rd.com.demo.auxiliares.Constants;
import rd.com.demo.auxiliares.LibraryClass;
import rd.com.demo.item.Snap_Item;
import rd.com.demo.item.firebase.Amostras;
import rd.com.demo.item.firebase.Estabelecimento;

public class ListaEstabelecimentos extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener  {

    private static final String TAG = "ListaEstabelecimentos";
    private RecyclerView mRecyclerView;
    List<Object> list2, list3;
    Context context;
    SnapAdapterListaEstabelecimentos snapAdapter;
    FirebaseUser user;
    List<String> servicos;
    public static int tipo;
    boolean iniciar_adapter = false;
    public static String tipoEstabelecimento, cidadecode;
    TextView textViewcidade;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        list2 = new ArrayList<>();
        list3 = new ArrayList<>();
        servicos = new ArrayList<>();
        servicos.add("restaurantes");
        servicos.add("lanchonetes");
        servicos.add("sorveterias");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        snapAdapter = new SnapAdapterListaEstabelecimentos();
        textViewcidade = findViewById(R.id.cidade);
        textViewcidade.setVisibility(View.GONE);
        progressBar = findViewById(R.id.progressBar5);
        progressBar.setVisibility(View.VISIBLE);

        try {
            tipo = getIntent().getIntExtra("tipo", 0) - 1;
            tipoEstabelecimento = getIntent().getStringExtra("nome");
            cidadecode = getIntent().getStringExtra(Constants.cidadecode);
            setTitle(tipoEstabelecimento);
        }catch (Exception e){
            e.printStackTrace();
        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(false);
        getEstabelecimentos();
        buscar_dados();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Carinho.class));
            }
        });
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void setupAdapter() {
        iniciar_adapter = true;
        snapAdapter.addSnap(new Snap_Item(0, "Restaurantes", list2, true));
        snapAdapter.addSnap(new Snap_Item(1, "Destaques", list3, true));
        progressBar.setVisibility(View.GONE);
        mRecyclerView.setAdapter(snapAdapter);
    }
    private void getEstabelecimentos(){
        FirebaseDatabase.getInstance().getReference()
                .child("Estabelecimentos")
                .child(servicos.get(tipo))
                .orderByChild("cidadecode")
                .equalTo(cidadecode)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        list2.add(dataSnapshot.getValue(Estabelecimento.class));
                        if (!iniciar_adapter){
                            setupAdapter();
                        }
                        snapAdapter.notifyItemChanged(0);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



    }
    private void buscar_dados(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("estabelecimentos")
                .document(Constants.cidade)
                .collection(cidadecode)
                .document(servicos.get(tipo))
                .collection("amostras")//obtem todos os itens de amostra
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                if (!documentSnapshots.isEmpty()) {
                    List<DocumentSnapshot> snapshotList = documentSnapshots.getDocuments();
                    for (int i = 0; i < snapshotList.size(); i++) {
                        Amostras p = snapshotList.get(i).toObject(Amostras.class);
                        list3.add(p);
                        if (!iniciar_adapter) {//caso o adapter ainda n esteja iniciado
                            setupAdapter();
                        }
                        snapAdapter.notifyItemChanged(1);

                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(mRecyclerView, "Nenhum produto disponível nesse estabelecimento", Snackbar.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.suporte){
            startActivity(new Intent(getApplicationContext(), Mensagem.class));
        }
        return super.onOptionsItemSelected(item);
    }
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.sair:
                sair();
                break;
            case R.id.carinhoitem:
                startActivity(new Intent(getApplicationContext(), Carinho.class));
                break;
            case R.id.favoritos:
                startActivity(new Intent(getApplicationContext(), MeusFavoritos.class));
                break;
            case R.id.reclamacoes:
                startActivity(new Intent(getApplicationContext(), Mensagem.class));
                break;
            case R.id.meuspedidos:
                startActivity(new Intent(getApplicationContext(), MeusPedidos.class));
                break;
            case R.id.avaliar:
                String url = "https://play.google.com/store/apps/details?id=" + getPackageName();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;
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
        CircleImageView imageView = header.findViewById(R.id.imageView);
        RelativeLayout linearLayout = header.findViewById(R.id.header_layout);

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user == null){
                    startActivity(new Intent(context, ComecarCadastro.class));
                } else {
                    //startActivity(new Intent(context, Perfil.class));
                }
            }
        });
        if (user != null) {
            SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
            nomeUsuario.setText(sharedPreferences.getString("nome", ""));

            File imgFile = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "Demo/Imagens/.profile.png");
            if(imgFile.exists()){
                imageView.setImageDrawable(Drawable.createFromPath(imgFile.getAbsolutePath()));
            } else {
                imageView.setImageResource(R.drawable.ic_account_circle_white_48dp);
            }
        } else {
            imageView.setImageResource(R.drawable.ic_account_circle_white_48dp);
            nomeUsuario.setText("Faça seu Login!");
            nomeUsuario.setBackgroundColor(ContextCompat.getColor(
                    context, R.color.transparent));
            //imageView.setVisibility(View.GONE);
        }
    }
    private void sair(){
        try {
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            LibraryClass.saveSP(context, Constants.nomeUsuarioLocal, null);
            definirHeader();
            new File(Environment.getExternalStorageDirectory()
                    + File.separator + "Demo/Imagens/.profile.png").delete();
            new File(Environment.getExternalStorageDirectory()
                    + File.separator + "Demo/Imagens/.photocover.png").delete();
        }catch (Exception e){
            e.printStackTrace();
        }
        user = null;
        definirHeader();
    }
    @Override
    public void onResume(){
        super.onResume();
        definirHeader();
    }
}

