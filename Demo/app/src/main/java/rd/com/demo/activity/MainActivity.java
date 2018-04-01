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
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import rd.com.demo.adapter.SnapAdapterMain;
import rd.com.demo.auxiliares.Constants;
import rd.com.demo.auxiliares.LibraryClass;
import rd.com.demo.banco.sugarOs.AmostrasFavoritasDB;
import rd.com.demo.item.firebase.Amostras;
import rd.com.demo.item.firebase.Estabelecimento;
import rd.com.demo.item.firebase.Item_Imagem;
import rd.com.demo.item.firebase.Lista_Opcoes;
import rd.com.demo.item.Snap_Item;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "Main Activity";
    private static final int REQUEST_LOCALIZATION = 0;
    private RecyclerView mRecyclerView;
    List<Object> list, list2, list3, list4, list5;
    Context context;
    public static String cidadecode, cidade;
    SnapAdapterMain snapAdapter;
    FirebaseUser user;
    public static List<String> amostrasFavoritasDB;
    TextView textViewcidade;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        list = new ArrayList<>();
        list2 = new ArrayList<>();
        list3 = new ArrayList<>();
        list4 = new ArrayList<>();
        list5 = new ArrayList<>();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        snapAdapter = new SnapAdapterMain();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open , R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        textViewcidade = findViewById(R.id.cidade);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(false);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        amostrasFavoritasDB = new ArrayList<>();
        carregarAmostrasFavoritas();

        addServicos();
        setupAdapter();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Carinho.class));
            }
        });
        //adionar_estabelecimentos();

        SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        cidadecode = sharedPreferences.getString(Constants.cidadecode, "");
        cidade = sharedPreferences.getString(Constants.cidade, "");

        if (!cidadecode.isEmpty()) {
            textViewcidade.setText(cidade);
            getRestaurantes();
            getLanchonetes();
            getSorveterias();
        } else {
            startActivityForResult(new Intent(getApplicationContext(), Localizacao.class), REQUEST_LOCALIZATION);
            finish();
        }
        textViewcidade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
                sharedPreferences.edit().remove(Constants.cidade).apply();
                sharedPreferences.edit().remove(Constants.cidadecode).apply();
                startActivity(new Intent(getApplicationContext(), Localizacao.class));
                finish();
            }
        });
    }

    private void carregarAmostrasFavoritas() {
        new Thread(new Runnable() {
            public void run(){
                List<AmostrasFavoritasDB> amostrasFavoritasDBS = AmostrasFavoritasDB.listAll(AmostrasFavoritasDB.class);
                for(int i = 0; i < amostrasFavoritasDBS.size(); i++){
                    amostrasFavoritasDB.add(amostrasFavoritasDBS.get(i).getCaminho());
                }
            }
        }).start();
    }

    private void adionar_estabelecimentos() {
        Estabelecimento estabelecimento = new Estabelecimento("PediuPagou", "restaurante1", "https://u.tfstatic.com/restaurant_photos/585/69585/169/612/das-restaurante-salle-du-restaurant-6c14c.jpg",
                "Das 10 da manhã até as 11 da noite", "Rua dos Expedicionários", "Slogan", "restaurante1", "-26.018048, -51.351256");
        estabelecimento.setCidadecode("84623000");
        String id = FirebaseDatabase.getInstance().getReference().child("Estabelecimentos")
                .child("restaurantes").push().getKey();
        FirebaseDatabase.getInstance().getReference()
                .child("Estabelecimentos")
                .child("restaurantes")
                .child(id)
                .setValue(estabelecimento);

        FirebaseDatabase.getInstance().getReference()
                .child("Estabelecimentos")
                .child("restaurantes")
                .child(id)
                .child("mesas")
                .push()
                .setValue("Mesa 01");
        FirebaseDatabase.getInstance().getReference()
                .child("Estabelecimentos")
                .child("restaurantes")
                .child(id)
                .child("mesas")
                .push()
                .setValue("Mesa 02");
        FirebaseDatabase.getInstance().getReference()
                .child("Estabelecimentos")
                .child("restaurantes")
                .child(id)
                .child("mesas")
                .push()
                .setValue("Mesa 03");
        FirebaseDatabase.getInstance().getReference()
                .child("Estabelecimentos")
                .child("restaurantes")
                .child(id)
                .child("mesas")
                .push()
                .setValue("Mesa 04");
        FirebaseDatabase.getInstance().getReference()
                .child("Estabelecimentos")
                .child("restaurantes")
                .child(id)
                .child("mesas")
                .push()
                .setValue("Mesa 05");
        FirebaseDatabase.getInstance().getReference()
                .child("Estabelecimentos")
                .child("restaurantes")
                .child(id)
                .child("mesas")
                .push()
                .setValue("Mesa 06");

    }
    private void setupAdapter() {
        //list.add("");
        //snapAdapter.addSnap(new Snap_Item(4, "", list, false));//pesquisa
        snapAdapter.addSnap(new Snap_Item(1, "Categorias", list2, true));
        snapAdapter.addSnap(new Snap_Item(3, "Restaurantes", list3, true));
        //snapAdapter.addSnap(new Snap_Item(3, "Lanchonetes", list4, true));
        //snapAdapter.addSnap(new Snap_Item(3, "Sorveterias", list5, true));

        mRecyclerView.setAdapter(snapAdapter);
    }

    private void addItens() {
        list.add(new Item_Imagem("http://www.maumauburger.com.br/media/cache/9f/be/9fbe3b1508a28e8f07fc6c8abb14fb1f.jpg"));
        list.add(new Item_Imagem("http://www.campinas.com.br/sites/default/files/styles/large/public/abbraccio-promocoes.jpg?itok=tyk8Jygd"));
        list.add(new Item_Imagem("https://www.testapramim.com.br/wp-content/uploads/2017/03/novo-original-mex-mc-donalds-meramente-ilustrativo.png"));

    }
    private void addServicos(){
        list2.add(new Lista_Opcoes("Restaurantes", R.drawable.restaurant_icon_rosa, "", 1));
        list2.add(new Lista_Opcoes("Lanchonetes", R.drawable.lanches_icon, "", 2));
        list2.add(new Lista_Opcoes("Sorveterias", R.drawable.sorveteria_green, "", 3));
    }
    private void buscar_dados(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("estabelecimentos")

                .document("restaurantes")
                .collection("amostras")//obter todos os itens
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                List<DocumentSnapshot> snapshotList = documentSnapshots.getDocuments();
                Log.v(TAG, snapshotList.toString());
                for(int i = 0; i < snapshotList.size(); i++){
                    Amostras p = snapshotList.get(i).toObject(Amostras.class);
                    list3.add(p);
                    snapAdapter.notifyItemChanged(2);

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }
    private void getRestaurantes(){
        FirebaseDatabase.getInstance().getReference()
                .child("Estabelecimentos")
                .child("restaurantes")
                .orderByChild("cidadecode")
                .equalTo(cidadecode)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot.exists()) {
                            list3.add(dataSnapshot.getValue(Estabelecimento.class));
                            snapAdapter.notifyItemChanged(1);
                        }
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
    private void getLanchonetes(){
        FirebaseDatabase.getInstance().getReference()
                .child("Estabelecimentos")
                .child("lanchonetes")
                .orderByChild("cidadecode")
                .equalTo(cidadecode)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot.exists()) {
                            list4.add(dataSnapshot.getValue(Estabelecimento.class));
                            snapAdapter.notifyItemChanged(2);
                        } else {
                            Snackbar.make(mRecyclerView, "Nenhuma Lanchonete cadastrada", Snackbar.LENGTH_SHORT).show();
                        }
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
                        Log.e("MainActivity", "Erro ao recuperar lanchonetes" + databaseError.getMessage());
                    }
                });



    }
    private void getSorveterias(){
        FirebaseDatabase.getInstance().getReference()
                .child("Estabelecimentos")
                .child("sorveterias")
                .orderByChild("cidadecode")
                .equalTo(cidadecode)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        list5.add(dataSnapshot.getValue(Estabelecimento.class));
                        snapAdapter.notifyItemChanged(3);
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
        if (id == R.id.layoutType) {
            startActivity(new Intent(getApplicationContext(), VerificarTelefone.class));
            return true;
        } else if (id == R.id.action_search){
            Snackbar.make(mRecyclerView, "Aqui o usuario fará a pesquisa dos produtos", Snackbar.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void definirHeader() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TextView nomeUsuario = (TextView) header.findViewById(R.id.nome);
        CircleImageView imageView = (CircleImageView) header.findViewById(R.id.imageView);
        RelativeLayout linearLayout = (RelativeLayout) header.findViewById(R.id.header_layout);

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
            String nome = sharedPreferences.getString("nome", "");
            for(int i = 0; i < nome.length() - 1; i++){
                if (nome.substring(i, i + 1).equals(" ")){
                    nome = nome.substring(0, i);
                    i = nome.length();//finaliza o loop
                }
            }
            nomeUsuario.setText(nome);
            setTitle(String.format("Olá %s ☺", nome));

            File imgFile = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "Demo/Imagens/.profile.png");
            if(imgFile.exists()){
                imageView.setImageDrawable(Drawable.createFromPath(imgFile.getAbsolutePath()));
            } else {
                imageView.setImageResource(R.drawable.ic_account_circle_white_48dp);
            }
            /*
            imgFile = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "Demo/Imagens/.photocover.png");
            if(imgFile.exists()){
                fotoCapa.setImageDrawable(Drawable.createFromPath(imgFile.getAbsolutePath()));
                nomeUsuario.setBackgroundColor(ContextCompat.getColor(
                        context, R.color.quase_transp));;
            }*/ //fundo da imagem
        } else {
            imageView.setImageResource(R.drawable.ic_account_circle_white_48dp);
            nomeUsuario.setText("Faça seu Login!");
            nomeUsuario.setBackgroundColor(ContextCompat.getColor(
                    context, R.color.transparent));;
            //imageView.setVisibility(View.GONE);
        }
    }
    private void sair(){
        Snackbar.make(mRecyclerView, "Saindo...", Snackbar.LENGTH_SHORT).show();
        try {
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            LibraryClass.saveSP(context, Constants.nomeUsuarioLocal, null);
            definirHeader();
            new File(Environment.getExternalStorageDirectory()
                    + File.separator + "Get/Imagens/.profile.png").delete();
            new File(Environment.getExternalStorageDirectory()
                    + File.separator + "Get/Imagens/.photocover.png").delete();
        }catch (Exception e){
            e.printStackTrace();
        }
        user = null;
        definirHeader();
    }
    @Override
    public void onResume(){
        super.onResume();
        user = FirebaseAuth.getInstance().getCurrentUser();
        definirHeader();
    }
}
