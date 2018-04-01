package rd.com.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import rd.com.demo.R;
import rd.com.demo.adapter.AdapterCarinho;
import rd.com.demo.banco.sugarOs.Carinho_itemDB;

public class Carinho extends AppCompatActivity {
    private static final String TAG = "Carinho";
    RecyclerView recyclerView;
    AdapterCarinho adapter;
    RecyclerView.LayoutManager layoutManager;
    List<Carinho_itemDB> list;
    List<String> listestabelecimentos;
    ImageView imageView;
    boolean single = true;
    boolean empty = true;//variavel para saber se o carrinho esta vazio ou nao
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carinho);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        fab = findViewById(R.id.fab);
        imageView = findViewById(R.id.image);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (empty){//carinho vazio
                    finish();
                } else {
                    Carinho_itemDB comprar = checar_marcados();
                    if (comprar != null) {
                        if (single) {//essa variavel tem a info se o cliente esta comprando apenas de um unico estabelecimento
                            Intent intent = new Intent(getApplicationContext(), ConfirmacaoCompra.class);
                            intent.putExtra("pedido", comprar);
                            intent.setAction("comprar");
                            intent.putExtra("type", "buyAll");
                            startActivity(intent);
                        } else {//caso não, alerta-o sobre isso
                            Snackbar.make(view, "Sorry, Você pode comprar de apenas de um estabelecimento por vez", Snackbar.LENGTH_SHORT).show();
                        }
                    } else {
                        Snackbar.make(view, "Por favor marque os itens que deseja comprar", Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        });
        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        list = new ArrayList<>();
        listestabelecimentos = new ArrayList<>();
        adapter = new AdapterCarinho(list);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        carregar_itens();
    }

    private Carinho_itemDB checar_marcados() {
        Carinho_itemDB carinhoitemList = null;
        for(int i = 0; i < list.size(); i++){
            if (list.get(i).isCheck2()){
                if (carinhoitemList == null){
                    carinhoitemList = new Carinho_itemDB();

                    carinhoitemList.setTitulo(list.get(i).getTitulo());
                    carinhoitemList.setDescricao(list.get(i).getDescricao());
                    carinhoitemList.setUrl(list.get(i).getUrl());
                    carinhoitemList.setid(list.get(i).getid());
                    carinhoitemList.setEstabelecimento(list.get(i).getEstabelecimento());
                    carinhoitemList.setCodigo(list.get(i).getCodigo());
                    carinhoitemList.setPreco(list.get(i).getPreco());
                    carinhoitemList.setQuantidade(list.get(i).getQuantidade());
                    carinhoitemList.setTipoestabelecimento(list.get(i).getTipoestabelecimento());
                    carinhoitemList.setEstabelecimentoid(list.get(i).getEstabelecimentoid());
                    carinhoitemList.setCidade(list.get(i).getCidade());
                    carinhoitemList.setCidadecode(list.get(i).getCidadecode());
                    carinhoitemList.setNomeamostra(list.get(i).getNomeamostra());

                } else {
                    if (carinhoitemList.getid().equals(list.get(i).getid())) {
                        carinhoitemList.setTitulo(carinhoitemList.getTitulo() + "¨" + list.get(i).getTitulo());
                        carinhoitemList.setDescricao(carinhoitemList.getDescricao() + "¨" + list.get(i).getDescricao());
                        carinhoitemList.setUrl(carinhoitemList.getUrl() + "¨" + list.get(i).getUrl());
                        carinhoitemList.setid(carinhoitemList.getid() + "¨" + list.get(i).getid());
                        carinhoitemList.setEstabelecimento(carinhoitemList.getEstabelecimento() + "¨" + list.get(i).getEstabelecimento());
                        carinhoitemList.setCodigo(carinhoitemList.getCodigo() + "¨" + list.get(i).getCodigo());
                        carinhoitemList.setPreco(carinhoitemList.getPreco() + "¨" + list.get(i).getPreco());
                        carinhoitemList.setQuantidade(carinhoitemList.getQuantidade() + "¨" + list.get(i).getQuantidade());
                        carinhoitemList.setQuantidade(carinhoitemList.getNomeamostra() + "¨" + list.get(i).getNomeamostra());

                        carinhoitemList.setTipoestabelecimento(list.get(i).getTipoestabelecimento());
                        carinhoitemList.setEstabelecimentoid(list.get(i).getEstabelecimentoid());
                        carinhoitemList.setCidade(list.get(i).getCidade());
                        carinhoitemList.setCidadecode(list.get(i).getCidadecode());
                    } else {
                        single = false;
                        i = list.size();//finaliza o loop
                    }
                }
            }
        }
        return carinhoitemList;
    }

    private void carregar_itens() {
        List<Carinho_itemDB> p = new ArrayList<>();
                p.addAll(Carinho_itemDB.find(Carinho_itemDB.class, "", new String[]{}
                , "", "data DESC", ""));//retorna os pedidos ordenados pelo estabelecimento
        if (p.size() > 0) {
            empty = false;
            separar_estabelecimentos(p);
            ler_pedidos_estabelecimento();
            imageView.setVisibility(View.GONE);
        } else {
            //Carinho Vazio
            fab.setImageResource(R.drawable.ic_add_white_36dp);
        }
    }

    private void ler_pedidos_estabelecimento() {
        for(int i = 0; i < listestabelecimentos.size(); i++){
            list.addAll(Carinho_itemDB.find(Carinho_itemDB.class, "estabelecimentoid = ?", listestabelecimentos.get(i) ));
            adapter.notifyItemInserted(list.size() - 1);
            //le de cada estabelecimento separado para deixar os itens organizados de acordo com o estabelecimento
        }

    }

    private void separar_estabelecimentos(List<Carinho_itemDB> todosOsItens) {
        listestabelecimentos.add(todosOsItens.get(0).getid());//pega o id do estabelecimento do primeiro item
        for(int i = 1; i < todosOsItens.size(); i++){
            if (!listestabelecimentos.contains(todosOsItens.get(i).getid())){
                listestabelecimentos.add(todosOsItens.get(i).getid());
                Log.v(TAG, listestabelecimentos.get(listestabelecimentos.size()- 1));
            }
        }

    }

}
