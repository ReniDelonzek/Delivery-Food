package rd.com.vendedor.adm.adapter;

/**
 * Created by Reni on 04/02/2018.
 */

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import rd.com.vendedor.R;
import rd.com.vendedor.adm.item.Amostras;


public class AdapterSelecionarAmostra extends RecyclerView.Adapter {

    public interface OnItemClickListener {
        void onItemClick(Amostras item);
    }

    private int type;
    private final OnItemClickListener listener;

    private List<Amostras> list;
    public AdapterSelecionarAmostra(List<Amostras> objects, OnItemClickListener onItemClickListener) {
        list = objects;
        this.type = 0;
        listener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        viewHolder = new Holder_Produtos(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_amostra, parent, false));
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder instanceof Holder_Produtos){//adaptador para as promoções do topo
            final Holder_Produtos holderProdutos = (Holder_Produtos) viewHolder;
            final Amostras amostras = list.get(position);
            holderProdutos.titulo.setText(amostras.getTitulo());
            String s = String.valueOf(amostras.getQuantidade()) + " produto(s)";
            holderProdutos.descricao.setText(s);
            Picasso.with(holderProdutos.imageView.getContext()).
                    load(amostras.getUrl()).into(holderProdutos.imageView);

            holderProdutos.add.hide();
            holderProdutos.edit.hide();
            holderProdutos.cardView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(amostras);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class Holder_Produtos extends RecyclerView.ViewHolder {//holder anuncios topo
        private final FloatingActionButton edit, add;
        private final TextView titulo, descricao;
        private final ImageView imageView;
        private final RelativeLayout cardView;
        Holder_Produtos(View itemView) {
            super(itemView);
            edit = itemView.findViewById(R.id.edit);
            cardView = itemView.findViewById(R.id.relativeLayout);
            add = itemView.findViewById(R.id.remove);
            imageView = itemView.findViewById(R.id.imageView);
            titulo = itemView.findViewById(R.id.nameTextView);
            descricao = itemView.findViewById(R.id.text_quantidade);



        }
    }
}


