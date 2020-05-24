package rd.com.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

import rd.com.demo.R;
import rd.com.demo.banco.sugarOs.Carinho_itemDB;
import rd.com.demo.item.firebase.Produto;

public class DetalhesProduto extends AppCompatActivity {
    TextView titulo, descricao, preco, quantidade;
    ImageView imageView;
    FloatingActionButton carinhoadd;
    //Button add_carrinho, comprar;
    Produto produto;
    ImageButton add, remove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_produto);
        titulo = findViewById(R.id.nome);
        descricao = findViewById(R.id.descricao);
        preco = findViewById(R.id.preco);
        imageView = findViewById(R.id.imagem_produto);
        //add_carrinho = findViewById(R.id.add_carinho);
        //comprar = findViewById(R.id.comprar);
        carinhoadd = findViewById(R.id.floatingActionButton2);
        add = findViewById(R.id.add);
        remove = findViewById(R.id.remove);
        quantidade = findViewById(R.id.quantidade);

        if (getIntent().getSerializableExtra("produto") != null){
            produto = (Produto) getIntent().getSerializableExtra("produto");
            //Picasso.with(getApplicationContext()).
              //      load(produto.getUrl()).into(imageView);
            imageView.setImageResource(produto.getCaminho());
            titulo.setText(produto.getNome());
            descricao.setText(produto.getDescricao());
            String p = "R$" + String.format(Locale.getDefault(), "%.2f", produto.getPreco());
            preco.setText(p);
        }

        carinhoadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adc_carinho(produto, v);
            }
        });
        /*
        comprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long tsLong = System.currentTimeMillis()/1000;
                String ts = tsLong.toString();
                Carinho_itemDB carinho_itemDB = new Carinho_itemDB(produto.getNome(), produto.getDescricao(), produto.getTipo_estabelecimento(), produto.getEstabelecimento(),
                        produto.getEstabelecimento_id(), produto.getCodigo(), produto.getUrl(), quantidade.getText().toString(), String.valueOf(produto.getPreco()), ts, false, true, produto.getCidadecode(),
                        produto.getCidade(), produto.getNomeamostra());
                Intent intent = new Intent(getApplicationContext(), ConfirmacaoCompra.class);
                intent.setAction("comprar");
                intent.putExtra("pedido", carinho_itemDB);
                intent.putExtra("type", "");
                startActivity(intent);
            }
        });
        */
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Imagem.class);
                intent.putExtra("url", produto.getUrl());
                startActivity(intent);
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantidade.setText(String.valueOf(Integer.parseInt(quantidade.getText().toString()) + 1));

            }
        });
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int q = Integer.parseInt(quantidade.getText().toString());
                if (q > 1){
                    quantidade.setText(String.valueOf(--q));
                }
            }
        });
    }
    private void adc_carinho(Produto produto, View v) {
        List<Carinho_itemDB> itensCarinho = Carinho_itemDB.find(Carinho_itemDB.class, "codigo = ? " +
                        "and estabelecimentoid = ?",
                produto.getCodigo(), produto.getEstabelecimento_id());//le todos os itens ja presentes no carinho

        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();
        if (itensCarinho.isEmpty()){//nao existe nenhum pedido assim no carinho, adiciona um novo
            Carinho_itemDB pedido = new Carinho_itemDB(produto.getNome(), produto.getDescricao(), produto.getTipo_estabelecimento(), produto.getEstabelecimento(),
                    produto.getEstabelecimento_id(), produto.getCodigo(), produto.getUrl(), quantidade.getText().toString(), String.valueOf(produto.getPreco()), ts, false, true, produto.getCidadecode(),
                    produto.getCidade(), produto.getNomeamostra());
            pedido.save();
            Snackbar.make(v, "Produto adicionado ao Carrinho :)", Snackbar.LENGTH_SHORT).show();
        } else if (itensCarinho.size() == 1){//o produto ja consta no carrinho, apenas adiciona mais um item
            Carinho_itemDB carinhoitem1 = itensCarinho.get(0);
            carinhoitem1.setQuantidade(String.valueOf(Integer.parseInt(carinhoitem1.getQuantidade()) + 1));
            carinhoitem1.save();
            Snackbar.make(v, String.valueOf(carinhoitem1.getQuantidade()) +
                    " itens no carinho", Snackbar.LENGTH_SHORT).show();
        }

    }
}
