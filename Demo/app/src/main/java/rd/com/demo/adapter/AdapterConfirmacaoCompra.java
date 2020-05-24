package rd.com.demo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

import rd.com.demo.R;
import rd.com.demo.activity.ConfirmacaoCompra;
import rd.com.demo.banco.sugarOs.Carinho_itemDB;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Reni on 24/02/2018.
 */

public class AdapterConfirmacaoCompra extends RecyclerView.Adapter {
    private List<Carinho_itemDB> list;
    private TextView precoTotal;
    private Carinho_itemDB carinho_itemDB;

    public AdapterConfirmacaoCompra(List<Carinho_itemDB> list, TextView textView,
                                    Carinho_itemDB carinho_itemDB, double precot){
        this.list = list;
        precoTotal = textView;
        this.carinho_itemDB = carinho_itemDB;
       // this.precofrete = precot;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder_Produtos(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_compra_produto, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final Holder_Produtos viewHolder = (Holder_Produtos) holder;
        viewHolder.titulo.setText(list.get(position).getTitulo());
        viewHolder.descricao.setText(list.get(position).getDescricao());
        viewHolder.preco.setText(String.format("R$%s", String.format(Locale.getDefault(), "%.2f",
                Double.parseDouble(list.get(holder.getAdapterPosition()).getPreco()))));
        viewHolder.quantidade.setText(list.get(holder.getAdapterPosition()).getQuantidade());
        Picasso.with(getApplicationContext()).load(list.get(position).getUrl()).into(viewHolder.imageView);

        viewHolder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Carinho_itemDB carinhoitem = list.get(holder.getAdapterPosition());
                carinhoitem.setQuantidade(String.valueOf(Integer.parseInt(carinhoitem.getQuantidade().replaceAll("[^0-9]", "")) + 1)
                        .replaceAll("[^0-9]", ""));
                list.set(holder.getAdapterPosition(), carinhoitem);//atualiza o numero do itens na lista
                viewHolder.quantidade.setText(String.valueOf(carinhoitem.getQuantidade()));
                Double precTotal =  Double.parseDouble(carinhoitem.getPreco())
                        * Integer.parseInt(carinhoitem.getQuantidade());
                viewHolder.preco.setText(String.format("R$%s", String.format(Locale.getDefault(), "%.2f",
                        precTotal)));
                atualizarPrecoTotal();
            }
        });
        viewHolder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Carinho_itemDB carinhoitem = list.get(holder.getAdapterPosition());
                int q = Integer.parseInt(carinhoitem.getQuantidade().replaceAll("[^0-9]", ""));
                if (q > 1){
                    carinhoitem.setQuantidade(String.valueOf(q - 1).replaceAll("[^0-9]", ""));
                    list.set(holder.getAdapterPosition(), carinhoitem);//atualiza o numero do itens na lista
                    viewHolder.quantidade.setText(String.valueOf(carinhoitem.getQuantidade()));
                    Double precTotal =  Double.parseDouble(carinhoitem.getPreco())
                            * Integer.parseInt(carinhoitem.getQuantidade());
                    viewHolder.preco.setText(String.format("R$%s", String.format(Locale.getDefault(), "%.2f",
                            precTotal)));
                    atualizarPrecoTotal();
                }
            }
        });
    }
    private void atualizarCarinho(){
        StringBuilder quant = new StringBuilder();
        for(int i = 0; i < list.size(); i++){
            quant.append(list.get(i).getQuantidade()).append("\n");
        }
        carinho_itemDB.setQuantidade(quant.toString());
    }
    private void atualizarPrecoTotal(){
        Double preco = 0.0;
        for(int i = 0; i < list.size(); i++){
            preco += Double.parseDouble(list.get(i).getPreco()) * Integer.parseInt(list.get(i).getQuantidade());
        }
        preco = preco + ConfirmacaoCompra.frete;
        precoTotal.setText(String.format("R$%s", String.format(Locale.getDefault(), "%.2f",
                preco)));
        atualizarCarinho();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    private class Holder_Produtos extends RecyclerView.ViewHolder implements View.OnClickListener {//
        private final ImageView imageView;
        private final TextView titulo, preco, descricao, quantidade;
        private final RelativeLayout relativeLayout;
        private final ImageButton add, remove;

        Holder_Produtos(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            quantidade = itemView.findViewById(R.id.quantidade);
            add = itemView.findViewById(R.id.add);
            remove = itemView.findViewById(R.id.remove);
            imageView = itemView.findViewById(R.id.fotoproduto);
            titulo = itemView.findViewById(R.id.titulo);
            descricao = itemView.findViewById(R.id.descricao);
            preco = itemView.findViewById(R.id.preco);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);

        }

        @Override
        public void onClick(View v) {

        }
    }
}
