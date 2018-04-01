package rd.com.demo.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import rd.com.demo.R;
import rd.com.demo.adapter.AdapterAmostras;
import rd.com.demo.auxiliares.Constants;
import rd.com.demo.item.firebase.Amostras;

public class ListaProdutosCategorias extends AppCompatActivity {
    private static final String TAG = "Lista Produtos";
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    static List<Amostras> amostras1;
    static List<Amostras> amostras2;
    static List<Amostras> amostras3;
    static List<Amostras> amostras4;
    static List<Amostras> amostras5;
    static List<Amostras> amostras6;
    static List<Amostras> amostras7;
    static List<Amostras> amostras8;
    static List<Amostras> amostras9;
    static List<Amostras> amostras10;

    static List<String> servicos;
    static List<String> categorias;
    static AdapterAmostras adapter_amostras;
    static int tipo;
    String estabelecimento_id, cidadecode;
    static AVLoadingIndicatorView progress;
    static TextView text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_produtos_categorias);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        progress = findViewById(R.id.avi);
        text = findViewById(R.id.text);

        amostras1 = new ArrayList<>();
        amostras2 = new ArrayList<>();
        amostras3 = new ArrayList<>();
        amostras4 = new ArrayList<>();
        amostras5 = new ArrayList<>();
        amostras6 = new ArrayList<>();
        amostras7 = new ArrayList<>();
        amostras8 = new ArrayList<>();
        amostras9 = new ArrayList<>();
        amostras10 = new ArrayList<>();

        tipo = getIntent().getIntExtra("tipo", 0);
        estabelecimento_id = getIntent().getStringExtra("id");
        cidadecode = getIntent().getStringExtra(Constants.cidadecode);
        setTitle(getIntent().getStringExtra("nome"));
        SharedPreferences sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);

        cidadecode = sharedPreferences.getString(Constants.cidadecode, "");

        servicos = new ArrayList<>();
        servicos.add("restaurantes");
        servicos.add("lanchonetes");
        servicos.add("sorveterias");

        categorias = new ArrayList<>();

        if (tipo == 0) {//configurar categoria restaurantes
            categorias.add("Pratos");
            categorias.add("Porções e Entradas");
            categorias.add("Petiscos");
            categorias.add("Carnes");
            categorias.add("Massas");
            categorias.add("Saladas");
            categorias.add("Sobremessas");
            categorias.add("Lanches");
            categorias.add("Bebidas");


            buscar_dados_categoria1();
            buscar_dados_categoria2();
            buscar_dados_categoria3();
            buscar_dados_categoria4();
            buscar_dados_categoria5();
            buscar_dados_categoria6();
            buscar_dados_categoria7();
            buscar_dados_categoria8();
            buscar_dados_categoria9();


        } else if (tipo == 1){//configurar categoria lanchonetes
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

            buscar_dados_categoria1();
            buscar_dados_categoria2();
            buscar_dados_categoria3();
            buscar_dados_categoria4();
            buscar_dados_categoria5();
            buscar_dados_categoria6();
            buscar_dados_categoria7();
            buscar_dados_categoria8();
            buscar_dados_categoria9();
            buscar_dados_categoria10();

        }


        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Carinho.class));
            }
        });

    }
    private void buscar_dados_categoria1(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("estabelecimentos")
                .document(Constants.cidade)
                .collection(cidadecode)
                .document(servicos.get(tipo))
                .collection("amostras")//obtem todos os itens de amostra
                .whereEqualTo("id_estabelecimento", estabelecimento_id)//filtra por estabelecimento
                .whereEqualTo("categoria", categorias.get(0))//retorna resultados apenas da primeira categoria
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                List<DocumentSnapshot> snapshotList = documentSnapshots.getDocuments();
                Log.v(TAG, snapshotList.toString());
                for(int i = 0; i < snapshotList.size(); i++){
                    Amostras p = snapshotList.get(i).toObject(Amostras.class);
                    amostras1.add(p);
                    if (mViewPager.getCurrentItem() == 0){
                        progress.hide();
                        text.setVisibility(View.GONE);
                        try {
                            adapter_amostras.notifyItemInserted(amostras1.size() - 1);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
                mSectionsPagerAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }
    private void buscar_dados_categoria2(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("estabelecimentos")
                .document(Constants.cidade)
                .collection(cidadecode)
                .document(servicos.get(tipo))
                .collection("amostras")//obtem todos os itens de amostra
                .whereEqualTo("id_estabelecimento", estabelecimento_id)//filtra por estabelecimento
                .whereEqualTo("categoria", categorias.get(1))//retorna resultados apenas da primeira categoria
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                List<DocumentSnapshot> snapshotList = documentSnapshots.getDocuments();
                Log.v(TAG, snapshotList.toString());
                for(int i = 0; i < snapshotList.size(); i++){
                    Amostras p = snapshotList.get(i).toObject(Amostras.class);
                    amostras2.add(p);
                    if (mViewPager.getCurrentItem() == 1){
                        progress.hide();
                        text.setVisibility(View.GONE);
                        adapter_amostras.notifyItemInserted(amostras2.size() - 1);
                    }
                }
                mSectionsPagerAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }
    private void buscar_dados_categoria3(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("estabelecimentos")
                .document(Constants.cidade)
                .collection(cidadecode)
                .document(servicos.get(tipo))
                .collection("amostras")//obtem todos os itens de amostra
                .whereEqualTo("id_estabelecimento", estabelecimento_id)//filtra por estabelecimento
                .whereEqualTo("categoria", categorias.get(2))//retorna resultados apenas da primeira categoria
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                List<DocumentSnapshot> snapshotList = documentSnapshots.getDocuments();
                Log.v(TAG, snapshotList.toString());
                for(int i = 0; i < snapshotList.size(); i++){
                    Amostras p = snapshotList.get(i).toObject(Amostras.class);
                    amostras3.add(p);
                    if (mViewPager.getCurrentItem() == 2){
                        progress.hide();
                        text.setVisibility(View.GONE);
                        adapter_amostras.notifyItemInserted(amostras3.size() - 1);
                    }
                }
                mSectionsPagerAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }
    private void buscar_dados_categoria4(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("estabelecimentos")
                .document(Constants.cidade)
                .collection(cidadecode)
                .document(servicos.get(tipo))
                .collection("amostras")//obtem todos os itens de amostra
                .whereEqualTo("id_estabelecimento", estabelecimento_id)//filtra por estabelecimento
                .whereEqualTo("categoria", categorias.get(3))//retorna resultados apenas da primeira categoria
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                List<DocumentSnapshot> snapshotList = documentSnapshots.getDocuments();
                Log.v(TAG, snapshotList.toString());
                for(int i = 0; i < snapshotList.size(); i++){
                    Amostras p = snapshotList.get(i).toObject(Amostras.class);
                    amostras4.add(p);
                    if (mViewPager.getCurrentItem() == 3){
                        progress.hide();
                        text.setVisibility(View.GONE);
                        adapter_amostras.notifyItemInserted(amostras4.size() - 1);
                    }
                }
                mSectionsPagerAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }
    private void buscar_dados_categoria5(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("estabelecimentos")
                .document(Constants.cidade)
                .collection(cidadecode)
                .document(servicos.get(tipo))
                .collection("amostras")//obtem todos os itens de amostra
                .whereEqualTo("id_estabelecimento", estabelecimento_id)//filtra por estabelecimento
                .whereEqualTo("categoria", categorias.get(4))//retorna resultados apenas da primeira categoria
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                List<DocumentSnapshot> snapshotList = documentSnapshots.getDocuments();
                Log.v(TAG, snapshotList.toString());
                for(int i = 0; i < snapshotList.size(); i++){
                    Amostras p = snapshotList.get(i).toObject(Amostras.class);
                    amostras5.add(p);
                    if (mViewPager.getCurrentItem() == 4){
                        progress.hide();
                        text.setVisibility(View.GONE);
                        adapter_amostras.notifyItemInserted(amostras5.size() - 1);
                    }
                }
                mSectionsPagerAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }
    private void buscar_dados_categoria6(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("estabelecimentos")
                .document(Constants.cidade)
                .collection(cidadecode)
                .document(servicos.get(tipo))
                .collection("amostras")//obtem todos os itens de amostra
                .whereEqualTo("id_estabelecimento", estabelecimento_id)//filtra por estabelecimento
                .whereEqualTo("categoria", categorias.get(5))//retorna resultados apenas da primeira categoria
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                List<DocumentSnapshot> snapshotList = documentSnapshots.getDocuments();
                Log.v(TAG, snapshotList.toString());
                for(int i = 0; i < snapshotList.size(); i++){
                    Amostras p = snapshotList.get(i).toObject(Amostras.class);
                    amostras6.add(p);
                    if (mViewPager.getCurrentItem() == 5){
                        progress.hide();
                        text.setVisibility(View.GONE);
                        adapter_amostras.notifyItemInserted(amostras6.size() - 1);
                    }
                }
                mSectionsPagerAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }
    private void buscar_dados_categoria7(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("estabelecimentos")
                .document(Constants.cidade)
                .collection(cidadecode)
                .document(servicos.get(tipo))
                .collection("amostras")//obtem todos os itens de amostra
                .whereEqualTo("id_estabelecimento", estabelecimento_id)//filtra por estabelecimento
                .whereEqualTo("categoria", categorias.get(6))//retorna resultados apenas da primeira categoria
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                List<DocumentSnapshot> snapshotList = documentSnapshots.getDocuments();
                Log.v(TAG, snapshotList.toString());
                for(int i = 0; i < snapshotList.size(); i++){
                    Amostras p = snapshotList.get(i).toObject(Amostras.class);
                    amostras7.add(p);
                    if (mViewPager.getCurrentItem() == 6){
                        progress.hide();
                        text.setVisibility(View.GONE);
                        adapter_amostras.notifyItemInserted(amostras7.size() - 1);
                    }
                }
                mSectionsPagerAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }
    private void buscar_dados_categoria8(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("estabelecimentos")
                .document(Constants.cidade)
                .collection(cidadecode)
                .document(servicos.get(tipo))
                .collection("amostras")//obtem todos os itens de amostra
                .whereEqualTo("id_estabelecimento", estabelecimento_id)//filtra por estabelecimento
                .whereEqualTo("categoria", categorias.get(7))//retorna resultados apenas da primeira categoria
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                List<DocumentSnapshot> snapshotList = documentSnapshots.getDocuments();
                Log.v(TAG, snapshotList.toString());
                for(int i = 0; i < snapshotList.size(); i++){
                    Amostras p = snapshotList.get(i).toObject(Amostras.class);
                    amostras8.add(p);
                    if (mViewPager.getCurrentItem() == 7){
                        progress.hide();
                        text.setVisibility(View.GONE);
                        adapter_amostras.notifyItemInserted(amostras8.size() - 1);
                    }
                }
                mSectionsPagerAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }
    private void buscar_dados_categoria9(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("estabelecimentos")
                .document(Constants.cidade)
                .collection(cidadecode)
                .document(servicos.get(tipo))
                .collection("amostras")//obtem todos os itens de amostra
                .whereEqualTo("id_estabelecimento", estabelecimento_id)//filtra por estabelecimento
                .whereEqualTo("categoria", categorias.get(8))//retorna resultados apenas da primeira categoria
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                List<DocumentSnapshot> snapshotList = documentSnapshots.getDocuments();
                Log.v(TAG, snapshotList.toString());
                for(int i = 0; i < snapshotList.size(); i++){
                    Amostras p = snapshotList.get(i).toObject(Amostras.class);
                    amostras9.add(p);
                    if (mViewPager.getCurrentItem() == 8){
                        progress.hide();
                        text.setVisibility(View.GONE);
                        adapter_amostras.notifyItemInserted(amostras9.size() - 1);
                    }
                }
                mSectionsPagerAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }
    private void buscar_dados_categoria10(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("estabelecimentos")
                .document(Constants.cidade)
                .collection(cidadecode)
                .document(servicos.get(tipo))
                .collection("amostras")//obtem todos os itens de amostra
                .whereEqualTo("id_estabelecimento", estabelecimento_id)//filtra por estabelecimento
                .whereEqualTo("categoria", categorias.get(9))//retorna resultados apenas da primeira categoria
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                List<DocumentSnapshot> snapshotList = documentSnapshots.getDocuments();
                Log.v(TAG, snapshotList.toString());
                for(int i = 0; i < snapshotList.size(); i++){
                    Amostras p = snapshotList.get(i).toObject(Amostras.class);
                    amostras10.add(p);
                    if (mViewPager.getCurrentItem() == 9){
                        progress.hide();
                        text.setVisibility(View.GONE);
                        adapter_amostras.notifyItemInserted(amostras10.size() - 1);
                    }
                }
                mSectionsPagerAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_lista_produtos_categorias, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();



        return super.onOptionsItemSelected(item);
    }

    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        RecyclerView recyclerView;
        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onResume() {
            super.onResume();
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_lista_produtos_categorias, container, false);
            recyclerView = rootView.findViewById(R.id.recyclerView);

            switch (getArguments().getInt(ARG_SECTION_NUMBER)){
                case 1:
                     adapter_amostras = new AdapterAmostras(amostras1);
                     if (!amostras1.isEmpty()){
                         hideProgress();
                     } else {
                         progress.setVisibility(View.VISIBLE);
                         text.setVisibility(View.VISIBLE);
                     }
                    break;
                case 2:
                    adapter_amostras = new AdapterAmostras(amostras2);
                    if (!amostras2.isEmpty()){
                        hideProgress();
                    }else {
                    progress.setVisibility(View.VISIBLE);
                    text.setVisibility(View.VISIBLE);
                }
                    break;
                case 3:
                    adapter_amostras = new AdapterAmostras(amostras3);
                    if (!amostras3.isEmpty()){
                        hideProgress();
                    }else {
                    progress.setVisibility(View.VISIBLE);
                    text.setVisibility(View.VISIBLE);
                }
                    break;
                case 4:
                    adapter_amostras = new AdapterAmostras(amostras4);
                    if (!amostras4.isEmpty()){
                        hideProgress();
                    }else {
                        progress.setVisibility(View.VISIBLE);
                        text.setVisibility(View.VISIBLE);
                    }
                    break;
                case 5:
                    adapter_amostras = new AdapterAmostras(amostras5);
                    if (!amostras5.isEmpty()){
                        hideProgress();
                    }else {
                        progress.setVisibility(View.VISIBLE);
                        text.setVisibility(View.VISIBLE);
                    }
                    break;
                case 6:
                    adapter_amostras = new AdapterAmostras(amostras6);
                    if (!amostras6.isEmpty()){
                        hideProgress();
                    } else {
                        progress.setVisibility(View.VISIBLE);
                        text.setVisibility(View.VISIBLE);
                    }
                    break;
                case 7:
                    adapter_amostras = new AdapterAmostras(amostras7);
                    if (!amostras7.isEmpty()){
                        hideProgress();
                    }else {
                        progress.setVisibility(View.VISIBLE);
                        text.setVisibility(View.VISIBLE);
                    }
                    break;
                case 8:
                    adapter_amostras = new AdapterAmostras(amostras8);
                    if (!amostras8.isEmpty()){
                        hideProgress();
                    }else {
                        progress.setVisibility(View.VISIBLE);
                        text.setVisibility(View.VISIBLE);
                    }
                    break;
                case 9:
                    adapter_amostras = new AdapterAmostras(amostras9);
                    if (!amostras9.isEmpty()){
                        hideProgress();
                    }else {
                        progress.setVisibility(View.VISIBLE);
                        text.setVisibility(View.VISIBLE);
                    }
                    break;
                case 10:
                    adapter_amostras = new AdapterAmostras(amostras10);
                    if (!amostras10.isEmpty()){
                        hideProgress();
                    }else {
                        progress.setVisibility(View.VISIBLE);
                        text.setVisibility(View.VISIBLE);
                    }
                    break;

                    default:
                        adapter_amostras = new AdapterAmostras(amostras1);
            }

            recyclerView.setLayoutManager(new GridLayoutManager(container.getContext(), 2));
            recyclerView.setAdapter(adapter_amostras);

            return rootView;
        }
    }

    private static void hideProgress(){
        progress.hide();
        text.setVisibility(View.GONE);
    }
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position + 1);
        }


        @Override
        public int getCount() {
            return categorias.size();
        }
        @Override
        public CharSequence getPageTitle(int position) {
          return categorias.get(position);
        }
    }
}
