package rd.com.demo.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import rd.com.demo.R;
import rd.com.demo.activity.ConfirmacaoCompra;
import rd.com.demo.activity.DetalhesProduto;
import rd.com.demo.banco.sugarOs.Carinho_itemDB;
import rd.com.demo.item.firebase.Produto;


public class AdapterDetalhesAmostra extends RecyclerView.Adapter {
    private List<Produto> list;
    public AdapterDetalhesAmostra(List<Produto> apps) {
        list = apps;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_produto_, parent, false));
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        final Produto produto = list.get(position);
        Holder holder = (Holder) viewHolder;
        final String preco = "R$" + String.format(Locale.getDefault(), "%.2f", produto.getPreco());
        holder.preco.setText(preco);
        holder.descricao.setText(produto.getDescricao());
        holder.nome.setText(produto.getNome());
        holder.bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Carinho_itemDB carinho_itemDB = new Carinho_itemDB(produto.getNome(), produto.getDescricao(), produto.getTipo_estabelecimento(),
                        produto.getEstabelecimento(), produto.getEstabelecimento_id(),
                        produto.getCodigo(), produto.getUrl(), "1", String.valueOf(produto.getPreco()), "00/00/00",
                        true, true, produto.getCidadecode(), produto.getCidade(), produto.getNomeamostra());
                Intent intent = new Intent(v.getContext(), ConfirmacaoCompra.class);
                intent.setAction("comprar");
                intent.putExtra("type", "buySimple");
                intent.putExtra("pedido", carinho_itemDB);
                v.getContext().startActivity(intent);
            }
        });
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DetalhesProduto.class);
                intent.putExtra("produto", produto);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class Holder extends RecyclerView.ViewHolder {//
        private final TextView nome, descricao, preco;
        private final Button bt;
        private final RelativeLayout layout;

        Holder(View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.nome);
            layout = itemView.findViewById(R.id.relativeLayout);
            descricao = itemView.findViewById(R.id.descricao);
            preco = itemView.findViewById(R.id.preco);
            bt = itemView.findViewById(R.id.button2);



        }
    }

}


